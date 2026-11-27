package com.kingodogo.buildscape.biome;

import java.util.function.IntUnaryOperator;

/** Durability cost for each changed tint column. */
public final class BiomeBrushDurability {
    private BiomeBrushDurability() { }

    public static boolean chargesDurability(boolean creative, int unbreakingLevel, IntUnaryOperator nextInt) {
        if (creative) return false;
        return unbreakingLevel <= 0 || nextInt.applyAsInt(unbreakingLevel + 1) == 0;
    }
}
