package com.example.shitterlist;

import com.example.shitterlist.config.ShitterListConfig;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PartyDetectionHandler {
    private static final Pattern PARTY_JOIN_PATTERN = Pattern.compile(
        "§6§lParty Finder §r§8§l» §r§a(?<username>[\\w]+) §r§7joined the party! §r§8\\(§r§7Party Finder§r§8\\)",
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern PARTY_JOIN_PATTERN2 = Pattern.compile(
        "§6§lParty Finder §r§8§l» §r§a(?<username>[\\w]+) §r§7joined the party group!",
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern PARTY_JOIN_PATTERN3 = Pattern.compile(
        ".*Party Finder > (?<username>[\\w]+) joined the dungeon group!.*",
        Pattern.CASE_INSENSITIVE
    );
    
    private final ShitterListConfig config;
    private final MinecraftClient client;
    
    public PartyDetectionHandler(ShitterListConfig config) {
        this.config = config;
        this.client = MinecraftClient.getInstance();
    }
    
    public void register() {
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (overlay) return;
            
            String messageText = message.getString();
            checkForPartyJoin(messageText);
        });
        
        ClientReceiveMessageEvents.CHAT.register((message, signedMessage, sender, parameters, receptionProfile) -> {
            String messageText = message.getString();
            checkForPartyJoin(messageText);
        });
    }
    
    private void checkForPartyJoin(String message) {
        // Debug: Print all incoming messages
        System.out.println("[ShitterList Debug] Received message: " + message);
        
        Matcher matcher = PARTY_JOIN_PATTERN.matcher(message);
        if (matcher.matches()) {
            System.out.println("[ShitterList Debug] Matched PATTERN1");
            String username = matcher.group("username");
            handlePlayerJoin(username);
            return;
        }
        
        matcher = PARTY_JOIN_PATTERN2.matcher(message);
        if (matcher.matches()) {
            System.out.println("[ShitterList Debug] Matched PATTERN2");
            String username = matcher.group("username");
            handlePlayerJoin(username);
            return;
        }
        
        matcher = PARTY_JOIN_PATTERN3.matcher(message);
        if (matcher.matches()) {
            System.out.println("[ShitterList Debug] Matched PATTERN3");
            String username = matcher.group("username");
            handlePlayerJoin(username);
            return;
        }
        
        // Check if message contains "Party Finder" for debugging
        if (message.toLowerCase().contains("party finder")) {
            System.out.println("[ShitterList Debug] Message contains 'Party Finder' but didn't match any pattern");
        }
    }
    
    private void handlePlayerJoin(String username) {
        System.out.println("[ShitterList Debug] Handling player join for: " + username);
        System.out.println("[ShitterList Debug] Is on shitterlist: " + config.isOnShitterList(username));
        
        if (config.isOnShitterList(username)) {
            System.out.println("[ShitterList Debug] Scheduling kick for " + username + " in 0.75s");
            
            // Schedule the kick with 0.75 second delay
            client.execute(() -> {
                try {
                    Thread.sleep(750); // 0.75 second delay
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                
                performKick(username);
            });
        } else {
            System.out.println("[ShitterList Debug] " + username + " is not on shitterlist, no action taken");
        }
    }
    
    private void performKick(String username) {
        System.out.println("[ShitterList Debug] Executing delayed kick for: " + username);
        ClientPlayerEntity player = client.player;
        if (player != null) {
            String kickCommand = "p kick " + username;
            
            System.out.println("[ShitterList Debug] Sending kick command: " + kickCommand);
            player.networkHandler.sendChatCommand(kickCommand);
            
            // Only send party chat message if enabled
            if (config.shouldSendPartyChatMessage()) {
                String partyChatCommand = "pc " + username + " is shitterlisted";
                System.out.println("[ShitterList Debug] Sending party chat command: " + partyChatCommand);
                player.networkHandler.sendChatCommand(partyChatCommand);
            } else {
                System.out.println("[ShitterList Debug] Party chat messages disabled, skipping message");
            }
            
            Text feedback = Text.literal("§c[ShitterList] §fAutomatically kicked " + username + " from party");
            player.sendMessage(feedback, false);
            System.out.println("[ShitterList Debug] Sent kick command and feedback");
        } else {
            System.out.println("[ShitterList Debug] Player is null, cannot send command");
        }
    }
}
