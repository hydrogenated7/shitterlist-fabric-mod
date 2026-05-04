package com.example.shitterlist.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class ShitterListConfig {
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("shitterlist.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    private Set<String> shitterList = new HashSet<>();
    private boolean sendPartyChatMessage = true;
    private static final Set<String> HARDCODED_SHITTERS = Set.of("prefour", "c3lestiqal");
    
    public ShitterListConfig() {
        load();
    }
    
    public void load() {
        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                shitterList = GSON.fromJson(reader, new TypeToken<Set<String>>() {}.getType());
                if (shitterList == null) {
                    shitterList = new HashSet<>();
                }
            } catch (IOException e) {
                System.err.println("Failed to load shitterlist config: " + e.getMessage());
                shitterList = new HashSet<>();
            }
        }
    }
    
    public void save() {
        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
            GSON.toJson(shitterList, writer);
        } catch (IOException e) {
            System.err.println("Failed to save shitterlist config: " + e.getMessage());
        }
    }
    
    public boolean addToShitterList(String username) {
        if (shitterList.add(username.toLowerCase())) {
            save();
            return true;
        }
        return false;
    }
    
    public boolean removeFromShitterList(String username) {
        String lowerUsername = username.toLowerCase();
        if (HARDCODED_SHITTERS.contains(lowerUsername)) {
            return false; // Cannot remove hardcoded shitters
        }
        if (shitterList.remove(lowerUsername)) {
            save();
            return true;
        }
        return false;
    }
    
    public boolean isOnShitterList(String username) {
        String lowerUsername = username.toLowerCase();
        return shitterList.contains(lowerUsername) || HARDCODED_SHITTERS.contains(lowerUsername);
    }
    
    public boolean isHardcodedShitter(String username) {
        return HARDCODED_SHITTERS.contains(username.toLowerCase());
    }
    
    public Set<String> getHardcodedShitters() {
        return new HashSet<>(HARDCODED_SHITTERS);
    }
    
    public Set<String> getAllShitters() {
        Set<String> allShitters = new HashSet<>(shitterList);
        allShitters.addAll(HARDCODED_SHITTERS);
        return allShitters;
    }
    
    public Set<String> getShitterList() {
        return new HashSet<>(shitterList);
    }
    
    public boolean isShitterListEmpty() {
        return shitterList.isEmpty();
    }
    
    public boolean shouldSendPartyChatMessage() {
        return sendPartyChatMessage;
    }
    
    public void togglePartyChatMessage() {
        sendPartyChatMessage = !sendPartyChatMessage;
        save();
    }
}
