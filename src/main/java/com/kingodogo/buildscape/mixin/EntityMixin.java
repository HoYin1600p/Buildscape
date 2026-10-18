package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.block.ExperienceFluidBlock;
import com.kingodogo.buildscape.fluid.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "doWaterSplashEffect", at = @At("HEAD"), cancellable = true)
    private void onDoWaterSplashEffect(CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (isEntityInExperienceFluid(self)) {
            ci.cancel();
        }
    }

    private static boolean isEntityInExperienceFluid(Entity entity) {
        if (entity == null || entity.level == null) return false;
        BlockPos pos = entity.blockPosition();
        return isExperienceFluid(entity.level.getFluidState(pos), entity.level.getBlockState(pos)) ||
               isExperienceFluid(entity.level.getFluidState(pos.above()), entity.level.getBlockState(pos.above())) ||
               isExperienceFluid(entity.level.getFluidState(pos.below()), entity.level.getBlockState(pos.below()));
    }

    private static boolean isExperienceFluid(FluidState fluidState, BlockState blockState) {
        return (fluidState != null && !fluidState.isEmpty() && ModFluids.isExperience(fluidState.getType()))
                || (blockState != null && blockState.getBlock() instanceof ExperienceFluidBlock);
    }
}
