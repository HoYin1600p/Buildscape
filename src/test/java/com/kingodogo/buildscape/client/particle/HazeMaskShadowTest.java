package com.kingodogo.buildscape.client.particle;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

public final class HazeMaskShadowTest {

    @Test
    public void boundaryShadowPassMatchesPreviousAlgorithmOnSeededGrids() {
        Random random = new Random(0x48415A45L);
        double[] densities = {0.0, 0.1, 0.3, 0.6, 0.9, 1.0};
        for (int sample = 0; sample < 96; sample++) {
            int width = 1 + random.nextInt(HazeMask.MAX_GRID_SIZE);
            int height = 1 + random.nextInt(HazeMask.MAX_GRID_SIZE);
            int centreX = random.nextInt(width);
            int centreZ = random.nextInt(height);
            boolean[] occupied = new boolean[width * height];
            for (int i = 0; i < occupied.length; i++) {
                occupied[i] = random.nextDouble() < densities[sample % densities.length];
            }
            occupied[centreZ * width + centreX] = sample % 4 == 0;
            assertMatchesPrevious(occupied, width, height, centreX, centreZ);
        }
    }

    @Test
    public void interiorWallsAndDisconnectedObstaclesKeepTheirPreviousShadows() {
        int size = 25;
        for (int thickness = 1; thickness <= 5; thickness++) {
            boolean[] occupied = new boolean[size * size];
            for (int z = 0; z < size; z++) {
                for (int x = 0; x < size; x++) {
                    int distance = Math.max(Math.abs(x - 12), Math.abs(z - 12));
                    occupied[z * size + x] = distance >= 3 && distance < 3 + thickness;
                }
            }
            assertMatchesPrevious(occupied, size, size, 12, 12);
            assertMatchesPrevious(occupied, size, size, 0, 0);
            occupied[12 * size + 15] = false;
            assertMatchesPrevious(occupied, size, size, 12, 12);
        }
    }

    private static void assertMatchesPrevious(boolean[] occupied, int width, int height, int centreX, int centreZ) {
        boolean[] expected = previousMask(occupied, width, height, centreX, centreZ);
        HazeMask actual = HazeMask.compute(width, height, centreX, centreZ, (x, z) -> occupied[z * width + x]);
        for (int z = 0; z < height; z++) {
            for (int x = 0; x < width; x++) {
                assertEquals(width + "x" + height + " centre " + centreX + "," + centreZ + " cell " + x + "," + z,
                        expected[z * width + x], actual.isVisible(x, z));
            }
        }
    }

    // Keep the previous all-obstacle algorithm independent of the production boundary filter.
    private static boolean[] previousMask(boolean[] occupied, int width, int height, int centreX, int centreZ) {
        boolean[] visible = new boolean[occupied.length];
        int[] queue = new int[occupied.length];
        int head = 0;
        int tail = 0;
        int centre = centreZ * width + centreX;
        if (!occupied[centre]) {
            visible[centre] = true;
            queue[tail++] = centre;
        }
        while (head < tail) {
            int index = queue[head++];
            int x = index % width;
            int z = index / width;
            for (int direction = 0; direction < 4; direction++) {
                int nextX = x + (direction == 0 ? -1 : direction == 1 ? 1 : 0);
                int nextZ = z + (direction == 2 ? -1 : direction == 3 ? 1 : 0);
                if (nextX < 0 || nextX >= width || nextZ < 0 || nextZ >= height) {
                    continue;
                }
                int next = nextZ * width + nextX;
                if (!occupied[next] && !visible[next]) {
                    visible[next] = true;
                    queue[tail++] = next;
                }
            }
        }
        for (int index = 0; index < visible.length; index++) {
            if (!visible[index]) {
                continue;
            }
            for (int obstacle = 0; obstacle < occupied.length; obstacle++) {
                if (occupied[obstacle] && previousBlocksView(centreX + 0.5, centreZ + 0.5,
                        index % width, index / width, obstacle % width, obstacle / width)) {
                    visible[index] = false;
                    break;
                }
            }
        }
        return visible;
    }

    private static boolean previousBlocksView(double sourceX, double sourceZ, int x, int z, int blockX, int blockZ) {
        if (Math.max(sourceX, x + 1) <= blockX || Math.min(sourceX, x) >= blockX + 1
                || Math.max(sourceZ, z + 1) <= blockZ || Math.min(sourceZ, z) >= blockZ + 1) {
            return false;
        }
        for (int corner = 0; corner < 4; corner++) {
            double axisX = z + (corner / 2) - sourceZ;
            double axisZ = sourceX - x - (corner % 2);
            if (axisX == 0.0 && axisZ == 0.0) {
                continue;
            }
            double source = sourceX * axisX + sourceZ * axisZ;
            double target = (x + 0.5) * axisX + (z + 0.5) * axisZ;
            double radius = (Math.abs(axisX) + Math.abs(axisZ)) * 0.5;
            double obstacle = (blockX + 0.5) * axisX + (blockZ + 0.5) * axisZ;
            if (Math.max(source, target + radius) <= obstacle - radius
                    || Math.min(source, target - radius) >= obstacle + radius) {
                return false;
            }
        }
        return true;
    }
}
