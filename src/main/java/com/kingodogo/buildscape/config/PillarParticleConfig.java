package com.kingodogo.buildscape.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.kingodogo.buildscape.BuildScape;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class PillarParticleConfig {
    public static final int CONFIG_PERMISSION_LEVEL = 4;


    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    private static final String PROPERTIES_FILE_NAME = "pillar-properties.json";
    private static final String ITEMS_FILE_NAME = "pillar-items.json";
    private static PillarParticleConfig INSTANCE;

    private static PillarParticleConfig SERVER_CONFIG = null;

    private long lastLoadedProperties = 0L;
    private long lastFileSizeProperties = 0L;
    private long lastLoadedItems = 0L;
    private long lastFileSizeItems = 0L;

    private static WatchService watchService = null;
    private static Thread watchThread = null;
    private static final AtomicBoolean watcherInitialized = new AtomicBoolean(
            false);
    private static final List<java.util.function.Consumer<Boolean>> configReloadCallbacks = new java.util.concurrent.CopyOnWriteArrayList<>();
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

    public static void addConfigReloadCallback(java.util.function.Consumer<Boolean> callback) {
        configReloadCallbacks.add(callback);
    }

    private static void notifyCallbacks(boolean isRemote) {
        for (java.util.function.Consumer<Boolean> callback : configReloadCallbacks) {
            try {
                callback.accept(isRemote);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        clearMatchCache();
    }

    public volatile Set<String> items = java.util.concurrent.ConcurrentHashMap.newKeySet();
    private boolean itemsLoaded;
    private boolean itemsFileUnreadable;
    private transient boolean remote;
    private transient boolean propertiesReadOnly;
    private transient String propertiesSource;
    private transient JsonObject originalProperties;
    private transient JsonObject loadedPropertyValues;
    private transient String itemsSource;
    private transient JsonObject originalItems;
    private final transient Path configDirectory;

    public PillarParticleConfig() {
        this(null);
    }

    PillarParticleConfig(Path configDirectory) {
        this.configDirectory = configDirectory;
    }

    public synchronized void replaceItems(Collection<String> updatedItems) {
        Set<String> replacement = java.util.concurrent.ConcurrentHashMap.newKeySet();
        replacement.addAll(updatedItems);
        items = replacement;
        clearMatchCache();
    }

    static void backupUnreadableFile(File file) {
        Path backup = file.toPath().resolveSibling(file.getName() + ".bak");
        try {
            if (!Files.exists(backup)) {
                Files.copy(file.toPath(), backup);
            }
        } catch (Exception e) {
            BuildScape.LOGGER.error("Unable to back up unreadable config " + file, e);
        }
    }

    private static final java.util.concurrent.ConcurrentHashMap<net.minecraft.world.item.Item, Boolean> MATCH_CACHE = new java.util.concurrent.ConcurrentHashMap<>();

    private static void clearMatchCache() {
        MATCH_CACHE.clear();
    }

    public double particle_speed = 0.02;
    public double particle_spread = 0.1;
    public int particle_lifetime = 20;
    public int particle_density = 2;

    public boolean use_pattern = false;
    public String pattern = "ring";

    public double pattern_speed = 0.05;
    public double pattern_spread = 0.05;
    public double pattern_intensity = 1.0;

    public volatile List<String> particle_color = new ArrayList<>();

    public int max_particle_color = 3;

    private static boolean isClientConnectedToServer() {
        if (FMLEnvironment.dist != Dist.CLIENT) {
            return false;
        }
        long now = System.currentTimeMillis();
        if (now - connectedCacheTime < CONNECTED_CACHE_TTL_MS) {
            return cachedConnectedToServer;
        }
        boolean result = ClientConnectionState.isConnected();
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
            com.kingodogo.buildscape.network.SyncConfigPacket packet) {
        SERVER_CONFIG = new PillarParticleConfig();
        SERVER_CONFIG.remote = true;
        SERVER_CONFIG.particle_speed = packet.particle_speed > 0
                ? packet.particle_speed
                : 0.02;
        SERVER_CONFIG.particle_spread = packet.particle_spread > 0
                ? packet.particle_spread
                : 0.1;
        SERVER_CONFIG.particle_lifetime = packet.particle_lifetime > 0
                ? packet.particle_lifetime
                : 20;
        SERVER_CONFIG.particle_density = packet.particle_density > 0
                ? packet.particle_density
                : 2;
        SERVER_CONFIG.use_pattern = packet.use_pattern;
        SERVER_CONFIG.pattern = packet.pattern != null && !packet.pattern.isEmpty()
                ? packet.pattern
                : "ring";
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

        clearMatchCache();
        notifyCallbacks(true);
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
                            BuildScape.LOGGER.error("Error in config file watcher: " + e.getMessage());
                        }
                    },
                    "BuildScape-ConfigWatcher");

            watchThread.setDaemon(true);
            watchThread.start();
        } catch (Exception e) {
            watcherInitialized.set(false);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static final class ClientConnectionState {
        private static boolean isConnected() {
            net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
            return minecraft.getConnection() != null && minecraft.level != null;
        }
    }

    public static void clearServerConfig() {
        SERVER_CONFIG = null;
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
        if (!dir.exists())
            dir.mkdirs();
        return dir;
    }

    private void writeDefaultProperties(File f) {
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

    private void writeDefaultItems(File f) {
        try {
            File parentDir = f.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (FileWriter writer = new FileWriter(f)) {
                java.io.StringWriter grouped = new java.io.StringWriter();
                DefaultPillarItems.writeGrouped(grouped, net.minecraftforge.fml.ModList.get().isLoaded("the_vault"));
                String layout = grouped.toString();
                writer.write(layout.substring(0, layout.lastIndexOf('}')));
                writer.write("  // MOB SPAWN EGGS:\n");
                writer.write(
                        "  // Spawn eggs (e.g., creeper_spawn_egg, zombie_spawn_egg) render the actual entity/mob on the pillar\n");
                writer.write(
                        "  // When you place a spawn egg on a pillar, the mob will face the direction you were looking\n");
                writer.write(
                        "  // The mob is displayed on top of the pillar and will spawn particles like other items\n");
                writer.write("  // \n");
                writer.write("  // MAKING MOBS SPIN:\n");
                writer.write(
                        "  // To make a mob spin continuously, rename the spawn egg to \"spin\" using an anvil\n");
                writer.write(
                        "  // The name must be exactly \"spin\" (case-insensitive, so \"Spin\" or \"SPIN\" also works)\n");
                writer.write(
                        "  // Mobs with \"spin\" name tag will rotate continuously (40% slower than regular items)\n");
                writer.write(
                        "  // Mobs without \"spin\" name tag will stay facing the direction you were looking when placed\n");
                writer.write("  // \n");
                writer.write("  // ROTATING MOBS:\n");
                writer.write(
                        "  // You can rotate mobs by Shift+Right-Clicking the pillar (rotates 180 degrees each time)\n");
                writer.write(
                        "  // This works for both spinning and non-spinning mobs\n");
                writer.write("}\n");
            }
        } catch (Exception ignored) {
        }
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
        if (!propertiesFile.exists() || !itemsFile.exists()) {
            migrateOldConfig();
        }

        loadPropertiesInternal();
        loadItemsInternal();
    }

    private synchronized void loadPropertiesInternal() {
        File file = getPropertiesFile();
        if (!file.exists()) {
            writeDefaultProperties(file);
        }
        try {
            propertiesSource = ConfigLayoutIO.readText(file.toPath());
            propertiesReadOnly = false;
            try {
                originalProperties = ConfigLayoutIO.readProperties(propertiesSource);
            } catch (Exception e) {
                propertiesReadOnly = true;
                originalProperties = null;
            }
            Type mapType = new TypeToken<Map<String, Object>>() { }.getType();
            Map<String, Object> jsonMap = GSON.fromJson(stripComments(propertiesSource), mapType);
            if (jsonMap == null) throw new IllegalArgumentException("Expected pillar properties");
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
                    this.particle_density = loaded.particle_density > 0
                            ? loaded.particle_density
                            : this.particle_density;
                    this.use_pattern = loaded.use_pattern;
                    this.pattern = loaded.pattern != null ? loaded.pattern : "ring";
                    this.pattern_speed = loaded.pattern_speed > 0
                            ? loaded.pattern_speed
                            : this.pattern_speed;
                    this.pattern_spread = loaded.pattern_spread > 0
                            ? loaded.pattern_spread
                            : this.pattern_spread;
                    this.pattern_intensity = loaded.pattern_intensity > 0
                            ? loaded.pattern_intensity
                            : this.pattern_intensity;

                    List<String> rawColors = null;
                    if (loaded.particle_color != null && !loaded.particle_color.isEmpty()) {
                        rawColors = loaded.particle_color;
                    } else {
                        if (jsonMap.containsKey("particle_color")) {
                            Object colorObj = jsonMap.get("particle_color");
                            if (colorObj instanceof String) {
                                rawColors = new ArrayList<>();
                                rawColors.add((String) colorObj);
                            } else if (colorObj instanceof List) {
                                rawColors = (List<String>) colorObj;
                            }
                        }
                    }

                    List<String> newColors = new ArrayList<>();
                    if (rawColors != null && !rawColors.isEmpty()) {
                        for (String color : rawColors) {
                            if (color != null && color.matches("^#[0-9A-Fa-f]{6}$")) {
                                newColors.add(color.toUpperCase());
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
        File file = getItemsFile();
        if (!itemsLoaded) {
            initializeDefaultItems();
            itemsLoaded = true;
        }
        if (!file.exists()) {
            writeDefaultItems(file);
            initializeDefaultItems();
        }
        try {
            String source = ConfigLayoutIO.readText(file.toPath());
            Set<String> parsed = parseItems(source, file.toString());
            JsonObject document = GSON.fromJson(stripComments(source), JsonObject.class);
            boolean migrated = DefaultPillarItems.merge405(document, parsed,
                    net.minecraftforge.fml.ModList.get().isLoaded("the_vault"));
            if (migrated) {
                List<String> sorted = new ArrayList<>(parsed);
                sorted.sort(String::compareTo);
                document.add("items", GSON.toJsonTree(sorted));
                String output = ConfigLayoutIO.retainComments(source, GSON.toJson(document), true);
                try {
                    ConfigLayoutIO.write(file.toPath(), output, source, true);
                    source = output;
                } catch (Exception e) {
                    BuildScape.LOGGER.warn("Unable to persist the one-time pillar item update", e);
                }
            }
            itemsSource = source;
            originalItems = document;
            replaceItems(parsed);
            itemsFileUnreadable = false;
        } catch (Exception e) {
            itemsFileUnreadable = true;
            backupUnreadableFile(file);
            BuildScape.LOGGER.error("Unable to load items config " + file + "; keeping the last good item list", e);
        } finally {
            lastLoadedItems = file.lastModified();
            lastFileSizeItems = file.length();
        }
    }

    static Set<String> parseItems(String content) {
        return parseItems(content, ITEMS_FILE_NAME);
    }

    static Set<String> parseItems(String content, String source) {
        com.google.gson.JsonObject root = GSON.fromJson(stripComments(content), com.google.gson.JsonObject.class);
        if (root == null || !root.has("items") || !root.get("items").isJsonArray()) {
            throw new IllegalArgumentException("Expected an items array");
        }
        Set<String> parsed = new HashSet<>();
        boolean skippedEntries = false;
        for (com.google.gson.JsonElement item : root.getAsJsonArray("items")) {
            if (!item.isJsonPrimitive() || !item.getAsJsonPrimitive().isString()) {
                skippedEntries = true;
                continue;
            }
            parsed.add(item.getAsString());
        }
        if (skippedEntries) {
            BuildScape.LOGGER.warn("Skipped non-string entries in items array from " + source);
        }
        return parsed;
    }

    private void initializeDefaultItems() {
        replaceItems(DefaultPillarItems.items(net.minecraftforge.fml.ModList.get().isLoaded("the_vault")));
    }

    private void migrateOldConfig() {
        Path oldFile = Paths.get("config", "buildscape", "pillar-particles.json");
        if (!Files.exists(oldFile)) return;
        try {
            String source = ConfigLayoutIO.readText(oldFile);
            JsonObject old = GSON.fromJson(stripComments(source), JsonObject.class);
            if (old == null) return;
            Path backup = oldFile.resolveSibling("pillar-particles.json.backup");
            if (!Files.exists(backup)) Files.copy(oldFile, backup);
            File itemsFile = getItemsFile();
            if (!itemsFile.exists() && old.has("items")) {
                parseItems(source, oldFile.toString());
                JsonObject itemValues = new JsonObject();
                itemValues.add("items", old.get("items"));
                ConfigLayoutIO.write(itemsFile.toPath(),
                        ConfigLayoutIO.retainComments(source, GSON.toJson(itemValues), true), null, false);
            }
            File propertiesFile = getPropertiesFile();
            if (!propertiesFile.exists()) {
                JsonObject properties = old.deepCopy();
                properties.remove("items");
                ConfigLayoutIO.write(propertiesFile.toPath(),
                        ConfigLayoutIO.retainComments(source, groupedProperties(properties), true), null, false);
            }
        } catch (Exception e) {
            BuildScape.LOGGER.error("Unable to migrate legacy pillar config; retaining the original", e);
        }
    }
    public static void forceReload() {
        if (INSTANCE != null) {
            INSTANCE.loadInternal();
        }
    }

    public boolean matches(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return false;

        if (items == null || items.isEmpty())
            return true;

        Boolean cached = MATCH_CACHE.get(stack.getItem());
        if (cached != null)
            return cached;

        boolean result = matchesUncached(stack);
        MATCH_CACHE.put(stack.getItem(), result);
        return result;
    }

    private boolean matchesUncached(ItemStack stack) {
        ResourceLocation id = net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(
                stack.getItem());
        if (id != null && items.contains(id.toString())) {
            return true;
        }

        for (String itemOrTag : items) {
            if (itemOrTag != null && itemOrTag.startsWith("#")) {
                String tagString = itemOrTag.substring(1);
                try {
                    ResourceLocation tagLocation = new ResourceLocation(tagString);
                    net.minecraft.tags.TagKey<net.minecraft.world.item.Item> tagKey = net.minecraft.tags.TagKey.create(
                            net.minecraft.core.Registry.ITEM_REGISTRY,
                            tagLocation);

                    if (stack.getItem().builtInRegistryHolder().is(tagKey)) {
                        return true;
                    }
                } catch (Exception e) {
                }
            }
        }

        return false;
    }

    public void saveItems() {
        clearMatchCache();
        if (this == SERVER_CONFIG && isClientConnectedToServer()) {
            net.minecraftforge.fml.DistExecutor.unsafeRunWhenOn(net.minecraftforge.api.distmarker.Dist.CLIENT,
                    () -> () -> {
                        if (net.minecraft.client.Minecraft.getInstance().player != null &&
                                net.minecraft.client.Minecraft.getInstance().player.hasPermissions(CONFIG_PERMISSION_LEVEL)) {
                            com.kingodogo.buildscape.network.UpdateConfigPacket.send(this);
                        }
                    });
            return;
        }
        saveItemsToDisk();
    }

    public synchronized void saveItemsToDisk() {
        if (remote) return;
        clearMatchCache();
        if (itemsFileUnreadable) {
            BuildScape.LOGGER.error("Not overwriting unreadable items config " + getItemsFile());
            return;
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
                originalItems = GSON.fromJson(stripComments(itemsSource), JsonObject.class);
            }
            JsonObject json = originalItems == null ? new JsonObject() : originalItems.deepCopy();
            // Explicit saves also mark the list, so removed defaults never return on the next load.
            json.addProperty("defaults_version", 405);
            if (net.minecraftforge.fml.ModList.get().isLoaded("the_vault")) {
                json.addProperty("vault_defaults_version", 405);
            }
            List<String> sorted = new ArrayList<>(items);
            sorted.sort(String::compareTo);
            json.add("items", GSON.toJsonTree(sorted));
            String output = ConfigLayoutIO.retainComments(itemsSource, GSON.toJson(json), true);
            ConfigLayoutIO.write(file.toPath(), output, itemsSource, false);
            itemsSource = output;
            originalItems = json;
            lastLoadedItems = file.lastModified();
            lastFileSizeItems = file.length();
        } catch (Exception e) {
            BuildScape.LOGGER.error("Error saving items config: " + e.getMessage());
        }
    }

    public void saveProperties() {
        if (this == SERVER_CONFIG && isClientConnectedToServer()) {
            net.minecraftforge.fml.DistExecutor.unsafeRunWhenOn(net.minecraftforge.api.distmarker.Dist.CLIENT,
                    () -> () -> {
                        if (net.minecraft.client.Minecraft.getInstance().player != null &&
                                net.minecraft.client.Minecraft.getInstance().player.hasPermissions(CONFIG_PERMISSION_LEVEL)) {
                            com.kingodogo.buildscape.network.UpdateConfigPacket.send(this);
                        }
                    });
            return;
        }
        savePropertiesToDisk();
    }

    public synchronized void savePropertiesToDisk() {
        if (remote || propertiesReadOnly) return;
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
            String text = ConfigLayoutIO.retainComments(propertiesSource, groupedProperties(output), true);
            ConfigLayoutIO.write(file.toPath(), text, propertiesSource, true);
            propertiesSource = text;
            originalProperties = output;
            loadedPropertyValues = current;
            lastLoadedProperties = file.lastModified();
            lastFileSizeProperties = file.length();
        } catch (Exception ignored) {
            BuildScape.LOGGER.warn("Unable to save pillar properties", ignored);
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
        return values;
    }

    private static String groupedProperties(JsonObject values) {
        StringBuilder text = new StringBuilder("{\n"
                + "  // Basic parameters apply when use_pattern = false; patterns use pattern_* values instead.\n"
                + "  // Right-click a pillar with dyes to override config colors for that pillar (up to 5 dyes).\n"
                + "  // Dye colors cycle in application order; break and replace the pillar to reset them.\n"
                + "  // Config colors cycle from the first color through max_particle_color.\n");
        java.util.Set<String> written = new java.util.HashSet<>();
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
        text.append("\n  // --- Other ---\n");
        // Sort unknown keys as well, so output remains stable regardless of input key order.
        for (String key : new java.util.TreeSet<>(values.keySet())) {
            if (!written.contains(key)) {
                text.append("  // Unrecognized setting retained with its original value\n");
                appendProperty(text, key, values.get(key));
            }
        }
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
        text.append("  ").append(GSON.toJson(key)).append(": ").append(GSON.toJson(value)).append(",\n");
    }

    public boolean addItem(String itemId) {
        if (itemId == null || itemId.isEmpty()) {
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
        if (itemId == null || itemId.isEmpty()) {
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
