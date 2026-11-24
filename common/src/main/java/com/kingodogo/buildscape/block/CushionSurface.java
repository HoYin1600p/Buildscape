package com.kingodogo.buildscape.block;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Resolves the horizontal surface under a cushion without relying on block-specific state. */
public final class CushionSurface {

    private CushionSurface() {}

    /**
     * Returns the highest surface in {@code shape} under the supplied block-local X/Z coordinate, or NaN if none
     * exists.
     */
    public static double topAt(VoxelShape shape, double localX, double localZ) {
        if (shape == null || shape.isEmpty()) return Double.NaN;
        double top = Double.NaN;
        for (AABB box : shape.toAabbs()) {
            if (localX >= box.minX && localX <= box.maxX && localZ >= box.minZ && localZ <= box.maxZ) {
                top = Double.isNaN(top) ? box.maxY : Math.max(top, box.maxY);
            }
        }
        return top;
    }
}
