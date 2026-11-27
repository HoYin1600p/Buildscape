package com.kingodogo.buildscape.client.particle;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.ARGB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.List;
import org.joml.Quaternionf;

/** Slowly fading haze lies flat on the ground and remains visible from either side. */
public final class HazeParticle extends SingleQuadParticle {
    private final SpriteSet sprites;
    private final float rotationSpeed;
    private final float maxAlpha;
    private final float baseSize;
    private final double originX, originZ;
    private final HazeGeometry geometry;
    private net.minecraft.client.renderer.texture.TextureAtlasSprite geometrySprite;
    private HazeTextureBounds textureBounds;

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
        this.originX = x;
        this.originZ = z;
        float cellSize = 0.5F;
        int radius = Math.min((HazeMask.MAX_GRID_SIZE - 1) / 2,
                Mth.ceil((baseSize * 1.2 * Math.sqrt(2) + 0.02) / cellSize));
        float maskMin = -(radius + 0.5F) * cellSize;
        var shapes = new Long2ObjectOpenHashMap<List<AABB>>();
        var pos = new BlockPos.MutableBlockPos();
        int gridSize = radius * 2 + 1;
        HazeMask mask = HazeMask.compute(gridSize, gridSize, radius, radius, (cellX, cellZ) -> {
            double minX = originX + maskMin + cellX * cellSize;
            double minZ = originZ + maskMin + cellZ * cellSize;
            return HazeMask.blocksCell(level, pos, shapes, minX, minZ, minX + cellSize, minZ + cellSize, y);
        });
        this.geometry = new HazeGeometry(mask.rectangles(), maskMin, cellSize);
        if (mask.rectangles().isEmpty()) remove();
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
        if (alpha <= 0) return;
        if (state instanceof HazeMeshSink sink) {
            if (sprite != geometrySprite) {
                textureBounds = HazeTextureBounds.forSprite(sprite);
                geometry.setTextureBounds(textureBounds);
                geometrySprite = sprite;
            }
            if (textureBounds.isEmpty()) return;
            var cameraPosition = camera.position();
            float dx = (float) (Mth.lerp((double) partialTick, xo, x) - originX);
            float dz = (float) (Mth.lerp((double) partialTick, zo, z) - originZ);
            float size = getQuadSize(partialTick), angle = Mth.lerp(partialTick, oRoll, roll);
            geometry.update(dx, dz, Mth.cos(angle) * size, Mth.sin(angle) * size);
            float[] source = geometry.vertices();
            float[] vertices = new float[geometry.vertexCount() * 5];
            float renderY = (float) (Mth.lerp((double) partialTick, yo, y) - cameraPosition.y());
            for (int i = 0; i < geometry.vertexCount(); i++) {
                int from = i * 4, to = i * 5;
                vertices[to] = (float) (originX + source[from] - cameraPosition.x());
                vertices[to + 1] = renderY;
                vertices[to + 2] = (float) (originZ + source[from + 1] - cameraPosition.z());
                vertices[to + 3] = Mth.lerp(source[from + 2], getU0(), getU1());
                vertices[to + 4] = Mth.lerp(source[from + 3], getV0(), getV1());
            }
            if (vertices.length == 0) return;
            sink.buildscape$addHazeMesh(vertices, ARGB.colorFromFloat(alpha, rCol, gCol, bCol), getLightCoords(partialTick));
            // Ensure the translucent particle layer is submitted even when it contains only haze.
            state.add(getLayer(), 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0);
            return;
        }
        float angle = Mth.lerp(partialTick, oRoll, roll);
        extractRotatedQuad(state, camera, new Quaternionf().rotationX((float) Math.PI / 2).rotateZ(angle), partialTick);
        extractRotatedQuad(state, camera, new Quaternionf().rotationX(-(float) Math.PI / 2).rotateZ(-angle), partialTick);
    }

    @Override protected Layer getLayer() { return Layer.TRANSLUCENT; }
}
