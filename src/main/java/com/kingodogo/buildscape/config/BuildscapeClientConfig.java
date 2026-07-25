package com.kingodogo.buildscape.config;

import com.kingodogo.buildscape.BuildScape;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class BuildscapeClientConfig {
    public static final String KEY_HIDE_CONFIG_BUTTON = "HideBuildscapeConfig";
    public static final String KEY_PARALLEL_MODEL_LOADING = "OptimizeBuildscapeModelLoading";
    public static final String KEY_CACHE_MODEL_MATERIALS = "OptimizeBuildscapeModelMaterials";
    public static final String KEY_PARALLEL_MODEL_BAKING = "OptimizeBuildscapeModelBaking";
    public static final String KEY_PARALLEL_BLOCK_STATE_CACHE = "OptimizeBuildscapeBlockStateCache";

    private static final LinkedHashMap<String, String> DEFAULTS = new LinkedHashMap<>();
    private static volatile BuildscapeClientConfig INSTANCE;

    static {
        DEFAULTS.put(KEY_HIDE_CONFIG_BUTTON, "false");
        DEFAULTS.put(KEY_PARALLEL_MODEL_LOADING, "true");
        DEFAULTS.put(KEY_CACHE_MODEL_MATERIALS, "true");
        DEFAULTS.put(KEY_PARALLEL_MODEL_BAKING, "true");
        DEFAULTS.put(KEY_PARALLEL_BLOCK_STATE_CACHE, "true");
    }

    private final Map<String, String> values;

    private BuildscapeClientConfig() {
        this.values = new LinkedHashMap<>(DEFAULTS);
        load();
    }

    public static BuildscapeClientConfig get() {
        BuildscapeClientConfig config = INSTANCE;
        if (config == null) {
            synchronized (BuildscapeClientConfig.class) {
                config = INSTANCE;
                if (config == null) {
                    config = new BuildscapeClientConfig();
                    INSTANCE = config;
                }
            }
        }
        return config;
    }

    public static synchronized void reload() {
        INSTANCE = new BuildscapeClientConfig();
    }

    private static File getConfigFile() {
        Path configDir = FMLPaths.CONFIGDIR.get();
        return configDir.resolve("buildscape.cfg").toFile();
    }

    private void load() {
        File file = getConfigFile();
        if (!file.exists()) {
            save();
            return;
        }

        Set<String> loadedKeys = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                int eq = line.indexOf('=');
                if (eq <= 0) {
                    continue;
                }

                String key = line.substring(0, eq).trim();
                String value = line.substring(eq + 1).trim();
                if (DEFAULTS.containsKey(key)) {
                    values.put(key, value);
                    loadedKeys.add(key);
                }
            }
        } catch (IOException e) {
            BuildScape.getLogger().warn("BuildscapeClientConfig: Failed to read buildscape.cfg - using defaults. " + e.getMessage());
        }

        if (loadedKeys.size() != DEFAULTS.size()) {
            save();
        }
    }

    private void save() {
        File file = getConfigFile();
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("# BuildScape Client Configuration");
            writer.println("# Edit this file to customise BuildScape behaviour.");
            writer.println("# Changes take effect on the next game launch.");
            writer.println();
            for (Map.Entry<String, String> entry : values.entrySet()) {
                writer.println(entry.getKey() + " = " + entry.getValue());
            }
        } catch (IOException e) {
            BuildScape.getLogger().warn("BuildscapeClientConfig: Failed to write buildscape.cfg - " + e.getMessage());
        }
    }

    private boolean getBoolean(String key) {
        return Boolean.parseBoolean(values.getOrDefault(key, DEFAULTS.getOrDefault(key, "false")));
    }

    public boolean isConfigButtonHidden() {
        return getBoolean(KEY_HIDE_CONFIG_BUTTON);
    }

    public boolean isParallelModelLoadingEnabled() {
        return getBoolean(KEY_PARALLEL_MODEL_LOADING);
    }

    public boolean isModelMaterialCacheEnabled() {
        return getBoolean(KEY_CACHE_MODEL_MATERIALS);
    }

    public boolean isParallelModelBakingEnabled() {
        return getBoolean(KEY_PARALLEL_MODEL_BAKING);
    }

    public boolean isParallelBlockStateCacheEnabled() {
        return getBoolean(KEY_PARALLEL_BLOCK_STATE_CACHE);
    }
}
