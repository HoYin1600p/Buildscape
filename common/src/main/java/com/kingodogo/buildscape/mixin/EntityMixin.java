package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.block.ExperienceFluidBlock;
import com.kingodogo.buildscape.fluid.ModFluids;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "move", at = @At("TAIL"))
    private void onMove(
            net.minecraft.world.entity.MoverType type,
            Vec3 movement,
            CallbackInfo ci
    ) {
        Entity self = (Entity) (Object) this;

        if (self instanceof Player) {
            return;
        }

        Level level = Services.PLATFORM.getEntityLevel(self);
        if (level != null) {
            BlockPos pos = self.blockPosition();
            if (isChainBlock(level.getBlockState(pos))) {
                self.setOnGround(false);
            }
        }
    }

    @Inject(method = "doWaterSplashEffect", at = @At("HEAD"), cancellable = true)
    private void onDoWaterSplashEffect(CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (isEntityInExperienceFluid(self)) {
            ci.cancel();
        }
    }

    private static boolean isEntityInExperienceFluid(Entity entity) {
        Level level = Services.PLATFORM.getEntityLevel(entity);
        if (entity == null || level == null) return false;
        BlockPos pos = entity.blockPosition();
        return isExperienceFluid(level.getFluidState(pos), level.getBlockState(pos)) ||
               isExperienceFluid(level.getFluidState(pos.above()), level.getBlockState(pos.above())) ||
               isExperienceFluid(level.getFluidState(pos.below()), level.getBlockState(pos.below()));
    }

    private static boolean isExperienceFluid(FluidState fluidState, BlockState blockState) {
        if (fluidState != null && !fluidState.isEmpty()) {
            Fluid fluid = fluidState.getType();
            if (ModFluids.isExperience(fluid)) {
                return true;
            }
            CommonId reg = Services.PLATFORM.getFluidId(fluid);
            if (reg != null && (reg.getPath().contains("experience") || reg.getPath().contains("xp"))) {
                return true;
            }
        }
        if (blockState != null && !blockState.isAir()) {
            if (blockState.getBlock() instanceof ExperienceFluidBlock) {
                return true;
            }
            CommonId reg = Services.PLATFORM.getBlockId(blockState.getBlock());
            if (reg != null && (reg.getPath().contains("experience") || reg.getPath().contains("xp"))) {
                return true;
            }
        }
        return false;
    }

    private static boolean isChainBlock(BlockState state) {
        Block block = state.getBlock();
        if (block instanceof ChainBlock) {
            return true;
        }
        CommonId key = Services.PLATFORM.getBlockId(block);
        return key != null && "buildscape".equals(key.getNamespace()) && key.getPath().contains("chain");
    }
}
