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
public class CakeParticle extends TextureSheetParticle {
    
    private final SpriteSet sprites;
    
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

    protected CakeParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.sprites = sprites;
        
        this.lifetime = 60 + level.random.nextInt(40);
        this.gravity = 0.05F;
        this.hasPhysics = true;
        this.quadSize = 0.2F + level.random.nextFloat() * 0.1F;
        
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        
        this.setSpriteFromAge(sprites);
        
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
            this.setColor(1.0F, 1.0F, 1.0F); // Default white
        }
        
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
        this.setSpriteFromAge(this.sprites);
        
        if (this.age > this.lifetime * 0.7F) {
            float fadeProgress = (this.age - this.lifetime * 0.7F) / (this.lifetime * 0.3F);
            this.alpha = 1.0F - fadeProgress;
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
            return new CakeParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites);
        }
    }
}
