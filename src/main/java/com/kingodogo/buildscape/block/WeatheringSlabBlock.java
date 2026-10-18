package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.block.copper.BuildscapeWeatheringCopper;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Random;

public class WeatheringSlabBlock extends SlabBlock implements BuildscapeWeatheringCopper {
    public WeatheringSlabBlock(BlockBehaviour.Properties properties) {
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
