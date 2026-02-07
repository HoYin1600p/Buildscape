package com.kingodogo.buildscape.particle;

import com.kingodogo.buildscape.config.PillarParticleConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PillarSparkleParticle extends TextureSheetParticle {

    private final TextureAtlasSprite baseSprite;
    private static final int FRAME_COUNT = 10;
    private int currentFrame = 0;

    private static final Map<String, ColorEntry> POSITION_COLOR_MAP =
            new ConcurrentHashMap<>();

    private static final Map<String, Float> POSITION_SIZE_MAP =
            new ConcurrentHashMap<>();

    private static class ColorEntry {

        final String colorCode;
        final long timestamp;

        ColorEntry(String colorCode) {
            this.colorCode = colorCode;
            this.timestamp = System.currentTimeMillis();
        }
    }

    protected PillarSparkleParticle(
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
        this.setSpriteFromAge(sprites);
        this.baseSprite = this.sprite;
        this.xd = dx;
        this.yd = dy;
        this.zd = dz;
        this.lifetime = 100;

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
                colorCode = "#FFFFFF";
            }
        }

        Float sizeMultiplier = POSITION_SIZE_MAP.remove(positionKey);
        if (sizeMultiplier == null || sizeMultiplier <= 0) {
            sizeMultiplier = 1.0F;
        }

        this.quadSize = 0.3F * sizeMultiplier;
        this.hasPhysics = false;

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
        if (
                colorCode == null || colorCode.isEmpty() || !colorCode.startsWith("#")
        ) {
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
        this.currentFrame = (this.age * FRAME_COUNT) / this.lifetime;
        if (this.currentFrame >= FRAME_COUNT) this.currentFrame = FRAME_COUNT - 1;

        float fadeOut = 0.9F;
        if (this.age > this.lifetime * fadeOut) {
            float fadeProgress =
                    (float) (this.age - this.lifetime * fadeOut) /
                            (this.lifetime * (1.0F - fadeOut));
            this.alpha = 1.0F - fadeProgress;
        } else {
            this.alpha = 1.0F;
        }
    }

    @Override
    protected float getU0() {
        return this.baseSprite.getU0();
    }

    @Override
    protected float getU1() {
        return this.baseSprite.getU1();
    }

    @Override
    protected float getV0() {
        float frameHeight = 1.0F / FRAME_COUNT;
        float minV = this.currentFrame * frameHeight;
        return (
                this.baseSprite.getV0() +
                        (this.baseSprite.getV1() - this.baseSprite.getV0()) * minV
        );
    }

    @Override
    protected float getV1() {
        float frameHeight = 1.0F / FRAME_COUNT;
        float maxV = (this.currentFrame + 1) * frameHeight;
        return (
                this.baseSprite.getV0() +
                        (this.baseSprite.getV1() - this.baseSprite.getV0()) * maxV
        );
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public int getLightColor(float partialTick) {
        return 0xF000F0;
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
            return new PillarSparkleParticle(level, x, y, z, dx, dy, dz, sprites);
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

    public static void queueSize(
            double x,
            double y,
            double z,
            float sizeMultiplier
    ) {
        if (sizeMultiplier > 0) {
            String positionKey = String.format("%.1f,%.1f,%.1f", x, y, z);
            POSITION_SIZE_MAP.put(positionKey, sizeMultiplier);
        }
    }

    @Deprecated
    public static void queueColor(String colorCode) {
    }
}
