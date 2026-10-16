package com.kingodogo.buildscape.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;

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

    public enum EditPermissionMode {
        STRICT,
        STANDARD,
        OPEN
    }

    public enum EditScope {
        LIMITED,
        GENERAL,
        POLICY
    }

    public static EditPermissionMode parseEditPermissionMode(String value) {
        if (value == null) return EditPermissionMode.STRICT;
        try {
            return EditPermissionMode.valueOf(value.trim().toUpperCase(java.util.Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return EditPermissionMode.STRICT;
        }
    }

    public static boolean canEdit(EditPermissionMode mode, int permissionLevel, EditScope scope) {
        if (scope == EditScope.POLICY) return permissionLevel >= CONFIG_PERMISSION_LEVEL;
        EditPermissionMode resolvedMode = mode != null ? mode : EditPermissionMode.STRICT;
        return switch (resolvedMode) {
            case STRICT -> permissionLevel >= CONFIG_PERMISSION_LEVEL;
            case STANDARD -> scope == EditScope.LIMITED
                    ? permissionLevel >= 2
                    : permissionLevel >= CONFIG_PERMISSION_LEVEL;
            case OPEN -> true;
        };
    }

    public static boolean canPlayerEdit(Player player, EditPermissionMode mode, EditScope scope) {
        int permissionLevel = Services.PLATFORM.hasPermission(player, CONFIG_PERMISSION_LEVEL) ? CONFIG_PERMISSION_LEVEL
                : Services.PLATFORM.hasPermission(player, 2) ? 2 : 0;
        return canEdit(mode, permissionLevel, scope);
    }
    public static final String[] DEFAULT_COLORS = {
            "#FFB81C", "#FFFFFF", "#FFFF00", "#E8FEFD", "#FF5C00", "#3CDFFF", "#9B51E0"
    };

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Gson LAYOUT_GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
    private static final String PROPERTIES_FILE_NAME = "pillar-properties.json";
    private static final String ITEMS_FILE_NAME = "pillar-items.json";

    private static PillarParticleConfig INSTANCE;
    private static volatile PillarParticleConfig SERVER_CONFIG;
    private static volatile PillarParticleConfig SNAPSHOT = null;
    private static volatile int CLIENT_PERMISSION_LEVEL = -1;

    public static int getClientPermissionLevel() {
        return CLIENT_PERMISSION_LEVEL;
    }

    public static void setClientPermissionLevel(int permissionLevel) {
        CLIENT_PERMISSION_LEVEL = Math.max(-1, Math.min(CONFIG_PERMISSION_LEVEL, permissionLevel));
    }

    public static void clearServerConfig() {
        SERVER_CONFIG = null;
        SNAPSHOT = null;
        CLIENT_PERMISSION_LEVEL = -1;
        notifyCallbacks(false);
    }

    public static void setServerConfig(PillarParticleConfig config) {
        config.remote = true;
        SERVER_CONFIG = config;
        notifyCallbacks(true);
    }

    private transient boolean remote;
    private transient boolean corruptedProperties;
    private transient JsonObject originalProperties;
    private transient JsonObject loadedPropertyValues;
    private transient String propertiesSource;
    private transient boolean corruptedItems;
    private final transient java.nio.file.Path configDirectory;

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
    public EditPermissionMode edit_permission_mode = EditPermissionMode.STRICT;

    public PillarParticleConfig() {
        this(null);
    }

    PillarParticleConfig(java.nio.file.Path configDirectory) {
        this.configDirectory = configDirectory;
        this.particle_color.add("#FFB81C");
        this.particle_color.add("#FFFFFF");
        this.particle_color.add("#FFFF00");
        this.particle_color.add("#E8FEFD");
        this.particle_color.add("#FF5C00");
        this.particle_color.add("#3CDFFF");
        this.particle_color.add("#9B51E0");
        this.loadedPropertyValues = propertyValues();
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
        PillarParticleConfig server = SERVER_CONFIG;
        return server != null ? server : getLocalConfig();
    }

    public static PillarParticleConfig getServerConfig() {
        return getLocalConfig();
    }
    public static PillarParticleConfig peek() {
        PillarParticleConfig server = SERVER_CONFIG;
        if (server != null) return server;
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
        File dir = configDirectory != null ? configDirectory.toFile() : ConfigPaths.dir("buildscape", "pillar").toFile();
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

            JsonObject defaults = new PillarParticleConfig(configDirectory).propertyValues();
            String text = groupedProperties(defaults);
            ConfigLayoutIO.write(f.toPath(), text, null, true);
            propertiesSource = text;
            originalProperties = defaults;
        } catch (Exception ignored) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Unable to write default pillar properties", ignored);
        }
    }

    private void writeDefaultItems(File f, boolean vaultLoaded) {
        try {
            File parentDir = f.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (FileWriter writer = new FileWriter(f)) {
                DefaultPillarItems.writeGrouped(writer, vaultLoaded);
            }
        } catch (Exception ignored) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Unable to write default pillar items", ignored);
        }
    }

    static String[] getDefaultItemArray() {
        return DefaultPillarItems.baseItems().toArray(new String[0]);
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
            this.particle_color.add("#9B51E0");
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
            JsonObject original = null;
            boolean legacyReadOnly = false;
            String jsonWithoutComments;
            try {
                original = ConfigLayoutIO.readProperties(content.toString());
                jsonWithoutComments = GSON.toJson(original);
            } catch (Exception strictFailure) {
                // Preserve 4.0.4's loading semantics for hand-edited files, but never migrate them.
                jsonWithoutComments = stripComments(content.toString());
                legacyReadOnly = true;
            }

            Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> jsonMap = GSON.fromJson(jsonWithoutComments, mapType);

            if (jsonMap == null) throw new IllegalArgumentException("Empty pillar properties");

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
                    Object permissionMode = jsonMap.get("edit_permission_mode");
                    this.edit_permission_mode = parseEditPermissionMode(permissionMode instanceof String value ? value : null);

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
            originalProperties = original;
            propertiesSource = content.toString();
            loadedPropertyValues = propertyValues();
            corruptedProperties = legacyReadOnly;
            if (legacyReadOnly) {
                com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn(
                        "Loaded pillar properties using the legacy parser; the file is read-only and will be kept as-is");
            }
            lastLoadedProperties = file.lastModified();
            lastFileSizeProperties = file.length();
        } catch (Exception ignored) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Unable to load pillar properties", ignored);
            corruptedProperties = true;
        }
    }

    private void loadItemsInternal() {
        loadItemsInternal(Services.PLATFORM.isModLoaded("the_vault"));
    }

    void loadItemsInternal(boolean vaultLoaded) {
        File file = getItemsFile();
        if (!file.exists()) {
            writeDefaultItems(file, vaultLoaded);
            initializeDefaultItems();
            VaultPillarItems.merge(items, vaultLoaded);
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

            if (jsonMap == null || !(jsonMap.get("items") instanceof List<?>)) {
                throw new IllegalArgumentException("Expected pillar items list");
            }

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

                changed |= VaultPillarItems.merge(items, vaultLoaded);
                if (changed) {
                    saveItemsToDisk();
                }
            }

            lastLoadedItems = file.lastModified();
            lastFileSizeItems = file.length();
        } catch (Exception ignored) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Unable to load pillar items", ignored);
            corruptedItems = true;
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
        return matchesUncached(stack, Services.PLATFORM);
    }

    boolean matchesUncached(ItemStack stack, com.kingodogo.buildscape.platform.IPlatformAdapter platform) {
        CommonId id = platform.getItemId(stack.getItem());
        if (id != null && items.contains(id.toString())) {
            return true;
        }

        for (String itemOrTag : items) {
            if (itemOrTag != null && itemOrTag.startsWith("#")) {
                String tagString = itemOrTag.substring(1);
                CommonId tagId = tagString.matches("(?:[a-z0-9_.-]+:)?[a-z0-9/._-]+")
                        ? CommonId.parse(tagString) : null;
                if (tagId != null && platform.isItemInTag(stack, tagId)) {
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
        clearMatchCache();
        if (remote) return;
        File file = getItemsFile();
        try {
            if (corruptedItems) {
                ConfigBackup.copyCorrupted(file.toPath());
                corruptedItems = false;
            }
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("{\n");
                writer.write("  // Item IDs that trigger particles when placed on pillars\n");
                writer.write("  \"items\": [\n");
                List<String> itemsList = new ArrayList<>(items);
                itemsList.sort(String::compareTo);
                for (int i = 0; i < itemsList.size(); i++) {
                    writer.write("    " + GSON.toJson(itemsList.get(i)));
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
        if (remote || corruptedProperties) return;
        File file = getPropertiesFile();
        try {
            if (originalProperties == null && file.exists()) {
                // Also protect explicit saves made before the first load.
                String source = ConfigLayoutIO.readText(file.toPath());
                JsonObject original = ConfigLayoutIO.readProperties(source);
                GSON.fromJson(original, PillarParticleConfig.class);
                propertiesSource = source;
                originalProperties = original;
            }
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            JsonObject current = propertyValues();
            JsonObject output = originalProperties == null ? new JsonObject() : originalProperties.deepCopy();
            for (Map.Entry<String, JsonElement> entry : current.entrySet()) {
                String key = entry.getKey();
                boolean changed = loadedPropertyValues == null || !entry.getValue().equals(loadedPropertyValues.get(key));
                if (changed || !output.has(key)) {
                    String alias = key.equals("use_pattern") ? "use_template" : key.replace("pattern", "template");
                    output.add(key, !changed && output.has(alias) ? output.get(alias) : entry.getValue());
                }
            }
            String text = groupedProperties(output);
            ConfigLayoutIO.write(file.toPath(), text, propertiesSource, true);
            propertiesSource = text;
            originalProperties = output;
            loadedPropertyValues = current;
            lastLoadedProperties = file.lastModified();
            lastFileSizeProperties = file.length();
        } catch (Exception ignored) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Unable to save pillar properties", ignored);
        }
    }

    private JsonObject propertyValues() {
        JsonObject values = new JsonObject();
        values.add("particle_color", GSON.toJsonTree(particle_color));
        values.addProperty("max_particle_color", max_particle_color);
        values.addProperty("particle_spread", particle_spread);
        values.addProperty("particle_speed", particle_speed);
        values.addProperty("particle_lifetime", particle_lifetime);
        values.addProperty("pattern", pattern);
        values.addProperty("pattern_speed", pattern_speed);
        values.addProperty("pattern_spread", pattern_spread);
        values.addProperty("pattern_intensity", pattern_intensity);
        values.addProperty("use_pattern", use_pattern);
        values.addProperty("particle_density", particle_density);
        values.addProperty("edit_permission_mode", edit_permission_mode == null ? null : edit_permission_mode.name());
        return values;
    }

    private static String groupedProperties(JsonObject values) {
        StringBuilder text = new StringBuilder("{\n");
        java.util.Set<String> written = new java.util.HashSet<>();
        appendProperties(text, values, written, "Visuals", new String[][]{
                {"particle_color", "Hexadecimal particle color palette (up to 7 colors)"},
                {"max_particle_color", "Maximum number of colors to use from particle_color (1-7)"},
                {"particle_spread", "How far particles spread from center when use_pattern = false"}});
        appendProperties(text, values, written, "Timing & patterns", new String[][]{
                {"particle_speed", "Basic particle parameters (used when use_pattern = false): base speed multiplier"},
                {"particle_lifetime", "How long particles last (in ticks, 20 ticks = 1 second)"},
                {"pattern", "Available patterns: beam, spiral, fountain, pulse, ring, burst, snowflake"},
                {"pattern_speed", "Base speed multiplier for pattern particles"},
                {"pattern_spread", "Spread amount for pattern particles"},
                {"pattern_intensity", "Intensity multiplier for pattern effects (higher = more intense)"}});
        appendProperties(text, values, written, "Behaviour", new String[][]{
                {"use_pattern", "Pattern system (overrides basic particle parameters)"},
                {"particle_density", "Number of particles spawned per tick when use_pattern = false"}});
        text.append("\n  // --- Other ---\n");
        // Sort unknown keys as well, so output remains stable regardless of input key order.
        for (String key : new java.util.TreeSet<>(values.keySet())) {
            if (!written.contains(key) && !key.equals("edit_permission_mode")) {
                text.append("  // Unrecognized setting retained with its original value\n");
                appendProperty(text, key, values.get(key));
            }
        }
        appendProperties(text, values, written, "Permissions & policy", new String[][]{
                {"edit_permission_mode", "Who may edit pillar settings: STRICT, STANDARD, or OPEN"}});
        int comma = text.lastIndexOf(",\n");
        if (comma >= 0) text.deleteCharAt(comma);
        return text.append("}\n").toString();
    }

    private static void appendProperties(StringBuilder text, JsonObject values, Set<String> written,
                                         String section, String[][] entries) {
        text.append("\n  // --- ").append(section).append(" ---\n");
        for (String[] entry : entries) {
            written.add(entry[0]);
            if (!values.has(entry[0])) continue;
            text.append("  // ").append(entry[1]).append('\n');
            appendProperty(text, entry[0], values.get(entry[0]));
        }
    }

    private static void appendProperty(StringBuilder text, String key, JsonElement value) {
        text.append("  ").append(LAYOUT_GSON.toJson(key)).append(": ").append(LAYOUT_GSON.toJson(value)).append(",\n");
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
