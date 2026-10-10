package com.kingodogo.buildscape.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

public class MangroveMossCarpetDecorator extends TreeDecorator {

    public static final Codec<MangroveMossCarpetDecorator> CODEC =
            RecordCodecBuilder.create(instance ->
                    instance
                            .group(
                                    Codec.floatRange(0.0F, 1.0F)
                                            .fieldOf("probability")
                                            .forGetter(decorator -> decorator.probability)
                            )
                            .apply(instance, MangroveMossCarpetDecorator::new)
            );

    private final float probability;

    public MangroveMossCarpetDecorator(float probability) {
        this.probability = probability;
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return ModTreeDecoratorTypes.MANGROVE_MOSS_CARPET.get();
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

        Set<BlockPos> checkedPositions = new HashSet<>();
        for (int x = -10; x <= 10; x++) {
            for (int z = -10; z <= 10; z++) {
                for (int y = -5; y <= 5; y++) {
                    BlockPos checkPos = basePos.offset(x, y, z);

                    if (checkedPositions.contains(checkPos)) {
                        continue;
                    }
                    checkedPositions.add(checkPos);

                    boolean isRoot = level.isStateAtPosition(
                            checkPos,
                            state ->
                                    state.is(
                                            com.kingodogo.buildscape.block.ModBlocks.MANGROVE_ROOTS.get()
                                    ) ||
                                            state.is(
                                                    com.kingodogo.buildscape.block.ModBlocks.MUDDY_MANGROVE_ROOTS.get()
                                            )
                    );

                    if (isRoot) {
                        BlockPos abovePos = checkPos.above();

                        if (random.nextFloat() < probability) {
                            if (level.isStateAtPosition(abovePos, state -> state.isAir())) {
                                blockSetter.accept(
                                        abovePos,
                                        com.kingodogo.buildscape.block.ModBlocks.MOSS_OVERLAY.get()
                                                .defaultBlockState()
                                );
                            }
                        }
                    }
                }
            }
        }
    }
}
