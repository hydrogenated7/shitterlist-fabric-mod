package com.example.shitterlist;

import com.example.shitterlist.config.ShitterListConfig;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShitterListMod implements ModInitializer {
    public static final String MOD_ID = "shitterlist";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    private static ShitterListConfig config;
    
    @Override
    public void onInitialize() {
        LOGGER.info("Initializing ShitterList Mod");
        
        config = new ShitterListConfig();
        
        LOGGER.info("ShitterList Mod initialized successfully");
    }
    
    public static ShitterListConfig getConfig() {
        return config;
    }
}
