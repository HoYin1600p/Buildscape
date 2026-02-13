package com.kingodogo.buildscape.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PillarIdManager {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    private static final String FILE_NAME = "pillar-ids.dat";
    private static final String BACKUP_FILE_NAME = "pillar-ids.bak.dat";
    private static final String FOLDER_NAME = "buildscape";
    private static PillarIdManager INSTANCE;

    private final Map<String, PillarData> pillarData = new ConcurrentHashMap<>();

    // ── O(1) position index ───────────────────────────────────────────────
    // getPillarDataByPosition() previously scanned all pillarData entries.
    // On servers with hundreds of pillars this became O(n) per serverTick.
    // The index maps "dimension:x:y:z" → pillarId so lookups are O(1).
    private final Map<String, String> positionIndex = new ConcurrentHashMap<>();

    private long lastLoadedTime = 0L;
    private long lastFileSize = 0L;

    private boolean hasLoaded = false;
    private boolean hadColorsOnLoad = false; // Track if we had colors when we loaded
    private boolean isServerSynced = false; // Flag to indicate data came from server
    private boolean allowEmptySave = false; // Flag to allow saving empty pillar data (e.g., user removed all)
    
    private static boolean recoveryScheduled = false;
    private static long recoveryScheduledTime = 0L;
    private static final long RECOVERY_DELAY_MS = 5000; // 5 seconds after world load
    private static boolean recoveryInProgress = false; // Flag to prevent saving during recovery

    private static long worldLoadStartTime = 0L;
    private static final long MIN_WORLD_LOAD_TIME_MS = 15000;

    private static File cachedWorldSaveDir = null;

    public static final String PREFIX_MOSSY = "M";
    public static final String PREFIX_STONE = "S";
    public static final String PREFIX_DEEPSLATE = "D";
    public static final String PREFIX_QUARTZ = "Q";

    /**
     * Reset world cache directory and CLEAR data - makes Pillar IDs world/server specific.
     * Called on world unload/player logout.
     */
    public static void resetWorldCache() {
        cachedWorldSaveDir = null;
        worldLoadStartTime = System.currentTimeMillis();
        recoveryScheduled = false;
        recoveryScheduledTime = 0L;

        if (INSTANCE != null) {
            INSTANCE.isServerSynced = false;
            INSTANCE.pillarData.clear();
            INSTANCE.positionIndex.clear();
            INSTANCE.hasLoaded = false;
            INSTANCE.hadColorsOnLoad = false;
            INSTANCE.lastLoadedTime = 0L;
            INSTANCE.lastFileSize = 0L;
        }
    }

    public static PillarIdManager get() {
        if (INSTANCE == null) {
            INSTANCE = new PillarIdManager();
        }
        return INSTANCE;
    }

    public boolean hasLoaded() {
        return hasLoaded;
    }

    public static String getVariantPrefix(Level level, BlockPos pos) {
        if (level == null || pos == null) return PREFIX_STONE + "-P";

        if (
                !level.isClientSide &&
                        !com.kingodogo.buildscape.BuildScape.isServerFullyInitialized()
        ) {
            return PREFIX_STONE + "-P";
        }

        if (!level.hasChunkAt(pos)) {
            return PREFIX_STONE + "-P";
        }

        try {
            if (!level.isClientSide) {
                net.minecraft.world.level.chunk.ChunkAccess chunk = level.getChunk(pos);
                if (!(chunk instanceof net.minecraft.world.level.chunk.LevelChunk)) {
                    return PREFIX_STONE + "-P";
                }
                if (
                        !chunk
                                .getStatus()
                                .isOrAfter(net.minecraft.world.level.chunk.ChunkStatus.FULL)
                ) {
                    return PREFIX_STONE + "-P";
                }
            }
        } catch (Exception e) {
            return PREFIX_STONE + "-P";
        }

        try {
            BlockState state = level.getBlockState(pos);
            Block block = state.getBlock();
            String blockName =
                    net.minecraftforge.registries.ForgeRegistries.BLOCKS.getKey(
                            block
                    ).getPath();

            if (blockName.contains("mossy")) {
                return PREFIX_MOSSY + "-P";
            } else if (blockName.contains("deepslate")) {
                return PREFIX_DEEPSLATE + "-P";
            } else if (blockName.contains("quartz")) {
                return PREFIX_QUARTZ + "-P";
            } else if (blockName.contains("ashenking_diamond")) {
                return "AK-D";
            } else if (blockName.contains("ashenking_emerald")) {
                return "AK-E";
            } else if (blockName.contains("ashenking_netherite")) {
                return "AK-N";
            } else if (blockName.contains("ashenking_gold")) {
                return "AK-G";
            } else if (blockName.contains("ashenking_pillar")) {
                return "AK-P";
            } else {
                return PREFIX_STONE + "-P";
            }
        } catch (Exception e) {
            return PREFIX_STONE + "-P";
        }
    }

    private void updateCachedWorldDir() {
        try {
            if (!com.kingodogo.buildscape.BuildScape.isServerFullyInitialized()) {
                return;
            }

            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (
                    server != null &&
                            server.isRunning() &&
                            server.getPlayerList().getPlayerCount() > 0
            ) {
                try {
                    Path worldPath = server.getWorldPath(LevelResource.ROOT);
                    if (worldPath != null) {
                        File buildscapeDir = worldPath.resolve(FOLDER_NAME).toFile();
                        if (!buildscapeDir.exists()) {
                            buildscapeDir.mkdirs();
                        }
                        cachedWorldSaveDir = buildscapeDir;
                    }
                } catch (Exception e) {
                }
            }
        } catch (Throwable t) {
        }
    }

    private File getDataFile() {
        return new File(getDataDir(), FILE_NAME);
    }
    
    private File getBackupDataFile() {
        return new File(getDataDir(), BACKUP_FILE_NAME);
    }

    private File getDataDir() {
        try {
            if (cachedWorldSaveDir != null && cachedWorldSaveDir.exists()) {
                return cachedWorldSaveDir;
            }

            if (com.kingodogo.buildscape.BuildScape.isServerFullyInitialized()) {
                MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                if (server != null) {
                    try {
                        Path worldPath = server.getWorldPath(LevelResource.ROOT);
                        if (worldPath != null) {
                            File buildscapeDir = worldPath.resolve(FOLDER_NAME).toFile();
                            if (!buildscapeDir.exists()) {
                                buildscapeDir.mkdirs();
                            }
                            cachedWorldSaveDir = buildscapeDir;
                            return buildscapeDir;
                        }
                    } catch (Exception e) {
                    }
                }
            }

            String configPath = Paths.get(
                    "config",
                    "buildscape",
                    "pillar"
            ).toString();
            File dir = new File(configPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            return dir;
        } catch (Throwable t) {
            return new File(".");
        }
    }
    
    /**
     * Full reset - clears all data. Only called on server stop.
     */
    public static void fullReset() {
        cachedWorldSaveDir = null;
        worldLoadStartTime = System.currentTimeMillis();
        recoveryScheduled = false;
        recoveryScheduledTime = 0L;
        
        if (INSTANCE != null) {
            INSTANCE.pillarData.clear();
            INSTANCE.positionIndex.clear(); // keep index in sync
            INSTANCE.lastLoadedTime = 0L;
            INSTANCE.lastFileSize = 0L;
            INSTANCE.hasLoaded = false;
            INSTANCE.hadColorsOnLoad = false;
            INSTANCE.fileWasDeleted = false;
        }
    }
    
    /**
     * Schedule recovery to run after world load.
     * Recovery will run automatically after RECOVERY_DELAY_MS.
     */
    public static void scheduleRecoveryAfterLoad() {
        recoveryScheduled = true;
        recoveryScheduledTime = System.currentTimeMillis();
    }
    
    /**
     * Check if scheduled recovery should run and execute it.
     * Called from server tick event.
     */
    public static void checkAndRunScheduledRecovery() {
        if (!recoveryScheduled) {
            return;
        }
        
        long elapsed = System.currentTimeMillis() - recoveryScheduledTime;
        if (elapsed < RECOVERY_DELAY_MS) {
            return;
        }
        
        recoveryScheduled = false;
        
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null || !server.isRunning()) {
            return;
        }
        
        PillarIdManager manager = get();
        if (manager == null) {
            return;
        }
        
        if (!manager.hasLoaded()) {
            return;
        }
        
        manager.recoverPillarsFromWorld(server, false); // false = don't clear colors
    }

    private static boolean isWorldReadyForRecovery() {
        if (worldLoadStartTime == 0L) {
            return true;
        }
        long elapsed = System.currentTimeMillis() - worldLoadStartTime;
        return elapsed >= MIN_WORLD_LOAD_TIME_MS;
    }

    public String generatePillarId(String expectedPrefix) {
        if (expectedPrefix == null || expectedPrefix.isEmpty()) {
            expectedPrefix = PREFIX_STONE + "-P";
        }

        String prefix = expectedPrefix;
        Random random = new Random();

        for (int attempt = 0; attempt < 50; attempt++) {
            int num = random.nextInt(9999) + 1;
            String id = prefix + String.format("%04d", num);
            if (!pillarData.containsKey(id)) {
                return id;
            }
        }

        for (int num = 1; num <= 9999; num++) {
            String id = prefix + String.format("%04d", num);
            if (!pillarData.containsKey(id)) {
                return id;
            }
        }

        for (int attempt = 0; attempt < 100; attempt++) {
            char c1 = (char) ('a' + random.nextInt(26));
            char c2 = ALPHANUMERIC.charAt(random.nextInt(36));
            char c3 = ALPHANUMERIC.charAt(random.nextInt(36));
            char c4 = ALPHANUMERIC.charAt(random.nextInt(36));
            String id = prefix + c1 + c2 + c3 + c4;
            if (!pillarData.containsKey(id)) {
                return id;
            }
        }

        for (int i = 10; i < 36; i++) {
            char c1 = ALPHANUMERIC.charAt(i);
            for (int j = 0; j < 36; j++) {
                for (int k = 0; k < 36; k++) {
                    for (int l = 0; l < 36; l++) {
                        String id =
                                prefix +
                                        c1 +
                                        ALPHANUMERIC.charAt(j) +
                                        ALPHANUMERIC.charAt(k) +
                                        ALPHANUMERIC.charAt(l);
                        if (!pillarData.containsKey(id)) {
                            return id;
                        }
                    }
                }
            }
        }

        return prefix + Long.toString(System.currentTimeMillis(), 36);
    }

    private static final String ALPHANUMERIC =
            "0123456789abcdefghijklmnopqrstuvwxyz";

    public String generatePillarId() {
        return generatePillarId(PREFIX_STONE + "-P");
    }

    public PillarData getOrCreatePillarData(Level level, BlockPos pos) {
        // Always orient to the bottom of the stack for consistent ID mapping
        BlockPos basePos = pos;
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof com.kingodogo.buildscape.block.PillarBlock) {
            BlockPos current = pos;
            int limit = 256;
            while (limit > 0 && level.getBlockState(current.below()).getBlock() instanceof com.kingodogo.buildscape.block.PillarBlock) {
                current = current.below();
                limit--;
            }
            basePos = current;
        }

        String dimension = getDimensionKey(level);
        String expectedPrefix = getVariantPrefix(level, basePos);
        String posKey = positionKey(dimension, basePos);

        // O(1) fast path via position index
        String existingId = positionIndex.get(posKey);
        if (existingId != null) {
            PillarData existing = pillarData.get(existingId);
            if (existing != null) {
                if (existing.id != null && existing.id.startsWith(expectedPrefix)) {
                    return existing;
                } else {
                    // Wrong variant prefix — evict and recreate
                    pillarData.remove(existingId);
                    positionIndex.remove(posKey);
                }
            } else {
                positionIndex.remove(posKey); // dangling reference — clean up
            }
        }

        String id = generatePillarId(expectedPrefix);
        PillarData newData = new PillarData(id, dimension, basePos);

        // Populate initial data from the world (type, displayed item from stack, etc.)
        net.minecraft.world.level.block.entity.BlockEntity be = level.getBlockEntity(basePos);
        if (be instanceof com.kingodogo.buildscape.block.PillarBlockEntity pillar) {
            pillar.setPillarId(id);
            pillar.setChanged();
            syncPatternSettingsFromNBT(pillar, newData);

            // Sync colors too if they exist in the world but not yet in the manager
            List<String> worldColors = pillar.getParticleColors();
            if (worldColors != null && !worldColors.isEmpty()) {
                for (String color : worldColors) {
                    newData.addColor(color);
                }
            }
        }

        pillarData.put(id, newData);
        positionIndex.put(posKey, id); // keep index in sync

        // IMPORTANT: Don't save during recovery - recovery will save once at the end
        // This prevents saving empty colors repeatedly during recovery
        if (!recoveryInProgress) {
            saveImmediate();
        }
        return newData;
    }

    private String positionKey(String dimension, BlockPos pos) {
        return dimension + ":" + pos.getX() + ":" + pos.getY() + ":" + pos.getZ();
    }

    public static String getDimensionKey(Level level) {
        if (level == null) return "unknown";
        return level.dimension().location().toString();
    }

    public java.util.Collection<PillarData> getAllData() {
        return pillarData.values();
    }

    public PillarData getPillarData(String pillarId) {
        if (pillarId == null) return null;
        return pillarData.get(pillarId);
    }

    public String addDyeColor(Level level, BlockPos pos, String colorCode) {
        PillarData data = getOrCreatePillarData(level, pos);

        // Sync items and type whenever dyeing to ensure instant GUI reflection
        net.minecraft.world.level.block.entity.BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof com.kingodogo.buildscape.block.PillarBlockEntity pillar) {
            syncPatternSettingsFromNBT(pillar, data);
        }

        if (data.addColor(colorCode)) {
            // Lock current pattern settings if they are currently following global defaults
            if (data.pattern == null || data.pattern.equals("default")) {
                data.pattern = com.kingodogo.buildscape.config.PillarParticleConfig.get().pattern;
                if (data.pattern == null || data.pattern.isEmpty()) {
                    data.pattern = "ring"; // Safe fallback
                }
            }
            saveImmediate();
            return data.id;
        }
        return null;
    }

    public PillarData getPillarDataByPosition(Level level, BlockPos pos) {
        String dimension = getDimensionKey(level);
        // O(1) lookup via position index instead of iterating all entries
        String id = positionIndex.get(positionKey(dimension, pos));
        return id != null ? pillarData.get(id) : null;
    }

    public String getPillarIdByPosition(Level level, BlockPos pos) {
        PillarData data = getPillarDataByPosition(level, pos);
        return data != null ? data.id : null;
    }

    public void removePillar(String pillarId) {
        if (pillarId != null) {
            PillarData data = pillarData.remove(pillarId);
            if (data != null) {
                // Remove from position index too
                positionIndex.remove(positionKey(data.dimension, new BlockPos(data.x, data.y, data.z)));
                PillarResetHandler.resetPillarFromData(data);
                // Allow saving empty file if user removes all pillars
                if (pillarData.isEmpty()) {
                    allowEmptySave = true;
                }
                saveImmediate();
            }
        }
    }

    public void updateDisplayedItem(String pillarId, String itemResourceId) {
        if (pillarId == null) return;
        PillarData data = pillarData.get(pillarId);
        if (data != null) {
            boolean changed = false;
            if (itemResourceId == null || itemResourceId.isEmpty()) {
                if (data.displayedItem != null) {
                    data.displayedItem = null;
                    changed = true;
                }
            } else if (!itemResourceId.equals(data.displayedItem)) {
                data.displayedItem = itemResourceId;
                changed = true;
            }

            if (changed) {
                data.modifiedTime = System.currentTimeMillis();
                forceSaveImmediate();
            }
        }
    }

    public void removePillarByPosition(Level level, BlockPos pos) {
        String dimension = getDimensionKey(level);
        String posKey = positionKey(dimension, pos);

        // O(1) removal via position index
        String idToRemove = positionIndex.remove(posKey);
        if (idToRemove != null) {
            PillarData dataToReset = pillarData.remove(idToRemove);
            if (dataToReset != null) {
                PillarResetHandler.resetPillarFromData(dataToReset);
            }
            saveImmediate();
        }
    }

    public void clearPillarColors(String pillarId) {
        PillarData data = pillarData.get(pillarId);
        if (data != null) {
            data.clearColors();
            saveImmediate();
        }
    }

    public boolean hasCustomColors(Level level, BlockPos pos) {
        PillarData data = getPillarDataByPosition(level, pos);
        return data != null && data.hasColors();
    }

    public void updateDisplayedItemByPosition(Level level, BlockPos pos, String itemResourceId) {
        PillarData data = getOrCreatePillarData(level, pos);
        if (data != null) {
            boolean changed = false;

            // Sync all settings from NBT whenever item is updated
            net.minecraft.world.level.block.entity.BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof com.kingodogo.buildscape.block.PillarBlockEntity pillar) {
                if (syncPatternSettingsFromNBT(pillar, data)) {
                    changed = true;
                }
            }

            if (itemResourceId == null || itemResourceId.isEmpty()) {
                if (data.displayedItem != null) {
                    data.displayedItem = null;
                    changed = true;
                }
            } else if (!itemResourceId.equals(data.displayedItem)) {
                data.displayedItem = itemResourceId;
                changed = true;
            }

            if (changed) {
                data.modifiedTime = System.currentTimeMillis();
                forceSaveImmediate();
            }
        }
    }

    public void load() {
        if (isServerSynced) {
            return;
        }
        try {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server == null) {
                pillarData.clear();
                lastLoadedTime = 0L;
                lastFileSize = 0L;
                fileWasDeleted = false;
                hasLoaded = false;
                return;
            }

            File file = getDataFile();
            if (hasLoaded && pillarData.isEmpty() && file.exists()) {
                hasLoaded = false;
            }

            if (hasLoaded) {
                return;
            }

            server.execute(() -> {
                try {
                    loadFileAsync(server);
                } catch (Exception e) {
                    System.err.println(
                            "BuildScape: Error in async file load: " + e.getMessage()
                    );
                    e.printStackTrace();
                    hasLoaded = true;
                }
            });
        } catch (Throwable t) {
            System.err.println(
                    "BuildScape: Critical error in load() - will recover from world later: " +
                            t.getMessage()
            );
            t.printStackTrace();
            hasLoaded = true;
            pillarData.clear();
            lastLoadedTime = 0L;
            lastFileSize = 0L;
            fileWasDeleted = true;
        }
    }

    public Set<String> getAllPillarIds() {
        return Collections.unmodifiableSet(pillarData.keySet());
    }

    public int getPillarCount() {
        return pillarData.size();
    }

    private void handleCorruptedFile(
            File file,
            Throwable error,
            String errorType
    ) {
        System.err.println(
                "BuildScape: Pillar data file is corrupted (" +
                        errorType +
                        "). Creating backup and starting fresh."
        );
        System.err.println("BuildScape: Error details: " + error.getMessage());

        pillarData.clear();
        lastLoadedTime = 0L;
        lastFileSize = 0L;

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null && server.isRunning()) {
            server.execute(() -> {
                try {
                    try {
                        File backupFile = new File(
                                file.getParent(),
                                FILE_NAME + ".corrupted." + System.currentTimeMillis()
                        );
                        if (file.exists() && file.length() > 0) {
                            java.nio.file.Files.copy(
                                    file.toPath(),
                                    backupFile.toPath(),
                                    java.nio.file.StandardCopyOption.REPLACE_EXISTING
                            );

                        }
                    } catch (Exception backupEx) {
                        System.err.println(
                                "BuildScape: Failed to backup corrupted file: " +
                                        backupEx.getMessage()
                        );
                    }

                    try {
                        if (file.exists()) {
                            file.delete();
                        }
                    } catch (Exception deleteEx) {
                    }

                    try {
                        saveImmediate();
                    } catch (Exception saveEx) {
                        System.err.println(
                                "BuildScape: Failed to create new pillar data file: " +
                                        saveEx.getMessage()
                        );
                    }
                } catch (Exception e) {
                    System.err.println(
                            "BuildScape: Error in deferred file recovery: " + e.getMessage()
                    );
                }
            });
        } else {
            try {
                try {
                    File backupFile = new File(
                            file.getParent(),
                            FILE_NAME + ".corrupted." + System.currentTimeMillis()
                    );
                    if (file.exists() && file.length() > 0) {
                        java.nio.file.Files.copy(
                                file.toPath(),
                                backupFile.toPath(),
                                java.nio.file.StandardCopyOption.REPLACE_EXISTING
                        );

                    }
                } catch (Exception backupEx) {
                    System.err.println(
                            "BuildScape: Failed to backup corrupted file: " +
                                    backupEx.getMessage()
                    );
                }

                try {
                    if (file.exists()) {
                        file.delete();
                    }
                } catch (Exception deleteEx) {
                }

                try {
                    saveImmediate();
                } catch (Exception saveEx) {
                    System.err.println(
                            "BuildScape: Failed to create new pillar data file (will retry later): " +
                                    saveEx.getMessage()
                    );
                }
            } catch (Exception e) {
                System.err.println(
                        "BuildScape: Error in file recovery: " + e.getMessage()
                );
            }
        }
    }

    private boolean fileWasDeleted = false;

    /**
     * Process loaded data and merge with existing data.
     */
    private void processLoadedData(Map<String, PillarData> loaded, MinecraftServer server, File sourceFile) {
        try {

                // IMPORTANT: Preserve existing colors when reloading
                // If file has empty colors but manager has colors, preserve manager colors
                Map<String, PillarData> existingData = new HashMap<>(pillarData);

                pillarData.clear();
                if (loaded != null && !loaded.isEmpty()) {
                    int migrated = 0;
                    int skipped = 0;

                    for (Map.Entry<String, PillarData> entry : loaded.entrySet()) {
                        try {
                            String id = entry.getKey();
                            PillarData data = entry.getValue();

                            if (id == null || id.isEmpty() || data == null) {
                                skipped++;
                                continue;
                            }

                            boolean needsMigration = false;

                            if (data.dimension == null || data.dimension.isEmpty()) {
                                data.dimension = "minecraft:overworld";
                                needsMigration = true;
                            }

                            // CRITICAL: Preserve colors from file - file is the source of truth for GUI
                            // GSON should have populated data.dyeColors from JSON
                            // Save the original GSON-deserialized colors IMMEDIATELY
                            List<String> originalFileColors = null;
                            if (data.dyeColors != null) {
                                // GSON deserialized something - preserve it exactly as-is
                                originalFileColors = new ArrayList<>(data.dyeColors);
                            }

                            PillarData existing = existingData.get(id);

                            // CRITICAL: File colors take absolute priority - use them if they exist
                            if (originalFileColors != null && !originalFileColors.isEmpty()) {
                                // File has colors - ALWAYS use them, ignore everything else
                                data.dyeColors = originalFileColors;
                            } else {
                                // File is empty or null - initialize and check manager
                                if (data.dyeColors == null) {
                                    data.dyeColors = new ArrayList<>();
                                    needsMigration = true;
                                }

                                // Use manager colors if they exist
                                if (existing != null && existing.hasColors() && existing.dyeColors != null) {
                                    data.dyeColors = new ArrayList<>(existing.dyeColors);
                                }
                            }
                            // If both are empty, keep empty (will sync from NBT later)

                            try {
                                BlockPos pos = data.getBlockPos();
                                if (pos == null) {
                                    skipped++;
                                    continue;
                                }
                            } catch (Exception e) {
                                skipped++;
                                continue;
                            }

                            if (!id.matches("^[A-Z-]+[a-z0-9]+$")) {
                                skipped++;
                                continue;
                            }

                            // FINAL SAFEGUARD: Ensure colors are preserved before putting into map
                            // Double-check that colors are set (file colors take priority)
                            if (data.dyeColors == null || data.dyeColors.isEmpty()) {
                                // If colors are empty, check if we have original file colors
                                if (originalFileColors != null && !originalFileColors.isEmpty()) {
                                    data.dyeColors = new ArrayList<>(originalFileColors);
                                } else if (data.dyeColors == null) {
                                    data.dyeColors = new ArrayList<>();
                                }
                            }

                            pillarData.put(id, data);
                            // Keep position index in sync with loaded data
                            positionIndex.put(positionKey(data.dimension, data.getBlockPos()), id);
                            if (needsMigration) {
                                migrated++;
                            }
                        } catch (Exception e) {
                            skipped++;
                            continue;
                        }
                    }

                    if (migrated > 0) {
                        if (server != null && server.isRunning()) {
                            server.execute(() -> {
                                try {
                                    saveImmediate();
                                } catch (Exception e) {
                                }
                            });
                        }
                    }

                    if (skipped > 0) {
                    }

                    // Log how many entries were loaded and how many have colors
                    int loadedCount = pillarData.size();
                    int colorsCount = 0;
                    for (PillarData data : pillarData.values()) {
                        if (data != null && data.hasColors()) {
                            colorsCount++;
                        }
                    }

                    // Track if we had colors when we loaded
                    hadColorsOnLoad = (colorsCount > 0);
                }

                if (pillarData.isEmpty()) {
                    fileWasDeleted = true;
                    hadColorsOnLoad = false;
                } else {
                    fileWasDeleted = false;
                }

                if (sourceFile != null) {
                    lastLoadedTime = sourceFile.lastModified();
                    lastFileSize = sourceFile.length();
                }

                hasLoaded = true;

                updateCachedWorldDir();

                // IMPORTANT: Don't sync from NBT here - block entities might not be loaded yet
                // Colors will be synced from NBT during recovery or when GUI opens
                // This prevents clearing colors before block entities are ready

                // Schedule recovery to run after world load (to add any missing pillars and sync colors)
                scheduleRecoveryAfterLoad();

        } catch (Exception e) {
            fileWasDeleted = true;
            System.err.println(
                    "BuildScape: Error processing loaded data: " + e.getMessage()
            );
            e.printStackTrace();
            pillarData.clear();
            lastLoadedTime = 0L;
            lastFileSize = 0L;
            hasLoaded = true;
        } catch (Throwable t) {
            System.err.println(
                    "BuildScape: Critical error in loadFileAsync() - will recover after world is fully loaded: " +
                            t.getMessage()
            );
            t.printStackTrace();
            fileWasDeleted = true;
            pillarData.clear();
            lastLoadedTime = 0L;
            lastFileSize = 0L;
            hasLoaded = true;
        }
    }

    private void loadFileAsync(MinecraftServer server) {
        try {
            // IMPORTANT: Load from main file only (pillar-ids.dat)
            // Backup file is separate and only saved on world save/server close
            File file = getDataFile();
            
            Map<String, PillarData> loadedData = null;
            File sourceFile = null;
            
            // Load from main file first
            if (file.exists() && file.length() > 0) {
                try {
                    loadedData = loadFromFile(file);
                    if (loadedData != null && !loadedData.isEmpty()) {
                        sourceFile = file;
                    }
                } catch (Exception e) {
                    System.err.println("BuildScape: Error loading main file: " + e.getMessage());
                }
            }
            
            // CRITICAL: If main file has empty colors, try backup file (backup is preferred for GUI)
            // Check if main file has colors
            boolean mainFileHasColors = false;
            if (loadedData != null) {
                for (PillarData data : loadedData.values()) {
                    if (data != null && data.hasColors()) {
                        mainFileHasColors = true;
                        break;
                    }
                }
            }
            
            // If main file has no colors, try backup file
            if (!mainFileHasColors) {
                File backupFile = getBackupDataFile();
                if (backupFile.exists() && backupFile.length() > 0) {
                    try {
                        Map<String, PillarData> backupData = loadFromFile(backupFile);
                        if (backupData != null && !backupData.isEmpty()) {
                            // Check if backup has colors
                            boolean backupHasColors = false;
                            for (PillarData data : backupData.values()) {
                                if (data != null && data.hasColors()) {
                                    backupHasColors = true;
                                    break;
                                }
                            }
                            
                            // If backup has colors, use it (backup is preferred for GUI)
                            if (backupHasColors) {
                                loadedData = backupData;
                                sourceFile = backupFile;
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("BuildScape: Error loading backup file: " + e.getMessage());
                    }
                }
            }
            
            // If file failed or doesn't exist, start fresh
            if (loadedData == null || loadedData.isEmpty()) {
                fileWasDeleted = true;
                pillarData.clear();
                lastLoadedTime = 0L;
                lastFileSize = 0L;
                hasLoaded = true;
                return;
            }
            
            // Process loaded data
            processLoadedData(loadedData, server, sourceFile);
            
        } catch (Throwable t) {
            System.err.println(
                    "BuildScape: Critical error in loadFileAsync() - will recover after world is fully loaded: " +
                            t.getMessage()
            );
            t.printStackTrace();
            fileWasDeleted = true;
            pillarData.clear();
            lastLoadedTime = 0L;
            lastFileSize = 0L;
            hasLoaded = true;
        }
    }
    
    /**
     * Load data from a specific file.
     */
    private Map<String, PillarData> loadFromFile(File file) throws Exception {
        try (
                FileInputStream fis = new FileInputStream(file);
                InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
                BufferedReader reader = new BufferedReader(isr)
        ) {
            Type type = new TypeToken<Map<String, PillarData>>() {}.getType();
            return GSON.fromJson(reader, type);
        }
    }
    
    public void saveImmediate() {
        try {
            // IMPORTANT: Don't save during recovery - recovery will save once at the end after syncing colors
            if (recoveryInProgress) {
                return;
            }

            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server == null) {
                return;
            }

            if (!com.kingodogo.buildscape.BuildScape.isServerFullyInitialized()) {
                return;
            }

            // Log what we're saving
            int saveCount = pillarData.size();
            int colorsCount = 0;
            for (PillarData data : pillarData.values()) {
                if (data != null && data.hasColors()) {
                    colorsCount++;
                }
            }

            // SAFEGUARD: Only prevent save if we have a TOTAL loss of data (count dropped to 0)
            // AND we know the file previously had lots of data.
            // UNLESS the user explicitly removed all pillars (allowEmptySave = true)
            if (saveCount == 0 && lastFileSize > 0 && !allowEmptySave) {
                // Check the file directly to see if it has colors
                boolean fileHasColors = false;
                try {
                    File mainFile = getDataFile();
                    if (mainFile.exists() && mainFile.length() > 0) {
                        try (FileInputStream fis = new FileInputStream(mainFile);
                             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
                             BufferedReader reader = new BufferedReader(isr)) {
                            Type type = new TypeToken<Map<String, PillarData>>() {
                            }.getType();
                            Map<String, PillarData> fileData = GSON.fromJson(reader, type);
                            if (fileData != null && !fileData.isEmpty()) {
                                fileHasColors = true;
                            }
                        }
                    }
                } catch (Exception e) {
                    // Ignore - allow save to proceed if we can't check file
                }

                // If we had data and now have 0, this might be a corruption/fail-safe trigger
                // Only prevent if we haven't finished loading our data yet
                if (fileHasColors && !hasLoaded) {
                    // BuildScape.getLogger().warn("PillarIdManager: TOTAL DATA LOSS DETECTED - Preventing save and reloading!");
                    load();
                    return;
                }
            }

            // Reset the flag after saving
            if (allowEmptySave) {
                allowEmptySave = false;
            }


            // Save to main file only (backup file is saved separately on world save/server close)
            saveToFile(getDataFile(), FILE_NAME);

            // Update timestamps from main file
            File mainFile = getDataFile();
            if (mainFile.exists()) {
                lastLoadedTime = mainFile.lastModified();
                lastFileSize = mainFile.length();
            }

            // Sync with all clients instantly
            com.kingodogo.buildscape.network.ModMessages.INSTANCE.send(
                    net.minecraftforge.network.PacketDistributor.ALL.noArg(),
                    new com.kingodogo.buildscape.network.SyncPillarIdsPacket(getAllPillarDataForSync())
            );
        } catch (Throwable t) {
        }
    }

    private void scheduleRecoveryFromWorld(
            MinecraftServer server,
            boolean clearColors
    ) {
    }

    public int clearAllPillarIdsFromWorld(MinecraftServer server) {
        if (server == null || !server.isRunning()) {
            return 0;
        }

        if (server.getPlayerList().getPlayerCount() == 0) {
            return 0;
        }

        int clearedCount = 0;

        try {

            for (net.minecraft.server.level.ServerLevel level : server.getAllLevels()) {
                if (level == null) continue;

                try {
                    int chunkRange = 64;
                    net.minecraft.core.BlockPos spawnPos = level.getSharedSpawnPos();
                    net.minecraft.world.level.ChunkPos spawnChunk = spawnPos != null
                            ? new net.minecraft.world.level.ChunkPos(spawnPos)
                            : new net.minecraft.world.level.ChunkPos(0, 0);

                    for (
                            int chunkX = spawnChunk.x - chunkRange;
                            chunkX <= spawnChunk.x + chunkRange;
                            chunkX++
                    ) {
                        for (
                                int chunkZ = spawnChunk.z - chunkRange;
                                chunkZ <= spawnChunk.z + chunkRange;
                                chunkZ++
                        ) {
                            net.minecraft.world.level.ChunkPos chunkPos =
                                    new net.minecraft.world.level.ChunkPos(chunkX, chunkZ);

                            if (!level.hasChunkAt(chunkPos.getWorldPosition())) {
                                continue;
                            }

                            try {
                                net.minecraft.world.level.chunk.ChunkAccess chunkAccess =
                                        level.getChunk(chunkX, chunkZ);

                                if (
                                        !(chunkAccess instanceof net.minecraft.world.level.chunk.LevelChunk chunk)
                                ) {
                                    continue;
                                }

                                if (
                                        !chunkAccess
                                                .getStatus()
                                                .isOrAfter(net.minecraft.world.level.chunk.ChunkStatus.FULL)
                                ) {
                                    continue;
                                }

                                for (net.minecraft.world.level.block.entity.BlockEntity be : chunk
                                        .getBlockEntities()
                                        .values()) {
                                    if (
                                            be instanceof com.kingodogo.buildscape.block.PillarBlockEntity pillarBE
                                    ) {

                                        if (
                                                pillarBE.getPillarId() != null &&
                                                        !pillarBE.getPillarId().isEmpty()
                                        ) {
                                            pillarBE.clearLocalStateOnly();
                                            pillarBE.setChanged();

                                            level.sendBlockUpdated(
                                                    be.getBlockPos(),
                                                    level.getBlockState(be.getBlockPos()),
                                                    level.getBlockState(be.getBlockPos()),
                                                    3
                                            );

                                            clearedCount++;
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                continue;
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println(
                            "BuildScape: Error scanning chunks to clear pillar IDs: " +
                                    e.getMessage()
                    );
                }
            }

        } catch (Exception e) {
            System.err.println(
                    "BuildScape: Error clearing pillar IDs from world: " + e.getMessage()
            );
            e.printStackTrace();
        }

        return clearedCount;
    }

    public void recoverPillarsFromWorld(
            MinecraftServer server,
            boolean clearColors
    ) {
        if (server == null || !server.isRunning()) {
            return;
        }

        if (server.getPlayerList().getPlayerCount() == 0) {
            return;
        }

        try {
            if (clearColors) {
                int clearedCount = clearAllPillarIdsFromWorld(server);
                saveImmediate();
                return;
            }

            recoveryInProgress = true; // Prevent saves during recovery
            int recoveredCount = 0;
            int skippedCount = 0;
            int colorClearedCount = 0;

            for (net.minecraft.server.level.ServerLevel level : server.getAllLevels()) {
                if (level == null) continue;

                String dimensionKey = getDimensionKey(level);

                try {
                    java.util.Set<
                            net.minecraft.world.level.block.entity.BlockEntity
                            > allBlockEntities = new java.util.HashSet<>();

                    int chunkRange = 64;
                    net.minecraft.core.BlockPos spawnPos = level.getSharedSpawnPos();
                    net.minecraft.world.level.ChunkPos spawnChunk = spawnPos != null
                            ? new net.minecraft.world.level.ChunkPos(spawnPos)
                            : new net.minecraft.world.level.ChunkPos(0, 0);

                    for (
                            int chunkX = spawnChunk.x - chunkRange;
                            chunkX <= spawnChunk.x + chunkRange;
                            chunkX++
                    ) {
                        for (
                                int chunkZ = spawnChunk.z - chunkRange;
                                chunkZ <= spawnChunk.z + chunkRange;
                                chunkZ++
                        ) {
                            net.minecraft.world.level.ChunkPos chunkPos =
                                    new net.minecraft.world.level.ChunkPos(chunkX, chunkZ);

                            if (!level.hasChunkAt(chunkPos.getWorldPosition())) {
                                continue;
                            }

                            try {
                                net.minecraft.world.level.chunk.ChunkAccess chunkAccess =
                                        level.getChunk(chunkX, chunkZ);

                                if (
                                        !(chunkAccess instanceof net.minecraft.world.level.chunk.LevelChunk chunk)
                                ) {
                                    continue;
                                }

                                if (
                                        !chunkAccess
                                                .getStatus()
                                                .isOrAfter(net.minecraft.world.level.chunk.ChunkStatus.FULL)
                                ) {
                                    continue;
                                }

                                for (net.minecraft.world.level.block.entity.BlockEntity be : chunk
                                        .getBlockEntities()
                                        .values()) {
                                    if (
                                            be instanceof
                                                    com.kingodogo.buildscape.block.PillarBlockEntity
                                    ) {
                                        allBlockEntities.add(be);
                                    }
                                }
                            } catch (Exception e) {
                                continue;
                            }
                        }
                    }

                    for (net.minecraft.world.level.block.entity.BlockEntity be : allBlockEntities) {
                        if (
                                be instanceof com.kingodogo.buildscape.block.PillarBlockEntity pillarBE
                        ) {
                            try {

                                String pillarId = pillarBE.getPillarId();
                                if (pillarId == null || pillarId.isEmpty()) {
                                    skippedCount++;
                                    continue;
                                }

                                BlockPos pos = be.getBlockPos();

                                // Check if pillar already exists in manager (by ID)
                                PillarData existingData = pillarData.get(pillarId);

                                if (existingData != null) {
                                    // Pillar exists - update it, don't create duplicate
                                    boolean positionChanged = !(
                                            existingData.dimension.equals(dimensionKey) &&
                                                    existingData.x == pos.getX() &&
                                                    existingData.y == pos.getY() &&
                                                    existingData.z == pos.getZ()
                                    );
                                    
                                    if (positionChanged) {

                                        existingData.dimension = dimensionKey;
                                        existingData.x = pos.getX();
                                        existingData.y = pos.getY();
                                        existingData.z = pos.getZ();
                                    }
                                    
                                    // Update colors if NBT has colors (preserve manager colors if NBT is empty)
                                    if (!clearColors) {
                                        java.util.List<String> pillarColors =
                                                pillarBE.getParticleColors();
                                        if (pillarColors != null && !pillarColors.isEmpty()) {
                                            // NBT has colors - sync them (only if different)
                                            boolean colorsChanged = false;
                                            if (existingData.dyeColors == null || existingData.dyeColors.size() != pillarColors.size()) {
                                                colorsChanged = true;
                                            } else {
                                                for (int i = 0; i < pillarColors.size(); i++) {
                                                    String nbtColor = pillarColors.get(i);
                                                    String managerColor = i < existingData.dyeColors.size() ? existingData.dyeColors.get(i) : null;
                                                    if (nbtColor == null || !nbtColor.equals(managerColor)) {
                                                        colorsChanged = true;
                                                        break;
                                                    }
                                                }
                                            }
                                            
                                            if (colorsChanged) {
                                                existingData.clearColors();
                                                for (String color : pillarColors) {
                                                    if (color != null && !color.isEmpty()) {
                                                        existingData.addColor(color);
                                                    }
                                                }
                                            }
                                        }
                                        // If NBT doesn't have colors, preserve manager colors (do nothing)
                                    } else if (clearColors && existingData.hasColors()) {
                                        existingData.clearColors();
                                        colorClearedCount++;
                                    }
                                    
                                    // Don't increment recoveredCount - this is an update, not a new recovery
                                    continue;
                                }

                                // New pillar - create data
                                PillarData data = new PillarData(pillarId, dimensionKey, pos);

                                if (clearColors) {
                                    data.clearColors();
                                    colorClearedCount++;
                                } else {
                                    java.util.List<String> pillarColors =
                                            pillarBE.getParticleColors();
                                    if (pillarColors != null && !pillarColors.isEmpty()) {
                                        for (String color : pillarColors) {
                                            if (color != null && !color.isEmpty()) {
                                                data.addColor(color);
                                            }
                                        }
                                    }
                                }

                                pillarData.put(pillarId, data);
                                recoveredCount++;
                            } catch (Exception e) {
                                System.err.println(
                                        "BuildScape: Error processing pillar: " + e.getMessage()
                                );
                                skippedCount++;
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println(
                            "BuildScape: Error scanning chunks for pillars: " + e.getMessage()
                    );
                    skippedCount++;
                }
            }

            // IMPORTANT: Sync colors from NBT BEFORE saving
            // This ensures colors are loaded from NBT and saved to file
            syncColorsFromNBTToManager(server);
            
            // Allow final save after syncing colors
            recoveryInProgress = false;
            
            if (recoveredCount > 0 || colorClearedCount > 0) {
                saveImmediate();
            } else {
            }

            // Don't call syncAllLoadedPillars here - we already synced colors above
        } catch (Exception e) {
            System.err.println(
                    "BuildScape: Error during pillar recovery: " + e.getMessage()
            );
            e.printStackTrace();
        } finally {
            // Always reset flag, even if recovery failed
            recoveryInProgress = false;
        }
    }

    public void save() {
        saveImmediate();
    }

    /**
     * Force save pillar data regardless of player count or server state.
     * Used during server shutdown when players have already disconnected
     * and the server is flagged as stopping.
     * CRITICAL: Uses cachedWorldSaveDir directly because getDataDir() has
     * playerCount>0 check that would redirect to fallback config dir.
     */
    public void forceSaveImmediate() {
        try {
            if (recoveryInProgress) {
                return;
            }

            if (!hasLoaded) {
                return;
            }

            int saveCount = pillarData.size();
            if (saveCount == 0) {
                return;
            }

            // Use cached world save dir directly - getDataDir() would fail
            // because playerCount==0 during shutdown
            File saveDir = cachedWorldSaveDir;
            if (saveDir == null || !saveDir.exists()) {
                // Fallback: try to get from server path directly
                try {
                    MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                    if (server != null) {
                        Path worldPath = server.getWorldPath(LevelResource.ROOT);
                        if (worldPath != null) {
                            saveDir = worldPath.resolve(FOLDER_NAME).toFile();
                            if (!saveDir.exists()) {
                                saveDir.mkdirs();
                            }
                        }
                    }
                } catch (Exception e) {
                    // Last resort
                }
            }

            if (saveDir == null || !saveDir.exists()) {
                System.err.println("BuildScape: forceSaveImmediate - Cannot find save directory!");
                return;
            }

            File saveFile = new File(saveDir, FILE_NAME);
            saveToFile(saveFile, FILE_NAME);

            // Also save backup
            File backupFile = new File(saveDir, BACKUP_FILE_NAME);
            saveToFile(backupFile, BACKUP_FILE_NAME);

            if (saveFile.exists()) {
                lastLoadedTime = saveFile.lastModified();
                lastFileSize = saveFile.length();
            }

            // Sync with all clients instantly
            com.kingodogo.buildscape.network.ModMessages.INSTANCE.send(
                    net.minecraftforge.network.PacketDistributor.ALL.noArg(),
                    new com.kingodogo.buildscape.network.SyncPillarIdsPacket(getAllPillarDataForSync())
            );
        } catch (Throwable t) {
            System.err.println("BuildScape: Error in forceSaveImmediate: " + t.getMessage());
            t.printStackTrace();
        }
    }

    public void checkAndReload() {
        // Check main file only (backup file is separate, only saved on world save/server close)
        File mainFile = getDataFile();

        if (mainFile.exists()) {
            long currentModified = mainFile.lastModified();
            long currentSize = mainFile.length();
            if (currentModified != lastLoadedTime || currentSize != lastFileSize) {
                if (!isServerSynced) {
                    load();
                }
            }
        }
    }

    /**
     * Save pillar data to a specific file.
     */
    private void saveToFile(File file, String tempFileName) {
        try {
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            File tempFile = new File(file.getParentFile(), tempFileName + ".tmp");

            try (
                    FileOutputStream fos = new FileOutputStream(tempFile);
                    OutputStreamWriter osw = new OutputStreamWriter(
                            fos,
                            StandardCharsets.UTF_8
                    );
                    BufferedWriter writer = new BufferedWriter(osw)
            ) {
                GSON.toJson(pillarData, writer);
                writer.flush();
                osw.flush();
                fos.flush();

                try {
                    FileChannel channel = fos.getChannel();
                    channel.force(true);
                } catch (Exception forceEx) {
                }
            }

            try {
                if (file.exists()) {
                    file.delete();
                }
                boolean renamed = tempFile.renameTo(file);

                if (!renamed) {
                    try {
                        tempFile.delete();
                    } catch (Exception cleanupEx) {
                    }
                }
            } catch (Exception renameEx) {
                try {
                    tempFile.delete();
                } catch (Exception cleanupEx) {
                }
            }
        } catch (Exception e) {
            System.err.println("BuildScape: Error saving to " + file.getName() + ": " + e.getMessage());
        }
    }
    
    public void forceReload() {
        load();
    }

    public void syncAllLoadedPillars(
            net.minecraft.server.MinecraftServer server
    ) {
        if (server == null || !server.isRunning()) {
            return;
        }

        if (server.getPlayerList().getPlayerCount() == 0) {
            return;
        }

        try {
            int syncedCount = 0;
            int skippedCount = 0;

            for (net.minecraft.server.level.ServerLevel level : server.getAllLevels()) {
                if (level == null) continue;

                if (!server.isRunning()) break;

                String dimensionKey = getDimensionKey(level);

                for (PillarData data : pillarData.values()) {
                    if (data == null) continue;
                    if (!data.dimension.equals(dimensionKey)) continue;

                    try {
                        BlockPos pos = data.getBlockPos();

                        if (!level.hasChunkAt(pos)) {
                            skippedCount++;
                            continue;
                        }

                        net.minecraft.world.level.chunk.ChunkAccess chunk = level.getChunk(
                                pos
                        );
                        if (
                                !(chunk instanceof net.minecraft.world.level.chunk.LevelChunk)
                        ) {
                            skippedCount++;
                            continue;
                        }

                        if (
                                !chunk
                                        .getStatus()
                                        .isOrAfter(net.minecraft.world.level.chunk.ChunkStatus.FULL)
                        ) {
                            skippedCount++;
                            continue;
                        }

                        net.minecraft.world.level.block.entity.BlockEntity be =
                                level.getBlockEntity(pos);
                        if (
                                be instanceof com.kingodogo.buildscape.block.PillarBlockEntity pillarBE
                        ) {

                            // Sync ALL data from manager (colors, pattern, speed, spread, etc.)
                            pillarBE.syncFromData(data);
                            syncedCount++;
                        }
                    } catch (Exception e) {
                        System.err.println(
                                "BuildScape: Error syncing pillar " +
                                        (data != null ? data.id : "unknown") +
                                        ": " +
                                        e.getMessage()
                        );
                    }
                }
            }

        } catch (Exception e) {
            System.err.println(
                    "BuildScape: Error in syncAllLoadedPillars: " + e.getMessage()
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Save the backup file (only called on world save/server close).
     */
    public void saveBackupFile() {
        try {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server == null || !server.isRunning()) {
                return;
            }
            
            if (!hasLoaded) {
                return;
            }
            
            int saveCount = pillarData.size();
            int colorsCount = 0;
            for (PillarData data : pillarData.values()) {
                if (data != null && data.hasColors()) {
                    colorsCount++;
                }
            }
            
            
            // Save to backup file only
            saveToFile(getBackupDataFile(), BACKUP_FILE_NAME);
        } catch (Throwable t) {
            System.err.println("BuildScape: Error saving backup file: " + t.getMessage());
        }
    }

    public void cleanupOrphans(Level level) {
    }

    /**
     * Syncs ALL settings (colors, pattern, speed, spread, intensity, max_particle_color)
     * FROM block entity NBT TO manager for all loaded pillars.
     * This ensures the manager has all settings that exist in NBT after world load,
     * so the GUI can display them correctly.
     */
    public void syncColorsFromNBTToManager(MinecraftServer server) {
        if (server == null) {
            return;
        }

        // syncColorsFromNBTToManager should run even when players are not online (e.g. during startup/shutdown)
        // to ensure manager's data is consistent with the world.

        // IMPORTANT: Don't sync if manager hasn't loaded yet - this prevents clearing colors before load
        if (!hasLoaded()) {
            return;
        }

        try {
            int syncedCount = 0;
            int preservedCount = 0;
            int patternSyncedCount = 0; // Track pattern/item changes separately

            for (net.minecraft.server.level.ServerLevel level : server.getAllLevels()) {
                if (level == null) continue;

                String dimensionKey = getDimensionKey(level);

                // Iterate through all pillar data in manager
                for (PillarData data : pillarData.values()) {
                    if (data == null) continue;
                    if (!data.dimension.equals(dimensionKey)) continue;

                    // Preserve existing colors count for logging
                    int existingColorCount = (data.dyeColors != null) ? data.dyeColors.size() : 0;

                    try {
                        BlockPos pos = data.getBlockPos();

                        if (!level.isLoaded(pos)) {
                            // Chunk not loaded - preserve manager colors
                            preservedCount++;
                            continue;
                        }

                        net.minecraft.world.level.chunk.ChunkAccess chunk = level.getChunk(pos);
                        if (!(chunk instanceof net.minecraft.world.level.chunk.LevelChunk)) {
                            // Chunk not ready - preserve manager colors
                            preservedCount++;
                            continue;
                        }

                        if (!chunk.getStatus().isOrAfter(net.minecraft.world.level.chunk.ChunkStatus.FULL)) {
                            // Chunk not fully loaded - preserve manager colors
                            preservedCount++;
                            continue;
                        }

                        net.minecraft.world.level.block.entity.BlockEntity be = level.getBlockEntity(pos);
                        if (!(be instanceof com.kingodogo.buildscape.block.PillarBlockEntity pillarBE)) {
                            // No block entity - preserve manager colors
                            preservedCount++;
                            continue;
                        }

                        // Find the bottom of the stack to get the actual block entity with colors
                        BlockPos bottomPos = pillarBE.findStackBottom();
                        net.minecraft.world.level.block.entity.BlockEntity bottomBE = level.getBlockEntity(bottomPos);

                        if (!(bottomBE instanceof com.kingodogo.buildscape.block.PillarBlockEntity bottomPillarBE)) {
                            // No bottom block entity - preserve manager colors
                            preservedCount++;
                            continue;
                        }

                        // Get colors from NBT (block entity at bottom of stack)
                        java.util.List<String> nbtColors = bottomPillarBE.getParticleColors();

                        // IMPORTANT: Only sync if NBT has colors
                        // If NBT is empty or null, preserve manager colors (don't clear them)
                        if (nbtColors != null && !nbtColors.isEmpty()) {
                            // Check if manager colors match NBT colors
                            boolean needsSync = false;
                            if (data.dyeColors == null || data.dyeColors.isEmpty()) {
                                // Manager has no colors, NBT has colors - sync
                                needsSync = true;
                            } else if (data.dyeColors.size() != nbtColors.size()) {
                                // Different number of colors - sync
                                needsSync = true;
                            } else {
                                // Compare colors
                                for (int i = 0; i < nbtColors.size(); i++) {
                                    String nbtColor = nbtColors.get(i);
                                    String managerColor = i < data.dyeColors.size() ? data.dyeColors.get(i) : null;
                                    if (nbtColor == null || !nbtColor.equals(managerColor)) {
                                        needsSync = true;
                                        break;
                                    }
                                }
                            }

                            if (needsSync) {
                                // Sync colors FROM NBT TO manager
                                data.clearColors();
                                for (String color : nbtColors) {
                                    if (color != null && !color.isEmpty()) {
                                        data.addColor(color);
                                    }
                                }
                                syncedCount++;
                            } else {
                                // Colors already match - preserve
                                preservedCount++;
                            }

                            // Also sync pattern settings and items from NBT
                            if (syncPatternSettingsFromNBT(bottomPillarBE, data)) {
                                patternSyncedCount++;
                            }
                        } else {
                            // NBT is empty or null - preserve manager colors (do nothing)
                            if (existingColorCount > 0) {
                                preservedCount++;
                            }
                            // Still try to sync pattern settings and items even if colors are empty
                            if (syncPatternSettingsFromNBT(bottomPillarBE, data)) {
                                patternSyncedCount++;
                            }
                        }
                    } catch (Exception e) {
                        // Error accessing block entity - preserve manager colors
                        preservedCount++;
                        System.err.println(
                                "BuildScape: Error syncing colors from NBT for pillar " +
                                        (data != null ? data.id : "unknown") +
                                        ": " + e.getMessage()
                        );
                    }
                }
            }

            // Save if ANY data changed (colors, patterns, or items)
            if (syncedCount > 0 || patternSyncedCount > 0) {
                saveImmediate();
            } else if (preservedCount > 0) {
            }
        } catch (Exception e) {
            System.err.println(
                    "BuildScape: Error in syncColorsFromNBTToManager: " + e.getMessage()
            );
            e.printStackTrace();
        }
    }

    /**
     * Loads colors directly from NBT for all loaded pillar block entities.
     * This is called after loading the file to populate colors from the actual world data.
     * Colors are loaded directly from NBT, not from the file.
     */
    public void loadColorsFromNBT(MinecraftServer server) {
        if (server == null || !server.isRunning()) {
            return;
        }

        if (server.getPlayerList().getPlayerCount() == 0) {
            return;
        }

        try {
            int loadedCount = 0;

            for (net.minecraft.server.level.ServerLevel level : server.getAllLevels()) {
                if (level == null) continue;

                String dimensionKey = getDimensionKey(level);

                // Iterate through all pillar data in manager
                for (PillarData data : pillarData.values()) {
                    if (data == null) continue;
                    if (!data.dimension.equals(dimensionKey)) continue;

                    try {
                        BlockPos pos = data.getBlockPos();

                        if (!level.isLoaded(pos)) {
                            continue;
                        }

                        net.minecraft.world.level.chunk.ChunkAccess chunk = level.getChunk(pos);
                        if (!(chunk instanceof net.minecraft.world.level.chunk.LevelChunk)) {
                            continue;
                        }

                        if (!chunk.getStatus().isOrAfter(net.minecraft.world.level.chunk.ChunkStatus.FULL)) {
                            continue;
                        }

                        net.minecraft.world.level.block.entity.BlockEntity be = level.getBlockEntity(pos);
                        if (!(be instanceof com.kingodogo.buildscape.block.PillarBlockEntity pillarBE)) {
                            continue;
                        }

                        // Find the bottom of the stack to get the actual block entity with colors
                        BlockPos bottomPos = pillarBE.findStackBottom();
                        net.minecraft.world.level.block.entity.BlockEntity bottomBE = level.getBlockEntity(bottomPos);
                        
                        if (!(bottomBE instanceof com.kingodogo.buildscape.block.PillarBlockEntity bottomPillarBE)) {
                            continue;
                        }

                        // Get colors directly from NBT
                        java.util.List<String> nbtColors = bottomPillarBE.getParticleColors();
                        
                        // Load colors from NBT into manager (if NBT has colors)
                        // If NBT is empty, preserve colors from file
                        if (nbtColors != null && !nbtColors.isEmpty()) {
                            // NBT has colors - use them (overwrite file colors)
                            data.clearColors();
                            for (String color : nbtColors) {
                                if (color != null && !color.isEmpty()) {
                                    data.addColor(color);
                                }
                            }
                            loadedCount++;
                        } else {
                            // NBT is empty - preserve colors from file (if any)
                            int fileColorCount = (data.dyeColors != null) ? data.dyeColors.size() : 0;
                            if (fileColorCount > 0) {
                            }
                        }
                    } catch (Exception e) {
                        System.err.println(
                                "BuildScape: Error loading colors from NBT for pillar " +
                                        (data != null ? data.id : "unknown") +
                                        ": " + e.getMessage()
                        );
                    }
                }
            }

            // Only save if colors were actually loaded from NBT
            // Don't save if no colors were loaded - this preserves file colors
            if (loadedCount > 0) {
                saveImmediate();
            } else {
            }
        } catch (Exception e) {
            System.err.println(
                    "BuildScape: Error in loadColorsFromNBT: " + e.getMessage()
            );
            e.printStackTrace();
        }
    }

    /**
     * Syncs pattern and item settings from a BlockEntity to a PillarData object.
     * Returns true if any changes were made.
     */
    public boolean syncPatternSettingsFromNBT(
            com.kingodogo.buildscape.block.PillarBlockEntity pillarBE,
            PillarData data
    ) {
        if (pillarBE == null || data == null) {
            return false;
        }

        boolean needsSave = false;

        // Sync pillar type
        String pillarType = net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(pillarBE.getBlockState().getBlock().asItem()).toString();
        if (data.pillarType == null || !data.pillarType.equals(pillarType)) {
            data.pillarType = pillarType;
            needsSave = true;
        }

        // Sync pattern
        String nbtPattern = pillarBE.getParticlePattern();
        if (nbtPattern != null && !nbtPattern.isEmpty()) {
            if (data.pattern == null || !data.pattern.equals(nbtPattern)) {
                data.pattern = nbtPattern;
                needsSave = true;
            }
        }

        // Sync pattern speed
        Double nbtSpeed = pillarBE.getPatternSpeed();
        if (nbtSpeed != null) {
            if (data.pattern_speed == null || !data.pattern_speed.equals(nbtSpeed)) {
                data.pattern_speed = nbtSpeed;
                needsSave = true;
            }
        }

        // Sync pattern spread
        Double nbtSpread = pillarBE.getPatternSpread();
        if (nbtSpread != null) {
            if (data.pattern_spread == null || !data.pattern_spread.equals(nbtSpread)) {
                data.pattern_spread = nbtSpread;
                needsSave = true;
            }
        }

        // Sync pattern intensity
        Double nbtIntensity = pillarBE.getPatternIntensity();
        if (nbtIntensity != null) {
            if (data.pattern_intensity == null || !data.pattern_intensity.equals(nbtIntensity)) {
                data.pattern_intensity = nbtIntensity;
                needsSave = true;
            }
        }

        // Sync max particle colors (from the number of colors in NBT)
        java.util.List<String> nbtColors = pillarBE.getParticleColors();
        if (nbtColors != null && !nbtColors.isEmpty()) {
            int nbtColorCount = nbtColors.size();
            if (data.max_particle_color == null || data.max_particle_color != nbtColorCount) {
                data.max_particle_color = nbtColorCount;
                needsSave = true;
            }
        }

        // Sync displayed item - item is always moved to the top of the stack
        net.minecraft.core.BlockPos topPos = pillarBE.findStackTop();
        net.minecraft.world.level.block.entity.BlockEntity topBE = pillarBE.getLevel().getBlockEntity(topPos);
        net.minecraft.world.item.ItemStack displayedItem = net.minecraft.world.item.ItemStack.EMPTY;

        if (topBE instanceof com.kingodogo.buildscape.block.PillarBlockEntity topPillar) {
            displayedItem = topPillar.getDisplayedItem();
        } else {
            displayedItem = pillarBE.getDisplayedItem();
        }

        if (displayedItem != null && !displayedItem.isEmpty()) {
            net.minecraft.resources.ResourceLocation key = net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(displayedItem.getItem());
            String itemId = key != null ? key.toString() : null;
            if (itemId != null && (data.displayedItem == null || !data.displayedItem.equals(itemId))) {
                data.displayedItem = itemId;
                needsSave = true;
            }
        } else {
            if (data.displayedItem != null) {
                data.displayedItem = null;
                needsSave = true;
            }
        }

        // Sync item yaw (rotation)
        float itemYaw = pillarBE.getFacingYaw();
        if (data.itemYaw == null || !data.itemYaw.equals(itemYaw)) {
            data.itemYaw = itemYaw;
            needsSave = true;
        }

        if (needsSave) {
            data.modifiedTime = System.currentTimeMillis();
        }

        return needsSave;
    }

    /**
     * Syncs pattern settings (pattern, speed, spread, intensity, max_particle_color, displayed item)
     * FROM block entity NBT TO manager.
     * Returns true if any data was changed and needs to be saved.
     */

    public Map<String, PillarData> copyDataSnapshot() {
        // Create a deep copy to ensure colors are preserved
        Map<String, PillarData> snapshot = new HashMap<>();
        for (Map.Entry<String, PillarData> entry : pillarData.entrySet()) {
            PillarData original = entry.getValue();
            if (original != null) {
                PillarData copy = new PillarData();
                copy.id = original.id;
                copy.dimension = original.dimension;
                copy.x = original.x;
                copy.y = original.y;
                copy.z = original.z;
                copy.createdTime = original.createdTime;
                copy.modifiedTime = original.modifiedTime;
                copy.pattern = original.pattern;
                copy.pattern_speed = original.pattern_speed;
                copy.pattern_spread = original.pattern_spread;
                copy.pattern_intensity = original.pattern_intensity;
                copy.max_particle_color = original.max_particle_color;
                copy.use_pattern = original.use_pattern;
                // Copy displayed item data
                copy.displayedItem = original.displayedItem;
                copy.pillarType = original.pillarType;
                copy.itemYaw = original.itemYaw;
                // Deep copy colors list
                if (original.dyeColors != null && !original.dyeColors.isEmpty()) {
                    copy.dyeColors = new ArrayList<>(original.dyeColors);
                } else {
                    copy.dyeColors = new ArrayList<>();
                }
                snapshot.put(entry.getKey(), copy);
            }
        }
        return snapshot;
    }
    
    public void replaceAllPillarData(Map<String, PillarData> newData) {
        if (newData == null) {
            return;
        }
        pillarData.clear();
        pillarData.putAll(newData);

        // Rebuild position index to keep in sync
        positionIndex.clear();
        for (PillarData data : newData.values()) {
            if (data != null && data.dimension != null) {
                try {
                    positionIndex.put(positionKey(data.dimension, data.getBlockPos()), data.id);
                } catch (Exception ignored) {
                }
            }
        }

        saveImmediate();
    }

    /**
     * Clears pillar data for server sync. Called on client when receiving data from server.
     * IMPORTANT: This prepares the client to receive fresh data from the server.
     * The isServerSynced flag will be set to true after all data is loaded.
     */
    public void clearForServerSync() {
        pillarData.clear();
        positionIndex.clear();
        // Don't set isServerSynced here - wait until data is fully loaded
        hasLoaded = false;
    }

    /**
     * Adds pillar data from server sync packet. Called on client.
     */
    public void addPillarDataFromSync(PillarData data) {
        if (data == null || data.id == null) {
            return;
        }
        pillarData.put(data.id, data);
        if (data.dimension != null) {
            try {
                positionIndex.put(positionKey(data.dimension, data.getBlockPos()), data.id);
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Registers a pillar block entity with the manager.
     * Called by PillarBlockEntity on load and place.
     */
    public void registerPillar(net.minecraft.world.level.block.entity.BlockEntity be) {
        if (be == null || be.getLevel() == null || be.getLevel().isClientSide) {
            return;
        }

        if (!(be instanceof com.kingodogo.buildscape.block.PillarBlockEntity pillar)) {
            return;
        }

        String id = pillar.getPillarId();
        if (id == null || id.isEmpty()) {
            return;
        }

        String dimension = getDimensionKey(pillar.getLevel());
        BlockPos pos = pillar.getBlockPos();
        String posKey = positionKey(dimension, pos);

        // Check if already registered
        PillarData existing = pillarData.get(id);
        if (existing == null) {
            // New pillar discovered from world/NBT
            PillarData data = new PillarData(id, dimension, pos);

            // Sync current state from BE (colors, pattern, item, etc.)
            syncPatternSettingsFromNBT(pillar, data);

            List<String> colors = pillar.getParticleColors();
            if (colors != null && !colors.isEmpty()) {
                for (String c : colors) data.addColor(c);
            }

            pillarData.put(id, data);
            positionIndex.put(posKey, id);

            // Save if on server
            if (com.kingodogo.buildscape.BuildScape.isServerFullyInitialized()) {
                saveImmediate();
            }
        } else {
            // Already exists - update position index just in case it moved/was reindexed
            positionIndex.put(posKey, id);

            // Sync all settings from BE to manager
            if (syncPatternSettingsFromNBT(pillar, existing)) {
                if (com.kingodogo.buildscape.BuildScape.isServerFullyInitialized()) {
                    saveImmediate();
                }
            }
        }
    }

    /**
     * Marks the manager as loaded. Called after syncing from server.
     * This sets both hasLoaded and isServerSynced flags to indicate
     * that the client has received complete data from the server.
     */
    public void markAsLoaded() {
        hasLoaded = true;
        isServerSynced = true;
    }

    /**
     * Gets a list of all pillar data for syncing to clients.
     */
    public java.util.List<PillarData> getAllPillarDataForSync() {
        return new ArrayList<>(pillarData.values());
    }

    public static class PillarData {

        public String id;
        public List<String> dyeColors = new ArrayList<>();
        public String dimension;
        public int x, y, z;
        public long createdTime;
        public long modifiedTime;

        // Per-pillar config options (optional, defaults to global config if not set)
        public Boolean use_pattern = null; // null means use global config
        public String pattern = null; // null means use global config
        public Double pattern_speed = null;
        public Double pattern_spread = null;
        public Double pattern_intensity = null;
        public Integer max_particle_color = null; // Max number of colors for this pillar (1-5)

        // Display item (serialized as string for JSON compatibility)
        public String displayedItem = null; // Format: "minecraft:item_id"
        public String pillarType = null; // Format: "minecraft:stone_pillar"
        public Float itemYaw = null; // Rotation of displayed item

        public PillarData() {
        }

        public PillarData(String id, String dimension, BlockPos pos) {
            this.id = id;
            this.dimension = dimension;
            this.x = pos.getX();
            this.y = pos.getY();
            this.z = pos.getZ();
            this.createdTime = System.currentTimeMillis();
            this.modifiedTime = this.createdTime;
        }

        public BlockPos getBlockPos() {
            return new BlockPos(x, y, z);
        }

        public boolean addColor(String colorCode) {
            if (dyeColors.size() >= 5) {
                return false;
            }
            dyeColors.add(colorCode.toUpperCase());
            modifiedTime = System.currentTimeMillis();
            return true;
        }

        public void clearColors() {
            dyeColors.clear();
            modifiedTime = System.currentTimeMillis();
        }

        public List<String> getColors() {
            return Collections.unmodifiableList(dyeColors);
        }

        public boolean hasColors() {
            return !dyeColors.isEmpty();
        }

        public int getColorCount() {
            return dyeColors.size();
        }
    }
}
