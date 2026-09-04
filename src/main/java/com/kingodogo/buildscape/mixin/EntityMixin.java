package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.block.ExperienceFluidBlock;
import com.kingodogo.buildscape.fluid.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
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

        EntityAccessor accessor = (EntityAccessor) self;
        BlockPos pos = accessor.callBlockPosition();
        if (accessor.getLevel() != null && isChainBlock(accessor.getLevel().getBlockState(pos))) {
            accessor.setOnGround(false);
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
        if (entity == null || entity.level == null) return false;
        BlockPos pos = entity.blockPosition();
        return isExperienceFluid(entity.level.getFluidState(pos), entity.level.getBlockState(pos)) ||
               isExperienceFluid(entity.level.getFluidState(pos.above()), entity.level.getBlockState(pos.above())) ||
               isExperienceFluid(entity.level.getFluidState(pos.below()), entity.level.getBlockState(pos.below()));
    }

    private static boolean isExperienceFluid(FluidState fluidState, BlockState blockState) {
        if (fluidState != null && !fluidState.isEmpty()) {
            if (fluidState.getType() == ModFluids.EXPERIENCE_STILL.get() || fluidState.getType() == ModFluids.EXPERIENCE_FLOWING.get()) {
                return true;
            }
            ResourceLocation reg = ForgeRegistries.FLUIDS.getKey(fluidState.getType());
            if (reg != null && (reg.getPath().contains("experience") || reg.getPath().contains("xp"))) {
                return true;
            }
        }
        if (blockState != null && !blockState.isAir()) {
            if (blockState.getBlock() instanceof ExperienceFluidBlock) {
                return true;
            }
            ResourceLocation reg = ForgeRegistries.BLOCKS.getKey(blockState.getBlock());
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
        ResourceLocation key = ForgeRegistries.BLOCKS.getKey(block);
        return key != null && key.getNamespace().equals("buildscape") && key.getPath().contains("chain");
    }
}
