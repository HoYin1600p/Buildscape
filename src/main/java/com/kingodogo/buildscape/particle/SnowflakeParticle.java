package com.kingodogo.buildscape.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SnowflakeParticle extends TextureSheetParticle {
    
    // Static map to store color queues for particles
    // Key: "x,y,z" string, Value: ColorEntry
    private static final java.util.Map<String, ColorEntry> POSITION_COLOR_MAP = new java.util.concurrent.ConcurrentHashMap<>();

    private static class ColorEntry {
        final String colorCode;
        final long timestamp;

        ColorEntry(String colorCode) {
            this.colorCode = colorCode;
            this.timestamp = System.currentTimeMillis();
        }
    }

    protected SnowflakeParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        
        // Use setSpriteFromAge to properly initialize the sprite (like working particles)
        // Then randomly pick a sprite from the set (snowflake has 9 different textures)
        this.setSpriteFromAge(sprites);
        this.pickSprite(sprites);
        
        // Snowflake properties
        this.gravity = 0.05F; // Gentle fall
        this.lifetime = 60 + level.random.nextInt(40); // 60-100 ticks lifetime (approx 3-5 seconds)
        this.hasPhysics = true;
        
        // Random size variation
        this.quadSize = 0.1F + level.random.nextFloat() * 0.1F; // 0.1 to 0.2 size
        
        // Slight rotation and drift
        this.xd = xSpeed + (level.random.nextDouble() - 0.5) * 0.02;
        this.yd = ySpeed;
        this.zd = zSpeed + (level.random.nextDouble() - 0.5) * 0.02;
        
        // Color handling logic
        String positionKey = String.format("%.1f,%.1f,%.1f", x, y, z);
        String colorCode = null;

        ColorEntry colorEntry = POSITION_COLOR_MAP.remove(positionKey);
        if (colorEntry != null) {
            colorCode = colorEntry.colorCode;
        }

        if (colorCode != null && !colorCode.isEmpty()) {
            float[] color = parseColorCode(colorCode);
            this.setColor(color[0], color[1], color[2]);
        } else {
            // Set color to white so texture shows its natural colors without tinting
            this.setColor(1.0F, 1.0F, 1.0F);
        }
        
        this.alpha = 0.8F + level.random.nextFloat() * 0.2F;
        
        if (POSITION_COLOR_MAP.size() > 1000) {
            cleanupOldEntries();
        }
    }
    
    private static void cleanupOldEntries() {
        long now = System.currentTimeMillis();
        POSITION_COLOR_MAP.entrySet()
                .removeIf(entry -> {
                    ColorEntry colorEntry = entry.getValue();
                    return (now - colorEntry.timestamp) > 1000;
                });
    }

    private static float[] parseColorCode(String colorCode) {
        if (colorCode == null || colorCode.isEmpty() || !colorCode.startsWith("#")) {
            return new float[]{1.0F, 1.0F, 1.0F};
        }

        try {
            String hex = colorCode.substring(1);
            if (hex.length() != 6) {
                return new float[]{1.0F, 1.0F, 1.0F};
            }

            int r = Integer.parseInt(hex.substring(0, 2), 16);
            int g = Integer.parseInt(hex.substring(2, 4), 16);
            int b = Integer.parseInt(hex.substring(4, 6), 16);

            return new float[]{r / 255.0F, g / 255.0F, b / 255.0F};
        } catch (NumberFormatException e) {
            return new float[]{1.0F, 1.0F, 1.0F};
        }
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // Gentle rotation effect
        this.oRoll = this.roll;
        this.roll += 0.1F;
        
        // Fade out as it falls
        if (this.age > this.lifetime * 0.7F) {
            float fadeProgress = (this.age - this.lifetime * 0.7F) / (this.lifetime * 0.3F);
            this.alpha = (1.0F - fadeProgress) * 0.8F;
        }
    }
    
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
    
    public static void queueColor(double x, double y, double z, String colorCode) {
        if (colorCode != null && !colorCode.isEmpty()) {
            String positionKey = String.format("%.1f,%.1f,%.1f", x, y, z);
            POSITION_COLOR_MAP.put(positionKey, new ColorEntry(colorCode));
        }
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

