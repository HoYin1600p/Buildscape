package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.registry.ModParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;

import java.util.Random;

public final class ConfettiBurstClient {

    private ConfettiBurstClient() {}

    public static void spawn(double startX, double startY, double startZ,
            double lookX, double lookY, double lookZ, int burstLevel, long seed) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }

        Random random = new Random(seed);
        int particleCount = (75 + random.nextInt(46)) * burstLevel;
        double speedMultiplier = 1.0 + (burstLevel - 1) * 0.2;
        double spreadMultiplier = 1.0 + (burstLevel - 1) * 0.1;
        SimpleParticleType type = (SimpleParticleType) ModParticles.CONFETTI.get();

        for (int i = 0; i < particleCount; i++) {
            double speed = (0.15 + random.nextDouble() * 0.25) * speedMultiplier;
            double spread = (0.30 + random.nextDouble() * 0.35) * spreadMultiplier;

            double vx = lookX * speed + (random.nextDouble() - 0.5) * spread;
            double vy = lookY * speed + (random.nextDouble() - 0.5) * spread + 0.12;
            double vz = lookZ * speed + (random.nextDouble() - 0.5) * spread;

            double px = startX + (random.nextDouble() - 0.5) * 0.4;
            double py = startY + (random.nextDouble() - 0.5) * 0.4;
            double pz = startZ + (random.nextDouble() - 0.5) * 0.4;

            level.addParticle(type, px, py, pz, vx, vy, vz);
        }
    }
}
