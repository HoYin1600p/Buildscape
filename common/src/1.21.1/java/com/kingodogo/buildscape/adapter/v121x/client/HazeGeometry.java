package com.kingodogo.buildscape.adapter.v121x.client;

import net.minecraft.util.Mth;

import java.util.Arrays;
import java.util.List;

/** Camera-independent clipped haze mesh. */
final class HazeGeometry {
    private final float[] bounds;
    private final float[][] clipA = new float[8][2];
    private final float[][] clipB = new float[8][2];
    private final float[][] uv = new float[8][2];
    private float[] vertices = new float[32];
    private int vertexCount;
    private boolean valid;
    private float cachedX, cachedZ, cachedCos, cachedSin;

    HazeGeometry(List<HazeMask.Rectangle> rectangles, float maskMin, float cellSize) {
        bounds = new float[rectangles.size() * 4];
        for (int i = 0; i < rectangles.size(); i++) {
            HazeMask.Rectangle r = rectangles.get(i);
            bounds[i * 4] = maskMin + r.minX() * cellSize;
            bounds[i * 4 + 1] = maskMin + r.maxX() * cellSize;
            bounds[i * 4 + 2] = maskMin + r.minZ() * cellSize;
            bounds[i * 4 + 3] = maskMin + r.maxZ() * cellSize;
        }
    }

    int vertexCount() { return vertexCount; }
    float[] vertices() { return vertices; }

    void update(float dx, float dz, float cos, float sin) {
        if (valid && dx == cachedX && dz == cachedZ && cos == cachedCos && sin == cachedSin) return;
        cachedX = dx; cachedZ = dz; cachedCos = cos; cachedSin = sin; valid = true; vertexCount = 0;
        float left = -1, right = 1, top = 1, bottom = -1;
        float x0 = dx + cos * left - sin * top, z0 = dz + sin * left + cos * top;
        float x1 = dx + cos * right - sin * top, z1 = dz + sin * right + cos * top;
        float x2 = dx + cos * right - sin * bottom, z2 = dz + sin * right + cos * bottom;
        float x3 = dx + cos * left - sin * bottom, z3 = dz + sin * left + cos * bottom;
        float particleMinX = Math.min(Math.min(x0, x1), Math.min(x2, x3));
        float particleMaxX = Math.max(Math.max(x0, x1), Math.max(x2, x3));
        float particleMinZ = Math.min(Math.min(z0, z1), Math.min(z2, z3));
        float particleMaxZ = Math.max(Math.max(z0, z1), Math.max(z2, z3));
        float sizeSquared = cos * cos + sin * sin;
        for (int rectangle = 0; rectangle < bounds.length; rectangle += 4) {
            float minX = bounds[rectangle], maxX = bounds[rectangle + 1], minZ = bounds[rectangle + 2], maxZ = bounds[rectangle + 3];
            if (particleMaxX < minX || particleMinX > maxX || particleMaxZ < minZ || particleMinZ > maxZ) continue;
            set(clipA[0], x0, z0); set(clipA[1], x1, z1); set(clipA[2], x2, z2); set(clipA[3], x3, z3);
            int count = 4;
            if (!(particleMinX >= minX && particleMaxX <= maxX && particleMinZ >= minZ && particleMaxZ <= maxZ)) {
                count = clip(clipA, clipB, count, 0, minX, true); count = clip(clipB, clipA, count, 0, maxX, false);
                count = clip(clipA, clipB, count, 1, minZ, true); count = clip(clipB, clipA, count, 1, maxZ, false);
            }
            for (int i = 0; i < count; i++) {
                float rx = clipA[i][0] - dx, rz = clipA[i][1] - dz;
                uv[i][0] = ((rx * cos + rz * sin) / sizeSquared + 1) * .5F;
                uv[i][1] = (1 - (-rx * sin + rz * cos) / sizeSquared) * .5F;
            }
            if (count == 4) { quad(0, 1, 2, 3); quad(3, 2, 1, 0); }
            else for (int i = 1; i + 1 < count; i += 2) { int last = Math.min(i + 2, count - 1); quad(0, i, i + 1, last); quad(0, last, i + 1, i); }
        }
    }
    private void quad(int a, int b, int c, int d) { vertex(a); vertex(b); vertex(c); vertex(d); }
    private void vertex(int index) {
        int offset = vertexCount++ * 4; if (offset + 4 > vertices.length) vertices = Arrays.copyOf(vertices, vertices.length * 2);
        vertices[offset] = clipA[index][0]; vertices[offset + 1] = clipA[index][1]; vertices[offset + 2] = uv[index][0]; vertices[offset + 3] = uv[index][1];
    }
    private static void set(float[] p, float x, float z) { p[0] = x; p[1] = z; }
    private static int clip(float[][] input, float[][] output, int count, int axis, float edge, boolean minimum) {
        int result = 0;
        for (int i = 0; i < count; i++) {
            float[] previous = input[(i + count - 1) % count], current = input[i];
            boolean previousInside = minimum ? previous[axis] >= edge : previous[axis] <= edge;
            boolean currentInside = minimum ? current[axis] >= edge : current[axis] <= edge;
            if (previousInside != currentInside) { float fraction = (edge - previous[axis]) / (current[axis] - previous[axis]); set(output[result], Mth.lerp(fraction, previous[0], current[0]), Mth.lerp(fraction, previous[1], current[1])); output[result++][axis] = edge; }
            if (currentInside) set(output[result++], current[0], current[1]);
        }
        return result;
    }
}
