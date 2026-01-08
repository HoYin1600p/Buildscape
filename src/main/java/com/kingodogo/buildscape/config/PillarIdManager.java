package com.kingodogo.buildscape.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.server.ServerLifecycleHooks;

public class PillarIdManager {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    private static final String FILE_NAME = "pillar-ids.dat";
    private static final String BACKUP_FILE_NAME = "pillar-ids.bak.dat";
    private static final String FOLDER_NAME = "buildscape";
    private static PillarIdManager INSTANCE;

    private final Map<String, PillarData> pillarData = new ConcurrentHashMap<>();

    private long lastLoadedTime = 0L;
    private long lastFileSize = 0L;

    private boolean hasLoaded = false;
    private boolean hadColorsOnLoad = false; // Track if we had colors when we loaded
    
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

    public static PillarIdManager get() {
        if (INSTANCE == null) {
            INSTANCE = new PillarIdManager();
        }
        return INSTANCE;
    }

    public boolean hasLoaded() {
        return hasLoaded;
    }

    private File getDataDir() {
        try {
            if (cachedWorldSaveDir != null && cachedWorldSaveDir.exists()) {
                return cachedWorldSaveDir;
            }

            if (com.kingodogo.buildscape.BuildScape.isServerFullyInitialized()) {
                try {
                    MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                    if (
                            server != null &&
                                    server.isRunning() &&
                                    server.getPlayerList().getPlayerCount() > 0
                    ) {
                        Path worldPath = server.getWorldPath(LevelResource.ROOT);
                        if (worldPath != null) {
                            File buildscapeDir = worldPath.resolve(FOLDER_NAME).toFile();
                            if (!buildscapeDir.exists()) {
                                buildscapeDir.mkdirs();
                            }
                            cachedWorldSaveDir = buildscapeDir;
                            return buildscapeDir;
                        }
                    }
                } catch (Exception e) {
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

    /**
     * Reset world cache directory only - does NOT clear pillar data.
     * Called on world unload/player logout to reset cache, but preserves data.
     */
    public static void resetWorldCache() {
        System.out.println("BuildScape: resetWorldCache() called - preserving pillar data (not clearing)");
        cachedWorldSaveDir = null;
        worldLoadStartTime = System.currentTimeMillis();
        recoveryScheduled = false;
        recoveryScheduledTime = 0L;
        
        // IMPORTANT: Don't clear pillarData or reset hasLoaded here!
        // Data should persist through world switches
        // Only reset the cache directory, not the actual data
    }
    
    /**
     * Full reset - clears all data. Only called on server stop.
     */
    public static void fullReset() {
        System.out.println("BuildScape: fullReset() called - clearing all pillar data from memory");
        cachedWorldSaveDir = null;
        worldLoadStartTime = System.currentTimeMillis();
        recoveryScheduled = false;
        recoveryScheduledTime = 0L;
        
        if (INSTANCE != null) {
            int dataCount = INSTANCE.pillarData.size();
            INSTANCE.pillarData.clear();
            INSTANCE.lastLoadedTime = 0L;
            INSTANCE.lastFileSize = 0L;
            INSTANCE.hasLoaded = false;
            INSTANCE.hadColorsOnLoad = false;
            INSTANCE.fileWasDeleted = false;
            System.out.println("BuildScape: Cleared " + dataCount + " pillar entries from memory");
        }
    }
    
    /**
     * Schedule recovery to run after world load.
     * Recovery will run automatically after RECOVERY_DELAY_MS.
     */
    public static void scheduleRecoveryAfterLoad() {
        recoveryScheduled = true;
        recoveryScheduledTime = System.currentTimeMillis();
        System.out.println("BuildScape: Scheduled pillar recovery to run after " + (RECOVERY_DELAY_MS / 1000) + " seconds");
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
            System.out.println("BuildScape: Scheduled recovery skipped - server not running");
            return;
        }
        
        PillarIdManager manager = get();
        if (manager == null) {
            System.out.println("BuildScape: Scheduled recovery skipped - manager is null");
            return;
        }
        
        if (!manager.hasLoaded()) {
            System.out.println("BuildScape: Scheduled recovery skipped - manager not loaded yet");
            return;
        }
        
        System.out.println("BuildScape: Running scheduled pillar recovery (preserving colors)...");
        manager.recoverPillarsFromWorld(server, false); // false = don't clear colors
    }

    private static boolean isWorldReadyForRecovery() {
        if (worldLoadStartTime == 0L) {
            return true;
        }
        long elapsed = System.currentTimeMillis() - worldLoadStartTime;
        return elapsed >= MIN_WORLD_LOAD_TIME_MS;
    }

    public static String getVariantPrefix(Level level, BlockPos pos) {
        if (level == null || pos == null) return PREFIX_STONE;

        if (
                !level.isClientSide &&
                        !com.kingodogo.buildscape.BuildScape.isServerFullyInitialized()
        ) {
            return PREFIX_STONE;
        }

        if (!level.hasChunkAt(pos)) {
            return PREFIX_STONE;
        }

        try {
            if (!level.isClientSide) {
                net.minecraft.world.level.chunk.ChunkAccess chunk = level.getChunk(pos);
                if (!(chunk instanceof net.minecraft.world.level.chunk.LevelChunk)) {
                    return PREFIX_STONE;
                }
                if (
                        !chunk
                                .getStatus()
                                .isOrAfter(net.minecraft.world.level.chunk.ChunkStatus.FULL)
                ) {
                    return PREFIX_STONE;
                }
            }
        } catch (Exception e) {
            return PREFIX_STONE;
        }

        try {
            BlockState state = level.getBlockState(pos);
            Block block = state.getBlock();
            String blockName =
                    net.minecraftforge.registries.ForgeRegistries.BLOCKS.getKey(
                            block
                    ).getPath();

            if (blockName.contains("mossy")) {
                return PREFIX_MOSSY;
            } else if (blockName.contains("deepslate")) {
                return PREFIX_DEEPSLATE;
            } else if (blockName.contains("quartz")) {
                return PREFIX_QUARTZ;
            } else {
                return PREFIX_STONE;
            }
        } catch (Exception e) {
            return PREFIX_STONE;
        }
    }

    private static final String ALPHANUMERIC =
            "0123456789abcdefghijklmnopqrstuvwxyz";

    public String generatePillarId(String variantPrefix) {
        if (variantPrefix == null || variantPrefix.isEmpty()) {
            variantPrefix = PREFIX_STONE;
        }

        String prefix = variantPrefix + "-P";
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
            String id = prefix + "" + c1 + c2 + c3 + c4;
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
                                        "" +
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

    public String generatePillarId() {
        return generatePillarId(PREFIX_STONE);
    }

    private String positionKey(String dimension, BlockPos pos) {
        return dimension + ":" + pos.getX() + ":" + pos.getY() + ":" + pos.getZ();
    }

    public static String getDimensionKey(Level level) {
        if (level == null) return "unknown";
        return level.dimension().location().toString();
    }

    public PillarData getOrCreatePillarData(Level level, BlockPos pos) {
        String dimension = getDimensionKey(level);
        String expectedPrefix = getVariantPrefix(level, pos);

        String existingIdToRemove = null;
        for (PillarData data : pillarData.values()) {
            if (
                    data.dimension.equals(dimension) &&
                            data.x == pos.getX() &&
                            data.y == pos.getY() &&
                            data.z == pos.getZ()
            ) {
                if (data.id != null && data.id.startsWith(expectedPrefix + "-P")) {
                    return data;
                } else {
                    existingIdToRemove = data.id;
                    break;
                }
            }
        }

        if (existingIdToRemove != null) {
            pillarData.remove(existingIdToRemove);
        }

        String id = generatePillarId(expectedPrefix);
        PillarData newData = new PillarData(id, dimension, pos);
        pillarData.put(id, newData);
        
        // IMPORTANT: Don't save during recovery - recovery will save once at the end
        // This prevents saving empty colors repeatedly during recovery
        if (!recoveryInProgress) {
            saveImmediate();
        }
        return newData;
    }

    public PillarData getPillarData(String pillarId) {
        if (pillarId == null) return null;
        return pillarData.get(pillarId);
    }

    public PillarData getPillarDataByPosition(Level level, BlockPos pos) {
        String dimension = getDimensionKey(level);
        for (PillarData data : pillarData.values()) {
            if (
                    data.dimension.equals(dimension) &&
                            data.x == pos.getX() &&
                            data.y == pos.getY() &&
                            data.z == pos.getZ()
            ) {
                return data;
            }
        }
        return null;
    }

    public String getPillarIdByPosition(Level level, BlockPos pos) {
        PillarData data = getPillarDataByPosition(level, pos);
        return data != null ? data.id : null;
    }

    public String addDyeColor(Level level, BlockPos pos, String colorCode) {
        PillarData data = getOrCreatePillarData(level, pos);
        if (data.addColor(colorCode)) {
            saveImmediate();
            return data.id;
        }
        return null;
    }

    public void removePillar(String pillarId) {
        if (pillarId != null) {
            PillarData data = pillarData.remove(pillarId);
            if (data != null) {
                // Reset the pillar block entity to default state (freshly placed)
                // This removes custom colors/patterns from NBT and resets the pillar
                PillarResetHandler.resetPillarFromData(data);
                saveImmediate();
            }
        }
    }

    public void removePillarByPosition(Level level, BlockPos pos) {
        String dimension = getDimensionKey(level);
        String idToRemove = null;
        PillarData dataToReset = null;

        for (Map.Entry<String, PillarData> entry : pillarData.entrySet()) {
            PillarData data = entry.getValue();
            if (
                    data.dimension.equals(dimension) &&
                            data.x == pos.getX() &&
                            data.y == pos.getY() &&
                            data.z == pos.getZ()
            ) {
                idToRemove = entry.getKey();
                dataToReset = data;
                break;
            }
        }

        if (idToRemove != null) {
            pillarData.remove(idToRemove);
            // Reset the pillar block entity to default state (freshly placed)
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
                            System.out.println(
                                    "BuildScape: Corrupted file backed up to: " +
                                            backupFile.getName()
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
                        System.out.println("BuildScape: Created fresh pillar data file");
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
                        System.out.println(
                                "BuildScape: Corrupted file backed up to: " + backupFile.getName()
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
                    System.out.println("BuildScape: Created fresh pillar data file");
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

    public void load() {
        try {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server == null || !server.isRunning()) {
                pillarData.clear();
                lastLoadedTime = 0L;
                lastFileSize = 0L;
                fileWasDeleted = false;
                hasLoaded = false;
                return;
            }

            File file = getDataFile();
            if (hasLoaded && pillarData.isEmpty() && file.exists()) {
                System.out.println(
                        "BuildScape: Detected stale load state, allowing reload"
                );
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
                        System.out.println("BuildScape: Loaded from main file (pillar-ids.dat)");
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
                                System.out.println("BuildScape: Loaded from backup file (pillar-ids.bak.dat) - has colors");
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
                System.out.println(
                        "BuildScape: No valid pillar data file found - will recover after world is fully loaded"
                );
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

                            if (!id.matches("^[MSDQ]-P[a-z0-9]{4}$")) {
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
                            if (needsMigration) {
                                migrated++;
                            }
                        } catch (Exception e) {
                            skipped++;
                            continue;
                        }
                    }

                    if (migrated > 0) {
                        System.out.println(
                                "BuildScape: Migrated " +
                                        migrated +
                                        " pillar entries for old world compatibility"
                        );
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
                        System.out.println(
                                "BuildScape: Skipped " +
                                        skipped +
                                        " invalid or outdated pillar entries"
                        );
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
                    System.out.println(
                            "BuildScape: Pillar data file is empty (just {}) - will recover after world is fully loaded"
                    );
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

    private void scheduleRecoveryFromWorld(
            MinecraftServer server,
            boolean clearColors
    ) {
        System.out.println(
                "BuildScape: Pillar recovery scheduling disabled during world loading - recovery will happen later if needed"
        );
    }

    public int clearAllPillarIdsFromWorld(MinecraftServer server) {
        if (server == null || !server.isRunning()) {
            return 0;
        }

        if (server.getPlayerList().getPlayerCount() == 0) {
            System.out.println(
                    "BuildScape: Skipping pillar ID clearing - no players connected yet"
            );
            return 0;
        }

        int clearedCount = 0;

        try {
            System.out.println("BuildScape: Clearing all pillar IDs from world...");

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
                                        !(chunkAccess instanceof
                                                net.minecraft.world.level.chunk.LevelChunk)
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

                                net.minecraft.world.level.chunk.LevelChunk chunk =
                                        (net.minecraft.world.level.chunk.LevelChunk) chunkAccess;

                                for (net.minecraft.world.level.block.entity.BlockEntity be : chunk
                                        .getBlockEntities()
                                        .values()) {
                                    if (
                                            be instanceof
                                                    com.kingodogo.buildscape.block.PillarBlockEntity
                                    ) {
                                        com.kingodogo.buildscape.block.PillarBlockEntity pillarBE =
                                                (com.kingodogo.buildscape.block.PillarBlockEntity) be;

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

            System.out.println(
                    "BuildScape: Cleared " + clearedCount + " pillar IDs from world"
            );
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
            System.out.println(
                    "BuildScape: Skipping pillar recovery - no players connected yet"
            );
            return;
        }

        try {
            if (clearColors) {
                System.out.println(
                        "BuildScape: File was deleted - clearing all pillar IDs from world..."
                );
                int clearedCount = clearAllPillarIdsFromWorld(server);
                System.out.println(
                        "BuildScape: Cleared " +
                                clearedCount +
                                " pillar IDs. All pillars are now treated as newly placed."
                );

                saveImmediate();
                return;
            }

            System.out.println("BuildScape: Starting pillar recovery from world...");
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
                                        !(chunkAccess instanceof
                                                net.minecraft.world.level.chunk.LevelChunk)
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

                                net.minecraft.world.level.chunk.LevelChunk chunk =
                                        (net.minecraft.world.level.chunk.LevelChunk) chunkAccess;

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
                                be instanceof com.kingodogo.buildscape.block.PillarBlockEntity
                        ) {
                            try {
                                com.kingodogo.buildscape.block.PillarBlockEntity pillarBE =
                                        (com.kingodogo.buildscape.block.PillarBlockEntity) be;

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
                                        System.out.println(
                                                "BuildScape: Updating position for existing pillar " + pillarId
                                        );
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
                                                    if (nbtColor == null || managerColor == null || !nbtColor.equals(managerColor)) {
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
            System.out.println("BuildScape: Syncing colors from NBT before saving recovery data...");
            syncColorsFromNBTToManager(server);
            
            // Allow final save after syncing colors
            recoveryInProgress = false;
            
            if (recoveredCount > 0 || colorClearedCount > 0) {
                saveImmediate();
                System.out.println(
                        "BuildScape: Pillar recovery completed - " +
                                recoveredCount +
                                " pillars recovered, " +
                                colorClearedCount +
                                " colors cleared, " +
                                skippedCount +
                                " skipped"
                );
            } else {
                System.out.println(
                        "BuildScape: Pillar recovery completed - no pillars found in loaded chunks"
                );
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

    public void saveImmediate() {
        try {
            // IMPORTANT: Don't save during recovery - recovery will save once at the end after syncing colors
            if (recoveryInProgress) {
                return;
            }
            
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server == null || !server.isRunning()) {
                return;
            }

            if (!com.kingodogo.buildscape.BuildScape.isServerFullyInitialized()) {
                return;
            }

            if (server.getPlayerList().getPlayerCount() == 0) {
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
            
            // SAFEGUARD: NEVER save empty colors if the file has colors
            // This prevents recovery or other operations from clearing colors
            if (colorsCount == 0 && saveCount > 0) {
                // Check the file directly to see if it has colors
                boolean fileHasColors = false;
                try {
                    File mainFile = getDataFile();
                    if (mainFile.exists() && mainFile.length() > 0) {
                        try (FileInputStream fis = new FileInputStream(mainFile);
                             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
                             BufferedReader reader = new BufferedReader(isr)) {
                            Type type = new TypeToken<Map<String, PillarData>>() {}.getType();
                            Map<String, PillarData> fileData = GSON.fromJson(reader, type);
                            if (fileData != null) {
                                for (PillarData data : fileData.values()) {
                                    if (data != null && data.hasColors()) {
                                        fileHasColors = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    // Ignore - allow save to proceed if we can't check file
                }
                
                // NEVER save empty colors if file has colors - this prevents data loss
                if (fileHasColors) {
                    // Reload from file to restore colors
                    load();
                    return; // Don't save empty colors - this prevents overwriting file with empty colors
                }
            }
            
            System.out.println("BuildScape: Saving " + saveCount + " pillar entries to file, " + colorsCount + " with colors");

            // Save to main file only (backup file is saved separately on world save/server close)
            saveToFile(getDataFile(), FILE_NAME);
            
            // Update timestamps from main file
            File mainFile = getDataFile();
            if (mainFile.exists()) {
                lastLoadedTime = mainFile.lastModified();
                lastFileSize = mainFile.length();
            }
        } catch (Throwable t) {
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

    public void checkAndReload() {
        // Check main file only (backup file is separate, only saved on world save/server close)
        File mainFile = getDataFile();
        
        if (mainFile.exists()) {
            long currentModified = mainFile.lastModified();
            long currentSize = mainFile.length();
            if (currentModified != lastLoadedTime || currentSize != lastFileSize) {
                load();
            }
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
            
            System.out.println("BuildScape: Saving backup file with " + saveCount + " pillar entries, " + colorsCount + " with colors");
            
            // Save to backup file only
            saveToFile(getBackupDataFile(), BACKUP_FILE_NAME);
        } catch (Throwable t) {
            System.err.println("BuildScape: Error saving backup file: " + t.getMessage());
        }
    }

    public void cleanupOrphans(Level level) {
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
                    if (!data.hasColors()) continue;

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
                                be instanceof com.kingodogo.buildscape.block.PillarBlockEntity
                        ) {
                            com.kingodogo.buildscape.block.PillarBlockEntity pillarBE =
                                    (com.kingodogo.buildscape.block.PillarBlockEntity) be;

                            pillarBE.forceSetColors(data.getColors(), data.id);
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

            if (syncedCount > 0 || skippedCount > 0) {
                System.out.println(
                        "BuildScape: Synced " +
                                syncedCount +
                                " pillars, skipped " +
                                skippedCount +
                                " (chunks not loaded)"
                );
            }
        } catch (Exception e) {
            System.err.println(
                    "BuildScape: Error in syncAllLoadedPillars: " + e.getMessage()
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
                            System.out.println("BuildScape: Loaded " + nbtColors.size() + " colors from NBT for " + data.id);
                        } else {
                            // NBT is empty - preserve colors from file (if any)
                            int fileColorCount = (data.dyeColors != null) ? data.dyeColors.size() : 0;
                            if (fileColorCount > 0) {
                                System.out.println("BuildScape: Preserving " + fileColorCount + " colors from file for " + data.id + " (NBT empty)");
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
                System.out.println(
                        "BuildScape: Loaded colors from NBT for " + loadedCount + " pillars, saved to file"
                );
            } else {
                System.out.println(
                        "BuildScape: No colors loaded from NBT, preserving file colors (not saving)"
                );
            }
        } catch (Exception e) {
            System.err.println(
                    "BuildScape: Error in loadColorsFromNBT: " + e.getMessage()
            );
            e.printStackTrace();
        }
    }

    /**
     * Syncs ALL settings (colors, pattern, speed, spread, intensity, max_particle_color) 
     * FROM block entity NBT TO manager for all loaded pillars.
     * This ensures the manager has all settings that exist in NBT after world load,
     * so the GUI can display them correctly.
     */
    public void syncColorsFromNBTToManager(MinecraftServer server) {
        if (server == null || !server.isRunning()) {
            return;
        }

        if (server.getPlayerList().getPlayerCount() == 0) {
            return;
        }

        // IMPORTANT: Don't sync if manager hasn't loaded yet - this prevents clearing colors before load
        if (!hasLoaded()) {
            System.out.println("BuildScape: Skipping syncColorsFromNBTToManager - manager not loaded yet");
            return;
        }

        try {
            int syncedCount = 0;
            int preservedCount = 0;

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
                                    if (nbtColor == null || managerColor == null || !nbtColor.equals(managerColor)) {
                                        needsSync = true;
                                        break;
                                    }
                                }
                            }
                            
                            if (needsSync) {
                                // Sync colors FROM NBT TO manager
                                System.out.println("BuildScape: Syncing colors for " + data.id + 
                                    " - NBT has " + nbtColors.size() + " colors, manager had " + existingColorCount);
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
                            
                            // Also sync pattern settings from NBT
                            syncPatternSettingsFromNBT(bottomPillarBE, data);
                        } else {
                            // NBT is empty or null - preserve manager colors (do nothing)
                            if (existingColorCount > 0) {
                                preservedCount++;
                                System.out.println("BuildScape: Preserving " + existingColorCount + " colors for " + data.id + " (NBT empty)");
                            }
                            // Still try to sync pattern settings even if colors are empty
                            syncPatternSettingsFromNBT(bottomPillarBE, data);
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

            if (syncedCount > 0) {
                saveImmediate();
                System.out.println(
                        "BuildScape: Synced " + syncedCount + " pillar colors from NBT to manager, preserved " + preservedCount
                );
            } else if (preservedCount > 0) {
                System.out.println(
                        "BuildScape: Preserved " + preservedCount + " pillar colors (NBT empty or not loaded)"
                );
            }
        } catch (Exception e) {
            System.err.println(
                    "BuildScape: Error in syncColorsFromNBTToManager: " + e.getMessage()
            );
            e.printStackTrace();
        }
    }

    /**
     * Syncs pattern settings (pattern, speed, spread, intensity, max_particle_color) 
     * FROM block entity NBT TO manager.
     */
    private void syncPatternSettingsFromNBT(
            com.kingodogo.buildscape.block.PillarBlockEntity pillarBE,
            PillarData data
    ) {
        if (pillarBE == null || data == null) {
            return;
        }
        
        boolean needsSave = false;
        
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
        
        if (needsSave) {
            data.modifiedTime = System.currentTimeMillis();
        }
    }
    
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
        saveImmediate();
    }
}
