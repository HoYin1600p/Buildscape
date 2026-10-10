package com.kingodogo.buildscape.particle;

import com.kingodogo.buildscape.config.PillarParticleConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;


public class PillarSparkleParticle extends TextureSheetParticle {

    private final TextureAtlasSprite baseSprite;
    private static final int FRAME_COUNT = 10;
    private int currentFrame = 0;

    private static final ThreadLocal<PendingSpawn> PENDING = ThreadLocal.withInitial(PendingSpawn::new);

    private static final class PendingSpawn {
        double x;
        double y;
        double z;
        String colorCode;
        float sizeMultiplier;

        void target(double x, double y, double z) {
            if (this.x != x || this.y != y || this.z != z) {
                this.x = x;
                this.y = y;
                this.z = z;
                this.colorCode = null;
                this.sizeMultiplier = 0.0F;
            }
        }

        boolean matches(double x, double y, double z) {
            return this.x == x && this.y == y && this.z == z;
        }

        void clear() {
            this.colorCode = null;
            this.sizeMultiplier = 0.0F;
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

        PendingSpawn pending = PENDING.get();
        boolean hasPending = pending.matches(x, y, z);
        float[] colors = parseColorOrDefault(hasPending ? pending.colorCode : null);
        this.setColor(colors[0], colors[1], colors[2]);

        float sizeMultiplier = hasPending && pending.sizeMultiplier > 0 ? pending.sizeMultiplier : 1.0F;
        if (hasPending) {
            pending.clear();
        }

        this.quadSize = 0.2F * sizeMultiplier;
        this.hasPhysics = false;

        this.alpha = 1.0F;
    }

    public static float[] parseColorCode(String colorCode) {
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

    private static float[] parseColorOrDefault(String colorCode) {
        if (colorCode == null || colorCode.isEmpty()) {
            PillarParticleConfig cfg = PillarParticleConfig.get();
            if (cfg.particle_color != null && !cfg.particle_color.isEmpty()) {
                colorCode = cfg.particle_color.get(0);
            } else {
                colorCode = "#FFFFFF";
            }
        }
        return parseColorCode(colorCode);
    }

    @Override
    public void tick() {
        super.tick();
        this.currentFrame = (this.age * FRAME_COUNT) / this.lifetime;
        if (this.currentFrame >= FRAME_COUNT) this.currentFrame = FRAME_COUNT - 1;

        float fadeOut = 0.9F;
        if (this.age > this.lifetime * fadeOut) {
            float fadeProgress =
                    (this.age - this.lifetime * fadeOut) /
                            (this.lifetime * (1.0F - fadeOut));
            this.alpha = 1.0F - fadeProgress;
        } else {
            this.alpha = 1.0F;
        }
    }

    @Override
    protected float getU0() {
        float u0 = this.baseSprite.getU0();
        float u1 = this.baseSprite.getU1();
        return u0 + (u1 - u0) * 0.02F;
    }

    @Override
    protected float getU1() {
        float u0 = this.baseSprite.getU0();
        float u1 = this.baseSprite.getU1();
        return u1 - (u1 - u0) * 0.02F;
    }

    @Override
    protected float getV0() {
        float frameHeight = 1.0F / FRAME_COUNT;
        float minV = this.currentFrame * frameHeight;
        float v0 = this.baseSprite.getV0();
        float v1 = this.baseSprite.getV1();
        return v0 + (v1 - v0) * (minV + frameHeight * 0.02F);
    }

    @Override
    protected float getV1() {
        float frameHeight = 1.0F / FRAME_COUNT;
        float maxV = (this.currentFrame + 1) * frameHeight;
        float v0 = this.baseSprite.getV0();
        float v1 = this.baseSprite.getV1();
        return v0 + (v1 - v0) * (maxV - frameHeight * 0.02F);
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
            PendingSpawn pending = PENDING.get();
            pending.target(x, y, z);
            pending.colorCode = colorCode;
        }
    }

    public static void queueSize(
            double x,
            double y,
            double z,
            float sizeMultiplier
    ) {
        if (sizeMultiplier > 0) {
            PendingSpawn pending = PENDING.get();
            pending.target(x, y, z);
            pending.sizeMultiplier = sizeMultiplier;
        }
    }

    @Deprecated
    public static void queueColor(String colorCode) {
    }
}
