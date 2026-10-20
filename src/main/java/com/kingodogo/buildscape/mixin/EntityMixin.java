package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.block.decor.CushionBlock;
import com.kingodogo.buildscape.block.decor.CushionSupport;
import com.kingodogo.buildscape.block.water.ExperienceFluidBlock;
import com.kingodogo.buildscape.registry.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "doWaterSplashEffect", at = @At("HEAD"), cancellable = true)
    private void onDoWaterSplashEffect(CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (isEntityInExperienceFluid(self)) {
            ci.cancel();
        }
    }

    @Inject(method = "getOnPos", at = @At("RETURN"), cancellable = true)
    private void buildscape$cushionOnPos(CallbackInfoReturnable<BlockPos> cir) {
        Entity self = (Entity) (Object) this;
        BlockPos vanilla = cir.getReturnValue();
        BlockPos fixed = CushionSupport.supportingPos(vanilla, self.getX(), self.getY(), self.getZ(),
                pos -> self.level.getBlockState(pos).getBlock() instanceof CushionBlock);
        if (fixed != vanilla) {
            cir.setReturnValue(fixed);
        }
    }

    private static boolean isEntityInExperienceFluid(Entity entity) {
        if (entity == null || entity.level == null) {
            return false;
        }
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
