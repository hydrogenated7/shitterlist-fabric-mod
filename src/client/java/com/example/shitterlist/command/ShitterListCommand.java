package com.example.shitterlist.command;

import com.example.shitterlist.ShitterListMod;
import com.example.shitterlist.config.ShitterListConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

import java.util.Set;

public class ShitterListCommand {
    
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("shitterlist")
            .then(ClientCommandManager.literal("add")
                .then(ClientCommandManager.argument("username", StringArgumentType.word())
                    .executes(ShitterListCommand::addPlayer)))
            .then(ClientCommandManager.literal("remove")
                .then(ClientCommandManager.argument("username", StringArgumentType.word())
                    .executes(ShitterListCommand::removePlayer)))
            .then(ClientCommandManager.literal("list")
                .executes(ShitterListCommand::listPlayers))
            .then(ClientCommandManager.literal("clear")
                .executes(ShitterListCommand::clearList))
            .then(ClientCommandManager.literal("togglemsg")
                .executes(ShitterListCommand::togglePartyChatMessage))
            .executes(ShitterListCommand::showHelp));
    }
    
    private static int addPlayer(CommandContext<FabricClientCommandSource> context) {
        String username = StringArgumentType.getString(context, "username");
        ShitterListConfig config = ShitterListMod.getConfig();
        ClientPlayerEntity player = context.getSource().getPlayer();
        
        if (config.addToShitterList(username)) {
            if (player != null) {
                Text message = Text.literal("§a[ShitterList] §fAdded " + username + " to the shitterlist");
                player.sendMessage(message, false);
            }
        } else {
            if (player != null) {
                Text message = Text.literal("§c[ShitterList] §f" + username + " is already on the shitterlist");
                player.sendMessage(message, false);
            }
        }
        
        return 1;
    }
    
    private static int removePlayer(CommandContext<FabricClientCommandSource> context) {
        String username = StringArgumentType.getString(context, "username");
        ShitterListConfig config = ShitterListMod.getConfig();
        ClientPlayerEntity player = context.getSource().getPlayer();
        
        if (config.isHardcodedShitter(username)) {
            if (player != null) {
                Text message = Text.literal("§c[ShitterList] §f" + username + " cannot be removed from the shitterlist");
                player.sendMessage(message, false);
            }
        } else if (config.removeFromShitterList(username)) {
            if (player != null) {
                Text message = Text.literal("§a[ShitterList] §fRemoved " + username + " from the shitterlist");
                player.sendMessage(message, false);
            }
        } else {
            if (player != null) {
                Text message = Text.literal("§c[ShitterList] §f" + username + " is not on the shitterlist");
                player.sendMessage(message, false);
            }
        }
        
        return 1;
    }
    
    private static int listPlayers(CommandContext<FabricClientCommandSource> context) {
        ShitterListConfig config = ShitterListMod.getConfig();
        ClientPlayerEntity player = context.getSource().getPlayer();
        
        if (player != null) {
            Set<String> allShitters = config.getAllShitters();
            
            if (allShitters.isEmpty()) {
                Text message = Text.literal("§6[ShitterList] §fThe shitterlist is empty");
                player.sendMessage(message, false);
            } else {
                Text header = Text.literal("§6[ShitterList] §fPlayers on shitterlist:");
                player.sendMessage(header, false);
                
                // Show hardcoded players first with special indicator
                Set<String> hardcodedShitters = config.getHardcodedShitters();
                for (String shitter : hardcodedShitters) {
                    Text playerText = Text.literal("§7- §c" + shitter + " §4[HARDCODED]");
                    player.sendMessage(playerText, false);
                }
                
                // Show user-added players
                Set<String> userShitters = config.getShitterList();
                for (String shitter : userShitters) {
                    Text playerText = Text.literal("§7- §f" + shitter);
                    player.sendMessage(playerText, false);
                }
            }
        }
        
        return 1;
    }
    
    private static int clearList(CommandContext<FabricClientCommandSource> context) {
        ShitterListConfig config = ShitterListMod.getConfig();
        ClientPlayerEntity player = context.getSource().getPlayer();
        
        if (!config.getShitterList().isEmpty()) {
            for (String username : config.getShitterList()) {
                config.removeFromShitterList(username);
            }
            
            if (player != null) {
                Text message = Text.literal("§a[ShitterList] §fCleared user-added players from the shitterlist");
                player.sendMessage(message, false);
                
                // Show remaining hardcoded players
                Set<String> hardcodedShitters = config.getHardcodedShitters();
                if (!hardcodedShitters.isEmpty()) {
                    Text notice = Text.literal("§6[ShitterList] §fHardcoded players cannot be removed:");
                    player.sendMessage(notice, false);
                    for (String shitter : hardcodedShitters) {
                        Text playerText = Text.literal("§7- §c" + shitter + " §4[HARDCODED]");
                        player.sendMessage(playerText, false);
                    }
                }
            }
        } else {
            if (player != null) {
                // Check if there are only hardcoded players
                if (!config.getAllShitters().isEmpty()) {
                    Text message = Text.literal("§6[ShitterList] §fOnly hardcoded players remain on the shitterlist:");
                    player.sendMessage(message, false);
                    Set<String> hardcodedShitters = config.getHardcodedShitters();
                    for (String shitter : hardcodedShitters) {
                        Text playerText = Text.literal("§7- §c" + shitter + " §4[HARDCODED]");
                        player.sendMessage(playerText, false);
                    }
                } else {
                    Text message = Text.literal("§c[ShitterList] §fThe shitterlist is already empty");
                    player.sendMessage(message, false);
                }
            }
        }
        
        return 1;
    }
    
    private static int togglePartyChatMessage(CommandContext<FabricClientCommandSource> context) {
        ShitterListConfig config = ShitterListMod.getConfig();
        ClientPlayerEntity player = context.getSource().getPlayer();
        
        config.togglePartyChatMessage();
        
        if (player != null) {
            String status = config.shouldSendPartyChatMessage() ? "enabled" : "disabled";
            Text message = Text.literal("§a[ShitterList] §fParty chat messages " + status);
            player.sendMessage(message, false);
        }
        
        return 1;
    }
    
    private static int showHelp(CommandContext<FabricClientCommandSource> context) {
        ClientPlayerEntity player = context.getSource().getPlayer();
        
        if (player != null) {
            Text header = Text.literal("§6[ShitterList] §fCommands:");
            player.sendMessage(header, false);
            
            Text addCmd = Text.literal("§7/shitterlist add <username> §f- Add a player to the shitterlist");
            player.sendMessage(addCmd, false);
            
            Text removeCmd = Text.literal("§7/shitterlist remove <username> §f- Remove a player from the shitterlist");
            player.sendMessage(removeCmd, false);
            
            Text listCmd = Text.literal("§7/shitterlist list §f- Show all players on the shitterlist");
            player.sendMessage(listCmd, false);
            
            Text clearCmd = Text.literal("§7/shitterlist clear §f- Clear the entire shitterlist");
            player.sendMessage(clearCmd, false);
            
            String msgStatus = ShitterListMod.getConfig().shouldSendPartyChatMessage() ? "enabled" : "disabled";
            Text toggleCmd = Text.literal("§7/shitterlist togglemsg §f- Toggle party chat messages (" + msgStatus + ")");
            player.sendMessage(toggleCmd, false);

            Text footer = Text.literal("§4[§7Made with §clove §7by §bhydro§4]");
            player.sendMessage(footer, false);
        }
        
        return 1;
    }
}
