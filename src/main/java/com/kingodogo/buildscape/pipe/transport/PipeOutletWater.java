package com.kingodogo.buildscape.pipe.transport;

import com.kingodogo.buildscape.block.HollowLogBlock;
import com.kingodogo.buildscape.block.HollowPipeBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public final class PipeOutletWater {
    private PipeOutletWater() {}

    public static int amount(BlockState pipe, PipeFlowState flow, Direction exit) {
        boolean open;
        boolean containedFluid;
        if (pipe.getBlock() instanceof HollowPipeBlock) {
            open = HollowPipeBlock.isOpenEndpoint(pipe, exit);
            containedFluid = pipe.getValue(HollowPipeBlock.WATERLOGGED)
                    || pipe.getValue(HollowPipeBlock.LAVA_LOGGED);
        } else if (pipe.getBlock() instanceof HollowLogBlock) {
            open = HollowLogBlock.isOpenEnd(pipe, exit);
            containedFluid = pipe.getValue(HollowLogBlock.WATERLOGGED)
                    || pipe.getValue(HollowLogBlock.LAVA_LOGGED);
        } else {
            return 0;
        }
        return amount(flow, exit, open, (flow != null && flow.hasFluid()) || containedFluid);
    }

    static int amount(PipeFlowState flow, Direction exit, boolean open, boolean wet) {
        if (!open || !wet || flow == null || !flow.hasFluid() || !flow.hasFlowDirection(exit)
                || exit == Direction.UP) return 0;
        if (exit == Direction.DOWN) return 8;
        return Math.max(0, WaterPipeTransport.MAX_HORIZONTAL_FLOW - Math.max(0, flow.getDistance()));
    }
}
