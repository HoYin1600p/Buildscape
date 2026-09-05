package com.kingodogo.buildscape.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

    private final Map<String, String> positionIndex = new ConcurrentHashMap<>();

    private long lastLoadedTime = 0L;
    private long lastFileSize = 0L;

    private volatile boolean hasLoaded = false;
    private volatile boolean loadInProgress = false;
    private boolean hadColorsOnLoad = false;
    private boolean isServerSynced = false;
    private boolean allowEmptySave = false;

    private static boolean recoveryScheduled = false;
    private static long recoveryScheduledTime = 0L;
    private static final long RECOVERY_DELAY_MS = 5000;
    private static boolean recoveryInProgress = false;

    private static long worldLoadStartTime = 0L;
    private static final long MIN_WORLD_LOAD_TIME_MS = 15000;

    private static File cachedWorldSaveDir = null;

    public static final String PREFIX_MOSSY = "M";
    public static final String PREFIX_STONE = "S";
    public static final String PREFIX_DEEPSLATE = "D";
    public static final String PREFIX_QUARTZ = "Q";
    public static final String PREFIX_ITEM_FRAME = "I-F";

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
            INSTANCE.loadInProgress = false;
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

    public boolean isLoadInProgress() {
        return loadInProgress;
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
            } else if (blockName.contains("amethyst")) {
                return "AM-P";
            } else if (blockName.contains("andesite")) {
                return "AN-P";
            } else if (blockName.contains("basalt")) {
                return "BS-P";
            } else if (blockName.contains("blackstone")) {
                return "BL-P";
            } else if (blockName.contains("blue_ice")) {
                return "BI-P";
            } else if (blockName.contains("calcite")) {
                return "CA-P";
            } else if (blockName.contains("cinnabar")) {
                return "CI-P";
            } else if (blockName.contains("exposed_copper")) {
                return "EC-P";
            } else if (blockName.contains("oxidized_copper")) {
                return "OC-P";
            } else if (blockName.contains("weathered_copper")) {
                return "WC-P";
            } else if (blockName.contains("copper")) {
                return "CO-P";
            } else if (blockName.contains("dark_prismarine")) {
                return "DP-P";
            } else if (blockName.contains("prismarine")) {
                return "PR-P";
            } else if (blockName.contains("diorite")) {
                return "DI-P";
            } else if (blockName.contains("dripstone")) {
                return "DR-P";
            } else if (blockName.contains("granite")) {
                return "GR-P";
            } else if (blockName.contains("netherrack")) {
                return "NE-P";
            } else if (blockName.contains("obsidian")) {
                return "OB-P";
            } else if (blockName.contains("packed_mud")) {
                return "PM-P";
            } else if (blockName.contains("sculk")) {
                return "SC-P";
            } else if (blockName.contains("sulfur")) {
                return "SU-P";
            } else if (blockName.contains("tuff")) {
                return "TU-P";
            } else if (blockName.contains("stone")) {
                return PREFIX_STONE + "-P";
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

    public static void fullReset() {
        cachedWorldSaveDir = null;
        worldLoadStartTime = System.currentTimeMillis();
        recoveryScheduled = false;
        recoveryScheduledTime = 0L;

        if (INSTANCE != null) {
            INSTANCE.pillarData.clear();
            INSTANCE.positionIndex.clear();
            INSTANCE.lastLoadedTime = 0L;
            INSTANCE.lastFileSize = 0L;
            INSTANCE.hasLoaded = false;
            INSTANCE.loadInProgress = false;
            INSTANCE.hadColorsOnLoad = false;
            INSTANCE.fileWasDeleted = false;
        }
    }

    public static void scheduleRecoveryAfterLoad() {
        recoveryScheduled = true;
        recoveryScheduledTime = System.currentTimeMillis();
    }

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

        manager.recoverPillarsFromWorld(server, false);
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

        String existingId = positionIndex.get(posKey);
        if (existingId != null) {
            PillarData existing = pillarData.get(existingId);
            if (existing != null) {
                if (existing.id != null && existing.id.startsWith(expectedPrefix)) {
                    return existing;
                } else {
                    pillarData.remove(existingId);
                    positionIndex.remove(posKey);
                }
            } else {
                positionIndex.remove(posKey);
            }
        }

        String id = generatePillarId(expectedPrefix);
        PillarData newData = new PillarData(id, dimension, basePos);

        net.minecraft.world.level.block.entity.BlockEntity be = level.getBlockEntity(basePos);
        if (be instanceof com.kingodogo.buildscape.block.PillarBlockEntity pillar) {
            pillar.setPillarId(id);
            pillar.setChanged();
            syncPatternSettingsFromNBT(pillar, newData);

            List<String> worldColors = pillar.getParticleColors();
            if (worldColors != null && !worldColors.isEmpty()) {
                for (String color : worldColors) {
                    newData.addColor(color);
                }
            }
        }

        pillarData.put(id, newData);
        positionIndex.put(posKey, id);

        if (!recoveryInProgress) {
            saveImmediate();
        }
        return newData;
    }

    private String positionKey(String dimension, BlockPos pos) {
        return positionKey(dimension, pos, null);
    }

    public String positionKey(String dimension, BlockPos pos, net.minecraft.core.Direction facing) {
        String key = dimension + ":" + pos.getX() + ":" + pos.getY() + ":" + pos.getZ();
        if (facing != null) {
            key += ":" + facing.getSerializedName();
        }
        return key;
    }

    public static String getDimensionKey(Level level) {
        if (level == null) return "unknown";
        return level.dimension().location().toString();
    }

    public String getIdForPosition(String posKey) {
        if (posKey == null) return null;
        return positionIndex.get(posKey);
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

        net.minecraft.world.level.block.entity.BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof com.kingodogo.buildscape.block.PillarBlockEntity pillar) {
            syncPatternSettingsFromNBT(pillar, data);
        }

        if (data.addColor(colorCode)) {
            if (data.pattern == null || data.pattern.equals("default")) {
                data.pattern = com.kingodogo.buildscape.config.PillarParticleConfig.get().pattern;
                if (data.pattern == null || data.pattern.isEmpty()) {
                    data.pattern = "ring";
                }
            }
            saveImmediate();
            return data.id;
        }
        return null;
    }

    public PillarData getPillarDataByPosition(Level level, BlockPos pos) {
        String dimension = getDimensionKey(level);
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
                positionIndex.remove(positionKey(data.dimension, new BlockPos(data.x, data.y, data.z)));
                PillarResetHandler.resetPillarFromData(data);
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
                loadInProgress = false;
                return;
            }

            File file = getDataFile();
            if (hasLoaded && pillarData.isEmpty() && file.exists()) {
                hasLoaded = false;
            }

            if (hasLoaded || loadInProgress) {
                return;
            }

            loadInProgress = true;
            server.execute(() -> {
                try {
                    loadFileAsync(server);
                } catch (Exception e) {
                    System.err.println(
                            "BuildScape: Error in async file load: " + e.getMessage()
                    );
                    e.printStackTrace();
                    hasLoaded = true;
                } finally {
                    loadInProgress = false;
                }
            });
        } catch (Throwable t) {
            System.err.println(
                    "BuildScape: Critical error in load() - will recover from world later: " +
                            t.getMessage()
            );
            t.printStackTrace();
            loadInProgress = false;
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

    private void processLoadedData(Map<String, PillarData> loaded, MinecraftServer server, File sourceFile) {
        try {

                Map<String, PillarData> existingData = new HashMap<>(pillarData);

                pillarData.clear();
            positionIndex.clear();

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

                            List<String> originalFileColors = null;
                            if (data.dyeColors != null) {
                                originalFileColors = new ArrayList<>(data.dyeColors);
                            }

                            PillarData existing = existingData.get(id);

                            if (originalFileColors != null && !originalFileColors.isEmpty()) {
                                data.dyeColors = originalFileColors;
                            } else {
                                if (data.dyeColors == null) {
                                    data.dyeColors = new ArrayList<>();
                                    needsMigration = true;
                                }

                                if (existing != null && existing.hasColors() && existing.dyeColors != null) {
                                    data.dyeColors = new ArrayList<>(existing.dyeColors);
                                }
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

                            if (!id.matches("^[A-Z-]+[a-z0-9]+$")) {
                                skipped++;
                                continue;
                            }

                            if (data.dyeColors == null || data.dyeColors.isEmpty()) {
                                if (originalFileColors != null && !originalFileColors.isEmpty()) {
                                    data.dyeColors = new ArrayList<>(originalFileColors);
                                } else if (data.dyeColors == null) {
                                    data.dyeColors = new ArrayList<>();
                                }
                            }

                            pillarData.put(id, data);
                            net.minecraft.core.Direction facing = null;
                            if (data.facing != null) {
                                facing = net.minecraft.core.Direction.byName(data.facing);
                            }
                            positionIndex.put(positionKey(data.dimension, data.getBlockPos(), facing), id);
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

                }

            for (Map.Entry<String, PillarData> entry : existingData.entrySet()) {
                if (!pillarData.containsKey(entry.getKey())) {
                    PillarData earlyData = entry.getValue();
                    pillarData.put(entry.getKey(), earlyData);

                    net.minecraft.core.Direction facing = null;
                    if (earlyData.facing != null) {
                        facing = net.minecraft.core.Direction.byName(earlyData.facing);
                    }
                    positionIndex.put(positionKey(earlyData.dimension, earlyData.getBlockPos(), facing), entry.getKey());
                }
            }

            int colorsCountAfterMerge = 0;
            for (PillarData data : pillarData.values()) {
                if (data != null && data.hasColors()) {
                    colorsCountAfterMerge++;
                }
            }
            hadColorsOnLoad = (colorsCountAfterMerge > 0);

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
            File file = getDataFile();

            Map<String, PillarData> loadedData = null;
            File sourceFile = null;

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

            boolean mainFileHasColors = false;
            if (loadedData != null) {
                for (PillarData data : loadedData.values()) {
                    if (data != null && data.hasColors()) {
                        mainFileHasColors = true;
                        break;
                    }
                }
            }

            if (!mainFileHasColors) {
                File backupFile = getBackupDataFile();
                if (backupFile.exists() && backupFile.length() > 0) {
                    try {
                        Map<String, PillarData> backupData = loadFromFile(backupFile);
                        if (backupData != null && !backupData.isEmpty()) {
                            boolean backupHasColors = false;
                            for (PillarData data : backupData.values()) {
                                if (data != null && data.hasColors()) {
                                    backupHasColors = true;
                                    break;
                                }
                            }

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

            if (loadedData == null || loadedData.isEmpty()) {
                fileWasDeleted = true;
                pillarData.clear();
                lastLoadedTime = 0L;
                lastFileSize = 0L;
                hasLoaded = true;
                return;
            }

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

            int saveCount = pillarData.size();
            int colorsCount = 0;
            for (PillarData data : pillarData.values()) {
                if (data != null && data.hasColors()) {
                    colorsCount++;
                }
            }

            if (saveCount == 0 && lastFileSize > 0 && !allowEmptySave) {
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
                }

                if (fileHasColors && !hasLoaded) {
                    load();
                    return;
                }
            }

            if (allowEmptySave) {
                allowEmptySave = false;
            }


            saveToFile(getDataFile(), FILE_NAME);

            File mainFile = getDataFile();
            if (mainFile.exists()) {
                lastLoadedTime = mainFile.lastModified();
                lastFileSize = mainFile.length();
            }

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

            recoveryInProgress = true;
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

                                PillarData existingData = pillarData.get(pillarId);

                                if (existingData != null) {
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

                                    if (!clearColors) {
                                        java.util.List<String> pillarColors =
                                                pillarBE.getParticleColors();
                                        if (pillarColors != null && !pillarColors.isEmpty()) {
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
                                    } else if (clearColors && existingData.hasColors()) {
                                        existingData.clearColors();
                                        colorClearedCount++;
                                    }

                                    continue;
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

            syncColorsFromNBTToManager(server);

            recoveryInProgress = false;

            if (recoveredCount > 0 || colorClearedCount > 0) {
                saveImmediate();
            } else {
            }

        } catch (Exception e) {
            System.err.println(
                    "BuildScape: Error during pillar recovery: " + e.getMessage()
            );
            e.printStackTrace();
        } finally {
            recoveryInProgress = false;
        }
    }

    public void save() {
        saveImmediate();
    }

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

            File saveDir = cachedWorldSaveDir;
            if (saveDir == null || !saveDir.exists()) {
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
                }
            }

            if (saveDir == null || !saveDir.exists()) {
                System.err.println("BuildScape: forceSaveImmediate - Cannot find save directory!");
                return;
            }

            File saveFile = new File(saveDir, FILE_NAME);
            saveToFile(saveFile, FILE_NAME);

            File backupFile = new File(saveDir, BACKUP_FILE_NAME);
            saveToFile(backupFile, BACKUP_FILE_NAME);

            if (saveFile.exists()) {
                lastLoadedTime = saveFile.lastModified();
                lastFileSize = saveFile.length();
            }

            com.kingodogo.buildscape.network.ModMessages.INSTANCE.send(
                    net.minecraftforge.network.PacketDistributor.ALL.noArg(),
                    new com.kingodogo.buildscape.network.SyncPillarIdsPacket(getAllPillarDataForSync())
            );
        } catch (Throwable t) {
            System.err.println("BuildScape: Error in forceSaveImmediate: " + t.getMessage());
            t.printStackTrace();
        }
    }

    public void savePeriodic(boolean includeBackup) {
        if (recoveryInProgress || !hasLoaded) {
            return;
        }
        if (pillarData.isEmpty() && lastFileSize > 0L && !allowEmptySave) {
            return;
        }

        File mainFile = getDataFile();
        saveToFile(mainFile, FILE_NAME);
        if (mainFile.exists()) {
            lastLoadedTime = mainFile.lastModified();
            lastFileSize = mainFile.length();
        }
        if (includeBackup) {
            saveToFile(getBackupDataFile(), BACKUP_FILE_NAME);
        }
    }

    public void checkAndReload() {
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

    private synchronized void saveToFile(File file, String tempFileName) {
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
                Files.move(tempFile.toPath(), file.toPath(),
                        StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(tempFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
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


            saveToFile(getBackupDataFile(), BACKUP_FILE_NAME);
        } catch (Throwable t) {
            System.err.println("BuildScape: Error saving backup file: " + t.getMessage());
        }
    }

    public void cleanupOrphans(Level level) {
    }

    public void syncColorsFromNBTToManager(MinecraftServer server) {
        if (server == null) {
            return;
        }


        if (!hasLoaded()) {
            return;
        }

        try {
            int syncedCount = 0;
            int preservedCount = 0;
            int patternSyncedCount = 0;

            for (net.minecraft.server.level.ServerLevel level : server.getAllLevels()) {
                if (level == null) continue;

                String dimensionKey = getDimensionKey(level);

                for (PillarData data : pillarData.values()) {
                    if (data == null) continue;
                    if (!data.dimension.equals(dimensionKey)) continue;

                    int existingColorCount = (data.dyeColors != null) ? data.dyeColors.size() : 0;

                    try {
                        BlockPos pos = data.getBlockPos();

                        if (!level.isLoaded(pos)) {
                            preservedCount++;
                            continue;
                        }

                        net.minecraft.world.level.chunk.ChunkAccess chunk = level.getChunk(pos);
                        if (!(chunk instanceof net.minecraft.world.level.chunk.LevelChunk)) {
                            preservedCount++;
                            continue;
                        }

                        if (!chunk.getStatus().isOrAfter(net.minecraft.world.level.chunk.ChunkStatus.FULL)) {
                            preservedCount++;
                            continue;
                        }

                        net.minecraft.world.level.block.entity.BlockEntity be = level.getBlockEntity(pos);
                        if (!(be instanceof com.kingodogo.buildscape.block.PillarBlockEntity pillarBE)) {
                            preservedCount++;
                            continue;
                        }

                        BlockPos bottomPos = pillarBE.findStackBottom();
                        net.minecraft.world.level.block.entity.BlockEntity bottomBE = level.getBlockEntity(bottomPos);

                        if (!(bottomBE instanceof com.kingodogo.buildscape.block.PillarBlockEntity bottomPillarBE)) {
                            preservedCount++;
                            continue;
                        }

                        java.util.List<String> nbtColors = bottomPillarBE.getParticleColors();

                        if (nbtColors != null && !nbtColors.isEmpty()) {
                            boolean needsSync = false;
                            if (data.dyeColors == null || data.dyeColors.isEmpty()) {
                                needsSync = true;
                            } else if (data.dyeColors.size() != nbtColors.size()) {
                                needsSync = true;
                            } else {
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
                                data.clearColors();
                                for (String color : nbtColors) {
                                    if (color != null && !color.isEmpty()) {
                                        data.addColor(color);
                                    }
                                }
                                syncedCount++;
                            } else {
                                preservedCount++;
                            }

                            if (syncPatternSettingsFromNBT(bottomPillarBE, data)) {
                                patternSyncedCount++;
                            }
                        } else {
                            if (existingColorCount > 0) {
                                preservedCount++;
                            }
                            if (syncPatternSettingsFromNBT(bottomPillarBE, data)) {
                                patternSyncedCount++;
                            }
                        }
                    } catch (Exception e) {
                        preservedCount++;
                        System.err.println(
                                "BuildScape: Error syncing colors from NBT for pillar " +
                                        (data != null ? data.id : "unknown") +
                                        ": " + e.getMessage()
                        );
                    }
                }
            }

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

                        BlockPos bottomPos = pillarBE.findStackBottom();
                        net.minecraft.world.level.block.entity.BlockEntity bottomBE = level.getBlockEntity(bottomPos);

                        if (!(bottomBE instanceof com.kingodogo.buildscape.block.PillarBlockEntity bottomPillarBE)) {
                            continue;
                        }

                        java.util.List<String> nbtColors = bottomPillarBE.getParticleColors();

                        if (nbtColors != null && !nbtColors.isEmpty()) {
                            data.clearColors();
                            for (String color : nbtColors) {
                                if (color != null && !color.isEmpty()) {
                                    data.addColor(color);
                                }
                            }
                            loadedCount++;
                        } else {
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

    public boolean syncPatternSettingsFromNBT(
            com.kingodogo.buildscape.block.PillarBlockEntity pillarBE,
            PillarData data
    ) {
        if (pillarBE == null || data == null) {
            return false;
        }

        boolean needsSave = false;

        String pillarType = net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(pillarBE.getBlockState().getBlock().asItem()).toString();
        if (data.pillarType == null || !data.pillarType.equals(pillarType)) {
            data.pillarType = pillarType;
            needsSave = true;
        }

        String nbtPattern = pillarBE.getParticlePattern();
        if (nbtPattern != null && !nbtPattern.isEmpty()) {
            if (data.pattern == null || !data.pattern.equals(nbtPattern)) {
                data.pattern = nbtPattern;
                needsSave = true;
            }
        }

        Double nbtSpeed = pillarBE.getPatternSpeed();
        if (nbtSpeed != null) {
            if (data.pattern_speed == null || !data.pattern_speed.equals(nbtSpeed)) {
                data.pattern_speed = nbtSpeed;
                needsSave = true;
            }
        }

        Double nbtSpread = pillarBE.getPatternSpread();
        if (nbtSpread != null) {
            if (data.pattern_spread == null || !data.pattern_spread.equals(nbtSpread)) {
                data.pattern_spread = nbtSpread;
                needsSave = true;
            }
        }

        Double nbtIntensity = pillarBE.getPatternIntensity();
        if (nbtIntensity != null) {
            if (data.pattern_intensity == null || !data.pattern_intensity.equals(nbtIntensity)) {
                data.pattern_intensity = nbtIntensity;
                needsSave = true;
            }
        }

        java.util.List<String> nbtColors = pillarBE.getParticleColors();
        if (nbtColors != null && !nbtColors.isEmpty()) {
            int nbtColorCount = nbtColors.size();
            if (data.max_particle_color == null || data.max_particle_color != nbtColorCount) {
                data.max_particle_color = nbtColorCount;
                needsSave = true;
            }
        }

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


    public Map<String, PillarData> copyDataSnapshot() {
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
                copy.displayedItem = original.displayedItem;
                copy.pillarType = original.pillarType;
                copy.itemYaw = original.itemYaw;
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

        positionIndex.clear();
        for (PillarData data : newData.values()) {
            if (data != null && data.dimension != null) {
                try {
                    net.minecraft.core.Direction facing = null;
                    if (data.facing != null) {
                        facing = net.minecraft.core.Direction.byName(data.facing);
                    }
                    positionIndex.put(positionKey(data.dimension, data.getBlockPos(), facing), data.id);
                } catch (Exception ignored) {
                }
            }
        }

        saveImmediate();
    }

    public void clearForServerSync() {
        pillarData.clear();
        positionIndex.clear();
        com.kingodogo.buildscape.event.ItemFrameParticleHandler.clearClientCaches();
        hasLoaded = false;
    }

    public void addPillarDataFromSync(PillarData data) {
        if (data == null || data.id == null) {
            return;
        }
        pillarData.put(data.id, data);
        if (data.dimension != null) {
            try {
                net.minecraft.core.Direction facing = null;
                if (data.facing != null) {
                    facing = net.minecraft.core.Direction.byName(data.facing);
                }
                positionIndex.put(positionKey(data.dimension, data.getBlockPos(), facing), data.id);
            } catch (Exception ignored) {
            }
        }
    }

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

        PillarData existing = pillarData.get(id);
        if (existing == null) {
            PillarData data = new PillarData(id, dimension, pos);

            syncPatternSettingsFromNBT(pillar, data);

            List<String> colors = pillar.getParticleColors();
            if (colors != null && !colors.isEmpty()) {
                for (String c : colors) data.addColor(c);
            }

            pillarData.put(id, data);
            positionIndex.put(posKey, id);

            if (com.kingodogo.buildscape.BuildScape.isServerFullyInitialized()) {
                saveImmediate();
            }
        } else {
            positionIndex.put(posKey, id);

            if (syncPatternSettingsFromNBT(pillar, existing)) {
                if (com.kingodogo.buildscape.BuildScape.isServerFullyInitialized()) {
                    saveImmediate();
                }
            }
        }
    }

    public void registerItemFrame(net.minecraft.world.entity.decoration.ItemFrame frame) {
        if (frame == null || frame.level == null || frame.level.isClientSide) {
            return;
        }

        CompoundTag dataTag = frame.getPersistentData();
        String id = dataTag.getString("BuildScapeFrameId");

        if (id == null || id.isEmpty()) {
            id = com.kingodogo.buildscape.event.ItemFrameParticleHandler.getFrameId(frame);
        }

        String pattern = dataTag.getString("BuildScapeParticlePattern");
        List<String> colors = new ArrayList<>();
        if (dataTag.contains("BuildScapeParticleColors")) {
            net.minecraft.nbt.ListTag colorList = dataTag.getList("BuildScapeParticleColors", 8);
            for (int i = 0; i < colorList.size(); i++) {
                colors.add(colorList.getString(i));
            }
        }

        String dimension = getDimensionKey(frame.level);
        BlockPos pos = frame.blockPosition();
        net.minecraft.core.Direction facing = frame.getDirection();
        String posKey = positionKey(dimension, pos, facing);

        PillarData existing = pillarData.get(id);
        if (existing == null) {
            PillarData data = new PillarData(id, dimension, pos);
            data.pattern = pattern;
            data.dyeColors = colors;

            data.pillarType = "minecraft:item_frame";
            data.facing = facing != null ? facing.getSerializedName() : null;

            if (!frame.getItem().isEmpty()) {
                net.minecraft.resources.ResourceLocation itemRL = net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(frame.getItem().getItem());
                if (itemRL != null) data.displayedItem = itemRL.toString();
            }

            pillarData.put(id, data);
            positionIndex.put(posKey, id);

            if (com.kingodogo.buildscape.BuildScape.isServerFullyInitialized()) {
                saveImmediate();
            }
        } else {
            positionIndex.put(posKey, id);
            boolean changed = false;

            if (existing.pillarType == null || !existing.pillarType.equals("minecraft:item_frame")) {
                existing.pillarType = "minecraft:item_frame";
                changed = true;
            }
            if (!Objects.equals(existing.facing, facing != null ? facing.getSerializedName() : null)) {
                existing.facing = facing != null ? facing.getSerializedName() : null;
                changed = true;
            }

            if (!Objects.equals(existing.pattern, pattern)) {
                existing.pattern = pattern;
                changed = true;
            }
            if (!Objects.equals(existing.dyeColors, colors)) {
                existing.dyeColors = colors;
                changed = true;
            }

            if (!frame.getItem().isEmpty()) {
                net.minecraft.resources.ResourceLocation itemKey = net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(frame.getItem().getItem());
                String itemId = itemKey != null ? itemKey.toString() : null;
                if (!Objects.equals(existing.displayedItem, itemId)) {
                    existing.displayedItem = itemId;
                    changed = true;
                }
            } else if (existing.displayedItem != null) {
                existing.displayedItem = null;
                changed = true;
            }

            if (changed && com.kingodogo.buildscape.BuildScape.isServerFullyInitialized()) {
                saveImmediate();
            }
        }
    }


    public void registerColoredItemFrame(com.kingodogo.buildscape.entity.ColoredItemFrameEntity frame) {
        if (frame == null || frame.level == null || frame.level.isClientSide) {
            return;
        }

        CompoundTag dataTag = frame.getPersistentData();
        String id = dataTag.getString("BuildScapeFrameId");

        if (id == null || id.isEmpty()) {
            id = com.kingodogo.buildscape.event.ItemFrameParticleHandler.getFrameIdColored(frame);
        }

        String pattern = dataTag.getString("BuildScapeParticlePattern");
        List<String> colors = new ArrayList<>();
        if (dataTag.contains("BuildScapeParticleColors")) {
            net.minecraft.nbt.ListTag colorList = dataTag.getList("BuildScapeParticleColors", 8);
            for (int i = 0; i < colorList.size(); i++) {
                colors.add(colorList.getString(i));
            }
        }

        String dimension = getDimensionKey(frame.level);
        BlockPos pos = frame.blockPosition();
        net.minecraft.core.Direction facing = frame.getDirection();
        String posKey = positionKey(dimension, pos, facing);

        PillarData existing = pillarData.get(id);
        if (existing == null) {
            PillarData data = new PillarData(id, dimension, pos);
            data.pattern = pattern;
            data.dyeColors = colors;

            String color = frame.getColorVariant();
            if (color == null || color.isEmpty()) color = "white";
            else color = color.toLowerCase(java.util.Locale.ROOT);

            net.minecraft.resources.ResourceLocation typeRL = new net.minecraft.resources.ResourceLocation("buildscape", color + "_item_frame");
            data.pillarType = typeRL.toString();
            data.facing = facing != null ? facing.getSerializedName() : null;

            if (!frame.getItem().isEmpty()) {
                net.minecraft.resources.ResourceLocation itemKey = net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(frame.getItem().getItem());
                if (itemKey != null) {
                    data.displayedItem = itemKey.toString();
                }
            }

            pillarData.put(id, data);
            positionIndex.put(posKey, id);

            if (com.kingodogo.buildscape.BuildScape.isServerFullyInitialized()) {
                saveImmediate();
            }
        } else {
            positionIndex.put(posKey, id);
            boolean changed = false;

            String color = frame.getColorVariant();
            if (color == null || color.isEmpty()) color = "white";
            else color = color.toLowerCase(java.util.Locale.ROOT);
            String expectedType = "buildscape:" + color + "_item_frame";

            if (existing.pillarType == null || !existing.pillarType.equals(expectedType)) {
                existing.pillarType = expectedType;
                changed = true;
            }
            if (!Objects.equals(existing.facing, facing != null ? facing.getSerializedName() : null)) {
                existing.facing = facing != null ? facing.getSerializedName() : null;
                changed = true;
            }

            if (!Objects.equals(existing.pattern, pattern)) {
                existing.pattern = pattern;
                changed = true;
            }
            if (!Objects.equals(existing.dyeColors, colors)) {
                existing.dyeColors = colors;
                changed = true;
            }

            if (!frame.getItem().isEmpty()) {
                net.minecraft.resources.ResourceLocation itemKey = net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(frame.getItem().getItem());
                String itemId = itemKey != null ? itemKey.toString() : null;
                if (!Objects.equals(existing.displayedItem, itemId)) {
                    existing.displayedItem = itemId;
                    changed = true;
                }
            } else if (existing.displayedItem != null) {
                existing.displayedItem = null;
                changed = true;
            }

            if (changed && com.kingodogo.buildscape.BuildScape.isServerFullyInitialized()) {
                saveImmediate();
            }
        }
    }

    public void markAsLoaded() {
        hasLoaded = true;
        isServerSynced = true;
    }

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

        public Boolean use_pattern = null;
        public String pattern = null;
        public Double pattern_speed = null;
        public Double pattern_spread = null;
        public Double pattern_intensity = null;
        public Integer max_particle_color = null;

        public String displayedItem = null;
        public String pillarType = null;
        public String facing = null;
        public Float itemYaw = null;

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
