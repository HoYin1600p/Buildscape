package com.kingodogo.buildscape.feature.sculk;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SculkGrowthRulesTest {
    @Test
    public void growthStartsExactlyFourBlocksFromCatalyst() {
        assertTrue(SculkGrowthRules.insideNoGrowthRadius(15));
        assertFalse(SculkGrowthRules.insideNoGrowthRadius(16));
        assertFalse(SculkGrowthRules.insideNoGrowthRadius(17));
        assertEquals(new SculkGrowthRules.ChargeUse(99, false),
                SculkGrowthRules.useCharge(100, 15, true, rolls(0, 0)));
        assertEquals(new SculkGrowthRules.ChargeUse(90, true),
                SculkGrowthRules.useCharge(100, 16, true, rolls(0, 9)));
    }

    @Test
    public void onlyOneOfTenChargeStepsCanGrow() {
        int growths = 0;
        for (int roll = 0; roll < 10; roll++) {
            SculkGrowthRules.ChargeUse use = SculkGrowthRules.useCharge(10, 25, true, rolls(roll, 0));
            if (use.grow()) {
                growths++;
            }
            assertEquals(roll == 0 ? 0 : 10, use.remainingCharge());
        }
        assertEquals(1, growths);
        assertEquals(new SculkGrowthRules.ChargeUse(0, false),
                SculkGrowthRules.useCharge(0, 25, true, rolls()));
    }

    @Test
    public void growthBoxIsReadOnlyOnAnEligibleChargeStep() {
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
    public void lowChargeControlsGrowthChanceButEveryAttemptCostsTen() {
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
    public void nonGrowthChargeDecaysOneTimeInFiftyWithoutHalvingNearCatalyst() {
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
    public void decayPenaltyScalesWithDistanceAndCapsAtHalfCharge() {
        assertEquals(1, SculkGrowthRules.decayPenalty(1000, 16));
        assertEquals(1, SculkGrowthRules.decayPenalty(1000, 25));
        assertEquals(20, SculkGrowthRules.decayPenalty(1000, 64));
        assertEquals(125, SculkGrowthRules.decayPenalty(1000, 196));
        assertEquals(500, SculkGrowthRules.decayPenalty(1000, 576));
        assertEquals(500, SculkGrowthRules.decayPenalty(1000, 1000000));
        assertEquals(12, SculkGrowthRules.decayPenalty(100, 196));
        assertEquals(new SculkGrowthRules.ChargeUse(0, false),
                SculkGrowthRules.useCharge(1, 576, false, rolls(0, 0)));
    }

    @Test
    public void growthIsAllowedWithTwoNearbyButRefusedWithThree() {
        assertTrue(SculkGrowthRules.hasGrowthCapacity(0));
        assertTrue(SculkGrowthRules.hasGrowthCapacity(1));
        assertTrue(SculkGrowthRules.hasGrowthCapacity(2));
        assertFalse(SculkGrowthRules.hasGrowthCapacity(3));
    }

    @Test
    public void defaultCellsExpireAfterTheirGraceStepAndSculkKeepsItsDelay() {
        assertEquals(1000, SculkGrowthRules.chargeOnDefaultCell(1000, 1));
        assertEquals(0, SculkGrowthRules.nextDecayDelay(false, 1));
        assertEquals(0, SculkGrowthRules.chargeOnDefaultCell(1000, 0));
        assertEquals(0, SculkGrowthRules.nextDecayDelay(false, 0));
        assertEquals(1, SculkGrowthRules.nextDecayDelay(true, 0));
        assertEquals(1, SculkGrowthRules.nextDecayDelay(true, 1));
    }

    private static Random rolls(int... values) {
        return new Random(0) {
            private int index;

            @Override
            public int nextInt(int bound) {
                assertTrue("Unexpected random draw", index < values.length);
                int value = values[index++];
                assertTrue(value >= 0 && value < bound);
                return value;
            }
        };
    }
}
