package com.kingodogo.buildscape.feature.pillar;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.feature.pillar.PillarIdManager.PillarData;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.server.ServerLifecycleHooks;

/**
 * Scheduling and running the recovery of pillar records from the loaded world, for the server instance.
 * The flags are static and shared, as they were in {@link PillarIdManager}.
 */
final class PillarRecovery {

    private PillarRecovery() {}

    static boolean recoveryScheduled = false;
    static long recoveryScheduledTime = 0L;
    static boolean recoveryClearColors = false;
    static final long RECOVERY_DELAY_MS = 5000;
    static boolean recoveryInProgress = false;

    static long worldLoadStartTime = 0L;
    static final long MIN_WORLD_LOAD_TIME_MS = 15000;
    /** Clears the recovery flags and restarts the world-load clock; called when a world is loaded or reset. */
    static void reset() {
        worldLoadStartTime = System.currentTimeMillis();
        recoveryScheduled = false;
        recoveryScheduledTime = 0L;
        recoveryClearColors = false;
        recoveryInProgress = false;
    }

    static void schedule(boolean clearColors) {
        recoveryScheduled = true;
        recoveryScheduledTime = System.currentTimeMillis();
        recoveryClearColors = clearColors;
    }

    static void checkAndRunScheduledRecovery() {
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

        PillarIdManager manager = PillarIdManager.get();
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
        recoverPillarsFromWorld(manager, server, clearColors);
    }

    static boolean isWorldReadyForRecovery() {
        if (worldLoadStartTime == 0L) {
            return true;
        }
        long elapsed = System.currentTimeMillis() - worldLoadStartTime;
        return elapsed >= MIN_WORLD_LOAD_TIME_MS;
    }

    static int clearAllPillarIdsFromWorld(MinecraftServer server) {
        if (server == null || !server.isRunning()) {
            return 0;
        }

        if (server.getPlayerList().getPlayerCount() == 0) {
            return 0;
        }

        int clearedCount = 0;

        try {

            for (ServerLevel level : server.getAllLevels()) {
                if (level == null) {
                    continue;
                }

                try {
                    int chunkRange = 64;
                    BlockPos spawnPos = level.getSharedSpawnPos();
                    ChunkPos spawnChunk = spawnPos != null
                            ? new ChunkPos(spawnPos)
                            : new ChunkPos(0, 0);

                    for (int chunkX = spawnChunk.x - chunkRange; chunkX <= spawnChunk.x + chunkRange; chunkX++) {
                        for (int chunkZ = spawnChunk.z - chunkRange; chunkZ <= spawnChunk.z + chunkRange; chunkZ++) {
                            ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);

                            if (!level.hasChunkAt(chunkPos.getWorldPosition())) {
                                continue;
                            }

                            try {
                                ChunkAccess chunkAccess = level.getChunk(chunkX, chunkZ);

                                if (!(chunkAccess instanceof LevelChunk chunk)) {
                                    continue;
                                }

                                if (!chunkAccess
                                        .getStatus()
                                        .isOrAfter(ChunkStatus.FULL)) {
                                    continue;
                                }

                                for (BlockEntity be : chunk
                                        .getBlockEntities()
                                        .values()) {
                                    if (be instanceof PillarBlockEntity pillarBE) {

                                        if (pillarBE.getPillarId() != null &&
                                                !pillarBE.getPillarId().isEmpty()) {
                                            pillarBE.clearLocalStateOnly();
                                            pillarBE.setChanged();

                                            level.sendBlockUpdated(
                                                    be.getBlockPos(),
                                                    level.getBlockState(be.getBlockPos()),
                                                    level.getBlockState(be.getBlockPos()),
                                                    3);

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

    static void recoverPillarsFromWorld(
            PillarIdManager m,
            MinecraftServer server,
            boolean clearColors) {
        if (server == null || !server.isRunning()) {
            return;
        }

        if (server.getPlayerList().getPlayerCount() == 0) {
            return;
        }

        try {
            if (clearColors) {
                int clearedCount = clearAllPillarIdsFromWorld(server);
                m.requestSave();
                return;
            }

            recoveryInProgress = true;
            int recoveredCount = 0;
            int skippedCount = 0;
            int colorClearedCount = 0;

            for (ServerLevel level : server.getAllLevels()) {
                if (level == null) {
                    continue;
                }

                String dimensionKey = PillarIdManager.getDimensionKey(level);

                try {
                    Set<BlockEntity> allBlockEntities = new HashSet<>();

                    int chunkRange = 64;
                    BlockPos spawnPos = level.getSharedSpawnPos();
                    ChunkPos spawnChunk = spawnPos != null
                            ? new ChunkPos(spawnPos)
                            : new ChunkPos(0, 0);

                    for (int chunkX = spawnChunk.x - chunkRange; chunkX <= spawnChunk.x + chunkRange; chunkX++) {
                        for (int chunkZ = spawnChunk.z - chunkRange; chunkZ <= spawnChunk.z + chunkRange; chunkZ++) {
                            ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);

                            if (!level.hasChunkAt(chunkPos.getWorldPosition())) {
                                continue;
                            }

                            try {
                                ChunkAccess chunkAccess = level.getChunk(chunkX, chunkZ);

                                if (!(chunkAccess instanceof LevelChunk chunk)) {
                                    continue;
                                }

                                if (!chunkAccess
                                        .getStatus()
                                        .isOrAfter(ChunkStatus.FULL)) {
                                    continue;
                                }

                                for (BlockEntity be : chunk
                                        .getBlockEntities()
                                        .values()) {
                                    if (be instanceof PillarBlockEntity) {
                                        allBlockEntities.add(be);
                                    }
                                }
                            } catch (Exception e) {
                                continue;
                            }
                        }
                    }

                    for (BlockEntity be : allBlockEntities) {
                        if (be instanceof PillarBlockEntity pillarBE) {
                            try {

                                String pillarId = pillarBE.getPillarId();
                                if (pillarId == null || pillarId.isEmpty()) {
                                    skippedCount++;
                                    continue;
                                }

                                BlockPos pos = be.getBlockPos();

                                PillarData existingData = m.registry.pillarData.get(pillarId);

                                if (existingData != null) {
                                    boolean positionChanged = !(existingData.dimension.equals(dimensionKey) &&
                                            existingData.x == pos.getX() &&
                                            existingData.y == pos.getY() &&
                                            existingData.z == pos.getZ());

                                    if (positionChanged) {

                                        existingData.dimension = dimensionKey;
                                        existingData.x = pos.getX();
                                        existingData.y = pos.getY();
                                        existingData.z = pos.getZ();
                                        m.requestSave();
                                    }

                                    if (!clearColors) {
                                        List<String> pillarColors = pillarBE.getParticleColors();
                                        if (pillarColors != null && !pillarColors.isEmpty()) {
                                            boolean colorsChanged = false;
                                            if (existingData.dyeColors == null
                                                    || existingData.dyeColors.size() != pillarColors.size()) {
                                                colorsChanged = true;
                                            } else {
                                                for (int i = 0; i < pillarColors.size(); i++) {
                                                    String nbtColor = pillarColors.get(i);
                                                    String managerColor = i < existingData.dyeColors.size()
                                                            ? existingData.dyeColors.get(i)
                                                            : null;
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
                                    List<String> pillarColors = pillarBE.getParticleColors();
                                    if (pillarColors != null && !pillarColors.isEmpty()) {
                                        for (String color : pillarColors) {
                                            if (color != null && !color.isEmpty()) {
                                                data.addColor(color);
                                            }
                                        }
                                    }
                                }

                                m.registry.pillarData.put(pillarId, data);
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

            m.registry.rebuildPositionIndex();
            PillarWorldSync.syncColorsFromNBTToManager(m, server);

            recoveryInProgress = false;

            if (recoveredCount > 0 || colorClearedCount > 0) {
                m.requestSave();
            } else {
            }

        } catch (Exception e) {
            BuildScape.LOGGER.error("Error during pillar recovery", e);
        } finally {
            recoveryInProgress = false;
        }
    }
}
