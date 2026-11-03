package com.kingodogo.buildscape.adapter.v121x.client;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.function.BiPredicate;

/** A fixed grid of free cells visible from a haze particle's centre. */
public final class HazeMask {
    public static final int MAX_GRID_SIZE = 32;
    private static final Cache<MaskKey, HazeMask> MASKS = CacheBuilder.newBuilder().maximumSize(128).build();
    private static final Cache<VoxelShape, List<AABB>> SHAPES = CacheBuilder.newBuilder()
            .weakKeys().maximumSize(512).build();

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

    public static void clearCaches() {
        MASKS.invalidateAll();
        SHAPES.invalidateAll();
    }

    private static List<AABB> collisionBoxes(VoxelShape shape) {
        List<AABB> boxes = SHAPES.getIfPresent(shape);
        if (boxes == null) {
            boxes = List.copyOf(shape.toAabbs());
            SHAPES.put(shape, boxes);
        }
        return boxes;
    }

    public static HazeMask compute(int width, int height, int centreX, int centreZ,
            BiPredicate<Integer, Integer> blocked) {
        if (width < 1 || height < 1 || width > MAX_GRID_SIZE || height > MAX_GRID_SIZE
                || centreX < 0 || centreX >= width || centreZ < 0 || centreZ >= height) {
            throw new IllegalArgumentException("Invalid haze grid dimensions or centre");
        }
        boolean[] occupied = new boolean[width * height];
        BitSet occupancy = new BitSet(occupied.length);
        for (int z = 0; z < height; z++) for (int x = 0; x < width; x++) {
            int index = z * width + x;
            occupied[index] = blocked.test(x, z);
            if (occupied[index]) occupancy.set(index);
        }
        MaskKey key = new MaskKey(width, height, centreX, centreZ, occupancy);
        HazeMask cached = MASKS.getIfPresent(key);
        if (cached != null) return cached;

        boolean[] visible = new boolean[occupied.length];
        int[] queue = new int[occupied.length];
        int head = 0, tail = 0, centre = centreZ * width + centreX;
        if (!occupied[centre]) { visible[centre] = true; queue[tail++] = centre; }
        while (head < tail) {
            int index = queue[head++], x = index % width, z = index / width;
            for (int direction = 0; direction < 4; direction++) {
                int nextX = x + (direction == 0 ? -1 : direction == 1 ? 1 : 0);
                int nextZ = z + (direction == 2 ? -1 : direction == 3 ? 1 : 0);
                if (nextX < 0 || nextX >= width || nextZ < 0 || nextZ >= height) continue;
                int next = nextZ * width + nextX;
                if (!occupied[next] && !visible[next]) { visible[next] = true; queue[tail++] = next; }
            }
        }
        int[] obstacles = queue;
        int obstacleCount = 0;
        for (int index = 0; index < occupied.length; index++) if (occupied[index] && ((index % width > 0 && visible[index - 1])
                || (index % width + 1 < width && visible[index + 1]) || (index >= width && visible[index - width])
                || (index + width < visible.length && visible[index + width]))) obstacles[obstacleCount++] = index;
        for (int index = 0; index < visible.length; index++) {
            if (!visible[index]) continue;
            for (int i = 0; i < obstacleCount; i++) {
                int obstacle = obstacles[i];
                if (blocksView(centreX + 0.5, centreZ + 0.5, index % width, index / width,
                        obstacle % width, obstacle / width)) { visible[index] = false; break; }
            }
        }
        HazeMask mask = new HazeMask(width, height, visible);
        MASKS.put(key, mask);
        return mask;
    }

    public static boolean blocksCell(BlockGetter level, BlockPos.MutableBlockPos pos,
            Long2ObjectMap<List<AABB>> shapes, double minX, double minZ, double maxX, double maxZ, double hazeY) {
        int blockY = Mth.floor(hazeY);
        for (int by = blockY - 1; by <= blockY; by++) for (int bx = Mth.floor(minX); bx <= Mth.floor(maxX); bx++)
            for (int bz = Mth.floor(minZ); bz <= Mth.floor(maxZ); bz++) {
                pos.set(bx, by, bz);
                long key = BlockPos.asLong(bx, by, bz);
                List<AABB> boxes = shapes.get(key);
                if (boxes == null) { boxes = collisionBoxes(level.getBlockState(pos).getCollisionShape(level, pos)); shapes.put(key, boxes); }
                for (AABB box : boxes) if (box.maxX + bx > minX && box.minX + bx < maxX && box.maxZ + bz > minZ
                        && box.minZ + bz < maxZ && box.minY + by < hazeY && box.maxY + by > hazeY) return true;
            }
        return false;
    }

    private static boolean blocksView(double sourceX, double sourceZ, int x, int z, int blockX, int blockZ) {
        if (Math.max(sourceX, x + 1) <= blockX || Math.min(sourceX, x) >= blockX + 1 || Math.max(sourceZ, z + 1) <= blockZ || Math.min(sourceZ, z) >= blockZ + 1) return false;
        for (int corner = 0; corner < 4; corner++) {
            double axisX = z + (corner / 2) - sourceZ, axisZ = sourceX - x - (corner % 2);
            if (axisX == 0.0 && axisZ == 0.0) continue;
            double source = sourceX * axisX + sourceZ * axisZ, target = (x + 0.5) * axisX + (z + 0.5) * axisZ;
            double radius = (Math.abs(axisX) + Math.abs(axisZ)) * 0.5, obstacle = (blockX + 0.5) * axisX + (blockZ + 0.5) * axisZ;
            if (Math.max(source, target + radius) <= obstacle - radius || Math.min(source, target - radius) >= obstacle + radius) return false;
        }
        return true;
    }

    public boolean isVisible(int x, int z) { return x >= 0 && x < width && z >= 0 && z < height && visible[z * width + x]; }
    public List<Rectangle> rectangles() { return rectangles; }
    private List<Rectangle> mergeRuns() {
        List<Rectangle> result = new ArrayList<>(); boolean[] merged = new boolean[visible.length];
        for (int z = 0; z < height; z++) for (int x = 0; x < width; x++) if (isVisible(x, z) && !merged[z * width + x]) {
            int endX = x + 1; while (endX < width && isVisible(endX, z) && !merged[z * width + endX]) endX++;
            int endZ = z + 1; while (endZ < height && unusedRun(x, endX, endZ, merged)) endZ++;
            for (int row = z; row < endZ; row++) for (int col = x; col < endX; col++) merged[row * width + col] = true;
            result.add(new Rectangle(x, z, endX, endZ));
        }
        return List.copyOf(result);
    }
    private boolean unusedRun(int startX, int endX, int z, boolean[] merged) {
        for (int x = startX; x < endX; x++) if (!isVisible(x, z) || merged[z * width + x]) return false;
        return true;
    }
    private record MaskKey(int width, int height, int centreX, int centreZ, BitSet occupied) { }
    public record Rectangle(int minX, int minZ, int maxX, int maxZ) { }
}
