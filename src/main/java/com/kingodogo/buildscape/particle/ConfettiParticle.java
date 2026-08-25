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

    private static final int[] VIBRANT_COLORS = {
        0xFF0000, // Red
        0x00FFFF, // Cyan
        0x1919EA, // Blue
        0x3CDFFF, // Light Blue
        0xFFFF00, // Yellow
        0xFF5C00, // Orange
        0xBFFE00, // Lime
        0x39FF14, // Green
        0xF686B7, // Pink
        0xAB87FF, // Purple
        0xFF00FF  // Magenta
    };

    protected ConfettiParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);

        // Pick a random sprite from the 7 confetti textures (confetti_1 through confetti_7)
        this.pickSprite(sprites);

        Random rand = level.random;

        // Pick a random vibrant color from the 11 base pillar colors
        int hexColor = VIBRANT_COLORS[rand.nextInt(VIBRANT_COLORS.length)];
        float r = ((hexColor >> 16) & 0xFF) / 255.0F;
        float g = ((hexColor >> 8) & 0xFF) / 255.0F;
        float b = (hexColor & 0xFF) / 255.0F;
        this.setColor(r, g, b);

        // Confetti physics - more realistic falling
        this.gravity = 0.05F + rand.nextFloat() * 0.04F; // Variable light gravity for gentle float
        this.lifetime = 70 + rand.nextInt(40); // 70-110 ticks lifetime
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

