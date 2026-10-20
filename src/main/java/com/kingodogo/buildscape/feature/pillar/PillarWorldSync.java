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

    static void syncAllLoadedPillars(
            PillarIdManager m,
            MinecraftServer server) {
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

                    try {
                        BlockPos pos = data.getBlockPos();

                        if (!level.hasChunkAt(pos)) {
                            skippedCount++;
                            continue;
                        }

                        ChunkAccess chunk = level.getChunk(
                                pos);
                        if (!(chunk instanceof LevelChunk)) {
                            skippedCount++;
                            continue;
                        }

                        if (!chunk
                                .getStatus()
                                .isOrAfter(ChunkStatus.FULL)) {
                            skippedCount++;
                            continue;
                        }

                        BlockEntity be = level.getBlockEntity(pos);
                        if (be instanceof PillarBlockEntity pillarBE) {

                            pillarBE.syncFromData(data);
                            syncedCount++;
                        }
                    } catch (Exception e) {
                        // one bad pillar must not stop the rest of the sync
                        BuildScape.LOGGER.error("Error syncing pillar {}: {}", (data != null ? data.id : "unknown"),
                                e.getMessage(), e);
                    }
                }
            }

        } catch (Exception e) {
            // the sync is best effort; a failure is logged and nothing else changes
            BuildScape.LOGGER.error("Error in syncAllLoadedPillars", e);
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

    static void syncColorsFromNBTToManager(PillarIdManager m, MinecraftServer server) {
        if (server == null) {
            return;
        }

        if (!m.hasLoaded()) {
            return;
        }

        try {
            int syncedCount = 0;
            int preservedCount = 0;
            int patternSyncedCount = 0;

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
                        // one bad pillar must not stop the rest of the sync
                        preservedCount++;
                        BuildScape.LOGGER.error("Error syncing colors from NBT for pillar {}: {}",
                                (data != null ? data.id : "unknown"),
                                e.getMessage(), e);
                    }
                }
            }

            if (syncedCount > 0 || patternSyncedCount > 0) {
                m.requestSave();
            } else if (preservedCount > 0) {
            }
        } catch (Exception e) {
            // the sync is best effort; a failure is logged and nothing else changes
            BuildScape.LOGGER.error("Error in syncColorsFromNBTToManager", e);
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
                        // one bad pillar must not stop the rest of the sync
                        BuildScape.LOGGER.error("Error loading colors from NBT for pillar {}: {}",
                                (data != null ? data.id : "unknown"),
                                e.getMessage(), e);
                    }
                }
            }

            if (loadedCount > 0) {
                m.requestSave();
            } else {
            }
        } catch (Exception e) {
            // the sync is best effort; a failure is logged and nothing else changes
            BuildScape.LOGGER.error("Error in loadColorsFromNBT", e);
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
