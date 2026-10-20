package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.core.ParticlePatterns;
import com.kingodogo.buildscape.feature.pillar.PillarBlockEntity;
import com.kingodogo.buildscape.feature.pillar.PillarParticleSettings;
import com.kingodogo.buildscape.particle.PillarSparkleParticle;
import com.kingodogo.buildscape.registry.ModParticles;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Spawns the particles of a pillar with a displayed item. Client only: the pillar block entity calls it from its
 * client ticker, after checking that it runs on the client, so a dedicated server never loads this class.
 */
@OnlyIn(Dist.CLIENT)
public final class PillarClientParticles {

    private PillarClientParticles() {
    }


    public static boolean isWithinRange(BlockPos pos) {
        Minecraft mc = Minecraft.getInstance();
        return mc != null && mc.gameRenderer.getMainCamera().getPosition().distanceToSqr(
                pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 32.0D * 32.0D;
    }

    public static void spawnParticles(
            Level level,
            BlockPos pos,
            PillarBlockEntity be,
            PillarParticleConfig cfg,
            PillarParticleSettings settings,
            long time,
            int count,
            Random rand,
            double centerX,
            double centerY,
            double centerZ) {

        boolean isSnowflake = ParticlePatterns.SNOWFLAKE.equals(settings.pattern());
        SimpleParticleType particleType = isSnowflake ? ModParticles.SNOWFLAKE.get()
                : ModParticles.GLOW_LIME_SPARKLE.get();

        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.particleEngine == null) {
            for (int i = 0; i < count; i++) {
                ParticleSpawnData data = calculateParticleData(
                        settings,
                        cfg,
                        time,
                        i,
                        count,
                        rand);
                if (data == null) {
                    continue;
                }

                double particleX = centerX + data.sx;
                double particleY = centerY + data.sy;
                double particleZ = centerZ + data.sz;

                if (!isSnowflake) {
                    String colorCode = be.getParticleColor(cfg);
                    PillarSparkleParticle.queueColor(
                            particleX,
                            particleY,
                            particleZ,
                            colorCode);
                    if (data.size != 1.0f) {
                        PillarSparkleParticle.queueSize(
                                particleX,
                                particleY,
                                particleZ,
                                data.size);
                    }
                }

                level.addParticle(
                        particleType,
                        particleX,
                        particleY,
                        particleZ,
                        data.vx,
                        data.vy,
                        data.vz);
            }
            return;
        }

        for (int i = 0; i < count; i++) {
            ParticleSpawnData data = calculateParticleData(
                    settings,
                    cfg,
                    time,
                    i,
                    count,
                    rand);
            if (data == null) {
                continue;
            }

            double particleX = centerX + data.sx;
            double particleY = centerY + data.sy;
            double particleZ = centerZ + data.sz;

            if (!isSnowflake) {
                String colorCode = be.getParticleColor(cfg);

                PillarSparkleParticle.queueColor(
                        particleX,
                        particleY,
                        particleZ,
                        colorCode);
                if (data.size != 1.0f) {
                    PillarSparkleParticle.queueSize(
                            particleX,
                            particleY,
                            particleZ,
                            data.size);
                }
            }

            level.addParticle(
                    particleType,
                    particleX,
                    particleY,
                    particleZ,
                    data.vx,
                    data.vy,
                    data.vz);
        }
    }


    private static ParticleSpawnData calculateParticleData(
            PillarParticleSettings settings,
            PillarParticleConfig cfg,
            long time,
            int i,
            int count,
            Random rand) {
        double sx, sy, sz, vx, vy, vz;
        float size = 1.0f;

        if (settings.usePattern()) {
            String pattern = settings.pattern();
            double speed = settings.speed() * settings.intensity();
            double spread = settings.spread();

            switch (pattern) {
                case ParticlePatterns.BEAM:
                    sx = (rand.nextDouble() - 0.5) * spread * 0.3;
                    sy = 0.0;
                    sz = (rand.nextDouble() - 0.5) * spread * 0.3;
                    vx = (rand.nextDouble() - 0.5) * speed * 0.2;
                    vy = speed * (0.8 + rand.nextDouble() * 0.4);
                    vz = (rand.nextDouble() - 0.5) * speed * 0.2;
                    break;
                case ParticlePatterns.SPIRAL:
                    double angle = (time * 0.1 + (i * 2.0 * Math.PI) / count) % (2.0 * Math.PI);
                    double radius = spread * 0.5;
                    sx = Math.cos(angle) * radius;
                    sy = 0.0;
                    sz = Math.sin(angle) * radius;
                    vx = Math.cos(angle) * speed * 0.3;
                    vy = speed * 0.6;
                    vz = Math.sin(angle) * speed * 0.3;
                    break;
                case ParticlePatterns.FOUNTAIN:
                    double fAngle = rand.nextDouble() * 2.0 * Math.PI;
                    double fRadius = rand.nextDouble() * spread;
                    sx = Math.cos(fAngle) * fRadius;
                    sy = rand.nextDouble() * spread * 0.5;
                    sz = Math.sin(fAngle) * fRadius;
                    vx = Math.cos(fAngle) * speed * 0.5;
                    vy = speed * 0.3 - rand.nextDouble() * speed * 0.2;
                    vz = Math.sin(fAngle) * speed * 0.5;
                    break;
                case ParticlePatterns.PULSE:
                    double pulsePhase = (time * 0.2) % (2.0 * Math.PI);
                    double pulseRadius = spread * (0.3 + Math.sin(pulsePhase) * 0.7);
                    double pAngle = rand.nextDouble() * 2.0 * Math.PI;
                    sx = Math.cos(pAngle) * pulseRadius;
                    sy = (rand.nextDouble() - 0.5) * spread * 0.5;
                    sz = Math.sin(pAngle) * pulseRadius;
                    vx = Math.cos(pAngle) * speed * Math.sin(pulsePhase);
                    vy = speed * 0.2;
                    vz = Math.sin(pAngle) * speed * Math.sin(pulsePhase);
                    break;
                case ParticlePatterns.RING:
                    double rAngle = ((i * 2.0 * Math.PI) / count) + (time * 0.05);
                    double rRadius = spread * 0.8;
                    sx = Math.cos(rAngle) * rRadius;
                    sy = (rand.nextDouble() - 0.5) * spread * 0.3;
                    sz = Math.sin(rAngle) * rRadius;
                    vx = Math.cos(rAngle + Math.PI / 2) * speed * 0.4;
                    vy = speed * 0.3;
                    vz = Math.sin(rAngle + Math.PI / 2) * speed * 0.4;
                    break;
                case ParticlePatterns.BURST:
                    double bAngle = rand.nextDouble() * 2.0 * Math.PI;
                    double bElevation = (rand.nextDouble() - 0.5) * Math.PI * 0.5;
                    sx = Math.cos(bAngle) * Math.cos(bElevation) * spread * 0.3;
                    sy = Math.sin(bElevation) * spread * 0.3;
                    sz = Math.sin(bAngle) * Math.cos(bElevation) * spread * 0.3;
                    vx = Math.cos(bAngle) * Math.cos(bElevation) * speed;
                    vy = Math.sin(bElevation) * speed;
                    vz = Math.sin(bAngle) * Math.cos(bElevation) * speed;
                    size = 0.5f;
                    break;
                case ParticlePatterns.SNOWFLAKE:
                    double sfAngle = rand.nextDouble() * 2.0 * Math.PI;
                    double sfRadius = rand.nextDouble() * spread;
                    sx = Math.cos(sfAngle) * sfRadius;
                    sy = 2.0;
                    sz = Math.sin(sfAngle) * sfRadius;
                    vx = (rand.nextDouble() - 0.5) * speed * 0.1;
                    vy = -speed * 0.3;
                    vz = (rand.nextDouble() - 0.5) * speed * 0.1;
                    size = 0.3f;
                    break;
                default:
                    sx = (rand.nextDouble() - 0.5) * spread;
                    sy = rand.nextDouble() * spread;
                    sz = (rand.nextDouble() - 0.5) * spread;
                    vx = (rand.nextDouble() - 0.5) * speed;
                    vy = rand.nextDouble() * speed;
                    vz = (rand.nextDouble() - 0.5) * speed;
                    break;
            }
        } else {
            double rAngle = ((i * 2.0 * Math.PI) / count) + (time * 0.05);
            double rRadius = cfg.particle_spread * 0.8;
            sx = Math.cos(rAngle) * rRadius;
            sy = (rand.nextDouble() - 0.5) * cfg.particle_spread * 0.3;
            sz = Math.sin(rAngle) * rRadius;
            vx = Math.cos(rAngle + Math.PI / 2) * cfg.particle_speed * 0.4;
            vy = cfg.particle_speed * 0.3;
            vz = Math.sin(rAngle + Math.PI / 2) * cfg.particle_speed * 0.4;
        }

        return new ParticleSpawnData(sx, sy, sz, vx, vy, vz, size);
    }

    private record ParticleSpawnData(double sx, double sy, double sz, double vx, double vy, double vz, float size) {

    }
}
