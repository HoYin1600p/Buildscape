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
public class CakeParticle extends TextureSheetParticle {
    
    private final SpriteSet sprites;

    protected CakeParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.sprites = sprites;
        
        this.lifetime = 60 + level.random.nextInt(40);
        this.gravity = 0.05F;
        this.hasPhysics = true;
        this.quadSize = 0.2F + level.random.nextFloat() * 0.1F;
        
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        
        this.setSpriteFromAge(sprites);
    }
    
    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
        
        if (this.age > this.lifetime * 0.7F) {
            float fadeProgress = (this.age - this.lifetime * 0.7F) / (this.lifetime * 0.3F);
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
            return new CakeParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites);
        }
    }
}
