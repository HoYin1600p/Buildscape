package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
public final class WeatheringBlockLogic {
    public static final int PRESSURE_PLATE_MAX_WEIGHT = 150;

    private WeatheringBlockLogic() {}

    public static void randomTick(BlockState state, ServerLevel level, BlockPos pos) {
        CopperOxidationHandler.tryOxidize(level, pos, state);
    }
}
