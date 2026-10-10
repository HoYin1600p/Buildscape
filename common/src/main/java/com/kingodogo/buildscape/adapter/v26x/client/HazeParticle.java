package com.kingodogo.buildscape.adapter.v26x.client;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.joml.Quaternionf;

/** Slowly fading haze lies flat on the ground and remains visible from either side. */
public final class HazeParticle extends SingleQuadParticle {
    private final SpriteSet sprites;
    private final float rotationSpeed;
    private final float maxAlpha;
    private final float baseSize;

    private HazeParticle(ClientLevel level, double x, double y, double z, double r, double g, double b,
            SpriteSet sprites, RandomSource random) {
        super(level, x, y, z, sprites.first());
        this.sprites = sprites;
        this.baseSize = 2F + random.nextFloat() * 1.5F;
        this.quadSize = baseSize;
        this.lifetime = 240 + random.nextInt(120);
        this.gravity = 0;
        this.hasPhysics = false;
        this.xd = (random.nextFloat() - .5D) * .0003D;
        this.yd = 0;
        this.zd = (random.nextFloat() - .5D) * .0003D;
        this.rotationSpeed = (random.nextFloat() - .5F) * .0006F;
        this.roll = random.nextFloat() * (float) (Math.PI * 2);
        this.oRoll = roll;
        if (r > .01 || g > .01 || b > .01) {
            float variation = (random.nextFloat() - .5F) * .03F;
            setColor(Mth.clamp((float) r + variation, 0, 1), Mth.clamp((float) g + variation, 0, 1),
                    Mth.clamp((float) b + variation, 0, 1));
        } else {
            float tint = .94F + random.nextFloat() * .06F;
            setColor(tint, tint, tint);
        }
        this.maxAlpha = .32F + random.nextFloat() * .12F;
        this.alpha = 0;
        setSpriteFromAge(sprites);
    }

    public static <T extends ParticleOptions> ParticleProvider<T> provider(SpriteSet sprites) {
        return (type, level, x, y, z, r, g, b, random) -> new HazeParticle(level, x, y, z, r, g, b, sprites, random);
    }

    @Override public void tick() {
        xo = x; yo = y; zo = z;
        if (age++ >= lifetime) { remove(); return; }
        oRoll = roll;
        roll += rotationSpeed;
        move(xd, yd, zd);
        xd *= .99D; zd *= .99D;
        float progress = (float) age / lifetime;
        quadSize = baseSize * (1 + progress * .20F);
        alpha = progress < .20F ? progress / .20F * maxAlpha
                : progress > .65F ? (1 - (progress - .65F) / .35F) * maxAlpha : maxAlpha;
        setSpriteFromAge(sprites);
    }

    @Override public void extract(QuadParticleRenderState state, Camera camera, float partialTick) {
        float angle = Mth.lerp(partialTick, oRoll, roll);
        extractRotatedQuad(state, camera, new Quaternionf().rotationX((float) Math.PI / 2).rotateZ(angle), partialTick);
        extractRotatedQuad(state, camera, new Quaternionf().rotationX(-(float) Math.PI / 2).rotateZ(-angle), partialTick);
    }

    @Override protected Layer getLayer() { return Layer.TRANSLUCENT; }
}
