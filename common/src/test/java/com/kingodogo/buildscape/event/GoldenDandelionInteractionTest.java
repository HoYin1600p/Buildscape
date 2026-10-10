package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.util.GoldenDandelionGrowth;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GoldenDandelionInteractionTest {
    @Test
    void freezesBabiesButDoesNotFreezeUnfrozenAdults() {
        assertTrue(GoldenDandelionGrowth.canToggle(true, false));
        assertFalse(GoldenDandelionGrowth.canToggle(false, false));
    }

    @Test
    void frozenAnimalsCanAlwaysBeUnfrozen() {
        assertTrue(GoldenDandelionGrowth.canToggle(true, true));
        assertTrue(GoldenDandelionGrowth.canToggle(false, true));
    }

    @Test
    void naturalAgeProgressResumesAfterUnfreezing() {
        assertFalse(GoldenDandelionGrowth.shouldAdvanceNaturally(true));
        assertTrue(GoldenDandelionGrowth.shouldAdvanceNaturally(false));
    }
}
