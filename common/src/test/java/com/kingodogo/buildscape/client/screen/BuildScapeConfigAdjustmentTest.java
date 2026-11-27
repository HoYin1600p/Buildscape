package com.kingodogo.buildscape.client.screen;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BuildScapeConfigAdjustmentTest {
    @Test
    void controlClickUsesTenTimesTheFineStep() {
        assertEquals(0.21, BuildScapeConfigScreen.adjustConfigValue(0.20, 1, 0.01, false, 0.01, 1.0, 2));
        assertEquals(0.30, BuildScapeConfigScreen.adjustConfigValue(0.20, 1, 0.01, true, 0.01, 1.0, 2));
        assertEquals(0.011, BuildScapeConfigScreen.adjustConfigValue(0.001, 1, 0.001, true, 0.001, 1.0, 3));
    }

    @Test
    void adjustmentRoundsDecrementsAndClampsAtBothBounds() {
        assertEquals(0.19, BuildScapeConfigScreen.adjustConfigValue(0.20, -1, 0.01, false, 0.01, 1.0, 2));
        assertEquals(0.01, BuildScapeConfigScreen.adjustConfigValue(0.01, -1, 0.01, true, 0.01, 1.0, 2));
        assertEquals(1.0, BuildScapeConfigScreen.adjustConfigValue(0.99, 1, 0.01, true, 0.01, 1.0, 2));
    }
}
