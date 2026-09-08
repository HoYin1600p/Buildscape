package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.fluid.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.LavaFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LavaFluid.class)
public abstract class LavaFluidMixin {
    @Inject(method = "spreadTo", at = @At("HEAD"), cancellable = true)
    private void buildscape$preserveExperience(LevelAccessor level, BlockPos pos, BlockState state,
                                              Direction direction, FluidState fluidState, CallbackInfo ci) {
        if (ModFluids.isExperience(level.getFluidState(pos).getType())) ci.cancel();
    }
}
