package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.feature.copper.BuildscapeWeatheringCopper;
import com.kingodogo.buildscape.feature.copper.CopperOxidationHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Random;

public class WeatheringClimbableChainBlock extends ClimbableChainBlock implements BuildscapeWeatheringCopper {
    public WeatheringClimbableChainBlock(BlockBehaviour.Properties properties) {
        super(properties.randomTicks());
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return CopperOxidationHandler.canOxidize(state);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        this.onRandomTick(state, level, pos, random);
    }
}
