package com.kingodogo.buildscape.feature.copper;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.WeightedPressurePlateBlock;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Random;

public class WeatheringPressurePlateBlock extends WeightedPressurePlateBlock implements BuildscapeWeatheringCopper {
    public WeatheringPressurePlateBlock(int maxWeight, Properties properties) {
        super(maxWeight, properties.randomTicks());
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
