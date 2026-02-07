package com.kingodogo.buildscape.particle;

import com.kingodogo.buildscape.config.PillarParticleConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;

public class TintableHeartParticle extends TextureSheetParticle {

    private final TextureAtlasSprite baseSprite;
    
    // Static map to store color queues for particles
    // Key: "x,y,z" string, Value: ColorEntry
    private static final Map<String, ColorEntry> POSITION_COLOR_MAP = new ConcurrentHashMap<>();

    private static class ColorEntry {
        final String colorCode;
        final long timestamp;

        ColorEntry(String colorCode) {
            this.colorCode = colorCode;
            this.timestamp = System.currentTimeMillis();
        }
    }

    protected TintableHeartParticle(
            ClientLevel level,
            double x,
            double y,
            double z,
            double dx,
            double dy,
            double dz,
            SpriteSet sprites
    ) {
        super(level, x, y, z, dx, dy, dz);
        this.setSpriteFromAge(sprites); // Use sprite set
        this.baseSprite = this.sprite;
        
        // Heart particle movement logic (similar to vanilla heart)
        this.xd = dx;
        this.yd = dy;
        this.zd = dz;
        this.lifetime = 100; // Longer life
        
        // Initial upward velocity if none provided
        if (this.yd == 0) {
            this.yd = 0.1;
        }

        // Color handling logic (copied from PillarSparkleParticle)
        String positionKey = String.format("%.1f,%.1f,%.1f", x, y, z);
        String colorCode = null;

        ColorEntry colorEntry = POSITION_COLOR_MAP.remove(positionKey);
        if (colorEntry != null) {
            colorCode = colorEntry.colorCode;
        }

        if (colorCode == null || colorCode.isEmpty()) {
            PillarParticleConfig cfg = PillarParticleConfig.get();
            if (cfg.particle_color != null && !cfg.particle_color.isEmpty()) {
                colorCode = cfg.particle_color.get(0);
            } else {
                colorCode = "#FF0000"; // Default Red for heart
            }
        }

        this.quadSize = 0.3F; // Default size
        this.hasPhysics = false; // No collision

        float[] color = parseColorCode(colorCode);
        this.setColor(color[0], color[1], color[2]);

        this.alpha = 1.0F;

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
        if (colorCode == null || colorCode.isEmpty()) {
            return new float[]{1.0F, 0.0F, 0.0F}; // Red fallback
        }
        
        if (!colorCode.startsWith("#")) {
             return new float[]{1.0F, 0.0F, 0.0F};
        }

        try {
            String hex = colorCode.substring(1);
            if (hex.length() != 6) {
                return new float[]{1.0F, 0.0F, 0.0F};
            }

            int r = Integer.parseInt(hex.substring(0, 2), 16);
            int g = Integer.parseInt(hex.substring(2, 4), 16);
            int b = Integer.parseInt(hex.substring(4, 6), 16);

            return new float[]{r / 255.0F, g / 255.0F, b / 255.0F};
        } catch (NumberFormatException e) {
            return new float[]{1.0F, 0.0F, 0.0F};
        }
    }

    @Override
    public void tick() {
        super.tick();
        
        // Simple float up movement
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        
        if (this.age++ >= this.lifetime) {
             this.remove();
        } else {
             this.move(this.xd, this.yd, this.zd);
             this.xd *= 0.99;
             this.yd *= 0.99;
             this.zd *= 0.99;
             
             // Fade out
             if (this.age > this.lifetime - 20) {
                 this.alpha = (this.lifetime - this.age) / 20.0f;
             }
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
    
    @Override
    public int getLightColor(float partialTick) {
         return 0xF000F0; // Full brightness
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(
                SimpleParticleType type,
                ClientLevel level,
                double x,
                double y,
                double z,
                double dx,
                double dy,
                double dz
        ) {
            return new TintableHeartParticle(level, x, y, z, dx, dy, dz, sprites);
        }
    }

    public static void queueColor(
            double x,
            double y,
            double z,
            String colorCode
    ) {
        if (colorCode != null && !colorCode.isEmpty()) {
            String positionKey = String.format("%.1f,%.1f,%.1f", x, y, z);
            POSITION_COLOR_MAP.put(positionKey, new ColorEntry(colorCode));
        }
    }
}
