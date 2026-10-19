package com.kingodogo.buildscape.block;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FireflyNightTest {
    private static float timeOfDay(long dayTime) {
        // Vanilla DimensionType.timeOfDay for a normal overworld day cycle.
        double d = (dayTime / 24000.0D) - 0.25D;
        if (d < 0.0D) {
            d += 1.0D;
        }
        double e = 0.5D - Math.cos(d * Math.PI) / 2.0D;
        return (float) ((d * 2.0D + e) / 3.0D);
    }

    @Test
    public void noonIsDayAndMidnightIsNight() {
        assertFalse(FireflyNight.isNight(timeOfDay(6000), 0.0F, 0.0F));
        assertTrue(FireflyNight.isNight(timeOfDay(18000), 0.0F, 0.0F));
    }

    @Test
    public void followsTheVanillaDayAndNightWindows() {
        assertFalse(FireflyNight.isNight(timeOfDay(1000), 0.0F, 0.0F));
        assertFalse(FireflyNight.isNight(timeOfDay(11000), 0.0F, 0.0F));
        assertTrue(FireflyNight.isNight(timeOfDay(14000), 0.0F, 0.0F));
        assertTrue(FireflyNight.isNight(timeOfDay(22000), 0.0F, 0.0F));
        assertTrue(FireflyNight.isNight(timeOfDay(0), 0.0F, 0.0F));
        assertFalse(FireflyNight.isNight(timeOfDay(2000), 0.0F, 0.0F));
    }

    @Test
    public void changesAsTimePassesWithoutRejoining() {
        boolean sawDay = false;
        boolean sawNight = false;
        boolean previous = FireflyNight.isNight(timeOfDay(0), 0.0F, 0.0F);
        int flips = 0;
        for (long t = 0; t < 24000; t += 20) {
            boolean night = FireflyNight.isNight(timeOfDay(t), 0.0F, 0.0F);
            sawDay |= !night;
            sawNight |= night;
            if (night != previous) {
                flips++;
                previous = night;
            }
        }
        assertTrue(sawDay && sawNight);
        assertEquals(2, flips);
    }

    @Test
    public void heavyStormDarkensTheDaySky() {
        assertFalse(FireflyNight.isNight(timeOfDay(6000), 1.0F, 0.0F));
        assertTrue(FireflyNight.isNight(timeOfDay(6000), 1.0F, 1.0F));
    }
}
