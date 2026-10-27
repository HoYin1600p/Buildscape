package com.kingodogo.buildscape.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class HazeParticle extends TextureSheetParticle {

    private static final float CELL_SIZE = 0.5F;

    private final SpriteSet sprites;
    private final float rotSpeed;
    private final float maxAlpha;
    private final float baseSize;
    private final double originX;
    private final double originZ;
    private final float maskMin;
    private final HazeGeometry geometry;
    private TextureAtlasSprite geometrySprite;
    private int geometryCutoff = -1;
    private HazeTextureBounds textureBounds;

    protected HazeParticle(ClientLevel level, double x, double y, double z,
            double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;

        this.baseSize = 2.0F + level.random.nextFloat() * 1.5F;
        this.quadSize = this.baseSize;
        this.lifetime = 240 + level.random.nextInt(120);
        this.gravity = 0.0F;
        this.hasPhysics = false;

        this.xd = (level.random.nextFloat() - 0.5) * 0.0003;
        this.yd = 0.0;
        this.zd = (level.random.nextFloat() - 0.5) * 0.0003;

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
        // Cover the final size at every rotation, plus the total damped drift (less than 0.015).
        int radius = Math.min((HazeMask.MAX_GRID_SIZE - 1) / 2,
                Mth.ceil((this.baseSize * 1.2 * Math.sqrt(2.0) + 0.02) / CELL_SIZE));
        this.maskMin = -(radius + 0.5F) * CELL_SIZE;
        HazeMask mask = createMask(level, radius);
        this.geometry = new HazeGeometry(mask.rectangles(), this.maskMin, CELL_SIZE);
        if (mask.rectangles().isEmpty()) {
            this.remove();
        }

        this.setSpriteFromAge(sprites);
    }

    private HazeMask createMask(ClientLevel level, int radius) {
        Long2ObjectMap<List<AABB>> shapes = new Long2ObjectOpenHashMap<>();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int gridSize = radius * 2 + 1;
        return HazeMask.compute(gridSize, gridSize, radius, radius, (cellX, cellZ) -> {
            double minX = this.originX + this.maskMin + cellX * CELL_SIZE;
            double minZ = this.originZ + this.maskMin + cellZ * CELL_SIZE;
            double maxX = minX + CELL_SIZE;
            double maxZ = minZ + CELL_SIZE;
            return HazeMask.blocksCell(level, pos, shapes, minX, minZ, maxX, maxZ, this.y);
        });
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
        if (this.alpha <= 0.0F) {
            return;
        }
        int cutoff = HazeShaderSupport.alphaCutoff(this.alpha);
        if (this.sprite != this.geometrySprite || cutoff != this.geometryCutoff) {
            this.textureBounds = HazeTextureBounds.forSprite(this.sprite, cutoff);
            this.geometry.setTextureBounds(this.textureBounds);
            this.geometrySprite = this.sprite;
            this.geometryCutoff = cutoff;
        }
        if (this.textureBounds.isEmpty()) {
            return;
        }
        Vec3 camPos = camera.getPosition();
        float py = (float) (Mth.lerp((double) partialTick, this.yo, this.y) - camPos.y());
        float dx = (float) (Mth.lerp((double) partialTick, this.xo, this.x) - this.originX);
        float dz = (float) (Mth.lerp((double) partialTick, this.zo, this.z) - this.originZ);
        float size = this.getQuadSize(partialTick);
        float angle = Mth.lerp(partialTick, this.oRoll, this.roll);
        float cos = Mth.cos(angle) * size;
        float sin = Mth.sin(angle) * size;
        int light = this.getLightColor(partialTick);
        this.geometry.update(dx, dz, cos, sin);
        float[] vertices = this.geometry.vertices();
        float u0 = this.getU0();
        float u1 = this.getU1();
        float v0 = this.getV0();
        float v1 = this.getV1();
        for (int i = 0; i < this.geometry.vertexCount() * 4; i += 4) {
            consumer.vertex(this.originX + vertices[i] - camPos.x(), py, this.originZ + vertices[i + 1] - camPos.z())
                    .uv(Mth.lerp(vertices[i + 2], u0, u1), Mth.lerp(vertices[i + 3], v0, v1))
                    .color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
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
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new HazeParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites);
        }
    }
}
