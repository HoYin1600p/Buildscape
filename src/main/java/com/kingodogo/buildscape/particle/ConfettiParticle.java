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

import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class ConfettiParticle extends TextureSheetParticle {
    
    private final float rotationSpeed;
    
    protected ConfettiParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);

        this.setSpriteFromAge(sprites);
        this.pickSprite(sprites);
        
        Random rand = level.random;

        this.setColor(1.0F, 1.0F, 1.0F);

        this.gravity = 0.15F + rand.nextFloat() * 0.1F;
        this.lifetime = 60 + rand.nextInt(40);
        this.hasPhysics = true;

        this.quadSize = 0.08F + rand.nextFloat() * 0.12F;

        this.roll = rand.nextFloat() * (float)(Math.PI * 2);
        this.oRoll = this.roll;

        this.rotationSpeed = (rand.nextFloat() - 0.5F) * 0.5F;

        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

        this.alpha = 1.0F;
    }
    
    @Override
    public void tick() {
        super.tick();

        this.oRoll = this.roll;
        this.roll += this.rotationSpeed;

        this.xd *= 0.98;
        this.zd *= 0.98;

        if (this.age % 5 == 0) {
            this.xd += (this.level.random.nextDouble() - 0.5) * 0.01;
            this.zd += (this.level.random.nextDouble() - 0.5) * 0.01;
        }

        if (this.age > this.lifetime * 0.8F) {
            float fadeProgress = (float)(this.age - this.lifetime * 0.8F) / (this.lifetime * 0.2F);
            this.alpha = 1.0F - fadeProgress;
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
            return new ConfettiParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites);
        }
    }
}

