package com.kingodogo.buildscape.adapter;

import com.kingodogo.buildscape.adapter.client.HazeBushParticleHandler;
import com.kingodogo.buildscape.block.HazeBushBlock;
import com.kingodogo.buildscape.block.PottedHazeBushBlock;
import com.kingodogo.buildscape.particle.ModParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public final class HazeAnimation {
    private HazeAnimation() {}

    public static void animate(BlockState state, Level level, BlockPos pos, net.minecraft.util.RandomSource randomSource) {
        animate(state, level, pos, new Random(randomSource.nextLong()));
    }

    public static void animate(BlockState state, Level level, BlockPos pos, Random random) {
        DyeColor color;
        if (state.getBlock() instanceof HazeBushBlock bush) {
            if (!state.getValue(HazeBushBlock.HAS_HAZE)) return;
            color = bush.getColor();
        } else if (state.getBlock() instanceof PottedHazeBushBlock bush) {
            if (!state.getValue(PottedHazeBushBlock.HAS_HAZE)) return;
            color = bush.getColor();
        } else return;

        ParticleStatus setting = Minecraft.getInstance().options.particles().get();
        if (setting == ParticleStatus.MINIMAL) return;
        HazeBushParticleHandler.track(pos);
        if (setting == ParticleStatus.DECREASED && random.nextFloat() > 0.5F) return;

        float[] rgb = HazeBushBlock.getPastelColor(color);
        int count = setting == ParticleStatus.DECREASED ? 1 : 1 + random.nextInt(2);
        for (int i = 0; i < count; i++) {
            var particlePos = HazeBushParticleHandler.findHazeParticlePos(level, pos, random);
            if (particlePos != null) {
                level.addParticle(ModParticles.HAZE.get(), particlePos.x, particlePos.y, particlePos.z,
                        rgb[0], rgb[1], rgb[2]);
            }
        }
    }
}
