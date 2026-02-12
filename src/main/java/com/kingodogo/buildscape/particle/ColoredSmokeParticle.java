package com.kingodogo.buildscape.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ColoredSmokeParticle extends TextureSheetParticle {

    protected ColoredSmokeParticle(ClientLevel level, double x, double y, double z,
                                   double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z);

        this.quadSize = 0.3f;
        this.lifetime = 200 + level.random.nextInt(100);
        this.gravity = 3.0E-6f;
        this.hasPhysics = false;

        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

        // Pick one random sprite and keep it (no animation)
        this.pickSprite(sprites);

        // Get color from position queue
        String colorCode = SmokeColorRegistry.consumeColor(x, y, z);

        if (colorCode != null && !colorCode.isEmpty()) {
            float[] color = parseColorCode(colorCode);
            this.setColor(color[0], color[1], color[2]);
        } else {
            this.setColor(0.9f, 0.9f, 0.9f);
        }

        this.alpha = 0.9f;
    }

    private static float[] parseColorCode(String colorCode) {
        if (colorCode == null || colorCode.isEmpty() || !colorCode.startsWith("#")) {
            return new float[]{0.9f, 0.9f, 0.9f};
        }
        try {
            String hex = colorCode.substring(1);
            if (hex.length() != 6) return new float[]{0.9f, 0.9f, 0.9f};
            int r = Integer.parseInt(hex.substring(0, 2), 16);
            int g = Integer.parseInt(hex.substring(2, 4), 16);
            int b = Integer.parseInt(hex.substring(4, 6), 16);
            return new float[]{r / 255.0f, g / 255.0f, b / 255.0f};
        } catch (NumberFormatException e) {
            return new float[]{0.9f, 0.9f, 0.9f};
        }
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        this.yd -= this.gravity;
        this.move(this.xd, this.yd, this.zd);

        // Fade out
        float progress = (float) this.age / (float) this.lifetime;
        this.alpha = 0.9f * (1.0f - progress);
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
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new ColoredSmokeParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites);
        }
    }
}
