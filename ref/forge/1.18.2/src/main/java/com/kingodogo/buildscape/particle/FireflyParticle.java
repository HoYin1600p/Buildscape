package com.kingodogo.buildscape.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@OnlyIn(Dist.CLIENT)
public class FireflyParticle extends TextureSheetParticle {

    public static final int MAX_FIREFLIES_IN_RANGE = 500;
    public static final double RANGE = 10.0D;
    public static final double RANGE_SQR = RANGE * RANGE;

    private static final Set<FireflyParticle> ACTIVE_FIREFLIES = ConcurrentHashMap.newKeySet();

    private final SpriteSet sprites;
    private final float baseAlpha;

    public static int getFireflyCountInRange(ClientLevel level, double x, double y, double z, double rangeSqr) {
        int count = 0;
        Iterator<FireflyParticle> it = ACTIVE_FIREFLIES.iterator();
        while (it.hasNext()) {
            FireflyParticle p = it.next();
            if (!p.isAlive() || p.level != level) {
                it.remove();
                continue;
            }
            double dx = p.x - x;
            double dy = p.y - y;
            double dz = p.z - z;
            if (dx * dx + dy * dy + dz * dz <= rangeSqr) {
                count++;
                if (count >= MAX_FIREFLIES_IN_RANGE) {
                    return count;
                }
            }
        }
        return count;
    }

    protected FireflyParticle(ClientLevel level, double x, double y, double z,
                              double xSpeed, double ySpeed, double zSpeed,
                              SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.sprites = sprites;

        this.lifetime = 200 + level.random.nextInt(101);
        this.gravity = 0.0F;
        this.hasPhysics = false;

        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

        this.quadSize = 0.08F + level.random.nextFloat() * 0.05F;

        this.baseAlpha = 1.0F;
        this.alpha = 0.0F;

        float greenVariation = 0.92F + level.random.nextFloat() * 0.06F;
        float blueVariation = 0.55F + level.random.nextFloat() * 0.15F;
        this.setColor(1.0F, greenVariation, blueVariation);

        this.pickSprite(sprites);

        if (getFireflyCountInRange(level, x, y, z, RANGE_SQR) >= MAX_FIREFLIES_IN_RANGE) {
            this.remove();
            return;
        }
        ACTIVE_FIREFLIES.add(this);
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

        this.xd += (this.random.nextFloat() - 0.5F) * 0.002F;
        this.yd += (this.random.nextFloat() - 0.5F) * 0.001F;
        this.zd += (this.random.nextFloat() - 0.5F) * 0.002F;

        this.xd = Mth.clamp(this.xd, -0.02, 0.02);
        this.yd = Mth.clamp(this.yd, -0.01, 0.01);
        this.zd = Mth.clamp(this.zd, -0.02, 0.02);

        this.move(this.xd, this.yd, this.zd);

        float progress = (float) this.age / (float) this.lifetime;
        if (progress < 0.3F) {
            this.alpha = this.baseAlpha * (progress / 0.3F);
        } else if (progress > 0.7F) {
            this.alpha = this.baseAlpha * ((1.0F - progress) / 0.3F);
        } else {
            this.alpha = this.baseAlpha;
        }
        this.alpha = Mth.clamp(this.alpha, 0.0F, 1.0F);
    }

    @Override
    public void remove() {
        super.remove();
        ACTIVE_FIREFLIES.remove(this);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public int getLightColor(float partialTick) {
        return 240 | (240 << 16);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            if (getFireflyCountInRange(level, x, y, z, RANGE_SQR) >= MAX_FIREFLIES_IN_RANGE) {
                return null;
            }
            return new FireflyParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites);
        }
    }
}
