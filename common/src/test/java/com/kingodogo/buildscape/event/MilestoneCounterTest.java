package com.kingodogo.buildscape.event;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MilestoneCounterTest {
    @Test void existingLifetimeStatsDoNotImmediatelyUnlockNewMilestones() {
        int baseline = MilestoneCounter.baseline(10000, null);
        assertFalse(MilestoneCounter.reached(10000, baseline, 10));
        assertFalse(MilestoneCounter.reached(10008, baseline, 10));
        assertTrue(MilestoneCounter.reached(10009, baseline, 10));
    }

    @Test void resettingLifetimeStatsRestartsRelativeProgress() {
        int baseline = MilestoneCounter.baseline(4, 500);
        assertEquals(3, baseline);
        assertFalse(MilestoneCounter.reached(4, baseline, 10));
        assertTrue(MilestoneCounter.reached(13, baseline, 10));
        assertEquals(baseline, MilestoneCounter.baseline(7, baseline));
    }
    @Test void batchCraftingAndIndividualCraftingReachTheSameJarMilestone() {
        int individual = 0;
        for (int i = 0; i < 100; i++) individual = MilestoneCounter.advance(individual, 1);
        int batch = MilestoneCounter.advance(MilestoneCounter.advance(0, 64), 36);
        assertEquals(100, batch);
        assertEquals(individual, batch);
        assertTrue(MilestoneCounter.advance(98, 4) >= 100);
    }

    @Test void largeCraftingCountsCannotWrapAndLoseProgress() {
        assertEquals(Integer.MAX_VALUE, MilestoneCounter.advance(Integer.MAX_VALUE - 1, 64));
        assertEquals(Integer.MAX_VALUE, MilestoneCounter.advance(Integer.MAX_VALUE, Integer.MAX_VALUE));
    }

    @Test void invalidCountsCannotRemoveEarnedProgress() {
        assertEquals(365, MilestoneCounter.advance(365, -1));
        assertEquals(4, MilestoneCounter.advance(-10, 4));
        assertEquals(99, MilestoneCounter.advance(99, 0));
    }
}
