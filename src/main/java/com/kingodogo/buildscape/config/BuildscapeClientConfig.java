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
    public static final String KEY_UI_THEME = "UITheme";
    public static final String KEY_UI_BACKGROUND = "UIBackground";
    public static final String KEY_SIDE_PANEL_EXPANDED = "SidePanelExpanded";
    public static final String KEY_ANIMATION_SPEED = "AnimationSpeed";
    public static final String KEY_LAST_ACTIVE_TAB = "LastActiveTab";

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
        this(ConfigPaths.root());
    }

    BuildscapeClientConfig(Path configDirectory) {
        this.configDirectory = configDirectory != null ? configDirectory : ConfigPaths.root();
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
            configDir = ConfigPaths.root();
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
            configDir = ConfigPaths.root();
        }
        return configDir.resolve("buildscape.cfg").toFile();
    }

    private void load() {
        File file = getConfigFile();
        if (file == null) {
            return;
        }
        File legacyFile = getLegacyConfigFile();

        if (legacyFile != null && !file.exists() && legacyFile.exists()) {
            try {
                Files.copy(legacyFile.toPath(), file.toPath());
                BuildScape.getLogger().info("BuildscapeClientConfig: Migrated config/buildscape.cfg -> config/buildscape/buildscape.cfg");
            } catch (IOException e) {
                readOnly = true;
                file = legacyFile;
                BuildScape.getLogger().warn("BuildscapeClientConfig: Failed to copy legacy config - " + e.getMessage());
            }
        }

        try {
            if (file.exists()) {
                source = ConfigLayoutIO.readText(file.toPath());
                readOnly |= ConfigLayoutIO.hasReplacementCharacters(source);
                values.putAll(ConfigLayoutIO.readEntries(source));
            }
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

        // Migrate and merge legacy buildscape/ui-state.cfg if it exists
        Path root = configDirectory != null ? configDirectory : ConfigPaths.root();
        Path uiFile = root.resolve("buildscape/ui-state.cfg");
        if (Files.exists(uiFile)) {
            try {
                String uiSource = ConfigLayoutIO.readText(uiFile);
                Map<String, String> uiEntries = ConfigLayoutIO.readEntries(uiSource);
                for (Map.Entry<String, String> entry : uiEntries.entrySet()) {
                    values.putIfAbsent(entry.getKey(), entry.getValue());
                }
                Files.deleteIfExists(uiFile);
                BuildScape.getLogger().info("BuildscapeClientConfig: Merged buildscape/ui-state.cfg into buildscape/buildscape.cfg and removed ui-state.cfg");
                if (file.exists() && !readOnly) {
                    save();
                }
            } catch (Exception e) {
                BuildScape.getLogger().warn("BuildscapeClientConfig: Failed to merge ui-state.cfg into buildscape.cfg", e);
            }
        }

        if (!file.exists()) {
            save();
        }
    }

    public void save() {
        if (readOnly) {
            BuildScape.getLogger().warn("BuildscapeClientConfig: Not saving read-only config; unreadable/legacy file kept as-is");
            return;
        }
        File file = getConfigFile();
        if (file == null) return;
        try {
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
            String output = ConfigLayoutIO.retainComments(source, text.toString(), false);
            ConfigLayoutIO.write(file.toPath(), output, source, true);
            source = output;
        } catch (IOException e) {
            BuildScape.getLogger().warn("BuildscapeClientConfig: Failed to write config", e);
        }
    }

    private static String section(String key) {
        return switch (key) {
            case KEY_CAKE_STACKING, KEY_WATER_BOTTLE_STACKING, KEY_MAX_PIPE_NETWORK_SIZE -> "Gameplay";
            case KEY_PARALLEL_MODEL_LOADING, KEY_CACHE_MODEL_MATERIALS,
                    KEY_PARALLEL_MODEL_BAKING, KEY_PARALLEL_BLOCK_STATE_CACHE -> "Performance";
            case KEY_HIDE_CONFIG_BUTTON, KEY_UI_THEME, KEY_UI_BACKGROUND,
                    KEY_SIDE_PANEL_EXPANDED, KEY_ANIMATION_SPEED, KEY_LAST_ACTIVE_TAB -> "Interface";
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
            case KEY_UI_THEME -> "Active theme for the BuildScape configuration screen (dark, light, cyber, etc.).";
            case KEY_UI_BACKGROUND -> "Background style for the BuildScape configuration screen (none, blueprint, etc.).";
            case KEY_SIDE_PANEL_EXPANDED -> "Whether the configuration screen side panel is expanded.";
            case KEY_ANIMATION_SPEED -> "UI animation speed index (0=normal, 1=fast, 2=instant).";
            case KEY_LAST_ACTIVE_TAB -> "Last selected tab index in the BuildScape configuration screen.";
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

    public void setMaxPipeNetworkSize(int size) {
        values.put(KEY_MAX_PIPE_NETWORK_SIZE, String.valueOf(Math.max(1, size)));
    }

    public boolean isCakeStackingEnabled() {
        return getBoolean(KEY_CAKE_STACKING);
    }

    public boolean isWaterBottleStackingEnabled() {
        return getBoolean(KEY_WATER_BOTTLE_STACKING);
    }

    public String getUiTheme() {
        return values.getOrDefault(KEY_UI_THEME, "dark");
    }

    public void setUiTheme(String theme) {
        values.put(KEY_UI_THEME, theme != null ? theme : "dark");
        save();
    }

    public String getUiBackground() {
        return values.getOrDefault(KEY_UI_BACKGROUND, "none");
    }

    public void setUiBackground(String background) {
        values.put(KEY_UI_BACKGROUND, background != null ? background : "none");
        save();
    }

    public boolean isSidePanelExpanded() {
        return getBoolean(KEY_SIDE_PANEL_EXPANDED);
    }

    public void setSidePanelExpanded(boolean expanded) {
        values.put(KEY_SIDE_PANEL_EXPANDED, String.valueOf(expanded));
        save();
    }

    public int getAnimationSpeedIndex() {
        return getInt(KEY_ANIMATION_SPEED, 0);
    }

    public void setAnimationSpeedIndex(int index) {
        values.put(KEY_ANIMATION_SPEED, String.valueOf(index));
        save();
    }

    public int getLastActiveTab() {
        return getInt(KEY_LAST_ACTIVE_TAB, 0);
    }

    public void setLastActiveTab(int tab) {
        values.put(KEY_LAST_ACTIVE_TAB, String.valueOf(tab));
        save();
    }
}
