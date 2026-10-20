package com.kingodogo.buildscape.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@OnlyIn(Dist.CLIENT)
public class FireflyParticle extends TextureSheetParticle {

    public static final int MAX_FIREFLIES_IN_RANGE = 500;
    public static final double RANGE = 10.0D;
    public static final double RANGE_SQR = RANGE * RANGE;

    private static final Map<BlockPos, Set<FireflyParticle>> ACTIVE_FIREFLIES = new ConcurrentHashMap<>();
    private BlockPos cell;
    private long lastTickTime;

    private final SpriteSet sprites;
    private final float baseAlpha;

    public static int getFireflyCountInRange(ClientLevel level, double x, double y, double z, double rangeSqr) {
        int count = 0;
        double radius = Math.sqrt(rangeSqr);
        for (int cx = cellCoordinate(x - radius); cx <= cellCoordinate(x + radius); cx++) {
            for (int cy = cellCoordinate(y - radius); cy <= cellCoordinate(y + radius); cy++) {
                for (int cz = cellCoordinate(z - radius); cz <= cellCoordinate(z + radius); cz++) {
                    BlockPos cellPos = new BlockPos(cx, cy, cz);
                    Set<FireflyParticle> particles = ACTIVE_FIREFLIES.get(cellPos);
                    if (particles == null) {
                        continue;
                    }
                    particles.removeIf(p -> !p.isAlive() || p.level != level
                            || level.getGameTime() - p.lastTickTime > 2L);
                    if (particles.isEmpty()) {
                        ACTIVE_FIREFLIES.remove(cellPos, particles);
                        continue;
                    }
                    for (FireflyParticle p : particles) {
                        if (!p.isAlive() || p.level != level) {
                            continue;
                        }
                        double dx = p.x - x;
                        double dy = p.y - y;
                        double dz = p.z - z;
                        if (dx * dx + dy * dy + dz * dz <= rangeSqr && ++count >= MAX_FIREFLIES_IN_RANGE) {
                            return count;
                        }
                    }
                }
            }
        }
        return count;
    }

    protected FireflyParticle(ClientLevel level, double x, double y, double z,
            double xSpeed, double ySpeed, double zSpeed,
            SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.lastTickTime = level.getGameTime();
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

        updateCell();
    }

    private static int cellCoordinate(double coordinate) {
        return Mth.floor(coordinate / RANGE);
    }

    private void updateCell() {
        int cx = cellCoordinate(x);
        int cy = cellCoordinate(y);
        int cz = cellCoordinate(z);
        if (cell != null && cell.getX() == cx && cell.getY() == cy && cell.getZ() == cz) {
            return;
        }
        leaveCell();
        cell = new BlockPos(cx, cy, cz);
        ACTIVE_FIREFLIES.computeIfAbsent(cell, key -> ConcurrentHashMap.newKeySet()).add(this);
    }

    private void leaveCell() {
        if (cell == null) {
            return;
        }
        Set<FireflyParticle> particles = ACTIVE_FIREFLIES.get(cell);
        if (particles != null) {
            particles.remove(this);
            if (particles.isEmpty()) {
                ACTIVE_FIREFLIES.remove(cell, particles);
            }
        }
        cell = null;
    }

    public static void clearActiveParticles() {
        ACTIVE_FIREFLIES.clear();
    }

    @Override
    public void tick() {
        this.lastTickTime = this.level.getGameTime();
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
        updateCell();

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
        leaveCell();
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
