package com.kingodogo.buildscape.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PresetsConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String PRESETS_FILE_NAME = "pillar-presets.json";
    private static PresetsConfig INSTANCE;
    private final transient Path configDirectory;

    public PresetsConfig() {
        this(null);
    }

    PresetsConfig(Path configDirectory) {
        this.configDirectory = configDirectory;
    }

    private File getConfigDir() {
        String configPath = (configDirectory != null ? configDirectory : Paths.get("config", "buildscape", "pillar")).toString();
        File dir = new File(configPath);
        if (!dir.exists())
            dir.mkdirs();
        return dir;
    }

    private Map<String, Preset> presets = new HashMap<>();
    private boolean fileUnreadable;
    private String lastAppliedPreset = "default";
    private static final String UNNAMED_PRESET_KEY = "_unnamed";
    public static final int MAX_PRESETS = 5;

    public void load() {
        File file = getPresetsFile();
        if (!file.exists()) {
            initializeDefaults();
            fileUnreadable = false;
            save();
            return;
        }

        try {
            String source = ConfigLayoutIO.readText(file.toPath());
            com.google.gson.JsonObject loaded = GSON.fromJson(source, com.google.gson.JsonObject.class);
            boolean[] skippedEntries = {false};
            Map<String, Preset> loadedPresets = parsePresets(loaded, skippedEntries);
            if (skippedEntries[0]) {
                PillarParticleConfig.backupUnreadableFile(file);
            }
            String loadedLastApplied = "default";
            if (loaded.has("_lastApplied") && loaded.get("_lastApplied").isJsonPrimitive()
                    && loaded.get("_lastApplied").getAsJsonPrimitive().isString()) {
                loadedLastApplied = loaded.get("_lastApplied").getAsString();
            }
            presets = loadedPresets;
            lastAppliedPreset = loadedLastApplied;
            fileUnreadable = ConfigLayoutIO.hasReplacementCharacters(source);
        } catch (Exception e) {
            fileUnreadable = true;
            PillarParticleConfig.backupUnreadableFile(file);
            com.kingodogo.buildscape.BuildScape.getLogger().error(
                    "Failed to load presets " + file + "; keeping the last good presets", e);
            if (presets.isEmpty()) {
                initializeDefaults();
            }
            return;
        }

        if (!presets.containsKey("default")) {
            presets.put("default", new Preset("Default", getDefaultItems()));
        }

        if (lastAppliedPreset == null || lastAppliedPreset.isEmpty()) {
            lastAppliedPreset = "default";
        }
    }

    private File getPresetsFile() {
        return new File(getConfigDir(), PRESETS_FILE_NAME);
    }

    public static PresetsConfig get() {
        if (INSTANCE == null) {
            INSTANCE = new PresetsConfig();
            INSTANCE.load();
        }
        return INSTANCE;
    }

    static Map<String, Preset> parsePresets(com.google.gson.JsonObject loaded) {
        return parsePresets(loaded, new boolean[]{false});
    }

    private static Map<String, Preset> parsePresets(com.google.gson.JsonObject loaded, boolean[] skippedEntries) {
        if (loaded == null) {
            throw new IllegalArgumentException("Expected a presets object");
        }
        Map<String, Preset> parsed = new HashMap<>();
        for (Map.Entry<String, com.google.gson.JsonElement> entry : loaded.entrySet()) {
            if (!entry.getKey().equals("_lastApplied") && !entry.getKey().equals(UNNAMED_PRESET_KEY)) {
                try {
                    com.google.gson.JsonElement value = entry.getValue();
                    if (!value.isJsonObject() || !value.getAsJsonObject().has("items")
                            || !value.getAsJsonObject().get("items").isJsonArray()) {
                        throw new IllegalArgumentException("Expected items for preset " + entry.getKey());
                    }
                    Set<String> items = PillarParticleConfig.parseItems(value.toString(),
                            PRESETS_FILE_NAME + " preset " + entry.getKey());
                    for (com.google.gson.JsonElement item : value.getAsJsonObject().getAsJsonArray("items")) {
                        if (!item.isJsonPrimitive() || !item.getAsJsonPrimitive().isString()) {
                            skippedEntries[0] = true;
                        }
                    }
                    String name = value.getAsJsonObject().has("name")
                            ? GSON.fromJson(value.getAsJsonObject().get("name"), String.class) : "";
                    if (name == null) {
                        throw new IllegalArgumentException("Expected a name for preset " + entry.getKey());
                    }
                    parsed.put(entry.getKey(), new Preset(name, items));
                } catch (RuntimeException e) {
                    skippedEntries[0] = true;
                    com.kingodogo.buildscape.BuildScape.getLogger().warn(
                            "Skipped malformed preset " + entry.getKey() + " in " + PRESETS_FILE_NAME, e);
                }
            }
        }
        return parsed;
    }

    public void save() {
        File file = getPresetsFile();
        if (fileUnreadable) {
            com.kingodogo.buildscape.BuildScape.getLogger().error("Not overwriting unreadable presets " + file);
            return;
        }
        Path temporary = null;
        try {
            if (file.exists() && ConfigLayoutIO.hasReplacementCharacters(ConfigLayoutIO.readText(file.toPath()))) {
                fileUnreadable = true;
                com.kingodogo.buildscape.BuildScape.getLogger().warn("Not overwriting replacement-decoded presets " + file);
                return;
            }
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            temporary = Files.createTempFile(parentDir.toPath(), PRESETS_FILE_NAME, ".tmp");
            try (FileWriter writer = new FileWriter(temporary.toFile())) {
                Map<String, Object> toSave = new HashMap<>();
                for (Map.Entry<String, Preset> entry : presets.entrySet()) {
                    if (!entry.getKey().equals(UNNAMED_PRESET_KEY)) {
                        toSave.put(entry.getKey(), entry.getValue());
                    }
                }
                toSave.put("_lastApplied", lastAppliedPreset);
                GSON.toJson(toSave, writer);
            }
            try {
                Files.move(temporary, file.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporary, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            com.kingodogo.buildscape.BuildScape.getLogger().error("Failed to save presets " + file, e);
        } finally {
            if (temporary != null) {
                try {
                    Files.deleteIfExists(temporary);
                } catch (java.io.IOException e) {
                    com.kingodogo.buildscape.BuildScape.getLogger().warn("Failed to remove temporary presets file", e);
                }
            }
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

    private Set<String> getDefaultItems() {
        return DefaultPillarItems.items(net.minecraftforge.fml.ModList.get().isLoaded("the_vault"));
    }

    public List<Preset> getPresets() {
        List<Preset> presetList = new ArrayList<>();
        if (presets.containsKey("default")) {
            presetList.add(presets.get("default"));
        }
        if (presets.containsKey(UNNAMED_PRESET_KEY)) {
            presetList.add(presets.get(UNNAMED_PRESET_KEY));
        }
        List<String> sortedKeys = new ArrayList<>(presets.keySet());
        sortedKeys.remove("default");
        sortedKeys.remove(UNNAMED_PRESET_KEY);
        sortedKeys.sort(String::compareTo);

        int customCount = 0;
        for (String key : sortedKeys) {
            if (customCount < MAX_PRESETS - 1) {
                presetList.add(presets.get(key));
                customCount++;
            }
        }
        return presetList;
    }

    public List<String> getPresetKeys() {
        List<String> keys = new ArrayList<>();
        if (presets.containsKey("default")) {
            keys.add("default");
        }
        if (presets.containsKey(UNNAMED_PRESET_KEY)) {
            keys.add(UNNAMED_PRESET_KEY);
        }
        List<String> sortedKeys = new ArrayList<>(presets.keySet());
        sortedKeys.remove("default");
        sortedKeys.remove(UNNAMED_PRESET_KEY);
        sortedKeys.sort(String::compareTo);

        int customCount = 0;
        for (String key : sortedKeys) {
            if (customCount < MAX_PRESETS - 1) {
                keys.add(key);
                customCount++;
            }
        }
        return keys;
    }

    public boolean savePreset(String key, String name, Set<String> items) {
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

    public boolean deletePreset(String key) {
        if (key.equals("default") || key.equals(UNNAMED_PRESET_KEY)) {
            return false;
        }
        if (presets.remove(key) != null) {
            save();
            return true;
        }
        return false;
    }

    public void applyPreset(String key) {
        Preset preset = presets.get(key);
        if (preset != null) {
            PillarParticleConfig config = PillarParticleConfig.get();
            config.replaceItems(preset.items);
            config.saveItems();
            if (!key.equals(UNNAMED_PRESET_KEY)) {
                lastAppliedPreset = key;
                save();
            }
        }
    }

    public String getLastAppliedPreset() {
        return lastAppliedPreset;
    }

    public void saveUnnamedPreset(Set<String> items) {
        presets.put(UNNAMED_PRESET_KEY, new Preset("", items));
    }

    public Preset getUnnamedPreset() {
        return presets.get(UNNAMED_PRESET_KEY);
    }

    public boolean hasUnnamedPreset() {
        return presets.containsKey(UNNAMED_PRESET_KEY);
    }

    public void clearUnnamedPreset() {
        presets.remove(UNNAMED_PRESET_KEY);
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
