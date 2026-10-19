package com.kingodogo.buildscape.worldgen;

import com.kingodogo.buildscape.registry.ModBlocks;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

import java.util.*;
import java.util.function.BiConsumer;

public class MangroveRootDecorator extends TreeDecorator {

    public static final Codec<MangroveRootDecorator> CODEC =
            RecordCodecBuilder.create(instance ->
                    instance
                            .group(
                                    IntProvider.codec(1, 16)
                                            .fieldOf("max_root_width")
                                            .forGetter(decorator -> decorator.maxRootWidth),
                                    IntProvider.codec(1, 16)
                                            .fieldOf("max_root_length")
                                            .forGetter(decorator -> decorator.maxRootLength),
                                    Codec.floatRange(0.0F, 1.0F)
                                            .fieldOf("random_skew_chance")
                                            .forGetter(decorator -> decorator.randomSkewChance),
                                    IntProvider.codec(0, 16)
                                            .optionalFieldOf("trunk_offset_y", ConstantInt.of(0))
                                            .forGetter(decorator -> decorator.trunkOffsetY)
                            )
                            .apply(instance, MangroveRootDecorator::new)
            );

    private final IntProvider maxRootWidth;
    private final IntProvider maxRootLength;
    private final float randomSkewChance;
    private final IntProvider trunkOffsetY;

    public MangroveRootDecorator(
            IntProvider maxRootWidth,
            IntProvider maxRootLength,
            float randomSkewChance
    ) {
        this(maxRootWidth, maxRootLength, randomSkewChance, ConstantInt.of(0));
    }

    public MangroveRootDecorator(
            IntProvider maxRootWidth,
            IntProvider maxRootLength,
            float randomSkewChance,
            IntProvider trunkOffsetY
    ) {
        this.maxRootWidth = maxRootWidth;
        this.maxRootLength = maxRootLength;
        this.randomSkewChance = randomSkewChance;
        this.trunkOffsetY = trunkOffsetY;
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return ModTreeDecoratorTypes.MANGROVE_ROOT.get();
    }

    int sampleTrunkOffset(Random random) {
        return trunkOffsetY.sample(random);
    }

    @Override
    public void place(
            LevelSimulatedReader level,
            BiConsumer<BlockPos, BlockState> blockSetter,
            Random random,
            List<BlockPos> logPositions,
            List<BlockPos> leavesPositions
    ) {
        if (logPositions.isEmpty()) {
            return;
        }

        BlockPos basePos = logPositions
                .stream()
                .min((a, b) -> Integer.compare(a.getY(), b.getY()))
                .orElse(logPositions.get(0));

        int rootWidth = maxRootWidth.sample(random);
        int rootLength = maxRootLength.sample(random);
        int logBaseLevel = basePos.getY();

        int groundLevel = findGroundLevel(level, basePos);

        int rootStartLevel = logBaseLevel;

        Direction[] cardinalDirections = {
                Direction.NORTH,
                Direction.SOUTH,
                Direction.EAST,
                Direction.WEST,
        };

        for (Direction direction : cardinalDirections) {
            int horizontalDistance = 1 + random.nextInt(Math.min(rootWidth, rootLength));

            List<BlockPos> horizontalRoots = new ArrayList<>();
            for (int i = 1; i <= horizontalDistance; i++) {
                BlockPos rootPos = new BlockPos(
                        basePos.getX() + direction.getStepX() * i,
                        rootStartLevel,
                        basePos.getZ() + direction.getStepZ() * i
                );
                horizontalRoots.add(rootPos);
            }

            if (horizontalRoots.isEmpty()) {
                continue;
            }

            for (BlockPos horizontalPos : horizontalRoots) {
                placeRootBlock(level, blockSetter, random, horizontalPos);
            }

            BlockPos lastPos = horizontalRoots.get(horizontalRoots.size() - 1);
            int downDepth = Math.min(lastPos.getY() - groundLevel, rootLength - horizontalDistance);

            if (downDepth > 0) {
                for (int y = 1; y <= downDepth; y++) {
                    BlockPos rootPos = lastPos.below(y);
                    placeRootBlock(level, blockSetter, random, rootPos);
                }
            }

            if (
                    horizontalRoots.size() >= 2 && random.nextFloat() < randomSkewChance
            ) {
                BlockPos secondLastPos = horizontalRoots.get(
                        horizontalRoots.size() - 2
                );
                int secondDownDepth = Math.min(
                        Math.min(2 + random.nextInt(2), secondLastPos.getY() - groundLevel),
                        rootLength - (horizontalDistance - 1)
                );

                if (secondDownDepth > 0) {
                    for (int y = 1; y <= secondDownDepth; y++) {
                        BlockPos rootPos = secondLastPos.below(y);
                        placeRootBlock(level, blockSetter, random, rootPos);
                    }
                }
            }
        }
    }

    private int findGroundLevel(LevelSimulatedReader level, BlockPos startPos) {
        BlockPos.MutableBlockPos checkPos = startPos.mutable();
        int minY = -64;

        while (checkPos.getY() > minY) {
            final BlockPos.MutableBlockPos finalCheckPos = checkPos.mutable();
            boolean isTerrain = level.isStateAtPosition(finalCheckPos, state -> {
                if (state.isAir() || state.is(Blocks.WATER)) {
                    return false;
                }
                boolean isTerrainBlock =
                        state.is(Blocks.DIRT) ||
                                state.is(Blocks.GRASS_BLOCK) ||
                                state.is(Blocks.STONE) ||
                                state.is(Blocks.SAND) ||
                                state.is(Blocks.GRAVEL) ||
                                state.is(Blocks.CLAY);
                boolean isTreeBlock =
                        state.is(BlockTags.LOGS) ||
                                state.is(
                                        ModBlocks.MANGROVE_ROOTS.get()
                                ) ||
                                state.is(
                                        ModBlocks.MUDDY_MANGROVE_ROOTS.get()
                                );
                return (
                        isTerrainBlock &&
                                !isTreeBlock &&
                                state.getMaterial().isSolid() &&
                                !state.getMaterial().isReplaceable()
                );
            });

            if (isTerrain) {
                return finalCheckPos.getY();
            }
            checkPos.move(Direction.DOWN);
        }

        return Math.max(startPos.getY() - 3, minY);
    }

    private void placeRootBlock(
            LevelSimulatedReader level,
            BiConsumer<BlockPos, BlockState> blockSetter,
            Random random,
            BlockPos rootPos
    ) {
        boolean canPlace = level.isStateAtPosition(rootPos, state -> {
            if (state.is(BlockTags.LOGS)) {
                return false;
            }
            if (
                    state.is(
                            ModBlocks.MANGROVE_ROOTS.get()
                    ) ||
                            state.is(
                                    ModBlocks.MUDDY_MANGROVE_ROOTS.get()
                            )
            ) {
                return true;
            }
            return state.isAir() || state.is(Blocks.WATER);
        });

        if (!canPlace) {
            return;
        }

        BlockState rootState =
                ModBlocks.MANGROVE_ROOTS.get()
                        .defaultBlockState()
                        .setValue(
                                RotatedPillarBlock.AXIS,
                                Direction.Axis.Y
                        );

        blockSetter.accept(rootPos, rootState);
    }
}
