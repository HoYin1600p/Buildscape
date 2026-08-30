package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.block.HollowLogBlock;
import com.kingodogo.buildscape.block.HollowPipeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlowingFluid.class)
public abstract class FlowingFluidMixin {

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
