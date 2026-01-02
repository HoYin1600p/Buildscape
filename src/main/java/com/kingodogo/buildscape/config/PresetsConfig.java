package com.kingodogo.buildscape.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.file.Paths;
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
    
    private Map<String, Preset> presets = new HashMap<>();
    private String lastAppliedPreset = "default";
    private static final String UNNAMED_PRESET_KEY = "_unnamed";
    public static final int MAX_PRESETS = 5;
    
    private File getConfigDir() {
        String configPath = Paths.get("config", "buildscape", "pillar").toString();
        File dir = new File(configPath);
        if (!dir.exists()) dir.mkdirs();
        return dir;
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
    
    public void load() {
        File file = getPresetsFile();
        if (!file.exists()) {
            initializeDefaults();
            save();
            return;
        }
        
        try (FileReader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> loaded = GSON.fromJson(reader, type);
            if (loaded != null) {
                presets = new HashMap<>();
                for (Map.Entry<String, Object> entry : loaded.entrySet()) {
                    if (entry.getKey().equals("_lastApplied")) {
                        if (entry.getValue() instanceof String) {
                            lastAppliedPreset = (String) entry.getValue();
                        }
                    } else if (!entry.getKey().equals(UNNAMED_PRESET_KEY)) {
                        try {
                            Preset preset = GSON.fromJson(GSON.toJson(entry.getValue()), Preset.class);
                            if (preset != null) {
                                presets.put(entry.getKey(), preset);
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            }
        } catch (Exception e) {
            com.kingodogo.buildscape.BuildScape.getLogger().error("Failed to load presets: " + e.getMessage());
            initializeDefaults();
        }

        if (!presets.containsKey("default")) {
            initializeDefaults();
        }

        if (lastAppliedPreset == null || lastAppliedPreset.isEmpty()) {
            lastAppliedPreset = "default";
        }
    }
    
    public void applyPresetOnStartup() {
        PillarParticleConfig itemConfig = PillarParticleConfig.get();
        if (itemConfig.items.isEmpty()) {
            applyPreset("default");
        }
    }
    
    public void save() {
        File file = getPresetsFile();
        try {
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
            com.kingodogo.buildscape.BuildScape.getLogger().error("Failed to save presets: " + e.getMessage());
        }
    }
    
    private void initializeDefaults() {
        presets.clear();

        Preset defaultPreset = new Preset("Default", getDefaultItems());
        presets.put("default", defaultPreset);
    }
    
    private Set<String> getDefaultItems() {
        Set<String> defaultItems = new HashSet<>();
        defaultItems.add("minecraft:diamond");
        defaultItems.add("minecraft:netherite_ingot");
        defaultItems.add("minecraft:nether_star");
        defaultItems.add("minecraft:heart_of_the_sea");
        defaultItems.add("minecraft:trident");
        defaultItems.add("minecraft:emerald");
        defaultItems.add("minecraft:dragon_breath");
        defaultItems.add("minecraft:netherite_scrap");
        defaultItems.add("minecraft:totem_of_undying");
        defaultItems.add("minecraft:spyglass");
        defaultItems.add("minecraft:elytra");
        defaultItems.add("minecraft:diamond_sword");
        defaultItems.add("minecraft:diamond_hoe");
        defaultItems.add("minecraft:diamond_axe");
        defaultItems.add("minecraft:diamond_pickaxe");
        defaultItems.add("minecraft:diamond_shovel");
        defaultItems.add("minecraft:diamond_boots");
        defaultItems.add("minecraft:diamond_leggings");
        defaultItems.add("minecraft:diamond_chestplate");
        defaultItems.add("minecraft:diamond_helmet");
        defaultItems.add("minecraft:netherite_sword");
        defaultItems.add("minecraft:netherite_hoe");
        defaultItems.add("minecraft:netherite_pickaxe");
        defaultItems.add("minecraft:netherite_axe");
        defaultItems.add("minecraft:netherite_shovel");
        defaultItems.add("minecraft:netherite_boots");
        defaultItems.add("minecraft:netherite_leggings");
        defaultItems.add("minecraft:netherite_chestplate");
        defaultItems.add("minecraft:netherite_helmet");
        defaultItems.add("minecraft:nautilus_shell");
        defaultItems.add("minecraft:shulker_shell");
        defaultItems.add("minecraft:golden_apple");
        defaultItems.add("minecraft:enchanted_golden_apple");
        defaultItems.add("minecraft:golden_carrot");
        defaultItems.add("minecraft:experience_bottle");
        defaultItems.add("minecraft:mojang_banner_pattern");
        defaultItems.add("minecraft:ancient_debris");
        defaultItems.add("minecraft:dragon_head");
        defaultItems.add("minecraft:dragon_egg");
        defaultItems.add("minecraft:player_head");
        defaultItems.add("minecraft:beacon");
        defaultItems.add("minecraft:end_crystal");
        defaultItems.add("minecraft:conduit");
        defaultItems.add("minecraft:skeleton_skull");
        defaultItems.add("minecraft:zombie_head");
        defaultItems.add("minecraft:wither_skeleton_skull");
        defaultItems.add("minecraft:creeper_head");
        defaultItems.add("minecraft:enchanting_table");
        defaultItems.add("minecraft:emerald_block");
        defaultItems.add("minecraft:diamond_block");
        defaultItems.add("minecraft:gold_block");
        defaultItems.add("minecraft:netherite_block");
        defaultItems.add("minecraft:deepslate_diamond_ore");
        defaultItems.add("minecraft:diamond_ore");
        defaultItems.add("minecraft:bedrock");
        return defaultItems;
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
    
    public Preset getPreset(String key) {
        return presets.get(key);
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
            config.items.clear();
            config.items.addAll(preset.items);
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

