package com.kingodogo.buildscape.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.client.network.ClientConfigHooks;
import com.kingodogo.buildscape.core.ParticlePatterns;
import com.kingodogo.buildscape.core.io.Json;
import com.kingodogo.buildscape.network.SyncConfigPacket;
import com.kingodogo.buildscape.network.UpdateConfigPacket;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
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
        if (value == null) {
            return EditPermissionMode.STRICT;
        }
        try {
            return EditPermissionMode.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return EditPermissionMode.STRICT;
        }
    }

    public static boolean canEdit(EditPermissionMode mode, int permissionLevel, EditScope scope) {
        if (scope == EditScope.POLICY) {
            return permissionLevel >= CONFIG_PERMISSION_LEVEL;
        }
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
        int permissionLevel = player != null && player.hasPermissions(CONFIG_PERMISSION_LEVEL)
                ? CONFIG_PERMISSION_LEVEL
                : player != null && player.hasPermissions(2) ? 2 : 0;
        return canEdit(mode, permissionLevel, scope);
    }

    public static final String[] DEFAULT_COLORS = {
            "#FFB81C", "#FFFFFF", "#FFFF00", "#E8FEFD", "#FF5C00", "#3CDFFF", "#9B51E0"
    };

    public static int resolveClientPermissionLevel(int syncedLevel, boolean localLevelTwo, boolean localLevelFour) {
        if (syncedLevel >= 0) {
            return Math.min(CONFIG_PERMISSION_LEVEL, syncedLevel);
        }
        return localLevelFour ? CONFIG_PERMISSION_LEVEL : localLevelTwo ? 2 : 0;
    }

    public EditPermissionMode edit_permission_mode = EditPermissionMode.STRICT;

    private static final Gson GSON = Json.PRETTY;
    private static final String PROPERTIES_FILE_NAME = "pillar-properties.json";
    private static final String ITEMS_FILE_NAME = "pillar-items.json";
    private static PillarParticleConfig INSTANCE;

    private static PillarParticleConfig SERVER_CONFIG = null;
    private static final Set<String> WARNED_INVALID_ITEMS = ConcurrentHashMap.newKeySet();

    private long lastLoadedProperties = 0L;
    private long lastFileSizeProperties = 0L;
    private long lastLoadedItems = 0L;
    private long lastFileSizeItems = 0L;

    private static WatchService watchService = null;
    private static Thread watchThread = null;
    private static final AtomicBoolean watcherInitialized = new AtomicBoolean(
            false);
    private static final List<Consumer<Boolean>> configReloadCallbacks = new CopyOnWriteArrayList<>();
    private static long lastNotifyTime = 0;

    private static volatile PillarParticleConfig SNAPSHOT = null;

    public static PillarParticleConfig peek() {
        PillarParticleConfig s = SNAPSHOT;
        return s != null ? s : get();
    }

    private static volatile boolean cachedConnectedToServer = false;
    private static volatile long connectedCacheTime = 0L;
    private static final long CONNECTED_CACHE_TTL_MS = 3000L;

    private static volatile long lastStatCheckTime = 0L;
    private static final long STAT_CHECK_INTERVAL_MS = 2000L;

    public static void addConfigReloadCallback(Consumer<Boolean> callback) {
        configReloadCallbacks.add(callback);
    }

    private static void notifyCallbacks(boolean isRemote) {
        for (Consumer<Boolean> callback : configReloadCallbacks) {
            try {
                callback.accept(isRemote);
            } catch (Exception e) {
                BuildScape.LOGGER.error("Config reload callback failed", e);
            }
        }
        clearMatchCache();
    }

    public volatile Set<String> items = ConcurrentHashMap.newKeySet();
    private boolean itemsLoaded;
    private boolean itemsFileUnreadable;
    private transient boolean itemsReadOnly;
    private transient boolean remote;
    private transient boolean propertiesReadOnly;
    private transient String propertiesSource;
    private transient JsonObject originalProperties;
    private transient JsonObject loadedPropertyValues;
    private transient String itemsSource;
    private transient JsonObject originalItems;
    /** True while the file on disk is still the untouched 4.0.5 file (a load that adds nothing does not rewrite it). */
    private transient boolean itemsOnDiskIs405;
    private final transient Path configDirectory;

    public PillarParticleConfig() {
        this(null);
    }

    PillarParticleConfig(Path configDirectory) {
        this.configDirectory = configDirectory;
    }

    public synchronized void replaceItems(Collection<String> updatedItems) {
        Set<String> replacement = ConcurrentHashMap.newKeySet();
        replacement.addAll(updatedItems);
        for (String item : replacement) {
            if (!isValidItemId(item) && WARNED_INVALID_ITEMS.add(item)) {
                BuildScape.LOGGER.warn("Invalid configured item or tag retained without applying it: {}", item);
            }
        }
        items = replacement;
        clearMatchCache();
    }

    public static boolean isValidItemId(String item) {
        if (item == null) {
            return false;
        }
        String id = item.startsWith("#") ? item.substring(1) : item;
        return !id.isEmpty() && ResourceLocation.isValidResourceLocation(id);
    }

    public Set<String> getInvalidItems() {
        Set<String> invalid = new TreeSet<>();
        for (String item : items) {
            if (!isValidItemId(item)) {
                invalid.add(item);
            }
        }
        return invalid;
    }

    public synchronized void editItems(Collection<String> updatedItems) {
        Set<String> replacement = getInvalidItems();
        for (String item : updatedItems) {
            if (isValidItemId(item)) {
                replacement.add(item);
            }
        }
        replaceItems(replacement);
    }

    static void backupUnreadableFile(File file) {
        Path backup = file.toPath().resolveSibling(file.getName() + ".bak");
        try {
            if (!Files.exists(backup)) {
                Files.copy(file.toPath(), backup);
            }
        } catch (Exception e) {
            BuildScape.LOGGER.error("Unable to back up unreadable config {}", file, e);
        }
    }

    private static final ConcurrentHashMap<Item, Boolean> MATCH_CACHE = new ConcurrentHashMap<>();

    private static void clearMatchCache() {
        MATCH_CACHE.clear();
    }

    @Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static final class TagEvents {
        @SubscribeEvent
        public static void onTagsUpdated(TagsUpdatedEvent event) {
            clearMatchCache();
        }
    }

    public double particle_speed = 0.02;
    public double particle_spread = 0.1;
    public int particle_lifetime = 20;
    public int particle_density = 2;

    public boolean use_pattern = true;
    public String pattern = ParticlePatterns.RING;

    public double pattern_speed = 0.05;
    public double pattern_spread = 0.05;
    public double pattern_intensity = 1.0;

    public volatile List<String> particle_color = new ArrayList<>(Arrays.asList(DEFAULT_COLORS));

    public int max_particle_color = 3;

    private static boolean isClientConnectedToServer() {
        if (FMLEnvironment.dist != Dist.CLIENT) {
            return false;
        }
        long now = System.currentTimeMillis();
        if (now - connectedCacheTime < CONNECTED_CACHE_TTL_MS) {
            return cachedConnectedToServer;
        }
        boolean result = ClientConfigHooks.isConnected();
        cachedConnectedToServer = result;
        connectedCacheTime = now;
        return result;
    }

    public static PillarParticleConfig get() {
        if (isClientConnectedToServer() && SERVER_CONFIG != null) {
            SNAPSHOT = SERVER_CONFIG;
            return SERVER_CONFIG;
        }

        return getLocalConfig();
    }

    public static PillarParticleConfig getServerConfig() {
        return getLocalConfig();
    }

    private static synchronized PillarParticleConfig getLocalConfig() {
        if (INSTANCE == null) {
            INSTANCE = new PillarParticleConfig();
            INSTANCE.loadInternal();
            initializeFileWatcher();
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
                    if (currentModified != INSTANCE.lastLoadedProperties ||
                            currentSize != INSTANCE.lastFileSizeProperties) {
                        reloadProperties = true;
                    }
                } else {
                    INSTANCE.lastLoadedProperties = 0L;
                    INSTANCE.lastFileSizeProperties = 0L;
                }

                if (itemsFile.exists()) {
                    long currentModified = itemsFile.lastModified();
                    long currentSize = itemsFile.length();
                    if (currentModified != INSTANCE.lastLoadedItems ||
                            currentSize != INSTANCE.lastFileSizeItems) {
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

    public static void setServerConfig(
            SyncConfigPacket packet) {
        SERVER_CONFIG = new PillarParticleConfig();
        SERVER_CONFIG.remote = true;
        SERVER_CONFIG.itemsReadOnly = packet.itemsReadOnly;
        SERVER_CONFIG.propertiesReadOnly = packet.propertiesReadOnly;
        SERVER_CONFIG.particle_speed = packet.particle_speed > 0
                ? packet.particle_speed
                : 0.02;
        SERVER_CONFIG.particle_spread = packet.particle_spread > 0
                ? packet.particle_spread
                : 0.1;
        SERVER_CONFIG.particle_lifetime = packet.particle_lifetime > 0
                ? packet.particle_lifetime
                : 20;
        SERVER_CONFIG.particle_density = packet.particle_density >= 0
                ? packet.particle_density
                : 2;
        SERVER_CONFIG.use_pattern = packet.use_pattern;
        SERVER_CONFIG.pattern = packet.pattern != null && !packet.pattern.isEmpty()
                ? packet.pattern
                : ParticlePatterns.RING;
        SERVER_CONFIG.pattern_speed = packet.pattern_speed > 0
                ? packet.pattern_speed
                : 0.05;
        SERVER_CONFIG.pattern_spread = packet.pattern_spread > 0
                ? packet.pattern_spread
                : 0.05;
        SERVER_CONFIG.pattern_intensity = packet.pattern_intensity > 0
                ? packet.pattern_intensity
                : 1.0;

        if (packet.particle_color != null && !packet.particle_color.isEmpty()) {
            SERVER_CONFIG.particle_color = new ArrayList<>(packet.particle_color);
        } else {
            SERVER_CONFIG.particle_color = new ArrayList<>();
            SERVER_CONFIG.particle_color.add("#FFB81C");
            SERVER_CONFIG.particle_color.add("#FFFFFF");
            SERVER_CONFIG.particle_color.add("#FFFF00");
            SERVER_CONFIG.particle_color.add("#E8FEFD");
            SERVER_CONFIG.particle_color.add("#FF5C00");
            SERVER_CONFIG.particle_color.add("#3CDFFF");
        }

        int numColors = SERVER_CONFIG.particle_color.size();
        SERVER_CONFIG.max_particle_color = Math.max(
                1,
                Math.min(
                        7,
                        Math.min(
                                packet.max_particle_color > 0 ? packet.max_particle_color : numColors,
                                numColors)));

        SERVER_CONFIG.replaceItems(packet.items != null ? packet.items : Collections.emptySet());
        SERVER_CONFIG.edit_permission_mode = packet.edit_permission_mode != null
                ? packet.edit_permission_mode
                : EditPermissionMode.STRICT;

        clearMatchCache();
        notifyCallbacks(true);
        SNAPSHOT = SERVER_CONFIG;
    }

    private static void initializeFileWatcher() {
        if (FMLEnvironment.dist != Dist.CLIENT || !watcherInitialized.compareAndSet(false, true)) {
            return;
        }

        try {
            watchService = FileSystems.getDefault().newWatchService();
            File configDir = INSTANCE.getConfigDir();
            Path configPath = configDir.toPath();

            configPath.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_CREATE);

            watchThread = new Thread(
                    () -> {
                        try {
                            while (true) {
                                WatchKey key = watchService.take();

                                for (WatchEvent<?> event : key.pollEvents()) {
                                    WatchEvent.Kind<?> kind = event.kind();

                                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                                        continue;
                                    }

                                    @SuppressWarnings("unchecked")
                                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                                    Path filename = ev.context();

                                    if (filename.toString().equals(PROPERTIES_FILE_NAME) ||
                                            filename.toString().equals(ITEMS_FILE_NAME)) {

                                        Thread.sleep(100);

                                        if (INSTANCE != null) {
                                            boolean wasProperties = filename
                                                    .toString()
                                                    .equals(PROPERTIES_FILE_NAME);
                                            boolean wasItems = filename
                                                    .toString()
                                                    .equals(ITEMS_FILE_NAME);

                                            boolean reloaded = false;
                                            if (wasProperties) {
                                                INSTANCE.loadPropertiesInternal();
                                                reloaded = true;
                                            }
                                            if (wasItems) {
                                                INSTANCE.loadItemsInternal();
                                                reloaded = true;
                                            }

                                            if (reloaded) {
                                                long currentTime = System.currentTimeMillis();
                                                if (currentTime - lastNotifyTime > 500) {
                                                    lastNotifyTime = currentTime;
                                                    if (!configReloadCallbacks.isEmpty()) {
                                                        notifyCallbacks(false);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                boolean valid = key.reset();
                                if (!valid) {
                                    break;
                                }
                            }
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        } catch (Exception e) {
                            BuildScape.LOGGER.error("Error in config file watcher", e);
                        }
                    },
                    "BuildScape-ConfigWatcher");

            watchThread.setDaemon(true);
            watchThread.start();
        } catch (Exception e) {
            watcherInitialized.set(false);
        }
    }

    public static void clearServerConfig() {
        SERVER_CONFIG = null;
        SNAPSHOT = INSTANCE;
        clearMatchCache();
    }

    private File getPropertiesFile() {
        return new File(getConfigDir(), PROPERTIES_FILE_NAME);
    }

    private File getItemsFile() {
        return new File(getConfigDir(), ITEMS_FILE_NAME);
    }

    private File getConfigDir() {
        File dir = (configDirectory != null ? configDirectory : Paths.get("config", "buildscape", "pillar")).toFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private void writeDefaultProperties(File f) {
        if (ConfigPaths.copyTemplateIfMissing(f.toPath(), "/data/buildscape/config/default_pillar_properties.json")) {
            return;
        }
        try {
            PillarParticleConfig defaults = new PillarParticleConfig();
            defaults.use_pattern = true;
            defaults.particle_color = new ArrayList<>(List.of(
                    "#FFB81C", "#FFFFFF", "#FFFF00", "#E8FEFD", "#FF5C00", "#3CDFFF"));
            ConfigLayoutIO.write(f.toPath(), groupedProperties(defaults.propertyValues()), null, false);
        } catch (Exception e) {
            BuildScape.LOGGER.error("Unable to create default pillar properties", e);
        }
    }

    private void writeDefaultItems(File f, boolean vaultLoaded) {
        String template = vaultLoaded ? "vault_pillar_items.json" : "vanilla_pillar_items.json";
        ConfigPaths.copyTemplateIfMissing(f.toPath(), "/data/buildscape/config/" + template);
    }

    private static String stripComments(String json) {
        StringBuilder result = new StringBuilder();
        boolean inString = false;
        boolean escaped = false;

        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);

            if (escaped) {
                result.append(c);
                escaped = false;
                continue;
            }

            if (c == '\\') {
                escaped = true;
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
                    while (i + 1 < json.length() &&
                            json.charAt(i + 1) != '\n' &&
                            json.charAt(i + 1) != '\r') {
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
        File propertiesFile = getPropertiesFile();
        File itemsFile = getItemsFile();
        boolean migrationComplete = migrateOldConfig();

        if (migrationComplete || propertiesFile.exists()) {
            loadPropertiesInternal();
        } else {
            propertiesReadOnly = true;
        }
        if (migrationComplete || itemsFile.exists()) {
            loadItemsInternal();
        } else {
            itemsFileUnreadable = true;
        }
    }

    private synchronized void loadPropertiesInternal() {
        File file = getPropertiesFile();
        if (!file.exists()) {
            writeDefaultProperties(file);
        }
        try {
            propertiesSource = ConfigLayoutIO.readText(file.toPath());
            propertiesReadOnly = ConfigLayoutIO.protectInvalidEncoding(file.toPath())
                    || !ConfigLayoutIO.isWritable(file.toPath());
            try {
                originalProperties = ConfigLayoutIO.readProperties(propertiesSource);
            } catch (Exception e) {
                propertiesReadOnly = true;
                originalProperties = null;
            }
            Type mapType = new TypeToken<Map<String, Object>>() {
            }.getType();
            Map<String, Object> jsonMap = GSON.fromJson(stripComments(propertiesSource), mapType);
            if (jsonMap == null) {
                throw new IllegalArgumentException("Expected pillar properties");
            }
            if (jsonMap != null) {
                if (jsonMap.containsKey("use_template") &&
                        !jsonMap.containsKey("use_pattern")) {
                    jsonMap.put("use_pattern", jsonMap.get("use_template"));
                }
                if (jsonMap.containsKey("template") && !jsonMap.containsKey("pattern")) {
                    jsonMap.put("pattern", jsonMap.get("template"));
                }
                if (jsonMap.containsKey("template_speed") &&
                        !jsonMap.containsKey("pattern_speed")) {
                    jsonMap.put("pattern_speed", jsonMap.get("template_speed"));
                }
                if (jsonMap.containsKey("template_spread") &&
                        !jsonMap.containsKey("pattern_spread")) {
                    jsonMap.put("pattern_spread", jsonMap.get("template_spread"));
                }
                if (jsonMap.containsKey("template_intensity") &&
                        !jsonMap.containsKey("pattern_intensity")) {
                    jsonMap.put("pattern_intensity", jsonMap.get("template_intensity"));
                }

                // The legacy single-color form still has the same meaning as a one-element palette.
                if (jsonMap.get("particle_color") instanceof String color) {
                    jsonMap.put("particle_color", List.of(color));
                }
                String updatedJson = GSON.toJson(jsonMap);
                Type type = new TypeToken<PillarParticleConfig>() {
                }.getType();
                PillarParticleConfig loaded = GSON.fromJson(updatedJson, type);

                if (loaded != null) {
                    this.particle_speed = loaded.particle_speed > 0
                            ? loaded.particle_speed
                            : this.particle_speed;
                    this.particle_spread = loaded.particle_spread > 0
                            ? loaded.particle_spread
                            : this.particle_spread;
                    this.particle_lifetime = loaded.particle_lifetime > 0
                            ? loaded.particle_lifetime
                            : this.particle_lifetime;
                    this.particle_density = jsonMap.containsKey("particle_density")
                            ? Math.max(0, loaded.particle_density)
                            : this.particle_density;
                    this.use_pattern = loaded.use_pattern;
                    this.pattern = loaded.pattern != null ? loaded.pattern : ParticlePatterns.RING;
                    this.pattern_speed = loaded.pattern_speed > 0
                            ? loaded.pattern_speed
                            : this.pattern_speed;
                    this.pattern_spread = loaded.pattern_spread > 0
                            ? loaded.pattern_spread
                            : this.pattern_spread;
                    this.pattern_intensity = loaded.pattern_intensity > 0
                            ? loaded.pattern_intensity
                            : this.pattern_intensity;

                    if (jsonMap.containsKey("edit_permission_mode")) {
                        this.edit_permission_mode = parseEditPermissionMode(
                                String.valueOf(jsonMap.get("edit_permission_mode")));
                    } else if (loaded.edit_permission_mode != null) {
                        this.edit_permission_mode = loaded.edit_permission_mode;
                    }

                    List<String> rawColors = null;
                    if (loaded.particle_color != null && !loaded.particle_color.isEmpty()) {
                        rawColors = loaded.particle_color;
                    } else {
                        if (jsonMap.containsKey("particle_color")) {
                            Object colorObj = jsonMap.get("particle_color");
                            if (colorObj instanceof String string) {
                                rawColors = new ArrayList<>();
                                rawColors.add(string);
                            } else if (colorObj instanceof List) {
                                rawColors = (List<String>) colorObj;
                            }
                        }
                    }

                    List<String> newColors = new ArrayList<>();
                    if (rawColors != null && !rawColors.isEmpty()) {
                        for (String color : rawColors) {
                            if (color != null && color.matches("^#[0-9A-Fa-f]{6}$")) {
                                newColors.add(color.toUpperCase(Locale.ROOT));
                            }
                        }
                    }

                    if (newColors.isEmpty()) {
                        newColors.add("#FFB81C");
                        newColors.add("#FFFFFF");
                        newColors.add("#FFFF00");
                    }

                    if (newColors.size() > 7) {
                        newColors = new ArrayList<>(
                                newColors.subList(0, 7));
                    }

                    if (newColors.isEmpty()) {
                        newColors.add("#FFB81C");
                        newColors.add("#FFFFFF");
                        newColors.add("#FFFF00");
                    }

                    this.particle_color = newColors;

                    int numColors = this.particle_color.size();
                    this.max_particle_color = numColors;

                    boolean maxColorExplicitlySet = jsonMap.containsKey(
                            "max_particle_color");
                    if (maxColorExplicitlySet && loaded.max_particle_color > 0) {
                        int requestedMax = loaded.max_particle_color;
                        if (requestedMax > 0 && requestedMax <= 7 && requestedMax <= numColors) {
                            this.max_particle_color = requestedMax;
                        } else if (requestedMax > numColors) {
                            this.max_particle_color = numColors;
                        }
                    }
                    this.max_particle_color = Math.max(
                            1,
                            Math.min(7, Math.min(this.max_particle_color, numColors)));
                }
            }
            loadedPropertyValues = propertyValues();
        } catch (Exception e) {
            propertiesReadOnly = true;
            backupUnreadableFile(file);
            BuildScape.LOGGER.error("Unable to load pillar properties; retaining the original file", e);
        } finally {
            lastLoadedProperties = file.lastModified();
            lastFileSizeProperties = file.length();
        }
    }

    private synchronized void loadItemsInternal() {
        loadItemsInternal(ModList.get().isLoaded("the_vault"));
    }

    synchronized void loadItemsInternal(boolean vaultLoaded) {
        File file = getItemsFile();
        if (!itemsLoaded) {
            replaceItems(ConfigPaths.loadDefaultItems(vaultLoaded));
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
                    if (!merged.get("items").equals(document.get("items"))) {
                        String output = ConfigLayoutIO.retainComments(source, GSON.toJson(merged), true);
                        output = ConfigLayoutIO.retainInvalidItemTokens(source, output);
                        ConfigLayoutIO.writeItems(file.toPath(), output, source, itemsOnDiskIs405);
                        itemsOnDiskIs405 = false;
                        source = output;
                        parsed = parseItems(output, file.toString());
                    }
                    document = merged;
                } catch (Exception e) {
                    BuildScape.LOGGER.warn("Unable to merge new default items; retaining the existing file", e);
                }
            }
            itemsSource = source;
            originalItems = document;
            replaceItems(parsed);
            itemsFileUnreadable = false;
        } catch (Exception e) {
            itemsFileUnreadable = true;
            backupUnreadableFile(file);
            BuildScape.LOGGER.error("Unable to load items config {}; keeping the last good item list", file, e);
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
            boolean hadVaultItems = false;
            for (JsonElement entry : document.getAsJsonArray("items")) {
                if (isVaultItem(entry.getAsString())) {
                    hadVaultItems = true;
                }
            }
            // Infer removals only from defaults the old installation could have offered.
            for (JsonElement entry : ConfigPaths.loadItemsTemplate("legacy_pillar_items").getAsJsonArray("items")) {
                String item = entry.getAsString();
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
        for (JsonElement entry : ConfigPaths.loadItemsTemplate("legacy_pillar_items").getAsJsonArray("items")) {
            String item = entry.getAsString();
            vault.add(item);
            if (!isVaultItem(item)) {
                vanilla.add(item);
            }
        }
        // Use a normal backup when an unversioned file's origin cannot be established.
        return existing.equals(vanilla) || existing.equals(vault);
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
            BuildScape.LOGGER.warn("Skipped non-string entries in items array from {}", source);
        }
        return parsed;
    }

    private boolean migrateOldConfig() {
        Path oldFile = configDirectory == null
                ? Paths.get("config", "buildscape", "pillar-particles.json")
                : configDirectory.resolveSibling("pillar-particles.json");
        Path backup = oldFile.resolveSibling("pillar-particles.json.backup");
        Path completion = oldFile.resolveSibling("pillar-particles.json.backup.complete");
        if (!Files.exists(oldFile) || Files.exists(completion)) {
            return true;
        }
        try {
            String source = ConfigLayoutIO.readText(oldFile);
            JsonObject old = GSON.fromJson(ConfigLayoutIO.itemsJson(source), JsonObject.class);
            if (old == null) {
                return false;
            }
            boolean replacementDecoded = ConfigLayoutIO.protectInvalidEncoding(oldFile);
            boolean hasItems = old.has("items") && !old.get("items").isJsonNull();
            try {
                File itemsFile = getItemsFile();
                if (!itemsFile.exists() && hasItems) {
                    parseItems(source, oldFile.toString());
                    JsonObject itemValues = new JsonObject();
                    itemValues.add("items", old.get("items"));
                    if (replacementDecoded) {
                        // Copy the original bytes; the loader will keep this migrated file read-only.
                        Files.copy(oldFile, itemsFile.toPath());
                    } else {
                        ConfigLayoutIO.write(itemsFile.toPath(),
                                ConfigLayoutIO.retainComments(source, GSON.toJson(itemValues), true), null, false);
                    }
                }
            } catch (Exception e) {
                BuildScape.LOGGER.error("Unable to migrate legacy pillar items; retaining the original", e);
            }
            try {
                File propertiesFile = getPropertiesFile();
                if (!propertiesFile.exists()) {
                    JsonObject properties = old.deepCopy();
                    properties.remove("items");
                    if (replacementDecoded) {
                        Files.copy(oldFile, propertiesFile.toPath());
                    } else {
                        ConfigLayoutIO.write(propertiesFile.toPath(),
                                ConfigLayoutIO.retainComments(source, groupedProperties(properties), true), null,
                                false);
                    }
                }
            } catch (Exception e) {
                BuildScape.LOGGER.error("Unable to migrate legacy pillar properties; retaining the original", e);
            }
            boolean migrated = getPropertiesFile().exists() && (getItemsFile().exists() || !hasItems);
            if (migrated) {
                if (!Files.exists(backup)) {
                    Files.copy(oldFile, backup);
                }
                Files.createFile(completion);
            }
            return migrated;
        } catch (Exception e) {
            BuildScape.LOGGER.error("Unable to migrate legacy pillar config; retaining the original", e);
            return false;
        }
    }
    public static void forceReload() {
        if (INSTANCE != null) {
            INSTANCE.loadInternal();
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
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(
                stack.getItem());
        if (id != null && items.contains(id.toString())) {
            return true;
        }

        for (String itemOrTag : items) {
            if (itemOrTag != null && itemOrTag.startsWith("#")) {
                String tagString = itemOrTag.substring(1);
                try {
                    ResourceLocation tagLocation = new ResourceLocation(tagString);
                    TagKey<Item> tagKey = TagKey.create(
                            Registry.ITEM_REGISTRY,
                            tagLocation);

                    if (stack.getItem().builtInRegistryHolder().is(tagKey)) {
                        return true;
                    }
                } catch (Exception e) {
                    // invalid tag id; treat it as not matching
                }
            }
        }

        return false;
    }

    public void saveItems() {
        if (!canEditItems()) {
            BuildScape.LOGGER.warn("Item edit refused because the config file is read-only");
            return;
        }
        clearMatchCache();
        if (this == SERVER_CONFIG && isClientConnectedToServer()) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                    () -> () -> {
                        if (ClientConfigHooks.canUploadConfig()) {
                            UpdateConfigPacket.send(this);
                        }
                    });
            return;
        }
        saveItemsToDisk();
    }

    public synchronized boolean saveItemsToDisk() {
        if (remote) {
            return false;
        }
        return saveItemsToDisk(ModList.get().isLoaded("the_vault"));
    }

    synchronized boolean saveItemsToDisk(boolean vaultLoaded) {
        if (remote) {
            return false;
        }
        clearMatchCache();
        if (itemsFileUnreadable || itemsReadOnly) {
            BuildScape.LOGGER.warn("Not saving read-only items config; unreadable/legacy file kept as-is: {}",
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
            replaceItems(parseItems(output));
            itemsSource = output;
            originalItems = json;
            lastLoadedItems = file.lastModified();
            lastFileSizeItems = file.length();
            return true;
        } catch (Exception e) {
            BuildScape.LOGGER.error("Error saving items config", e);
            return false;
        }
    }

    public void saveProperties() {
        if (!canEditProperties()) {
            BuildScape.LOGGER.warn("Particle edit refused because the config file is read-only");
            return;
        }
        if (this == SERVER_CONFIG && isClientConnectedToServer()) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                    () -> () -> {
                        if (ClientConfigHooks.canUploadConfig()) {
                            UpdateConfigPacket.send(this);
                        }
                    });
            return;
        }
        savePropertiesToDisk();
    }

    public synchronized boolean savePropertiesToDisk() {
        if (remote) {
            return false;
        }
        if (propertiesReadOnly) {
            BuildScape.LOGGER.warn("Not saving read-only pillar properties; unreadable/legacy file kept as-is: {}",
                    getPropertiesFile());
            return false;
        }
        File file = getPropertiesFile();
        try {
            if (originalProperties == null && file.exists()) {
                // Also protect explicit saves made before the first load.
                String source = ConfigLayoutIO.readText(file.toPath());
                if (ConfigLayoutIO.protectInvalidEncoding(file.toPath())) {
                    propertiesReadOnly = true;
                    BuildScape.LOGGER.warn(
                            "Not saving read-only pillar properties; replacement-decoded file kept as-is: {}", file);
                    return false;
                }
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
                boolean changed = loadedPropertyValues == null
                        || !entry.getValue().equals(loadedPropertyValues.get(key));
                if ("edit_permission_mode".equals(key) && !output.has(key) && !changed && originalProperties != null) {
                    continue;
                }
                if (changed || !output.has(key)) {
                    String alias = key.equals("use_pattern") ? "use_template" : key.replace("pattern", "template");
                    output.add(key, !changed && output.has(alias) ? output.get(alias) : entry.getValue());
                }
            }
            String text = ConfigLayoutIO.retainComments(propertiesSource, groupedProperties(output), true);
            ConfigLayoutIO.write(file.toPath(), text, propertiesSource, true);
            propertiesSource = text;
            originalProperties = output;
            loadedPropertyValues = current;
            lastLoadedProperties = file.lastModified();
            lastFileSizeProperties = file.length();
            return true;
        } catch (Exception ignored) {
            BuildScape.LOGGER.warn("Unable to save pillar properties", ignored);
            return false;
        }
    }

    public String getSaveFailureReason() {
        return propertiesReadOnly || itemsReadOnly || itemsFileUnreadable
                ? "the server's config file is read-only because an unreadable, replacement-decoded or legacy file was kept as-is"
                : "the server's config file changed on disk or could not be written; check the server log";
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

    public boolean canEditItems() {
        if (remote && FMLEnvironment.dist == Dist.CLIENT && ClientConfigHooks.hasLocalServer()) {
            return getLocalConfig().canEditItems();
        }
        return !itemsFileUnreadable && !itemsReadOnly;
    }

    public boolean canEditProperties() {
        if (remote && FMLEnvironment.dist == Dist.CLIENT && ClientConfigHooks.hasLocalServer()) {
            return getLocalConfig().canEditProperties();
        }
        return !propertiesReadOnly;
    }

    public boolean canApplyUpdate() {
        return canApplyUpdate(true, true);
    }

    public boolean canApplyUpdate(boolean items, boolean properties) {
        if ((properties && !canEditProperties()) || (items && !canEditItems())) {
            return false;
        }
        if (remote) {
            return true;
        }
        try {
            File propertiesFile = getPropertiesFile();
            File itemsFile = getItemsFile();
            if (properties && propertiesFile.exists()) {
                propertiesReadOnly |= ConfigLayoutIO.protectInvalidEncoding(propertiesFile.toPath())
                        || !ConfigLayoutIO.isWritable(propertiesFile.toPath());
            }
            if (items && itemsFile.exists()) {
                itemsReadOnly |= ConfigLayoutIO.protectInvalidEncoding(itemsFile.toPath())
                        || !ConfigLayoutIO.isWritable(itemsFile.toPath());
            }
            return (!properties || canEditProperties()) && (!items || canEditItems());
        } catch (IOException e) {
            BuildScape.LOGGER.warn("Unable to check server config protection", e);
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
        values.addProperty("edit_permission_mode",
                edit_permission_mode != null ? edit_permission_mode.name().toLowerCase(Locale.ROOT) : "strict");
        return values;
    }

    private static String groupedProperties(JsonObject values) {
        StringBuilder text = new StringBuilder("{\n"
                + "  // Basic parameters apply when use_pattern = false; patterns use pattern_* values instead.\n"
                + "  // Right-click a pillar with dyes to override config colors for that pillar (up to 5 dyes).\n"
                + "  // Dye colors cycle in application order; break and replace the pillar to reset them.\n"
                + "  // Config colors cycle from the first color through max_particle_color.\n");
        Set<String> written = new HashSet<>();
        appendProperties(text, values, written, "Visuals", new String[][]{
                {"particle_color", "Hexadecimal particle color palette (up to 7 colors)"},
                {"max_particle_color", "Maximum number of colors to use from particle_color (1-7)"},
                {"particle_spread", "How far particles spread from center when use_pattern = false"}});
        appendProperties(text, values, written, "Timing & patterns", new String[][]{
                {"particle_speed", "Basic particle parameters (used when use_pattern = false): base speed multiplier"},
                {"particle_lifetime", "How long particles last (in ticks, 20 ticks = 1 second)"},
                {"pattern", "Available patterns: beam, spiral, fountain, pulse, ring, burst"},
                {"pattern_speed", "Base speed multiplier for pattern particles"},
                {"pattern_spread", "Spread amount for pattern particles"},
                {"pattern_intensity", "Intensity multiplier for pattern effects (higher = more intense)"}});
        appendProperties(text, values, written, "Behaviour", new String[][]{
                {"use_pattern", "Pattern system (overrides basic particle parameters)"},
                {"particle_density", "Number of particles spawned per tick when use_pattern = false"}});
        boolean otherHeading = false;
        // Sort unknown keys as well, so output remains stable regardless of input key order.
        for (String key : new TreeSet<>(values.keySet())) {
            if (!written.contains(key)) {
                if (!otherHeading) {
                    text.append("\n  // --- Other ---\n");
                    otherHeading = true;
                }
                text.append("  // Unrecognized setting retained with its original value\n");
                appendProperty(text, key, values.get(key));
            }
        }
        int comma = text.lastIndexOf(",\n");
        if (comma >= 0) {
            text.deleteCharAt(comma);
        }
        return text.append("}\n").toString();
    }

    private static void appendProperties(StringBuilder text, JsonObject values, Set<String> written,
            String section, String[][] entries) {
        text.append("\n  // --- ").append(section).append(" ---\n");
        for (String[] entry : entries) {
            written.add(entry[0]);
            if (!values.has(entry[0])) {
                continue;
            }
            text.append("  // ").append(entry[1]).append('\n');
            appendProperty(text, entry[0], values.get(entry[0]));
        }
    }

    private static void appendProperty(StringBuilder text, String key, JsonElement value) {
        text.append("  ").append(GSON.toJson(key)).append(": ").append(GSON.toJson(value)).append(",\n");
    }

    public boolean addItem(String itemId) {
        if (!canEditItems()) {
            return false;
        }
        if (!isValidItemId(itemId)) {
            return false;
        }
        if (items.add(itemId)) {
            clearMatchCache();
            saveItems();
            return true;
        }
        return false;
    }

    public boolean removeItem(String itemId) {
        if (!canEditItems()) {
            return false;
        }
        if (!isValidItemId(itemId)) {
            return false;
        }
        if (items.remove(itemId)) {
            clearMatchCache();
            saveItems();
            return true;
        }
        return false;
    }

    public static void addItemToConfig(String itemId) {
        PillarParticleConfig config = get();
        config.addItem(itemId);
    }
}
