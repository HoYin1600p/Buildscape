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
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
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
    private final Map<String, com.google.gson.JsonElement> preservedPresets = new HashMap<>();
    private boolean fileUnreadable;
    private String lastEditFailure = "";
    private String lastAppliedPreset = "default";
    private static final String UNNAMED_PRESET_KEY = "_unnamed";
    public static final int MAX_PRESETS = 5;

    public void load() {
        File file = getPresetsFile();
        if (!file.exists()) {
            if (ConfigPaths.copyTemplateIfMissing(file.toPath(), "/data/buildscape/config/default_pillar_presets.json")) {
                // Template copied from resources, proceed to parse from disk
            } else {
                initializeDefaults();
                fileUnreadable = false;
                save();
                return;
            }
        }

        try {
            String source = ConfigLayoutIO.readText(file.toPath());
            com.google.gson.JsonObject loaded = GSON.fromJson(source, com.google.gson.JsonObject.class);
            boolean[] skippedEntries = {false};
            Map<String, Preset> loadedPresets = parsePresets(loaded, skippedEntries);
            Map<String, com.google.gson.JsonElement> preserved = new HashMap<>();
            for (Map.Entry<String, com.google.gson.JsonElement> entry : loaded.entrySet()) {
                if (entry.getKey().equals("_lastApplied") || entry.getKey().equals(UNNAMED_PRESET_KEY)) continue;
                Preset preset = loadedPresets.get(entry.getKey());
                boolean partial = preset == null;
                if (preset != null) {
                    for (com.google.gson.JsonElement item : entry.getValue().getAsJsonObject().getAsJsonArray("items")) {
                        if (!item.isJsonPrimitive() || !item.getAsJsonPrimitive().isString()) partial = true;
                    }
                }
                if (partial) preserved.put(entry.getKey(), entry.getValue().deepCopy());
            }
            if (skippedEntries[0]) {
                PillarParticleConfig.backupUnreadableFile(file);
            }
            String loadedLastApplied = "default";
            if (loaded.has("_lastApplied") && loaded.get("_lastApplied").isJsonPrimitive()
                    && loaded.get("_lastApplied").getAsJsonPrimitive().isString()) {
                loadedLastApplied = loaded.get("_lastApplied").getAsString();
            }
            presets = loadedPresets;
            preservedPresets.clear();
            preservedPresets.putAll(preserved);
            lastAppliedPreset = loadedLastApplied;
            fileUnreadable = ConfigLayoutIO.protectInvalidEncoding(file.toPath())
                    || !ConfigLayoutIO.isWritable(file.toPath());
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
        saveToDisk();
    }

    private boolean saveToDisk() {
        lastEditFailure = "save";
        File file = getPresetsFile();
        if (fileUnreadable) {
            lastEditFailure = "unreadable";
            com.kingodogo.buildscape.BuildScape.getLogger().error("Not overwriting unreadable presets " + file);
            return false;
        }
        Path temporary = null;
        try {
            if (file.exists() && ConfigLayoutIO.protectInvalidEncoding(file.toPath())) {
                fileUnreadable = true;
                lastEditFailure = "unreadable";
                com.kingodogo.buildscape.BuildScape.getLogger().warn("Not overwriting replacement-decoded presets " + file);
                return false;
            }
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            temporary = Files.createTempFile(parentDir.toPath(), PRESETS_FILE_NAME, ".tmp");
            try (FileWriter writer = new FileWriter(temporary.toFile(), java.nio.charset.StandardCharsets.UTF_8)) {
                Map<String, Object> toSave = new HashMap<>();
                for (Map.Entry<String, Preset> entry : presets.entrySet()) {
                    if (!entry.getKey().equals(UNNAMED_PRESET_KEY)) {
                        toSave.put(entry.getKey(), entry.getValue());
                    }
                }
                toSave.put("_lastApplied", lastAppliedPreset);
                toSave.putAll(preservedPresets);
                GSON.toJson(toSave, writer);
            }
            try {
                Files.move(temporary, file.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporary, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            lastEditFailure = "";
            return true;
        } catch (Exception e) {
            com.kingodogo.buildscape.BuildScape.getLogger().error("Failed to save presets " + file, e);
            return false;
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
        preservedPresets.clear();

        Preset defaultPreset = new Preset("Default", getDefaultItems());
        presets.put("default", defaultPreset);
    }

    private Set<String> getDefaultItems() {
        return ConfigPaths.loadDefaultItems(net.minecraftforge.fml.ModList.get().isLoaded("the_vault"));
    }

    public List<Preset> getPresets() {
        List<Preset> presetList = new ArrayList<>();
        for (String key : getPresetKeys()) presetList.add(getPreset(key));
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
        Set<String> allKeys = new HashSet<>(presets.keySet());
        allKeys.addAll(preservedPresets.keySet());
        List<String> sortedKeys = new ArrayList<>(allKeys);
        sortedKeys.remove("default");
        sortedKeys.remove(UNNAMED_PRESET_KEY);
        sortedKeys.sort(String::compareTo);

        keys.addAll(sortedKeys);
        return keys;
    }

    public boolean savePreset(String key, String name, Set<String> items) {
        if (key.equals("default") || key.equals(UNNAMED_PRESET_KEY)) {
            lastEditFailure = "reserved";
            return false;
        }
        if (fileUnreadable) {
            lastEditFailure = "unreadable";
            return false;
        }

        int customPresetCount = 0;
        for (String k : getPresetKeys()) {
            if (!k.equals("default") && !k.equals(UNNAMED_PRESET_KEY)) {
                customPresetCount++;
            }
        }

        if (!presets.containsKey(key) && !preservedPresets.containsKey(key) && customPresetCount >= MAX_PRESETS - 1) {
            lastEditFailure = "limit";
            return false;
        }

        Preset previous = presets.put(key, new Preset(name, items));
        com.google.gson.JsonElement preserved = preservedPresets.remove(key);
        if (saveToDisk()) return true;
        if (previous != null) presets.put(key, previous);
        else presets.remove(key);
        if (preserved != null) preservedPresets.put(key, preserved);
        return false;
    }

    public Preset getPreset(String key) {
        Preset preset = presets.get(key);
        return preset == null && preservedPresets.containsKey(key) ? new Preset(key, Set.of()) : preset;
    }

    public String getLastEditFailure() {
        return lastEditFailure;
    }

    public boolean canEdit() {
        return !fileUnreadable;
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
            lastEditFailure = "reserved";
            return false;
        }
        if (fileUnreadable) {
            lastEditFailure = "unreadable";
            return false;
        }
        Preset previous = presets.remove(key);
        com.google.gson.JsonElement preserved = preservedPresets.remove(key);
        if (previous != null || preserved != null) {
            String previousLastApplied = lastAppliedPreset;
            if (key.equals(lastAppliedPreset)) lastAppliedPreset = "default";
            if (saveToDisk()) return true;
            lastAppliedPreset = previousLastApplied;
            if (previous != null) presets.put(key, previous);
            if (preserved != null) preservedPresets.put(key, preserved);
            return false;
        }
        lastEditFailure = "missing";
        return false;
    }

    public void applyPreset(String key) {
        Preset preset = presets.get(key);
        if (preset != null) {
            PillarParticleConfig config = PillarParticleConfig.get();
            if (fileUnreadable || !config.canEditItems()) {
                lastEditFailure = "unreadable";
                com.kingodogo.buildscape.BuildScape.getLogger().warn("Not applying a preset while its config files are read-only");
                return;
            }
            config.editItems(preset.items);
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
        while (presets.containsKey("preset_" + index) || preservedPresets.containsKey("preset_" + index)) {
            index++;
        }
        return "preset_" + index;
    }
}
