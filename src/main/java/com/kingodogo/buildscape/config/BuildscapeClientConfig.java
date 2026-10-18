package com.kingodogo.buildscape.config;

import com.kingodogo.buildscape.BuildScape;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class BuildscapeClientConfig {
    public static final String KEY_HIDE_CONFIG_BUTTON = "HideBuildscapeConfig";
    public static final String KEY_PARALLEL_MODEL_LOADING = "OptimizeBuildscapeModelLoading";
    public static final String KEY_CACHE_MODEL_MATERIALS = "OptimizeBuildscapeModelMaterials";
    public static final String KEY_PARALLEL_MODEL_BAKING = "OptimizeBuildscapeModelBaking";
    public static final String KEY_PARALLEL_BLOCK_STATE_CACHE = "OptimizeBuildscapeBlockStateCache";
    public static final String KEY_MAX_PIPE_NETWORK_SIZE = "MaxPipeNetworkSize";
    public static final String KEY_CAKE_STACKING = "isCakeStack";
    public static final String KEY_WATER_BOTTLE_STACKING = "isWaterbottleStack";

    private static final LinkedHashMap<String, String> DEFAULTS = new LinkedHashMap<>();
    private static volatile BuildscapeClientConfig INSTANCE;

    static {
        DEFAULTS.put(KEY_HIDE_CONFIG_BUTTON, "false");
        DEFAULTS.put(KEY_PARALLEL_MODEL_LOADING, "true");
        DEFAULTS.put(KEY_CACHE_MODEL_MATERIALS, "true");
        DEFAULTS.put(KEY_PARALLEL_MODEL_BAKING, "true");
        DEFAULTS.put(KEY_PARALLEL_BLOCK_STATE_CACHE, "true");
        DEFAULTS.put(KEY_MAX_PIPE_NETWORK_SIZE, "64");
        DEFAULTS.put(KEY_CAKE_STACKING, "true");
        DEFAULTS.put(KEY_WATER_BOTTLE_STACKING, "true");
    }

    private final Map<String, String> values;
    private final Path configDirectory;
    private boolean readOnly;
    private String source;

    private BuildscapeClientConfig() {
        this(null);
    }

    BuildscapeClientConfig(Path configDirectory) {
        this.configDirectory = configDirectory;
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

    private File getConfigFile() {
        Path configDir = configDirectory;
        try {
            if (configDir == null && FMLPaths.CONFIGDIR != null) {
                configDir = FMLPaths.CONFIGDIR.get();
            }
        } catch (Throwable ignored) {
        }
        if (configDir == null) {
            return null;
        }
        Path buildscapeDir = configDir.resolve("buildscape");
        File dir = buildscapeDir.toFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return buildscapeDir.resolve("buildscape.cfg").toFile();
    }

    private File getLegacyConfigFile() {
        Path configDir = configDirectory;
        try {
            if (configDir == null && FMLPaths.CONFIGDIR != null) {
                configDir = FMLPaths.CONFIGDIR.get();
            }
        } catch (Throwable ignored) {
        }
        if (configDir == null) {
            return null;
        }
        return configDir.resolve("buildscape.cfg").toFile();
    }

    private void load() {
        File file = getConfigFile();
        if (file == null) {
            return;
        }
        File legacyFile = getLegacyConfigFile();

        file = migrateLegacyConfig(file, legacyFile);

        if (!file.exists()) {
            save();
            return;
        }

        try {
            source = ConfigLayoutIO.readText(file.toPath());
            readOnly |= ConfigLayoutIO.protectInvalidEncoding(file.toPath());
            Map<String, String> loaded = ConfigLayoutIO.readEntries(source);
            values.putAll(loaded);
            if (!readOnly) appendMissingDefaults(file.toPath(), loaded);
        } catch (IOException e) {
            readOnly = true;
            if (source != null) {
                for (String line : source.split("\\R")) {
                    try {
                        values.putAll(ConfigLayoutIO.readEntries(line));
                    } catch (IOException ignored) {
                    }
                }
            }
            BuildScape.getLogger().warn("BuildscapeClientConfig: Config is read-only because it could not be parsed", e);
        }
    }

    private void appendMissingDefaults(Path file, Map<String, String> loaded) throws IOException {
        String newline = source.contains("\r\n") ? "\r\n" : source.contains("\r") ? "\r" : "\n";
        StringBuilder missing = new StringBuilder();
        for (Map.Entry<String, String> entry : DEFAULTS.entrySet()) {
            if (loaded.containsKey(entry.getKey())) continue;
            if (missing.length() == 0 && !source.isEmpty() && !source.endsWith("\n") && !source.endsWith("\r")) {
                missing.append(newline);
            }
            missing.append("# ").append(description(entry.getKey())).append(newline);
            missing.append(entry.getKey()).append(" = ").append(entry.getValue()).append(newline);
        }
        if (missing.length() == 0) return;
        ConfigLayoutIO.append(file, missing.toString(), source);
        source += missing;
    }

    private File migrateLegacyConfig(File file, File legacyFile) {
        if (legacyFile == null || !legacyFile.exists()) return file;
        Path legacy = legacyFile.toPath();
        Path backup = legacy.resolveSibling(legacyFile.getName() + ".backup");
        Path completion = legacy.resolveSibling(legacyFile.getName() + ".backup.complete");
        if (Files.exists(completion)) return file;
        try {
            String output = null;
            boolean replacementDecoded = false;
            if (!file.exists()) {
                String original = ConfigLayoutIO.readText(legacy);
                Map<String, String> migrated = new LinkedHashMap<>(DEFAULTS);
                migrated.putAll(ConfigLayoutIO.readEntries(original));
                replacementDecoded = ConfigLayoutIO.protectInvalidEncoding(legacy);
                output = groupedText(original, migrated);
            }
            if (!Files.exists(backup)) Files.copy(legacy, backup);
            else if (!Files.isRegularFile(backup) || Files.mismatch(legacy, backup) != -1) {
                throw new IOException("Legacy backup does not match the original: " + backup);
            }
            if (output != null) {
                if (replacementDecoded) Files.copy(legacy, file.toPath());
                else ConfigLayoutIO.writeNew(file.toPath(), output);
            }
            Files.createFile(completion);
            if (output != null) {
                BuildScape.getLogger().info("BuildscapeClientConfig: Migrated config/buildscape.cfg -> config/buildscape/buildscape.cfg");
            }
        } catch (IOException e) {
            BuildScape.getLogger().warn("BuildscapeClientConfig: Failed to migrate legacy config - " + e.getMessage());
            if (!file.exists()) {
                readOnly = true;
                return legacyFile;
            }
        }
        return file;
    }

    public void save() {
        if (readOnly) {
            BuildScape.getLogger().warn("BuildscapeClientConfig: Not saving read-only config; unreadable/legacy file kept as-is");
            return;
        }
        File file = getConfigFile();
        if (file == null) return;
        try {
            String output = groupedText(source, values);
            ConfigLayoutIO.write(file.toPath(), output, source, true);
            source = output;
        } catch (IOException e) {
            BuildScape.getLogger().warn("BuildscapeClientConfig: Failed to write config", e);
        }
    }

    private static String groupedText(String source, Map<String, String> values) {
        StringBuilder text = new StringBuilder("# BuildScape Configuration\n# Changes take effect on the next game launch.\n");
        for (String section : new String[]{"Gameplay", "Performance", "Visuals & particles", "Interface", "Other"}) {
            if (values.keySet().stream().noneMatch(key -> section.equals(section(key)))) continue;
            text.append("\n# --- ").append(section).append(" ---\n");
            for (Map.Entry<String, String> entry : values.entrySet()) {
                if (!section.equals(section(entry.getKey()))) continue;
                text.append("# ").append(description(entry.getKey())).append('\n');
                text.append(entry.getKey()).append(" = ").append(entry.getValue()).append('\n');
            }
        }
        return ConfigLayoutIO.retainComments(source, text.toString(), false);
    }

    private static String section(String key) {
        return switch (key) {
            case KEY_CAKE_STACKING, KEY_WATER_BOTTLE_STACKING, KEY_MAX_PIPE_NETWORK_SIZE -> "Gameplay";
            case KEY_PARALLEL_MODEL_LOADING, KEY_CACHE_MODEL_MATERIALS,
                    KEY_PARALLEL_MODEL_BAKING, KEY_PARALLEL_BLOCK_STATE_CACHE -> "Performance";
            case KEY_HIDE_CONFIG_BUTTON -> "Interface";
            default -> "Other";
        };
    }

    private static String description(String key) {
        return switch (key) {
            case KEY_CAKE_STACKING -> "Default cake-stacking game rule (stacks of 64).";
            case KEY_WATER_BOTTLE_STACKING -> "Default water-bottle-stacking game rule (stacks of 16).";
            case KEY_MAX_PIPE_NETWORK_SIZE -> "Maximum number of pipes in one network.";
            case KEY_PARALLEL_MODEL_LOADING -> "Load bundled block models in parallel.";
            case KEY_CACHE_MODEL_MATERIALS -> "Cache block-model material lookups.";
            case KEY_PARALLEL_MODEL_BAKING -> "Bake bundled block models in parallel.";
            case KEY_PARALLEL_BLOCK_STATE_CACHE -> "Build block-state caches in parallel.";
            case KEY_HIDE_CONFIG_BUTTON -> "Hide the BuildScape button on client screens.";
            default -> "Unrecognized setting retained with its original value.";
        };
    }

    private boolean getBoolean(String key) {
        return Boolean.parseBoolean(values.getOrDefault(key, DEFAULTS.getOrDefault(key, "false")));
    }

    private int getInt(String key, int fallback) {
        try {
            return Integer.parseInt(values.getOrDefault(key, String.valueOf(fallback)));
        } catch (NumberFormatException e) {
            return fallback;
        }
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

    public int getMaxPipeNetworkSize() {
        return Math.max(1, getInt(KEY_MAX_PIPE_NETWORK_SIZE, 64));
    }

    public boolean isCakeStackingEnabled() {
        return getBoolean(KEY_CAKE_STACKING);
    }

    public boolean isWaterBottleStackingEnabled() {
        return getBoolean(KEY_WATER_BOTTLE_STACKING);
    }
}
