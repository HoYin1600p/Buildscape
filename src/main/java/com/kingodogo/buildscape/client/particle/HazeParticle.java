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
    // How far the haze reaches past a see-through or thin block's face, so no gap shows there (DGA).
    private static final double FACE_OVERLAP = 1.0 / 16.0;

    private final SpriteSet sprites;
    private final float rotSpeed;
    private final float maxAlpha;
    private final float baseSize;
    private final double originX;
    private final double originZ;
    private final float maskMinX;
    private final float maskMinZ;
    private final HazeMask mask;
    /** Per drawn area (mask rectangles, then edge strips): min X, max X, min Z, max Z relative to the origin. */
    private final float[][] edges;
    private final float[][] clipA = new float[8][2];
    private final float[][] clipB = new float[8][2];

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
        // The grid follows the world's half-block lines, so cell edges sit on block faces. The centre
        // cell holds the origin, leaving at least radius cells of cover on every side.
        this.maskMinX = (float) (HazeMask.gridMin(x, radius, CELL_SIZE) - x);
        this.maskMinZ = (float) (HazeMask.gridMin(z, radius, CELL_SIZE) - z);
        this.mask = createMask(level, radius);
        if (this.mask.rectangles().isEmpty()) {
            this.remove();
        }
        List<HazeMask.Rectangle> rectangles = this.mask.drawRectangles();
        List<HazeMask.Strip> strips = this.mask.strips();
        this.edges = new float[rectangles.size() + strips.size()][];
        for (int i = 0; i < rectangles.size(); i++) {
            HazeMask.Rectangle rectangle = rectangles.get(i);
            this.edges[i] = new float[]{
                    this.maskMinX + rectangle.minX() * CELL_SIZE, this.maskMinX + rectangle.maxX() * CELL_SIZE,
                    this.maskMinZ + rectangle.minZ() * CELL_SIZE, this.maskMinZ + rectangle.maxZ() * CELL_SIZE};
        }
        for (int i = 0; i < strips.size(); i++) {
            HazeMask.Strip strip = strips.get(i);
            this.edges[rectangles.size() + i] = new float[]{
                    (float) (this.maskMinX + strip.minX() * CELL_SIZE),
                    (float) (this.maskMinX + strip.maxX() * CELL_SIZE),
                    (float) (this.maskMinZ + strip.minZ() * CELL_SIZE),
                    (float) (this.maskMinZ + strip.maxZ() * CELL_SIZE)};
        }

        this.setSpriteFromAge(sprites);
    }

    private HazeMask createMask(ClientLevel level, int radius) {
        Long2ObjectMap<List<AABB>> shapes = new Long2ObjectOpenHashMap<>();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int gridSize = radius * 2 + 1;
        // Computed from the aligned grid in doubles, so a cell edge equals a block face exactly.
        double gridMinX = HazeMask.gridMin(this.originX, radius, CELL_SIZE);
        double gridMinZ = HazeMask.gridMin(this.originZ, radius, CELL_SIZE);
        return HazeMask.compute(gridSize, gridSize, radius, radius, (cellX, cellZ) -> {
            double minX = gridMinX + cellX * CELL_SIZE;
            double minZ = gridMinZ + cellZ * CELL_SIZE;
            return HazeMask.blocksCell(level, pos, shapes, minX, minZ, minX + CELL_SIZE, minZ + CELL_SIZE, this.y);
        }, (cellX, cellZ, side) -> HazeMask.entryDepth(level, pos, shapes, gridMinX + cellX * CELL_SIZE,
                gridMinZ + cellZ * CELL_SIZE, CELL_SIZE, this.y, side, FACE_OVERLAP));
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
        Vec3 camPos = camera.getPosition();
        float py = (float) (Mth.lerp((double) partialTick, this.yo, this.y) - camPos.y());
        float dx = (float) (Mth.lerp((double) partialTick, this.xo, this.x) - this.originX);
        float dz = (float) (Mth.lerp((double) partialTick, this.zo, this.z) - this.originZ);
        float size = this.getQuadSize(partialTick);
        float angle = Mth.lerp(partialTick, this.oRoll, this.roll);
        float cos = Mth.cos(angle) * size;
        float sin = Mth.sin(angle) * size;
        int light = this.getLightColor(partialTick);
        for (float[] edge : this.edges) {
            // Clockwise as seen from above, preserving the original top face and texture orientation.
            setPoint(this.clipA[0], dx - cos - sin, dz - sin + cos);
            setPoint(this.clipA[1], dx + cos - sin, dz + sin + cos);
            setPoint(this.clipA[2], dx + cos + sin, dz + sin - cos);
            setPoint(this.clipA[3], dx - cos + sin, dz - sin - cos);
            int count = clip(this.clipA, this.clipB, 4, 0, edge[0], true);
            count = clip(this.clipB, this.clipA, count, 0, edge[1], false);
            count = clip(this.clipA, this.clipB, count, 1, edge[2], true);
            count = clip(this.clipB, this.clipA, count, 1, edge[3], false);
            if (count == 4) {
                for (int i = 0; i < 4; i++) {
                    emitVertex(consumer, camPos, py, this.clipA[i], dx, dz, cos, sin, light);
                }
                for (int i = 3; i >= 0; i--) {
                    emitVertex(consumer, camPos, py, this.clipA[i], dx, dz, cos, sin, light);
                }
            } else {
                // The particle buffer uses quads; a repeated final vertex represents each triangle.
                for (int i = 1; i + 1 < count; i++) {
                    emitVertex(consumer, camPos, py, this.clipA[0], dx, dz, cos, sin, light);
                    emitVertex(consumer, camPos, py, this.clipA[i], dx, dz, cos, sin, light);
                    emitVertex(consumer, camPos, py, this.clipA[i + 1], dx, dz, cos, sin, light);
                    emitVertex(consumer, camPos, py, this.clipA[i + 1], dx, dz, cos, sin, light);
                    emitVertex(consumer, camPos, py, this.clipA[i + 1], dx, dz, cos, sin, light);
                    emitVertex(consumer, camPos, py, this.clipA[i], dx, dz, cos, sin, light);
                    emitVertex(consumer, camPos, py, this.clipA[0], dx, dz, cos, sin, light);
                    emitVertex(consumer, camPos, py, this.clipA[0], dx, dz, cos, sin, light);
                }
            }
        }
    }

    private static void setPoint(float[] point, float x, float z) {
        point[0] = x;
        point[1] = z;
    }

    private static int clip(float[][] input, float[][] output, int count, int axis, float edge, boolean minimum) {
        int result = 0;
        for (int i = 0; i < count; i++) {
            float[] previous = input[(i + count - 1) % count];
            float[] current = input[i];
            boolean previousInside = minimum ? previous[axis] >= edge : previous[axis] <= edge;
            boolean currentInside = minimum ? current[axis] >= edge : current[axis] <= edge;
            if (previousInside != currentInside) {
                float fraction = (edge - previous[axis]) / (current[axis] - previous[axis]);
                setPoint(output[result], Mth.lerp(fraction, previous[0], current[0]),
                        Mth.lerp(fraction, previous[1], current[1]));
                output[result++][axis] = edge;
            }
            if (currentInside) {
                setPoint(output[result++], current[0], current[1]);
            }
        }
        return result;
    }

    private void emitVertex(VertexConsumer consumer, Vec3 camera, float py, float[] point,
            float dx, float dz, float cos, float sin, int light) {
        float relativeX = point[0] - dx;
        float relativeZ = point[1] - dz;
        float sizeSquared = cos * cos + sin * sin;
        float u = ((relativeX * cos + relativeZ * sin) / sizeSquared + 1.0F) * 0.5F;
        float v = (1.0F - (-relativeX * sin + relativeZ * cos) / sizeSquared) * 0.5F;
        consumer.vertex(this.originX + point[0] - camera.x(), py, this.originZ + point[1] - camera.z())
                .uv(Mth.lerp(u, this.getU0(), this.getU1()), Mth.lerp(v, this.getV0(), this.getV1()))
                .color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
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
