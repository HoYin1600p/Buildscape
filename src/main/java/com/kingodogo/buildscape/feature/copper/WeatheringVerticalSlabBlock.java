package com.kingodogo.buildscape.feature.copper;

import com.kingodogo.buildscape.block.VerticalSlabBlock;


import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Random;

public class WeatheringVerticalSlabBlock extends VerticalSlabBlock implements BuildscapeWeatheringCopper {
    public WeatheringVerticalSlabBlock(Block baseBlock, Properties properties) {
        super(baseBlock, properties.randomTicks());
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return this.canWeather(state);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        this.onRandomTick(state, level, pos, random);
    }
}
