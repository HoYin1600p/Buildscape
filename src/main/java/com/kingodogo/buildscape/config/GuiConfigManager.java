package com.kingodogo.buildscape.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kingodogo.buildscape.BuildScape;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class GuiConfigManager {
    private static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .serializeNulls()
        .create();

    private static final Map<String, GuiConfigData> CACHE = new HashMap<>();

    private File getConfigDir() {
        String configPath = Paths.get("config", BuildScape.MODID).toString();
        File dir = new File(configPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private File getGuiConfigFile(String tabName) {
        String fileName = sanitizeFileName(tabName) + "-GUI.json";
        return new File(getConfigDir(), fileName);
    }

    private String sanitizeFileName(String tabName) {
        return tabName.replaceAll("[^a-zA-Z0-9-_]", "_");
    }

    public GuiConfigData loadConfig(String tabName) {
        String cacheKey = tabName.toLowerCase();

        if (CACHE.containsKey(cacheKey)) {
            return CACHE.get(cacheKey);
        }

        File file = getGuiConfigFile(tabName);
        GuiConfigData config = new GuiConfigData();

        if (file.exists()) {
            try {
                String source = ConfigLayoutIO.readText(file.toPath());
                ConfigLayoutIO.protectInvalidEncoding(file.toPath());
                config = GSON.fromJson(source, GuiConfigData.class);
                if (config == null) {
                    config = new GuiConfigData();
                }
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

        CACHE.put(cacheKey, config);
        return config;
    }

    public void saveConfig(String tabName, GuiConfigData config) {
        if (config == null) {
            BuildScape.getLogger().warn("Attempted to save null GUI config for tab '{}'", tabName);
            return;
        }

        File file = getGuiConfigFile(tabName);
        try {
            if (file.exists() && ConfigLayoutIO.protectInvalidEncoding(file.toPath())) return;
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (FileWriter writer = new FileWriter(file, java.nio.charset.StandardCharsets.UTF_8)) {
                GSON.toJson(config, writer);
                writer.flush();
            }

            String cacheKey = tabName.toLowerCase();
            CACHE.put(cacheKey, config);


        } catch (Exception e) {
            BuildScape.getLogger().error("Failed to save GUI config for tab '{}': {}", tabName, e.getMessage());
            e.printStackTrace();
        }
    }

    public void clearCache(String tabName) {
        CACHE.remove(tabName.toLowerCase());
    }

    public void clearAllCache() {
        CACHE.clear();
    }

    private static GuiConfigManager INSTANCE;

    public static GuiConfigManager get() {
        if (INSTANCE == null) {
            INSTANCE = new GuiConfigManager();
        }
        return INSTANCE;
    }
}

