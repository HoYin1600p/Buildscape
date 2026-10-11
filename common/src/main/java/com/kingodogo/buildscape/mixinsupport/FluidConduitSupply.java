package com.kingodogo.buildscape.mixinsupport;

import com.kingodogo.buildscape.block.HollowLogBlock;
import com.kingodogo.buildscape.block.HollowLogBlockEntity;
import com.kingodogo.buildscape.block.HollowPipeBlock;
import com.kingodogo.buildscape.pipe.transport.PipeOutletWater;
import com.kingodogo.buildscape.pipe.transport.WorldPipeTopologyAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public final class FluidConduitSupply {
    private FluidConduitSupply() {
    }

    @FunctionalInterface
    public interface WallPasser {
        boolean canPass(Direction direction, BlockPos fromPos, BlockState fromState,
                        BlockPos toPos, BlockState toState);
    }

    public static FluidState augment(FlowingFluid flowingFluid, LevelReader level, BlockPos pos,
                                     BlockState state, FluidState vanilla, WallPasser wallPasser) {
        if (!WorldPipeTopologyAccess.isTransportFluid(flowingFluid)
                || state.getBlock() instanceof HollowPipeBlock || state.getBlock() instanceof HollowLogBlock
                || vanilla.isSource() || (!vanilla.isEmpty() && vanilla.getValue(FlowingFluid.FALLING))) {
            return vanilla;
        }

        int amount = vanilla.getAmount();
        for (Direction direction : Direction.values()) {
            if (direction == Direction.DOWN) continue;
            BlockPos pipePos = pos.relative(direction);
            BlockState pipe = level.getBlockState(pipePos);
            if (!(pipe.getBlock() instanceof HollowPipeBlock)
                    || !(level.getBlockEntity(pipePos) instanceof HollowLogBlockEntity entity)) continue;
            var flow = entity.getPipeFlowState();
            if (flow == null || !flow.hasFluid()) continue;
            Fluid suppliedFluid = WorldPipeTopologyAccess.fluidById(flow.getFluidId());
            if (!flowingFluid.isSame(suppliedFluid)) continue;
            int supply = PipeOutletWater.amount(pipe, flow, direction.getOpposite());
            if (supply == 0 || !wallPasser.canPass(direction, pos, state, pipePos, pipe)) continue;
            if (direction == Direction.UP) return flowingFluid.getFlowing(8, true);
            amount = Math.max(amount, supply);
        }
        return amount > vanilla.getAmount() ? flowingFluid.getFlowing(amount, false) : vanilla;
    }
}
