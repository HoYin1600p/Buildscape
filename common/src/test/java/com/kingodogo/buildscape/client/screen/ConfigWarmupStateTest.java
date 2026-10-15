package com.kingodogo.buildscape.client.screen;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigWarmupStateTest {
    @Test
    void onlyOneWarmupCanBeScheduledUntilItFinishes() {
        BuildScapeConfigScreen.ConfigWarmupState state = new BuildScapeConfigScreen.ConfigWarmupState();

        assertTrue(state.tryBegin());
        assertFalse(state.tryBegin());

        state.finish(false);
        assertTrue(state.tryBegin());
    }

    @Test
    void successfulWarmupPermanentlyClosesTheState() {
        BuildScapeConfigScreen.ConfigWarmupState state = new BuildScapeConfigScreen.ConfigWarmupState();

        assertTrue(state.tryBegin());
        state.finish(true);

        assertTrue(state.isComplete());
        assertFalse(state.tryBegin());
    }
}
