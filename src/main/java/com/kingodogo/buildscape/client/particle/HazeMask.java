package com.kingodogo.buildscape.client.particle;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiPredicate;

/** A fixed grid of free cells visible from the particle's centre. */
public final class HazeMask {

    public static final int MAX_GRID_SIZE = 32;

    private final int width;
    private final int height;
    private final boolean[] visible;
    private final List<Rectangle> rectangles;
    private final List<Rectangle> drawRectangles;
    private final List<Strip> strips;

    private HazeMask(int width, int height, boolean[] visible, EdgeFill fill) {
        this.width = width;
        this.height = height;
        this.visible = visible;
        this.rectangles = mergeRuns(visible);
        boolean[] drawn = visible.clone();
        for (int index = 0; index < drawn.length; index++) {
            drawn[index] |= fill.whole()[index];
        }
        this.drawRectangles = mergeRuns(drawn);
        this.strips = fill.strips();
    }

    /** Strips of partly covered blocked cells, and the blocked cells the haze covers entirely. */
    record EdgeFill(List<Strip> strips, boolean[] whole) {
    }

    /** Grid origin on the world's half-block lines for a particle at {@code origin}; the centre cell holds it. */
    public static double gridMin(double origin, int radius, double cellSize) {
        return Math.floor(origin / cellSize) * cellSize - radius * cellSize;
    }

    /**
     * How far the haze may reach into a blocked cell, as a fraction of the cell (0 to 1), entering
     * from the given side of that cell: 0 = from min X, 1 = from max X, 2 = from min Z, 3 = from max Z.
     */
    @FunctionalInterface
    public interface EntryDepth {
        double depth(int x, int z, int side);
    }

    /** Evaluates each cell once, then keeps connected cells with an unobstructed view of the centre. */
    public static HazeMask compute(int width, int height, int centreX, int centreZ,
            BiPredicate<Integer, Integer> blocked) {
        return compute(width, height, centreX, centreZ, blocked, null);
    }

    /**
     * As above; with {@code entryDepth}, the haze also reaches into each blocked cell next to a
     * visible one, up to that cell's real block face (DGA: no gap where the haze meets a block).
     */
    public static HazeMask compute(int width, int height, int centreX, int centreZ,
            BiPredicate<Integer, Integer> blocked, EntryDepth entryDepth) {
        if (width < 1 || height < 1 || width > MAX_GRID_SIZE || height > MAX_GRID_SIZE
                || centreX < 0 || centreX >= width || centreZ < 0 || centreZ >= height) {
            throw new IllegalArgumentException("Invalid haze grid dimensions or centre");
        }

        boolean[] occupied = new boolean[width * height];
        for (int z = 0; z < height; z++) {
            for (int x = 0; x < width; x++) {
                int index = z * width + x;
                occupied[index] = blocked.test(x, z);
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

        // Only the boundary of the flood-filled region can cast a new shadow. Any ray reaching an
        // interior obstacle must first cross a boundary obstacle. Use the fill before shadowing:
        // removing partly shadowed cells does not make their neighbouring obstacles redundant.
        int[] obstacles = queue;
        int obstacleCount = 0;
        for (int index = 0; index < occupied.length; index++) {
            if (occupied[index] && ((index % width > 0 && visible[index - 1])
                    || (index % width + 1 < width && visible[index + 1])
                    || (index >= width && visible[index - width])
                    || (index + width < visible.length && visible[index + width]))) {
                obstacles[obstacleCount++] = index;
            }
        }

        // Flood fill alone can walk around an isolated pane. Reject its shadow too, including cells
        // only partly shadowed, so the edge of a rendered cell cannot leak beyond the obstacle.
        for (int index = 0; index < visible.length; index++) {
            if (!visible[index]) {
                continue;
            }
            for (int i = 0; i < obstacleCount; i++) {
                int obstacle = obstacles[i];
                if (blocksView(centreX + 0.5, centreZ + 0.5, index % width, index / width,
                        obstacle % width, obstacle / width)) {
                    visible[index] = false;
                    break;
                }
            }
        }
        EdgeFill fill = entryDepth == null
                ? new EdgeFill(List.of(), new boolean[visible.length])
                : edgeFill(width, height, visible, occupied, entryDepth);
        return new HazeMask(width, height, visible, fill);
    }

    /**
     * For each blocked cell next to a visible one, the parts of it the haze may cover: along the
     * entry sides, as deep as {@code entryDepth} allows. Strips entering along X span the whole cell;
     * strips entering along Z take only the width the X strips left, so no area is drawn twice. A cell
     * covered completely is returned as whole instead, so it merges into the drawn rectangles.
     */
    static EdgeFill edgeFill(int width, int height, boolean[] visible, boolean[] occupied,
            EntryDepth entryDepth) {
        List<Strip> strips = new ArrayList<>();
        boolean[] whole = new boolean[visible.length];
        for (int z = 0; z < height; z++) {
            for (int x = 0; x < width; x++) {
                if (!occupied[z * width + x]) {
                    continue;
                }
                double minXDepth = x > 0 && visible[z * width + x - 1] ? clampDepth(entryDepth.depth(x, z, 0)) : 0.0;
                double maxXDepth = x + 1 < width && visible[z * width + x + 1]
                        ? clampDepth(entryDepth.depth(x, z, 1))
                        : 0.0;
                double minZDepth = z > 0 && visible[(z - 1) * width + x] ? clampDepth(entryDepth.depth(x, z, 2)) : 0.0;
                double maxZDepth = z + 1 < height && visible[(z + 1) * width + x]
                        ? clampDepth(entryDepth.depth(x, z, 3))
                        : 0.0;
                if (minXDepth + maxXDepth >= 1.0 || minZDepth + maxZDepth >= 1.0) {
                    whole[z * width + x] = true;
                    continue;
                }
                if (minXDepth > 0.0) {
                    strips.add(new Strip(x, z, x + minXDepth, z + 1));
                }
                if (maxXDepth > 0.0) {
                    strips.add(new Strip(x + 1 - maxXDepth, z, x + 1, z + 1));
                }
                double left = x + minXDepth;
                double right = x + 1 - maxXDepth;
                if (right > left) {
                    if (minZDepth > 0.0) {
                        strips.add(new Strip(left, z, right, z + minZDepth));
                    }
                    if (maxZDepth > 0.0) {
                        strips.add(new Strip(left, z + 1 - maxZDepth, right, z + 1));
                    }
                }
            }
        }
        return new EdgeFill(mergeStrips(mergeStrips(strips, true), false), whole);
    }

    private static double clampDepth(double depth) {
        return depth > 0.0 ? Math.min(depth, 1.0) : 0.0;
    }

    /**
     * Joins strips that continue each other exactly (same span, touching ends) to keep the draw count
     * low: along X when {@code alongX}, else along Z. Sorted once, then one pass.
     */
    private static List<Strip> mergeStrips(List<Strip> input, boolean alongX) {
        List<Strip> sorted = new ArrayList<>(input);
        sorted.sort(alongX
                ? Comparator.comparingDouble(Strip::minZ).thenComparingDouble(Strip::maxZ)
                        .thenComparingDouble(Strip::minX)
                : Comparator.comparingDouble(Strip::minX).thenComparingDouble(Strip::maxX)
                        .thenComparingDouble(Strip::minZ));
        List<Strip> merged = new ArrayList<>();
        for (Strip strip : sorted) {
            Strip last = merged.isEmpty() ? null : merged.get(merged.size() - 1);
            if (last != null && alongX && last.minZ() == strip.minZ() && last.maxZ() == strip.maxZ()
                    && last.maxX() == strip.minX()) {
                merged.set(merged.size() - 1, new Strip(last.minX(), last.minZ(), strip.maxX(), last.maxZ()));
            } else if (last != null && !alongX && last.minX() == strip.minX() && last.maxX() == strip.maxX()
                    && last.maxZ() == strip.minZ()) {
                merged.set(merged.size() - 1, new Strip(last.minX(), last.minZ(), last.maxX(), strip.maxZ()));
            } else {
                merged.add(strip);
            }
        }
        return List.copyOf(merged);
    }

    /**
     * How far the haze may reach into the blocked cell, as a fraction of the cell, entering from
     * {@code side}: the whole cell when the block at haze height is opaque (it hides the overshoot, so
     * the haze always meets its face), otherwise up to the first collision face plus {@code overlap}.
     */
    static double entryDepth(BlockGetter level, BlockPos.MutableBlockPos pos,
            Long2ObjectMap<List<AABB>> shapes, double minX, double minZ, double cellSize, double hazeY,
            int side, double overlap) {
        pos.set(Mth.floor(minX + cellSize * 0.5), Mth.floor(hazeY), Mth.floor(minZ + cellSize * 0.5));
        if (level.getBlockState(pos).isSolidRender(level, pos)) {
            return 1.0;
        }
        double face = faceDistance(level, pos, shapes, minX, minZ, minX + cellSize, minZ + cellSize, hazeY, side);
        return face == Double.MAX_VALUE ? 0.0 : (face + overlap) / cellSize;
    }

    /**
     * Distance in blocks from the given side of a cell to the first collision face inside it at the
     * haze height, or {@code Double.MAX_VALUE} when nothing there blocks. Sides as in {@link EntryDepth}.
     */
    static double faceDistance(BlockGetter level, BlockPos.MutableBlockPos pos,
            Long2ObjectMap<List<AABB>> shapes, double minX, double minZ, double maxX, double maxZ,
            double hazeY, int side) {
        double nearest = Double.MAX_VALUE;
        int blockY = Mth.floor(hazeY);
        for (int by = blockY - 1; by <= blockY; by++) {
            for (int bx = Mth.floor(minX); bx <= lastBlock(maxX); bx++) {
                for (int bz = Mth.floor(minZ); bz <= lastBlock(maxZ); bz++) {
                    for (AABB box : shapesAt(level, pos, shapes, bx, by, bz)) {
                        double boxMinX = box.minX + bx;
                        double boxMaxX = box.maxX + bx;
                        double boxMinZ = box.minZ + bz;
                        double boxMaxZ = box.maxZ + bz;
                        if (boxMaxX > minX && boxMinX < maxX && boxMaxZ > minZ && boxMinZ < maxZ
                                && blocksHeight(box.minY + by, box.maxY + by, hazeY)) {
                            double distance = switch (side) {
                                case 0 -> boxMinX - minX;
                                case 1 -> maxX - boxMaxX;
                                case 2 -> boxMinZ - minZ;
                                default -> maxZ - boxMaxZ;
                            };
                            nearest = Math.min(nearest, Math.max(0.0, distance));
                        }
                    }
                }
            }
        }
        return nearest;
    }

    private static List<AABB> shapesAt(BlockGetter level, BlockPos.MutableBlockPos pos,
            Long2ObjectMap<List<AABB>> shapes, int bx, int by, int bz) {
        long key = BlockPos.asLong(bx, by, bz);
        List<AABB> boxes = shapes.get(key);
        if (boxes == null) {
            pos.set(bx, by, bz);
            boxes = level.getBlockState(pos).getCollisionShape(level, pos).toAabbs();
            shapes.put(key, boxes);
        }
        return boxes;
    }

    static boolean blocksCell(BlockGetter level, BlockPos.MutableBlockPos pos,
            Long2ObjectMap<List<AABB>> shapes, double minX, double minZ, double maxX, double maxZ,
            double hazeY) {
        int blockY = Mth.floor(hazeY);
        // The layer below can contain tall fence/wall collision. The layer above cannot reach down.
        for (int by = blockY - 1; by <= blockY; by++) {
            for (int bx = Mth.floor(minX); bx <= lastBlock(maxX); bx++) {
                for (int bz = Mth.floor(minZ); bz <= lastBlock(maxZ); bz++) {
                    for (AABB box : shapesAt(level, pos, shapes, bx, by, bz)) {
                        if (box.maxX + bx > minX && box.minX + bx < maxX
                                && box.maxZ + bz > minZ && box.minZ + bz < maxZ
                                && blocksHeight(box.minY + by, box.maxY + by, hazeY)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
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

    /** Parts of blocked cells next to visible ones that the haze also covers, in cell units. */
    public List<Strip> strips() {
        return this.strips;
    }

    public boolean isVisible(int x, int z) {
        return x >= 0 && x < this.width && z >= 0 && z < this.height && this.visible[z * this.width + x];
    }

    static boolean blocksHeight(double minY, double maxY, double hazeY) {
        return minY < hazeY && maxY > hazeY;
    }

    /** Last block column a span ending at {@code max} reaches (a span ending on a block line does not enter it). */
    private static int lastBlock(double max) {
        return Mth.ceil(max) - 1;
    }

    public List<Rectangle> rectangles() {
        return this.rectangles;
    }

    /** Rectangles to draw: the visible cells plus the blocked cells the haze covers entirely. */
    public List<Rectangle> drawRectangles() {
        return this.drawRectangles;
    }

    private List<Rectangle> mergeRuns(boolean[] cells) {
        List<Rectangle> result = new ArrayList<>();
        boolean[] merged = new boolean[cells.length];
        for (int z = 0; z < this.height; z++) {
            for (int x = 0; x < this.width; x++) {
                if (!cells[z * this.width + x] || merged[z * this.width + x]) {
                    continue;
                }
                int endX = x + 1;
                while (endX < this.width && cells[z * this.width + endX] && !merged[z * this.width + endX]) {
                    endX++;
                }
                int endZ = z + 1;
                while (endZ < this.height && isUnusedRun(cells, x, endX, endZ, merged)) {
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

    private boolean isUnusedRun(boolean[] cells, int startX, int endX, int z, boolean[] merged) {
        for (int x = startX; x < endX; x++) {
            if (!cells[z * this.width + x] || merged[z * this.width + x]) {
                return false;
            }
        }
        return true;
    }

    public record Rectangle(int minX, int minZ, int maxX, int maxZ) {
    }

    public record Strip(double minX, double minZ, double maxX, double maxZ) {
    }
}
