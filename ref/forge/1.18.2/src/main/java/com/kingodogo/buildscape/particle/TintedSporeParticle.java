package com.kingodogo.buildscape.particle;

import java.util.concurrent.ConcurrentLinkedQueue;

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
public class TintedSporeParticle extends TextureSheetParticle {

    private static final ConcurrentLinkedQueue<String> COLOR_QUEUE =
            new ConcurrentLinkedQueue<>();

    protected TintedSporeParticle(
            ClientLevel level,
            double x,
            double y,
            double z,
            double xSpeed,
            double ySpeed,
            double zSpeed,
            SpriteSet sprites
    ) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.setSpriteFromAge(sprites);
        this.gravity = 0.0F;
        this.lifetime = 400 + level.random.nextInt(400);
        this.hasPhysics = true;

        String colorCode = COLOR_QUEUE.poll();
        if (colorCode != null && !colorCode.isEmpty()) {
            float[] color = parseColorCode(colorCode);
            this.setColor(color[0], color[1], color[2]);
        } else {
            this.setColor(1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.yd -= 0.0005D;

            if (this.level.random.nextInt(8) == 0) {
                this.xd += (this.level.random.nextDouble() - 0.5D) * 0.001D;
                this.zd += (this.level.random.nextDouble() - 0.5D) * 0.001D;

                this.yd += (this.level.random.nextDouble() - 0.5D) * 0.0005D;
            }

            this.xd *= 0.999D;
            this.zd *= 0.999D;

            this.move(this.xd, this.yd, this.zd);

            if (this.age > this.lifetime - 60) {
                float fade = (float) (this.lifetime - this.age) / 60.0F;
                this.alpha = fade;
            }
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static void queueColor(String colorCode) {
        COLOR_QUEUE.offer(colorCode);
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

    @OnlyIn(Dist.CLIENT)
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
                double xSpeed,
                double ySpeed,
                double zSpeed
        ) {
            return new TintedSporeParticle(
                    level,
                    x,
                    y,
                    z,
                    xSpeed,
                    ySpeed,
                    zSpeed,
                    sprites
            );
        }
    }
}
