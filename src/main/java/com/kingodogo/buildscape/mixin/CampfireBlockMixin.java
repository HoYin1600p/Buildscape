package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.particle.ModParticles;
import com.kingodogo.buildscape.particle.SmokeColorRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(CampfireBlock.class)
public abstract class CampfireBlockMixin {

    @Inject(method = "animateTick", at = @At("HEAD"), cancellable = true)
    private void buildscape$campfireAnimateTick(BlockState state, Level level, BlockPos pos, Random random, CallbackInfo ci) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be == null) return;

        CompoundTag data = be.getTileData();

        boolean active = !data.contains("BuildScapeSmokeActive") || data.getBoolean("BuildScapeSmokeActive");
        String color = data.contains("BuildScapeSmokeColor") ? data.getString("BuildScapeSmokeColor") : null;
        boolean isLit = state.getValue(CampfireBlock.LIT);

        // If not active, we take over to cancel everything (no smoke)
        if (!active) {
            ci.cancel();
            return;
        }

        // If it's a regular lit campfire with no color, let vanilla handle it
        if (isLit && (color == null || color.isEmpty())) {
            return;
        }

        // Handle Custom Color smoke or Smoking while unlit (acting as vent)
        if (color != null && !color.isEmpty()) {
            ci.cancel(); // Cancel vanilla smoke or nothing to replace with colored one

            // Only crackle if actually lit
            if (isLit && random.nextInt(10) == 0) {
                level.playLocalSound((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.6F, false);
            }

            // Always spawn colored smoke if active and has color
            if (random.nextFloat() < 0.2F) {
                double x = (double) pos.getX() + 0.5 + random.nextDouble() / 3.0 * (double) (random.nextBoolean() ? 1 : -1);
                double y = (double) pos.getY() + random.nextDouble() + random.nextDouble();
                double z = (double) pos.getZ() + 0.5 + random.nextDouble() / 3.0 * (double) (random.nextBoolean() ? 1 : -1);

                SmokeColorRegistry.registerColorForPosition(x, y, z, color);
                level.addAlwaysVisibleParticle(ModParticles.COLORED_SMOKE.get(), true, x, y, z, 0.0, 0.07, 0.0);
            }
        }
    }
}
