package com.kingodogo.buildscape.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

public class MangroveLeaveVineDecorator extends TreeDecorator {

    public static final Codec<MangroveLeaveVineDecorator> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    Codec.floatRange(0.0F, 1.0F)
                            .fieldOf("probability")
                            .forGetter(decorator -> decorator.probability))
            .apply(instance, MangroveLeaveVineDecorator::new));

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
            List<BlockPos> leavesPositions) {
        leavesPositions.forEach(pos -> {
            if (random.nextFloat() < probability) {
                BlockPos belowPos = pos.below();
                BlockState vine = Blocks.VINE.defaultBlockState().setValue(VineBlock.UP, true);
                if (level.isStateAtPosition(belowPos, BlockState::isAir)
                        && level instanceof LevelReader reader
                        && vine.canSurvive(reader, belowPos)) {
                    blockSetter.accept(belowPos, vine);
                }
            }
        });
    }
}
