package com.kingodogo.buildscape.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.kingodogo.buildscape.BuildScape;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class PillarParticleConfig {

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
    private static final List<java.util.function.Consumer<Boolean>> configReloadCallbacks = new ArrayList<>();
    private static long lastNotifyTime = 0;

    // ── Cheap-read snapshot for the hot tick path ────────────────────────────────
    // clientTick() reads config dozens of times per second. Rather than going
    // through the full get() machinery (isClientConnectedToServer reflection +
    // file-stat check on two files) on every call, we keep a volatile snapshot
    // that get() refreshes whenever the underlying config actually changes.
    private static volatile PillarParticleConfig SNAPSHOT = null;

    /** Fast path for the hot client-tick loop: returns the last loaded config
     *  without any I/O or reflection.  Falls back to the full get() path only
     *  until the first non-null snapshot is available. */
    public static PillarParticleConfig peek() {
        PillarParticleConfig s = SNAPSHOT;
        return s != null ? s : get();
    }

    // ── isClientConnectedToServer cache ────────────────────────────────────
    // Class.forName + 3 reflection invocations is very expensive to call on
    // every tick.  Cache the result and refresh at most once per ~3 seconds.
    private static volatile boolean cachedConnectedToServer = false;
    private static volatile long connectedCacheTime = 0L;
    private static final long CONNECTED_CACHE_TTL_MS = 3000L; // 3 s

    // ── File-stat throttle ───────────────────────────────────────────────
    // file.lastModified() + file.length() on two files was being called on
    // every invocation of get() which happens every client tick per pillar.
    // Throttle to once every 2 seconds (40 game ticks / 2000 ms).
    private static volatile long lastStatCheckTime = 0L;
    private static final long STAT_CHECK_INTERVAL_MS = 2000L; // 2 s

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
        clearMatchCache(); // invalidate per-Item match cache on every config reload
    }

    public Set<String> items = new HashSet<>();

    // ── matches() cache ───────────────────────────────────────────────────
    // matches() iterates the items set, calling new ResourceLocation() and a full
    // tag-registry lookup for every tag-prefixed entry, every 5 client ticks per
    // active pillar.  The result only depends on the Item type (registry key +
    // tags) — not on the ItemStack NBT — so we can safely cache Boolean per Item.
    // The cache is cleared whenever the config reloads so hot-reload still works.
    private static final java.util.concurrent.ConcurrentHashMap<
            net.minecraft.world.item.Item, Boolean> MATCH_CACHE =
            new java.util.concurrent.ConcurrentHashMap<>();

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

    public List<String> particle_color = new ArrayList<>();

    public int max_particle_color = 3;

    private static boolean isClientConnectedToServer() {
        long now = System.currentTimeMillis();
        if (now - connectedCacheTime < CONNECTED_CACHE_TTL_MS) {
            return cachedConnectedToServer;
        }
        boolean result;
        try {
            Class<?> mcClass = Class.forName("net.minecraft.client.Minecraft");
            Object mc = mcClass.getMethod("getInstance").invoke(null);
            if (mc == null) { result = false; }
            else {
                Object connection = mcClass.getMethod("getConnection").invoke(mc);
                if (connection == null) { result = false; }
                else {
                    Object level = mcClass.getMethod("level").invoke(mc);
                    if (level == null) { result = false; }
                    else {
                        Boolean isClientSide = (Boolean) level.getClass()
                                .getMethod("isClientSide").invoke(level);
                        result = isClientSide != null && isClientSide;
                    }
                }
            }
        } catch (Exception e) {
            result = false;
        }
        cachedConnectedToServer = result;
        connectedCacheTime = now;
        return result;
    }

    public static PillarParticleConfig get() {
        if (isClientConnectedToServer() && SERVER_CONFIG != null) {
            SNAPSHOT = SERVER_CONFIG;
            return SERVER_CONFIG;
        }

        if (INSTANCE == null) {
            INSTANCE = new PillarParticleConfig();
            INSTANCE.loadInternal();
            initializeFileWatcher();
            SNAPSHOT = INSTANCE;
        } else {
            // ── Throttled file-stat check ──────────────────────────────────
            // Only hit the filesystem to check for config changes every 2 s.
            // In steady-state, most calls return immediately without any I/O.
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
            SERVER_CONFIG.particle_color.add("#E8FEFD"); // White
            SERVER_CONFIG.particle_color.add("#FF5C00"); // Orange
            SERVER_CONFIG.particle_color.add("#3CDFFF"); // Light Blue
        }

        int numColors = SERVER_CONFIG.particle_color.size();
        SERVER_CONFIG.max_particle_color = Math.max(
                1,
                Math.min(
                        7,
                        Math.min(
                                packet.max_particle_color > 0 ? packet.max_particle_color : numColors,
                                numColors)));

        SERVER_CONFIG.items = new HashSet<>(
                packet.items != null ? packet.items : new HashSet<>());

        clearMatchCache(); // new server config — invalidate match cache
        notifyCallbacks(true);
    }

    private static void initializeFileWatcher() {
        if (watcherInitialized.getAndSet(true)) {
            return;
        }

        try {
            Class.forName("net.minecraft.client.Minecraft");
        } catch (ClassNotFoundException e) {
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

                                        // Simple debounce: wait a bit to let file write finish
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
                                                // Check if it actually reloaded by comparing timestamps/sizes isn't easy here since loadPropertiesInternal updates them.
                                                // But loadPropertiesInternal is only called if we are here. 
                                                // To properly debounce, we should check if enough time passed since last reload/notify.
                                                reloaded = true;
                                            }
                                            if (wasItems) {
                                                INSTANCE.loadItemsInternal();
                                                reloaded = true;
                                            }

                                            if (reloaded) {
                                                // Debounce notifications: only notify if > 500ms since last notification
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

    public static void clearServerConfig() {
        SERVER_CONFIG = null;
    }


    private File getPropertiesFile() {
        return new File(getConfigDir(), PROPERTIES_FILE_NAME);
    }

    private File getItemsFile() {
        return new File(getConfigDir(), ITEMS_FILE_NAME);
    }

    private File getConfigDir() {
        String configPath = Paths.get("config", "buildscape", "pillar").toString();
        File dir = new File(configPath);
        if (!dir.exists())
            dir.mkdirs();
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
                writer.write(
                        "  // Basic particle parameters (used when use_pattern = false)\n");
                writer.write(
                        "  // These parameters control particle behavior when patterns are disabled\n");
                writer.write(
                        "  \"particle_speed\": 0.02,      // Base speed multiplier for particles\n");
                writer.write(
                        "  \"particle_spread\": 0.1,      // How far particles spread from center\n");
                writer.write(
                        "  \"particle_lifetime\": 20,     // How long particles last (in ticks, 20 ticks = 1 second)\n");
                writer.write(
                        "  \"particle_density\": 2,       // Number of particles spawned per tick\n");
                writer.write("  \n");
                writer.write(
                        "  // Pattern system - set use_pattern to true to enable patterns (overrides basic parameters above)\n");
                writer.write(
                        "  // When use_pattern is false, basic particle parameters (particle_speed, particle_spread, etc.) are used\n");
                writer.write(
                        "  // When use_pattern is true, pattern-specific parameters (pattern_speed, pattern_spread, etc.) are used\n");
                writer.write("  \"use_pattern\": true,\n");
                writer.write("  \n");
                writer.write(
                        "  // Available patterns: \"beam\", \"spiral\", \"fountain\", \"pulse\", \"ring\", \"burst\"\n");
                writer.write("  //   \"ring\" - Default ring of particles\n");
                writer.write(
                        "  //   \"beam\" - Straight upward beam with subtle spread\n");
                writer.write(
                        "  //   \"spiral\" - Particles spiral upward in a helix pattern\n");
                writer.write(
                        "  //   \"fountain\" - Particles spread outward then fall down\n");
                writer.write(
                        "  //   \"pulse\" - Particles pulse in and out from the center\n");
                writer.write(
                        "  //   \"ring\" - Particles form a ring around the pillar\n");
                writer.write(
                        "  //   \"burst\" - Particles burst outward in all directions (half size)\n");
                writer.write(
                        "  // You can cycle through patterns in-game by pressing the cycle pattern keybind (default: P)\n");
                writer.write("  \"pattern\": \"ring\",\n");
                writer.write("  \n");
                writer.write(
                        "  // Pattern-specific parameters (only used when use_pattern = true)\n");
                writer.write(
                        "  // These parameters control pattern behavior and override basic particle parameters\n");
                writer.write(
                        "  \"pattern_speed\": 0.05,       // Base speed multiplier for pattern particles\n");
                writer.write(
                        "  \"pattern_spread\": 0.05,      // Spread amount for pattern particles\n");
                writer.write(
                        "  \"pattern_intensity\": 1.0,    // Intensity multiplier for pattern effects (higher = more intense)\n");
                writer.write("  \n");
                writer.write(
                        "  // Particle colors (array of #RRGGBB format colors, up to 7 colors)\n");
                writer.write(
                        "  // Particles cycle through these colors (1st particle = 1st color, 2nd particle = 2nd color, etc.)\n");
                writer.write(
                        "  // The texture is white, so any color will tint it appropriately\n");
                writer.write("  // \n");
                writer.write("  // DYING PILLARS:\n");
                writer.write(
                        "  // You can dye pillars by right-clicking them with any dye item (red, blue, green, etc.)\n");
                writer.write(
                        "  // Each pillar can have up to 5 different dye colors applied to it\n");
                writer.write(
                        "  // When a pillar is dyed, it overrides these config colors for that specific pillar\n");
                writer.write(
                        "  // Dye colors are applied in order - particles will cycle through all applied dye colors\n");
                writer.write(
                        "  // Each pillar can have its own dye colors, independent of other pillars\n");
                writer.write("  // To reset a pillar's colors, break and replace it\n");
                writer.write(
                        "  \"particle_color\": [\"#E8FEFD\", \"#FF5C00\", \"#3CDFFF\"],\n");
                writer.write("  \n");
                writer.write(
                        "  // Maximum number of colors to use from particle_color array (1-7)\n");
                writer.write(
                        "  // Particles will cycle through colors from first to max_particle_color\n");
                writer.write(
                        "  // This only applies when using config colors - dyed pillars use their dye colors instead\n");
                writer.write("  \"max_particle_color\": 3\n");
                writer.write("}\n");
            }
        } catch (Exception ignored) {
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
                writer.write(
                        "  // Item IDs that trigger particles when placed on pillars\n");
                writer.write(
                        "  // Add or remove item IDs from this list to control which items create particles\n");
                writer.write(
                        "  // Format: \"minecraft:item_name\" (use the item's registry name)\n");
                writer.write(
                        "  // Items must be placed on a pillar block to trigger particles\n");
                writer.write(
                        "  // Right-click a pillar with an item from this list to place it and activate particles\n");
                writer.write(
                        "  // You can also use item tags by prefixing with \"#\" (e.g., \"#minecraft:planks\")\n");
                writer.write("  \"items\": [\n");
                writer.write("    \"minecraft:diamond\",\n");
                writer.write("    \"minecraft:netherite_ingot\",\n");
                writer.write("    \"minecraft:nether_star\",\n");
                writer.write("    \"minecraft:heart_of_the_sea\",\n");
                writer.write("    \"minecraft:trident\",\n");
                writer.write("    \"minecraft:emerald\",\n");
                writer.write("    \"minecraft:dragon_breath\",\n");
                writer.write("    \"minecraft:netherite_scrap\",\n");
                writer.write("    \"minecraft:totem_of_undying\",\n");
                writer.write("    \"minecraft:spyglass\",\n");
                writer.write("    \"minecraft:elytra\",\n");
                writer.write("    \"minecraft:diamond_sword\",\n");
                writer.write("    \"minecraft:diamond_hoe\",\n");
                writer.write("    \"minecraft:diamond_axe\",\n");
                writer.write("    \"minecraft:diamond_pickaxe\",\n");
                writer.write("    \"minecraft:diamond_shovel\",\n");
                writer.write("    \"minecraft:diamond_boots\",\n");
                writer.write("    \"minecraft:diamond_leggings\",\n");
                writer.write("    \"minecraft:diamond_chestplate\",\n");
                writer.write("    \"minecraft:diamond_helmet\",\n");
                writer.write("    \"minecraft:netherite_sword\",\n");
                writer.write("    \"minecraft:netherite_hoe\",\n");
                writer.write("    \"minecraft:netherite_pickaxe\",\n");
                writer.write("    \"minecraft:netherite_axe\",\n");
                writer.write("    \"minecraft:netherite_shovel\",\n");
                writer.write("    \"minecraft:netherite_boots\",\n");
                writer.write("    \"minecraft:netherite_leggings\",\n");
                writer.write("    \"minecraft:netherite_chestplate\",\n");
                writer.write("    \"minecraft:netherite_helmet\",\n");
                writer.write("    \"minecraft:nautilus_shell\",\n");
                writer.write("    \"minecraft:shulker_shell\",\n");
                writer.write("    \"minecraft:golden_apple\",\n");
                writer.write("    \"minecraft:enchanted_golden_apple\",\n");
                writer.write("    \"minecraft:golden_carrot\",\n");
                writer.write("    \"minecraft:experience_bottle\",\n");
                writer.write("    \"minecraft:mojang_banner_pattern\",\n");
                writer.write("    \"minecraft:ancient_debris\",\n");
                writer.write("    \"minecraft:dragon_head\",\n");
                writer.write("    \"minecraft:dragon_egg\",\n");
                writer.write("    \"minecraft:player_head\",\n");
                writer.write("    \"minecraft:beacon\",\n");
                writer.write("    \"minecraft:end_crystal\",\n");
                writer.write("    \"minecraft:conduit\",\n");
                writer.write("    \"minecraft:skeleton_skull\",\n");
                writer.write("    \"minecraft:zombie_head\",\n");
                writer.write("    \"minecraft:wither_skeleton_skull\",\n");
                writer.write("    \"minecraft:creeper_head\",\n");
                writer.write("    \"minecraft:enchanting_table\",\n");
                writer.write("    \"minecraft:emerald_block\",\n");
                writer.write("    \"minecraft:diamond_block\",\n");
                writer.write("    \"minecraft:gold_block\",\n");
                writer.write("    \"minecraft:netherite_block\",\n");
                writer.write("    \"minecraft:deepslate_diamond_ore\",\n");
                writer.write("    \"minecraft:diamond_ore\",\n");
                writer.write("    \"minecraft:bedrock\",\n");
                writer.write("    \"minecraft:pufferfish\",\n");
                writer.write("    \"minecraft:poisonous_potato\",\n");
                writer.write("    \"minecraft:creeper_spawn_egg\",\n");
                writer.write("    \"minecraft:turtle_spawn_egg\",\n");
                writer.write("    \"minecraft:axolotl_spawn_egg\",\n");
                writer.write("    \"minecraft:wither_skeleton_spawn_egg\",\n");
                writer.write("    \"minecraft:shulker_spawn_egg\",\n");
                writer.write("    \"minecraft:elder_guardian_spawn_egg\",\n");
                writer.write("    \"minecraft:ravager_spawn_egg\",\n");
                writer.write("    \"minecraft:slime_spawn_egg\",\n");
                writer.write("    \"minecraft:zoglin_spawn_egg\",\n");
                writer.write("    \"minecraft:villager_spawn_egg\",\n");
                writer.write("    \"minecraft:skeleton_horse_spawn_egg\",\n");
                writer.write("    \"minecraft:glow_squid_spawn_egg\",\n");
                writer.write("    \"minecraft:goat_spawn_egg\",\n");
                writer.write("    \"minecraft:enderman_spawn_egg\",\n");
                writer.write("    \"minecraft:written_book\",\n");
                writer.write("    \"buildscape:ancient_ashen_scroll\"");

                boolean hasVaultHunters = net.minecraftforge.fml.ModList.get().isLoaded("the_vault");
                if (hasVaultHunters) {
                    writer.write(",\n");
                    writer.write("    \"the_vault:echo_pog\",\n");
                    writer.write("    \"the_vault:gem_pog\",\n");
                    writer.write("    \"the_vault:vault_crystal\",\n");
                    writer.write("    \"the_vault:spicy_hearty_burger\",\n");
                    writer.write("    \"the_vault:omega_pog\",\n");
                    writer.write("    \"the_vault:knowledge_star\",\n");
                    writer.write("    \"the_vault:antique\",\n");
                    writer.write("    \"the_vault:herald_trophy\",\n");
                    writer.write("    \"the_vault:pvp_trophy\",\n");
                    writer.write("    \"the_vault:treasure_keyring\",\n");
                    writer.write("    \"the_vault:companion_egg\",\n");
                    writer.write("    \"the_vault:vault_artifact\",\n");
                    writer.write("    \"the_vault:tool\",\n");
                    writer.write("    \"the_vault:deck_socket\",\n");
                    writer.write("    \"the_vault:card_deck\",\n");
                    writer.write("    \"the_vault:vault_god_charm\",\n");
                    writer.write("    \"the_vault:boost_modification_stone\",\n");
                    writer.write("    \"the_vault:neuralizer\",\n");
                    writer.write("    \"the_vault:soul_vortex\",\n");
                    writer.write("    \"#the_vault:crystal_capstones\",\n");
                    writer.write("    \"#the_vault:keys\",\n");
                    writer.write("    \"#the_vault:gems\",\n");
                    writer.write("    \"#the_vault:fruits\",\n");
                    writer.write("    \"the_vault:unidentified_artifact\",\n");
                    writer.write("    \"#the_vault:playerclusters\",\n");
                    writer.write("    \"#the_vault:perfectgems\",\n");
                    writer.write("    \"#the_vault:playerchunks\",\n");
                    writer.write("    \"#the_vault:magnet\",\n");
                    writer.write("    \"#the_vault:unique\",\n");
                    writer.write("    \"#the_vault:vault_gear\",\n");
                    writer.write("    \"the_vault:santa_egg\",\n");
                    writer.write("    \"the_vault:grinch_egg\",\n");
                    writer.write("    \"the_vault:yeti_egg\"\n");
                } else {
                    writer.write("\n");
                }
                writer.write("  ],\n");
                writer.write("  \n");
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

    private String stripComments(String json) {
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

    private void loadPropertiesInternal() {
        File file = getPropertiesFile();
        if (!file.exists()) {
            writeDefaultProperties(file);
            this.particle_color.clear();
            this.particle_color.add("#E8FEFD"); // White
            this.particle_color.add("#FF5C00"); // Orange
            this.particle_color.add("#3CDFFF"); // Light Blue
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

            Type mapType = new TypeToken<Map<String, Object>>() {
            }.getType();
            Map<String, Object> jsonMap = GSON.fromJson(jsonWithoutComments, mapType);

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
                        this.particle_color = new ArrayList<>(
                                this.particle_color.subList(0, 7));
                    }

                    if (this.particle_color.isEmpty()) {
                        this.particle_color.add("#FFB81C");
                        this.particle_color.add("#FFFFFF");
                        this.particle_color.add("#FFFF00");
                    }

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
            lastLoadedProperties = file.lastModified();
            lastFileSizeProperties = file.length();
        } catch (Exception ignored) {
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

            Type mapType = new TypeToken<Map<String, Object>>() {
            }.getType();
            Map<String, Object> jsonMap = GSON.fromJson(jsonWithoutComments, mapType);

            if (jsonMap != null && jsonMap.containsKey("items")) {
                Object itemsObj = jsonMap.get("items");
                this.items = new HashSet<>();
                if (itemsObj instanceof List<?> itemsList) {
                    for (Object item : itemsList) {
                        if (item instanceof String) {
                            this.items.add((String) item);
                        }
                    }
                }

                // Auto-add any new default vanilla items that were added in mod updates
                {
                    boolean changed = false;
                    String[] defaultVanillaItems = new String[]{
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

                    for (String defaultItem : defaultVanillaItems) {
                        if (!this.items.contains(defaultItem)) {
                            this.items.add(defaultItem);
                            changed = true;
                        }
                    }

                    if (changed) {
                        saveItems();
                    }
                }

                // Check for auto-update of Vault Hunters items
                if (net.minecraftforge.fml.ModList.get().isLoaded("the_vault")) {
                    boolean changed = false;
                    String[] vaultItems = new String[]{
                            "the_vault:echo_pog",
                            "the_vault:gem_pog",
                            "the_vault:vault_crystal",
                            "the_vault:spicy_hearty_burger",
                            "the_vault:omega_pog",
                            "the_vault:knowledge_star",
                            "the_vault:antique",
                            "the_vault:herald_trophy",
                            "the_vault:pvp_trophy",
                            "the_vault:treasure_keyring",
                            "the_vault:companion_egg",
                            "the_vault:vault_artifact",
                            "the_vault:tool",
                            "the_vault:deck_socket",
                            "the_vault:card_deck",
                            "the_vault:vault_god_charm",
                            "the_vault:boost_modification_stone",
                            "the_vault:neuralizer",
                            "the_vault:soul_vortex",
                            "#the_vault:crystal_capstones",
                            "#the_vault:keys",
                            "#the_vault:gems",
                            "#the_vault:fruits",
                            "the_vault:unidentified_artifact",
                            "#the_vault:playerclusters",
                            "#the_vault:perfectgems",
                            "#the_vault:playerchunks",
                            "#the_vault:magnet",
                            "#the_vault:unique",
                            "#the_vault:vault_gear",
                            "the_vault:santa_egg",
                            "the_vault:grinch_egg",
                            "the_vault:yeti_egg"
                    };

                    for (String vaultItem : vaultItems) {
                        if (!this.items.contains(vaultItem)) {
                            this.items.add(vaultItem);
                            changed = true;
                        }
                    }

                    if (changed) {
                        saveItems();
                    }
                }
            }

            lastLoadedItems = file.lastModified();
            lastFileSizeItems = file.length();
        } catch (Exception ignored) {
        }
    }

    private void initializeDefaultItems() {
        items.clear();
        items.add("minecraft:diamond");
        items.add("minecraft:netherite_ingot");
        items.add("minecraft:nether_star");
        items.add("minecraft:heart_of_the_sea");
        items.add("minecraft:trident");
        items.add("minecraft:emerald");
        items.add("minecraft:dragon_breath");
        items.add("minecraft:netherite_scrap");
        items.add("minecraft:totem_of_undying");
        items.add("minecraft:spyglass");
        items.add("minecraft:elytra");
        items.add("minecraft:diamond_sword");
        items.add("minecraft:diamond_hoe");
        items.add("minecraft:diamond_axe");
        items.add("minecraft:diamond_pickaxe");
        items.add("minecraft:diamond_shovel");
        items.add("minecraft:diamond_boots");
        items.add("minecraft:diamond_leggings");
        items.add("minecraft:diamond_chestplate");
        items.add("minecraft:diamond_helmet");
        items.add("minecraft:netherite_sword");
        items.add("minecraft:netherite_hoe");
        items.add("minecraft:netherite_pickaxe");
        items.add("minecraft:netherite_axe");
        items.add("minecraft:netherite_shovel");
        items.add("minecraft:netherite_boots");
        items.add("minecraft:netherite_leggings");
        items.add("minecraft:netherite_chestplate");
        items.add("minecraft:netherite_helmet");
        items.add("minecraft:nautilus_shell");
        items.add("minecraft:shulker_shell");
        items.add("minecraft:golden_apple");
        items.add("minecraft:enchanted_golden_apple");
        items.add("minecraft:golden_carrot");
        items.add("minecraft:experience_bottle");
        items.add("minecraft:mojang_banner_pattern");
        items.add("minecraft:ancient_debris");
        items.add("minecraft:dragon_head");
        items.add("minecraft:dragon_egg");
        items.add("minecraft:player_head");
        items.add("minecraft:beacon");
        items.add("minecraft:end_crystal");
        items.add("minecraft:conduit");
        items.add("minecraft:skeleton_skull");
        items.add("minecraft:zombie_head");
        items.add("minecraft:wither_skeleton_skull");
        items.add("minecraft:creeper_head");
        items.add("minecraft:enchanting_table");
        items.add("minecraft:emerald_block");
        items.add("minecraft:diamond_block");
        items.add("minecraft:gold_block");
        items.add("minecraft:netherite_block");
        items.add("minecraft:deepslate_diamond_ore");
        items.add("minecraft:diamond_ore");
        items.add("minecraft:bedrock");
        items.add("minecraft:pufferfish");
        items.add("minecraft:poisonous_potato");
        items.add("minecraft:written_book");

        items.add("minecraft:creeper_spawn_egg");
        items.add("minecraft:turtle_spawn_egg");
        items.add("minecraft:axolotl_spawn_egg");
        items.add("minecraft:wither_skeleton_spawn_egg");
        items.add("minecraft:shulker_spawn_egg");
        items.add("minecraft:elder_guardian_spawn_egg");
        items.add("minecraft:ravager_spawn_egg");
        items.add("minecraft:slime_spawn_egg");
        items.add("minecraft:zoglin_spawn_egg");
        items.add("minecraft:villager_spawn_egg");
        items.add("minecraft:skeleton_horse_spawn_egg");
        items.add("minecraft:glow_squid_spawn_egg");
        items.add("minecraft:goat_spawn_egg");
        items.add("minecraft:enderman_spawn_egg");
        items.add("buildscape:ancient_ashen_scroll");

        if (net.minecraftforge.fml.ModList.get().isLoaded("the_vault")) {
            items.add("the_vault:echo_pog");
            items.add("the_vault:gem_pog");
            items.add("the_vault:vault_crystal");
            items.add("the_vault:spicy_hearty_burger");
            items.add("the_vault:omega_pog");
            items.add("the_vault:knowledge_star");
            items.add("the_vault:antique");
            items.add("the_vault:herald_trophy");
            items.add("the_vault:pvp_trophy");
            items.add("the_vault:treasure_keyring");
            items.add("the_vault:companion_egg");
            items.add("the_vault:vault_artifact");
            items.add("the_vault:tool");
            items.add("the_vault:deck_socket");
            items.add("the_vault:card_deck");
            items.add("the_vault:vault_god_charm");
            items.add("the_vault:boost_modification_stone");
            items.add("the_vault:neuralizer");
            items.add("the_vault:soul_vortex");
            items.add("#the_vault:crystal_capstones");
            items.add("#the_vault:keys");
            items.add("#the_vault:gems");
            items.add("#the_vault:fruits");
            items.add("the_vault:unidentified_artifact");
            items.add("#the_vault:playerclusters");
            items.add("#the_vault:perfectgems");
            items.add("#the_vault:playerchunks");
            items.add("#the_vault:magnet");
            items.add("#the_vault:unique");
            items.add("#the_vault:vault_gear");
            items.add("the_vault:santa_egg");
            items.add("the_vault:grinch_egg");
            items.add("the_vault:yeti_egg");
        }
    }

    private void migrateOldConfig() {
        File oldConfigDir = new File(Paths.get("config", "buildscape").toString());
        File oldConfigFile = new File(oldConfigDir, "pillar-particles.json");

        if (oldConfigFile.exists()) {
            try (FileReader r = new FileReader(oldConfigFile)) {
                StringBuilder content = new StringBuilder();
                int ch;
                while ((ch = r.read()) != -1) {
                    content.append((char) ch);
                }
                String jsonWithoutComments = stripComments(content.toString());

                Type mapType = new TypeToken<Map<String, Object>>() {
                }.getType();
                Map<String, Object> jsonMap = GSON.fromJson(
                        jsonWithoutComments,
                        mapType);

                if (jsonMap != null) {
                    if (jsonMap.containsKey("items")) {
                        Object itemsObj = jsonMap.get("items");
                        if (itemsObj instanceof List<?> itemsList) {
                            File newItemsFile = getItemsFile();
                            writeDefaultItems(newItemsFile);

                            try (FileWriter writer = new FileWriter(newItemsFile)) {
                                writer.write("{\n");
                                writer.write(
                                        "  // Item IDs that trigger particles when placed on pillars\n");
                                writer.write("  // Migrated from old config file\n");
                                writer.write("  \"items\": [\n");
                                for (int i = 0; i < itemsList.size(); i++) {
                                    Object item = itemsList.get(i);
                                    if (item instanceof String) {
                                        writer.write("    \"" + item + "\"");
                                        if (i < itemsList.size() - 1) {
                                            writer.write(",");
                                        }
                                        writer.write("\n");
                                    }
                                }
                                writer.write("  ]\n");
                                writer.write("}\n");
                            }
                        }
                    }

                    File newPropertiesFile = getPropertiesFile();
                    writeDefaultProperties(newPropertiesFile);

                    try (FileWriter writer = new FileWriter(newPropertiesFile)) {
                        writer.write("{\n");
                        writer.write("  // Migrated from old config file\n");
                        boolean first = true;
                        if (jsonMap.containsKey("particle_speed")) {
                            if (!first)
                                writer.write(",\n");
                            writer.write(
                                    "  \"particle_speed\": " + jsonMap.get("particle_speed"));
                            first = false;
                        }
                        if (jsonMap.containsKey("particle_spread")) {
                            if (!first)
                                writer.write(",\n");
                            writer.write(
                                    "  \"particle_spread\": " + jsonMap.get("particle_spread"));
                            first = false;
                        }
                        if (jsonMap.containsKey("particle_lifetime")) {
                            if (!first)
                                writer.write(",\n");
                            writer.write(
                                    "  \"particle_lifetime\": " + jsonMap.get("particle_lifetime"));
                            first = false;
                        }
                        if (jsonMap.containsKey("particle_density")) {
                            if (!first)
                                writer.write(",\n");
                            writer.write(
                                    "  \"particle_density\": " + jsonMap.get("particle_density"));
                            first = false;
                        }
                        if (jsonMap.containsKey("use_pattern")) {
                            if (!first)
                                writer.write(",\n");
                            writer.write("  \"use_pattern\": " + jsonMap.get("use_pattern"));
                            first = false;
                        }
                        if (jsonMap.containsKey("pattern")) {
                            if (!first)
                                writer.write(",\n");
                            writer.write("  \"pattern\": \"" + jsonMap.get("pattern") + "\"");
                            first = false;
                        }
                        if (jsonMap.containsKey("pattern_speed")) {
                            if (!first)
                                writer.write(",\n");
                            writer.write(
                                    "  \"pattern_speed\": " + jsonMap.get("pattern_speed"));
                            first = false;
                        }
                        if (jsonMap.containsKey("pattern_spread")) {
                            if (!first)
                                writer.write(",\n");
                            writer.write(
                                    "  \"pattern_spread\": " + jsonMap.get("pattern_spread"));
                            first = false;
                        }
                        if (jsonMap.containsKey("pattern_intensity")) {
                            if (!first)
                                writer.write(",\n");
                            writer.write(
                                    "  \"pattern_intensity\": " + jsonMap.get("pattern_intensity"));
                            first = false;
                        }
                        if (jsonMap.containsKey("particle_color")) {
                            if (!first)
                                writer.write(",\n");
                            writer.write(
                                    "  \"particle_color\": " +
                                            GSON.toJson(jsonMap.get("particle_color")));
                            first = false;
                        }
                        if (jsonMap.containsKey("max_particle_color")) {
                            if (!first)
                                writer.write(",\n");
                            writer.write(
                                    "  \"max_particle_color\": " + jsonMap.get("max_particle_color"));
                        }
                        writer.write("\n}\n");
                    }

                    File backupFile = new File(
                            oldConfigDir,
                            "pillar-particles.json.backup");
                    oldConfigFile.renameTo(backupFile);
                }
            } catch (Exception ignored) {
            }
        }
    }

    public static void forceReload() {
        if (INSTANCE != null) {
            INSTANCE.loadInternal();
        }
    }

    public boolean matches(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;

        // Fast path: result is cached per Item type (cleared on config reload)
        Boolean cached = MATCH_CACHE.get(stack.getItem());
        if (cached != null) return cached;

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
        if (isClientConnectedToServer() && SERVER_CONFIG != null) {
            // Update the server's global config when changed on client
            net.minecraftforge.fml.DistExecutor.unsafeRunWhenOn(net.minecraftforge.api.distmarker.Dist.CLIENT, () -> () -> {
                if (net.minecraft.client.Minecraft.getInstance().player != null &&
                        net.minecraft.client.Minecraft.getInstance().player.hasPermissions(2)) {
                    com.kingodogo.buildscape.network.ModMessages.INSTANCE.sendToServer(
                            new com.kingodogo.buildscape.network.UpdateConfigPacket(this)
                    );
                }
            });
            return; // Don't save on client disk when connected to server
        }
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
        } catch (Exception e) {
            BuildScape.LOGGER.error("Error saving items config: " + e.getMessage());
        }
    }

    public void saveProperties() {
        if (isClientConnectedToServer() && SERVER_CONFIG != null) {
            // Update the server's global config when changed on client
            net.minecraftforge.fml.DistExecutor.unsafeRunWhenOn(net.minecraftforge.api.distmarker.Dist.CLIENT, () -> () -> {
                if (net.minecraft.client.Minecraft.getInstance().player != null &&
                        net.minecraft.client.Minecraft.getInstance().player.hasPermissions(2)) {
                    com.kingodogo.buildscape.network.ModMessages.INSTANCE.sendToServer(
                            new com.kingodogo.buildscape.network.UpdateConfigPacket(this)
                    );
                }
            });
            return; // Don't save on client disk when connected to server
        }
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
        } catch (Exception e) {
            BuildScape.LOGGER.error("Error saving properties config: " + e.getMessage());
        }
    }

    public boolean addItem(String itemId) {
        if (itemId == null || itemId.isEmpty()) {
            return false;
        }
        if (items.add(itemId)) {
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
