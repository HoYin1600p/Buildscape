package com.kingodogo.buildscape.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
public class PillarParticleConfig {

    public static final int CONFIG_PERMISSION_LEVEL = 4;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String PROPERTIES_FILE_NAME = "pillar-properties.json";
    private static final String ITEMS_FILE_NAME = "pillar-items.json";

    private static PillarParticleConfig INSTANCE;
    private static volatile PillarParticleConfig SNAPSHOT = null;

    public static void clearServerConfig() {
        SNAPSHOT = null;
    }

    private long lastLoadedProperties = 0L;
    private long lastFileSizeProperties = 0L;
    private long lastLoadedItems = 0L;
    private long lastFileSizeItems = 0L;

    private static final List<Consumer<Boolean>> CONFIG_RELOAD_CALLBACKS = new CopyOnWriteArrayList<>();
    private static final ConcurrentHashMap<Item, Boolean> MATCH_CACHE = new ConcurrentHashMap<>();

    private static volatile long lastStatCheckTime = 0L;
    private static final long STAT_CHECK_INTERVAL_MS = 2000L;

    public double particle_speed = 0.02D;
    public double particle_spread = 0.1D;
    public int particle_lifetime = 20;
    public int particle_density = 2;

    public boolean use_pattern = true;
    public String pattern = "ring";

    public double pattern_speed = 0.05D;
    public double pattern_spread = 0.05D;
    public double pattern_intensity = 1.0D;

    public List<String> particle_color = new ArrayList<>();
    public int max_particle_color = 3;

    public Set<String> items = new HashSet<>();

    public PillarParticleConfig() {
        this.particle_color.add("#FFB81C");
        this.particle_color.add("#FFFFFF");
        this.particle_color.add("#FFFF00");
        this.particle_color.add("#E8FEFD");
        this.particle_color.add("#FF5C00");
        this.particle_color.add("#3CDFFF");
    }
    public static void addConfigReloadCallback(Consumer<Boolean> callback) {
        CONFIG_RELOAD_CALLBACKS.add(callback);
    }

    private static void notifyCallbacks(boolean isRemote) {
        for (Consumer<Boolean> callback : CONFIG_RELOAD_CALLBACKS) {
            try {
                callback.accept(isRemote);
            } catch (Exception ignored) {
                com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Pillar config reload callback failed", ignored);
            }
        }
        clearMatchCache();
    }

    private static void clearMatchCache() {
        MATCH_CACHE.clear();
    }
    public static PillarParticleConfig get() {
        return getLocalConfig();
    }

    public static PillarParticleConfig getServerConfig() {
        return get();
    }
    public static PillarParticleConfig peek() {
        PillarParticleConfig s = SNAPSHOT;
        return s != null ? s : get();
    }

    private static synchronized PillarParticleConfig getLocalConfig() {
        if (INSTANCE == null) {
            INSTANCE = new PillarParticleConfig();
            INSTANCE.loadInternal();
            SNAPSHOT = INSTANCE;
        } else {
            long now = System.currentTimeMillis();
            if (now - lastStatCheckTime >= STAT_CHECK_INTERVAL_MS) {
                lastStatCheckTime = now;

                File propertiesFile = INSTANCE.getPropertiesFile();
                File itemsFile = INSTANCE.getItemsFile();

                boolean reloadProperties = false;
                boolean reloadItems = false;

                if (propertiesFile.exists()) {
                    long currentModified = propertiesFile.lastModified();
                    long currentSize = propertiesFile.length();
                    if (currentModified != INSTANCE.lastLoadedProperties || currentSize != INSTANCE.lastFileSizeProperties) {
                        reloadProperties = true;
                    }
                } else {
                    INSTANCE.lastLoadedProperties = 0L;
                    INSTANCE.lastFileSizeProperties = 0L;
                }

                if (itemsFile.exists()) {
                    long currentModified = itemsFile.lastModified();
                    long currentSize = itemsFile.length();
                    if (currentModified != INSTANCE.lastLoadedItems || currentSize != INSTANCE.lastFileSizeItems) {
                        reloadItems = true;
                    }
                } else {
                    INSTANCE.lastLoadedItems = 0L;
                    INSTANCE.lastFileSizeItems = 0L;
                }

                if (reloadProperties) {
                    INSTANCE.loadPropertiesInternal();
                    notifyCallbacks(false);
                }
                if (reloadItems) {
                    INSTANCE.loadItemsInternal();
                }
            }
            SNAPSHOT = INSTANCE;
        }
        return INSTANCE;
    }

    private File getPropertiesFile() {
        return new File(getConfigDir(), PROPERTIES_FILE_NAME);
    }

    private File getItemsFile() {
        return new File(getConfigDir(), ITEMS_FILE_NAME);
    }

    private File getConfigDir() {
        File dir = Paths.get("config", "buildscape", "pillar").toFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private void writeDefaultProperties(File f) {
        try {
            File parentDir = f.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (FileWriter writer = new FileWriter(f)) {
                writer.write("{\n");
                writer.write("  // Basic particle parameters (used when use_pattern = false)\n");
                writer.write("  \"particle_speed\": 0.02,\n");
                writer.write("  \"particle_spread\": 0.1,\n");
                writer.write("  \"particle_lifetime\": 20,\n");
                writer.write("  \"particle_density\": 2,\n");
                writer.write("  \n");
                writer.write("  // Pattern system (overrides basic particle parameters)\n");
                writer.write("  \"use_pattern\": true,\n");
                writer.write("  \n");
                writer.write("  // Available patterns: beam, spiral, fountain, pulse, ring, burst, snowflake\n");
                writer.write("  \"pattern\": \"ring\",\n");
                writer.write("  \n");
                writer.write("  \"pattern_speed\": 0.05,\n");
                writer.write("  \"pattern_spread\": 0.05,\n");
                writer.write("  \"pattern_intensity\": 1.0,\n");
                writer.write("  \n");
                writer.write("  // Hexadecimal particle color palette (up to 7 colors)\n");
                writer.write("  \"particle_color\": [\"#FFB81C\", \"#FFFFFF\", \"#FFFF00\", \"#E8FEFD\", \"#FF5C00\", \"#3CDFFF\"],\n");
                writer.write("  \n");
                writer.write("  \"max_particle_color\": 3\n");
                writer.write("}\n");
            }
        } catch (Exception ignored) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Unable to write default pillar properties", ignored);
        }
    }

    private void writeDefaultItems(File f) {
        try {
            File parentDir = f.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (FileWriter writer = new FileWriter(f)) {
                writer.write("{\n");
                writer.write("  // Item IDs that trigger particles when placed on pillars\n");
                writer.write("  \"items\": [\n");
                String[] defaultVanillaItems = getDefaultItemArray();
                for (int i = 0; i < defaultVanillaItems.length; i++) {
                    writer.write("    \"" + defaultVanillaItems[i] + "\"");
                    if (i < defaultVanillaItems.length - 1) {
                        writer.write(",");
                    }
                    writer.write("\n");
                }
                writer.write("  ]\n");
                writer.write("}\n");
            }
        } catch (Exception ignored) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Unable to write default pillar items", ignored);
        }
    }

    private static String[] getDefaultItemArray() {
        return new String[]{
                "minecraft:diamond",
                "minecraft:netherite_ingot",
                "minecraft:nether_star",
                "minecraft:heart_of_the_sea",
                "minecraft:trident",
                "minecraft:emerald",
                "minecraft:dragon_breath",
                "minecraft:netherite_scrap",
                "minecraft:totem_of_undying",
                "minecraft:spyglass",
                "minecraft:elytra",
                "minecraft:diamond_sword",
                "minecraft:diamond_hoe",
                "minecraft:diamond_axe",
                "minecraft:diamond_pickaxe",
                "minecraft:diamond_shovel",
                "minecraft:diamond_boots",
                "minecraft:diamond_leggings",
                "minecraft:diamond_chestplate",
                "minecraft:diamond_helmet",
                "minecraft:netherite_sword",
                "minecraft:netherite_hoe",
                "minecraft:netherite_pickaxe",
                "minecraft:netherite_axe",
                "minecraft:netherite_shovel",
                "minecraft:netherite_boots",
                "minecraft:netherite_leggings",
                "minecraft:netherite_chestplate",
                "minecraft:netherite_helmet",
                "minecraft:nautilus_shell",
                "minecraft:shulker_shell",
                "minecraft:golden_apple",
                "minecraft:enchanted_golden_apple",
                "minecraft:golden_carrot",
                "minecraft:experience_bottle",
                "minecraft:mojang_banner_pattern",
                "minecraft:ancient_debris",
                "minecraft:dragon_head",
                "minecraft:dragon_egg",
                "minecraft:player_head",
                "minecraft:beacon",
                "minecraft:end_crystal",
                "minecraft:conduit",
                "minecraft:skeleton_skull",
                "minecraft:zombie_head",
                "minecraft:wither_skeleton_skull",
                "minecraft:creeper_head",
                "minecraft:enchanting_table",
                "minecraft:emerald_block",
                "minecraft:diamond_block",
                "minecraft:gold_block",
                "minecraft:netherite_block",
                "minecraft:deepslate_diamond_ore",
                "minecraft:diamond_ore",
                "minecraft:bedrock",
                "minecraft:pufferfish",
                "minecraft:poisonous_potato",
                "minecraft:written_book",
                "minecraft:creeper_spawn_egg",
                "minecraft:turtle_spawn_egg",
                "minecraft:axolotl_spawn_egg",
                "minecraft:wither_skeleton_spawn_egg",
                "minecraft:shulker_spawn_egg",
                "minecraft:elder_guardian_spawn_egg",
                "minecraft:ravager_spawn_egg",
                "minecraft:slime_spawn_egg",
                "minecraft:zoglin_spawn_egg",
                "minecraft:villager_spawn_egg",
                "minecraft:skeleton_horse_spawn_egg",
                "minecraft:glow_squid_spawn_egg",
                "minecraft:goat_spawn_egg",
                "minecraft:enderman_spawn_egg",
                "buildscape:ancient_ashen_scroll"
        };
    }

    private static String stripComments(String json) {
        StringBuilder result = new StringBuilder();
        boolean inString = false;
        boolean inEscape = false;

        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);

            if (inEscape) {
                inEscape = false;
                result.append(c);
                continue;
            }

            if (c == '\\' && inString) {
                inEscape = true;
                result.append(c);
                continue;
            }

            if (c == '"') {
                inString = !inString;
                result.append(c);
                continue;
            }

            if (!inString && c == '/' && i + 1 < json.length()) {
                char next = json.charAt(i + 1);
                if (next == '/') {
                    i++;
                    while (i + 1 < json.length() && json.charAt(i + 1) != '\n' && json.charAt(i + 1) != '\r') {
                        i++;
                    }
                    continue;
                } else if (next == '*') {
                    i += 2;
                    while (i + 1 < json.length()) {
                        if (json.charAt(i) == '*' && json.charAt(i + 1) == '/') {
                            i++;
                            break;
                        }
                        i++;
                    }
                    continue;
                }
            }

            result.append(c);
        }

        return result.toString();
    }

    private void loadInternal() {
        loadPropertiesInternal();
        loadItemsInternal();
    }

    private void loadPropertiesInternal() {
        File file = getPropertiesFile();
        if (!file.exists()) {
            writeDefaultProperties(file);
            this.particle_color.clear();
            this.particle_color.add("#FFB81C");
            this.particle_color.add("#FFFFFF");
            this.particle_color.add("#FFFF00");
            this.particle_color.add("#E8FEFD");
            this.particle_color.add("#FF5C00");
            this.particle_color.add("#3CDFFF");
            this.max_particle_color = 3;
            lastLoadedProperties = file.lastModified();
            lastFileSizeProperties = file.length();
            return;
        }

        try (FileReader r = new FileReader(file)) {
            StringBuilder content = new StringBuilder();
            int ch;
            while ((ch = r.read()) != -1) {
                content.append((char) ch);
            }
            String jsonWithoutComments = stripComments(content.toString());

            Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> jsonMap = GSON.fromJson(jsonWithoutComments, mapType);

            if (jsonMap != null) {
                if (jsonMap.containsKey("use_template") && !jsonMap.containsKey("use_pattern")) {
                    jsonMap.put("use_pattern", jsonMap.get("use_template"));
                }
                if (jsonMap.containsKey("template") && !jsonMap.containsKey("pattern")) {
                    jsonMap.put("pattern", jsonMap.get("template"));
                }
                if (jsonMap.containsKey("template_speed") && !jsonMap.containsKey("pattern_speed")) {
                    jsonMap.put("pattern_speed", jsonMap.get("template_speed"));
                }
                if (jsonMap.containsKey("template_spread") && !jsonMap.containsKey("pattern_spread")) {
                    jsonMap.put("pattern_spread", jsonMap.get("template_spread"));
                }
                if (jsonMap.containsKey("template_intensity") && !jsonMap.containsKey("pattern_intensity")) {
                    jsonMap.put("pattern_intensity", jsonMap.get("template_intensity"));
                }

                String updatedJson = GSON.toJson(jsonMap);
                Type type = new TypeToken<PillarParticleConfig>() {}.getType();
                PillarParticleConfig loaded = GSON.fromJson(updatedJson, type);

                if (loaded != null) {
                    this.particle_speed = loaded.particle_speed > 0 ? loaded.particle_speed : this.particle_speed;
                    this.particle_spread = loaded.particle_spread > 0 ? loaded.particle_spread : this.particle_spread;
                    this.particle_lifetime = loaded.particle_lifetime > 0 ? loaded.particle_lifetime : this.particle_lifetime;
                    this.particle_density = loaded.particle_density > 0 ? loaded.particle_density : this.particle_density;
                    this.use_pattern = loaded.use_pattern;
                    this.pattern = loaded.pattern != null ? loaded.pattern : "ring";
                    this.pattern_speed = loaded.pattern_speed > 0 ? loaded.pattern_speed : this.pattern_speed;
                    this.pattern_spread = loaded.pattern_spread > 0 ? loaded.pattern_spread : this.pattern_spread;
                    this.pattern_intensity = loaded.pattern_intensity > 0 ? loaded.pattern_intensity : this.pattern_intensity;

                    List<String> rawColors = null;
                    if (loaded.particle_color != null && !loaded.particle_color.isEmpty()) {
                        rawColors = loaded.particle_color;
                    }

                    this.particle_color = new ArrayList<>();
                    if (rawColors != null && !rawColors.isEmpty()) {
                        for (String color : rawColors) {
                            if (color != null && color.matches("^#[0-9A-Fa-f]{6}$")) {
                                this.particle_color.add(color.toUpperCase());
                            }
                        }
                    }

                    if (this.particle_color.isEmpty()) {
                        this.particle_color.add("#FFB81C");
                        this.particle_color.add("#FFFFFF");
                        this.particle_color.add("#FFFF00");
                    }

                    if (this.particle_color.size() > 7) {
                        this.particle_color = new ArrayList<>(this.particle_color.subList(0, 7));
                    }

                    int numColors = this.particle_color.size();
                    this.max_particle_color = Math.max(1, Math.min(7, Math.min(loaded.max_particle_color > 0 ? loaded.max_particle_color : numColors, numColors)));
                }
            }
            lastLoadedProperties = file.lastModified();
            lastFileSizeProperties = file.length();
        } catch (Exception ignored) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Unable to load pillar properties", ignored);
        }
    }

    private void loadItemsInternal() {
        File file = getItemsFile();
        if (!file.exists()) {
            writeDefaultItems(file);
            initializeDefaultItems();
            lastLoadedItems = file.lastModified();
            lastFileSizeItems = file.length();
            return;
        }

        try (FileReader r = new FileReader(file)) {
            StringBuilder content = new StringBuilder();
            int ch;
            while ((ch = r.read()) != -1) {
                content.append((char) ch);
            }
            String jsonWithoutComments = stripComments(content.toString());

            Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> jsonMap = GSON.fromJson(jsonWithoutComments, mapType);

            if (jsonMap != null && jsonMap.containsKey("items")) {
                Object itemsObj = jsonMap.get("items");
                this.items = new HashSet<>();
                if (itemsObj instanceof List<?> itemsList) {
                    for (Object item : itemsList) {
                        if (item instanceof String s) {
                            this.items.add(s);
                        }
                    }
                }

                boolean changed = false;
                for (String defaultItem : getDefaultItemArray()) {
                    if (!this.items.contains(defaultItem)) {
                        this.items.add(defaultItem);
                        changed = true;
                    }
                }

                if (changed) {
                    saveItemsToDisk();
                }
            }

            lastLoadedItems = file.lastModified();
            lastFileSizeItems = file.length();
        } catch (Exception ignored) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Unable to load pillar items", ignored);
        }
    }

    private void initializeDefaultItems() {
        items.clear();
        for (String defaultItem : getDefaultItemArray()) {
            items.add(defaultItem);
        }
    }
    public boolean matches(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        if (items == null || items.isEmpty()) {
            return true;
        }

        Boolean cached = MATCH_CACHE.get(stack.getItem());
        if (cached != null) {
            return cached;
        }

        boolean result = matchesUncached(stack);
        MATCH_CACHE.put(stack.getItem(), result);
        return result;
    }

    private boolean matchesUncached(ItemStack stack) {
        CommonId id = Services.PLATFORM.getItemId(stack.getItem());
        if (id != null && items.contains(id.toString())) {
            return true;
        }

        for (String itemOrTag : items) {
            if (itemOrTag != null && itemOrTag.startsWith("#")) {
                String tagString = itemOrTag.substring(1);
                CommonId tagId = CommonId.tryParse(tagString);
                if (tagId != null && Services.PLATFORM.isItemInTag(stack, tagId)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void saveItems() {
        saveItemsToDisk();
    }
    public void saveItemsToDisk() {
        File file = getItemsFile();
        try {
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("{\n");
                writer.write("  // Item IDs that trigger particles when placed on pillars\n");
                writer.write("  \"items\": [\n");
                List<String> itemsList = new ArrayList<>(items);
                for (int i = 0; i < itemsList.size(); i++) {
                    writer.write("    \"" + itemsList.get(i) + "\"");
                    if (i < itemsList.size() - 1) {
                        writer.write(",");
                    }
                    writer.write("\n");
                }
                writer.write("  ]\n");
                writer.write("}\n");
            }
            lastLoadedItems = file.lastModified();
            lastFileSizeItems = file.length();
        } catch (Exception ignored) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Unable to save pillar items", ignored);
        }
    }

    public void saveProperties() {
        savePropertiesToDisk();
    }
    public void savePropertiesToDisk() {
        File file = getPropertiesFile();
        try {
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("{\n");
                writer.write("  \"particle_speed\": " + particle_speed + ",\n");
                writer.write("  \"particle_spread\": " + particle_spread + ",\n");
                writer.write("  \"particle_lifetime\": " + particle_lifetime + ",\n");
                writer.write("  \"particle_density\": " + particle_density + ",\n");
                writer.write("  \"use_pattern\": " + use_pattern + ",\n");
                writer.write("  \"pattern\": \"" + pattern + "\",\n");
                writer.write("  \"pattern_speed\": " + pattern_speed + ",\n");
                writer.write("  \"pattern_spread\": " + pattern_spread + ",\n");
                writer.write("  \"pattern_intensity\": " + pattern_intensity + ",\n");
                writer.write("  \"particle_color\": " + GSON.toJson(particle_color) + ",\n");
                writer.write("  \"max_particle_color\": " + max_particle_color + "\n");
                writer.write("}\n");
            }
            lastLoadedProperties = file.lastModified();
            lastFileSizeProperties = file.length();
        } catch (Exception ignored) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Unable to save pillar properties", ignored);
        }
    }

    public boolean addItem(String itemId) {
        if (itemId == null || itemId.isEmpty()) {
            return false;
        }
        if (items.add(itemId)) {
            saveItemsToDisk();
            clearMatchCache();
            return true;
        }
        return false;
    }

    public boolean removeItem(String itemId) {
        if (itemId == null || itemId.isEmpty()) {
            return false;
        }
        if (items.remove(itemId)) {
            saveItemsToDisk();
            clearMatchCache();
            return true;
        }
        return false;
    }

    public static void addItemToConfig(String itemId) {
        PillarParticleConfig config = get();
        config.addItem(itemId);
    }
}
