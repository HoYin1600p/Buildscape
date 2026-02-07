package com.kingodogo.buildscape.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class ConfettiParticle extends TextureSheetParticle {
    
    private final float rotationSpeed;
    
    protected ConfettiParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        
        // Use setSpriteFromAge to properly initialize the sprite (like working particles)
        // Then pick the sprite to ensure it's properly set (like snowflake does)
        this.setSpriteFromAge(sprites);
        this.pickSprite(sprites);
        
        Random rand = level.random;
        
        // Set color to white so texture shows its natural colors without tinting
        this.setColor(1.0F, 1.0F, 1.0F);
        
        // Confetti physics - more realistic falling
        this.gravity = 0.15F + rand.nextFloat() * 0.1F; // Variable gravity for different fall speeds
        this.lifetime = 60 + rand.nextInt(40); // 60-100 ticks lifetime (approx 3-5 seconds)
        this.hasPhysics = true;
        
        // Random size variation
        this.quadSize = 0.08F + rand.nextFloat() * 0.12F; // 0.08 to 0.2 size
        
        // Random initial rotation
        this.roll = rand.nextFloat() * (float)(Math.PI * 2);
        this.oRoll = this.roll;
        
        // Random rotation speed for tumbling effect
        this.rotationSpeed = (rand.nextFloat() - 0.5F) * 0.5F;
        
        // Add air resistance effect - particles slow down over time
        // Initial velocity is set, but we'll modify it in tick()
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        
        // Full opacity
        this.alpha = 1.0F;
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // Update rotation with acceleration for tumbling effect
        this.oRoll = this.roll;
        this.roll += this.rotationSpeed;
        // Apply rotation acceleration for more realistic tumbling
        // (simplified - in real physics this would be more complex)
        
        // Air resistance - particles slow down horizontally over time
        this.xd *= 0.98;
        this.zd *= 0.98;
        
        // Add slight horizontal drift for more realistic confetti movement
        if (this.age % 5 == 0) {
            this.xd += (this.level.random.nextDouble() - 0.5) * 0.01;
            this.zd += (this.level.random.nextDouble() - 0.5) * 0.01;
        }
        
        // Fade out near the end
        if (this.age > this.lifetime * 0.8F) {
            float fadeProgress = (float) (this.age - this.lifetime * 0.8F) / (this.lifetime * 0.2F);
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

