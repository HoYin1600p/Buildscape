package com.kingodogo.buildscape.adapter.client;

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

import java.util.List;

public final class HazeParticle extends TextureSheetParticle {
    private static final float CELL_SIZE = .5F;
    private final SpriteSet sprites;
    private final float rotSpeed, maxAlpha, baseSize, maskMin;
    private final double originX, originZ;
    private final HazeGeometry geometry;
    private TextureAtlasSprite geometrySprite;
    private int geometryCutoff = -1;
    private HazeTextureBounds textureBounds;

    private HazeParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;
        baseSize = 2 + level.random.nextFloat() * 1.5F;
        quadSize = baseSize; lifetime = 240 + level.random.nextInt(120); gravity = 0; hasPhysics = false;
        xd = (level.random.nextFloat() - .5) * .0003; yd = 0; zd = (level.random.nextFloat() - .5) * .0003;
        rotSpeed = (level.random.nextFloat() - .5F) * .0006F; roll = level.random.nextFloat() * ((float) Math.PI * 2); oRoll = roll;
        if (xSpeed > .01 || ySpeed > .01 || zSpeed > .01) {
            float variation = (level.random.nextFloat() - .5F) * .03F;
            setColor(Mth.clamp((float) xSpeed + variation, 0, 1), Mth.clamp((float) ySpeed + variation, 0, 1), Mth.clamp((float) zSpeed + variation, 0, 1));
        } else { float tint = .94F + level.random.nextFloat() * .06F; setColor(tint, tint, tint); }
        maxAlpha = .32F + level.random.nextFloat() * .12F; alpha = 0;
        originX = x; originZ = z;
        int radius = Math.min((HazeMask.MAX_GRID_SIZE - 1) / 2, Mth.ceil((baseSize * 1.2 * Math.sqrt(2) + .02) / CELL_SIZE));
        maskMin = -(radius + .5F) * CELL_SIZE;
        HazeMask mask = createMask(level, radius);
        geometry = new HazeGeometry(mask.rectangles(), maskMin, CELL_SIZE);
        if (mask.rectangles().isEmpty()) remove();
        setSpriteFromAge(sprites);
    }

    private HazeMask createMask(ClientLevel level, int radius) {
        Long2ObjectMap<List<AABB>> shapes = new Long2ObjectOpenHashMap<>();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int gridSize = radius * 2 + 1;
        return HazeMask.compute(gridSize, gridSize, radius, radius, (cellX, cellZ) -> {
            double minX = originX + maskMin + cellX * CELL_SIZE, minZ = originZ + maskMin + cellZ * CELL_SIZE;
            return HazeMask.blocksCell(level, pos, shapes, minX, minZ, minX + CELL_SIZE, minZ + CELL_SIZE, y);
        });
    }

    @Override public void tick() {
        xo = x; yo = y; zo = z;
        if (age++ >= lifetime) { remove(); return; }
        oRoll = roll; roll += rotSpeed; move(xd, yd, zd); xd *= .99; zd *= .99;
        float progress = (float) age / lifetime; quadSize = baseSize * (1 + progress * .2F);
        alpha = progress < .2F ? progress / .2F * maxAlpha : progress > .65F ? (1 - (progress - .65F) / .35F) * maxAlpha : maxAlpha;
        setSpriteFromAge(sprites);
    }

    @Override public void render(VertexConsumer consumer, Camera camera, float partialTick) {
        if (alpha <= 0) return;
        int cutoff = HazeRenderSupport.alphaCutoff(alpha);
        if (sprite != geometrySprite || cutoff != geometryCutoff) {
            textureBounds = HazeTextureBounds.forSprite(sprite, cutoff); geometry.setTextureBounds(textureBounds); geometrySprite = sprite; geometryCutoff = cutoff;
        }
        if (textureBounds.isEmpty()) return;
        Vec3 cameraPosition = camera.getPosition();
        float renderY = (float) (Mth.lerp((double) partialTick, yo, y) - cameraPosition.y());
        float dx = (float) (Mth.lerp((double) partialTick, xo, x) - originX), dz = (float) (Mth.lerp((double) partialTick, zo, z) - originZ);
        float size = getQuadSize(partialTick), angle = Mth.lerp(partialTick, oRoll, roll), cos = Mth.cos(angle) * size, sin = Mth.sin(angle) * size;
        geometry.update(dx, dz, cos, sin);
        float[] vertices = geometry.vertices(); int light = getLightColor(partialTick);
        float u0 = getU0(), u1 = getU1(), v0 = getV0(), v1 = getV1();
        for (int i = 0; i < geometry.vertexCount() * 4; i += 4) consumer.vertex(originX + vertices[i] - cameraPosition.x(), renderY, originZ + vertices[i + 1] - cameraPosition.z())
                .uv(Mth.lerp(vertices[i + 2], u0, u1), Mth.lerp(vertices[i + 3], v0, v1)).color(rCol, gCol, bCol, alpha).uv2(light).endVertex();
    }

    @Override public ParticleRenderType getRenderType() { return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT; }
    public static final class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        public Provider(SpriteSet sprites) { this.sprites = sprites; }
        @Override public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) { return new HazeParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites); }
    }
}
