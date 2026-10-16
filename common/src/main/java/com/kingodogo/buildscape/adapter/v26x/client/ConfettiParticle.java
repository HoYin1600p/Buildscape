package com.kingodogo.buildscape.adapter.v26x.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;

/** Confetti keeps one randomly selected shape and colour for its entire lifetime. */
public final class ConfettiParticle extends SingleQuadParticle {
    private static final int[] COLORS = {
        0xFF0000, 0x00FFFF, 0x1919EA, 0x3CDFFF, 0xFFFF00, 0xFF5C00,
        0xBFFE00, 0x39FF14, 0xF686B7, 0xAB87FF, 0xFF00FF
    };

    private final float rotationSpeed;

    private ConfettiParticle(ClientLevel level, double x, double y, double z,
                             double dx, double dy, double dz, SpriteSet sprites, RandomSource spawnRandom) {
        super(level, x, y, z, dx, dy, dz, pickSprite(sprites, spawnRandom));
        int rgb = pickColor(spawnRandom);
        setColor(((rgb >> 16) & 255) / 255.0F, ((rgb >> 8) & 255) / 255.0F, (rgb & 255) / 255.0F);
        this.gravity = 0.05F + spawnRandom.nextFloat() * 0.04F;
        this.lifetime = 70 + spawnRandom.nextInt(40);
        this.hasPhysics = true;
        this.quadSize = 0.08F + spawnRandom.nextFloat() * 0.12F;
        this.roll = spawnRandom.nextFloat() * (float) (Math.PI * 2.0D);
        this.oRoll = this.roll;
        this.rotationSpeed = (spawnRandom.nextFloat() - 0.5F) * 0.5F;
        this.xd = dx;
        this.yd = dy;
        this.zd = dz;
        this.alpha = 1.0F;
    }

    public static TextureAtlasSprite pickSprite(SpriteSet sprites, RandomSource random) {
        // These textures are different shapes, not frames of an age-based animation.
        return sprites.get(random);
    }

    public static int pickColor(RandomSource random) {
        return COLORS[random.nextInt(COLORS.length)];
    }

    public static <T extends ParticleOptions> ParticleProvider<T> provider(SpriteSet sprites) {
        return (type, level, x, y, z, dx, dy, dz, random) ->
                new ConfettiParticle(level, x, y, z, dx, dy, dz, sprites, random);
    }

    @Override public void tick() {
        super.tick();
        this.oRoll = this.roll;
        this.roll += this.rotationSpeed;
        this.xd *= 0.98D;
        this.zd *= 0.98D;
        if (this.age % 5 == 0) {
            this.xd += (this.random.nextDouble() - 0.5D) * 0.01D;
            this.zd += (this.random.nextDouble() - 0.5D) * 0.01D;
        }
        if (this.age > this.lifetime * 0.8F) {
            this.alpha = 1.0F - (float) (this.age - this.lifetime * 0.8F) / (this.lifetime * 0.2F);
        }
    }

    @Override protected Layer getLayer() { return Layer.TRANSLUCENT; }
}
