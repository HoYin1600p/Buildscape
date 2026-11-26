package com.kingodogo.buildscape.block;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/** Vanilla 1.19 catalyst charge, death and growth rules of the 1.18.2 sculk backport. */
class SculkRulesTest {
    @Test
    void eligibleExperienceIsConservedWithinCursorCapacity() {
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
    void invalidOrExcessCapacityCannotAllocateUnboundedCursors() {
        assertEquals(0, SculkCharge.split(-1, 32).length);
        assertEquals(0, SculkCharge.split(Integer.MIN_VALUE, 32).length);
        assertEquals(0, SculkCharge.split(100, -1).length);
        int[] charges = SculkCharge.split(Integer.MAX_VALUE, Integer.MAX_VALUE);
        assertEquals(32, charges.length);
        for (int charge : charges) {
            assertEquals(1000, charge);
        }
    }

    @Test
    void deathChargesOnceAndEveryLaterDropIsZeroed() {
        SculkDeathRules.ConsumptionGuard<Object> guard = new SculkDeathRules.ConsumptionGuard<>();
        Object entity = new Object();
        Object other = new Object();
        assertEquals(12, guard.experienceToDrop(entity, 12));
        assertTrue(guard.tryConsume(entity));
        assertFalse(guard.tryConsume(entity));
        assertEquals(0, guard.experienceToDrop(entity, 100));
        assertEquals(7, guard.experienceToDrop(other, 7));
    }

    @Test
    void rangeIsEightBlocksAndNearestCatalystWinsWithStableTies() {
        assertTrue(SculkDeathRules.isInRange(0.0));
        assertTrue(SculkDeathRules.isInRange(64.0));
        assertFalse(SculkDeathRules.isInRange(65.0));
        assertFalse(SculkDeathRules.isInRange(192.0));
        assertFalse(SculkDeathRules.isInRange(-1.0));
        assertFalse(SculkDeathRules.isInRange(Double.NaN));
        assertTrue(SculkDeathRules.preferCatalyst(64.0, Double.MAX_VALUE, -1));
        assertTrue(SculkDeathRules.preferCatalyst(9.0, 16.0, 1));
        assertFalse(SculkDeathRules.preferCatalyst(16.0, 9.0, -1));
        assertTrue(SculkDeathRules.preferCatalyst(9.0, 9.0, -1));
        assertFalse(SculkDeathRules.preferCatalyst(9.0, 9.0, 1));
        assertFalse(SculkDeathRules.preferCatalyst(65.0, Double.MAX_VALUE, -1));
    }

    @Test
    void growthStartsExactlyFourBlocksFromTheCatalyst() {
        assertTrue(SculkGrowthRules.insideNoGrowthRadius(15));
        assertFalse(SculkGrowthRules.insideNoGrowthRadius(16));
        assertEquals(new SculkGrowthRules.ChargeUse(99, false),
                SculkGrowthRules.useCharge(100, 15, true, rolls(0, 0)));
        assertEquals(new SculkGrowthRules.ChargeUse(90, true),
                SculkGrowthRules.useCharge(100, 16, true, rolls(0, 9)));
    }

    @Test
    void onlyOneOfTenChargeStepsCanGrow() {
        int growths = 0;
        for (int roll = 0; roll < 10; roll++) {
            SculkGrowthRules.ChargeUse use = SculkGrowthRules.useCharge(10, 25, true, rolls(roll, 0));
            if (use.grow()) {
                growths++;
            }
            assertEquals(roll == 0 ? 0 : 10, use.remainingCharge());
        }
        assertEquals(1, growths);
        assertEquals(new SculkGrowthRules.ChargeUse(0, false), SculkGrowthRules.useCharge(0, 25, true, rolls()));
    }

    @Test
    void growthBoxIsReadOnlyOnAnEligibleChargeStep() {
        for (double distanceSquared : new double[]{0, 15, 16, 100}) {
            for (int roll = 0; roll < 10; roll++) {
                if (distanceSquared < 16 || roll != 0) {
                    SculkGrowthRules.useCharge(100, distanceSquared, () -> {
                        fail("Ineligible step read the growth box");
                        return false;
                    }, rolls(roll, 1));
                }
            }
        }
    }

    @Test
    void lowChargeControlsGrowthChanceButEveryAttemptCostsTen() {
        for (int charge = 1; charge <= 11; charge++) {
            int growths = 0;
            for (int roll = 0; roll < 10; roll++) {
                SculkGrowthRules.ChargeUse use = SculkGrowthRules.useCharge(charge, 25, true, rolls(0, roll));
                if (use.grow()) {
                    growths++;
                }
                assertEquals(Math.max(0, charge - 10), use.remainingCharge());
            }
            assertEquals(Math.min(charge, 10), growths);
        }
    }

    @Test
    void nonGrowthChargeDecaysOneTimeInFiftyWithoutHalvingNearTheCatalyst() {
        for (int step = 0; step < 10; step++) {
            for (int additional = 0; additional < 5; additional++) {
                boolean decay = step == 0 && additional == 0;
                assertEquals(new SculkGrowthRules.ChargeUse(decay ? 999 : 1000, false),
                        SculkGrowthRules.useCharge(1000, 9, false, rolls(step, additional)));
                assertEquals(new SculkGrowthRules.ChargeUse(decay ? 875 : 1000, false),
                        SculkGrowthRules.useCharge(1000, 196, false, rolls(step, additional)));
            }
        }
    }

    @Test
    void decayPenaltyScalesWithDistanceAndCapsAtHalfCharge() {
        assertEquals(1, SculkGrowthRules.decayPenalty(1000, 16));
        assertEquals(20, SculkGrowthRules.decayPenalty(1000, 64));
        assertEquals(125, SculkGrowthRules.decayPenalty(1000, 196));
        assertEquals(500, SculkGrowthRules.decayPenalty(1000, 576));
        assertEquals(500, SculkGrowthRules.decayPenalty(1000, 1000000));
        assertEquals(12, SculkGrowthRules.decayPenalty(100, 196));
        assertEquals(new SculkGrowthRules.ChargeUse(0, false),
                SculkGrowthRules.useCharge(1, 576, false, rolls(0, 0)));
    }

    @Test
    void growthNeedsFewerThanThreeNearbyAndDefaultCellsExpireAfterOneStep() {
        assertTrue(SculkGrowthRules.hasGrowthCapacity(2));
        assertFalse(SculkGrowthRules.hasGrowthCapacity(3));
        assertEquals(1000, SculkGrowthRules.chargeOnDefaultCell(1000, 1));
        assertEquals(0, SculkGrowthRules.chargeOnDefaultCell(1000, 0));
        assertEquals(0, SculkGrowthRules.nextDecayDelay(false, 1));
        assertEquals(1, SculkGrowthRules.nextDecayDelay(true, 0));
    }

    private static Random rolls(int... values) {
        return new Random(0) {
            private int index;

            @Override
            public int nextInt(int bound) {
                assertTrue(index < values.length, "Unexpected random draw");
                int value = values[index++];
                assertTrue(value >= 0 && value < bound);
                return value;
            }
        };
    }
}
