package com.kingodogo.buildscape.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kingodogo.buildscape.BuildScape;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Manager for loading and saving GUI configuration files.
 * Handles JSON serialization/deserialization for GUI layouts.
 */
public class GuiConfigManager {
    private static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .serializeNulls()
        .create();
    
    private static final Map<String, GuiConfigData> CACHE = new HashMap<>();
    
    /**
     * Get the config directory for GUI configs
     */
    private File getConfigDir() {
        String configPath = Paths.get("config", BuildScape.MODID).toString();
        File dir = new File(configPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }
    
    /**
     * Get the file path for a tab's GUI config
     * @param tabName The name of the tab (e.g., "PillarItems", "PillarParticles")
     * @return The file for the GUI config
     */
    private File getGuiConfigFile(String tabName) {
        String fileName = sanitizeFileName(tabName) + "-GUI.json";
        return new File(getConfigDir(), fileName);
    }
    
    /**
     * Sanitize a tab name to be a valid file name
     */
    private String sanitizeFileName(String tabName) {
        // Replace invalid characters with underscores
        return tabName.replaceAll("[^a-zA-Z0-9-_]", "_");
    }
    
    /**
     * Load GUI configuration for a tab
     * @param tabName The name of the tab
     * @return The GUI configuration data, or a new empty config if file doesn't exist
     */
    public GuiConfigData loadConfig(String tabName) {
        String cacheKey = tabName.toLowerCase();
        
        // Return cached config if available
        if (CACHE.containsKey(cacheKey)) {
            return CACHE.get(cacheKey);
        }
        
        File file = getGuiConfigFile(tabName);
        GuiConfigData config = new GuiConfigData();
        
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                config = GSON.fromJson(reader, GuiConfigData.class);
                if (config == null) {
                    config = new GuiConfigData();
                }
                // Ensure maps are initialized
                if (config.elements == null) {
                    config.elements = new java.util.HashMap<>();
                }
                if (config.screen == null) {
                    config.screen = new GuiConfigData.ScreenConfig();
                }
            } catch (Exception e) {
                BuildScape.getLogger().error("Failed to load GUI config for tab '{}': {}", tabName, e.getMessage());
                e.printStackTrace();
                config = new GuiConfigData();
            }
        }
        
        // Cache the config
        CACHE.put(cacheKey, config);
        return config;
    }
    
    /**
     * Save GUI configuration for a tab
     * @param tabName The name of the tab
     * @param config The GUI configuration data to save
     */
    public void saveConfig(String tabName, GuiConfigData config) {
        if (config == null) {
            BuildScape.getLogger().warn("Attempted to save null GUI config for tab '{}'", tabName);
            return;
        }
        
        File file = getGuiConfigFile(tabName);
        try {
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            
            try (FileWriter writer = new FileWriter(file)) {
                GSON.toJson(config, writer);
                writer.flush();
            }
            
            // Update cache
            String cacheKey = tabName.toLowerCase();
            CACHE.put(cacheKey, config);
            

        } catch (Exception e) {
            BuildScape.getLogger().error("Failed to save GUI config for tab '{}': {}", tabName, e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Clear the cache for a specific tab (useful when reloading)
     * @param tabName The name of the tab
     */
    public void clearCache(String tabName) {
        CACHE.remove(tabName.toLowerCase());
    }
    
    /**
     * Clear all cached configs
     */
    public void clearAllCache() {
        CACHE.clear();
    }
    
    /**
     * Get the singleton instance
     */
    private static GuiConfigManager INSTANCE;
    
    public static GuiConfigManager get() {
        if (INSTANCE == null) {
            INSTANCE = new GuiConfigManager();
        }
        return INSTANCE;
    }
}

