package com.kingodogo.buildscape.block;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EyeblossomTransitionTest {
    @Test
    void loadedFlowersCatchUpAndContinueCyclingWithDaylight() {
        assertTrue(EyeblossomTransitionHandler.shouldTransition(false, false, true));
        assertFalse(EyeblossomTransitionHandler.shouldTransition(true, false, true));
        assertTrue(EyeblossomTransitionHandler.shouldTransition(true, false, false));
        assertFalse(EyeblossomTransitionHandler.shouldTransition(false, false, false));
    }

    @Test
    void waxedFlowersKeepTheirSavedStateAcrossTimeChanges() {
        assertFalse(EyeblossomTransitionHandler.shouldTransition(false, true, true));
        assertFalse(EyeblossomTransitionHandler.shouldTransition(true, true, false));
    }
}
