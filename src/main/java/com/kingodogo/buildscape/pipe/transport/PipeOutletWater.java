package com.kingodogo.buildscape.pipe.transport;

import com.kingodogo.buildscape.block.HollowPipeBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public final class PipeOutletWater {
    private PipeOutletWater() {}

    public static int amount(BlockState pipe, PipeFlowState flow, Direction exit) {
        if (!(pipe.getBlock() instanceof HollowPipeBlock)) return 0;
        return amount(flow, exit, HollowPipeBlock.isOpenEndpoint(pipe, exit),
                pipe.getValue(HollowPipeBlock.WATERLOGGED) || pipe.getValue(HollowPipeBlock.WATER_LEVEL) > 0);
    }

    static int amount(PipeFlowState flow, Direction exit, boolean open, boolean wet) {
        if (!open || !wet || flow == null || !flow.hasWater() || !flow.hasFlowDirection(exit)
                || exit == Direction.UP) return 0;
        if (exit == Direction.DOWN) return 8;
        return Math.max(0, WaterPipeTransport.MAX_HORIZONTAL_FLOW - Math.max(0, flow.getDistance()));
    }
}
