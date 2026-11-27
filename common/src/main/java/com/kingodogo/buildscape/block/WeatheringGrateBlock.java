package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class WeatheringGrateBlock extends WaterloggableGrateBlock {
    public WeatheringGrateBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        com.kingodogo.buildscape.adapter.RestoredCopperOxidation.tryOxidize(level, pos, state);
    }
}
