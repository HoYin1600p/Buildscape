package com.kingodogo.buildscape.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Random;
import java.util.function.BiConsumer;

import net.minecraft.core.BlockPos;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

public class MangroveRandomSpreadFoliagePlacer extends FoliagePlacer {

    public static final Codec<MangroveRandomSpreadFoliagePlacer> CODEC =
            RecordCodecBuilder.create(instance ->
                    foliagePlacerParts(instance)
                            .and(
                                    IntProvider.codec(0, 16)
                                            .fieldOf("foliage_height")
                                            .forGetter(placer -> placer.foliageHeight)
                            )
                            .and(
                                    Codec.intRange(0, 512)
                                            .fieldOf("leaf_placement_attempts")
                                            .forGetter(placer -> placer.leafPlacementAttempts)
                            )
                            .apply(instance, MangroveRandomSpreadFoliagePlacer::new)
            );

    private final IntProvider foliageHeight;
    private final int leafPlacementAttempts;

    public MangroveRandomSpreadFoliagePlacer(
            IntProvider radius,
            IntProvider offset,
            IntProvider foliageHeight,
            int leafPlacementAttempts
    ) {
        super(radius, offset);
        this.foliageHeight = foliageHeight;
        this.leafPlacementAttempts = leafPlacementAttempts;
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return ModFoliagePlacerTypes.MANGROVE_RANDOM_SPREAD.get();
    }

    @Override
    protected void createFoliage(
            LevelSimulatedReader level,
            BiConsumer<BlockPos, BlockState> blockSetter,
            Random random,
            TreeConfiguration config,
            int maxFreeTreeHeight,
            FoliageAttachment attachment,
            int foliageHeight,
            int foliageRadius,
            int offset
    ) {
        BlockPos centerPos = attachment.pos();
        int actualFoliageHeight = this.foliageHeight.sample(random);

        java.util.Set<BlockPos> placedLeaves = new java.util.HashSet<>();

        for (int attempt = 0; attempt < leafPlacementAttempts; attempt++) {
            double angle1 = random.nextDouble() * 2 * Math.PI;
            double angle2 = random.nextDouble() * Math.PI;
            double radius = foliageRadius * Math.sqrt(random.nextDouble());

            radius += (random.nextDouble() - 0.5) * 0.5;
            radius = Math.max(0, Math.min(radius, foliageRadius + 1));

            int x = (int) (centerPos.getX() +
                    radius * Math.sin(angle2) * Math.cos(angle1));
            int y = (int) (centerPos.getY() +
                    radius *
                            Math.cos(angle2) *
                            (actualFoliageHeight / (double) foliageRadius));
            int z = (int) (centerPos.getZ() +
                    radius * Math.sin(angle2) * Math.sin(angle1));

            y = Math.max(
                    centerPos.getY(),
                    Math.min(centerPos.getY() + actualFoliageHeight - 1, y)
            );

            BlockPos leafPos = new BlockPos(x, y, z);

            if (leafPos.equals(centerPos)) {
                continue;
            }

            if (isConnectedToLogOrLeaf(level, leafPos, placedLeaves, config)) {
                if (level.isStateAtPosition(leafPos, BlockState::isAir)) {
                    blockSetter.accept(
                            leafPos,
                            config.foliageProvider.getState(random, leafPos)
                    );
                    placedLeaves.add(leafPos);
                }
            }
        }

        java.util.List<BlockPos> candidates = new java.util.ArrayList<>();
        for (int yOffset = -1; yOffset < actualFoliageHeight + 1; yOffset++) {
            int currentY = centerPos.getY() + yOffset;
            int searchRadius = foliageRadius + 2;

            for (int xOffset = -searchRadius; xOffset <= searchRadius; xOffset++) {
                for (int zOffset = -searchRadius; zOffset <= searchRadius; zOffset++) {
                    BlockPos leafPos = new BlockPos(
                            centerPos.getX() + xOffset,
                            currentY,
                            centerPos.getZ() + zOffset
                    );

                    if (
                            level.isStateAtPosition(leafPos, BlockState::isAir) &&
                                    !placedLeaves.contains(leafPos)
                    ) {
                        int adjacentLeaves = countAdjacentLeaves(
                                level,
                                leafPos,
                                config,
                                placedLeaves
                        );
                        if (adjacentLeaves >= 4 && random.nextFloat() < 0.6F) {
                            candidates.add(leafPos);
                        }
                    }
                }
            }
        }

        java.util.Collections.shuffle(candidates, random);
        for (BlockPos candidate : candidates) {
            if (isConnectedToLogOrLeaf(level, candidate, placedLeaves, config)) {
                blockSetter.accept(
                        candidate,
                        config.foliageProvider.getState(random, candidate)
                );
                placedLeaves.add(candidate);
            }
        }

        java.util.List<BlockPos> extensionCandidates = new java.util.ArrayList<>(
                placedLeaves
        );
        java.util.Collections.shuffle(extensionCandidates, random);

        for (int i = 0; i < Math.min(extensionCandidates.size() / 4, 20); i++) {
            BlockPos baseLeaf = extensionCandidates.get(i);

            for (int dir = 0; dir < 6; dir++) {
                if (random.nextFloat() < 0.3F) continue;

                BlockPos extensionPos = baseLeaf.relative(
                        net.minecraft.core.Direction.from3DDataValue(dir)
                );

                int distFromCenter = (int) Math.sqrt(
                        (extensionPos.getX() - centerPos.getX()) *
                                (extensionPos.getX() - centerPos.getX()) +
                                (extensionPos.getZ() - centerPos.getZ()) *
                                        (extensionPos.getZ() - centerPos.getZ()) +
                                (extensionPos.getY() - centerPos.getY()) *
                                        (extensionPos.getY() - centerPos.getY())
                );

                if (
                        distFromCenter <= foliageRadius + 2 &&
                                !placedLeaves.contains(extensionPos) &&
                                level.isStateAtPosition(extensionPos, BlockState::isAir) &&
                                isConnectedToLogOrLeaf(level, extensionPos, placedLeaves, config)
                ) {
                    blockSetter.accept(
                            extensionPos,
                            config.foliageProvider.getState(random, extensionPos)
                    );
                    placedLeaves.add(extensionPos);
                    break;
                }
            }
        }
    }

    private boolean isConnectedToLogOrLeaf(
            LevelSimulatedReader level,
            BlockPos pos,
            java.util.Set<BlockPos> placedLeaves,
            TreeConfiguration config
    ) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;

                    BlockPos checkPos = pos.offset(dx, dy, dz);

                    if (
                            level.isStateAtPosition(
                                    checkPos,
                                    state ->
                                            state.is(net.minecraft.tags.BlockTags.LOGS) ||
                                                    state.getBlock() ==
                                                            config.trunkProvider.getState(null, null).getBlock()
                            )
                    ) {
                        return true;
                    }

                    if (placedLeaves.contains(checkPos)) {
                        return true;
                    }

                    if (
                            level.isStateAtPosition(
                                    checkPos,
                                    state ->
                                            state.getBlock() ==
                                                    config.foliageProvider.getState(null, null).getBlock()
                            )
                    ) {
                        return true;
                    }
                }
            }
        }

        for (int dx = -6; dx <= 6; dx++) {
            for (int dy = -6; dy <= 6; dy++) {
                for (int dz = -6; dz <= 6; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    int distanceSq = dx * dx + dy * dy + dz * dz;
                    if (distanceSq > 36) continue;

                    BlockPos checkPos = pos.offset(dx, dy, dz);
                    if (
                            level.isStateAtPosition(
                                    checkPos,
                                    state ->
                                            state.is(net.minecraft.tags.BlockTags.LOGS) ||
                                                    state.getBlock() ==
                                                            config.trunkProvider.getState(null, null).getBlock()
                            )
                    ) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private int countAdjacentLeaves(
            LevelSimulatedReader level,
            BlockPos pos,
            TreeConfiguration config,
            java.util.Set<BlockPos> placedLeaves
    ) {
        int count = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;

                    BlockPos checkPos = pos.offset(dx, dy, dz);
                    if (placedLeaves.contains(checkPos)) {
                        count++;
                    } else if (
                            level.isStateAtPosition(
                                    checkPos,
                                    state ->
                                            state.getBlock() ==
                                                    config.foliageProvider.getState(null, null).getBlock()
                            )
                    ) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    @Override
    protected boolean shouldSkipLocation(
            Random random,
            int baseHeight,
            int x,
            int y,
            int z,
            boolean large
    ) {
        if (x == 0 && z == 0) {
            return y == 0;
        }
        return false;
    }

    @Override
    public int foliageHeight(
            Random random,
            int height,
            TreeConfiguration config
    ) {
        return this.foliageHeight.sample(random);
    }
}
