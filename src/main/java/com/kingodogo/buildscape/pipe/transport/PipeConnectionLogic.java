package com.kingodogo.buildscape.pipe.transport;

import com.kingodogo.buildscape.block.HollowPipeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
public final class PipeConnectionLogic {

    private PipeConnectionLogic() {}
    public static void onLogNeighborChanged(Level level, BlockPos pos, BlockState state, BlockPos neighborPos) {
        if (!level.isClientSide()) {
            HollowPipeTransportManager.onNeighborChanged(level, pos, state, neighborPos);
        }
    }
    public static BlockState updatePipeConnections(BlockGetter level, BlockPos pos, BlockState state) {
        return HollowPipeBlock.updateConnections(level, pos, state);
    }
    public static boolean shouldConnect(BlockGetter level, BlockPos pos, Direction dir) {
        return PipeFluidTransport.isTopologyConnected(level, pos, dir);
    }
}
