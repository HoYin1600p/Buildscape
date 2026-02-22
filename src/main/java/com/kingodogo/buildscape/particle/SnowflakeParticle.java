package com.kingodogo.buildscape.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@OnlyIn(Dist.CLIENT)
public class SnowflakeParticle extends TextureSheetParticle {

    // Color queue for tinting particles at spawn position
    private static final Map<String, float[]> pendingColors = new ConcurrentHashMap<>();

    /**
     * Queue a color for a snowflake particle at the given position.
     * The next particle spawned near this position will use this color.
     */
    public static void queueColor(double x, double y, double z, String hexColor) {
        if (hexColor == null || hexColor.isEmpty()) return;
        try {
            String hex = hexColor.startsWith("#") ? hexColor.substring(1) : hexColor;
            int rgb = Integer.parseInt(hex, 16);
            float r = ((rgb >> 16) & 0xFF) / 255.0f;
            float g = ((rgb >> 8) & 0xFF) / 255.0f;
            float b = (rgb & 0xFF) / 255.0f;
            String key = String.format("%.1f,%.1f,%.1f", x, y, z);
            pendingColors.put(key, new float[]{r, g, b});
        } catch (Exception e) {
            // Invalid color format, ignore
        }
    }

    /**
     * Get and remove a queued color for the given position.
     */
    private static float[] getQueuedColor(double x, double y, double z) {
        String key = String.format("%.1f,%.1f,%.1f", x, y, z);
        return pendingColors.remove(key);
    }

    protected SnowflakeParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);

        // Use setSpriteFromAge to properly initialize the sprite (like working particles)
        // Then randomly pick a sprite from the set (snowflake has 9 different textures)
        this.setSpriteFromAge(sprites);
        this.pickSprite(sprites);

        // Snowflake properties
        this.gravity = 0.003F; // Almost no gravity - floats in place
        this.lifetime = 7 + level.random.nextInt(4); // 7-11 ticks lifetime
        this.hasPhysics = false; // No collision so they stay in formation

        // Larger particles for better visibility
        this.quadSize = 0.18F + level.random.nextFloat() * 0.10F; // 0.18 to 0.28 size

        // Slight rotation and drift
        this.xd = xSpeed + (level.random.nextDouble() - 0.5) * 0.02;
        this.yd = ySpeed;
        this.zd = zSpeed + (level.random.nextDouble() - 0.5) * 0.02;

        // Check for queued color
        float[] queuedColor = getQueuedColor(x, y, z);
        if (queuedColor != null) {
            this.setColor(queuedColor[0], queuedColor[1], queuedColor[2]);
        } else {
            // Default white so texture shows natural colors
            this.setColor(1.0F, 1.0F, 1.0F);
        }
        this.alpha = 0.8F + level.random.nextFloat() * 0.2F;
    }

    @Override
    public void tick() {
        super.tick();

        // Gentle rotation effect
        this.oRoll = this.roll;
        this.roll += 0.1F;

        // Fade out as it falls
        if (this.age > this.lifetime * 0.7F) {
            float fadeProgress = (float) (this.age - this.lifetime * 0.7F) / (this.lifetime * 0.3F);
            this.alpha = (1.0F - fadeProgress) * 0.8F;
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new SnowflakeParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites);
        }
    }
}
