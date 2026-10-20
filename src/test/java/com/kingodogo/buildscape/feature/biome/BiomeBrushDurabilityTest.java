package com.kingodogo.buildscape.feature.biome;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BiomeBrushDurabilityTest {
    @Test
    public void creativePlayersAreNeverCharged() {
        assertFalse(BiomeBrushDurability.chargesDurability(true, 0, bound -> 0));
        assertFalse(BiomeBrushDurability.chargesDurability(true, 3, bound -> 0));
    }

    @Test
    public void survivalPlayersPayForEveryColumnWithoutUnbreaking() {
        assertTrue(BiomeBrushDurability.chargesDurability(false, 0, bound -> {
            throw new AssertionError("no roll without unbreaking");
        }));
    }

    @Test
    public void unbreakingChargesOnlyOnAZeroRoll() {
        int[] seenBound = new int[1];
        assertTrue(BiomeBrushDurability.chargesDurability(false, 2, bound -> {
            seenBound[0] = bound;
            return 0;
        }));
        assertEquals(3, seenBound[0]);
        assertFalse(BiomeBrushDurability.chargesDurability(false, 2, bound -> 1));
        assertFalse(BiomeBrushDurability.chargesDurability(false, 2, bound -> 2));
    }
}
