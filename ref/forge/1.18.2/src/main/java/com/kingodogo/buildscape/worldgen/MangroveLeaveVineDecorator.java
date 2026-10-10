package com.kingodogo.buildscape.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

public class MangroveLeaveVineDecorator extends TreeDecorator {

    public static final Codec<MangroveLeaveVineDecorator> CODEC =
            RecordCodecBuilder.create(instance ->
                    instance
                            .group(
                                    Codec.floatRange(0.0F, 1.0F)
                                            .fieldOf("probability")
                                            .forGetter(decorator -> decorator.probability)
                            )
                            .apply(instance, MangroveLeaveVineDecorator::new)
            );

    private final float probability;

    public MangroveLeaveVineDecorator(float probability) {
        this.probability = probability;
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return ModTreeDecoratorTypes.MANGROVE_LEAVE_VINE.get();
    }

    @Override
    public void place(
            LevelSimulatedReader level,
            BiConsumer<BlockPos, BlockState> blockSetter,
            Random random,
            List<BlockPos> logPositions,
            List<BlockPos> leavesPositions
    ) {
        leavesPositions.forEach(pos -> {
            if (random.nextFloat() < probability) {
                BlockPos belowPos = pos.below();
                if (level.isStateAtPosition(belowPos, BlockState::isAir)) {
                    blockSetter.accept(belowPos, Blocks.VINE.defaultBlockState());
                }
            }
        });
    }
}
