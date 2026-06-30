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
import java.util.LinkedHashMap;
import java.util.Map;

public class BuildscapeClientConfig {
    public static final String KEY_HIDE_CONFIG_BUTTON = "HideBuildscapeConfig";

    private static final LinkedHashMap<String, String> DEFAULTS = new LinkedHashMap<>();
    private static BuildscapeClientConfig INSTANCE;

    static {
        DEFAULTS.put(KEY_HIDE_CONFIG_BUTTON, "false");
    }

    private final Map<String, String> values;

    private BuildscapeClientConfig() {
        this.values = new LinkedHashMap<>(DEFAULTS);
        load();
    }

    public static BuildscapeClientConfig get() {
        if (INSTANCE == null) {
            INSTANCE = new BuildscapeClientConfig();
        }
        return INSTANCE;
    }

    public static void reload() {
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
                }
            }
        } catch (IOException e) {
            BuildScape.getLogger().warn("BuildscapeClientConfig: Failed to read buildscape.cfg - using defaults. " + e.getMessage());
        }

        boolean needsSave = false;
        for (String key : DEFAULTS.keySet()) {
            if (!values.containsKey(key)) {
                values.put(key, DEFAULTS.get(key));
                needsSave = true;
            }
        }

        if (needsSave) {
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
}
