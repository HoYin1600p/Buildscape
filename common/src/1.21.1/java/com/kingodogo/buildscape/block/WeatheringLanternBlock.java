package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.RandomSource;

public class WeatheringLanternBlock extends LanternBlock {
    public WeatheringLanternBlock(Properties properties) {
        super(properties.randomTicks());
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        com.kingodogo.buildscape.adapter.v121x.RestoredCopperOxidation.tryOxidize(level, pos, state);
    }
}
