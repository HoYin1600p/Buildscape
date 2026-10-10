package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
public class WeatheringGrateBlock extends WaterloggableGrateBlock implements ICommonRandomTick {

    public WeatheringGrateBlock(BlockBehaviour.Properties properties) {
        super(properties.randomTicks());
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    public void onRandomTick(BlockState state, ServerLevel level, BlockPos pos) {
        CopperOxidationHandler.tryOxidize(level, pos, state);
    }
}
