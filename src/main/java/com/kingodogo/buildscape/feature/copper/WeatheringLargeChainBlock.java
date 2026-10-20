package com.kingodogo.buildscape.feature.copper;

import com.kingodogo.buildscape.block.LargeChainBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Random;

public class WeatheringLargeChainBlock extends LargeChainBlock implements BuildscapeWeatheringCopper {
    public WeatheringLargeChainBlock(BlockBehaviour.Properties properties) {
        super(properties.randomTicks());
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
