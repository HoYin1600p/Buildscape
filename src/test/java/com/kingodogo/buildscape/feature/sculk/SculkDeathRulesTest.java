package com.kingodogo.buildscape.feature.sculk;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SculkDeathRulesTest {
    @Test
    public void deathCanChargeOnlyOnceAndEveryLaterDropIsZeroed() {
        SculkDeathRules.ConsumptionGuard<Object> guard = new SculkDeathRules.ConsumptionGuard<>();
        Object entity = new Object();
        assertFalse(guard.isConsumed(entity));
        assertEquals(12, guard.experienceToDrop(entity, 12));
        assertTrue(guard.tryConsume(entity));
        assertEquals(0, guard.experienceToDrop(entity, 12));
        assertFalse(guard.tryConsume(entity));
        assertEquals(0, guard.experienceToDrop(entity, 100));
        assertTrue(guard.isConsumed(entity));
    }

    @Test
    public void consumingOneDeathDoesNotAffectAnother() {
        SculkDeathRules.ConsumptionGuard<Object> guard = new SculkDeathRules.ConsumptionGuard<>();
        Object consumed = new Object();
        Object other = new Object();
        assertTrue(guard.tryConsume(consumed));
        assertEquals(7, guard.experienceToDrop(other, 7));
        assertTrue(guard.tryConsume(other));
        assertEquals(0, guard.experienceToDrop(other, 7));
    }

    @Test
    public void rangeIncludesExactlyEightBlocksAndRejectsCubeCorners() {
        assertTrue(SculkDeathRules.isInRange(0.0));
        assertTrue(SculkDeathRules.isInRange(64.0));
        assertFalse(SculkDeathRules.isInRange(65.0));
        assertFalse(SculkDeathRules.isInRange(192.0));
        assertFalse(SculkDeathRules.isInRange(-1.0));
        assertFalse(SculkDeathRules.isInRange(Double.NaN));
    }

    @Test
    public void nearestCatalystWinsWithStablePositionOrderForTies() {
        assertTrue(SculkDeathRules.preferCatalyst(64.0, Double.MAX_VALUE, -1));
        assertTrue(SculkDeathRules.preferCatalyst(9.0, 16.0, 1));
        assertFalse(SculkDeathRules.preferCatalyst(16.0, 9.0, -1));
        assertTrue(SculkDeathRules.preferCatalyst(9.0, 9.0, -1));
        assertFalse(SculkDeathRules.preferCatalyst(9.0, 9.0, 1));
        assertFalse(SculkDeathRules.preferCatalyst(9.0, 9.0, 0));
        assertFalse(SculkDeathRules.preferCatalyst(65.0, Double.MAX_VALUE, -1));
    }
}
