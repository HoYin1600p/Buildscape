package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.block.ExperienceFluidBlock;
import com.kingodogo.buildscape.fluid.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Level.class)
public abstract class LevelMixin {

    @Inject(method = "addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V", at = @At("HEAD"), cancellable = true)
    private void onAddParticle(ParticleOptions options, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, CallbackInfo ci) {
        if (isWaterParticle(options)) {
            Level self = (Level) (Object) this;
            BlockPos pos = new BlockPos(x, y, z);
            if (isExperienceFluid(self, pos)) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "addParticle(Lnet/minecraft/core/particles/ParticleOptions;ZDDDDDD)V", at = @At("HEAD"), cancellable = true)
    private void onAddParticleAlways(ParticleOptions options, boolean alwaysRender, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, CallbackInfo ci) {
        if (isWaterParticle(options)) {
            Level self = (Level) (Object) this;
            BlockPos pos = new BlockPos(x, y, z);
            if (isExperienceFluid(self, pos)) {
                ci.cancel();
            }
        }
    }

    private static boolean isWaterParticle(ParticleOptions options) {
        if (options == null) return false;
        return options.getType() == ParticleTypes.SPLASH ||
               options.getType() == ParticleTypes.BUBBLE ||
               options.getType() == ParticleTypes.BUBBLE_POP ||
               options.getType() == ParticleTypes.BUBBLE_COLUMN_UP ||
               options.getType() == ParticleTypes.UNDERWATER ||
               options.getType() == ParticleTypes.RAIN ||
               options.getType() == ParticleTypes.FALLING_WATER;
    }

    private static boolean isExperienceFluid(Level level, BlockPos pos) {
        if (level == null || pos == null) return false;
        return checkFluidOrBlock(level, pos) || checkFluidOrBlock(level, pos.below()) || checkFluidOrBlock(level, pos.above());
    }

    private static boolean checkFluidOrBlock(Level level, BlockPos pos) {
        FluidState fluidState = level.getFluidState(pos);
        if (fluidState != null && !fluidState.isEmpty()) {
            if (fluidState.getType() == ModFluids.EXPERIENCE_STILL.get() || fluidState.getType() == ModFluids.EXPERIENCE_FLOWING.get()) {
                return true;
            }
            ResourceLocation reg = ForgeRegistries.FLUIDS.getKey(fluidState.getType());
            if (reg != null && (reg.getPath().contains("experience") || reg.getPath().contains("xp"))) {
                return true;
            }
        }

        BlockState blockState = level.getBlockState(pos);
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
}
