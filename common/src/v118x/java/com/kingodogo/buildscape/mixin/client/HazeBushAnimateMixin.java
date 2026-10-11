package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.adapter.v118x.client.HazeBushParticleHandler;
import com.kingodogo.buildscape.block.HazeBushBlock;
import com.kingodogo.buildscape.block.PottedHazeBushBlock;
import com.kingodogo.buildscape.particle.ModParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(Block.class)
public abstract class HazeBushAnimateMixin {
    @Dynamic
    @Inject(method = "animateTick", at = @At("TAIL"), require = 0)
    private void buildscape$animateHaze(BlockState state, Level level, BlockPos pos, Random random, CallbackInfo callback) {
        float[] rgb;
        if (state.getBlock() instanceof HazeBushBlock bush) {
            if (!state.getValue(HazeBushBlock.HAS_HAZE)) return;
            rgb = HazeBushBlock.getPastelColor(bush.getColor());
        } else if (state.getBlock() instanceof PottedHazeBushBlock bush) {
            if (!state.getValue(PottedHazeBushBlock.HAS_HAZE)) return;
            rgb = HazeBushBlock.getPastelColor(bush.getColor());
        } else return;
        ParticleStatus setting = Minecraft.getInstance().options.particles;
        if (setting == ParticleStatus.MINIMAL) return;
        HazeBushParticleHandler.track(pos);
        if (setting == ParticleStatus.DECREASED && random.nextFloat() > 0.5F) return;
        int count = setting == ParticleStatus.DECREASED ? 1 : 1 + random.nextInt(2);
        for (int i = 0; i < count; i++) {
            var particlePos = HazeBushParticleHandler.findHazeParticlePos(level, pos, random);
            if (particlePos != null) level.addParticle(ModParticles.HAZE.get(), particlePos.x, particlePos.y, particlePos.z, rgb[0], rgb[1], rgb[2]);
        }
    }
}
