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
public class NoxiousGasParticle extends TextureSheetParticle {

    private final SpriteSet sprites;

    protected NoxiousGasParticle(ClientLevel level, double x, double y, double z,
            double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;

        this.quadSize = 0.24F + level.random.nextFloat() * 0.18F;
        this.lifetime = 60 + level.random.nextInt(40);
        this.gravity = -0.003F; // Rise up slowly
        this.hasPhysics = true;
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

        this.setColor(1.0F, 1.0F, 1.0F); // Custom texture has color
        this.alpha = 0.8F;

        this.setSpriteFromAge(sprites);
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

        this.setSpriteFromAge(this.sprites);

        // Rise up and drift
        this.yd -= this.gravity;
        this.move(this.xd, this.yd, this.zd);

        this.xd *= 0.96;
        this.yd *= 0.96;
        this.zd *= 0.96;

        // Slow fade out
        float progress = (float) this.age / (float) this.lifetime;
        if (progress > 0.5F) {
            float fadeProgress = (progress - 0.5F) / 0.5F;
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
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new NoxiousGasParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites);
        }
    }
}
