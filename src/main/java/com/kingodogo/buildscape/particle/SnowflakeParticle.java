package com.kingodogo.buildscape.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SnowflakeParticle extends TextureSheetParticle {
    
    protected SnowflakeParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        
        // Use setSpriteFromAge to properly initialize the sprite (like working particles)
        // Then randomly pick a sprite from the set (snowflake has 9 different textures)
        this.setSpriteFromAge(sprites);
        this.pickSprite(sprites);
        
        // Snowflake properties
        this.gravity = 0.05F; // Gentle fall
        this.lifetime = 60 + level.random.nextInt(40); // 60-100 ticks lifetime (approx 3-5 seconds)
        this.hasPhysics = true;
        
        // Random size variation
        this.quadSize = 0.1F + level.random.nextFloat() * 0.1F; // 0.1 to 0.2 size
        
        // Slight rotation and drift
        this.xd = xSpeed + (level.random.nextDouble() - 0.5) * 0.02;
        this.yd = ySpeed;
        this.zd = zSpeed + (level.random.nextDouble() - 0.5) * 0.02;

        // Set color to white so texture shows its natural colors without tinting
        this.setColor(1.0F, 1.0F, 1.0F);
        this.alpha = 0.8F + level.random.nextFloat() * 0.2F;
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // Gentle rotation effect
        this.oRoll = this.roll;
        this.roll += 0.1F;
        
        // Fade out as it falls
        if (this.age > this.lifetime * 0.7F) {
            float fadeProgress = (float) (this.age - this.lifetime * 0.7F) / (this.lifetime * 0.3F);
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
            return new SnowflakeParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites);
        }
    }
}

