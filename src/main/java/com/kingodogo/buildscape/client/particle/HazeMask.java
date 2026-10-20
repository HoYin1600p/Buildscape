package com.kingodogo.buildscape.client.particle;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

/** A fixed grid of free cells visible from the particle's centre. No world access is needed here. */
public final class HazeMask {

    public static final int MAX_GRID_SIZE = 32;

    private final int width;
    private final int height;
    private final boolean[] visible;
    private final List<Rectangle> rectangles;

    private HazeMask(int width, int height, boolean[] visible) {
        this.width = width;
        this.height = height;
        this.visible = visible;
        this.rectangles = mergeRuns();
    }

    /** Evaluates each cell once, then keeps connected cells with an unobstructed view of the centre. */
    public static HazeMask compute(int width, int height, int centreX, int centreZ,
            BiPredicate<Integer, Integer> blocked) {
        if (width < 1 || height < 1 || width > MAX_GRID_SIZE || height > MAX_GRID_SIZE
                || centreX < 0 || centreX >= width || centreZ < 0 || centreZ >= height) {
            throw new IllegalArgumentException("Invalid haze grid dimensions or centre");
        }

        boolean[] occupied = new boolean[width * height];
        List<Integer> obstacles = new ArrayList<>();
        for (int z = 0; z < height; z++) {
            for (int x = 0; x < width; x++) {
                int index = z * width + x;
                occupied[index] = blocked.test(x, z);
                if (occupied[index]) {
                    obstacles.add(index);
                }
            }
        }

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

        // Flood fill alone can walk around an isolated pane. Reject its shadow too, including cells
        // only partly shadowed, so the edge of a rendered cell cannot leak beyond the obstacle.
        for (int index = 0; index < visible.length; index++) {
            if (!visible[index]) {
                continue;
            }
            for (int obstacle : obstacles) {
                if (blocksView(centreX + 0.5, centreZ + 0.5, index % width, index / width,
                        obstacle % width, obstacle / width)) {
                    visible[index] = false;
                    break;
                }
            }
        }
        return new HazeMask(width, height, visible);
    }

    private static boolean blocksView(double sourceX, double sourceZ, int x, int z, int blockX, int blockZ) {
        if (Math.max(sourceX, x + 1) <= blockX || Math.min(sourceX, x) >= blockX + 1
                || Math.max(sourceZ, z + 1) <= blockZ || Math.min(sourceZ, z) >= blockZ + 1) {
            return false;
        }
        // Separating axes for the convex hull of the source point and the entire target cell,
        // against the obstacle square: world axes above, then normals to the four corner rays.
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

    public boolean isVisible(int x, int z) {
        return x >= 0 && x < this.width && z >= 0 && z < this.height && this.visible[z * this.width + x];
    }

    static boolean blocksHeight(double minY, double maxY, double hazeY) {
        return minY < hazeY && maxY > hazeY;
    }

    public List<Rectangle> rectangles() {
        return this.rectangles;
    }

    private List<Rectangle> mergeRuns() {
        List<Rectangle> result = new ArrayList<>();
        boolean[] merged = new boolean[this.visible.length];
        for (int z = 0; z < this.height; z++) {
            for (int x = 0; x < this.width; x++) {
                if (!isVisible(x, z) || merged[z * this.width + x]) {
                    continue;
                }
                int endX = x + 1;
                while (endX < this.width && isVisible(endX, z) && !merged[z * this.width + endX]) {
                    endX++;
                }
                int endZ = z + 1;
                while (endZ < this.height && isUnusedRun(x, endX, endZ, merged)) {
                    endZ++;
                }
                for (int row = z; row < endZ; row++) {
                    for (int column = x; column < endX; column++) {
                        merged[row * this.width + column] = true;
                    }
                }
                result.add(new Rectangle(x, z, endX, endZ));
            }
        }
        return List.copyOf(result);
    }

    private boolean isUnusedRun(int startX, int endX, int z, boolean[] merged) {
        for (int x = startX; x < endX; x++) {
            if (!isVisible(x, z) || merged[z * this.width + x]) {
                return false;
            }
        }
        return true;
    }

    public record Rectangle(int minX, int minZ, int maxX, int maxZ) {
    }
}
