package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

import java.util.function.Predicate;

/** Resolves the cushion directly beneath an entity when vanilla's support lookup lands below it. */
public final class CushionSupport {
    private CushionSupport() {
    }

    public static BlockPos supportingPos(BlockPos vanilla, double x, double y, double z,
            Predicate<BlockPos> isCushion) {
        int probeY = Mth.floor(y - 0.01);
        if (probeY == vanilla.getY() || isCushion.test(vanilla)) {
            return vanilla;
        }
        BlockPos probe = new BlockPos(Mth.floor(x), probeY, Mth.floor(z));
        return isCushion.test(probe) ? probe : vanilla;
    }
}
