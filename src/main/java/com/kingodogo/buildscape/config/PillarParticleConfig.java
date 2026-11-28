package com.kingodogo.buildscape.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.util.CommonId;
import com.kingodogo.buildscape.network.SyncConfigPacket;
import com.kingodogo.buildscape.network.NetworkPacketLimits;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;

import java.io.File;
import java.io.IOException;
import java.util.TreeSet;
import java.util.Collection;
import java.util.Objects;
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
@SuppressWarnings("PMD.CompareObjectsWithEquals")
public class PillarParticleConfig {

    public static boolean isValidItemId(String item) {
        if (item == null) return false;
        String id = item.startsWith("#") ? item.substring(1) : item;
        return id.matches("(?:[a-z0-9_.-]*:)?[a-z0-9/._-]+");
    }

    public static final int CONFIG_PERMISSION_LEVEL = 4;

    public static int resolveClientPermissionLevel(int syncedLevel, boolean levelTwo, boolean levelFour) {
        return syncedLevel >= 0 ? Math.min(CONFIG_PERMISSION_LEVEL, syncedLevel)
                : levelFour ? CONFIG_PERMISSION_LEVEL : levelTwo ? 2 : 0;
    }

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
        com.kingodogo.buildscape.network.SyncConfigChunkPacket.clearPending();
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
    private transient boolean itemsReadOnly;
    private transient boolean itemsLoaded;
    private transient boolean itemsOnDiskIs405;
    private transient String itemsSource;
    private transient JsonObject originalItems;
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
        recordSnapshot();
    }
    public static class Snapshot {
        public final int revision;
        public final double particle_speed;
        public final double particle_spread;
        public final int particle_lifetime;
        public final int particle_density;
        public final boolean use_pattern;
        public final String pattern;
        public final double pattern_speed;
        public final double pattern_spread;
        public final double pattern_intensity;
        public final List<String> particle_color;
        public final int max_particle_color;
        public final Set<String> items;
        public final EditPermissionMode edit_permission_mode;

        public Snapshot(int revision, PillarParticleConfig config) {
            this.revision = revision;
            this.particle_speed = config.particle_speed;
            this.particle_spread = config.particle_spread;
            this.particle_lifetime = config.particle_lifetime;
            this.particle_density = config.particle_density;
            this.use_pattern = config.use_pattern;
            this.pattern = config.pattern != null ? config.pattern : "ring";
            this.pattern_speed = config.pattern_speed;
            this.pattern_spread = config.pattern_spread;
            this.pattern_intensity = config.pattern_intensity;
            this.particle_color = new ArrayList<>(config.particle_color != null ? config.particle_color : List.of());
            this.max_particle_color = config.max_particle_color;
            this.items = new HashSet<>(config.items != null ? config.items : Set.of());
            this.edit_permission_mode = config.edit_permission_mode;
        }
    }

    public int revision = 0;
    private final transient List<Snapshot> snapshotHistory = new ArrayList<>();
    private transient boolean initialLoadDone = false;
    private transient boolean isApplyingUpload = false;

    public synchronized void establishBaseline() {
        snapshotHistory.clear();
        snapshotHistory.add(new Snapshot(revision, this));
        initialLoadDone = true;
    }

    public synchronized void onReload() {
        this.revision++;
        recordSnapshot();
    }

    public synchronized void recordSnapshot() {
        if (isApplyingUpload) {
            if (snapshotHistory.isEmpty() || !isSameState(snapshotHistory.get(snapshotHistory.size() - 1))) {
                snapshotHistory.add(new Snapshot(revision, this));
                if (snapshotHistory.size() > 50) {
                    snapshotHistory.remove(0);
                }
            }
            return;
        }
        if (snapshotHistory.isEmpty()) {
            snapshotHistory.add(new Snapshot(revision, this));
        } else if (snapshotHistory.size() == 1 && revision == 0 && !initialLoadDone) {
            snapshotHistory.set(0, new Snapshot(revision, this));
        } else if (!isSameState(snapshotHistory.get(snapshotHistory.size() - 1))) {
            snapshotHistory.add(new Snapshot(revision, this));
            if (snapshotHistory.size() > 50) {
                snapshotHistory.remove(0);
            }
        }
    }

    private boolean isSameState(Snapshot s) {
        return revision == s.revision
                && Double.compare(particle_speed, s.particle_speed) == 0
                && Double.compare(particle_spread, s.particle_spread) == 0
                && particle_lifetime == s.particle_lifetime
                && particle_density == s.particle_density
                && use_pattern == s.use_pattern
                && Objects.equals(pattern, s.pattern)
                && Double.compare(pattern_speed, s.pattern_speed) == 0
                && Double.compare(pattern_spread, s.pattern_spread) == 0
                && Double.compare(pattern_intensity, s.pattern_intensity) == 0
                && Objects.equals(particle_color, s.particle_color)
                && max_particle_color == s.max_particle_color
                && Objects.equals(items, s.items)
                && edit_permission_mode == s.edit_permission_mode;
    }

    public synchronized Snapshot findBaseSnapshot(int baseRev) {
        for (int i = snapshotHistory.size() - 1; i >= 0; i--) {
            Snapshot s = snapshotHistory.get(i);
            if (s.revision == baseRev) {
                return s;
            }
        }
        return null;
    }

    public synchronized Snapshot findBaseSnapshot(int baseRev, Set<ConfigSetting> groups, Double inSpeed,
            Integer inLifetime, Set<String> inItems) {
        return findBaseSnapshot(baseRev);
    }

    public synchronized boolean applyUpload(PillarParticleConfig client, Set<ConfigSetting> groups) {
        SyncConfigPacket data = new SyncConfigPacket(client);
        data.baseRevision = client.revision;
        boolean applied = applyUpload(data, groups);
        if (applied) {
            client.revision = this.revision;
        }
        return applied;
    }

    public synchronized boolean applyUpload(SyncConfigPacket data, Set<ConfigSetting> groups) {
        if (groups == null || groups.isEmpty()) {
            return true;
        }
        Snapshot base = findBaseSnapshot(data.baseRevision);
        if (base == null) {
            groups.clear();
            return false;
        }

        isApplyingUpload = true;
        try {
            if (groups.contains(ConfigSetting.PARTICLES)) {
                if (Double.compare(data.particle_speed, base.particle_speed) != 0) {
                    this.particle_speed = data.particle_speed;
                }
                if (Double.compare(data.particle_spread, base.particle_spread) != 0) {
                    this.particle_spread = data.particle_spread;
                }
                if (data.particle_lifetime != base.particle_lifetime) {
                    this.particle_lifetime = data.particle_lifetime;
                }
                if (data.particle_density != base.particle_density) {
                    this.particle_density = data.particle_density;
                }
                if (data.use_pattern != base.use_pattern) {
                    this.use_pattern = data.use_pattern;
                }
                if (!Objects.equals(data.pattern, base.pattern) && data.pattern != null) {
                    this.pattern = data.pattern;
                }
                if (Double.compare(data.pattern_speed, base.pattern_speed) != 0) {
                    this.pattern_speed = data.pattern_speed;
                }
                if (Double.compare(data.pattern_spread, base.pattern_spread) != 0) {
                    this.pattern_spread = data.pattern_spread;
                }
                if (Double.compare(data.pattern_intensity, base.pattern_intensity) != 0) {
                    this.pattern_intensity = data.pattern_intensity;
                }
            }

            if (groups.contains(ConfigSetting.COLORS)) {
                if (data.max_particle_color != base.max_particle_color && data.max_particle_color > 0) {
                    this.max_particle_color = data.max_particle_color;
                }
                if (data.particle_color != null) {
                    if (data.particle_color.size() != base.particle_color.size()) {
                        this.particle_color = new ArrayList<>(data.particle_color);
                    } else {
                        List<String> merged = new ArrayList<>(this.particle_color);
                        for (int i = 0; i < data.particle_color.size(); i++) {
                            if (!Objects.equals(data.particle_color.get(i), base.particle_color.get(i))) {
                                while (merged.size() <= i) {
                                    merged.add(DEFAULT_COLORS[Math.min(merged.size(), DEFAULT_COLORS.length - 1)]);
                                }
                                merged.set(i, data.particle_color.get(i));
                            }
                        }
                        this.particle_color = merged;
                    }
                }
            }

            if (groups.contains(ConfigSetting.ITEMS) && data.items != null) {
                Set<String> added = new HashSet<>();
                for (String item : data.items) {
                    if (!base.items.contains(item)) {
                        added.add(item);
                    }
                }
                Set<String> removed = new HashSet<>();
                for (String item : base.items) {
                    if (!data.items.contains(item) && isValidItemId(item)
                            && item.length() <= NetworkPacketLimits.MAX_RESOURCE_ID_LENGTH) {
                        removed.add(item);
                    }
                }

                Set<String> merged = new HashSet<>(this.items);
                merged.addAll(added);
                merged.removeAll(removed);
                this.editItems(merged);
            }

            if (groups.contains(ConfigSetting.POLICY) && data.edit_permission_mode != null) {
                if (data.edit_permission_mode != base.edit_permission_mode) {
                    this.edit_permission_mode = data.edit_permission_mode;
                }
            }

            this.revision++;
            data.baseRevision = this.revision;
            recordSnapshot();
            return true;
        } finally {
            isApplyingUpload = false;
        }
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
                }
                if (reloadItems) {
                    INSTANCE.loadItemsInternal();
                }
                if (reloadProperties || reloadItems) {
                    INSTANCE.onReload();
                    notifyCallbacks(false);
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
        if (ConfigPaths.copyTemplateIfMissing(f.toPath(), "/data/buildscape/config/default_pillar_properties.json")
                || f.exists()) return;
        try {
            File parentDir = f.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            PillarParticleConfig defaultConfig = new PillarParticleConfig(configDirectory);
            defaultConfig.particle_color.remove("#9B51E0");
            JsonObject defaults = defaultConfig.propertyValues();
            String text = groupedProperties(defaults);
            ConfigLayoutIO.write(f.toPath(), text, null, true);
            propertiesSource = text;
            originalProperties = defaults;
        } catch (Exception ignored) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Unable to write default pillar properties", ignored);
        }
    }

    private void writeDefaultItems(File f, boolean vaultLoaded) {
        ConfigPaths.copyTemplateIfMissing(f.toPath(), "/data/buildscape/config/"
                + (vaultLoaded ? "vault_pillar_items.json" : "vanilla_pillar_items.json"));
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
        establishBaseline();
    }

    private void loadPropertiesInternal() {
        File file = getPropertiesFile();
        if (!file.exists()) {
            writeDefaultProperties(file);
        }
        if (!file.exists()) {
            this.particle_color.clear();
            this.particle_color.add("#FFB81C");
            this.particle_color.add("#FFFFFF");
            this.particle_color.add("#FFFF00");
            this.particle_color.add("#E8FEFD");
            this.particle_color.add("#FF5C00");
            this.particle_color.add("#3CDFFF");
            this.max_particle_color = 3;
            loadedPropertyValues = propertyValues();
            corruptedProperties = true;
            lastLoadedProperties = file.lastModified();
            lastFileSizeProperties = file.length();
            return;
        }

        try (java.io.StringReader r = new java.io.StringReader(ConfigLayoutIO.readText(file.toPath()))) {
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

                if (jsonMap.get("particle_color") instanceof String color) {
                    jsonMap.put("particle_color", List.of(color));
                }
                String updatedJson = GSON.toJson(jsonMap);
                Type type = new TypeToken<PillarParticleConfig>() {}.getType();
                PillarParticleConfig loaded = GSON.fromJson(updatedJson, type);

                if (loaded != null) {
                    this.particle_speed = loaded.particle_speed > 0 ? loaded.particle_speed : this.particle_speed;
                    this.particle_spread = loaded.particle_spread > 0 ? loaded.particle_spread : this.particle_spread;
                    this.particle_lifetime = loaded.particle_lifetime > 0 ? loaded.particle_lifetime : this.particle_lifetime;
                    this.particle_density = jsonMap.containsKey("particle_density")
                            ? Math.max(0, loaded.particle_density) : this.particle_density;
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
                    int requestedColors = jsonMap.containsKey("max_particle_color") && loaded.max_particle_color > 0
                            ? loaded.max_particle_color : numColors;
                    this.max_particle_color = Math.max(1, Math.min(7, Math.min(requestedColors, numColors)));
                }
            }
            originalProperties = original;
            propertiesSource = content.toString();
            loadedPropertyValues = propertyValues();
            corruptedProperties = legacyReadOnly || ConfigLayoutIO.protectInvalidEncoding(file.toPath())
                    || !ConfigLayoutIO.isWritable(file.toPath());
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

    synchronized void loadItemsInternal(boolean vaultLoaded) {
        File file = getItemsFile();
        if (!itemsLoaded) {
            items = new HashSet<>(ConfigPaths.loadDefaultItems(vaultLoaded));
            itemsLoaded = true;
        }
        if (!file.exists()) {
            writeDefaultItems(file, vaultLoaded);
        }
        try {
            String source = ConfigLayoutIO.readText(file.toPath());
            Set<String> parsed = parseItems(source, file.toString());
            String normalized = ConfigLayoutIO.itemsJson(source);
            JsonObject document = GSON.fromJson(normalized, JsonObject.class);
            itemsReadOnly = ConfigLayoutIO.protectInvalidEncoding(file.toPath());
            try {
                ConfigLayoutIO.readProperties(normalized);
            } catch (Exception e) {
                // unparsable properties text only makes the items file read-only
                itemsReadOnly = true;
            }
            // Non-string entries can be ignored in memory, but must not be lost in a rewrite.
            for (JsonElement entry : document.getAsJsonArray("items")) {
                if (!entry.isJsonPrimitive() || !entry.getAsJsonPrimitive().isString()) {
                    itemsReadOnly = true;
                }
            }
            itemsReadOnly |= !ConfigLayoutIO.isWritable(file.toPath());
            if (!itemsReadOnly) {
                try {
                    itemsOnDiskIs405 = isLegacyItems(document);
                    JsonObject merged = mergeDefaultItems(document, vaultLoaded);
                    boolean itemsChanged = !merged.get("items").equals(document.get("items"));
                    boolean inferredProvenance = !document.has("defaults_version")
                            && !document.has("offered_defaults");
                    if (itemsChanged || inferredProvenance && !merged.equals(document)) {
                        String output = ConfigLayoutIO.retainComments(source, GSON.toJson(merged), true);
                        output = ConfigLayoutIO.retainInvalidItemTokens(source, output);
                        ConfigLayoutIO.writeItems(file.toPath(), output, source, itemsOnDiskIs405);
                        itemsOnDiskIs405 = false;
                        source = output;
                        parsed = parseItems(output, file.toString());
                    }
                    document = merged;
                } catch (Exception e) {
                    // merging new default items is best effort; keep the existing file
                    BuildscapeCommon.LOGGER.warn("Unable to merge new default items; retaining the existing file", e);
                }
            }
            itemsSource = source;
            originalItems = document;
            items = new HashSet<>(parsed);
            clearMatchCache();
            corruptedItems = false;
        } catch (Exception e) {
            // the items file may be malformed in any way; keep the last good item list
            corruptedItems = true;
            try { ConfigBackup.copyCorrupted(file.toPath()); } catch (IOException backupFailure) { BuildscapeCommon.LOGGER.warn("Unable to back up unreadable pillar items", backupFailure); }
            BuildscapeCommon.LOGGER.error("Unable to load items config {}; keeping the last good item list", file, e);
        } finally {
            lastLoadedItems = file.lastModified();
            lastFileSizeItems = file.length();
        }
    }

    static JsonObject mergeDefaultItems(JsonObject document, boolean vaultLoaded) throws IOException {
        JsonObject defaults = ConfigPaths
                .loadItemsTemplate(vaultLoaded ? "vault_pillar_items" : "vanilla_pillar_items");
        int defaultsVersion = defaults.get("defaults_version").getAsInt();
        Set<String> offered = new HashSet<>();
        if (document.has("offered_defaults")) {
            if (!document.get("offered_defaults").isJsonArray()) {
                throw new IOException("Invalid offered defaults");
            }
            for (JsonElement entry : document.getAsJsonArray("offered_defaults")) {
                offered.add(entry.getAsString());
            }
        } else if (document.has("defaults_version") && document.get("defaults_version").getAsInt() >= defaultsVersion) {
            // Current files may have been hand-edited before offered defaults were recorded.
            for (JsonElement entry : defaults.getAsJsonArray("items")) {
                offered.add(entry.getAsString());
            }
        } else {
            Set<String> pre405 = parseItems(ConfigPaths.loadItemsTemplate("legacy_pillar_items_pre405").toString());
            Set<String> legacy405 = parseItems(ConfigPaths.loadItemsTemplate("legacy_pillar_items").toString());
            boolean hadVaultItems = document.has("vault_defaults_version")
                    && document.get("vault_defaults_version").getAsInt() >= 405;
            boolean has405OnlyItem = false;
            for (JsonElement entry : document.getAsJsonArray("items")) {
                String item = entry.getAsString();
                if (isVaultItem(item)) {
                    hadVaultItems = true;
                }
                if (legacy405.contains(item) && !pre405.contains(item)) {
                    has405OnlyItem = true;
                }
            }
            // An explicit version takes precedence over guessing from the remaining items.
            boolean had405Defaults = document.has("defaults_version")
                    ? document.get("defaults_version").getAsInt() >= 405
                    : has405OnlyItem;
            // Infer removals only from defaults the old installation could have offered.
            for (String item : had405Defaults ? legacy405 : pre405) {
                if (!isVaultItem(item) || hadVaultItems) {
                    offered.add(item);
                }
            }
        }
        JsonObject merged = document.deepCopy();
        Set<String> present = new HashSet<>();
        for (JsonElement entry : document.getAsJsonArray("items")) {
            present.add(entry.getAsString());
        }
        for (JsonElement entry : defaults.getAsJsonArray("items")) {
            String item = entry.getAsString();
            if (!offered.contains(item) && present.add(item)) {
                merged.getAsJsonArray("items").add(entry.deepCopy());
            }
            offered.add(item);
        }
        merged.add("offered_defaults", GSON.toJsonTree(new TreeSet<>(offered)));
        merged.addProperty("defaults_version", defaultsVersion);
        if (vaultLoaded) {
            merged.addProperty("vault_defaults_version", defaultsVersion);
        }
        return merged;
    }

    private static boolean isVaultItem(String item) {
        return item.startsWith("the_vault:") || item.startsWith("#the_vault:");
    }

    private static boolean isLegacyItems(JsonObject document) throws IOException {
        if (document == null || document.has("offered_defaults")) {
            return false;
        }
        if (document.has("defaults_version")) {
            return document.get("defaults_version").getAsInt() <= 405;
        }
        Set<String> existing = new HashSet<>();
        for (JsonElement entry : document.getAsJsonArray("items")) {
            existing.add(entry.getAsString());
        }
        Set<String> vanilla = new HashSet<>();
        Set<String> vault = new HashSet<>();
        for (String template : new String[]{"legacy_pillar_items", "legacy_pillar_items_pre405"}) {
            vanilla.clear();
            vault.clear();
            for (JsonElement entry : ConfigPaths.loadItemsTemplate(template).getAsJsonArray("items")) {
                String item = entry.getAsString();
                vault.add(item);
                if (!isVaultItem(item)) {
                    vanilla.add(item);
                }
            }
            if (existing.equals(vanilla) || existing.equals(vault)) {
                return true;
            }
        }
        // Use a normal backup when an unversioned file's origin cannot be established.
        return false;
    }

    static Set<String> parseItems(String content) {
        return parseItems(content, ITEMS_FILE_NAME);
    }

    static Set<String> parseItems(String content, String source) {
        JsonObject root;
        try {
            root = GSON.fromJson(ConfigLayoutIO.itemsJson(content), JsonObject.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid items config " + source, e);
        }
        if (root == null || !root.has("items") || !root.get("items").isJsonArray()) {
            throw new IllegalArgumentException("Expected an items array");
        }
        Set<String> parsed = new HashSet<>();
        boolean skippedEntries = false;
        for (JsonElement item : root.getAsJsonArray("items")) {
            if (!item.isJsonPrimitive() || !item.getAsJsonPrimitive().isString()) {
                skippedEntries = true;
                continue;
            }
            parsed.add(item.getAsString());
        }
        if (skippedEntries) {
            BuildscapeCommon.LOGGER.warn("Skipped non-string entries in items array from {}", source);
        }
        return parsed;
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
    public synchronized boolean saveItemsToDisk() {
        if (remote) {
            return false;
        }
        return saveItemsToDisk(Services.PLATFORM.isModLoaded("the_vault"));
    }

    synchronized boolean saveItemsToDisk(boolean vaultLoaded) {
        if (remote) {
            return false;
        }
        clearMatchCache();
        if (corruptedItems || itemsReadOnly) {
            BuildscapeCommon.LOGGER.warn("Not saving read-only items config; unreadable/legacy file kept as-is: {}",
                    getItemsFile());
            return false;
        }
        File file = getItemsFile();
        try {
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            if (originalItems == null && file.exists()) {
                itemsSource = ConfigLayoutIO.readText(file.toPath());
                parseItems(itemsSource, file.toString());
                originalItems = ConfigLayoutIO.readProperties(ConfigLayoutIO.itemsJson(itemsSource));
            }
            JsonObject json = originalItems == null ? new JsonObject() : originalItems.deepCopy();
            json.add("items", GSON.toJsonTree(itemsForSave(originalItems, items)));
            json = mergeDefaultItems(json, vaultLoaded);
            String output = ConfigLayoutIO.retainComments(itemsSource, GSON.toJson(json), true);
            output = ConfigLayoutIO.retainInvalidItemTokens(itemsSource, output);
            ConfigLayoutIO.writeItems(file.toPath(), output, itemsSource,
                    itemsOnDiskIs405 || isLegacyItems(originalItems));
            itemsOnDiskIs405 = false;
            items = new HashSet<>(parseItems(output));
            clearMatchCache();
            itemsSource = output;
            originalItems = json;
            lastLoadedItems = file.lastModified();
            lastFileSizeItems = file.length();
            return true;
        } catch (Exception e) {
            // any save failure is logged and reported to the caller as false
            BuildscapeCommon.LOGGER.error("Error saving items config", e);
            return false;
        }
    }

    private static List<String> itemsForSave(JsonObject original, Collection<String> updated) {
        List<String> existing = new ArrayList<>();
        if (original != null) {
            for (JsonElement entry : original.getAsJsonArray("items")) {
                existing.add(entry.getAsString());
            }
        }
        return orderedItemsForSave(existing, updated);
    }

    /**
     * The list to write: entries already in the file keep their position (and any duplicates, and invalid
     * tokens, are left as they are); removed entries disappear; entries new to the file are appended at the
     * end, sorted among themselves so one batch of additions is written the same way every time.
     */
    static List<String> orderedItemsForSave(List<String> existing, Collection<String> updated) {
        Set<String> wanted = new HashSet<>(updated);
        List<String> values = new ArrayList<>();
        Set<String> present = new HashSet<>();
        for (String item : existing) {
            present.add(item);
            if (wanted.contains(item) || !isValidItemId(item)) {
                values.add(item);
            }
        }
        List<String> added = new ArrayList<>();
        for (String item : wanted) {
            if (!present.contains(item)) {
                added.add(item);
            }
        }
        added.sort(String::compareTo);
        values.addAll(added);
        return values;
    }

    public void saveProperties() {
        savePropertiesToDisk();
    }
    public boolean savePropertiesToDisk() {
        if (remote || corruptedProperties) return false;
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
            if (originalProperties != null && output.equals(originalProperties)) return true;
            String text = ConfigLayoutIO.retainComments(propertiesSource, groupedProperties(output), true);
            ConfigLayoutIO.write(file.toPath(), text, propertiesSource, true);
            propertiesSource = text;
            originalProperties = output;
            loadedPropertyValues = current;
            lastLoadedProperties = file.lastModified();
            lastFileSizeProperties = file.length();
            return true;
        } catch (Exception ignored) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Unable to save pillar properties", ignored);
            return false;
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
        if (!canEditItems() || !isValidItemId(itemId)) {
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
        if (!canEditItems() || !isValidItemId(itemId)) {
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

    public boolean canEditItems() {
        return !corruptedItems && !itemsReadOnly;
    }

    public boolean canEditProperties() {
        return !corruptedProperties;
    }

    public void setServerProtection(boolean itemsProtected, boolean propertiesProtected) {
        itemsReadOnly = itemsProtected;
        corruptedProperties = propertiesProtected;
    }

    public boolean canApplyUpdate() {
        return canApplyUpdate(true, true);
    }

    public boolean canApplyUpdate(boolean updateItems, boolean updateProperties) {
        if ((updateItems && !canEditItems()) || (updateProperties && !canEditProperties())) return false;
        if (remote) return true;
        try {
            File properties = getPropertiesFile();
            File configuredItems = getItemsFile();
            if (updateProperties && properties.exists()) {
                corruptedProperties |= ConfigLayoutIO.protectInvalidEncoding(properties.toPath())
                        || !ConfigLayoutIO.isWritable(properties.toPath());
            }
            if (updateItems && configuredItems.exists()) {
                itemsReadOnly |= ConfigLayoutIO.protectInvalidEncoding(configuredItems.toPath())
                        || !ConfigLayoutIO.isWritable(configuredItems.toPath());
            }
            return (!updateItems || canEditItems()) && (!updateProperties || canEditProperties());
        } catch (IOException | SecurityException e) {
            BuildscapeCommon.LOGGER.warn("Unable to check server config protection", e);
            return false;
        }
    }

    public String getSaveFailureReason() {
        return corruptedProperties || itemsReadOnly || corruptedItems
                ? "the config file is protected because it could not be read safely"
                : "the config file changed on disk or could not be written; check the server log";
    }

    public synchronized void replaceItems(Collection<String> updatedItems) {
        items = new HashSet<>(updatedItems);
        clearMatchCache();
    }

    public synchronized void editItems(Collection<String> updatedItems) {
        if (!canEditItems()) return;
        Set<String> replacement = new HashSet<>();
        for (String item : items) {
            if (!isValidItemId(item)) replacement.add(item);
        }
        for (String item : updatedItems) {
            if (isValidItemId(item)) replacement.add(item);
        }
        items = replacement;
        clearMatchCache();
    }

    static void backupUnreadableFile(File file) {
        java.nio.file.Path backup = file.toPath().resolveSibling(file.getName() + ".bak");
        try {
            if (!java.nio.file.Files.exists(backup)) java.nio.file.Files.copy(file.toPath(), backup);
        } catch (IOException e) {
            BuildscapeCommon.LOGGER.warn("Unable to back up unreadable config {}", file, e);
        }
    }
}
