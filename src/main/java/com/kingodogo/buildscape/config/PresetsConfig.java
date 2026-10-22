package com.kingodogo.buildscape.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.core.io.AtomicFiles;
import com.kingodogo.buildscape.core.io.Json;
import net.minecraftforge.fml.ModList;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Named item presets for the pillar item list, saved in {@code config/buildscape/pillar/pillar-presets.json}.
 * It always has a Default preset, a number of custom slots from the client config, an automatic "unnamed" preset for
 * unsaved edits and the key of the last applied preset. Entries it cannot understand are kept and written back.
 */
public class PresetsConfig {
    private static final Gson GSON = Json.PRETTY_WITH_NULLS;
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
        String configPath = (configDirectory != null ? configDirectory : Paths.get("config", "buildscape", "pillar"))
                .toString();
        File dir = new File(configPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private Map<String, Preset> presets = new HashMap<>();
    private final Map<String, JsonElement> preservedPresets = new HashMap<>();
    private boolean fileUnreadable;
    private String lastEditFailure = "";
    private String lastAppliedPreset = DEFAULT_PRESET_KEY;
    private static final String UNNAMED_PRESET_KEY = "_unnamed";
    private static final String DEFAULT_PRESET_KEY = "default";
    private static final String LAST_APPLIED_KEY = "_lastApplied";
    /** Custom preset slots besides Default; null reads the buildscape.cfg setting. */
    private transient Integer customSlotsOverride;

    public void load() {
        ModList mods = ModList.get();
        load(mods != null && mods.isLoaded("the_vault"));
    }

    void load(boolean vaultLoaded) {
        Set<String> defaultItems = ConfigPaths.loadDefaultItems(vaultLoaded);
        File file = getPresetsFile();
        if (!file.exists()) {
            if (ConfigPaths.copyTemplateIfMissing(file.toPath(),
                    "/data/buildscape/config/default_pillar_presets.json")) {
                // Template copied from resources, proceed to parse from disk
            } else {
                initializeDefaults(defaultItems);
                fileUnreadable = false;
                save();
                return;
            }
        }

        try {
            String source = ConfigLayoutIO.readText(file.toPath());
            JsonObject loaded = GSON.fromJson(source, JsonObject.class);
            boolean[] skippedEntries = {false};
            Map<String, Preset> loadedPresets = parsePresets(loaded, skippedEntries);
            Map<String, JsonElement> preserved = new HashMap<>();
            for (Map.Entry<String, JsonElement> entry : loaded.entrySet()) {
                if (entry.getKey().equals(LAST_APPLIED_KEY) || entry.getKey().equals(UNNAMED_PRESET_KEY)) {
                    continue;
                }
                Preset preset = loadedPresets.get(entry.getKey());
                boolean partial = preset == null;
                if (preset != null) {
                    for (JsonElement item : entry.getValue().getAsJsonObject().getAsJsonArray("items")) {
                        if (!item.isJsonPrimitive() || !item.getAsJsonPrimitive().isString()) {
                            partial = true;
                        }
                    }
                }
                if (partial) {
                    preserved.put(entry.getKey(), entry.getValue().deepCopy());
                }
            }
            if (skippedEntries[0]) {
                PillarParticleConfig.backupUnreadableFile(file);
            }
            String loadedLastApplied = DEFAULT_PRESET_KEY;
            if (loaded.has(LAST_APPLIED_KEY) && loaded.get(LAST_APPLIED_KEY).isJsonPrimitive()
                    && loaded.get(LAST_APPLIED_KEY).getAsJsonPrimitive().isString()) {
                loadedLastApplied = loaded.get(LAST_APPLIED_KEY).getAsString();
            }
            presets = loadedPresets;
            preservedPresets.clear();
            preservedPresets.putAll(preserved);
            lastAppliedPreset = loadedLastApplied;
            fileUnreadable = ConfigLayoutIO.protectInvalidEncoding(file.toPath())
                    || !ConfigLayoutIO.isWritable(file.toPath());
        } catch (Exception e) {
            // the presets file may be malformed in any way; keep the last good presets
            fileUnreadable = true;
            PillarParticleConfig.backupUnreadableFile(file);
            BuildScape.LOGGER.error("Failed to load presets {}; keeping the last good presets", file, e);
            if (presets.isEmpty()) {
                initializeDefaults(defaultItems);
            }
            rebuildDefaultPreset(defaultItems);
            return;
        }

        rebuildDefaultPreset(defaultItems);

        if (lastAppliedPreset == null || lastAppliedPreset.isEmpty()) {
            lastAppliedPreset = DEFAULT_PRESET_KEY;
        }
    }

    /** Number of custom presets that may exist besides Default (MaxCustomPresets in buildscape.cfg, default 4). */
    public int getCustomSlots() {
        if (customSlotsOverride != null) {
            return customSlotsOverride;
        }
        try {
            return BuildscapeClientConfig.get().getMaxCustomPresets();
        } catch (Throwable e) {
            // the client config can be unavailable, for example in tests, including as a linkage error
            return BuildscapeClientConfig.DEFAULT_CUSTOM_PRESETS;
        }
    }

    void setCustomSlots(int slots) {
        customSlotsOverride = BuildscapeClientConfig.clampCustomPresets(slots);
    }

    public int getCustomPresetCount() {
        int count = 0;
        for (String k : getPresetKeys()) {
            if (!k.equals(DEFAULT_PRESET_KEY) && !k.equals(UNNAMED_PRESET_KEY)) {
                count++;
            }
        }
        return count;
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

    static Map<String, Preset> parsePresets(JsonObject loaded) {
        return parsePresets(loaded, new boolean[]{false});
    }

    private static Map<String, Preset> parsePresets(JsonObject loaded, boolean[] skippedEntries) {
        if (loaded == null) {
            throw new IllegalArgumentException("Expected a presets object");
        }
        Map<String, Preset> parsed = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : loaded.entrySet()) {
            if (!entry.getKey().equals(LAST_APPLIED_KEY) && !entry.getKey().equals(UNNAMED_PRESET_KEY)) {
                try {
                    JsonElement value = entry.getValue();
                    if (!value.isJsonObject() || !value.getAsJsonObject().has("items")
                            || !value.getAsJsonObject().get("items").isJsonArray()) {
                        throw new IllegalArgumentException("Expected items for preset " + entry.getKey());
                    }
                    Set<String> items = PillarParticleConfig.parseItems(value.toString(),
                            PRESETS_FILE_NAME + " preset " + entry.getKey());
                    for (JsonElement item : value.getAsJsonObject().getAsJsonArray("items")) {
                        if (!item.isJsonPrimitive() || !item.getAsJsonPrimitive().isString()) {
                            skippedEntries[0] = true;
                        }
                    }
                    String name = value.getAsJsonObject().has("name")
                            ? GSON.fromJson(value.getAsJsonObject().get("name"), String.class)
                            : "";
                    if (name == null) {
                        throw new IllegalArgumentException("Expected a name for preset " + entry.getKey());
                    }
                    parsed.put(entry.getKey(), new Preset(name, items));
                } catch (RuntimeException e) {
                    skippedEntries[0] = true;
                    BuildScape.LOGGER.warn("Skipped malformed preset {} in {}", entry.getKey(), PRESETS_FILE_NAME,
                            e);
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
            BuildScape.LOGGER.error("Not overwriting unreadable presets {}", file);
            return false;
        }
        try {
            if (file.exists() && ConfigLayoutIO.protectInvalidEncoding(file.toPath())) {
                fileUnreadable = true;
                lastEditFailure = "unreadable";
                BuildScape.LOGGER.warn("Not overwriting replacement-decoded presets {}", file);
                return false;
            }
            AtomicFiles.write(file.toPath(), PRESETS_FILE_NAME, temporary -> {
                try (FileWriter writer = new FileWriter(temporary.toFile(), StandardCharsets.UTF_8)) {
                    Map<String, Object> toSave = new HashMap<>();
                    for (Map.Entry<String, Preset> entry : presets.entrySet()) {
                        if (!entry.getKey().equals(UNNAMED_PRESET_KEY)) {
                            toSave.put(entry.getKey(), entry.getValue());
                        }
                    }
                    toSave.put(LAST_APPLIED_KEY, lastAppliedPreset);
                    toSave.putAll(preservedPresets);
                    GSON.toJson(toSave, writer);
                }
            }, e -> BuildScape.LOGGER.warn("Failed to remove temporary presets file", e));
            lastEditFailure = "";
            return true;
        } catch (Exception e) {
            // any save failure is logged and reported to the caller as false
            BuildScape.LOGGER.error("Failed to save presets {}", file, e);
            return false;
        }
    }

    /**
     * Applies the Default preset when the pillar item list is empty.
     */
    public void applyPresetOnStartup() {
        PillarParticleConfig itemConfig = PillarParticleConfig.get();
        if (itemConfig.items.isEmpty()) {
            applyPreset(DEFAULT_PRESET_KEY);
        }
    }

    private void initializeDefaults(Set<String> defaultItems) {
        presets.clear();
        preservedPresets.clear();

        Preset defaultPreset = new Preset("Default", defaultItems);
        presets.put(DEFAULT_PRESET_KEY, defaultPreset);
    }

    private void rebuildDefaultPreset(Set<String> defaultItems) {
        Preset previous = presets.get(DEFAULT_PRESET_KEY);
        presets.put(DEFAULT_PRESET_KEY, new Preset(previous == null ? "Default" : previous.name, defaultItems));
        preservedPresets.remove(DEFAULT_PRESET_KEY);
    }

    public List<Preset> getPresets() {
        List<Preset> presetList = new ArrayList<>();
        for (String key : getPresetKeys()) {
            presetList.add(getPreset(key));
        }
        return presetList;
    }

    public List<String> getPresetKeys() {
        List<String> keys = new ArrayList<>();
        if (presets.containsKey(DEFAULT_PRESET_KEY)) {
            keys.add(DEFAULT_PRESET_KEY);
        }
        if (presets.containsKey(UNNAMED_PRESET_KEY)) {
            keys.add(UNNAMED_PRESET_KEY);
        }
        Set<String> allKeys = new HashSet<>(presets.keySet());
        allKeys.addAll(preservedPresets.keySet());
        List<String> sortedKeys = new ArrayList<>(allKeys);
        sortedKeys.remove(DEFAULT_PRESET_KEY);
        sortedKeys.remove(UNNAMED_PRESET_KEY);
        sortedKeys.sort(String::compareTo);

        keys.addAll(sortedKeys);
        return keys;
    }

    public boolean savePreset(String key, String name, Set<String> items) {
        if (key.equals(DEFAULT_PRESET_KEY) || key.equals(UNNAMED_PRESET_KEY)) {
            lastEditFailure = "reserved";
            return false;
        }
        if (fileUnreadable) {
            lastEditFailure = "unreadable";
            return false;
        }

        if (!presets.containsKey(key) && !preservedPresets.containsKey(key)
                && getCustomPresetCount() >= getCustomSlots()) {
            lastEditFailure = "limit";
            return false;
        }

        Preset previous = presets.put(key, new Preset(name, items));
        JsonElement preserved = preservedPresets.remove(key);
        if (saveToDisk()) {
            return true;
        }
        if (previous != null) {
            presets.put(key, previous);
        } else {
            presets.remove(key);
        }
        if (preserved != null) {
            preservedPresets.put(key, preserved);
        }
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
        if (key.equals(DEFAULT_PRESET_KEY) || key.equals(UNNAMED_PRESET_KEY)) {
            lastEditFailure = "reserved";
            return false;
        }
        if (fileUnreadable) {
            lastEditFailure = "unreadable";
            return false;
        }
        Preset previous = presets.remove(key);
        JsonElement preserved = preservedPresets.remove(key);
        if (previous != null || preserved != null) {
            String previousLastApplied = lastAppliedPreset;
            if (key.equals(lastAppliedPreset)) {
                lastAppliedPreset = DEFAULT_PRESET_KEY;
            }
            if (saveToDisk()) {
                return true;
            }
            lastAppliedPreset = previousLastApplied;
            if (previous != null) {
                presets.put(key, previous);
            }
            if (preserved != null) {
                preservedPresets.put(key, preserved);
            }
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
                BuildScape.LOGGER.warn("Not applying a preset while its config files are read-only");
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

    /**
     * Applies the unnamed preset if one exists, otherwise the last applied preset, otherwise Default.
     */
    public void autoApplyOnLoad() {
        if (hasUnnamedPreset()) {
            applyPreset(UNNAMED_PRESET_KEY);
        } else if (lastAppliedPreset != null && presets.containsKey(lastAppliedPreset)) {
            applyPreset(lastAppliedPreset);
        } else {
            applyPreset(DEFAULT_PRESET_KEY);
            lastAppliedPreset = DEFAULT_PRESET_KEY;
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
