package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Random;

public class WeatheringVerticalSlabBlock extends VerticalSlabBlock {
    public WeatheringVerticalSlabBlock(Block baseBlock, Properties properties) {
        super(baseBlock, properties.randomTicks());
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        CopperOxidationHandler.tryOxidize(level, pos, state);
    }
}
