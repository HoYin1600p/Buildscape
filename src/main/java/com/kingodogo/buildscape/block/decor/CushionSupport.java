package com.kingodogo.buildscape.block.decor;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

import java.util.function.Predicate;

/** Fall and bounce support lookup for {@link CushionBlock}, kept free of block registration so it can be tested. */
public final class CushionSupport {
    private CushionSupport() {}

    /**
     * The block an entity stands on for fall and bounce purposes. Vanilla {@code Entity.getOnPos()} looks
     * 0.2 below the feet, but a bottom-half cushion is only 3/16 (0.1875) high, so the lookup lands on the
     * block under the cushion and the cushion's fall and bounce handling never runs. When the vanilla
     * position is not a cushion and the feet are within the cushion's own cell, return that cell instead.
     */
    public static BlockPos supportingPos(BlockPos vanilla, double x, double y, double z,
            Predicate<BlockPos> isCushion) {
        int probeY = Mth.floor(y - 0.01);
        if (probeY == vanilla.getY()) {
            return vanilla;
        }
        if (isCushion.test(vanilla)) {
            return vanilla;
        }
        BlockPos probe = new BlockPos(Mth.floor(x), probeY, Mth.floor(z));
        return isCushion.test(probe) ? probe : vanilla;
    }
}
