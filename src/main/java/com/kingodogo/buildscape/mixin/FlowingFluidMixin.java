package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.block.HollowLogBlock;
import com.kingodogo.buildscape.block.HollowPipeBlock;
import com.kingodogo.buildscape.block.HollowLogBlockEntity;
import com.kingodogo.buildscape.pipe.transport.PipeOutletWater;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlowingFluid.class)
public abstract class FlowingFluidMixin {

    @Shadow
    private boolean canPassThroughWall(Direction direction, BlockGetter level, BlockPos fromPos,
                                       BlockState fromState, BlockPos toPos, BlockState toState) {
        throw new AssertionError();
    }

    @Inject(method = "getNewLiquid", at = @At("RETURN"), cancellable = true)
    private void buildscape$supplyWaterFromPipe(LevelReader level, BlockPos pos, BlockState state,
                                               CallbackInfoReturnable<FluidState> cir) {
        if (!((FlowingFluid) (Object) this).isSame(Fluids.WATER)
                || state.getBlock() instanceof HollowPipeBlock || state.getBlock() instanceof HollowLogBlock) return;
        FluidState vanilla = cir.getReturnValue();
        if (vanilla.isSource() || (!vanilla.isEmpty() && vanilla.getValue(FlowingFluid.FALLING))) return;

        int amount = vanilla.getAmount();
        // Query the supply only during fluid simulation; pipes remain empty to the global fluid/render APIs.
        for (Direction direction : Direction.values()) {
            if (direction == Direction.DOWN) continue;
            BlockPos pipePos = pos.relative(direction);
            BlockState pipe = level.getBlockState(pipePos);
            if (!(pipe.getBlock() instanceof HollowPipeBlock)
                    || !(level.getBlockEntity(pipePos) instanceof HollowLogBlockEntity entity)) continue;
            int supply = PipeOutletWater.amount(pipe, entity.getPipeFlowState(), direction.getOpposite());
            if (supply == 0 || !canPassThroughWall(direction, level, pos, state, pipePos, pipe)) continue;
            if (direction == Direction.UP) {
                cir.setReturnValue(Fluids.WATER.getFlowing(8, true));
                return;
            }
            amount = Math.max(amount, supply);
        }
        if (amount > vanilla.getAmount()) cir.setReturnValue(Fluids.WATER.getFlowing(amount, false));
    }

    @Inject(method = "canSpreadTo", at = @At("HEAD"), cancellable = true)
    private void buildscape$strictHollowFluidSpreading(
            BlockGetter level,
            BlockPos fromPos,
            BlockState fromBlockState,
            Direction direction,
            BlockPos toPos,
            BlockState toBlockState,
            FluidState toFluidState,
            Fluid fluid,
            CallbackInfoReturnable<Boolean> cir
    ) {
        // 1. Spreading OUT of a Hollow Pipe: strictly only allowed through open hollow endpoints
        if (fromBlockState.getBlock() instanceof HollowPipeBlock) {
            if (!HollowPipeBlock.isOpenEndpoint(fromBlockState, direction)) {
                cir.setReturnValue(false);
                return;
            }
        }
        // 2. Spreading OUT of a Hollow Log: strictly only allowed through open hollow ends
        else if (fromBlockState.getBlock() instanceof HollowLogBlock) {
            if (!HollowLogBlock.isOpenEnd(fromBlockState, direction)) {
                cir.setReturnValue(false);
                return;
            }
        }

        // 3. Spreading INTO a Hollow Pipe: strictly only allowed into open hollow endpoints
        if (toBlockState.getBlock() instanceof HollowPipeBlock) {
            if (!HollowPipeBlock.isOpenEndpoint(toBlockState, direction.getOpposite())) {
                cir.setReturnValue(false);
                return;
            }
        }
        // 4. Spreading INTO a Hollow Log: strictly only allowed into open hollow ends
        else if (toBlockState.getBlock() instanceof HollowLogBlock) {
            if (!HollowLogBlock.isOpenEnd(toBlockState, direction.getOpposite())) {
                cir.setReturnValue(false);
                return;
            }
        }
    }
}
