package com.kingodogo.buildscape.pipe.transport;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import javax.annotation.Nullable;

public interface PipeTopologyAccess {

    boolean isHollowPipe(BlockPos pos);

    boolean isConnected(BlockPos pos, Direction dir);

    boolean isOpenEndpoint(BlockPos pos, Direction dir);

    BubbleColumnState getBubbleColumnBase(BlockPos pos);

    @Nullable
    default String getSourceFluidId(BlockPos pos) {
        return isWaterSource(pos) ? PipeFlowState.WATER_FLUID_ID : null;
    }

    default boolean isWaterSource(BlockPos pos) {
        return false;
    }

    default int getInitialWaterFlowDistance(BlockPos pos) {
        return 0;
    }

    @Nullable
    default Direction getSourceInflowDirection(BlockPos pos) {
        return null;
    }
}
