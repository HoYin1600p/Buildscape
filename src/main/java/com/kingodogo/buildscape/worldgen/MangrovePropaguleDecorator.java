package com.kingodogo.buildscape.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

public class MangrovePropaguleDecorator extends TreeDecorator {

    public static final Codec<MangrovePropaguleDecorator> CODEC =
            RecordCodecBuilder.create(instance ->
                    instance
                            .group(
                                    Codec.floatRange(0.0F, 1.0F)
                                            .fieldOf("probability")
                                            .forGetter(decorator -> decorator.probability),
                                    IntProvider.codec(0, 16)
                                            .fieldOf("exclusion_radius_xz")
                                            .forGetter(decorator -> decorator.exclusionRadiusXZ),
                                    IntProvider.codec(0, 16)
                                            .fieldOf("exclusion_radius_y")
                                            .forGetter(decorator -> decorator.exclusionRadiusY),
                                    Codec.intRange(0, 16)
                                            .fieldOf("required_empty_blocks")
                                            .forGetter(decorator -> decorator.requiredEmptyBlocks)
                            )
                            .apply(instance, MangrovePropaguleDecorator::new)
            );

    private final float probability;
    private final IntProvider exclusionRadiusXZ;
    private final IntProvider exclusionRadiusY;
    private final int requiredEmptyBlocks;

    public MangrovePropaguleDecorator(
            float probability,
            IntProvider exclusionRadiusXZ,
            IntProvider exclusionRadiusY,
            int requiredEmptyBlocks
    ) {
        this.probability = probability;
        this.exclusionRadiusXZ = exclusionRadiusXZ;
        this.exclusionRadiusY = exclusionRadiusY;
        this.requiredEmptyBlocks = requiredEmptyBlocks;
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return ModTreeDecoratorTypes.MANGROVE_PROPAGULE.get();
    }

    @Override
    public void place(
            LevelSimulatedReader level,
            BiConsumer<BlockPos, BlockState> blockSetter,
            Random random,
            List<BlockPos> logPositions,
            List<BlockPos> leavesPositions
    ) {
        if (leavesPositions.isEmpty()) {
            return;
        }

        int propagulesToPlace = 2 + random.nextInt(3);

        java.util.Collections.shuffle(leavesPositions, random);

        int placed = 0;
        for (BlockPos leafPos : leavesPositions) {
            if (placed >= propagulesToPlace) {
                break;
            }

            BlockPos belowPos = leafPos.below();

            if (
                    level.isStateAtPosition(
                            belowPos,
                            state -> state.isAir() || state.is(Blocks.WATER)
                    )
            ) {
                boolean canPlace = true;
                for (int i = 1; i <= requiredEmptyBlocks; i++) {
                    BlockPos checkPos = belowPos.below(i);
                    if (
                            !level.isStateAtPosition(
                                    checkPos,
                                    state -> state.isAir() || state.is(Blocks.WATER)
                            )
                    ) {
                        canPlace = false;
                        break;
                    }
                }

                if (canPlace) {
                    int age = random.nextInt(5);
                    BlockState propaguleState =
                            com.kingodogo.buildscape.block.ModBlocks.MANGROVE_PROPAGULE.get()
                                    .defaultBlockState()
                                    .setValue(
                                            com.kingodogo.buildscape.block.MangrovePropaguleBlock.AGE,
                                            age
                                    )
                                    .setValue(
                                            com.kingodogo.buildscape.block.MangrovePropaguleBlock.HANGING,
                                            true
                                    );

                    blockSetter.accept(belowPos, propaguleState);
                    placed++;
                }
            }
        }
    }
}
