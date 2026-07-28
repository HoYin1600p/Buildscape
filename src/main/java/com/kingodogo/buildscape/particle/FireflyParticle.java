package com.kingodogo.buildscape.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FireflyParticle extends TextureSheetParticle {

    private final SpriteSet sprites;
    private final float baseAlpha;

    protected FireflyParticle(ClientLevel level, double x, double y, double z,
                              double xSpeed, double ySpeed, double zSpeed,
                              SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.sprites = sprites;

        // Vanilla-like lifetime: 40-80 ticks (2-4 seconds)
        this.lifetime = 40 + level.random.nextInt(40);
        this.gravity = 0.0F;
        this.hasPhysics = false;

        // Slow drift velocities
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

        // Small particle size
        this.quadSize = 0.025F + level.random.nextFloat() * 0.02F;

        // Start invisible, fade in then out
        this.baseAlpha = 0.8F + level.random.nextFloat() * 0.2F;
        this.alpha = 0.0F;

        // Warm yellowish-green glow color, like a real firefly
        float greenVariation = 0.9F + level.random.nextFloat() * 0.1F;
        this.setColor(1.0F, greenVariation, 0.3F + level.random.nextFloat() * 0.2F);

        this.pickSprite(sprites);
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

        // Gentle random drift
        this.xd += (this.random.nextFloat() - 0.5F) * 0.002F;
        this.yd += (this.random.nextFloat() - 0.5F) * 0.001F;
        this.zd += (this.random.nextFloat() - 0.5F) * 0.002F;

        // Clamp velocities
        this.xd = Mth.clamp(this.xd, -0.02, 0.02);
        this.yd = Mth.clamp(this.yd, -0.01, 0.01);
        this.zd = Mth.clamp(this.zd, -0.02, 0.02);

        this.move(this.xd, this.yd, this.zd);

        // Fade in/out: smooth pulse
        float progress = (float) this.age / (float) this.lifetime;
        if (progress < 0.2F) {
            // Fade in over first 20%
            this.alpha = this.baseAlpha * (progress / 0.2F);
        } else if (progress > 0.7F) {
            // Fade out over last 30%
            this.alpha = this.baseAlpha * (1.0F - (progress - 0.7F) / 0.3F);
        } else {
            // Full visibility with slight pulsing
            float pulse = (float) Math.sin(this.age * 0.3) * 0.15F;
            this.alpha = this.baseAlpha + pulse;
        }
        this.alpha = Mth.clamp(this.alpha, 0.0F, 1.0F);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    /**
     * Makes the particle glow (full brightness regardless of world light)
     */
    @Override
    public int getLightColor(float partialTick) {
        return 240 | (240 << 16);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new FireflyParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites);
        }
    }
}
