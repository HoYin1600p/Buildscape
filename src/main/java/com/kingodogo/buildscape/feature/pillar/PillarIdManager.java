package com.kingodogo.buildscape.feature.pillar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.core.NbtKeys;
import com.kingodogo.buildscape.core.ParticlePatterns;
import com.kingodogo.buildscape.core.io.AtomicFiles;
import com.kingodogo.buildscape.core.io.Json;
import com.kingodogo.buildscape.entity.ColoredItemFrameEntity;
import com.kingodogo.buildscape.network.EditPillarRowsPacket;
import com.kingodogo.buildscape.network.SyncPillarIdsPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

public class PillarIdManager {

    private static final Gson GSON = Json.PRETTY;
    private static final String FILE_NAME = "pillar-ids.dat";
    private static final String BACKUP_FILE_NAME = "pillar-ids.bak.dat";
    private static final String FOLDER_NAME = "buildscape";
    private static final int MAX_REMOVED_FRAMES = 4096;
    private static final PillarIdManager SERVER_INSTANCE = new PillarIdManager(true);
    private static final PillarIdManager CLIENT_INSTANCE = new PillarIdManager(false);

    private final boolean serverAuthoritative;

    private final Map<String, PillarData> pillarData = new ConcurrentHashMap<>();
    private final Map<String, PillarData> removedFrames = new ConcurrentHashMap<>();

    public static final String VALID_ID_PATTERN = "^[A-Z][A-Za-z0-9-]{1,31}$";
    private final Map<String, String> positionIndex = new ConcurrentHashMap<>();
    private final Set<LevelChunk> loadedChunks = Collections.newSetFromMap(new WeakHashMap<>());
    private volatile List<PillarData> syncSnapshot = Collections.emptyList();

    private long lastLoadedTime = 0L;
    private long lastFileSize = 0L;

    private volatile boolean hasLoaded = false;
    private volatile boolean loadInProgress = false;
    private boolean hadColorsOnLoad = false;
    private boolean isServerSynced = false;
    private boolean allowEmptySave = false;
    private volatile boolean dirty = false;
    private volatile boolean syncDirty = false;

    private static boolean recoveryScheduled = false;
    private static long recoveryScheduledTime = 0L;
    private static boolean recoveryClearColors = false;
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
        recoveryClearColors = false;
        recoveryInProgress = false;

        SERVER_INSTANCE.resetState(false);
    }

    public static PillarIdManager get() {
        return SERVER_INSTANCE;
    }

    public static PillarIdManager getClient() {
        return CLIENT_INSTANCE;
    }

    public static PillarIdManager get(Level level) {
        return level != null && level.isClientSide ? CLIENT_INSTANCE : SERVER_INSTANCE;
    }

    public static void resetClientCache() {
        CLIENT_INSTANCE.resetState(true);
    }

    private PillarIdManager(boolean serverAuthoritative) {
        this.serverAuthoritative = serverAuthoritative;
    }

    private void resetState(boolean serverSynced) {
        isServerSynced = serverSynced;
        pillarData.clear();
        removedFrames.clear();
        positionIndex.clear();
        loadedChunks.clear();
        hasLoaded = false;
        loadInProgress = false;
        hadColorsOnLoad = false;
        lastLoadedTime = 0L;
        lastFileSize = 0L;
        allowEmptySave = false;
        dirty = false;
        saveRequested = false;
        syncDirty = false;
        corruptedFileUnpreserved = false;
        fileWasDeleted = false;
        syncSnapshot = Collections.emptyList();
    }

    public boolean hasLoaded() {
        return hasLoaded;
    }

    public boolean isLoadInProgress() {
        return loadInProgress;
    }

    public boolean needsWorldRecovery() {
        return serverAuthoritative && hasLoaded && fileWasDeleted && pillarData.isEmpty();
    }

    public static String getVariantPrefix(Level level, BlockPos pos) {
        if (level == null || pos == null) return PREFIX_STONE + "-P";

        if (
                !level.isClientSide &&
                        !BuildScape.isServerFullyInitialized()
        ) {
            return PREFIX_STONE + "-P";
        }

        if (!level.hasChunkAt(pos)) {
            return PREFIX_STONE + "-P";
        }

        try {
            if (!level.isClientSide) {
                ChunkAccess chunk = level.getChunk(pos);
                if (!(chunk instanceof LevelChunk)) {
                    return PREFIX_STONE + "-P";
                }
                if (
                        !chunk
                                .getStatus()
                                .isOrAfter(ChunkStatus.FULL)
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
                    ForgeRegistries.BLOCKS.getKey(
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
            if (!BuildScape.isServerFullyInitialized()) {
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
        recoveryClearColors = false;
        recoveryInProgress = false;

        SERVER_INSTANCE.resetState(false);
    }

    public static void checkAndRunScheduledRecovery() {
        if (!recoveryScheduled) {
            return;
        }

        long elapsed = System.currentTimeMillis() - recoveryScheduledTime;
        if (elapsed < RECOVERY_DELAY_MS) {
            return;
        }

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

        if (server.getPlayerList().getPlayerCount() == 0 || !isWorldReadyForRecovery()) {
            return;
        }

        recoveryScheduled = false;
        boolean clearColors = recoveryClearColors;
        recoveryClearColors = false;
        manager.recoverPillarsFromWorld(server, clearColors);
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
            String id = prefix + String.format(Locale.ROOT, "%04d", num);
            if (!pillarData.containsKey(id)) {
                return id;
            }
        }

        for (int num = 1; num <= 9999; num++) {
            String id = prefix + String.format(Locale.ROOT, "%04d", num);
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
        if (state.getBlock() instanceof PillarBlock) {
            BlockPos current = pos;
            int limit = 256;
            while (limit > 0 && level.getBlockState(current.below()).getBlock() instanceof PillarBlock) {
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
                return existing;
            } else {
                positionIndex.remove(posKey);
            }
        }

        String id = generatePillarId(expectedPrefix);
        PillarData newData = new PillarData(id, dimension, basePos);

        BlockEntity be = level.getBlockEntity(basePos);
        if (be instanceof PillarBlockEntity pillar) {
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
            requestSave();
        }
        return newData;
    }

    private String positionKey(String dimension, BlockPos pos) {
        return positionKey(dimension, pos, null);
    }

    public String positionKey(String dimension, BlockPos pos, Direction facing) {
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

    public Collection<PillarData> getAllData() {
        return pillarData.values();
    }

    public PillarData getPillarData(String pillarId) {
        if (pillarId == null) return null;
        return pillarData.get(pillarId);
    }

    public String addDyeColor(Level level, BlockPos pos, String colorCode) {
        PillarData data = getOrCreatePillarData(level, pos);

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof PillarBlockEntity pillar) {
            syncPatternSettingsFromNBT(pillar, data);
        }

        if (data.addColor(colorCode)) {
            if (data.pattern == null || data.pattern.equals(ParticlePatterns.DEFAULT)) {
                data.pattern = PillarParticleConfig.get().pattern;
                if (data.pattern == null || data.pattern.isEmpty()) {
                    data.pattern = ParticlePatterns.RING;
                }
            }
            requestSave();
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

    public boolean isIdTaken(String id) {
        return id != null && (pillarData.containsKey(id) || removedFrames.containsKey(id));
    }

    private volatile boolean saveRequested = false;

    public void requestSave() {
        if (!serverAuthoritative) {
            return;
        }
        dirty = true;
        saveRequested = true;
    }

    public void flushRequestedSave() {
        if (saveRequested) {
            saveRequested = false;
            if (!saveImmediate()) saveRequested = true;
        }
        if (syncDirty && hasLoaded) {
            try {
                refreshSyncSnapshot();
                SyncPillarIdsPacket.sendToAll(getAllPillarDataForSync());
                syncDirty = false;
            } catch (RuntimeException e) {
                BuildScape.LOGGER.error("Unable to sync pillar data to players", e);
            }
        }
    }

    private static boolean isSameFrameLocation(PillarData data, String dimension, BlockPos pos, Direction facing) {
        if (!Objects.equals(data.dimension, dimension)) return false;
        if (data.x != pos.getX() || data.y != pos.getY() || data.z != pos.getZ()) return false;
        return data.facing == null || facing == null || data.facing.equals(facing.getSerializedName());
    }

    public void removePillar(String pillarId) {
        removePillars(Collections.singleton(pillarId));
    }

    public void unregisterDestroyedFrame(String frameId) {
        removePillars(Collections.singleton(frameId), false);
    }

    public void removePillarData(String pillarId) {
        removePillar(pillarId);
    }

    public void saveAllPillars() {
        saveImmediate();
    }

    public void updatePillarData(PillarData data) {
        if (data != null && data.id != null) {
            pillarData.put(data.id, data);
            data.modifiedTime = System.currentTimeMillis();
            saveImmediate();
        }
    }

    public static void clearPillarOverrides(PillarData data) {
        if (data == null) {
            return;
        }
        if (data.dyeColors == null) {
            data.dyeColors = new ArrayList<>();
        } else {
            data.dyeColors.clear();
        }
        data.use_pattern = null;
        data.pattern = null;
        data.pattern_speed = null;
        data.pattern_spread = null;
        data.pattern_intensity = null;
        data.max_particle_color = null;
    }

    /**
     * Clears the particle overrides of the given pillars. A frame keeps its pattern and colours in the
     * entity, so a reset frame is recorded like a frame removed in the screen: loaded frames are cleared
     * now and unloaded ones are cleared when they load, instead of copying the old values back in.
     */
    public boolean resetPillars(Collection<String> pillarIds) {
        if (pillarIds == null) return false;
        boolean changed = false;
        Set<String> frames = new LinkedHashSet<>();
        for (String id : pillarIds) {
            PillarData existing = id == null ? null : pillarData.get(id);
            if (existing == null) continue;
            clearPillarOverrides(existing);
            if (serverAuthoritative && isItemFrameData(existing)) frames.add(id);
            changed = true;
        }
        if (!frames.isEmpty()) removePillars(frames, true);
        return changed;
    }

    public boolean removePillars(Collection<String> pillarIds) {
        return removePillars(pillarIds, true);
    }

    private boolean removePillars(Collection<String> pillarIds, boolean rememberRemovedFrames) {
        if (pillarIds == null || pillarIds.isEmpty()) {
            return false;
        }

        boolean changed = false;
        Set<String> removedIds = new HashSet<>();
        for (String pillarId : pillarIds) {
            if (pillarId == null) {
                continue;
            }
            PillarData data = pillarData.remove(pillarId);
            if (data == null) {
                continue;
            }
            removedIds.add(pillarId);
            if (serverAuthoritative) {
                if (isItemFrameData(data)) {
                    if (rememberRemovedFrames) {
                        data.removed = true;
                        data.frameEditPending = null;
                        data.modifiedTime = System.currentTimeMillis();
                        removedFrames.put(pillarId, data);
                        resetLoadedFrame(data);
                    }
                } else {
                    PillarResetHandler.resetPillarFromData(data);
                }
            }
            changed = true;
        }

        if (changed) {
            pruneRemovedFrames();
            positionIndex.values().removeIf(removedIds::contains);
            if (pillarData.isEmpty()) {
                allowEmptySave = true;
            }
            requestSave();
        }
        return changed;
    }

    private boolean pruneRemovedFrames() {
        int excess = removedFrames.size() - MAX_REMOVED_FRAMES;
        if (excess <= 0) return false;
        List<PillarData> oldest = new ArrayList<>(removedFrames.values());
        oldest.sort(Comparator.comparingLong((PillarData data) -> data.modifiedTime)
                .thenComparing(data -> data.id));
        for (int i = 0; i < excess; i++) {
            PillarData data = oldest.get(i);
            removedFrames.remove(data.id, data);
        }
        return true;
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
                requestSave();
            }
        }
    }

    public void removePillarByPosition(Level level, BlockPos pos) {
        removePillarByPosition(getDimensionKey(level), pos);
    }

    private void removePillarByPosition(String dimension, BlockPos pos) {
        String posKey = positionKey(dimension, pos);

        String idToRemove = positionIndex.remove(posKey);
        if (idToRemove != null) {
            PillarData data = pillarData.get(idToRemove);
            if (data != null && Objects.equals(data.dimension, dimension)
                    && data.getBlockPos().equals(pos)) {
                removePillar(idToRemove);
            }
        }
    }

    public void removeStalePillarPositionIndex(Level level, BlockPos pos) {
        String dimension = getDimensionKey(level);
        String key = positionKey(dimension, pos);
        String id = positionIndex.get(key);
        if (id != null) {
            PillarData data = pillarData.get(id);
            if (data == null || !Objects.equals(data.dimension, dimension)
                    || !data.getBlockPos().equals(pos)) {
                positionIndex.remove(key, id);
            }
        }
    }

    public void updatePillarPosition(String pillarId, BlockPos pos) {
        PillarData data = pillarData.get(pillarId);
        if (data != null && !data.getBlockPos().equals(pos)) {
            data.x = pos.getX();
            data.y = pos.getY();
            data.z = pos.getZ();
            rebuildPositionIndex();
            requestSave();
        }
    }

    public BlockPos removePillarBlock(Level level, String pillarId, BlockPos pos, BlockPos bottom, BlockPos top) {
        return removePillarBlock(getDimensionKey(level), pillarId, pos, bottom, top);
    }

    private BlockPos removePillarBlock(String dimension, String pillarId, BlockPos pos, BlockPos bottom, BlockPos top) {
        BlockPos remaining = bottom.equals(pos) ? (top.equals(pos) ? null : pos.above()) : bottom;
        if (remaining == null) {
            if (pillarId != null && !pillarId.isEmpty()) {
                removePillar(pillarId);
            }
            removePillarByPosition(dimension, pos);
        } else if (pillarId != null) {
            updatePillarPosition(pillarId, remaining);
        }
        return remaining;
    }

    public void mergePillarStack(String pillarId, BlockPos bottom, BlockPos top) {
        PillarData data = pillarData.get(pillarId);
        if (data == null) return;

        boolean changed = false;
        for (PillarData other : new ArrayList<>(pillarData.values())) {
            if (!other.id.equals(pillarId) && !isItemFrameData(other)
                    && Objects.equals(other.dimension, data.dimension)
                    && other.x == bottom.getX() && other.z == bottom.getZ()
                    && other.y >= bottom.getY() && other.y <= top.getY()) {
                changed |= pillarData.remove(other.id, other);
            }
        }
        if (!data.getBlockPos().equals(bottom)) {
            data.x = bottom.getX();
            data.y = bottom.getY();
            data.z = bottom.getZ();
            changed = true;
        }
        if (changed) {
            rebuildPositionIndex();
            requestSave();
        }
    }

    public void clearPillarColors(String pillarId) {
        PillarData data = pillarData.get(pillarId);
        if (data != null) {
            data.clearColors();
            requestSave();
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

            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof PillarBlockEntity pillar) {
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
                requestSave();
            }
        }
    }

    public void load() {
        if (!serverAuthoritative) {
            return;
        }
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
            if (hasLoaded && pillarData.isEmpty() && file.exists() && !dirty) {
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
                    BuildScape.LOGGER.error("Error in async file load", e);
                    hasLoaded = true;
                } finally {
                    loadInProgress = false;
                }
            });
        } catch (Throwable t) {
            BuildScape.LOGGER.error("Critical error in load() - will recover from world later", t);
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

    private boolean corruptedFileUnpreserved = false;

    private void handleCorruptedFile(File file, Throwable error, String errorType) {
        lastFileSize = Math.max(lastFileSize, file.length());
        corruptedFileUnpreserved = true;
        try {
            Files.copy(file.toPath(), new File(file.getParentFile(),
                    file.getName() + ".corrupted." + System.currentTimeMillis()).toPath());
            corruptedFileUnpreserved = false;
        } catch (IOException e) {
            LogManager.getLogger(PillarIdManager.class).error("Unable to preserve corrupted pillar data; saves are blocked", e);
        }
        LogManager.getLogger(PillarIdManager.class).error("Unable to read pillar data (" + errorType + ")", error);
    }

    private boolean fileWasDeleted = false;

    private void processLoadedData(Map<String, PillarData> loaded, MinecraftServer server, File sourceFile) {
        Map<String, PillarData> existingData = new HashMap<>(pillarData);
        pillarData.clear();
        boolean changed = false;
        if (loaded != null) {
            for (Map.Entry<String, PillarData> entry : loaded.entrySet()) {
                String id = entry.getKey();
                PillarData data = entry.getValue();
                if (id == null || !id.matches(VALID_ID_PATTERN) || data == null) {
                    changed = true;
                    continue;
                }
                data.id = id;
                if (data.dimension == null || data.dimension.isEmpty()) {
                    data.dimension = "minecraft:overworld";
                    changed = true;
                }
                if (data.dyeColors == null) {
                    data.dyeColors = new ArrayList<>();
                    changed = true;
                }
                if (Boolean.TRUE.equals(data.removed) && isItemFrameData(data)) {
                    removedFrames.put(id, data);
                } else {
                    pillarData.put(id, data);
                }
            }
        }
        changed |= pruneRemovedFrames();
        rebuildPositionIndex();
        for (Map.Entry<String, PillarData> entry : existingData.entrySet()) {
            if (removedFrames.containsKey(entry.getKey())) continue;
            PillarData data = entry.getValue();
            if (isItemFrameData(data) && isRemovedFrame(data.id, data.dimension, data.getBlockPos(),
                    data.facing == null ? null : Direction.byName(data.facing))) continue;
            if (pillarData.putIfAbsent(entry.getKey(), entry.getValue()) == null) changed = true;
        }
        changed |= pruneRegistry();
        hadColorsOnLoad = pillarData.values().stream().anyMatch(PillarData::hasColors);
        fileWasDeleted = false;
        if (sourceFile != null) {
            lastLoadedTime = sourceFile.lastModified();
            lastFileSize = Math.max(lastFileSize, sourceFile.length());
        }
        hasLoaded = true;
        for (PillarData removed : removedFrames.values()) resetLoadedFrame(removed);
        for (PillarData edited : new ArrayList<>(pillarData.values())) {
            if (edited.frameEditPending != null && isItemFrameData(edited)) {
                applyFrameEdit(edited);
                if (edited.frameEditPending == null) changed = true;
            }
        }
        updateCachedWorldDir();
        refreshSyncSnapshot();
        if (changed) {
            if (pillarData.isEmpty()) allowEmptySave = true;
            requestSave();
        }
    }

    private void loadFileAsync(MinecraftServer server) {
        try {
            File file = getDataFile();

            SavedData saved = readSavedData(file, getBackupDataFile());
            Map<String, PillarData> loadedData = saved.data;
            File sourceFile = saved.file;
            if (loadedData == null || loadedData.isEmpty()) {
                fileWasDeleted = true;
                pillarData.clear();
                lastLoadedTime = 0L;
                lastFileSize = Math.max(lastFileSize, file.length());
                hasLoaded = true;
                scheduleRecoveryFromWorld(server, false);
                return;
            }

            processLoadedData(loadedData, server, sourceFile);

        } catch (Throwable t) {
            BuildScape.LOGGER.error("Critical error in loadFileAsync() - will recover after world is fully loaded", t);
            fileWasDeleted = true;
            pillarData.clear();
            lastLoadedTime = 0L;

            hasLoaded = true;
            scheduleRecoveryFromWorld(server, false);
        }
    }

    private static class SavedData {
        private final File file;
        private final Map<String, PillarData> data;

        private SavedData(File file, Map<String, PillarData> data) {
            this.file = file;
            this.data = data;
        }
    }

    private SavedData readSavedData(File file, File backupFile) {
        if (file.exists() && file.length() > 0) {
            try {
                Map<String, PillarData> loaded = loadFromFile(file);
                if (loaded != null) return new SavedData(file, loaded);
            } catch (Exception e) {
                handleCorruptedFile(file, e, "parse");
            }
        }
        if (backupFile.exists() && backupFile.length() > 0) {
            try {
                Map<String, PillarData> loaded = loadFromFile(backupFile);
                if (loaded != null && !loaded.isEmpty()) return new SavedData(backupFile, loaded);
            } catch (Exception e) {
                LogManager.getLogger(PillarIdManager.class).error("Unable to read pillar backup", e);
            }
        }
        return new SavedData(file, Collections.emptyMap());
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

    public synchronized boolean saveImmediate() {
        if (!serverAuthoritative) {
            return true;
        }
        dirty = true;
        try {
            if (recoveryInProgress) {
                return false;
            }

            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server == null) {
                return false;
            }

            if (!BuildScape.isServerFullyInitialized()) {
                return false;
            }

            if (pillarData.isEmpty() && lastFileSize > 0 && !allowEmptySave) {
                return false;
            }
            if (!saveToFile(getDataFile(), FILE_NAME)) {
                return false;
            }
            if (allowEmptySave && !saveToFile(getBackupDataFile(), BACKUP_FILE_NAME)) {
                return false;
            }
            allowEmptySave = false;

            File mainFile = getDataFile();
            if (mainFile.exists()) {
                lastLoadedTime = mainFile.lastModified();
                lastFileSize = mainFile.length();
            }

            dirty = false;
            fileWasDeleted = false;
            saveRequested = false;
            syncDirty = true;
            return true;
        } catch (Throwable t) {
            BuildScape.LOGGER.error("Unable to save pillar data", t);
            return false;
        }
    }

    public void scheduleRecoveryFromWorld(
            MinecraftServer server,
            boolean clearColors
    ) {
        if (!serverAuthoritative || server == null || !server.isRunning()) {
            return;
        }
        recoveryScheduled = true;
        recoveryScheduledTime = System.currentTimeMillis();
        recoveryClearColors = clearColors;
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

            for (ServerLevel level : server.getAllLevels()) {
                if (level == null) continue;

                try {
                    int chunkRange = 64;
                    BlockPos spawnPos = level.getSharedSpawnPos();
                    ChunkPos spawnChunk = spawnPos != null
                            ? new ChunkPos(spawnPos)
                            : new ChunkPos(0, 0);

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
                            ChunkPos chunkPos =
                                    new ChunkPos(chunkX, chunkZ);

                            if (!level.hasChunkAt(chunkPos.getWorldPosition())) {
                                continue;
                            }

                            try {
                                ChunkAccess chunkAccess =
                                        level.getChunk(chunkX, chunkZ);

                                if (
                                        !(chunkAccess instanceof LevelChunk chunk)
                                ) {
                                    continue;
                                }

                                if (
                                        !chunkAccess
                                                .getStatus()
                                                .isOrAfter(ChunkStatus.FULL)
                                ) {
                                    continue;
                                }

                                for (BlockEntity be : chunk
                                        .getBlockEntities()
                                        .values()) {
                                    if (
                                            be instanceof PillarBlockEntity pillarBE
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
                    BuildScape.LOGGER.error("Error scanning chunks to clear pillar IDs: {}", e.getMessage());
                }
            }

        } catch (Exception e) {
            BuildScape.LOGGER.error("Error clearing pillar IDs from world", e);
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
                requestSave();
                return;
            }

            recoveryInProgress = true;
            int recoveredCount = 0;
            int skippedCount = 0;
            int colorClearedCount = 0;

            for (ServerLevel level : server.getAllLevels()) {
                if (level == null) continue;

                String dimensionKey = getDimensionKey(level);

                try {
                    Set<
                            BlockEntity
                            > allBlockEntities = new HashSet<>();

                    int chunkRange = 64;
                    BlockPos spawnPos = level.getSharedSpawnPos();
                    ChunkPos spawnChunk = spawnPos != null
                            ? new ChunkPos(spawnPos)
                            : new ChunkPos(0, 0);

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
                            ChunkPos chunkPos =
                                    new ChunkPos(chunkX, chunkZ);

                            if (!level.hasChunkAt(chunkPos.getWorldPosition())) {
                                continue;
                            }

                            try {
                                ChunkAccess chunkAccess =
                                        level.getChunk(chunkX, chunkZ);

                                if (
                                        !(chunkAccess instanceof LevelChunk chunk)
                                ) {
                                    continue;
                                }

                                if (
                                        !chunkAccess
                                                .getStatus()
                                                .isOrAfter(ChunkStatus.FULL)
                                ) {
                                    continue;
                                }

                                for (BlockEntity be : chunk
                                        .getBlockEntities()
                                        .values()) {
                                    if (
                                            be instanceof
                                                    PillarBlockEntity
                                    ) {
                                        allBlockEntities.add(be);
                                    }
                                }
                            } catch (Exception e) {
                                continue;
                            }
                        }
                    }

                    for (BlockEntity be : allBlockEntities) {
                        if (
                                be instanceof PillarBlockEntity pillarBE
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
                                        requestSave();
                                    }

                                    if (!clearColors) {
                                        List<String> pillarColors =
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
                                    List<String> pillarColors =
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
                                BuildScape.LOGGER.error("Error processing pillar: {}", e.getMessage());
                                skippedCount++;
                            }
                        }
                    }
                } catch (Exception e) {
                    BuildScape.LOGGER.error("Error scanning chunks for pillars: {}", e.getMessage());
                    skippedCount++;
                }
            }

            rebuildPositionIndex();
            syncColorsFromNBTToManager(server);

            recoveryInProgress = false;

            if (recoveredCount > 0 || colorClearedCount > 0) {
                requestSave();
            } else {
            }

        } catch (Exception e) {
            BuildScape.LOGGER.error("Error during pillar recovery", e);
        } finally {
            recoveryInProgress = false;
        }
    }

    public void save() {
        requestSave();
    }

    public void savePeriodic(boolean includeBackup) {
        if (!serverAuthoritative) {
            return;
        }
        if (recoveryInProgress || !hasLoaded) {
            return;
        }
        if (pillarData.isEmpty() && lastFileSize > 0L && !allowEmptySave) {
            return;
        }

        if (dirty) {
            File mainFile = getDataFile();
            if (!saveToFile(mainFile, FILE_NAME)) {
                return;
            }
            if (mainFile.exists()) {
                lastLoadedTime = mainFile.lastModified();
                lastFileSize = mainFile.length();
            }
            dirty = false;
            allowEmptySave = false;
            fileWasDeleted = false;
            saveRequested = false;
            syncDirty = true;
        }
        if (includeBackup) {
            saveToFile(getBackupDataFile(), BACKUP_FILE_NAME);
        }
    }

    public void checkAndReload() {
        if (!serverAuthoritative) {
            return;
        }
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

    private synchronized boolean saveToFile(File file, String tempFileName) {
        if (corruptedFileUnpreserved || !hasLoaded) return false;
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
                Map<String, PillarData> savedData = new HashMap<>(removedFrames);
                savedData.putAll(pillarData);
                GSON.toJson(savedData, writer);
                writer.flush();
                osw.flush();
                fos.flush();

                try {
                    FileChannel channel = fos.getChannel();
                    channel.force(true);
                } catch (Exception forceEx) {
                }
            }

            AtomicFiles.moveReplacing(tempFile.toPath(), file.toPath());
            return true;
        } catch (Exception e) {
            BuildScape.LOGGER.error("Error saving pillar data to {}", file.getAbsolutePath(), e);
            return false;
        }
    }

    public void forceReload() {
        load();
    }

    public void syncAllLoadedPillars(
            MinecraftServer server
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

            for (ServerLevel level : server.getAllLevels()) {
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

                        ChunkAccess chunk = level.getChunk(
                                pos
                        );
                        if (
                                !(chunk instanceof LevelChunk)
                        ) {
                            skippedCount++;
                            continue;
                        }

                        if (
                                !chunk
                                        .getStatus()
                                        .isOrAfter(ChunkStatus.FULL)
                        ) {
                            skippedCount++;
                            continue;
                        }

                        BlockEntity be =
                                level.getBlockEntity(pos);
                        if (
                                be instanceof PillarBlockEntity pillarBE
                        ) {

                            pillarBE.syncFromData(data);
                            syncedCount++;
                        }
                    } catch (Exception e) {
                        BuildScape.LOGGER.error("Error syncing pillar {}: {}", (data != null ? data.id : "unknown"), e.getMessage());
                    }
                }
            }

        } catch (Exception e) {
            BuildScape.LOGGER.error("Error in syncAllLoadedPillars", e);
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
            BuildScape.LOGGER.error("Error saving backup file: {}", t.getMessage());
        }
    }

    public void cleanupOrphans(Level level) {
        if (!serverAuthoritative || !hasLoaded || !(level instanceof ServerLevel serverLevel)) {
            return;
        }
        String dimension = getDimensionKey(level);
        Map<ChunkPos, List<PillarData>> chunks = new HashMap<>();
        for (PillarData data : pillarData.values()) {
            if (!isItemFrameData(data) || !Objects.equals(dimension, data.dimension)) continue;
            BlockPos pos = data.getBlockPos();
            if (!level.hasChunkAt(pos) || !serverLevel.areEntitiesLoaded(ChunkPos.asLong(pos))) continue;
            chunks.computeIfAbsent(new ChunkPos(pos), key -> new ArrayList<>()).add(data);
        }
        boolean changed = false;
        for (Map.Entry<ChunkPos, List<PillarData>> entry : chunks.entrySet()) {
            ChunkPos chunk = entry.getKey();
            Set<String> livePositions = new HashSet<>();
            AABB bounds = new AABB(
                    chunk.getMinBlockX() - 1, level.getMinBuildHeight() - 1, chunk.getMinBlockZ() - 1,
                    chunk.getMinBlockX() + 17, level.getMaxBuildHeight() + 1, chunk.getMinBlockZ() + 17);
            for (Entity entity : level.getEntitiesOfClass(Entity.class, bounds)) {
                Direction facing;
                if (entity instanceof ItemFrame frame) {
                    facing = frame.getDirection();
                } else if (entity instanceof ColoredItemFrameEntity frame) {
                    facing = frame.getDirection();
                } else {
                    continue;
                }
                livePositions.add(positionKey(dimension, entity.blockPosition(), facing));
                livePositions.add(positionKey(dimension, entity.blockPosition(), null));
            }
            for (PillarData data : entry.getValue()) {
                Direction facing = data.facing != null
                        ? Direction.byName(data.facing) : null;
                if (!livePositions.contains(positionKey(dimension, data.getBlockPos(), facing))) {
                    changed |= pillarData.remove(data.id, data);
                }
            }
        }
        if (changed) {
            rebuildPositionIndex();
            if (pillarData.isEmpty()) allowEmptySave = true;
            requestSave();
        }
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

            for (ServerLevel level : server.getAllLevels()) {
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

                        ChunkAccess chunk = level.getChunk(pos);
                        if (!(chunk instanceof LevelChunk)) {
                            preservedCount++;
                            continue;
                        }

                        if (!chunk.getStatus().isOrAfter(ChunkStatus.FULL)) {
                            preservedCount++;
                            continue;
                        }

                        BlockEntity be = level.getBlockEntity(pos);
                        if (!(be instanceof PillarBlockEntity pillarBE)) {
                            preservedCount++;
                            continue;
                        }

                        BlockPos bottomPos = pillarBE.findStackBottom();
                        BlockEntity bottomBE = level.getBlockEntity(bottomPos);

                        if (!(bottomBE instanceof PillarBlockEntity bottomPillarBE)) {
                            preservedCount++;
                            continue;
                        }

                        List<String> nbtColors = bottomPillarBE.getParticleColors();

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
                        BuildScape.LOGGER.error("Error syncing colors from NBT for pillar {}: {}",
                                (data != null ? data.id : "unknown"),
                                e.getMessage());
                    }
                }
            }

            if (syncedCount > 0 || patternSyncedCount > 0) {
                requestSave();
            } else if (preservedCount > 0) {
            }
        } catch (Exception e) {
            BuildScape.LOGGER.error("Error in syncColorsFromNBTToManager", e);
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

            for (ServerLevel level : server.getAllLevels()) {
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

                        ChunkAccess chunk = level.getChunk(pos);
                        if (!(chunk instanceof LevelChunk)) {
                            continue;
                        }

                        if (!chunk.getStatus().isOrAfter(ChunkStatus.FULL)) {
                            continue;
                        }

                        BlockEntity be = level.getBlockEntity(pos);
                        if (!(be instanceof PillarBlockEntity pillarBE)) {
                            continue;
                        }

                        BlockPos bottomPos = pillarBE.findStackBottom();
                        BlockEntity bottomBE = level.getBlockEntity(bottomPos);

                        if (!(bottomBE instanceof PillarBlockEntity bottomPillarBE)) {
                            continue;
                        }

                        List<String> nbtColors = bottomPillarBE.getParticleColors();

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
                        BuildScape.LOGGER.error("Error loading colors from NBT for pillar {}: {}",
                                (data != null ? data.id : "unknown"),
                                e.getMessage());
                    }
                }
            }

            if (loadedCount > 0) {
                requestSave();
            } else {
            }
        } catch (Exception e) {
            BuildScape.LOGGER.error("Error in loadColorsFromNBT", e);
        }
    }

    public boolean syncPatternSettingsFromNBT(
            PillarBlockEntity pillarBE,
            PillarData data
    ) {
        if (pillarBE == null || data == null) {
            return false;
        }

        boolean needsSave = false;

        String pillarType = ForgeRegistries.ITEMS.getKey(pillarBE.getBlockState().getBlock().asItem()).toString();
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

        Integer nbtMaxColor = pillarBE.getMaxParticleColor();
        if (nbtMaxColor == null && data.max_particle_color == null) {
            List<String> nbtColors = pillarBE.getParticleColors();
            if (nbtColors != null && !nbtColors.isEmpty()) nbtMaxColor = nbtColors.size();
        }
        if (nbtMaxColor != null && !nbtMaxColor.equals(data.max_particle_color)) {
            data.max_particle_color = nbtMaxColor;
            needsSave = true;
        }

        BlockPos topPos = pillarBE.findStackTop();
        BlockEntity topBE = pillarBE.getLevel().getBlockEntity(topPos);
        ItemStack displayedItem = ItemStack.EMPTY;

        if (topBE instanceof PillarBlockEntity topPillar) {
            displayedItem = topPillar.getDisplayedItem();
        } else {
            displayedItem = pillarBE.getDisplayedItem();
        }

        if (displayedItem != null && !displayedItem.isEmpty()) {
            ResourceLocation key = ForgeRegistries.ITEMS.getKey(displayedItem.getItem());
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
                copy.facing = original.facing;
                copy.itemYaw = original.itemYaw;
                copy.frameEditPending = original.frameEditPending;
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

    public synchronized String editPillarRows(List<EditPillarRowsPacket.Row> rows) {
        if (!serverAuthoritative || rows == null
                || rows.size() > EditPillarRowsPacket.MAX_ROWS) {
            return "invalid";
        }
        Map<String, PillarData> originals = copyDataSnapshot();
        Map<String, PillarData> finalData = new HashMap<>(originals);
        Set<String> oldIds = new HashSet<>();
        for (EditPillarRowsPacket.Row row : rows) {
            if (row == null || !row.isValid() || !oldIds.add(row.oldId)) return "invalid";
            if (!pillarData.containsKey(row.oldId)) return "missing";
            finalData.remove(row.oldId);
        }
        for (EditPillarRowsPacket.Row row : rows) {
            PillarData data = originals.get(row.oldId);
            if (removedFrames.containsKey(row.newId)) return editConflict("id_conflict", row);
            data.id = row.newId;
            data.dimension = row.dimension;
            data.x = row.x;
            data.y = row.y;
            data.z = row.z;
            data.modifiedTime = System.currentTimeMillis();
            if (finalData.putIfAbsent(row.newId, data) != null) return editConflict("id_conflict", row);
        }
        Map<String, PillarData> positions = new HashMap<>();
        for (PillarData data : finalData.values()) {
            Direction facing = data.facing != null
                    ? Direction.byName(data.facing) : null;
            PillarData conflict = positions.putIfAbsent(positionKey(data.dimension, data.getBlockPos(), facing), data);
            if (conflict != null) {
                for (EditPillarRowsPacket.Row row : rows) {
                    if (row.newId.equals(data.id) || row.newId.equals(conflict.id)) return editConflict("position_conflict", row);
                }
                return "position_conflict";
            }
        }
        if (rows.isEmpty()) return "";

        Map<PillarBlockEntity, String> loadedUpdates = new HashMap<>();
        for (LevelChunk chunk : loadedChunks) {
            Level level = chunk.getLevel();
            Map<String, String> renamedIds = new HashMap<>();
            for (EditPillarRowsPacket.Row row : rows) {
                PillarData previous = pillarData.get(row.oldId);
                if (Objects.equals(previous.dimension, getDimensionKey(level)) && previous.facing == null) {
                    renamedIds.put(row.oldId, row.newId);
                }
            }
            if (renamedIds.isEmpty()) continue;
            for (BlockEntity be : chunk.getBlockEntities().values()) {
                if (be instanceof PillarBlockEntity pillar && !pillar.isRemoved()) {
                    String renamedId = renamedIds.get(pillar.getPillarId());
                    if (renamedId != null) loadedUpdates.put(pillar, renamedId);
                }
            }
        }
        Map<String, Map<String, String>> frameRenames = new HashMap<>();
        for (EditPillarRowsPacket.Row row : rows) {
            PillarData previous = pillarData.get(row.oldId);
            if (previous.facing != null) {
                frameRenames.computeIfAbsent(previous.dimension, key -> new HashMap<>()).put(row.oldId, row.newId);
            }
        }
        pillarData.clear();
        pillarData.putAll(finalData);
        rebuildPositionIndex();
        loadedUpdates.forEach(PillarBlockEntity::setPillarId);
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            for (ServerLevel level : server.getAllLevels()) {
                String dimension = getDimensionKey(level);
                Map<String, String> renamedFrames = frameRenames.get(dimension);
                if (renamedFrames == null) continue;
                for (Entity entity : level.getAllEntities()) {
                    if (entity instanceof ItemFrame
                            || entity instanceof ColoredItemFrameEntity) {
                        CompoundTag tag = entity.getPersistentData();
                        String renamedId = renamedFrames.get(tag.getString(NbtKeys.BUILD_SCAPE_FRAME_ID));
                        if (renamedId != null) tag.putString(NbtKeys.BUILD_SCAPE_FRAME_ID, renamedId);
                    }
                }
            }
        }
        // Persist an explicit editor apply now; keep a deferred retry if the immediate save is refused.
        requestSave();
        saveImmediate();
        refreshSyncSnapshot();
        return "";
    }

    private static String editConflict(String reason, EditPillarRowsPacket.Row row) {
        return reason + ":" + row.oldId + ":" + row.newId;
    }

    public synchronized void trackLoadedChunk(LevelChunk chunk, boolean loaded) {
        if (loaded) loadedChunks.add(chunk);
        else loadedChunks.remove(chunk);
    }

    public synchronized void replaceFromServerSync(Collection<PillarData> syncedData) {
        if (serverAuthoritative) {
            throw new IllegalStateException("Cannot replace authoritative pillar data from a client sync");
        }
        pillarData.clear();
        if (syncedData != null) {
            for (PillarData data : syncedData) {
                if (data != null && data.id != null) {
                    pillarData.put(data.id, data);
                }
            }
        }
        rebuildPositionIndex();
        ItemFrameParticleHandler.clearClientCaches();
        hasLoaded = true;
        isServerSynced = true;
    }

    private void rebuildPositionIndex() {
        deduplicateFramePositions();
        positionIndex.clear();
        for (PillarData data : pillarData.values()) {
            if (data == null || data.id == null || data.dimension == null) {
                continue;
            }
            try {
                indexPosition(data);
            } catch (Exception ignored) {
            }
        }
    }

    private void indexPosition(PillarData data) {
        Direction facing = data.facing != null
                ? Direction.byName(data.facing)
                : null;
        String key = positionKey(data.dimension, data.getBlockPos(), facing);
        String previousId = positionIndex.get(key);
        PillarData previous = previousId != null ? pillarData.get(previousId) : null;
        if (previous == null || data.modifiedTime > previous.modifiedTime
                || (data.modifiedTime == previous.modifiedTime && data.id.compareTo(previous.id) < 0)) {
            positionIndex.put(key, data.id);
        }
    }

    private static boolean isItemFrameData(PillarData data) {
        return data.pillarType != null && (data.pillarType.endsWith(":item_frame")
                || data.pillarType.endsWith("_item_frame"));
    }

    private static boolean hasFrameParticleData(String pattern, List<String> colors) {
        return (colors != null && !colors.isEmpty()) || (pattern != null && !pattern.isEmpty()
                && !ParticlePatterns.NONE.equals(pattern) && !ParticlePatterns.NOT_SET.equals(pattern));
    }

    private void deduplicateFramePositions() {
        Map<String, PillarData> frames = new HashMap<>();
        for (PillarData data : new ArrayList<>(pillarData.values())) {
            if (!isItemFrameData(data)) continue;
            Direction facing = data.facing != null
                    ? Direction.byName(data.facing) : null;
            String key = positionKey(data.dimension, data.getBlockPos(), facing);
            PillarData previous = frames.get(key);
            if (previous == null) {
                frames.put(key, data);
            } else if (data.modifiedTime > previous.modifiedTime
                    || (data.modifiedTime == previous.modifiedTime && data.id.compareTo(previous.id) < 0)) {
                pillarData.remove(previous.id, previous);
                frames.put(key, data);
            } else {
                pillarData.remove(data.id, data);
            }
        }
    }

    private boolean pruneRegistry() {
        int previousSize = pillarData.size();
        // Dimensions are never a pruning reason: mods register some dimensions dynamically after startup.
        pillarData.values().removeIf(data -> isItemFrameData(data) && data.frameEditPending == null
                && !hasFrameParticleData(data.pattern, data.dyeColors));
        rebuildPositionIndex();
        return previousSize != pillarData.size();
    }

    public String getFrameIdForPosition(String dimension, BlockPos pos, Direction facing) {
        return facing == null ? null : positionIndex.get(positionKey(dimension, pos, facing));
    }

    private boolean removeOtherFrameAtPosition(String key, String id) {
        String previousId = positionIndex.get(key);
        if (previousId == null || previousId.equals(id)) return false;
        PillarData previous = pillarData.get(previousId);
        if (previous == null || !isItemFrameData(previous)) return false;
        pillarData.remove(previousId, previous);
        positionIndex.remove(key, previousId);
        return true;
    }

    public void clearForServerSync() {
        pillarData.clear();
        positionIndex.clear();
        ItemFrameParticleHandler.clearClientCaches();
        hasLoaded = false;
    }

    public void addPillarDataFromSync(PillarData data) {
        if (data == null || data.id == null) {
            return;
        }
        pillarData.put(data.id, data);
        if (data.dimension != null) {
            try {
                Direction facing = null;
                if (data.facing != null) {
                    facing = Direction.byName(data.facing);
                }
                positionIndex.put(positionKey(data.dimension, data.getBlockPos(), facing), data.id);
            } catch (Exception ignored) {
            }
        }
    }

    public void registerPillar(BlockEntity be) {
        if (be == null || be.getLevel() == null || be.getLevel().isClientSide) {
            return;
        }

        if (!(be instanceof PillarBlockEntity pillar)) {
            return;
        }

        if (pillar.getLevel() instanceof ServerLevel level) {
            LevelChunk chunk = level.getChunkSource().getChunkNow(
                    pillar.getBlockPos().getX() >> 4, pillar.getBlockPos().getZ() >> 4);
            if (chunk != null) trackLoadedChunk(chunk, true);
        }
        if (!hasLoaded || !BuildScape.isServerFullyInitialized()) return;
        String dimension = getDimensionKey(pillar.getLevel());
        String id = pillar.getPillarId();
        if (id == null || id.isEmpty()) {
            return;
        }
        String authoritativeId = reconcilePillarId(id, dimension, pillar.getBlockPos(), pillar.findStackBottom());
        if (!Objects.equals(authoritativeId, id)) {
            pillar.setPillarId(authoritativeId);
            return;
        }

        PillarData existing = pillarData.get(id);
        PillarData data = registerPillarData(id, dimension, pillar.getBlockPos());
        if (existing == null) {
            syncPatternSettingsFromNBT(pillar, data);

            List<String> colors = pillar.getParticleColors();
            if (colors != null && !colors.isEmpty()) {
                for (String c : colors) data.addColor(c);
            }

            requestSave();
        } else {
            if (syncPatternSettingsFromNBT(pillar, existing)) {
                requestSave();
            }
        }
    }

    private PillarData registerPillarData(String id, String dimension, BlockPos pos) {
        PillarData data = pillarData.get(id);
        if (data == null) {
            data = new PillarData(id, dimension, pos);
            pillarData.put(id, data);
        }
        indexPosition(data);
        return data;
    }

    private String reconcilePillarId(String savedId, String dimension, BlockPos basePos) {
        return reconcilePillarId(savedId, dimension, basePos, basePos);
    }

    private String reconcilePillarId(String savedId, String dimension, BlockPos pos, BlockPos basePos) {
        if (savedId == null || savedId.isEmpty()) return savedId;
        PillarData saved = pillarData.get(savedId);
        if (saved != null && Objects.equals(saved.dimension, dimension)
                && saved.x == pos.getX() && saved.z == pos.getZ()
                && saved.y >= basePos.getY() && saved.y <= pos.getY()) {
            return savedId;
        }
        for (BlockPos current = pos; current.getY() >= basePos.getY(); current = current.below()) {
            String authoritativeId = positionIndex.get(positionKey(dimension, current));
            if (authoritativeId != null) return authoritativeId;
        }
        return savedId;
    }

    private String reconcileFrameId(String savedId, String dimension, BlockPos pos, Direction facing) {
        String authoritativeId = getFrameIdForPosition(dimension, pos, facing);
        return authoritativeId != null ? authoritativeId : savedId;
    }

    private void saveFrameChange() {
        requestSave();
    }

    private static void clearFrameData(Entity frame) {
        clearFrameData(frame.getPersistentData());
        clearColoredFrameData(frame);
    }

    private static void clearFrameData(CompoundTag tag) {
        tag.remove(NbtKeys.BUILD_SCAPE_FRAME_ID);
        tag.remove(NbtKeys.BUILD_SCAPE_PARTICLE_PATTERN);
        tag.remove(NbtKeys.BUILD_SCAPE_PARTICLE_COLORS);
        tag.remove("BuildScapeParticleEnabled");
        tag.remove(NbtKeys.PILLAR_PATTERN);
        tag.remove(NbtKeys.PILLAR_COLORS);
    }

    private static void clearColoredFrameData(Entity frame) {
        if (frame instanceof ColoredItemFrameEntity coloredFrame) {
            coloredFrame.setParticlePattern(ParticlePatterns.NONE);
            coloredFrame.setParticleColorsRaw("");
        }
    }

    private void resetLoadedFrame(PillarData data) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        for (ServerLevel level : server.getAllLevels()) {
            if (!Objects.equals(getDimensionKey(level), data.dimension) || !level.hasChunkAt(data.getBlockPos())) continue;
            for (Entity entity : level.getEntities(null,
                    new AABB(data.getBlockPos()).inflate(1.0))) {
                if (!(entity instanceof ItemFrame)
                        && !(entity instanceof ColoredItemFrameEntity)) continue;
                Direction facing = entity instanceof ItemFrame itemFrame
                        ? itemFrame.getDirection()
                        : ((ColoredItemFrameEntity) entity).getDirection();
                if (isRemovedFrame(entity.getPersistentData().getString(NbtKeys.BUILD_SCAPE_FRAME_ID),
                        data.dimension, entity.blockPosition(), facing)) clearFrameData(entity);
            }
        }
    }

    /**
     * Writes a frame edited in the detail editor to the entity's data. A frame keeps its pattern and colours
     * in the entity and registers them again when it loads, so the entity must carry the edit. Frames that are
     * not loaded are marked pending and take the registry values when they register.
     */
    public void applyFrameEdit(PillarData data) {
        if (!serverAuthoritative || data == null || !isItemFrameData(data)) return;
        data.frameEditPending = writeLoadedFrame(data) ? null : Boolean.TRUE;
    }

    private boolean writeLoadedFrame(PillarData data) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return false;
        boolean written = false;
        for (ServerLevel level : server.getAllLevels()) {
            if (!Objects.equals(getDimensionKey(level), data.dimension) || !level.hasChunkAt(data.getBlockPos())) continue;
            for (Entity entity : level.getEntities(null,
                    new AABB(data.getBlockPos()).inflate(1.0))) {
                if (!(entity instanceof ItemFrame)
                        && !(entity instanceof ColoredItemFrameEntity)) continue;
                Direction facing = entity instanceof ItemFrame itemFrame
                        ? itemFrame.getDirection()
                        : ((ColoredItemFrameEntity) entity).getDirection();
                if (!isSameFrameLocation(data, data.dimension, entity.blockPosition(), facing)) continue;
                writeFrameData(entity.getPersistentData(), data.pattern, data.dyeColors);
                if (entity instanceof ColoredItemFrameEntity coloredFrame) {
                    coloredFrame.setParticlePattern(framePattern(data.pattern));
                    coloredFrame.setParticleColorsRaw(String.join(";", data.dyeColors));
                }
                written = true;
            }
        }
        return written;
    }

    private static String framePattern(String pattern) {
        return pattern == null || pattern.isEmpty() ? ParticlePatterns.NONE : pattern;
    }

    /** Puts a frame's pattern and colours into its entity data, replacing the previous values. */
    static void writeFrameData(CompoundTag tag, String pattern, List<String> colors) {
        ListTag colorList = new ListTag();
        if (colors != null) {
            for (String color : colors) colorList.add(StringTag.valueOf(color));
        }
        tag.putString(NbtKeys.BUILD_SCAPE_PARTICLE_PATTERN, framePattern(pattern));
        tag.put(NbtKeys.BUILD_SCAPE_PARTICLE_COLORS, colorList);
        if (tag.contains(NbtKeys.PILLAR_PATTERN)) tag.putString(NbtKeys.PILLAR_PATTERN, framePattern(pattern));
        if (tag.contains(NbtKeys.PILLAR_COLORS)) tag.put(NbtKeys.PILLAR_COLORS, colorList.copy());
    }

    /**
     * Settles an edit made while the frame was unloaded. When the frame registers by itself the registry
     * wins and its values are written to the entity; when a player changed the frame (immediate), the
     * entity is newer, so the pending edit is dropped. Returns true when the entity data was rewritten.
     */
    private boolean applyPendingFrameEdit(PillarData existing, CompoundTag tag, boolean immediate) {
        if (existing == null || existing.frameEditPending == null) return false;
        existing.frameEditPending = null;
        saveFrameChange();
        if (immediate) return false;
        writeFrameData(tag, existing.pattern, existing.dyeColors);
        return true;
    }

    private boolean isRemovedFrame(String id, String dimension, BlockPos pos, Direction facing) {
        boolean hasId = id != null && !id.isEmpty();
        if (hasId && removedFrames.containsKey(id)) return true;
        if (getFrameIdForPosition(dimension, pos, facing) != null) return false;
        for (PillarData data : removedFrames.values()) {
            if ((!hasId || id.equals(data.id)) && isSameFrameLocation(data, dimension, pos, facing)) return true;
        }
        return false;
    }

    private boolean prepareFrameRegistration(Entity frame, boolean immediate) {
        Direction facing = frame instanceof ItemFrame itemFrame
                ? itemFrame.getDirection()
                : ((ColoredItemFrameEntity) frame).getDirection();
        if (immediate) {
            if (isRemovedFrame(frame.getPersistentData().getString(NbtKeys.BUILD_SCAPE_FRAME_ID),
                    getDimensionKey(frame.level), frame.blockPosition(), facing)) {
                ItemFrameParticleHandler.reassignFrameId(frame);
            }
            return true;
        }
        if (prepareFrameRegistration(frame.getPersistentData(), getDimensionKey(frame.level),
                frame.blockPosition(), facing)) return true;
        clearColoredFrameData(frame);
        return false;
    }

    private boolean prepareFrameRegistration(CompoundTag tag, String dimension, BlockPos pos,
                                             Direction facing) {
        if (!isRemovedFrame(tag.getString(NbtKeys.BUILD_SCAPE_FRAME_ID), dimension, pos, facing)) return true;
        clearFrameData(tag);
        return false;
    }

    public void registerItemFrame(ItemFrame frame) {
        registerItemFrame(frame, false);
    }

    public void registerItemFrame(ItemFrame frame, boolean immediate) {
        if (frame == null || frame.level == null || frame.level.isClientSide) {
            return;
        }

        if (!prepareFrameRegistration(frame, immediate)) return;
        CompoundTag dataTag = frame.getPersistentData();
        String id = dataTag.getString(NbtKeys.BUILD_SCAPE_FRAME_ID);

        String pattern = ItemFrameParticleHandler.getParticlePattern(frame);
        List<String> colors = new ArrayList<>();
        if (dataTag.contains(NbtKeys.BUILD_SCAPE_PARTICLE_COLORS)) {
            ListTag colorList = dataTag.getList(NbtKeys.BUILD_SCAPE_PARTICLE_COLORS, 8);
            for (int i = 0; i < colorList.size(); i++) {
                colors.add(colorList.getString(i));
            }
        }

        if (id == null || id.isEmpty()) {
            if (!hasFrameParticleData(pattern, colors)) return;
            id = ItemFrameParticleHandler.getFrameId(frame);
        }

        String dimension = getDimensionKey(frame.level);
        BlockPos pos = frame.blockPosition();
        Direction facing = frame.getDirection();
        String posKey = positionKey(dimension, pos, facing);

        id = reconcileFrameId(id, dimension, pos, facing);
        dataTag.putString(NbtKeys.BUILD_SCAPE_FRAME_ID, id);
        PillarData existing = pillarData.get(id);
        if (existing == null && !hasFrameParticleData(pattern, colors)) return;
        if (existing != null && !isSameFrameLocation(existing, dimension, pos, facing)) {
            id = ItemFrameParticleHandler.reassignFrameId(frame);
            existing = null;
        }
        if (applyPendingFrameEdit(existing, dataTag, immediate)) {
            pattern = framePattern(existing.pattern);
            colors = new ArrayList<>(existing.dyeColors);
        }
        boolean removedDuplicate = removeOtherFrameAtPosition(posKey, id);
        if (existing == null) {
            PillarData data = new PillarData(id, dimension, pos);
            data.pattern = pattern;
            data.dyeColors = colors;

            data.pillarType = "minecraft:item_frame";
            data.facing = facing != null ? facing.getSerializedName() : null;

            if (!frame.getItem().isEmpty()) {
                ResourceLocation itemRL = ForgeRegistries.ITEMS.getKey(frame.getItem().getItem());
                if (itemRL != null) data.displayedItem = itemRL.toString();
            }

            pillarData.put(id, data);
            positionIndex.put(posKey, id);

            saveFrameChange();
        } else {
            positionIndex.put(posKey, id);
            boolean changed = removedDuplicate;

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
                ResourceLocation itemKey = ForgeRegistries.ITEMS.getKey(frame.getItem().getItem());
                String itemId = itemKey != null ? itemKey.toString() : null;
                if (!Objects.equals(existing.displayedItem, itemId)) {
                    existing.displayedItem = itemId;
                    changed = true;
                }
            } else if (existing.displayedItem != null) {
                existing.displayedItem = null;
                changed = true;
            }

            if (changed) {
                existing.modifiedTime = System.currentTimeMillis();
                saveFrameChange();
            }
        }
    }


    public void registerColoredItemFrame(ColoredItemFrameEntity frame) {
        registerColoredItemFrame(frame, false);
    }

    public void registerColoredItemFrame(ColoredItemFrameEntity frame, boolean immediate) {
        if (frame == null || frame.level == null || frame.level.isClientSide) {
            return;
        }

        if (!prepareFrameRegistration(frame, immediate)) return;
        CompoundTag dataTag = frame.getPersistentData();
        String id = dataTag.getString(NbtKeys.BUILD_SCAPE_FRAME_ID);

        String pattern = ItemFrameParticleHandler.getParticlePatternColored(frame);
        List<String> colors = new ArrayList<>();
        if (dataTag.contains(NbtKeys.BUILD_SCAPE_PARTICLE_COLORS)) {
            ListTag colorList = dataTag.getList(NbtKeys.BUILD_SCAPE_PARTICLE_COLORS, 8);
            for (int i = 0; i < colorList.size(); i++) {
                colors.add(colorList.getString(i));
            }
        }

        if (id == null || id.isEmpty()) {
            if (!hasFrameParticleData(pattern, colors)) return;
            id = ItemFrameParticleHandler.getFrameIdColored(frame);
        }

        String dimension = getDimensionKey(frame.level);
        BlockPos pos = frame.blockPosition();
        Direction facing = frame.getDirection();
        String posKey = positionKey(dimension, pos, facing);

        id = reconcileFrameId(id, dimension, pos, facing);
        dataTag.putString(NbtKeys.BUILD_SCAPE_FRAME_ID, id);
        PillarData existing = pillarData.get(id);
        if (existing == null && !hasFrameParticleData(pattern, colors)) return;
        if (existing != null && !isSameFrameLocation(existing, dimension, pos, facing)) {
            id = ItemFrameParticleHandler.reassignFrameId(frame);
            existing = null;
        }
        if (applyPendingFrameEdit(existing, dataTag, immediate)) {
            pattern = framePattern(existing.pattern);
            colors = new ArrayList<>(existing.dyeColors);
            frame.setParticlePattern(pattern);
            frame.setParticleColorsRaw(String.join(";", colors));
        }
        boolean removedDuplicate = removeOtherFrameAtPosition(posKey, id);
        if (existing == null) {
            PillarData data = new PillarData(id, dimension, pos);
            data.pattern = pattern;
            data.dyeColors = colors;

            String color = frame.getColorVariant();
            if (color == null || color.isEmpty()) color = "white";
            else color = color.toLowerCase(Locale.ROOT);

            ResourceLocation typeRL = new ResourceLocation("buildscape", color + "_item_frame");
            data.pillarType = typeRL.toString();
            data.facing = facing != null ? facing.getSerializedName() : null;

            if (!frame.getItem().isEmpty()) {
                ResourceLocation itemKey = ForgeRegistries.ITEMS.getKey(frame.getItem().getItem());
                if (itemKey != null) {
                    data.displayedItem = itemKey.toString();
                }
            }

            pillarData.put(id, data);
            positionIndex.put(posKey, id);

            saveFrameChange();
        } else {
            positionIndex.put(posKey, id);
            boolean changed = removedDuplicate;

            String color = frame.getColorVariant();
            if (color == null || color.isEmpty()) color = "white";
            else color = color.toLowerCase(Locale.ROOT);
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
                ResourceLocation itemKey = ForgeRegistries.ITEMS.getKey(frame.getItem().getItem());
                String itemId = itemKey != null ? itemKey.toString() : null;
                if (!Objects.equals(existing.displayedItem, itemId)) {
                    existing.displayedItem = itemId;
                    changed = true;
                }
            } else if (existing.displayedItem != null) {
                existing.displayedItem = null;
                changed = true;
            }

            if (changed) {
                existing.modifiedTime = System.currentTimeMillis();
                saveFrameChange();
            }
        }
    }

    public void markAsLoaded() {
        hasLoaded = true;
        isServerSynced = true;
    }

    public List<PillarData> getAllPillarDataForSync() {
        return serverAuthoritative ? syncSnapshot : new ArrayList<>(pillarData.values());
    }

    private void refreshSyncSnapshot() {
        syncSnapshot = Collections.unmodifiableList(new ArrayList<>(copyDataSnapshot().values()));
    }

    public static class PillarData {

        public String id;
        public Boolean removed = null;
        /** Set on a frame whose pattern or colours were edited while its entity was not loaded. */
        public Boolean frameEditPending = null;
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
            dyeColors.add(colorCode.toUpperCase(Locale.ROOT));
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
