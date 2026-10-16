package com.kingodogo.buildscape.config;

import com.kingodogo.buildscape.BuildscapeCommon;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

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
        DEFAULTS.put(KEY_UI_THEME, "dark");
        DEFAULTS.put(KEY_UI_BACKGROUND, "none");
        DEFAULTS.put(KEY_SIDE_PANEL_EXPANDED, "false");
        DEFAULTS.put(KEY_ANIMATION_SPEED, "0");
        DEFAULTS.put(KEY_LAST_ACTIVE_TAB, "0");
    }

    private final Map<String, String> values;
    private final Path configDirectory;
    private final boolean client;
    private final Map<String, String> uiValues = new LinkedHashMap<>();
    private boolean readOnly;
    private boolean uiReadOnly;
    private String source;
    private String uiSource;
    private static final Set<String> UI_STATE = Set.of(KEY_LAST_ACTIVE_TAB, KEY_SIDE_PANEL_EXPANDED);

    private BuildscapeClientConfig() {
        this(ConfigPaths.root());
    }

    BuildscapeClientConfig(Path configDirectory) {
        this(configDirectory, com.kingodogo.buildscape.platform.Services.PLATFORM.isClient());
    }

    BuildscapeClientConfig(Path configDirectory, boolean client) {
        this.configDirectory = configDirectory;
        this.client = client;
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
        File dir = configDirectory.resolve("buildscape").toFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return new File(dir, "buildscape.cfg");
    }

    private File getLegacyConfigFile() {
        return configDirectory.resolve("buildscape.cfg").toFile();
    }

    private void load() {
        File file = getConfigFile();
        File legacyFile = getLegacyConfigFile();

        if (legacyFile.exists() && !file.exists()) {
            try {
                Files.copy(legacyFile.toPath(), file.toPath());
            } catch (IOException e) {
                readOnly = true;
                BuildscapeCommon.LOGGER.warn("BuildscapeClientConfig: Failed to copy legacy config", e);
                file = legacyFile;
            }
        }

        try {
            if (file.exists()) {
                source = ConfigLayoutIO.readText(file.toPath());
                values.putAll(ConfigLayoutIO.readEntries(source));
            }
        } catch (IOException e) {
            readOnly = true;
            // Keep usable entries available, but never rewrite a partially parsed file.
            if (source != null) {
                for (String line : source.split("\\R")) {
                    try { values.putAll(ConfigLayoutIO.readEntries(line)); }
                    catch (IOException ignored) { }
                }
            }
            BuildscapeCommon.LOGGER.warn("BuildscapeClientConfig: Config is read-only because it could not be parsed", e);
        }
        if (client) {
            Path uiFile = configDirectory.resolve("buildscape/ui-state.cfg");
            try {
                if (Files.exists(uiFile)) {
                    uiSource = ConfigLayoutIO.readText(uiFile);
                    uiValues.putAll(ConfigLayoutIO.readEntries(uiSource));
                }
                for (String key : UI_STATE) {
                    uiValues.putIfAbsent(key, values.get(key));
                    values.put(key, uiValues.get(key));
                }
            } catch (IOException e) {
                uiReadOnly = true;
                BuildscapeCommon.LOGGER.warn("BuildscapeClientConfig: UI state is read-only because it could not be parsed", e);
            }
        }
        if (!file.exists()) save();
    }

    public void save() {
        if (readOnly) return;
        try {
            boolean moveState = client && saveUiState();
            StringBuilder text = new StringBuilder("# BuildScape Configuration\n# Changes take effect on the next game launch.\n");
            for (String section : new String[]{"Gameplay", "Performance", "Visuals & particles", "Interface", "Other"}) {
                text.append("\n# --- ").append(section).append(" ---\n");
                for (Map.Entry<String, String> entry : values.entrySet()) {
                    String key = entry.getKey();
                    if (moveState && UI_STATE.contains(key)) continue;
                    if (!section.equals(section(key))) continue;
                    text.append("# ").append(description(key)).append('\n');
                    text.append(key).append(" = ").append(entry.getValue()).append('\n');
                }
            }
            ConfigLayoutIO.write(getConfigFile().toPath(), text.toString(), source, true);
            source = text.toString();
        } catch (IOException e) {
            BuildscapeCommon.LOGGER.warn("BuildscapeClientConfig: Failed to write config", e);
        }
    }

    private boolean saveUiState() {
        if (!client || readOnly || uiReadOnly) return false;
        try {
            for (String key : UI_STATE) uiValues.put(key, values.get(key));
            StringBuilder text = new StringBuilder("# BuildScape screen state\n");
            for (Map.Entry<String, String> entry : uiValues.entrySet()) {
                text.append(entry.getKey()).append(" = ").append(entry.getValue()).append('\n');
            }
            ConfigLayoutIO.write(configDirectory.resolve("buildscape/ui-state.cfg"), text.toString(), uiSource, false);
            uiSource = text.toString();
            return true;
        } catch (IOException e) {
            BuildscapeCommon.LOGGER.warn("BuildscapeClientConfig: Failed to save UI state; retaining the old location", e);
            return false;
        }
    }

    private void saveState() {
        if (!client || readOnly) return;
        // One layout migration may be needed; subsequent clicks only touch ui-state.cfg.
        if (source == null || hasOldState()) save();
        else saveUiState();
    }

    private boolean hasOldState() {
        try {
            Map<String, String> old = ConfigLayoutIO.readEntries(source);
            return old.keySet().stream().anyMatch(UI_STATE::contains);
        } catch (IOException e) { return false; }
    }

    private static String section(String key) {
        return switch (key) {
            case KEY_CAKE_STACKING, KEY_WATER_BOTTLE_STACKING, KEY_MAX_PIPE_NETWORK_SIZE -> "Gameplay";
            case KEY_PARALLEL_MODEL_LOADING, KEY_CACHE_MODEL_MATERIALS,
                    KEY_PARALLEL_MODEL_BAKING, KEY_PARALLEL_BLOCK_STATE_CACHE -> "Performance";
            case KEY_HIDE_CONFIG_BUTTON, KEY_UI_THEME, KEY_UI_BACKGROUND, KEY_ANIMATION_SPEED,
                    KEY_LAST_ACTIVE_TAB, KEY_SIDE_PANEL_EXPANDED -> "Interface";
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
            case KEY_UI_THEME -> "Color theme for the configuration screen.";
            case KEY_UI_BACKGROUND -> "Background for the configuration screen.";
            case KEY_ANIMATION_SPEED -> "Screen animation speed: 0 smooth, 1 snappy, 2 instant.";
            case KEY_LAST_ACTIVE_TAB -> "Last selected configuration tab (client screen state).";
            case KEY_SIDE_PANEL_EXPANDED -> "Whether the presets side panel is expanded (client screen state).";
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
            // The reference treats malformed numeric settings as their defaults.
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
        saveState();
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
        saveState();
    }
}
