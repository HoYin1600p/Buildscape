package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.block.HollowLogBlock;
import com.kingodogo.buildscape.block.HollowPipeBlock;
import com.kingodogo.buildscape.block.HollowLogBlockEntity;
import com.kingodogo.buildscape.pipe.transport.PipeOutletWater;
import com.kingodogo.buildscape.pipe.transport.WorldPipeTopologyAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlowingFluid.class)
public abstract class FlowingFluidV121xMixin {

    @Shadow
    private boolean canPassThroughWall(Direction direction, BlockGetter level, BlockPos fromPos,
                                       BlockState fromState, BlockPos toPos, BlockState toState) {
        throw new AssertionError();
    }

    @Inject(method = "getNewLiquid(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/level/material/FluidState;",
            at = @At("RETURN"), cancellable = true)
    private void buildscape$supplyFluidFromConduit(Level level, BlockPos pos, BlockState state,
                                                   CallbackInfoReturnable<FluidState> cir) {
        FlowingFluid flowingFluid = (FlowingFluid) (Object) this;
        if (!WorldPipeTopologyAccess.isTransportFluid(flowingFluid)
                || state.getBlock() instanceof HollowPipeBlock || state.getBlock() instanceof HollowLogBlock) return;
        FluidState vanilla = cir.getReturnValue();
        if (vanilla.isSource() || (!vanilla.isEmpty() && vanilla.getValue(FlowingFluid.FALLING))) return;

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
            if (supply == 0 || !canPassThroughWall(direction, level, pos, state, pipePos, pipe)) continue;
            if (direction == Direction.UP) {
                cir.setReturnValue(flowingFluid.getFlowing(8, true));
                return;
            }
            amount = Math.max(amount, supply);
        }
        if (amount > vanilla.getAmount()) cir.setReturnValue(flowingFluid.getFlowing(amount, false));
    }

}

