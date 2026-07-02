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
public class CascadeParticle extends TextureSheetParticle {

    private final SpriteSet sprites;

    protected CascadeParticle(ClientLevel level, double x, double y, double z,
            double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;

        this.quadSize = 0.5F + level.random.nextFloat() * 0.4F;
        this.lifetime = 30 + level.random.nextInt(30);
        this.gravity = 0.005F;
        this.hasPhysics = false;
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

        this.setColor(1.0F, 1.0F, 1.0F);
        this.alpha = 0.9F;

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

        if (this.level.getFluidState(new net.minecraft.core.BlockPos(this.x, this.y, this.z)).is(net.minecraft.tags.FluidTags.WATER)) {
            this.yd += 0.005F; // Float up gently underwater
        } else {
            this.yd -= this.gravity; // Fall down in air
        }

        // Move directly and update bounding box so particles render through water and frustum cull correctly
        this.move(this.xd, this.yd, this.zd);

        this.xd *= 0.95;
        this.zd *= 0.95;

        // Fade out in the last 40% of lifetime
        float progress = (float) this.age / (float) this.lifetime;
        if (progress > 0.6F) {
            float fadeProgress = (progress - 0.6F) / 0.4F;
            this.alpha = (1.0F - fadeProgress) * 0.9F;
        }
    }

    @Override
    protected int getLightColor(float partialTick) {
        // Full brightness so particles are visible underwater
        return 0xF000F0;
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
            return new CascadeParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites);
        }
    }
}
