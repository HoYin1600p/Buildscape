package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.mixin.support.FluidConduitSupply;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlowingFluid.class)
public abstract class FlowingFluidMixin {

    @Shadow
    private static boolean canPassThroughWall(Direction direction, BlockGetter level, BlockPos fromPos,
                                       BlockState fromState, BlockPos toPos, BlockState toState) {
        throw new AssertionError();
    }

    @Inject(method = "getNewLiquid(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/level/material/FluidState;", at = @At("RETURN"), cancellable = true)
    private void buildscape$supplyFluidFromConduit26(net.minecraft.server.level.ServerLevel level, BlockPos pos, BlockState state,
                                                    CallbackInfoReturnable<FluidState> cir) {
        cir.setReturnValue(FluidConduitSupply.augment((FlowingFluid) (Object) this, level, pos, state,
                cir.getReturnValue(), (direction, fromPos, fromState, toPos, toState) ->
                        canPassThroughWall(direction, level, fromPos, fromState, toPos, toState)));
    }
}
