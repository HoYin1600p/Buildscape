package com.kingodogo.buildscape.feature.pipe.transport;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import javax.annotation.Nullable;

/**
 * Read-only view of the pipe layout that the flow calculation uses: which positions are pipes, which sides connect,
 * which are open ends, and where fluid sources and bubble columns are. It lets the calculation run on a snapshot
 * instead of the live world.
 */
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
