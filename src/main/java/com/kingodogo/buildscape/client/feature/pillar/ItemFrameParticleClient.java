package com.kingodogo.buildscape.client.feature.pillar;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.client.particle.PillarSparkleParticle;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.core.ParticlePatterns;
import com.kingodogo.buildscape.entity.ColoredItemFrameEntity;
import com.kingodogo.buildscape.feature.pillar.ItemFrameParticleHandler;
import com.kingodogo.buildscape.registry.ModParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ItemFrameParticleClient {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || mc.isPaused()) {
            return;
        }

        ClientLevel level = mc.level;
        long gameTime = level.getGameTime();

        if (gameTime % 5 != 0) {
            return;
        }

        Vec3 playerPos = mc.player.position();
        double maxDistance = 32.0;

        PillarParticleConfig cfg = PillarParticleConfig.get();

        for (Entity entity : level.entitiesForRendering()) {
            if (entity instanceof ItemFrame itemFrame) {
                if (itemFrame.getItem().isEmpty()) {
                    continue;
                }

                Vec3 framePos = itemFrame.position();
                if (framePos.distanceToSqr(playerPos) > maxDistance * maxDistance) {
                    continue;
                }

                if (!cfg.matches(itemFrame.getItem())) {
                    continue;
                }

                String pattern = ItemFrameParticleHandler.getParticlePattern(itemFrame);

                spawnItemFrameParticles(level, itemFrame, pattern, cfg, gameTime);
            } else if (entity instanceof ColoredItemFrameEntity coloredFrame) {
                if (coloredFrame.getItem().isEmpty()) {
                    continue;
                }

                Vec3 framePos = coloredFrame.position();
                if (framePos.distanceToSqr(playerPos) > maxDistance * maxDistance) {
                    continue;
                }

                if (!cfg.matches(coloredFrame.getItem())) {
                    continue;
                }

                String pattern = ItemFrameParticleHandler.getParticlePatternColored(coloredFrame);

                spawnColoredItemFrameParticles(level, coloredFrame, pattern, cfg, gameTime);
            }
        }
    }

    private static void spawnItemFrameParticles(
            Level level,
            ItemFrame itemFrame,
            String pattern,
            PillarParticleConfig cfg,
            long time) {
        ItemFrameParticleHandler.FrameSettings settings = ItemFrameParticleHandler.resolveFrameSettings(level,
                ItemFrameParticleHandler.getFrameId(itemFrame), pattern, cfg);
        if (settings.count() == 0) {
            return;
        }
        pattern = settings.pattern();
        Random rand = new Random();

        Direction facing = itemFrame.getDirection();
        Vec3 normal = Vec3.atLowerCornerOf(facing.getNormal());

        double centerX = itemFrame.getX();
        double centerY = itemFrame.getY();
        double centerZ = itemFrame.getZ();

        double frameOffset = 0.0625;
        centerX += normal.x * frameOffset;
        centerY += normal.y * frameOffset;
        centerZ += normal.z * frameOffset;

        int count = settings.count();
        double speed = settings.speed();
        double spread = settings.spread();

        Vec3 upVec, rightVec;
        if (facing == Direction.UP) {
            upVec = new Vec3(0, 0, -1);
            rightVec = new Vec3(1, 0, 0);
        } else if (facing == Direction.DOWN) {
            upVec = new Vec3(0, 0, 1);
            rightVec = new Vec3(1, 0, 0);
        } else {
            upVec = new Vec3(0, 1, 0);
            rightVec = normal.cross(upVec).normalize();
        }

        for (int i = 0; i < count; i++) {
            double u, v, w, vu, vv, vw;

            switch (pattern) {
                case ParticlePatterns.BEAM -> {
                    u = (rand.nextDouble() - 0.5) * spread * 0.3;
                    v = (rand.nextDouble() - 0.5) * spread * 0.3;
                    w = 0.0;
                    vu = (rand.nextDouble() - 0.5) * speed * 0.2;
                    vv = (rand.nextDouble() - 0.5) * speed * 0.2;
                    vw = speed * (0.8 + rand.nextDouble() * 0.4);
                }
                case ParticlePatterns.SPIRAL -> {
                    double angle = (time * 0.1 + (i * 2.0 * Math.PI) / count) % (2.0 * Math.PI);
                    double radius = spread * 0.5;
                    u = Math.cos(angle) * radius;
                    v = Math.sin(angle) * radius;
                    w = 0.0;
                    vu = Math.cos(angle) * speed * 0.3;
                    vv = Math.sin(angle) * speed * 0.3;
                    vw = speed * 0.6;
                }
                case ParticlePatterns.FOUNTAIN -> {
                    double fAngle = rand.nextDouble() * 2.0 * Math.PI;
                    double fRadius = rand.nextDouble() * spread;
                    u = Math.cos(fAngle) * fRadius;
                    v = Math.sin(fAngle) * fRadius;
                    w = rand.nextDouble() * spread * 0.5;
                    vu = Math.cos(fAngle) * speed * 0.5;
                    vv = Math.sin(fAngle) * speed * 0.5;
                    vw = speed * 0.3 - rand.nextDouble() * speed * 0.2;
                }
                case ParticlePatterns.PULSE -> {
                    double pulsePhase = (time * 0.2) % (2.0 * Math.PI);
                    double pulseRadius = spread * (0.3 + Math.sin(pulsePhase) * 0.7);
                    double pAngle = rand.nextDouble() * 2.0 * Math.PI;
                    u = Math.cos(pAngle) * pulseRadius;
                    v = Math.sin(pAngle) * pulseRadius;
                    w = (rand.nextDouble() - 0.5) * spread * 0.5;
                    vu = Math.cos(pAngle) * speed * Math.sin(pulsePhase);
                    vv = Math.sin(pAngle) * speed * Math.sin(pulsePhase);
                    vw = speed * 0.2;
                }
                case ParticlePatterns.RING -> {
                    double rAngle = ((i * 2.0 * Math.PI) / count) + (time * 0.05);
                    double rRadius = spread * 0.8;
                    u = Math.cos(rAngle) * rRadius;
                    v = Math.sin(rAngle) * rRadius;
                    w = (rand.nextDouble() - 0.5) * spread * 0.3;
                    vu = Math.cos(rAngle + Math.PI / 2) * speed * 0.4;
                    vv = Math.sin(rAngle + Math.PI / 2) * speed * 0.4;
                    vw = speed * 0.3;
                }
                case ParticlePatterns.BURST -> {
                    double bAngle = rand.nextDouble() * 2.0 * Math.PI;
                    double bElevation = (rand.nextDouble() - 0.5) * Math.PI * 0.7;
                    u = Math.cos(bAngle) * Math.cos(bElevation) * spread * 0.6;
                    v = Math.sin(bAngle) * Math.cos(bElevation) * spread * 0.6;
                    w = Math.sin(bElevation) * spread * 0.6;
                    vu = Math.cos(bAngle) * Math.cos(bElevation) * speed * 1.5;
                    vv = Math.sin(bAngle) * Math.cos(bElevation) * speed * 1.5;
                    vw = Math.sin(bElevation) * speed * 1.5;
                }
                case ParticlePatterns.SNOWFLAKE -> {
                    u = (rand.nextDouble() - 0.5) * spread * 0.3;
                    v = (rand.nextDouble() - 0.5) * spread * 0.3;
                    w = 2.0;
                    vu = (rand.nextDouble() - 0.5) * speed * 0.2;
                    vv = (rand.nextDouble() - 0.5) * speed * 0.2;
                    vw = -speed * (0.8 + rand.nextDouble() * 0.4);
                }
                default -> {
                    u = (rand.nextDouble() - 0.5) * spread;
                    v = (rand.nextDouble() - 0.5) * spread;
                    w = rand.nextDouble() * spread;
                    vu = (rand.nextDouble() - 0.5) * speed;
                    vv = (rand.nextDouble() - 0.5) * speed;
                    vw = rand.nextDouble() * speed;
                }
            }

            double sx = rightVec.x * u + upVec.x * v + normal.x * w;
            double sy = rightVec.y * u + upVec.y * v + normal.y * w;
            double sz = rightVec.z * u + upVec.z * v + normal.z * w;

            double vx = rightVec.x * vu + upVec.x * vv + normal.x * vw;
            double vy = rightVec.y * vu + upVec.y * vv + normal.y * vw;
            double vz = rightVec.z * vu + upVec.z * vv + normal.z * vw;

            double particleX = centerX + sx;
            double particleY = centerY + sy;
            double particleZ = centerZ + sz;

            boolean isSnowflake = ParticlePatterns.SNOWFLAKE.equals(pattern);
            SimpleParticleType particleType = isSnowflake
                    ? ModParticles.SNOWFLAKE_STILL.get()
                    : ModParticles.GLOW_LIME_SPARKLE.get();

            if (!isSnowflake) {
                String colorCode = ItemFrameParticleHandler.getNextColor(itemFrame, cfg, settings.maxColors());
                PillarSparkleParticle.queueColor(
                        particleX,
                        particleY,
                        particleZ,
                        colorCode);
            }

            level.addParticle(
                    particleType,
                    particleX,
                    particleY,
                    particleZ,
                    vx,
                    vy,
                    vz);
        }
    }

    private static void spawnColoredItemFrameParticles(
            Level level,
            ColoredItemFrameEntity coloredFrame,
            String pattern,
            PillarParticleConfig cfg,
            long time) {
        ItemFrameParticleHandler.FrameSettings settings = ItemFrameParticleHandler.resolveFrameSettings(level,
                ItemFrameParticleHandler.getFrameIdColored(coloredFrame), pattern, cfg);
        if (settings.count() == 0) {
            return;
        }
        pattern = settings.pattern();
        Random rand = new Random();

        Direction facing = coloredFrame.getDirection();
        Vec3 normal = Vec3.atLowerCornerOf(facing.getNormal());

        double centerX = coloredFrame.getX();
        double centerY = coloredFrame.getY();
        double centerZ = coloredFrame.getZ();

        double frameOffset = 0.0625;
        centerX += normal.x * frameOffset;
        centerY += normal.y * frameOffset;
        centerZ += normal.z * frameOffset;

        int count = settings.count();
        double speed = settings.speed();
        double spread = settings.spread();

        Vec3 upVec, rightVec;
        if (facing == Direction.UP) {
            upVec = new Vec3(0, 0, -1);
            rightVec = new Vec3(1, 0, 0);
        } else if (facing == Direction.DOWN) {
            upVec = new Vec3(0, 0, 1);
            rightVec = new Vec3(1, 0, 0);
        } else {
            upVec = new Vec3(0, 1, 0);
            rightVec = normal.cross(upVec).normalize();
        }

        for (int i = 0; i < count; i++) {
            double u, v, w, vu, vv, vw;

            switch (pattern) {
                case ParticlePatterns.BEAM -> {
                    u = (rand.nextDouble() - 0.5) * spread * 0.3;
                    v = (rand.nextDouble() - 0.5) * spread * 0.3;
                    w = 0.0;
                    vu = (rand.nextDouble() - 0.5) * speed * 0.2;
                    vv = (rand.nextDouble() - 0.5) * speed * 0.2;
                    vw = speed * (0.8 + rand.nextDouble() * 0.4);
                }
                case ParticlePatterns.SPIRAL -> {
                    double angle = (time * 0.1 + (i * 2.0 * Math.PI) / count) % (2.0 * Math.PI);
                    double radius = spread * 0.5;
                    u = Math.cos(angle) * radius;
                    v = Math.sin(angle) * radius;
                    w = 0.0;
                    vu = Math.cos(angle) * speed * 0.3;
                    vv = Math.sin(angle) * speed * 0.3;
                    vw = speed * 0.6;
                }
                case ParticlePatterns.FOUNTAIN -> {
                    double fAngle = rand.nextDouble() * 2.0 * Math.PI;
                    double fRadius = rand.nextDouble() * spread;
                    u = Math.cos(fAngle) * fRadius;
                    v = Math.sin(fAngle) * fRadius;
                    w = rand.nextDouble() * spread * 0.5;
                    vu = Math.cos(fAngle) * speed * 0.5;
                    vv = Math.sin(fAngle) * speed * 0.5;
                    vw = speed * 0.3 - rand.nextDouble() * speed * 0.2;
                }
                case ParticlePatterns.PULSE -> {
                    double pulsePhase = (time * 0.2) % (2.0 * Math.PI);
                    double pulseRadius = spread * (0.3 + Math.sin(pulsePhase) * 0.7);
                    double pAngle = rand.nextDouble() * 2.0 * Math.PI;
                    u = Math.cos(pAngle) * pulseRadius;
                    v = Math.sin(pAngle) * pulseRadius;
                    w = (rand.nextDouble() - 0.5) * spread * 0.5;
                    vu = Math.cos(pAngle) * speed * Math.sin(pulsePhase);
                    vv = Math.sin(pAngle) * speed * Math.sin(pulsePhase);
                    vw = speed * 0.2;
                }
                case ParticlePatterns.RING -> {
                    double rAngle = ((i * 2.0 * Math.PI) / count) + (time * 0.05);
                    double rRadius = spread * 0.8;
                    u = Math.cos(rAngle) * rRadius;
                    v = Math.sin(rAngle) * rRadius;
                    w = (rand.nextDouble() - 0.5) * spread * 0.3;
                    vu = Math.cos(rAngle + Math.PI / 2) * speed * 0.4;
                    vv = Math.sin(rAngle + Math.PI / 2) * speed * 0.4;
                    vw = speed * 0.3;
                }
                case ParticlePatterns.BURST -> {
                    double bAngle = rand.nextDouble() * 2.0 * Math.PI;
                    double bElevation = (rand.nextDouble() - 0.5) * Math.PI * 0.5;
                    u = Math.cos(bAngle) * Math.cos(bElevation) * spread * 0.3;
                    v = Math.sin(bAngle) * Math.cos(bElevation) * spread * 0.3;
                    w = Math.sin(bElevation) * spread * 0.3;
                    vu = Math.cos(bAngle) * Math.cos(bElevation) * speed;
                    vv = Math.sin(bAngle) * Math.cos(bElevation) * speed;
                    vw = Math.sin(bElevation) * speed;
                }
                case ParticlePatterns.SNOWFLAKE -> {
                    u = (rand.nextDouble() - 0.5) * spread * 0.3;
                    v = (rand.nextDouble() - 0.5) * spread * 0.3;
                    w = 2.0;
                    vu = (rand.nextDouble() - 0.5) * speed * 0.2;
                    vv = (rand.nextDouble() - 0.5) * speed * 0.2;
                    vw = -speed * (0.8 + rand.nextDouble() * 0.4);
                }
                default -> {
                    u = (rand.nextDouble() - 0.5) * spread;
                    v = (rand.nextDouble() - 0.5) * spread;
                    w = rand.nextDouble() * spread;
                    vu = (rand.nextDouble() - 0.5) * speed;
                    vv = (rand.nextDouble() - 0.5) * speed;
                    vw = rand.nextDouble() * speed;
                }
            }

            double sx = rightVec.x * u + upVec.x * v + normal.x * w;
            double sy = rightVec.y * u + upVec.y * v + normal.y * w;
            double sz = rightVec.z * u + upVec.z * v + normal.z * w;

            double vx = rightVec.x * vu + upVec.x * vv + normal.x * vw;
            double vy = rightVec.y * vu + upVec.y * vv + normal.y * vw;
            double vz = rightVec.z * vu + upVec.z * vv + normal.z * vw;

            double particleX = centerX + sx;
            double particleY = centerY + sy;
            double particleZ = centerZ + sz;

            boolean isSnowflake = ParticlePatterns.SNOWFLAKE.equals(pattern);
            SimpleParticleType particleType = isSnowflake
                    ? ModParticles.SNOWFLAKE_STILL.get()
                    : ModParticles.GLOW_LIME_SPARKLE.get();

            if (!isSnowflake) {
                String colorCode = ItemFrameParticleHandler.getNextColorColored(coloredFrame, cfg,
                        settings.maxColors());
                PillarSparkleParticle.queueColor(
                        particleX,
                        particleY,
                        particleZ,
                        colorCode);
            }

            level.addParticle(
                    particleType,
                    particleX,
                    particleY,
                    particleZ,
                    vx,
                    vy,
                    vz);
        }
    }
}
