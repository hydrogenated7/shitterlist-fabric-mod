package com.example.shitterlist;

import com.example.shitterlist.command.ShitterListCommand;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

public class ShitterListClient implements ClientModInitializer {
    
    private static PartyDetectionHandler partyDetectionHandler;
    
    @Override
    public void onInitializeClient() {
        ShitterListMod.LOGGER.info("Initializing ShitterList Client");
        
        partyDetectionHandler = new PartyDetectionHandler(ShitterListMod.getConfig());
        
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            ShitterListCommand.register(dispatcher);
        });
        
        partyDetectionHandler.register();
        
        ShitterListMod.LOGGER.info("ShitterList Client initialized successfully");
    }
    
    public static PartyDetectionHandler getPartyDetectionHandler() {
        return partyDetectionHandler;
    }
}
