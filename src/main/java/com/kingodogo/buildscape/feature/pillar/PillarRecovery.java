package com.kingodogo.buildscape.feature.pillar;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.feature.pillar.PillarIdManager.PillarData;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
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
 * The flags belong to the current server lifecycle and live in {@link PillarServerState}.
 */
final class PillarRecovery {

    private PillarRecovery() {}

    static final long RECOVERY_DELAY_MS = 5000;
    static final long MIN_WORLD_LOAD_TIME_MS = 15000;

    /** True while a recovery scan of the current server is running; saves wait for it. */
    static boolean isInProgress() {
        return PillarServerState.current().recoveryInProgress;
    }

    static void schedule(boolean clearColors) {
        PillarServerState state = PillarServerState.current();
        state.recoveryScheduled = true;
        state.recoveryScheduledTime = System.currentTimeMillis();
        state.recoveryClearColors = clearColors;
    }

    static void checkAndRunScheduledRecovery() {
        PillarServerState state = PillarServerState.current();
        if (!state.recoveryScheduled) {
            return;
        }

        long elapsed = System.currentTimeMillis() - state.recoveryScheduledTime;
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

        state.recoveryScheduled = false;
        boolean clearColors = state.recoveryClearColors;
        state.recoveryClearColors = false;
        recoverPillarsFromWorld(manager, server, clearColors);
    }

    static boolean isWorldReadyForRecovery() {
        long worldLoadStartTime = PillarServerState.current().worldLoadStartTime;
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

        AtomicInteger clearedCount = new AtomicInteger();

        try {
            for (ServerLevel level : server.getAllLevels()) {
                if (level == null) {
                    continue;
                }

                try {
                    forEachLoadedChunk(level, chunk -> clearPillarIdsInChunk(level, chunk, clearedCount));
                } catch (Exception e) {
                    // a failure scanning one level is logged; the other levels continue
                    BuildScape.LOGGER.error("Error scanning chunks to clear pillar IDs", e);
                }
            }
        } catch (Exception e) {
            // recovery runs on live world data; log the failure and return the count so far
            BuildScape.LOGGER.error("Error clearing pillar IDs from world", e);
        }

        return clearedCount.get();
    }

    /**
     * Calls {@code action} for every fully loaded chunk within the scan range around the level's spawn. A chunk whose
     * read or action fails is skipped.
     */
    private static void forEachLoadedChunk(ServerLevel level, Consumer<LevelChunk> action) {
        int chunkRange = 64;
        BlockPos spawnPos = level.getSharedSpawnPos();
        ChunkPos spawnChunk = spawnPos != null ? new ChunkPos(spawnPos) : new ChunkPos(0, 0);

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

                    if (!chunkAccess.getStatus().isOrAfter(ChunkStatus.FULL)) {
                        continue;
                    }

                    action.accept(chunk);
                } catch (Exception e) {
                    // an unreadable chunk is skipped
                }
            }
        }
    }

    private static void clearPillarIdsInChunk(ServerLevel level, LevelChunk chunk, AtomicInteger clearedCount) {
        for (BlockEntity be : chunk.getBlockEntities().values()) {
            if (!(be instanceof PillarBlockEntity pillarBE)) {
                continue;
            }
            if (pillarBE.getPillarId() == null || pillarBE.getPillarId().isEmpty()) {
                continue;
            }

            pillarBE.clearLocalStateOnly();
            pillarBE.setChanged();

            level.sendBlockUpdated(
                    be.getBlockPos(), level.getBlockState(be.getBlockPos()), level.getBlockState(be.getBlockPos()), 3);

            clearedCount.incrementAndGet();
        }
    }

    /** Counters filled in while recovering pillars from the world. */
    private static final class RecoveryCounts {
        int recovered = 0;
        int skipped = 0;
        int colorCleared = 0;
    }

    static void recoverPillarsFromWorld(PillarIdManager m, MinecraftServer server, boolean clearColors) {
        if (server == null || !server.isRunning()) {
            return;
        }

        if (server.getPlayerList().getPlayerCount() == 0) {
            return;
        }

        PillarServerState state = PillarServerState.current();
        try {
            if (clearColors) {
                clearAllPillarIdsFromWorld(server);
                m.requestSave();
                return;
            }

            state.recoveryInProgress = true;
            RecoveryCounts counts = new RecoveryCounts();

            for (ServerLevel level : server.getAllLevels()) {
                if (level == null) {
                    continue;
                }

                String dimensionKey = PillarIdManager.getDimensionKey(level);

                try {
                    recoverPillarsInLevel(m, level, dimensionKey, clearColors, counts);
                } catch (Exception e) {
                    // a failure scanning one level is logged and counted as skipped
                    BuildScape.LOGGER.error("Error scanning chunks for pillars", e);
                    counts.skipped++;
                }
            }

            m.registry.rebuildPositionIndex();
            PillarWorldSync.syncColorsFromNBTToManager(m, server);

            state.recoveryInProgress = false;

            if (counts.recovered > 0 || counts.colorCleared > 0) {
                m.requestSave();
            }

        } catch (Exception e) {
            // recovery runs on live world data; log any failure (the flag is released below)
            BuildScape.LOGGER.error("Error during pillar recovery", e);
        } finally {
            state.recoveryInProgress = false;
        }
    }

    private static void recoverPillarsInLevel(
            PillarIdManager m, ServerLevel level, String dimensionKey, boolean clearColors, RecoveryCounts counts) {
        Set<BlockEntity> allBlockEntities = new HashSet<>();
        forEachLoadedChunk(level, chunk -> {
            for (BlockEntity be : chunk.getBlockEntities().values()) {
                if (be instanceof PillarBlockEntity) {
                    allBlockEntities.add(be);
                }
            }
        });

        for (BlockEntity be : allBlockEntities) {
            if (!(be instanceof PillarBlockEntity pillarBE)) {
                continue;
            }
            try {
                recoverPillar(m, pillarBE, dimensionKey, clearColors, counts);
            } catch (Exception e) {
                // one bad pillar is counted as skipped
                BuildScape.LOGGER.error("Error processing pillar", e);
                counts.skipped++;
            }
        }
    }

    private static void recoverPillar(
            PillarIdManager m,
            PillarBlockEntity pillarBE,
            String dimensionKey,
            boolean clearColors,
            RecoveryCounts counts) {
        String pillarId = pillarBE.getPillarId();
        if (pillarId == null || pillarId.isEmpty()) {
            counts.skipped++;
            return;
        }

        BlockPos pos = pillarBE.getBlockPos();

        PillarData existingData = m.registry.pillarData.get(pillarId);
        if (existingData != null) {
            updateExistingPillar(m, existingData, pillarBE, dimensionKey, pos, clearColors, counts);
            return;
        }

        PillarData data = new PillarData(pillarId, dimensionKey, pos);

        if (clearColors) {
            data.clearColors();
            counts.colorCleared++;
        } else {
            addNonEmptyColors(data, pillarBE.getParticleColors());
        }

        m.registry.pillarData.put(pillarId, data);
        counts.recovered++;
    }

    private static void updateExistingPillar(
            PillarIdManager m,
            PillarData existingData,
            PillarBlockEntity pillarBE,
            String dimensionKey,
            BlockPos pos,
            boolean clearColors,
            RecoveryCounts counts) {
        boolean positionChanged = !(existingData.dimension.equals(dimensionKey)
                && existingData.x == pos.getX()
                && existingData.y == pos.getY()
                && existingData.z == pos.getZ());

        if (positionChanged) {
            existingData.dimension = dimensionKey;
            existingData.x = pos.getX();
            existingData.y = pos.getY();
            existingData.z = pos.getZ();
            m.requestSave();
        }

        if (!clearColors) {
            List<String> pillarColors = pillarBE.getParticleColors();
            if (pillarColors != null && !pillarColors.isEmpty()
                    && colorsChanged(existingData.dyeColors, pillarColors)) {
                existingData.clearColors();
                addNonEmptyColors(existingData, pillarColors);
            }
        } else if (existingData.hasColors()) {
            existingData.clearColors();
            counts.colorCleared++;
        }
    }

    /** Whether the colours stored on the pillar block entity differ from the ones the manager holds. */
    static boolean colorsChanged(List<String> managerColors, List<String> pillarColors) {
        if (managerColors == null || managerColors.size() != pillarColors.size()) {
            return true;
        }
        for (int i = 0; i < pillarColors.size(); i++) {
            String nbtColor = pillarColors.get(i);
            if (nbtColor == null || !nbtColor.equals(managerColors.get(i))) {
                return true;
            }
        }
        return false;
    }

    private static void addNonEmptyColors(PillarData data, List<String> colors) {
        if (colors == null) {
            return;
        }
        for (String color : colors) {
            if (color != null && !color.isEmpty()) {
                data.addColor(color);
            }
        }
    }
}
