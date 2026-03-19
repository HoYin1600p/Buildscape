package com.kingodogo.buildscape.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.*;

public class VerticalPresetsConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String PRESETS_FILE_NAME = "vertical-presets.json";
    private static VerticalPresetsConfig INSTANCE;

    public static class VerticalPreset {
        public String name;
        public List<String> allowedFamilies;
        public List<String> blocklistedFamilies;
        public List<String> allowedMods;
        public List<String> blocklistedMods;

        public VerticalPreset() {
            this.name = "";
            this.allowedFamilies = new ArrayList<>();
            this.blocklistedFamilies = new ArrayList<>();
            this.allowedMods = new ArrayList<>();
            this.blocklistedMods = new ArrayList<>();
        }

        public VerticalPreset(String name, VerticalConfig config) {
            this.name = name;
            this.allowedFamilies = new ArrayList<>(config.getAllowedFamilies());
            this.blocklistedFamilies = new ArrayList<>(config.getBlocklistedFamilies());
            this.allowedMods = new ArrayList<>(config.getAllowedMods());
            this.blocklistedMods = new ArrayList<>(config.getBlocklistedMods());
        }
    }

    private Map<String, VerticalPreset> presets = new HashMap<>();
    private String lastAppliedPreset = "default";
    private static final String UNNAMED_PRESET_KEY = "_unnamed";

    public static VerticalPresetsConfig get() {
        if (INSTANCE == null) {
            INSTANCE = new VerticalPresetsConfig();
            INSTANCE.load();
        }
        return INSTANCE;
    }

    private File getFile() {
        File dir = FMLPaths.GAMEDIR.get().resolve("buildscape").resolve("data").toFile();
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, PRESETS_FILE_NAME);
    }

    public void load() {
        File file = getFile();
        if (!file.exists()) {
            initializeDefaults();
            save();
            return;
        }

        try (FileReader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> loaded = GSON.fromJson(reader, type);
            if (loaded != null) {
                presets = new HashMap<>();
                for (Map.Entry<String, Object> entry : loaded.entrySet()) {
                    if (entry.getKey().equals("_lastApplied")) {
                        lastAppliedPreset = (String) entry.getValue();
                    } else if (!entry.getKey().equals(UNNAMED_PRESET_KEY)) {
                        VerticalPreset preset = GSON.fromJson(GSON.toJson(entry.getValue()), VerticalPreset.class);
                        if (preset != null) presets.put(entry.getKey(), preset);
                    }
                }
            }
        } catch (Exception e) {
            initializeDefaults();
        }

        if (!presets.containsKey("default")) initializeDefaults();
    }

    public void save() {
        File file = getFile();
        try (FileWriter writer = new FileWriter(file)) {
            Map<String, Object> toSave = new HashMap<>();
            for (Map.Entry<String, VerticalPreset> entry : presets.entrySet()) {
                if (!entry.getKey().equals(UNNAMED_PRESET_KEY)) {
                    toSave.put(entry.getKey(), entry.getValue());
                }
            }
            toSave.put("_lastApplied", lastAppliedPreset);
            GSON.toJson(toSave, writer);
        } catch (Exception e) {}
    }

    private void initializeDefaults() {
        presets.clear();
        VerticalPreset defaultPreset = new VerticalPreset();
        defaultPreset.name = "Default";
        // Default could have some pre-configured families if needed
        presets.put("default", defaultPreset);
    }

    public List<VerticalPreset> getPresets() {
        List<VerticalPreset> list = new ArrayList<>();
        if (presets.containsKey("default")) list.add(presets.get("default"));
        if (presets.containsKey(UNNAMED_PRESET_KEY)) list.add(presets.get(UNNAMED_PRESET_KEY));
        
        List<String> sortedKeys = new ArrayList<>(presets.keySet());
        sortedKeys.remove("default");
        sortedKeys.remove(UNNAMED_PRESET_KEY);
        sortedKeys.sort(String::compareTo);
        
        for (String key : sortedKeys) list.add(presets.get(key));
        return list;
    }

    public List<String> getPresetKeys() {
        List<String> keys = new ArrayList<>();
        if (presets.containsKey("default")) keys.add("default");
        if (presets.containsKey(UNNAMED_PRESET_KEY)) keys.add(UNNAMED_PRESET_KEY);
        
        List<String> sortedKeys = new ArrayList<>(presets.keySet());
        sortedKeys.remove("default");
        sortedKeys.remove(UNNAMED_PRESET_KEY);
        sortedKeys.sort(String::compareTo);
        
        keys.addAll(sortedKeys);
        return keys;
    }

    public void applyPreset(String key) {
        VerticalPreset preset = presets.get(key);
        if (preset != null) {
            VerticalConfig config = VerticalConfig.get();
            config.getAllowedFamilies().clear();
            config.getAllowedFamilies().addAll(preset.allowedFamilies);
            config.getBlocklistedFamilies().clear();
            config.getBlocklistedFamilies().addAll(preset.blocklistedFamilies);
            config.getAllowedMods().clear();
            config.getAllowedMods().addAll(preset.allowedMods);
            config.getBlocklistedMods().clear();
            config.getBlocklistedMods().addAll(preset.blocklistedMods);
            config.save();
            
            if (!key.equals(UNNAMED_PRESET_KEY)) {
                lastAppliedPreset = key;
                save();
            }
        }
    }

    public void savePreset(String key, String name, VerticalConfig config) {
        presets.put(key, new VerticalPreset(name, config));
        save();
    }

    public void deletePreset(String key) {
        if (!key.equals("default") && !key.equals(UNNAMED_PRESET_KEY)) {
            presets.remove(key);
            save();
        }
    }

    public String generatePresetKey() {
        int i = 1;
        while (presets.containsKey("preset_" + i)) i++;
        return "preset_" + i;
    }

    public String getLastAppliedPreset() { return lastAppliedPreset; }
    public VerticalPreset getPreset(String key) { return presets.get(key); }
    public void saveUnnamedPreset(VerticalConfig config) { presets.put(UNNAMED_PRESET_KEY, new VerticalPreset("Unsaved", config)); }
    public boolean hasUnnamedPreset() { return presets.containsKey(UNNAMED_PRESET_KEY); }
    public void clearUnnamedPreset() { presets.remove(UNNAMED_PRESET_KEY); }
}
