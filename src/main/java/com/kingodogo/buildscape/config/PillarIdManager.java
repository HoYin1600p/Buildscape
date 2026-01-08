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
    private static final String FOLDER_NAME = "buildscape";
    private static PillarIdManager INSTANCE;

    private final Map<String, PillarData> pillarData = new ConcurrentHashMap<>();

    private long lastLoadedTime = 0L;
    private long lastFileSize = 0L;

    private boolean hasLoaded = false;

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

    public static void resetWorldCache() {
        cachedWorldSaveDir = null;

        worldLoadStartTime = System.currentTimeMillis();

        if (INSTANCE != null) {
            INSTANCE.pillarData.clear();
            INSTANCE.lastLoadedTime = 0L;
            INSTANCE.lastFileSize = 0L;
            INSTANCE.hasLoaded = false;
            INSTANCE.fileWasDeleted = false;
        }
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
        saveImmediate();
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
                // Reset the pillar block entity to default state
                resetPillarBlock(data);
                saveImmediate();
            }
        }
    }
    
    /**
     * Reset a pillar block entity to its default state (no custom colors).
     * This is called when a pillar is removed from the manager.
     */
    private void resetPillarBlock(PillarData data) {
        if (data == null) {
            return;
        }
        
        try {
            // Get the server and level for the pillar's dimension
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server == null) {
                return;
            }
            
            // Find the level for this pillar's dimension
            for (net.minecraft.server.level.ServerLevel level : server.getAllLevels()) {
                if (level.dimension().location().toString().equals(data.dimension)) {
                    BlockPos pos = data.getBlockPos();
                    
                    // Check if the block entity exists at this position
                    if (level.getBlockEntity(pos) instanceof com.kingodogo.buildscape.block.PillarBlockEntity pillarBE) {
                        // Reset the pillar's appearance to default (colors, pattern, etc.)
                        pillarBE.resetToDefaultAppearance();
                        pillarBE.setChanged();
                        
                        // Notify clients of the change
                        BlockState state = level.getBlockState(pos);
                        level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
                    }
                    break;
                }
            }
        } catch (Exception e) {
            // Silently fail if we can't reset the block (e.g., world not loaded)
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
            // Reset the pillar block entity to default state
            if (dataToReset != null) {
                resetPillarBlock(dataToReset);
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
            File file = getDataFile();

            if (!file.exists()) {
                fileWasDeleted = true;
                pillarData.clear();
                lastLoadedTime = 0L;
                lastFileSize = 0L;
                hasLoaded = true;
                System.out.println(
                        "BuildScape: Pillar data file not found - will recover after world is fully loaded"
                );
                return;
            }

            try {
                long fileSize = file.length();
                if (fileSize <= 0) {
                    fileWasDeleted = true;
                    pillarData.clear();
                    lastLoadedTime = 0L;
                    lastFileSize = 0L;
                    hasLoaded = true;
                    System.out.println(
                            "BuildScape: Pillar data file is empty - will recover after world is fully loaded"
                    );
                    return;
                }
                if (fileSize > 10 * 1024 * 1024) {
                    System.err.println(
                            "BuildScape: Pillar data file is suspiciously large (" +
                                    fileSize +
                                    " bytes). Skipping load to prevent corruption."
                    );
                    handleCorruptedFile(
                            file,
                            new RuntimeException("File too large"),
                            "file size validation"
                    );
                    return;
                }
            } catch (Exception e) {
                System.err.println(
                        "BuildScape: Cannot access pillar data file, starting fresh: " +
                                e.getMessage()
                );
                pillarData.clear();
                lastLoadedTime = 0L;
                lastFileSize = 0L;
                return;
            }

            try (
                    FileInputStream fis = new FileInputStream(file);
                    InputStreamReader isr = new InputStreamReader(
                            fis,
                            StandardCharsets.UTF_8
                    );
                    BufferedReader reader = new BufferedReader(isr)
            ) {
                Type type = new TypeToken<Map<String, PillarData>>() {
                }.getType();
                Map<String, PillarData> loaded = GSON.fromJson(reader, type);

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

                            if (data.dyeColors == null) {
                                data.dyeColors = new ArrayList<>();
                                needsMigration = true;
                            }

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
                }

                if (pillarData.isEmpty()) {
                    fileWasDeleted = true;
                    System.out.println(
                            "BuildScape: Pillar data file is empty (just {}) - will recover after world is fully loaded"
                    );
                } else {
                    fileWasDeleted = false;
                }

                lastLoadedTime = file.lastModified();
                lastFileSize = file.length();

                hasLoaded = true;

                updateCachedWorldDir();

            } catch (com.google.gson.JsonSyntaxException e) {
                fileWasDeleted = true;
                handleCorruptedFile(file, e, "JSON syntax error");
                hasLoaded = true;
                System.out.println(
                        "BuildScape: Pillar data file corrupted - will recover after world is fully loaded"
                );
            } catch (com.google.gson.stream.MalformedJsonException e) {
                fileWasDeleted = true;
                handleCorruptedFile(file, e, "malformed JSON");
                hasLoaded = true;
                System.out.println(
                        "BuildScape: Pillar data file corrupted - will recover after world is fully loaded"
                );
            } catch (java.io.IOException e) {
                if (
                        e instanceof com.google.gson.stream.MalformedJsonException ||
                                (e.getMessage() != null &&
                                        (e.getMessage().contains("JSON") ||
                                                e.getMessage().contains("malformed")))
                ) {
                    fileWasDeleted = true;
                    handleCorruptedFile(file, e, "JSON parsing error (IO)");
                    System.out.println(
                            "BuildScape: Pillar data file corrupted - will recover after world is fully loaded"
                    );
                } else {
                    fileWasDeleted = true;
                    System.err.println(
                            "BuildScape: IO error loading pillar data - will recover after world is fully loaded: " +
                                    e.getMessage()
                    );
                    pillarData.clear();
                    lastLoadedTime = 0L;
                    lastFileSize = 0L;
                    hasLoaded = true;
                }
            } catch (Exception e) {
                Throwable cause = e.getCause();
                if (
                        cause instanceof com.google.gson.JsonSyntaxException ||
                                cause instanceof com.google.gson.stream.MalformedJsonException ||
                                (e.getMessage() != null &&
                                        (e.getMessage().contains("JSON") ||
                                                e.getMessage().contains("malformed")))
                ) {
                    fileWasDeleted = true;
                    handleCorruptedFile(file, e, "JSON parsing error (wrapped)");
                    System.out.println(
                            "BuildScape: Pillar data file corrupted - will recover after world is fully loaded"
                    );
                } else {
                    fileWasDeleted = true;
                    System.err.println(
                            "BuildScape: Failed to load pillar data - will recover after world is fully loaded: " +
                                    e.getMessage()
                    );
                    pillarData.clear();
                    lastLoadedTime = 0L;
                    lastFileSize = 0L;
                    hasLoaded = true;
                }
            }
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

                                PillarData existingData = pillarData.get(pillarId);

                                if (existingData != null) {
                                    if (
                                            existingData.dimension.equals(dimensionKey) &&
                                                    existingData.x == pos.getX() &&
                                                    existingData.y == pos.getY() &&
                                                    existingData.z == pos.getZ()
                                    ) {
                                        if (clearColors && existingData.hasColors()) {
                                            existingData.clearColors();
                                            colorClearedCount++;
                                        }
                                        continue;
                                    } else {
                                        System.out.println(
                                                "BuildScape: Warning - Pillar ID " +
                                                        pillarId +
                                                        " exists at different position. Updating position."
                                        );
                                        existingData.dimension = dimensionKey;
                                        existingData.x = pos.getX();
                                        existingData.y = pos.getY();
                                        existingData.z = pos.getZ();
                                        if (clearColors && existingData.hasColors()) {
                                            existingData.clearColors();
                                            colorClearedCount++;
                                        }
                                        recoveredCount++;
                                        continue;
                                    }
                                }

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

            syncAllLoadedPillars(server);
        } catch (Exception e) {
            System.err.println(
                    "BuildScape: Error during pillar recovery: " + e.getMessage()
            );
            e.printStackTrace();
        }
    }

    public void save() {
        saveImmediate();
    }

    public void saveImmediate() {
        try {
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

            File file = getDataFile();

            try {
                File parentDir = file.getParentFile();
                if (parentDir != null && !parentDir.exists()) {
                    parentDir.mkdirs();
                }

                File tempFile = new File(file.getParentFile(), FILE_NAME + ".tmp");

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

                    if (renamed) {
                        lastLoadedTime = file.lastModified();
                        lastFileSize = file.length();
                    } else {
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
            }
        } catch (Throwable t) {
        }
    }

    public void forceReload() {
        load();
    }

    public void checkAndReload() {
        File file = getDataFile();
        if (file.exists()) {
            long currentModified = file.lastModified();
            long currentSize = file.length();
            if (currentModified != lastLoadedTime || currentSize != lastFileSize) {
                load();
            }
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

    public Map<String, PillarData> copyDataSnapshot() {
        return new HashMap<>(pillarData);
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
