package com.kingodogo.buildscape.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;

public class RandomStateProvider extends BlockStateProvider {

    public static final Codec<RandomStateProvider> CODEC =
            RecordCodecBuilder.create(instance ->
                    instance
                            .group(
                                    BlockState.CODEC.listOf()
                                            .fieldOf("states")
                                            .forGetter(provider -> provider.states)
                            )
                            .apply(instance, RandomStateProvider::new)
            );

    private final List<BlockState> states;

    public RandomStateProvider(List<BlockState> states) {
        this.states = states;
    }

    @Override
    protected BlockStateProviderType<?> type() {
        return ModBlockStateProviderTypes.RANDOM_STATE.get();
    }

    @Override
    public BlockState getState(Random random, BlockPos pos) {
        if (states.isEmpty()) {
            return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
        }
        return states.get(random.nextInt(states.size()));
    }
}
