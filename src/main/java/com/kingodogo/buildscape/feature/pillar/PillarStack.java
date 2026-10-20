package com.kingodogo.buildscape.feature.pillar;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.core.ParticlePatterns;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * Walks the stack of pillar blocks a pillar block entity belongs to: its top and bottom, the pattern and id
 * found along it, and pushing a pattern or colours up the whole stack.
 */
final class PillarStack {

    private PillarStack() {}

    static BlockPos findStackTop(PillarBlockEntity self) {
        final Level level = self.getLevel();
        final BlockPos worldPosition = self.getBlockPos();
        if (level == null) {
            return worldPosition;
        }

        if (!level.isClientSide &&
                !BuildScape.isServerFullyInitialized()) {
            return worldPosition;
        }

        BlockPos current = worldPosition;
        int maxHeight = 256;
        int checked = 0;

        while (checked < maxHeight) {
            BlockPos above = current.above();
            if (!level.hasChunkAt(above)) {
                break;
            }

            try {
                if (!level.isClientSide) {
                    ChunkAccess chunk = level.getChunk(
                            above);
                    if (!(chunk instanceof LevelChunk)) {
                        break;
                    }
                    if (!chunk
                            .getStatus()
                            .isOrAfter(ChunkStatus.FULL)) {
                        break;
                    }
                }
            } catch (Exception e) {
                // chunk access can fail while the area loads; treat the position as unavailable
                break;
            }

            if (!(level.getBlockState(above).getBlock() instanceof PillarBlock)) {
                break;
            }
            current = above;
            checked++;
        }
        return current;
    }

    static String getStackParticlePattern(PillarBlockEntity self) {
        final Level level = self.getLevel();
        if (level == null) {
            return self.particlePattern;
        }

        if (!level.isClientSide &&
                !BuildScape.isServerFullyInitialized()) {
            return self.particlePattern;
        }

        if (!level.isClientSide &&
                (level.getServer() == null ||
                        !level.getServer().isRunning() ||
                        level.getServer().getPlayerList().getPlayerCount() == 0)) {
            return self.particlePattern;
        }

        try {
            BlockPos bottom = findStackBottom(self);
            BlockPos current = bottom;
            int maxHeight = 256;
            int checked = 0;

            while (checked < maxHeight &&
                    level.getBlockState(current).getBlock() instanceof PillarBlock) {
                if (!level.hasChunkAt(current)) {
                    break;
                }

                try {
                    if (!level.isClientSide) {
                        ChunkAccess chunk = level.getChunk(
                                current);
                        if (!(chunk instanceof LevelChunk)) {
                            break;
                        }
                        if (!chunk
                                .getStatus()
                                .isOrAfter(ChunkStatus.FULL)) {
                            break;
                        }
                    }
                } catch (Exception e) {
                    // chunk access can fail while the area loads; treat the position as unavailable
                    break;
                }

                BlockEntity be = level.getBlockEntity(current);
                if (be instanceof PillarBlockEntity pillarBE) {
                    if (pillarBE.particlePattern != null &&
                            !pillarBE.particlePattern.isEmpty() &&
                            !pillarBE.particlePattern.equals(ParticlePatterns.DEFAULT)) {
                        return pillarBE.particlePattern;
                    }
                }
                current = current.above();
                checked++;
            }
        } catch (Exception e) {
            // the stack walk failed; fall back to this pillar's own value
            return self.particlePattern;
        }

        return null;
    }

    static BlockPos findStackBottom(PillarBlockEntity self) {
        final Level level = self.getLevel();
        final BlockPos worldPosition = self.getBlockPos();
        if (level == null) {
            return worldPosition;
        }

        if (!level.isClientSide &&
                !BuildScape.isServerFullyInitialized()) {
            return worldPosition;
        }

        BlockPos current = worldPosition;
        int maxDepth = 256;
        int checked = 0;

        while (checked < maxDepth) {
            BlockPos below = current.below();
            if (!level.hasChunkAt(below)) {
                break;
            }

            try {
                if (!level.isClientSide) {
                    ChunkAccess chunk = level.getChunk(
                            below);
                    if (!(chunk instanceof LevelChunk)) {
                        break;
                    }
                    if (!chunk
                            .getStatus()
                            .isOrAfter(ChunkStatus.FULL)) {
                        break;
                    }
                }
            } catch (Exception e) {
                // chunk access can fail while the area loads; treat the position as unavailable
                break;
            }

            if (!(level.getBlockState(below).getBlock() instanceof PillarBlock)) {
                break;
            }
            current = below;
            checked++;
        }
        return current;
    }

    static String getStackPillarId(PillarBlockEntity self) {
        final Level level = self.getLevel();
        if (level == null || level.isClientSide) {
            return self.pillarId;
        }

        if (!BuildScape.isServerFullyInitialized()) {
            return self.pillarId;
        }

        if (level.getServer() == null ||
                !level.getServer().isRunning() ||
                level.getServer().getPlayerList().getPlayerCount() == 0) {
            return self.pillarId;
        }

        try {
            BlockPos bottom = findStackBottom(self);
            BlockPos current = bottom;
            int maxHeight = 256;
            int checked = 0;

            while (checked < maxHeight &&
                    level.getBlockState(current).getBlock() instanceof PillarBlock) {
                if (!level.hasChunkAt(current)) {
                    break;
                }

                try {
                    if (!level.isClientSide) {
                        ChunkAccess chunk = level.getChunk(
                                current);
                        if (!(chunk instanceof LevelChunk)) {
                            break;
                        }
                        if (!chunk
                                .getStatus()
                                .isOrAfter(ChunkStatus.FULL)) {
                            break;
                        }
                    }
                } catch (Exception e) {
                    // chunk access can fail while the area loads; treat the position as unavailable
                    break;
                }

                BlockEntity be = level.getBlockEntity(current);
                if (be instanceof PillarBlockEntity pillarBE) {
                    if (pillarBE.pillarId != null && !pillarBE.pillarId.isEmpty()) {
                        return pillarBE.pillarId;
                    }
                }
                current = current.above();
                checked++;
            }
        } catch (Exception e) {
            // the stack walk failed; fall back to this pillar's own value
            return self.pillarId;
        }

        return null;
    }

    static void propagatePatternToStack(PillarBlockEntity self, String pattern) {
        final Level level = self.getLevel();
        if (level == null || level.isClientSide) {
            return;
        }

        BlockPos bottom = findStackBottom(self);
        BlockPos current = bottom;

        while (level.getBlockState(current).getBlock() instanceof PillarBlock) {
            BlockEntity be = level.getBlockEntity(current);
            if (be instanceof PillarBlockEntity pillarBE) {
                pillarBE.particlePattern = pattern;
                pillarBE.setChanged();

                level.sendBlockUpdated(
                        current,
                        level.getBlockState(current),
                        level.getBlockState(current),
                        3);
            }
            current = current.above();
        }
    }

    static void propagateToStack(PillarBlockEntity self, String stackId, List<String> colors) {
        final Level level = self.getLevel();
        if (level == null || level.isClientSide) {
            return;
        }

        BlockPos bottom = findStackBottom(self);
        BlockPos current = bottom;

        while (level.getBlockState(current).getBlock() instanceof PillarBlock) {
            BlockEntity be = level.getBlockEntity(current);
            if (be instanceof PillarBlockEntity pillarBE) {
                pillarBE.pillarId = stackId;
                pillarBE.particleColors = colors != null
                        ? new ArrayList<>(colors)
                        : null;
                pillarBE.colorsInitialized = (colors != null && !colors.isEmpty());
                pillarBE.particleColorCounter = 0;
                pillarBE.lastParticleTick = 0;
                pillarBE.setChanged();

                level.sendBlockUpdated(
                        current,
                        level.getBlockState(current),
                        level.getBlockState(current),
                        3);
            }
            current = current.above();
        }
    }

    static PillarBlockEntity getTopPillar(PillarBlockEntity self) {
        final Level level = self.getLevel();
        final BlockPos worldPosition = self.getBlockPos();
        if (level == null) {
            return self;
        }
        BlockPos current = worldPosition;
        try {
            while (level.getBlockState(current.above()).getBlock() instanceof PillarBlock &&
                    !(level.getBlockState(current.above()).getBlock() instanceof AshenKingPillarBlock)) {
                current = current.above();
            }
            BlockEntity be = level.getBlockEntity(current);
            if (be instanceof PillarBlockEntity topPillar) {
                return topPillar;
            }
        } catch (Exception ignored) {
            // the stack walk failed; this pillar counts as its own top
        }
        return self;
    }
}
