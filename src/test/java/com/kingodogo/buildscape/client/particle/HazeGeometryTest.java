package com.kingodogo.buildscape.client.particle;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class HazeGeometryTest {
    @Test
    public void openFieldKeepsOriginalQuadAndBothFaces() {
        HazeGeometry geometry = new HazeGeometry(List.of(new HazeMask.Rectangle(0, 0, 32, 32)), -8, 0.5F);
        assertTrue(geometry.update(0, 0, 2, 0));
        assertEquals(8, geometry.vertexCount());
        float[] vertices = geometry.vertices();
        assertArrayEquals(new float[]{-2, 2, 0, 0, 2, 2, 1, 0, 2, -2, 1, 1, -2, -2, 0, 1},
                java.util.Arrays.copyOf(vertices, 16), 0);
        for (int i = 0; i < 4; i++) {
            for (int attribute = 0; attribute < 4; attribute++) {
                assertEquals(vertices[i * 4 + attribute], vertices[(7 - i) * 4 + attribute], 0);
            }
        }
    }

    @Test
    public void repeatedPassReusesMeshButEveryGeometryInputInvalidatesIt() {
        HazeGeometry geometry = new HazeGeometry(List.of(new HazeMask.Rectangle(0, 0, 8, 8)), -2, 0.5F);
        assertTrue(geometry.update(0, 0, 1, 0));
        float[] storage = geometry.vertices();
        assertFalse(geometry.update(0, 0, 1, 0));
        assertTrue(geometry.update(0.001F, 0, 1, 0));
        assertTrue(geometry.update(0.001F, 0.001F, 1, 0));
        assertTrue(geometry.update(0.001F, 0.001F, 1.001F, 0));
        assertTrue(geometry.update(0.001F, 0.001F, 1.001F, 0.001F));
        assertSame(storage, geometry.vertices());
    }

    @Test
    public void octagonUsesHalfTheOriginalVertexWrites() {
        HazeGeometry geometry = new HazeGeometry(List.of(new HazeMask.Rectangle(0, 0, 4, 4)), -1, 0.5F);
        geometry.update(0, 0, 0.75F, 0.75F);
        assertEquals(24, geometry.vertexCount());
    }

    @Test
    public void emptyAndDisjointMasksEmitNothing() {
        HazeGeometry empty = new HazeGeometry(List.of(), 0, 0.5F);
        empty.update(0, 0, 1, 0);
        assertEquals(0, empty.vertexCount());
        HazeGeometry disjoint = new HazeGeometry(List.of(new HazeMask.Rectangle(0, 0, 2, 2)), 20, 0.5F);
        disjoint.update(0, 0, 1, 0);
        assertEquals(0, disjoint.vertexCount());
    }

    @Test
    public void clippedMeshesPreserveCoverageWindingAndUvsAcrossAnimationStates() {
        Random random = new Random(81642);
        List<HazeMask.Rectangle> rectangles = List.of(new HazeMask.Rectangle(0, 0, 4, 8),
                new HazeMask.Rectangle(4, 2, 8, 6), new HazeMask.Rectangle(8, 0, 12, 8));
        HazeGeometry geometry = new HazeGeometry(rectangles, -3, 0.5F);
        for (int sample = 0; sample < 500; sample++) {
            float dx = random.nextFloat() - 0.5F;
            float dz = random.nextFloat() - 0.5F;
            float angle = random.nextFloat() * 6.28F;
            float size = 2 + random.nextFloat() * 2.2F;
            float cos = (float) Math.cos(angle) * size;
            float sin = (float) Math.sin(angle) * size;
            geometry.update(dx, dz, cos, sin);
            double expectedArea = 0;
            for (HazeMask.Rectangle rectangle : rectangles) {
                List<double[]> polygon = new ArrayList<>(List.of(
                        new double[]{dx - cos - sin, dz - sin + cos},
                        new double[]{dx + cos - sin, dz + sin + cos},
                        new double[]{dx + cos + sin, dz + sin - cos},
                        new double[]{dx - cos + sin, dz - sin - cos}));
                polygon = referenceClip(polygon, 0, -3 + rectangle.minX() * 0.5, true);
                polygon = referenceClip(polygon, 0, -3 + rectangle.maxX() * 0.5, false);
                polygon = referenceClip(polygon, 1, -3 + rectangle.minZ() * 0.5, true);
                polygon = referenceClip(polygon, 1, -3 + rectangle.maxZ() * 0.5, false);
                for (int i = 1; i + 1 < polygon.size(); i++) {
                    expectedArea += Math.abs(cross(polygon.get(0), polygon.get(i), polygon.get(i + 1))) * 0.5;
                }
            }
            float[] vertices = geometry.vertices();
            double frontArea = 0;
            double backArea = 0;
            for (int i = 0; i < geometry.vertexCount(); i += 4) {
                double area = (triangle(vertices, i, i + 1, i + 2)
                        + triangle(vertices, i + 2, i + 3, i)) * 0.5;
                if (area < 0) {
                    frontArea -= area;
                } else {
                    backArea += area;
                }
            }
            assertEquals(expectedArea, frontArea, 0.0001);
            assertEquals(expectedArea, backArea, 0.0001);
            for (int i = 0; i < geometry.vertexCount() * 4; i += 4) {
                float x = vertices[i] - dx;
                float z = vertices[i + 1] - dz;
                float squared = cos * cos + sin * sin;
                assertEquals(((x * cos + z * sin) / squared + 1) * 0.5F, vertices[i + 2], 0);
                assertEquals((1 - (-x * sin + z * cos) / squared) * 0.5F, vertices[i + 3], 0);
                assertTrue(vertices[i + 2] >= -0.00001 && vertices[i + 2] <= 1.00001);
                assertTrue(vertices[i + 3] >= -0.00001 && vertices[i + 3] <= 1.00001);
            }
        }
    }

    private static double triangle(float[] vertices, int a, int b, int c) {
        return cross(new double[]{vertices[a * 4], vertices[a * 4 + 1]},
                new double[]{vertices[b * 4], vertices[b * 4 + 1]},
                new double[]{vertices[c * 4], vertices[c * 4 + 1]});
    }

    private static double cross(double[] a, double[] b, double[] c) {
        return (b[0] - a[0]) * (c[1] - a[1]) - (b[1] - a[1]) * (c[0] - a[0]);
    }

    private static List<double[]> referenceClip(List<double[]> polygon, int axis, double edge, boolean minimum) {
        List<double[]> result = new ArrayList<>();
        for (int i = 0; i < polygon.size(); i++) {
            double[] a = polygon.get(i);
            double[] b = polygon.get((i + 1) % polygon.size());
            boolean insideA = minimum ? a[axis] >= edge : a[axis] <= edge;
            boolean insideB = minimum ? b[axis] >= edge : b[axis] <= edge;
            if (insideA) {
                result.add(a);
            }
            if (insideA != insideB) {
                double t = (edge - a[axis]) / (b[axis] - a[axis]);
                result.add(new double[]{a[0] + t * (b[0] - a[0]), a[1] + t * (b[1] - a[1])});
            }
        }
        return result;
    }
}
