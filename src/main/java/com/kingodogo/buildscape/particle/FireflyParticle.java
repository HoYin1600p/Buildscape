package com.kingodogo.buildscape.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import com.kingodogo.buildscape.block.ModBlocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FireflyParticle extends TextureSheetParticle {

    private static final java.util.Map<BlockPos, Integer> BUSH_PARTICLE_COUNTS = new java.util.HashMap<>();
    private static final java.util.Map<BlockPos, Integer> BUSH_LIMITS = new java.util.HashMap<>();

    private final SpriteSet sprites;
    private final float baseAlpha;
    private BlockPos spawnedBushPos;

    protected FireflyParticle(ClientLevel level, double x, double y, double z,
                              double xSpeed, double ySpeed, double zSpeed,
                              SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.sprites = sprites;

        // Custom lifetime: 200-300 game ticks (10-15 seconds)
        this.lifetime = 200 + level.random.nextInt(101);
        this.gravity = 0.0F;
        this.hasPhysics = false;

        // Slow drift velocities
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

        // Small particle size - increased to 0.08F-0.13F as requested
        this.quadSize = 0.08F + level.random.nextFloat() * 0.05F;

        // Start invisible, fade in then out
        this.baseAlpha = 1.0F;
        this.alpha = 0.0F;

        // Opaque pale yellow glow color, like requested
        float greenVariation = 0.92F + level.random.nextFloat() * 0.06F;
        float blueVariation = 0.55F + level.random.nextFloat() * 0.15F;
        this.setColor(1.0F, greenVariation, blueVariation);

        this.pickSprite(sprites);

        // Find nearest firefly bush within 6 blocks
        BlockPos bushPos = null;
        double minDst = Double.MAX_VALUE;
        BlockPos particlePos = new BlockPos((int) x, (int) y, (int) z);
        for (BlockPos p : BlockPos.betweenClosed(particlePos.offset(-6, -6, -6), particlePos.offset(6, 6, 6))) {
            if (level.getBlockState(p).is(ModBlocks.FIREFLY_BUSH.get())) {
                double dst = p.distSqr(particlePos);
                if (dst < minDst) {
                    minDst = dst;
                    bushPos = p.immutable();
                }
            }
        }

        if (bushPos != null) {
            this.spawnedBushPos = bushPos;
            int limit = BUSH_LIMITS.computeIfAbsent(bushPos, k -> 30 + level.random.nextInt(21));
            int currentCount = BUSH_PARTICLE_COUNTS.getOrDefault(bushPos, 0);
            if (currentCount >= limit) {
                this.remove();
                return;
            }
            BUSH_PARTICLE_COUNTS.put(bushPos, currentCount + 1);
        }
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

        // Gentle random drift
        this.xd += (this.random.nextFloat() - 0.5F) * 0.002F;
        this.yd += (this.random.nextFloat() - 0.5F) * 0.001F;
        this.zd += (this.random.nextFloat() - 0.5F) * 0.002F;

        // Clamp velocities
        this.xd = Mth.clamp(this.xd, -0.02, 0.02);
        this.yd = Mth.clamp(this.yd, -0.01, 0.01);
        this.zd = Mth.clamp(this.zd, -0.02, 0.02);

        this.move(this.xd, this.yd, this.zd);

        // Fade in/out: smooth pulse
        float progress = (float) this.age / (float) this.lifetime;
        if (progress < 0.3F) {
            // Fade in over first 30%
            this.alpha = this.baseAlpha * (progress / 0.3F);
        } else if (progress > 0.7F) {
            // Fade out over last 30%
            this.alpha = this.baseAlpha * ((1.0F - progress) / 0.3F);
        } else {
            // Remain fully opaque
            this.alpha = this.baseAlpha;
        }
        this.alpha = Mth.clamp(this.alpha, 0.0F, 1.0F);
    }

    @Override
    public void remove() {
        super.remove();
        if (this.spawnedBushPos != null) {
            int currentCount = BUSH_PARTICLE_COUNTS.getOrDefault(this.spawnedBushPos, 0);
            if (currentCount > 1) {
                BUSH_PARTICLE_COUNTS.put(this.spawnedBushPos, currentCount - 1);
            } else {
                BUSH_PARTICLE_COUNTS.remove(this.spawnedBushPos);
                BUSH_LIMITS.remove(this.spawnedBushPos);
            }
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    /**
     * Makes the particle glow (full brightness regardless of world light)
     */
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
            return new FireflyParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites);
        }
    }
}
