package com.kingodogo.buildscape.adapter.v121x.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import java.util.List;
public class HazeParticle extends TextureSheetParticle {

    private static final float CELL_SIZE = 0.5F;
    private final SpriteSet sprites;
    private final float rotSpeed;
    private final float maxAlpha;
    private final float baseSize;
    private final double originX, originZ;
    private final HazeGeometry geometry;

    protected HazeParticle(ClientLevel level, double x, double y, double z,
                           double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;

        this.baseSize = 2.0F + level.random.nextFloat() * 1.5F;
        this.quadSize = this.baseSize;
        this.lifetime = 240 + level.random.nextInt(120);
        this.gravity = 0.0F;
        this.hasPhysics = false;

        this.xd = (level.random.nextFloat() - 0.5D) * 0.0003D;
        this.yd = 0.0D;
        this.zd = (level.random.nextFloat() - 0.5D) * 0.0003D;

        this.rotSpeed = (level.random.nextFloat() - 0.5F) * 0.0006F;
        this.roll = level.random.nextFloat() * ((float) Math.PI * 2.0F);
        this.oRoll = this.roll;

        if (xSpeed > 0.01 || ySpeed > 0.01 || zSpeed > 0.01) {
            float var = (level.random.nextFloat() - 0.5F) * 0.03F;
            float r = Mth.clamp((float) xSpeed + var, 0.0F, 1.0F);
            float g = Mth.clamp((float) ySpeed + var, 0.0F, 1.0F);
            float b = Mth.clamp((float) zSpeed + var, 0.0F, 1.0F);
            this.setColor(r, g, b);
        } else {
            float tint = 0.94F + level.random.nextFloat() * 0.06F;
            this.setColor(tint, tint, tint);
        }

        this.maxAlpha = 0.32F + level.random.nextFloat() * 0.12F;
        this.alpha = 0.0F;

        this.originX = x;
        this.originZ = z;
        int radius = Math.min((HazeMask.MAX_GRID_SIZE - 1) / 2,
                Mth.ceil((baseSize * 1.2 * Math.sqrt(2) + 0.02) / CELL_SIZE));
        float maskMin = -(radius + 0.5F) * CELL_SIZE;
        Long2ObjectMap<List<AABB>> shapes = new Long2ObjectOpenHashMap<>();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int gridSize = radius * 2 + 1;
        HazeMask mask = HazeMask.compute(gridSize, gridSize, radius, radius, (cellX, cellZ) -> {
            double minX = originX + maskMin + cellX * CELL_SIZE;
            double minZ = originZ + maskMin + cellZ * CELL_SIZE;
            return HazeMask.blocksCell(level, pos, shapes, minX, minZ,
                    minX + CELL_SIZE, minZ + CELL_SIZE, y);
        });
        this.geometry = new HazeGeometry(mask.rectangles(), maskMin, CELL_SIZE);
        if (mask.rectangles().isEmpty()) remove();

        this.setSpriteFromAge(sprites);
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

        this.oRoll = this.roll;
        this.roll += this.rotSpeed;

        this.move(this.xd, this.yd, this.zd);

        this.xd *= 0.99;
        this.zd *= 0.99;

        float progress = (float) this.age / (float) this.lifetime;
        this.quadSize = this.baseSize * (1.0F + progress * 0.20F);

        if (progress < 0.20F) {
            this.alpha = (progress / 0.20F) * this.maxAlpha;
        } else if (progress > 0.65F) {
            this.alpha = (1.0F - (progress - 0.65F) / 0.35F) * this.maxAlpha;
        } else {
            this.alpha = this.maxAlpha;
        }

        this.setSpriteFromAge(this.sprites);
    }

    @Override
    public void render(VertexConsumer consumer, Camera camera, float partialTick) {
        if (alpha <= 0) return;
        Vec3 cameraPosition = camera.getPosition();
        float renderY = (float) (Mth.lerp((double) partialTick, yo, y) - cameraPosition.y());
        float dx = (float) (Mth.lerp((double) partialTick, xo, x) - originX);
        float dz = (float) (Mth.lerp((double) partialTick, zo, z) - originZ);
        float size = getQuadSize(partialTick), angle = Mth.lerp(partialTick, oRoll, roll);
        geometry.update(dx, dz, Mth.cos(angle) * size, Mth.sin(angle) * size);
        float[] vertices = geometry.vertices();
        int light = getLightColor(partialTick);
        float u0 = getU0(), u1 = getU1(), v0 = getV0(), v1 = getV1();
        for (int i = 0; i < geometry.vertexCount() * 4; i += 4) {
            consumer.addVertex((float) (originX + vertices[i] - cameraPosition.x()), renderY,
                    (float) (originZ + vertices[i + 1] - cameraPosition.z()))
                    .setUv(Mth.lerp(vertices[i + 2], u0, u1), Mth.lerp(vertices[i + 3], v0, v1))
                    .setColor(rCol, gCol, bCol, alpha).setLight(light);
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new HazeParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites);
        }
    }
}

