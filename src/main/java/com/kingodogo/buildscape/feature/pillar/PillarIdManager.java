package com.kingodogo.buildscape.feature.pillar;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.core.NbtKeys;
import com.kingodogo.buildscape.core.ParticlePatterns;
import com.kingodogo.buildscape.entity.ColoredItemFrameEntity;
import com.kingodogo.buildscape.network.EditPillarRowsPacket;
import com.kingodogo.buildscape.network.SyncPillarIdsPacket;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.server.ServerLifecycleHooks;

/**
 * Registry of every pillar and item frame that has a pillar id. There is one server instance ({@link #get()}) and one
 * client copy ({@link #getClient()}) fed by {@code SyncPillarIdsPacket}. Each {@link PillarData} record stores
 * the id, dimension, position, displayed item and particle overrides. The server saves the registry as JSON
 * in the world folder ({@code buildscape/pillar-ids.dat} with a backup file); a damaged file is preserved and the
 * registry is rebuilt from the loaded block entities. Ids start with a variant prefix (M, S, D, Q, or I-F for item frames).
 */
public class PillarIdManager {
    private static final PillarIdManager SERVER_INSTANCE = new PillarIdManager(true);
    private static final PillarIdManager CLIENT_INSTANCE = new PillarIdManager(false);

    final boolean serverAuthoritative;
    final PillarRegistry registry = new PillarRegistry();
    private final Map<String, PillarData> removedFrames = registry.removedFrames;
    private final Map<String, String> positionIndex = registry.positionIndex;

    /** Shape an id must have: a capital letter, then letters, digits or dashes, 2 to 32 characters in all. */
    public static final String VALID_ID_PATTERN = "^[A-Z][A-Za-z0-9-]{1,31}$";
    private final Set<LevelChunk> loadedChunks = Collections.newSetFromMap(new WeakHashMap<>());
    private volatile List<PillarData> syncSnapshot = Collections.emptyList();

    private long lastLoadedTime = 0L;
    private long lastFileSize = 0L;

    private volatile boolean hasLoaded = false;
    private volatile boolean loadInProgress = false;
    private boolean hadColorsOnLoad = false;
    private boolean isServerSynced = false;
    boolean allowEmptySave = false;
    private volatile boolean dirty = false;
    private volatile boolean syncDirty = false;

    public static final String PREFIX_MOSSY = "M";
    public static final String PREFIX_STONE = "S";
    public static final String PREFIX_DEEPSLATE = "D";
    public static final String PREFIX_QUARTZ = "Q";
    public static final String PREFIX_ITEM_FRAME = "I-F";

    /** Forgets the cached world folder and any scheduled recovery, and empties the server registry. */
    public static void resetWorldCache() {
        PillarStore.resetCachedWorldDir();
        PillarRecovery.reset();

        SERVER_INSTANCE.resetState(false);
    }

    /** The server registry. On a client use {@link #getClient()} or {@link #get(Level)} for the synced copy. */
    public static PillarIdManager get() {
        return SERVER_INSTANCE;
    }

    /** The client copy, filled by {@code SyncPillarIdsPacket}. */
    public static PillarIdManager getClient() {
        return CLIENT_INSTANCE;
    }

    /** The client copy for a client level, the server registry for any other level (and for null). */
    public static PillarIdManager get(Level level) {
        return level != null && level.isClientSide ? CLIENT_INSTANCE : SERVER_INSTANCE;
    }

    /** Empties the client copy; the next sync from the server fills it again. */
    public static void resetClientCache() {
        CLIENT_INSTANCE.resetState(true);
    }

    private PillarIdManager(boolean serverAuthoritative) {
        this.serverAuthoritative = serverAuthoritative;
    }

    private void resetState(boolean serverSynced) {
        isServerSynced = serverSynced;
        registry.pillarData.clear();
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

    /** True once the registry file has been read (server) or a sync from the server has arrived (client). */
    public boolean hasLoaded() {
        return hasLoaded;
    }

    /** True while the registry file is being read on the server thread. */
    public boolean isLoadInProgress() {
        return loadInProgress;
    }

    /** Server only: true when the registry file was deleted and nothing is registered, so the records have to be rebuilt from the world. */
    public boolean needsWorldRecovery() {
        return serverAuthoritative && hasLoaded && fileWasDeleted && registry.pillarData.isEmpty();
    }

    /** The id prefix ({@code PREFIX_*}) that fits the pillar variant at {@code pos}. */
    public static String getVariantPrefix(Level level, BlockPos pos) {
        return PillarRegistry.getVariantPrefix(level, pos);
    }

    /** Same reset as {@link #resetWorldCache()}. */
    public static void fullReset() {
        PillarStore.resetCachedWorldDir();
        PillarRecovery.reset();

        SERVER_INSTANCE.resetState(false);
    }

    /** A new unused id with the default {@code S-P} prefix. */
    public String generatePillarId() {
        return generatePillarId(PREFIX_STONE + "-P");
    }

    /**
     * The record of the pillar stack that contains {@code pos}, found through its lowest block. A missing record is created
     * with a new id: the id is written to the block entity, and the block entity's pattern settings and colours are
     * copied into the record. A new record asks for a save.
     */
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
            PillarData existing = registry.pillarData.get(existingId);
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

        registry.pillarData.put(id, newData);
        positionIndex.put(posKey, id);

        if (!PillarRecovery.recoveryInProgress) {
            requestSave();
        }
        return newData;
    }

    /**
     * Adds a dye colour to the pillar's record, creating the record if needed, and asks for a save. A record without a
     * pattern, or with the default one, takes the pattern from the particle config. Returns the pillar id, or null when
     * the record already holds the maximum number of colours.
     */
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

    private volatile boolean saveRequested = false;

    /**
     * Marks the registry changed and asks for a save, which {@link #flushRequestedSave()} performs. Ignored on the client copy.
     */
    public void requestSave() {
        if (!serverAuthoritative) {
            return;
        }
        dirty = true;
        saveRequested = true;
    }

    /**
     * Performs a save asked for by {@link #requestSave()}; a refused save stays requested for the next call. When the
     * registry was saved since the last broadcast, also sends it to all players.
     */
    public void flushRequestedSave() {
        if (saveRequested) {
            saveRequested = false;
            if (!saveImmediate()) {
                saveRequested = true;
            }
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

    /** Deletes one record; see {@link #removePillars(Collection)}. */
    public void removePillar(String pillarId) {
        removePillars(Collections.singleton(pillarId));
    }

    /** Deletes the record of an item frame that was destroyed. Unlike a removal, the frame is not remembered as removed. */
    public void unregisterDestroyedFrame(String frameId) {
        removePillars(Collections.singleton(frameId), false);
    }

    /** Same as {@link #removePillar(String)}. */
    public void removePillarData(String pillarId) {
        removePillar(pillarId);
    }

    /**
     * Writes the registry to disk now.
     */
    public void saveAllPillars() {
        saveImmediate();
    }

    /** Stores the record under its id, stamps its modified time and saves at once. Ignores null data or data without an id. */
    public void updatePillarData(PillarData data) {
        if (data != null && data.id != null) {
            registry.pillarData.put(data.id, data);
            data.modifiedTime = System.currentTimeMillis();
            saveImmediate();
        }
    }

    /**
     * Clears the particle overrides of the given pillars. A frame keeps its pattern and colours in the
     * entity, so a reset frame is recorded like a frame removed in the screen: loaded frames are cleared
     * now and unloaded ones are cleared when they load, instead of copying the old values back in.
     */
    public boolean resetPillars(Collection<String> pillarIds) {
        if (pillarIds == null) {
            return false;
        }
        boolean changed = false;
        Set<String> frames = new LinkedHashSet<>();
        for (String id : pillarIds) {
            PillarData existing = id == null ? null : registry.pillarData.get(id);
            if (existing == null) {
                continue;
            }
            clearPillarOverrides(existing);
            if (serverAuthoritative && PillarRegistry.isItemFrameData(existing)) {
                frames.add(id);
            }
            changed = true;
        }
        if (!frames.isEmpty()) {
            removePillars(frames, true);
        }
        return changed;
    }

    /**
     * Deletes the listed pillar records. Returns true if anything changed. On the server a removed pillar is reset
     * in the world through {@code PillarResetHandler}, and a removed item frame is remembered so it is not registered
     * again.
     */
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
            PillarData data = registry.pillarData.remove(pillarId);
            if (data == null) {
                continue;
            }
            removedIds.add(pillarId);
            if (serverAuthoritative) {
                if (PillarRegistry.isItemFrameData(data)) {
                    if (rememberRemovedFrames) {
                        data.removed = true;
                        data.frameEditPending = null;
                        data.modifiedTime = System.currentTimeMillis();
                        removedFrames.put(pillarId, data);
                        PillarFrames.resetLoadedFrame(this, data);
                    }
                } else {
                    PillarResetHandler.resetPillarFromData(data);
                }
            }
            changed = true;
        }

        if (changed) {
            registry.pruneRemovedFrames();
            positionIndex.values().removeIf(removedIds::contains);
            if (registry.pillarData.isEmpty()) {
                allowEmptySave = true;
            }
            requestSave();
        }
        return changed;
    }

    /** Sets the displayed item of a record (null or empty clears it) and asks for a save when the value changed. */
    public void updateDisplayedItem(String pillarId, String itemResourceId) {
        if (pillarId == null) {
            return;
        }
        PillarData data = registry.pillarData.get(pillarId);
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

    /** Deletes the record indexed at {@code pos}, if the record's stored dimension and position still match. */
    public void removePillarByPosition(Level level, BlockPos pos) {
        removePillarByPosition(getDimensionKey(level), pos);
    }

    private void removePillarByPosition(String dimension, BlockPos pos) {
        String posKey = positionKey(dimension, pos);

        String idToRemove = positionIndex.remove(posKey);
        if (idToRemove != null) {
            PillarData data = registry.pillarData.get(idToRemove);
            if (data != null && Objects.equals(data.dimension, dimension)
                    && data.getBlockPos().equals(pos)) {
                removePillar(idToRemove);
            }
        }
    }

    /** Drops the position-index entry at {@code pos} when its record is missing or has moved. The record itself is not touched. */
    public void removeStalePillarPositionIndex(Level level, BlockPos pos) {
        String dimension = getDimensionKey(level);
        String key = positionKey(dimension, pos);
        String id = positionIndex.get(key);
        if (id != null) {
            PillarData data = registry.pillarData.get(id);
            if (data == null || !Objects.equals(data.dimension, dimension)
                    || !data.getBlockPos().equals(pos)) {
                positionIndex.remove(key, id);
            }
        }
    }

    /** Moves a record to {@code pos}, rebuilds the position index and asks for a save. Does nothing if it is already there. */
    public void updatePillarPosition(String pillarId, BlockPos pos) {
        PillarData data = registry.pillarData.get(pillarId);
        if (data != null && !data.getBlockPos().equals(pos)) {
            data.x = pos.getX();
            data.y = pos.getY();
            data.z = pos.getZ();
            rebuildPositionIndex();
            requestSave();
        }
    }

    /**
     * Updates the registry after the block at {@code pos} was removed from a stack running from {@code bottom} to
     * {@code top}. Returns where the record now sits, or null when the stack is gone and its record was deleted.
     */
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

    /**
     * Joins pillars into one stack: the record keeps its id and moves to {@code bottom}, and the other pillar records
     * (not item frames) in that column between {@code bottom} and {@code top} are deleted.
     */
    public void mergePillarStack(String pillarId, BlockPos bottom, BlockPos top) {
        PillarData data = registry.pillarData.get(pillarId);
        if (data == null) {
            return;
        }

        boolean changed = false;
        for (PillarData other : new ArrayList<>(registry.pillarData.values())) {
            if (!other.id.equals(pillarId) && !PillarRegistry.isItemFrameData(other)
                    && Objects.equals(other.dimension, data.dimension)
                    && other.x == bottom.getX() && other.z == bottom.getZ()
                    && other.y >= bottom.getY() && other.y <= top.getY()) {
                changed |= registry.pillarData.remove(other.id, other);
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

    /** Clears the dye colours of a record and asks for a save. */
    public void clearPillarColors(String pillarId) {
        PillarData data = registry.pillarData.get(pillarId);
        if (data != null) {
            data.clearColors();
            requestSave();
        }
    }

    /** True if the record at {@code pos} has dye colours. */
    public boolean hasCustomColors(Level level, BlockPos pos) {
        PillarData data = getPillarDataByPosition(level, pos);
        return data != null && data.hasColors();
    }

    /**
     * Like {@link #updateDisplayedItem(String, String)} for the pillar at {@code pos}. The record is created if missing,
     * and the block entity's pattern settings are copied into it first.
     */
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

    /**
     * Starts loading the registry file from the world folder. Does nothing on the client copy, once loaded, or while a load
     * is running.
     */
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
                registry.pillarData.clear();
                lastLoadedTime = 0L;
                lastFileSize = 0L;
                fileWasDeleted = false;
                hasLoaded = false;
                loadInProgress = false;
                return;
            }

            File file = PillarStore.getDataFile();
            if (hasLoaded && registry.pillarData.isEmpty() && file.exists() && !dirty) {
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
                    // server-thread task; a failed load is logged and must not leave the load flag set
                    BuildScape.LOGGER.error("Error in async file load", e);
                    hasLoaded = true;
                } finally {
                    loadInProgress = false;
                }
            });
        } catch (Throwable t) {
            // top-level guard: a failed load or save must not take the server down
            BuildScape.LOGGER.error("Critical error in load() - will recover from world later", t);
            loadInProgress = false;
            hasLoaded = true;
            registry.pillarData.clear();
            lastLoadedTime = 0L;
            lastFileSize = 0L;
            fileWasDeleted = true;
        }
    }

    private boolean corruptedFileUnpreserved = false;
    private void handleCorruptedFile(File file, Throwable error, String errorType) {
        lastFileSize = Math.max(lastFileSize, file.length());
        corruptedFileUnpreserved = !PillarStore.preserveCorrupted(file, error, errorType);
    }

    private boolean fileWasDeleted = false;

    private void processLoadedData(Map<String, PillarData> loaded, MinecraftServer server, File sourceFile) {
        Map<String, PillarData> existingData = new HashMap<>(registry.pillarData);
        registry.pillarData.clear();
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
                if (Boolean.TRUE.equals(data.removed) && PillarRegistry.isItemFrameData(data)) {
                    removedFrames.put(id, data);
                } else {
                    registry.pillarData.put(id, data);
                }
            }
        }
        changed |= registry.pruneRemovedFrames();
        rebuildPositionIndex();
        for (Map.Entry<String, PillarData> entry : existingData.entrySet()) {
            if (removedFrames.containsKey(entry.getKey())) {
                continue;
            }
            PillarData data = entry.getValue();
            if (PillarRegistry.isItemFrameData(data) && isRemovedFrame(data.id, data.dimension, data.getBlockPos(),
                    data.facing == null ? null : Direction.byName(data.facing))) {
                continue;
            }
            if (registry.pillarData.putIfAbsent(entry.getKey(), entry.getValue()) == null) {
                changed = true;
            }
        }
        changed |= pruneRegistry();
        hadColorsOnLoad = registry.pillarData.values().stream().anyMatch(PillarData::hasColors);
        fileWasDeleted = false;
        if (sourceFile != null) {
            lastLoadedTime = sourceFile.lastModified();
            lastFileSize = Math.max(lastFileSize, sourceFile.length());
        }
        hasLoaded = true;
        for (PillarData removed : removedFrames.values()) {
            PillarFrames.resetLoadedFrame(this, removed);
        }
        for (PillarData edited : new ArrayList<>(registry.pillarData.values())) {
            if (edited.frameEditPending != null && PillarRegistry.isItemFrameData(edited)) {
                applyFrameEdit(edited);
                if (edited.frameEditPending == null) {
                    changed = true;
                }
            }
        }
        PillarStore.updateCachedWorldDir();
        refreshSyncSnapshot();
        if (changed) {
            if (registry.pillarData.isEmpty()) {
                allowEmptySave = true;
            }
            requestSave();
        }
    }

    private void loadFileAsync(MinecraftServer server) {
        try {
            File file = PillarStore.getDataFile();

            SavedData saved = readSavedData(file, PillarStore.getBackupDataFile());
            Map<String, PillarData> loadedData = saved.data;
            File sourceFile = saved.file;
            if (loadedData == null || loadedData.isEmpty()) {
                fileWasDeleted = true;
                registry.pillarData.clear();
                lastLoadedTime = 0L;
                lastFileSize = Math.max(lastFileSize, file.length());
                hasLoaded = true;
                scheduleRecoveryFromWorld(server, false);
                return;
            }

            processLoadedData(loadedData, server, sourceFile);

        } catch (Throwable t) {
            // top-level guard: a failed load or save must not take the server down
            BuildScape.LOGGER.error("Critical error in loadFileAsync() - will recover after world is fully loaded", t);
            fileWasDeleted = true;
            registry.pillarData.clear();
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
                Map<String, PillarData> loaded = PillarStore.loadFromFile(file);
                if (loaded != null) {
                    return new SavedData(file, loaded);
                }
            } catch (Exception e) {
                // the pillar file may be corrupt in any way; fall back to the backup
                handleCorruptedFile(file, e, "parse");
            }
        }
        if (backupFile.exists() && backupFile.length() > 0) {
            try {
                Map<String, PillarData> loaded = PillarStore.loadFromFile(backupFile);
                if (loaded != null && !loaded.isEmpty()) {
                    return new SavedData(backupFile, loaded);
                }
            } catch (Exception e) {
                // the backup may be corrupt too; start empty
                BuildScape.LOGGER.error("Unable to read pillar backup", e);
            }
        }
        return new SavedData(file, Collections.emptyMap());
    }

    /**
     * Writes the registry file now. Returns false when nothing was written: while a recovery runs, before the server is
     * fully started, when an empty registry would replace a non-empty file, or when the write fails. A write marks the
     * registry as needing a broadcast to players. Always true and a no-op on the client copy.
     */
    public synchronized boolean saveImmediate() {
        if (!serverAuthoritative) {
            return true;
        }
        dirty = true;
        try {
            if (PillarRecovery.recoveryInProgress) {
                return false;
            }

            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server == null) {
                return false;
            }

            if (!BuildScape.isServerFullyInitialized()) {
                return false;
            }

            if (registry.pillarData.isEmpty() && lastFileSize > 0 && !allowEmptySave) {
                return false;
            }
            if (!saveToFile(PillarStore.getDataFile(), PillarStore.FILE_NAME)) {
                return false;
            }
            if (allowEmptySave && !saveToFile(PillarStore.getBackupDataFile(), PillarStore.BACKUP_FILE_NAME)) {
                return false;
            }
            allowEmptySave = false;

            File mainFile = PillarStore.getDataFile();
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
            // top-level guard: a failed load or save must not take the server down
            BuildScape.LOGGER.error("Unable to save pillar data", t);
            return false;
        }
    }

    /**
     * Server only: asks for a recovery from the loaded block entities. It runs later from
     * {@link #checkAndRunScheduledRecovery()}, once players are online and the world has been loaded for a while.
     * With {@code clearColors} the run clears the pillar ids stored in the world instead of recovering them.
     */
    public void scheduleRecoveryFromWorld(
            MinecraftServer server,
            boolean clearColors) {
        if (!serverAuthoritative || server == null || !server.isRunning()) {
            return;
        }
        PillarRecovery.schedule(clearColors);
    }

    /** Same as {@link #requestSave()}. */
    public void save() {
        requestSave();
    }

    /**
     * Server only: writes the registry file if it changed, and also the backup file when {@code includeBackup} is set.
     * Skips while a recovery runs, before the first load, and when an empty registry would replace a non-empty file.
     */
    public void savePeriodic(boolean includeBackup) {
        if (!serverAuthoritative) {
            return;
        }
        if (PillarRecovery.recoveryInProgress || !hasLoaded) {
            return;
        }
        if (registry.pillarData.isEmpty() && lastFileSize > 0L && !allowEmptySave) {
            return;
        }

        if (dirty) {
            File mainFile = PillarStore.getDataFile();
            if (!saveToFile(mainFile, PillarStore.FILE_NAME)) {
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
            saveToFile(PillarStore.getBackupDataFile(), PillarStore.BACKUP_FILE_NAME);
        }
    }

    /** Server only: reads the file again when its modified time or size differs from the last read or write. */
    public void checkAndReload() {
        if (!serverAuthoritative) {
            return;
        }
        File mainFile = PillarStore.getDataFile();

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
        if (corruptedFileUnpreserved || !hasLoaded) {
            return false;
        }
        return PillarStore.write(file, tempFileName, registry.removedFrames, registry.pillarData);
    }

    /** Same as {@link #load()}. */
    public void forceReload() {
        load();
    }

    /** Writes the backup file if the registry is loaded and the server is running. */
    public void saveBackupFile() {
        try {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server == null || !server.isRunning()) {
                return;
            }

            if (!hasLoaded) {
                return;
            }

            int saveCount = registry.pillarData.size();
            int colorsCount = 0;
            for (PillarData data : registry.pillarData.values()) {
                if (data != null && data.hasColors()) {
                    colorsCount++;
                }
            }

            saveToFile(PillarStore.getBackupDataFile(), PillarStore.BACKUP_FILE_NAME);
        } catch (Throwable t) {
            // top-level guard: a failed load or save must not take the server down
            BuildScape.LOGGER.error("Error saving backup file", t);
        }
    }

    /**
     * Applies id and position edits for several records as one all-or-nothing change. Returns an empty string on success,
     * otherwise a reason: {@code invalid}, {@code missing}, or {@code id_conflict} / {@code position_conflict} (the last two
     * followed by {@code :oldId:newId} of the row at fault). On success the new ids are written to loaded pillar block
     * entities and item frames, and the registry is saved at once. No game code calls this now that the screen shows
     * ids as read-only; tests cover it.
     */
    public synchronized String editPillarRows(List<EditPillarRowsPacket.Row> rows) {
        if (!serverAuthoritative || rows == null
                || rows.size() > EditPillarRowsPacket.MAX_ROWS) {
            return "invalid";
        }
        Map<String, PillarData> originals = copyDataSnapshot();
        Map<String, PillarData> finalData = new HashMap<>(originals);
        Set<String> oldIds = new HashSet<>();
        for (EditPillarRowsPacket.Row row : rows) {
            if (row == null || !row.isValid() || !oldIds.add(row.oldId)) {
                return "invalid";
            }
            if (!registry.pillarData.containsKey(row.oldId)) {
                return "missing";
            }
            finalData.remove(row.oldId);
        }
        for (EditPillarRowsPacket.Row row : rows) {
            PillarData data = originals.get(row.oldId);
            if (removedFrames.containsKey(row.newId)) {
                return editConflict("id_conflict", row);
            }
            data.id = row.newId;
            data.dimension = row.dimension;
            data.x = row.x;
            data.y = row.y;
            data.z = row.z;
            data.modifiedTime = System.currentTimeMillis();
            if (finalData.putIfAbsent(row.newId, data) != null) {
                return editConflict("id_conflict", row);
            }
        }
        Map<String, PillarData> positions = new HashMap<>();
        for (PillarData data : finalData.values()) {
            Direction facing = data.facing != null
                    ? Direction.byName(data.facing)
                    : null;
            PillarData conflict = positions.putIfAbsent(positionKey(data.dimension, data.getBlockPos(), facing), data);
            if (conflict != null) {
                for (EditPillarRowsPacket.Row row : rows) {
                    if (row.newId.equals(data.id) || row.newId.equals(conflict.id)) {
                        return editConflict("position_conflict", row);
                    }
                }
                return "position_conflict";
            }
        }
        if (rows.isEmpty()) {
            return "";
        }

        Map<PillarBlockEntity, String> loadedUpdates = new HashMap<>();
        for (LevelChunk chunk : loadedChunks) {
            Level level = chunk.getLevel();
            Map<String, String> renamedIds = new HashMap<>();
            for (EditPillarRowsPacket.Row row : rows) {
                PillarData previous = registry.pillarData.get(row.oldId);
                if (Objects.equals(previous.dimension, getDimensionKey(level)) && previous.facing == null) {
                    renamedIds.put(row.oldId, row.newId);
                }
            }
            if (renamedIds.isEmpty()) {
                continue;
            }
            for (BlockEntity be : chunk.getBlockEntities().values()) {
                if (be instanceof PillarBlockEntity pillar && !pillar.isRemoved()) {
                    String renamedId = renamedIds.get(pillar.getPillarId());
                    if (renamedId != null) {
                        loadedUpdates.put(pillar, renamedId);
                    }
                }
            }
        }
        Map<String, Map<String, String>> frameRenames = new HashMap<>();
        for (EditPillarRowsPacket.Row row : rows) {
            PillarData previous = registry.pillarData.get(row.oldId);
            if (previous.facing != null) {
                frameRenames.computeIfAbsent(previous.dimension, key -> new HashMap<>()).put(row.oldId, row.newId);
            }
        }
        registry.pillarData.clear();
        registry.pillarData.putAll(finalData);
        rebuildPositionIndex();
        loadedUpdates.forEach(PillarBlockEntity::setPillarId);
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            for (ServerLevel level : server.getAllLevels()) {
                String dimension = getDimensionKey(level);
                Map<String, String> renamedFrames = frameRenames.get(dimension);
                if (renamedFrames == null) {
                    continue;
                }
                for (Entity entity : level.getAllEntities()) {
                    if (entity instanceof ItemFrame
                            || entity instanceof ColoredItemFrameEntity) {
                        CompoundTag tag = entity.getPersistentData();
                        String renamedId = renamedFrames.get(tag.getString(NbtKeys.BUILD_SCAPE_FRAME_ID));
                        if (renamedId != null) {
                            tag.putString(NbtKeys.BUILD_SCAPE_FRAME_ID, renamedId);
                        }
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

    /** Remembers (or forgets) a loaded chunk so {@link #editPillarRows} can reach its pillar block entities. Chunks are held weakly. */
    public synchronized void trackLoadedChunk(LevelChunk chunk, boolean loaded) {
        if (loaded) {
            loadedChunks.add(chunk);
        } else {
            loadedChunks.remove(chunk);
        }
    }

    /** Client only: replaces every record with the list from the server and marks the copy loaded. Throws on the server registry. */
    public synchronized void replaceFromServerSync(Collection<PillarData> syncedData) {
        if (serverAuthoritative) {
            throw new IllegalStateException("Cannot replace authoritative pillar data from a client sync");
        }
        registry.pillarData.clear();
        if (syncedData != null) {
            for (PillarData data : syncedData) {
                if (data != null && data.id != null) {
                    registry.pillarData.put(data.id, data);
                }
            }
        }
        rebuildPositionIndex();
        ItemFrameParticleEvents.clearClientCaches();
        hasLoaded = true;
        isServerSynced = true;
    }

    /** Client: empties the records and the position index and marks the copy as not loaded until the next sync. */
    public void clearForServerSync() {
        registry.pillarData.clear();
        positionIndex.clear();
        ItemFrameParticleEvents.clearClientCaches();
        hasLoaded = false;
    }

    /** Adds or replaces one record received from the server and indexes its position. */
    public void addPillarDataFromSync(PillarData data) {
        if (data == null || data.id == null) {
            return;
        }
        registry.pillarData.put(data.id, data);
        if (data.dimension != null) {
            try {
                Direction facing = null;
                if (data.facing != null) {
                    facing = Direction.byName(data.facing);
                }
                positionIndex.put(positionKey(data.dimension, data.getBlockPos(), facing), data.id);
            } catch (Exception e) {
                // a bad entry only loses its index slot
                BuildScape.LOGGER.debug("Skipped position index for pillar {}", data.id, e);
            }
        }
    }

    /**
     * Server only: adds or reconciles the record of a loaded pillar or frame block entity.
     */
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
            if (chunk != null) {
                trackLoadedChunk(chunk, true);
            }
        }
        if (!hasLoaded || !BuildScape.isServerFullyInitialized()) {
            return;
        }
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

        PillarData existing = registry.pillarData.get(id);
        PillarData data = registerPillarData(id, dimension, pillar.getBlockPos());
        if (existing == null) {
            syncPatternSettingsFromNBT(pillar, data);

            List<String> colors = pillar.getParticleColors();
            if (colors != null && !colors.isEmpty()) {
                for (String c : colors) {
                    data.addColor(c);
                }
            }

            requestSave();
        } else {
            if (syncPatternSettingsFromNBT(pillar, existing)) {
                requestSave();
            }
        }
    }

    private PillarData registerPillarData(String id, String dimension, BlockPos pos) {
        PillarData data = registry.pillarData.get(id);
        if (data == null) {
            data = new PillarData(id, dimension, pos);
            registry.pillarData.put(id, data);
        }
        registry.indexPosition(data);
        return data;
    }

    /** Marks the registry as loaded and server-synced without reading a file. */
    public void markAsLoaded() {
        hasLoaded = true;
        isServerSynced = true;
    }

    /** The records to send to players: on the server the last snapshot taken when a broadcast was prepared, on the client a copy of the current records. */
    public List<PillarData> getAllPillarDataForSync() {
        return serverAuthoritative ? syncSnapshot : new ArrayList<>(registry.pillarData.values());
    }

    private void refreshSyncSnapshot() {
        syncSnapshot = Collections.unmodifiableList(new ArrayList<>(copyDataSnapshot().values()));
    }
    // ---- Registry delegation: the public API of the pillar records ----

    /** A new id not used by any record: the prefix plus four digits, or four letters and digits if the digit ids are taken. An empty prefix becomes {@code S-P}. */
    public String generatePillarId(String expectedPrefix) {
        return registry.generatePillarId(expectedPrefix);
    }

    private String positionKey(String dimension, BlockPos pos) {
        return PillarRegistry.positionKey(dimension, pos);
    }

    /** The key of the position index: dimension and coordinates, plus the facing for an item frame. */
    public String positionKey(String dimension, BlockPos pos, Direction facing) {
        return PillarRegistry.positionKey(dimension, pos, facing);
    }

    /** The dimension id as text, or {@code unknown} for a null level. */
    public static String getDimensionKey(Level level) {
        return PillarRegistry.getDimensionKey(level);
    }

    /** The id indexed under a {@link #positionKey} value, or null. */
    public String getIdForPosition(String posKey) {
        return registry.getIdForPosition(posKey);
    }

    /** The live records, not a copy; use {@link #copyDataSnapshot()} to keep them. */
    public Collection<PillarData> getAllData() {
        return registry.getAllData();
    }

    public PillarData getPillarData(String pillarId) {
        return registry.getPillarData(pillarId);
    }

    /** The record indexed at exactly {@code pos}, or null. Unlike {@link #getOrCreatePillarData} this does not look down the stack or create anything. */
    public PillarData getPillarDataByPosition(Level level, BlockPos pos) {
        return registry.getPillarDataByPosition(level, pos);
    }

    public String getPillarIdByPosition(Level level, BlockPos pos) {
        return registry.getPillarIdByPosition(level, pos);
    }

    /** True if the id belongs to a record or to a removed item frame. */
    public boolean isIdTaken(String id) {
        return registry.isIdTaken(id);
    }

    /** Clears the colours, pattern and pattern settings of a record. Does not save, and leaves the world untouched. */
    public static void clearPillarOverrides(PillarData data) {
        PillarRegistry.clearPillarOverrides(data);
    }

    // ---- Hooks the persistence tests reach by reflection; each is a one-line pass-through to the registry ----

    private static final int MAX_REMOVED_FRAMES = PillarRegistry.MAX_REMOVED_FRAMES;

    private void rebuildPositionIndex() {
        registry.rebuildPositionIndex();
    }

    private boolean pruneRegistry() {
        return registry.pruneRegistry();
    }

    private static boolean hasFrameParticleData(String pattern, List<String> colors) {
        return PillarRegistry.hasFrameParticleData(pattern, colors);
    }

    private boolean removeOtherFrameAtPosition(String key, String id) {
        return registry.removeOtherFrameAtPosition(key, id);
    }

    private String reconcilePillarId(String savedId, String dimension, BlockPos basePos) {
        return registry.reconcilePillarId(savedId, dimension, basePos);
    }

    private String reconcilePillarId(String savedId, String dimension, BlockPos pos, BlockPos basePos) {
        return registry.reconcilePillarId(savedId, dimension, pos, basePos);
    }

    private String reconcileFrameId(String savedId, String dimension, BlockPos pos, Direction facing) {
        return registry.reconcileFrameId(savedId, dimension, pos, facing);
    }

    private boolean isRemovedFrame(String id, String dimension, BlockPos pos, Direction facing) {
        return registry.isRemovedFrame(id, dimension, pos, facing);
    }

    /** A read-only live view of every record id. */
    public Set<String> getAllPillarIds() {
        return registry.getAllPillarIds();
    }

    public int getPillarCount() {
        return registry.getPillarCount();
    }

    /** A copy of every record, with new record objects that are safe to change. */
    public Map<String, PillarData> copyDataSnapshot() {
        return registry.copyDataSnapshot();
    }

    /** The id of the item frame record at that position and facing, or null (also for a null facing). */
    public String getFrameIdForPosition(String dimension, BlockPos pos, Direction facing) {
        return registry.getFrameIdForPosition(dimension, pos, facing);
    }
    // ---- Recovery and world sync delegation: the public API is unchanged ----

    /**
     * Runs a recovery asked for by {@link #scheduleRecoveryFromWorld}. It waits until the delay has passed, the registry is
     * loaded, a player is online and the world has been loaded for a while; until then each call does nothing.
     */
    public static void checkAndRunScheduledRecovery() {
        PillarRecovery.checkAndRunScheduledRecovery();
    }

    /** Clears the pillar ids stored in the loaded chunks of every level and returns how many were cleared. Returns 0 when no player is online. */
    public int clearAllPillarIdsFromWorld(MinecraftServer server) {
        return PillarRecovery.clearAllPillarIdsFromWorld(server);
    }

    /**
     * Rebuilds records from the pillar block entities in loaded chunks, then copies colours from the block entities to
     * the records. Needs a player online. With {@code clearColors} it only clears the ids stored in the world.
     */
    public void recoverPillarsFromWorld(
            MinecraftServer server,
            boolean clearColors) {
        PillarRecovery.recoverPillarsFromWorld(this, server, clearColors);
    }

    /** Pushes the records' settings into the loaded pillar block entities of every level. Needs a player online. */
    public void syncAllLoadedPillars(
            MinecraftServer server) {
        PillarWorldSync.syncAllLoadedPillars(this, server);
    }

    /** Deletes item frame records in {@code level} whose frame is no longer in the world. Only checks chunks whose entities are loaded. */
    public void cleanupOrphans(Level level) {
        PillarWorldSync.cleanupOrphans(this, level);
    }

    /** Copies colours and pattern settings from loaded pillar block entities into the records. */
    public void syncColorsFromNBTToManager(MinecraftServer server) {
        PillarWorldSync.syncColorsFromNBTToManager(this, server);
    }

    /** Replaces each record's colours with those of the bottom block entity of its stack, where that chunk is loaded. Needs a player online. */
    public void loadColorsFromNBT(MinecraftServer server) {
        PillarWorldSync.loadColorsFromNBT(this, server);
    }

    /** Copies the pillar type and pattern settings from the block entity into the record. Returns true if the record changed. */
    public boolean syncPatternSettingsFromNBT(
            PillarBlockEntity pillarBE,
            PillarData data) {
        return PillarWorldSync.syncPatternSettingsFromNBT(pillarBE, data);
    }
    // ---- Item frame registration delegation: the public API is unchanged ----

    /** Server only: writes an edited item frame record to the frame entity if it is loaded, otherwise marks the edit pending until the frame loads. */
    public void applyFrameEdit(PillarData data) {
        PillarFrames.applyFrameEdit(this, data);
    }

    /** Server only: adds or reconciles the record of an item frame that carries a pattern or colours. */
    public void registerItemFrame(ItemFrame frame) {
        PillarFrames.registerItemFrame(this, frame);
    }

    public void registerItemFrame(ItemFrame frame, boolean immediate) {
        PillarFrames.registerItemFrame(this, frame, immediate);
    }

    /** Server only: same as {@link #registerItemFrame(ItemFrame)} for a coloured item frame. */
    public void registerColoredItemFrame(ColoredItemFrameEntity frame) {
        PillarFrames.registerColoredItemFrame(this, frame);
    }

    public void registerColoredItemFrame(ColoredItemFrameEntity frame, boolean immediate) {
        PillarFrames.registerColoredItemFrame(this, frame, immediate);
    }

    // ---- Hooks the persistence tests reach by reflection ----

    private boolean applyPendingFrameEdit(PillarData existing, CompoundTag tag, boolean immediate) {
        return PillarFrames.applyPendingFrameEdit(this, existing, tag, immediate);
    }

    private boolean prepareFrameRegistration(CompoundTag tag, String dimension, BlockPos pos,
            Direction facing) {
        return PillarFrames.prepareFrameRegistration(this, tag, dimension, pos, facing);
    }

    public static class PillarData {

        /** The pillar id, unique in the registry. */
        public String id;
        /** True on the record of a removed item frame, which is kept so the frame is not registered again. */
        public Boolean removed = null;
        /** Set on a frame whose pattern or colours were edited while its entity was not loaded. */
        public Boolean frameEditPending = null;
        /** Dye colours in the order they were added, at most five. */
        public List<String> dyeColors = new ArrayList<>();
        /** Dimension id as text, for example {@code minecraft:overworld}. */
        public String dimension;
        /** Block position; for a pillar stack the lowest block. */
        public int x, y, z;
        /** Creation and last change, in milliseconds since the epoch. */
        public long createdTime;
        public long modifiedTime;

        // The pattern fields below are null when the pillar has no setting of its own and the global particle config applies.
        public Boolean use_pattern = null;
        public String pattern = null;
        public Double pattern_speed = null;
        public Double pattern_spread = null;
        public Double pattern_intensity = null;
        public Integer max_particle_color = null;

        /** Registry id of the displayed item, or null. */
        public String displayedItem = null;
        /** Registry id of the pillar block's item, or {@code minecraft:item_frame} for an item frame record. */
        public String pillarType = null;
        /** Facing of an item frame; null for a pillar. */
        public String facing = null;
        /** Facing yaw of the pillar's displayed item. */
        public Float itemYaw = null;

        public PillarData() {}

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

        /** Adds a colour in upper case. Returns false, adding nothing, when the record already has five. */
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

        /** A read-only view of the dye colours. */
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
