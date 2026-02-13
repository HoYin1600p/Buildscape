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
public class BubbleParticle extends TextureSheetParticle {

    protected BubbleParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        
        // Use custom bubble sprite
        this.setSpriteFromAge(sprites);
        this.pickSprite(sprites);
        
        // Bubble properties - match snowflake trail properties
        this.gravity = 0.05F; // Gentle fall (same as snowflake)
        this.lifetime = 60 + level.random.nextInt(40); // 60-100 ticks lifetime (same as snowflake)
        this.hasPhysics = true;
        
        // Random size variation (same as snowflake)
        this.quadSize = 0.1F + level.random.nextFloat() * 0.1F; // 0.1 to 0.2 size
        
        // Slight drift (same as snowflake)
        this.xd = xSpeed + (level.random.nextDouble() - 0.5) * 0.02;
        this.yd = ySpeed;
        this.zd = zSpeed + (level.random.nextDouble() - 0.5) * 0.02;
        
        // Bubbles are white/transparent
        this.setColor(1.0F, 1.0F, 1.0F);
        this.alpha = 0.8F + level.random.nextFloat() * 0.2F; // 0.8 to 1.0 (same as snowflake)
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // Wobble effect
        this.oRoll = this.roll;
        this.roll += 0.05F;
        
        // Fade out before popping
        if (this.age > this.lifetime * 0.8F) {
            float fadeProgress = (this.age - this.lifetime * 0.8F) / (this.lifetime * 0.2F);
            this.alpha = (1.0F - fadeProgress) * 0.6F;
        }
    }
    
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
    
    @Override
    public int getLightColor(float partialTick) {
        return 0xF000F0; // Full brightness
    }
    
    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        
        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }
        
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new BubbleParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites);
        }
    }
}
