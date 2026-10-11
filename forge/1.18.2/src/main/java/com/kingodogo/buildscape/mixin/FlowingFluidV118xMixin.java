package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.block.HollowLogBlock;
import com.kingodogo.buildscape.block.HollowPipeBlock;
import com.kingodogo.buildscape.fluid.ForgeExperienceFluids;
import com.kingodogo.buildscape.mixinsupport.FluidConduitSupply;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.18.2 counterpart of the neutral FlowingFluidMixin, which targets the 26.2 signatures
 * (static canPassThroughWall, ServerLevel getNewLiquid). The pipe supply logic is shared
 * through FluidConduitSupply; the hollow-block spreading rules match the 1.18.2 reference.
 */
@Mixin(FlowingFluid.class)
public abstract class FlowingFluidV118xMixin {

    @Shadow
    private boolean canPassThroughWall(Direction direction, BlockGetter level, BlockPos fromPos,
                                       BlockState fromState, BlockPos toPos, BlockState toState) {
        throw new AssertionError();
    }

    @Inject(method = "getNewLiquid", at = @At("RETURN"), cancellable = true)
    private void buildscape$supplyFluidFromConduit(LevelReader level, BlockPos pos, BlockState state,
                                                   CallbackInfoReturnable<FluidState> cir) {
        cir.setReturnValue(FluidConduitSupply.augment((FlowingFluid) (Object) this, level, pos, state,
                cir.getReturnValue(), (direction, fromPos, fromState, toPos, toState) ->
                        canPassThroughWall(direction, level, fromPos, fromState, toPos, toState)));
    }

    @Inject(method = "canSpreadTo", at = @At("HEAD"), cancellable = true)
    private void buildscape$strictHollowFluidSpreading(BlockGetter level, BlockPos fromPos, BlockState fromBlockState,
                                                       Direction direction, BlockPos toPos, BlockState toBlockState,
                                                       FluidState toFluidState, Fluid fluid,
                                                       CallbackInfoReturnable<Boolean> cir) {
        if (ForgeExperienceFluids.cannotMix(fluid, toFluidState.getType())) {
            cir.setReturnValue(false);
            return;
        }
        if (fromBlockState.getBlock() instanceof HollowLogBlock || toBlockState.getBlock() instanceof HollowLogBlock) {
            cir.setReturnValue(false);
            return;
        }
        if (fromBlockState.getBlock() instanceof HollowPipeBlock
                && !HollowPipeBlock.isOpenEndpoint(fromBlockState, direction)) {
            cir.setReturnValue(false);
            return;
        }
        if (toBlockState.getBlock() instanceof HollowPipeBlock
                && !HollowPipeBlock.isOpenEndpoint(toBlockState, direction.getOpposite())) {
            cir.setReturnValue(false);
        }
    }
}
