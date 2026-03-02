package com.kingodogo.buildscape.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SnowflakeStillParticle extends TextureSheetParticle {

    protected SnowflakeStillParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);

        this.setSpriteFromAge(sprites);
        this.pickSprite(sprites);

        this.gravity = 0.0F; // No gravity as requested
        this.lifetime = 200 + level.random.nextInt(200); // Match pillar snowflake lifetime
        this.hasPhysics = true; // Match pillar snowflake physics

        this.quadSize = 0.1F + level.random.nextFloat() * 0.1F; // Match pillar snowflake size

        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

        this.setColor(1.0F, 1.0F, 1.0F);
        this.alpha = 0.8F + level.random.nextFloat() * 0.2F; // Match pillar snowflake alpha
    }

    @Override
    public void tick() {
        super.tick();

        if (this.onGround) {
            this.remove();
            return;
        }

        this.oRoll = this.roll;
        this.roll += 0.1F;

        if (this.age > this.lifetime * 0.7F) {
            float fadeProgress = (this.age - this.lifetime * 0.7F) / (this.lifetime * 0.3F);
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
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new SnowflakeStillParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites);
        }
    }
}
