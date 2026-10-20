package com.kingodogo.buildscape.feature.pillar;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.entity.ColoredItemFrameEntity;
import com.kingodogo.buildscape.feature.pillar.PillarIdManager.PillarData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Copies colours and pattern settings between the loaded pillar block entities and the records of a
 * {@link PillarIdManager}, and drops records whose item frame is gone.
 */
final class PillarWorldSync {

    private PillarWorldSync() {}

    static void syncAllLoadedPillars(PillarIdManager m, MinecraftServer server) {
        if (server == null || !server.isRunning()) {
            return;
        }

        if (server.getPlayerList().getPlayerCount() == 0) {
            return;
        }

        try {
            for (ServerLevel level : server.getAllLevels()) {
                if (level == null) {
                    continue;
                }

                if (!server.isRunning()) {
                    break;
                }

                String dimensionKey = PillarIdManager.getDimensionKey(level);

                for (PillarData data : m.registry.pillarData.values()) {
                    if (data == null) {
                        continue;
                    }
                    if (!data.dimension.equals(dimensionKey)) {
                        continue;
                    }

                    syncPillarFromData(level, data);
                }
            }

        } catch (Exception e) {
            // the sync is best effort; a failure is logged and nothing else changes
            BuildScape.LOGGER.error("Error in syncAllLoadedPillars", e);
        }
    }

    private static void syncPillarFromData(ServerLevel level, PillarData data) {
        try {
            BlockPos pos = data.getBlockPos();

            if (!level.hasChunkAt(pos)) {
                return;
            }

            ChunkAccess chunk = level.getChunk(pos);
            if (!(chunk instanceof LevelChunk)) {
                return;
            }

            if (!chunk.getStatus().isOrAfter(ChunkStatus.FULL)) {
                return;
            }

            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof PillarBlockEntity pillarBE) {
                pillarBE.syncFromData(data);
            }
        } catch (Exception e) {
            // one bad pillar must not stop the rest of the sync
            BuildScape.LOGGER.error("Error syncing pillar {}: {}", data.id, e.getMessage(), e);
        }
    }

    static void cleanupOrphans(PillarIdManager m, Level level) {
        if (!m.serverAuthoritative || !m.hasLoaded() || !(level instanceof ServerLevel serverLevel)) {
            return;
        }
        String dimension = PillarIdManager.getDimensionKey(level);
        Map<ChunkPos, List<PillarData>> chunks = new HashMap<>();
        for (PillarData data : m.registry.pillarData.values()) {
            if (!PillarRegistry.isItemFrameData(data) || !Objects.equals(dimension, data.dimension)) {
                continue;
            }
            BlockPos pos = data.getBlockPos();
            if (!level.hasChunkAt(pos) || !serverLevel.areEntitiesLoaded(ChunkPos.asLong(pos))) {
                continue;
            }
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
                livePositions.add(PillarRegistry.positionKey(dimension, entity.blockPosition(), facing));
                livePositions.add(PillarRegistry.positionKey(dimension, entity.blockPosition(), null));
            }
            for (PillarData data : entry.getValue()) {
                Direction facing = data.facing != null
                        ? Direction.byName(data.facing)
                        : null;
                if (!livePositions.contains(PillarRegistry.positionKey(dimension, data.getBlockPos(), facing))) {
                    changed |= m.registry.pillarData.remove(data.id, data);
                }
            }
        }
        if (changed) {
            m.registry.rebuildPositionIndex();
            if (m.registry.pillarData.isEmpty()) {
                m.allowEmptySave = true;
            }
            m.requestSave();
        }
    }

    /** Counters filled in while copying colours from the block entities to the records. */
    private static final class ColorSyncCounts {
        int synced = 0;
        int preserved = 0;
        int patternSynced = 0;
    }

    static void syncColorsFromNBTToManager(PillarIdManager m, MinecraftServer server) {
        if (server == null) {
            return;
        }

        if (!m.hasLoaded()) {
            return;
        }

        try {
            ColorSyncCounts counts = new ColorSyncCounts();

            for (ServerLevel level : server.getAllLevels()) {
                if (level == null) {
                    continue;
                }

                String dimensionKey = PillarIdManager.getDimensionKey(level);

                for (PillarData data : m.registry.pillarData.values()) {
                    if (data == null) {
                        continue;
                    }
                    if (!data.dimension.equals(dimensionKey)) {
                        continue;
                    }

                    syncColorsForPillar(level, data, counts);
                }
            }

            if (counts.synced > 0 || counts.patternSynced > 0) {
                m.requestSave();
            }
        } catch (Exception e) {
            // the sync is best effort; a failure is logged and nothing else changes
            BuildScape.LOGGER.error("Error in syncColorsFromNBTToManager", e);
        }
    }

    private static void syncColorsForPillar(ServerLevel level, PillarData data, ColorSyncCounts counts) {
        try {
            PillarBlockEntity bottomPillarBE = findLoadedStackBottom(level, data);
            if (bottomPillarBE == null) {
                counts.preserved++;
                return;
            }

            List<String> nbtColors = bottomPillarBE.getParticleColors();

            if (nbtColors != null && !nbtColors.isEmpty()) {
                if (PillarRecovery.colorsChanged(data.dyeColors, nbtColors)) {
                    data.clearColors();
                    addNonEmptyColors(data, nbtColors);
                    counts.synced++;
                } else {
                    counts.preserved++;
                }
            } else {
                int existingColorCount = (data.dyeColors != null) ? data.dyeColors.size() : 0;
                if (existingColorCount > 0) {
                    counts.preserved++;
                }
            }

            if (syncPatternSettingsFromNBT(bottomPillarBE, data)) {
                counts.patternSynced++;
            }
        } catch (Exception e) {
            // one bad pillar must not stop the rest of the sync
            counts.preserved++;
            BuildScape.LOGGER.error("Error syncing colors from NBT for pillar {}: {}", data.id, e.getMessage(), e);
        }
    }

    static void loadColorsFromNBT(PillarIdManager m, MinecraftServer server) {
        if (server == null || !server.isRunning()) {
            return;
        }

        if (server.getPlayerList().getPlayerCount() == 0) {
            return;
        }

        try {
            int loadedCount = 0;

            for (ServerLevel level : server.getAllLevels()) {
                if (level == null) {
                    continue;
                }

                String dimensionKey = PillarIdManager.getDimensionKey(level);

                for (PillarData data : m.registry.pillarData.values()) {
                    if (data == null) {
                        continue;
                    }
                    if (!data.dimension.equals(dimensionKey)) {
                        continue;
                    }

                    if (loadColorsForPillar(level, data)) {
                        loadedCount++;
                    }
                }
            }

            if (loadedCount > 0) {
                m.requestSave();
            }
        } catch (Exception e) {
            // the sync is best effort; a failure is logged and nothing else changes
            BuildScape.LOGGER.error("Error in loadColorsFromNBT", e);
        }
    }

    /** Copies the stack bottom's colours onto the record; true when colours were loaded. */
    private static boolean loadColorsForPillar(ServerLevel level, PillarData data) {
        try {
            PillarBlockEntity bottomPillarBE = findLoadedStackBottom(level, data);
            if (bottomPillarBE == null) {
                return false;
            }

            List<String> nbtColors = bottomPillarBE.getParticleColors();
            if (nbtColors == null || nbtColors.isEmpty()) {
                return false;
            }

            data.clearColors();
            addNonEmptyColors(data, nbtColors);
            return true;
        } catch (Exception e) {
            // one bad pillar must not stop the rest of the sync
            BuildScape.LOGGER.error("Error loading colors from NBT for pillar {}: {}", data.id, e.getMessage(), e);
            return false;
        }
    }

    /**
     * The bottom pillar of the stack that holds the record's position, or null when the position is not in a
     * fully loaded chunk or is not a pillar stack.
     */
    private static PillarBlockEntity findLoadedStackBottom(ServerLevel level, PillarData data) {
        BlockPos pos = data.getBlockPos();

        if (!level.isLoaded(pos)) {
            return null;
        }

        ChunkAccess chunk = level.getChunk(pos);
        if (!(chunk instanceof LevelChunk)) {
            return null;
        }

        if (!chunk.getStatus().isOrAfter(ChunkStatus.FULL)) {
            return null;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof PillarBlockEntity pillarBE)) {
            return null;
        }

        BlockPos bottomPos = pillarBE.findStackBottom();
        BlockEntity bottomBE = level.getBlockEntity(bottomPos);
        return bottomBE instanceof PillarBlockEntity bottomPillarBE ? bottomPillarBE : null;
    }

    private static void addNonEmptyColors(PillarData data, List<String> colors) {
        for (String color : colors) {
            if (color != null && !color.isEmpty()) {
                data.addColor(color);
            }
        }
    }

    static boolean syncPatternSettingsFromNBT(
            PillarBlockEntity pillarBE,
            PillarData data) {
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
            if (nbtColors != null && !nbtColors.isEmpty()) {
                nbtMaxColor = nbtColors.size();
            }
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
}
