package com.kingodogo.buildscape.event;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FrostRoseDropHandlerTest {
    @Test
    void suppressesDropsWithinTwoBlocksOfTheSnowGolemDeath() {
        assertTrue(FrostRoseDropHandler.isWithinDropRange(0, 0, 0));
        assertTrue(FrostRoseDropHandler.isWithinDropRange(0.5, 0.5, 0.5));
        assertTrue(FrostRoseDropHandler.isWithinDropRange(-1.99, 0, 0));
    }

    @Test
    void leavesDropsAtOrBeyondTheSphericalBoundaryAlone() {
        assertFalse(FrostRoseDropHandler.isWithinDropRange(2, 0, 0));
        assertFalse(FrostRoseDropHandler.isWithinDropRange(0, -2, 0));
        assertFalse(FrostRoseDropHandler.isWithinDropRange(0, 0, 2));
        assertFalse(FrostRoseDropHandler.isWithinDropRange(1.5, 1.5, 0));
    }
}
