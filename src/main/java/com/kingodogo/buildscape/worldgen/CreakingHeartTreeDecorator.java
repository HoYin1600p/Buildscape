package com.kingodogo.buildscape.worldgen;

import com.kingodogo.buildscape.block.ModBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

public class CreakingHeartTreeDecorator extends TreeDecorator {
    public static final Codec<CreakingHeartTreeDecorator> CODEC = Codec.unit(CreakingHeartTreeDecorator::new);
    public static final CreakingHeartTreeDecorator INSTANCE = new CreakingHeartTreeDecorator();

    @Override
    protected TreeDecoratorType<?> type() {
        return ModTreeDecoratorTypes.CREAKING_HEART.get();
    }

    @Override
    public void place(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, Random random, List<BlockPos> logPositions, List<BlockPos> leafPositions) {
        if (logPositions.isEmpty()) {
            return;
        }

        // Guaranteed single Creaking Heart in the middle of the tree trunk
        int midIndex = logPositions.size() / 2;
        BlockPos targetPos = logPositions.get(midIndex);

        blockSetter.accept(targetPos, ModBlocks.CREAKING_HEART.get().defaultBlockState());
    }
}
