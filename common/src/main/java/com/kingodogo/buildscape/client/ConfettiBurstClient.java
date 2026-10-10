package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.particle.ModParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;

import java.util.Random;

public final class ConfettiBurstClient {

    private ConfettiBurstClient() {}

    public static void spawn(Level level, double startX, double startY, double startZ,
                             double lookX, double lookY, double lookZ, int burstLevel, long seed) {
        if (level == null) return;

        Random random = new Random(seed);
        int particleCount = (75 + random.nextInt(46)) * burstLevel;
        double speedMultiplier = 1.0 + (burstLevel - 1) * 0.2D;
        double spreadMultiplier = 1.0 + (burstLevel - 1) * 0.1D;
        ParticleOptions type = (ParticleOptions) ModParticles.CONFETTI.get();

        for (int i = 0; i < particleCount; i++) {
            double speed = (0.15D + random.nextDouble() * 0.25D) * speedMultiplier;
            double spread = (0.30D + random.nextDouble() * 0.35D) * spreadMultiplier;

            double vx = lookX * speed + (random.nextDouble() - 0.5D) * spread;
            double vy = lookY * speed + (random.nextDouble() - 0.5D) * spread + 0.12D;
            double vz = lookZ * speed + (random.nextDouble() - 0.5D) * spread;

            double px = startX + (random.nextDouble() - 0.5D) * 0.4D;
            double py = startY + (random.nextDouble() - 0.5D) * 0.4D;
            double pz = startZ + (random.nextDouble() - 0.5D) * 0.4D;

            level.addParticle(type, px, py, pz, vx, vy, vz);
        }
    }
}
