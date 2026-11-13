package com.kingodogo.buildscape.biome;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BiomeBrushDurabilityTest {
    @Test
    void creativeNeverPaysAndUnenchantedSurvivalAlwaysPays() {
        assertFalse(BiomeBrushDurability.chargesDurability(true, 3, bound -> { throw new AssertionError(); }));
        assertTrue(BiomeBrushDurability.chargesDurability(false, 0, bound -> { throw new AssertionError(); }));
    }

    @Test
    void unbreakingChargesOnlyOnZero() {
        assertTrue(BiomeBrushDurability.chargesDurability(false, 2, bound -> {
            assertEquals(3, bound);
            return 0;
        }));
        assertFalse(BiomeBrushDurability.chargesDurability(false, 2, bound -> 1));
        assertFalse(BiomeBrushDurability.chargesDurability(false, 2, bound -> 2));
    }
}
