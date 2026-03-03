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

    private File getConfigDir() {
        String configPath = Paths.get("config", "buildscape", "pillar").toString();
        File dir = new File(configPath);
        if (!dir.exists())
            dir.mkdirs();
        return dir;
    }

    private Map<String, Preset> presets = new HashMap<>();
    private String lastAppliedPreset = "default"; // Track last applied preset
    private static final String UNNAMED_PRESET_KEY = "_unnamed"; // Special key for unsaved changes
    public static final int MAX_PRESETS = 5;

    public void load() {
        File file = getPresetsFile();
        if (!file.exists()) {
            initializeDefaults();
            save();
            return;
        }

        try (FileReader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            Map<String, Object> loaded = GSON.fromJson(reader, type);
            if (loaded != null) {
                // Load presets
                presets = new HashMap<>();
                for (Map.Entry<String, Object> entry : loaded.entrySet()) {
                    if (entry.getKey().equals("_lastApplied")) {
                        // Load last applied preset key
                        if (entry.getValue() instanceof String) {
                            lastAppliedPreset = (String) entry.getValue();
                        }
                    } else if (!entry.getKey().equals(UNNAMED_PRESET_KEY)) {
                        // Load preset (skip unnamed as it's temporary)
                        try {
                            Preset preset = GSON.fromJson(GSON.toJson(entry.getValue()), Preset.class);
                            if (preset != null) {
                                presets.put(entry.getKey(), preset);
                            }
                        } catch (Exception e) {
                            // Skip invalid preset
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
        } else {
            // Check for auto-update of "default" preset with new mod items
            if (net.minecraftforge.fml.ModList.get().isLoaded("the_vault")) {
                Preset defaultPreset = presets.get("default");
                boolean changed = false;

                // Get all default items (includes vault items if mod is loaded)
                Set<String> allDefaults = getDefaultItems();

                for (String item : allDefaults) {
                    // Only auto-add vault items that are missing (respect user removal of other
                    // items)
                    if ((item.startsWith("the_vault:") || item.startsWith("#the_vault:"))
                            && !defaultPreset.items.contains(item)) {
                        defaultPreset.items.add(item);
                        changed = true;
                    }
                }

                if (changed) {
                    save();
                }
            }
        }

        // Initialize lastAppliedPreset if not set
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

    public void save() {
        File file = getPresetsFile();
        try {
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (FileWriter writer = new FileWriter(file)) {
                // Save presets and last applied preset (but not unnamed preset)
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

    // Call this after PillarParticleConfig has loaded to apply default preset
    public void applyPresetOnStartup() {
        // Always apply default preset on startup if items are empty
        PillarParticleConfig itemConfig = PillarParticleConfig.get();
        if (itemConfig.items.isEmpty()) {
            // First time - apply default preset
            applyPreset("default");
        }
    }

    private void initializeDefaults() {
        presets.clear();

        // Create default preset with default items
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
        return modId.equals("minecraft") || net.minecraftforge.fml.ModList.get().isLoaded(modId);
    }

    private Set<String> getDefaultItems() {
        Set<String> defaultItems = new HashSet<>();
        List<String> allItems = new ArrayList<>();
        allItems.add("minecraft:diamond");
        allItems.add("minecraft:netherite_ingot");
        allItems.add("minecraft:nether_star");
        allItems.add("minecraft:heart_of_the_sea");
        allItems.add("minecraft:trident");
        allItems.add("minecraft:emerald");
        allItems.add("minecraft:dragon_breath");
        allItems.add("minecraft:netherite_scrap");
        allItems.add("minecraft:totem_of_undying");
        allItems.add("minecraft:spyglass");
        allItems.add("minecraft:elytra");
        allItems.add("minecraft:diamond_sword");
        allItems.add("minecraft:diamond_hoe");
        allItems.add("minecraft:diamond_axe");
        allItems.add("minecraft:diamond_pickaxe");
        allItems.add("minecraft:diamond_shovel");
        allItems.add("minecraft:diamond_boots");
        allItems.add("minecraft:diamond_leggings");
        allItems.add("minecraft:diamond_chestplate");
        allItems.add("minecraft:diamond_helmet");
        allItems.add("minecraft:netherite_sword");
        allItems.add("minecraft:netherite_hoe");
        allItems.add("minecraft:netherite_pickaxe");
        allItems.add("minecraft:netherite_axe");
        allItems.add("minecraft:netherite_shovel");
        allItems.add("minecraft:netherite_boots");
        allItems.add("minecraft:netherite_leggings");
        allItems.add("minecraft:netherite_chestplate");
        allItems.add("minecraft:netherite_helmet");
        allItems.add("minecraft:nautilus_shell");
        allItems.add("minecraft:shulker_shell");
        allItems.add("minecraft:golden_apple");
        allItems.add("minecraft:enchanted_golden_apple");
        allItems.add("minecraft:golden_carrot");
        allItems.add("minecraft:experience_bottle");
        allItems.add("minecraft:mojang_banner_pattern");
        allItems.add("minecraft:ancient_debris");
        allItems.add("minecraft:dragon_head");
        allItems.add("minecraft:dragon_egg");
        allItems.add("minecraft:player_head");
        allItems.add("minecraft:beacon");
        allItems.add("minecraft:end_crystal");
        allItems.add("minecraft:conduit");
        allItems.add("minecraft:skeleton_skull");
        allItems.add("minecraft:zombie_head");
        allItems.add("minecraft:wither_skeleton_skull");
        allItems.add("minecraft:creeper_head");
        allItems.add("minecraft:enchanting_table");
        allItems.add("minecraft:emerald_block");
        allItems.add("minecraft:diamond_block");
        allItems.add("minecraft:gold_block");
        allItems.add("minecraft:netherite_block");
        allItems.add("minecraft:deepslate_diamond_ore");
        allItems.add("minecraft:diamond_ore");
        allItems.add("minecraft:bedrock");
        allItems.add("minecraft:pufferfish");
        allItems.add("minecraft:poisonous_potato");
        allItems.add("minecraft:written_book");
        allItems.add("buildscape:ancient_ashen_scroll");

        allItems.add("minecraft:creeper_spawn_egg");
        allItems.add("minecraft:turtle_spawn_egg");
        allItems.add("minecraft:axolotl_spawn_egg");
        allItems.add("minecraft:wither_skeleton_spawn_egg");
        allItems.add("minecraft:shulker_spawn_egg");
        allItems.add("minecraft:elder_guardian_spawn_egg");
        allItems.add("minecraft:ravager_spawn_egg");
        allItems.add("minecraft:slime_spawn_egg");
        allItems.add("minecraft:zoglin_spawn_egg");
        allItems.add("minecraft:villager_spawn_egg");
        allItems.add("minecraft:skeleton_horse_spawn_egg");
        allItems.add("minecraft:glow_squid_spawn_egg");
        allItems.add("minecraft:goat_spawn_egg");
        allItems.add("minecraft:enderman_spawn_egg");

        allItems.add("the_vault:echo_pog");
        allItems.add("the_vault:gem_pog");
        allItems.add("the_vault:vault_crystal");
        allItems.add("the_vault:spicy_hearty_burger");
        allItems.add("the_vault:omega_pog");
        allItems.add("the_vault:knowledge_star");
        allItems.add("the_vault:antique");
        allItems.add("the_vault:herald_trophy");
        allItems.add("the_vault:pvp_trophy");
        allItems.add("the_vault:treasure_keyring");
        allItems.add("the_vault:companion_egg");
        allItems.add("the_vault:vault_artifact");
        allItems.add("the_vault:tool");
        allItems.add("the_vault:deck_socket");
        allItems.add("the_vault:card_deck");
        allItems.add("the_vault:vault_god_charm");
        allItems.add("the_vault:boost_modification_stone");
        allItems.add("the_vault:neuralizer");
        allItems.add("the_vault:soul_vortex");
        allItems.add("#the_vault:crystal_capstones");
        allItems.add("#the_vault:keys");
        allItems.add("#the_vault:gems");
        allItems.add("#the_vault:fruits");
        allItems.add("the_vault:unidentified_artifact");
        allItems.add("#the_vault:playerclusters");
        allItems.add("#the_vault:perfectgems");
        allItems.add("#the_vault:playerchunks");
        allItems.add("#the_vault:magnet");
        allItems.add("#the_vault:unique");
        allItems.add("#the_vault:vault_gear");
        allItems.add("the_vault:santa_egg");
        allItems.add("the_vault:grinch_egg");
        allItems.add("the_vault:yeti_egg");

        for (String item : allItems) {
            if (isModLoaded(item)) {
                defaultItems.add(item);
            }
        }
        return defaultItems;
    }

    public List<Preset> getPresets() {
        List<Preset> presetList = new ArrayList<>();
        // Always include default first
        if (presets.containsKey("default")) {
            presetList.add(presets.get("default"));
        }
        // Include unnamed preset if it exists (for display)
        if (presets.containsKey(UNNAMED_PRESET_KEY)) {
            presetList.add(presets.get(UNNAMED_PRESET_KEY));
        }
        // Then add custom presets (up to 5 total including default)
        // Sort by key to maintain consistent order
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
        // Include unnamed preset if it exists
        if (presets.containsKey(UNNAMED_PRESET_KEY)) {
            keys.add(UNNAMED_PRESET_KEY);
        }
        // Add custom preset keys in sorted order
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
            return false; // Can't modify default or unnamed
        }

        // Check if we're at max presets (exclude default and unnamed from count)
        int customPresetCount = 0;
        for (String k : presets.keySet()) {
            if (!k.equals("default") && !k.equals(UNNAMED_PRESET_KEY)) {
                customPresetCount++;
            }
        }

        // If this is a new preset and we're at max, don't allow
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
            return false; // Can't delete default or unnamed
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
                lastAppliedPreset = key; // Track last applied preset (but not unnamed)
                save(); // Save the last applied preset info
            }
        }
    }

    public String getLastAppliedPreset() {
        return lastAppliedPreset;
    }

    public void saveUnnamedPreset(Set<String> items) {
        // Save current items as unnamed preset (unsaved changes)
        presets.put(UNNAMED_PRESET_KEY, new Preset("", items));
        // Don't save to file - this is temporary
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
        // Auto-apply unnamed preset if it exists, otherwise apply last applied preset
        if (hasUnnamedPreset()) {
            applyPreset(UNNAMED_PRESET_KEY);
        } else if (lastAppliedPreset != null && presets.containsKey(lastAppliedPreset)) {
            applyPreset(lastAppliedPreset);
        } else {
            // First time - apply default
            applyPreset("default");
            lastAppliedPreset = "default";
            save();
        }
    }

    public String generatePresetKey() {
        // Generate a unique key for a new preset
        int index = 1;
        while (presets.containsKey("preset_" + index)) {
            index++;
        }
        return "preset_" + index;
    }
}
