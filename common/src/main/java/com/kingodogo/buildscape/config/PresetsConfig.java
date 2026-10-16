package com.kingodogo.buildscape.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.platform.Services;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PresetsConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String PRESETS_FILE_NAME = "pillar-presets.json";
    private static final Map<String, Boolean> MOD_LOADED_CACHE = new java.util.concurrent.ConcurrentHashMap<>();
    private static volatile PresetsConfig INSTANCE;
    private boolean corrupted;

    private File getConfigDir() {
        String configPath = ConfigPaths.dir("buildscape", "pillar").toString();
        File dir = new File(configPath);
        if (!dir.exists())
            dir.mkdirs();
        return dir;
    }

    private Map<String, Preset> presets = new HashMap<>();
    private String lastAppliedPreset = "default";
    private static final String UNNAMED_PRESET_KEY = "_unnamed";
    public static final int MAX_PRESETS = 5;

    private List<Preset> cachedPresetList = null;
    private List<String> cachedKeyList = null;

    private synchronized void invalidateCache() {
        cachedPresetList = null;
        cachedKeyList = null;
    }

    public synchronized void load() {
        invalidateCache();
        File file = getPresetsFile();
        if (!file.exists()) {
            initializeDefaults();
            save();
            return;
        }

        try (FileReader reader = new FileReader(file)) {
            com.google.gson.JsonElement root = com.google.gson.JsonParser.parseReader(reader);
            if (root == null || !root.isJsonObject()) throw new IllegalArgumentException("Expected presets object");
            if (root != null && root.isJsonObject()) {
                com.google.gson.JsonObject obj = root.getAsJsonObject();
                presets = new HashMap<>();
                for (Map.Entry<String, com.google.gson.JsonElement> entry : obj.entrySet()) {
                    if ("_lastApplied".equals(entry.getKey())) {
                        if (entry.getValue() != null && entry.getValue().isJsonPrimitive()) {
                            lastAppliedPreset = entry.getValue().getAsString();
                        }
                    } else if (!UNNAMED_PRESET_KEY.equals(entry.getKey())) {
                        try {
                            Preset preset = GSON.fromJson(entry.getValue(), Preset.class);
                            if (preset != null) {
                                presets.put(entry.getKey(), preset);
                            }
                        } catch (Exception e) {
                            corrupted = true;
                            BuildscapeCommon.LOGGER.warn("Skipping malformed preset entry: " + entry.getKey(), e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            BuildscapeCommon.LOGGER.error("Failed to load presets", e);
            corrupted = true;
            initializeDefaults();
        }

        if (!presets.containsKey("default")) {
            initializeDefaults();
        } else {
            Preset defaultPreset = presets.get("default");
            if (defaultPreset.items.addAll(getDefaultItems())) {
                save();
            }
        }

        if (lastAppliedPreset == null || lastAppliedPreset.isEmpty()) {
            lastAppliedPreset = "default";
        }
        invalidateCache();
    }

    private File getPresetsFile() {
        return new File(getConfigDir(), PRESETS_FILE_NAME);
    }

    public static PresetsConfig get() {
        PresetsConfig config = INSTANCE;
        if (config == null) {
            synchronized (PresetsConfig.class) {
                config = INSTANCE;
                if (config == null) {
                    config = new PresetsConfig();
                    config.load();
                    INSTANCE = config;
                }
            }
        }
        return config;
    }

    public void save() {
        File file = getPresetsFile();
        try {
            if (corrupted) {
                ConfigBackup.copyCorrupted(file.toPath());
                corrupted = false;
            }
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (FileWriter writer = new FileWriter(file)) {
                Map<String, Object> toSave = new HashMap<>();
                for (Map.Entry<String, Preset> entry : presets.entrySet()) {
                    if (!entry.getKey().equals(UNNAMED_PRESET_KEY)) {
                        toSave.put(entry.getKey(), entry.getValue());
                    }
                }
                toSave.put("_lastApplied", lastAppliedPreset);
                GSON.toJson(toSave, writer);
            }
        } catch (Exception e) {
            BuildscapeCommon.LOGGER.error("Failed to save presets", e);
        }
    }

    public void applyPresetOnStartup() {
        PillarParticleConfig itemConfig = PillarParticleConfig.get();
        if (itemConfig.items.isEmpty()) {
            applyPreset("default");
        }
    }

    private void initializeDefaults() {
        presets.clear();

        Preset defaultPreset = new Preset("Default", getDefaultItems());
        presets.put("default", defaultPreset);
    }

    private boolean isModLoaded(String itemString) {
        if (itemString == null || itemString.isEmpty())
            return false;
        String modId;
        if (itemString.startsWith("#")) {
            modId = itemString.substring(1).split(":")[0];
        } else {
            modId = itemString.split(":")[0];
        }
        if ("minecraft".equals(modId) || "buildscape".equals(modId)) return true;
        return MOD_LOADED_CACHE.computeIfAbsent(modId, Services.PLATFORM::isModLoaded);
    }

    private Set<String> getDefaultItems() {
        return getDefaultItems(Services.PLATFORM.isModLoaded("the_vault"));
    }

    static Set<String> getDefaultItems(boolean vaultLoaded) {
        return DefaultPillarItems.items(vaultLoaded);
    }

    private synchronized void rebuildCache() {
        List<Preset> presetList = new ArrayList<>();
        List<String> keyList = new ArrayList<>();

        if (presets.containsKey("default")) {
            keyList.add("default");
            presetList.add(presets.get("default"));
        }
        if (presets.containsKey(UNNAMED_PRESET_KEY)) {
            keyList.add(UNNAMED_PRESET_KEY);
            presetList.add(presets.get(UNNAMED_PRESET_KEY));
        }

        List<String> sortedKeys = new ArrayList<>(presets.keySet());
        sortedKeys.remove("default");
        sortedKeys.remove(UNNAMED_PRESET_KEY);
        sortedKeys.sort(String::compareTo);

        int customCount = 0;
        for (String key : sortedKeys) {
            if (customCount < MAX_PRESETS - 1) {
                keyList.add(key);
                presetList.add(presets.get(key));
                customCount++;
            }
        }

        this.cachedPresetList = Collections.unmodifiableList(presetList);
        this.cachedKeyList = Collections.unmodifiableList(keyList);
    }

    public synchronized List<Preset> getPresets() {
        if (cachedPresetList == null) {
            rebuildCache();
        }
        return cachedPresetList;
    }

    public synchronized List<String> getPresetKeys() {
        if (cachedKeyList == null) {
            rebuildCache();
        }
        return cachedKeyList;
    }

    public synchronized boolean savePreset(String key, String name, Set<String> items) {
        if (key.equals("default") || key.equals(UNNAMED_PRESET_KEY)) {
            return false;
        }

        int customPresetCount = 0;
        for (String k : presets.keySet()) {
            if (!k.equals("default") && !k.equals(UNNAMED_PRESET_KEY)) {
                customPresetCount++;
            }
        }

        if (!presets.containsKey(key) && customPresetCount >= MAX_PRESETS - 1) {
            return false;
        }

        presets.put(key, new Preset(name, items));
        invalidateCache();
        save();
        return true;
    }

    public Preset getPreset(String key) {
        return presets.get(key);
    }

    public static class Preset {
        public String name;
        public Set<String> items;

        public Preset() {
            this.name = "";
            this.items = new HashSet<>();
        }

        public Preset(String name, Set<String> items) {
            this.name = name;
            this.items = new HashSet<>(items);
        }
    }

    public synchronized boolean deletePreset(String key) {
        if (key.equals("default") || key.equals(UNNAMED_PRESET_KEY)) {
            return false;
        }
        if (presets.remove(key) != null) {
            invalidateCache();
            save();
            return true;
        }
        return false;
    }

    public void applyPreset(String key) {
        Preset preset = presets.get(key);
        if (preset != null) {
            PillarParticleConfig config = PillarParticleConfig.get();
            config.items.clear();
            config.items.addAll(preset.items);
            config.saveItemsToDisk();
            if (!key.equals(UNNAMED_PRESET_KEY)) {
                lastAppliedPreset = key;
                save();
            }
        }
    }

    public String getLastAppliedPreset() {
        return lastAppliedPreset;
    }

    public synchronized void saveUnnamedPreset(Set<String> items) {
        presets.put(UNNAMED_PRESET_KEY, new Preset("", items));
        invalidateCache();
    }

    public Preset getUnnamedPreset() {
        return presets.get(UNNAMED_PRESET_KEY);
    }

    public boolean hasUnnamedPreset() {
        return presets.containsKey(UNNAMED_PRESET_KEY);
    }

    public synchronized void clearUnnamedPreset() {
        presets.remove(UNNAMED_PRESET_KEY);
        invalidateCache();
    }

    public void autoApplyOnLoad() {
        if (hasUnnamedPreset()) {
            applyPreset(UNNAMED_PRESET_KEY);
        } else if (lastAppliedPreset != null && presets.containsKey(lastAppliedPreset)) {
            applyPreset(lastAppliedPreset);
        } else {
            applyPreset("default");
            lastAppliedPreset = "default";
            save();
        }
    }

    public String generatePresetKey() {
        int index = 1;
        while (presets.containsKey("preset_" + index)) {
            index++;
        }
        return "preset_" + index;
    }
}
