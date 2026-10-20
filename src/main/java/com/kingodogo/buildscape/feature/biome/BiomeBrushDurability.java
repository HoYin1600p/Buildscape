package com.kingodogo.buildscape.feature.biome;

import java.util.function.IntUnaryOperator;

/** Durability rule of the biome brushes, kept free of item registration so it can be tested. */
public final class BiomeBrushDurability {
    private BiomeBrushDurability() {}

    /**
     * Whether one repainted column costs the brush a point of durability. Creative players are never
     * charged (as with every vanilla tool); otherwise Unbreaking skips a charge with probability
     * {@code level / (level + 1)}.
     */
    public static boolean chargesDurability(boolean creative, int unbreakingLevel, IntUnaryOperator nextInt) {
        if (creative) {
            return false;
        }
        return unbreakingLevel <= 0 || nextInt.applyAsInt(unbreakingLevel + 1) == 0;
    }
}
