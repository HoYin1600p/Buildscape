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

public class PillarIdManager {
    private static final PillarIdManager SERVER_INSTANCE = new PillarIdManager(true);
    private static final PillarIdManager CLIENT_INSTANCE = new PillarIdManager(false);

    final boolean serverAuthoritative;
    final PillarRegistry registry = new PillarRegistry();
    private final Map<String, PillarData> removedFrames = registry.removedFrames;
    private final Map<String, String> positionIndex = registry.positionIndex;

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

    public static void resetWorldCache() {
        PillarStore.resetCachedWorldDir();
        PillarRecovery.reset();

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

    public boolean hasLoaded() {
        return hasLoaded;
    }

    public boolean isLoadInProgress() {
        return loadInProgress;
    }

    public boolean needsWorldRecovery() {
        return serverAuthoritative && hasLoaded && fileWasDeleted && registry.pillarData.isEmpty();
    }
    public static String getVariantPrefix(Level level, BlockPos pos) {
        return PillarRegistry.getVariantPrefix(level, pos);
    }

    public static void fullReset() {
        PillarStore.resetCachedWorldDir();
        PillarRecovery.reset();

        SERVER_INSTANCE.resetState(false);
    }

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

    public void clearPillarColors(String pillarId) {
        PillarData data = registry.pillarData.get(pillarId);
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
                BuildScape.LOGGER.error("Unable to read pillar backup", e);
            }
        }
        return new SavedData(file, Collections.emptyMap());
    }

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
            BuildScape.LOGGER.error("Unable to save pillar data", t);
            return false;
        }
    }

    public void scheduleRecoveryFromWorld(
            MinecraftServer server,
            boolean clearColors) {
        if (!serverAuthoritative || server == null || !server.isRunning()) {
            return;
        }
        PillarRecovery.schedule(clearColors);
    }

    public void save() {
        requestSave();
    }

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

    public void forceReload() {
        load();
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

            int saveCount = registry.pillarData.size();
            int colorsCount = 0;
            for (PillarData data : registry.pillarData.values()) {
                if (data != null && data.hasColors()) {
                    colorsCount++;
                }
            }

            saveToFile(PillarStore.getBackupDataFile(), PillarStore.BACKUP_FILE_NAME);
        } catch (Throwable t) {
            BuildScape.LOGGER.error("Error saving backup file", t);
        }
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

    public synchronized void trackLoadedChunk(LevelChunk chunk, boolean loaded) {
        if (loaded) {
            loadedChunks.add(chunk);
        } else {
            loadedChunks.remove(chunk);
        }
    }

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
        ItemFrameParticleHandler.clearClientCaches();
        hasLoaded = true;
        isServerSynced = true;
    }

    public void clearForServerSync() {
        registry.pillarData.clear();
        positionIndex.clear();
        ItemFrameParticleHandler.clearClientCaches();
        hasLoaded = false;
    }

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
                BuildScape.LOGGER.debug("Skipped position index for pillar {}", data.id, e);
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

    public void markAsLoaded() {
        hasLoaded = true;
        isServerSynced = true;
    }

    public List<PillarData> getAllPillarDataForSync() {
        return serverAuthoritative ? syncSnapshot : new ArrayList<>(registry.pillarData.values());
    }

    private void refreshSyncSnapshot() {
        syncSnapshot = Collections.unmodifiableList(new ArrayList<>(copyDataSnapshot().values()));
    }
    // ---- Registry delegation: the public API of the pillar records ----

    public String generatePillarId(String expectedPrefix) {
        return registry.generatePillarId(expectedPrefix);
    }

    private String positionKey(String dimension, BlockPos pos) {
        return PillarRegistry.positionKey(dimension, pos);
    }

    public String positionKey(String dimension, BlockPos pos, Direction facing) {
        return PillarRegistry.positionKey(dimension, pos, facing);
    }

    public static String getDimensionKey(Level level) {
        return PillarRegistry.getDimensionKey(level);
    }

    public String getIdForPosition(String posKey) {
        return registry.getIdForPosition(posKey);
    }

    public Collection<PillarData> getAllData() {
        return registry.getAllData();
    }

    public PillarData getPillarData(String pillarId) {
        return registry.getPillarData(pillarId);
    }

    public PillarData getPillarDataByPosition(Level level, BlockPos pos) {
        return registry.getPillarDataByPosition(level, pos);
    }

    public String getPillarIdByPosition(Level level, BlockPos pos) {
        return registry.getPillarIdByPosition(level, pos);
    }

    public boolean isIdTaken(String id) {
        return registry.isIdTaken(id);
    }

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

    public Set<String> getAllPillarIds() {
        return registry.getAllPillarIds();
    }

    public int getPillarCount() {
        return registry.getPillarCount();
    }

    public Map<String, PillarData> copyDataSnapshot() {
        return registry.copyDataSnapshot();
    }

    public String getFrameIdForPosition(String dimension, BlockPos pos, Direction facing) {
        return registry.getFrameIdForPosition(dimension, pos, facing);
    }
    // ---- Recovery and world sync delegation: the public API is unchanged ----

    public static void checkAndRunScheduledRecovery() {
        PillarRecovery.checkAndRunScheduledRecovery();
    }

    public int clearAllPillarIdsFromWorld(MinecraftServer server) {
        return PillarRecovery.clearAllPillarIdsFromWorld(server);
    }

    public void recoverPillarsFromWorld(
            MinecraftServer server,
            boolean clearColors) {
        PillarRecovery.recoverPillarsFromWorld(this, server, clearColors);
    }

    public void syncAllLoadedPillars(
            MinecraftServer server) {
        PillarWorldSync.syncAllLoadedPillars(this, server);
    }

    public void cleanupOrphans(Level level) {
        PillarWorldSync.cleanupOrphans(this, level);
    }

    public void syncColorsFromNBTToManager(MinecraftServer server) {
        PillarWorldSync.syncColorsFromNBTToManager(this, server);
    }

    public void loadColorsFromNBT(MinecraftServer server) {
        PillarWorldSync.loadColorsFromNBT(this, server);
    }

    public boolean syncPatternSettingsFromNBT(
            PillarBlockEntity pillarBE,
            PillarData data) {
        return PillarWorldSync.syncPatternSettingsFromNBT(pillarBE, data);
    }
    // ---- Item frame registration delegation: the public API is unchanged ----

    public void applyFrameEdit(PillarData data) {
        PillarFrames.applyFrameEdit(this, data);
    }

    public void registerItemFrame(ItemFrame frame) {
        PillarFrames.registerItemFrame(this, frame);
    }

    public void registerItemFrame(ItemFrame frame, boolean immediate) {
        PillarFrames.registerItemFrame(this, frame, immediate);
    }

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
