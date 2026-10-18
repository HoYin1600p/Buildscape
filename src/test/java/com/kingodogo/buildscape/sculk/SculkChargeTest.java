package com.kingodogo.buildscape.sculk;

import com.kingodogo.buildscape.block.sculk.SculkCharge;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SculkChargeTest {
    @Test
    public void eligibleExperienceIsConservedWithinCapacity() {
        for (int available = 0; available <= 32; available++) {
            for (int experience : new int[]{0, 1, 5, 999, 1000, 1001, 31999, 32000, Integer.MAX_VALUE}) {
                int[] charges = SculkCharge.split(experience, available);
                assertTrue(charges.length <= available);
                int total = 0;
                for (int charge : charges) {
                    assertTrue(charge > 0 && charge <= 1000);
                    total += charge;
                }
                assertEquals(Math.min(experience, available * 1000), total);
            }
        }
    }

    @Test
    public void invalidOrExcessCapacityCannotAllocateUnboundedCursors() {
        assertEquals(0, SculkCharge.split(-1, 32).length);
        assertEquals(0, SculkCharge.split(Integer.MIN_VALUE, 32).length);
        assertEquals(0, SculkCharge.split(100, -1).length);
        int[] charges = SculkCharge.split(Integer.MAX_VALUE, Integer.MAX_VALUE);
        assertEquals(32, charges.length);
        for (int charge : charges) assertEquals(1000, charge);
    }
}
