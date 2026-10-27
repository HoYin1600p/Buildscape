package com.kingodogo.buildscape.client.particle;

import net.minecraft.util.Mth;

import java.util.Arrays;
import java.util.List;

/** Camera-independent clipped haze mesh, shared by render passes with the same animation state. */
final class HazeGeometry {
    private final float[] bounds;
    private final float[][] clipA = new float[8][2];
    private final float[][] clipB = new float[8][2];
    private final float[][] uv = new float[8][2];
    private float[] vertices = new float[32];
    private int vertexCount;
    private boolean valid;
    private float cachedX;
    private float cachedZ;
    private float cachedCos;
    private float cachedSin;
    private HazeTextureBounds textureBounds = HazeTextureBounds.FULL;

    void setTextureBounds(HazeTextureBounds bounds) {
        if (!bounds.equals(this.textureBounds)) {
            this.textureBounds = bounds;
            this.valid = false;
        }
    }

    HazeGeometry(List<HazeMask.Rectangle> rectangles, float maskMin, float cellSize) {
        this.bounds = new float[rectangles.size() * 4];
        for (int i = 0; i < rectangles.size(); i++) {
            HazeMask.Rectangle rectangle = rectangles.get(i);
            this.bounds[i * 4] = maskMin + rectangle.minX() * cellSize;
            this.bounds[i * 4 + 1] = maskMin + rectangle.maxX() * cellSize;
            this.bounds[i * 4 + 2] = maskMin + rectangle.minZ() * cellSize;
            this.bounds[i * 4 + 3] = maskMin + rectangle.maxZ() * cellSize;
        }
    }

    boolean update(float dx, float dz, float cos, float sin) {
        if (this.valid && dx == this.cachedX && dz == this.cachedZ
                && cos == this.cachedCos && sin == this.cachedSin) {
            return false;
        }
        this.cachedX = dx;
        this.cachedZ = dz;
        this.cachedCos = cos;
        this.cachedSin = sin;
        this.valid = true;
        this.vertexCount = 0;
        float left = this.textureBounds.minU() * 2 - 1;
        float right = this.textureBounds.maxU() * 2 - 1;
        float top = 1 - this.textureBounds.minV() * 2;
        float bottom = 1 - this.textureBounds.maxV() * 2;
        float x0 = dx + cos * left - sin * top;
        float x1 = dx + cos * right - sin * top;
        float x2 = dx + cos * right - sin * bottom;
        float x3 = dx + cos * left - sin * bottom;
        float z0 = dz + sin * left + cos * top;
        float z1 = dz + sin * right + cos * top;
        float z2 = dz + sin * right + cos * bottom;
        float z3 = dz + sin * left + cos * bottom;
        float particleMinX = Math.min(Math.min(x0, x1), Math.min(x2, x3));
        float particleMaxX = Math.max(Math.max(x0, x1), Math.max(x2, x3));
        float particleMinZ = Math.min(Math.min(z0, z1), Math.min(z2, z3));
        float particleMaxZ = Math.max(Math.max(z0, z1), Math.max(z2, z3));
        float sizeSquared = cos * cos + sin * sin;
        for (int rectangle = 0; rectangle < this.bounds.length; rectangle += 4) {
            float minX = this.bounds[rectangle];
            float maxX = this.bounds[rectangle + 1];
            float minZ = this.bounds[rectangle + 2];
            float maxZ = this.bounds[rectangle + 3];
            if (particleMaxX < minX || particleMinX > maxX || particleMaxZ < minZ || particleMinZ > maxZ) {
                continue;
            }
            setPoint(this.clipA[0], x0, z0);
            setPoint(this.clipA[1], x1, z1);
            setPoint(this.clipA[2], x2, z2);
            setPoint(this.clipA[3], x3, z3);
            // The common open-field case needs no polygon clipping.
            int count = 4;
            if (!(particleMinX >= minX && particleMaxX <= maxX && particleMinZ >= minZ && particleMaxZ <= maxZ)) {
                count = clip(this.clipA, this.clipB, count, 0, minX, true);
                count = clip(this.clipB, this.clipA, count, 0, maxX, false);
                count = clip(this.clipA, this.clipB, count, 1, minZ, true);
                count = clip(this.clipB, this.clipA, count, 1, maxZ, false);
            }
            for (int i = 0; i < count; i++) {
                float relativeX = this.clipA[i][0] - dx;
                float relativeZ = this.clipA[i][1] - dz;
                this.uv[i][0] = ((relativeX * cos + relativeZ * sin) / sizeSquared + 1.0F) * 0.5F;
                this.uv[i][1] = (1.0F - (-relativeX * sin + relativeZ * cos) / sizeSquared) * 0.5F;
            }
            if (count == 4) {
                quad(0, 1, 2, 3);
                quad(3, 2, 1, 0);
            } else {
                // Pair adjacent fan triangles; preserve their diagonal and both face windings.
                for (int i = 1; i + 1 < count; i += 2) {
                    int last = Math.min(i + 2, count - 1);
                    quad(0, i, i + 1, last);
                    quad(0, last, i + 1, i);
                }
            }
        }
        return true;
    }

    int vertexCount() {
        return this.vertexCount;
    }

    float[] vertices() {
        return this.vertices;
    }

    private void quad(int a, int b, int c, int d) {
        vertex(a);
        vertex(b);
        vertex(c);
        vertex(d);
    }

    private void vertex(int index) {
        int offset = this.vertexCount++ * 4;
        if (offset + 4 > this.vertices.length) {
            this.vertices = Arrays.copyOf(this.vertices, this.vertices.length * 2);
        }
        this.vertices[offset] = this.clipA[index][0];
        this.vertices[offset + 1] = this.clipA[index][1];
        this.vertices[offset + 2] = this.uv[index][0];
        this.vertices[offset + 3] = this.uv[index][1];
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
}
