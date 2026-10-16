package com.kingodogo.buildscape.event;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FrameRemovalTest {
    @Test
    void onlyDestroyedRemovedFramesLoseTheirRegistration() {
        assertTrue(ItemFrameParticleHandler.shouldRemoveFrameRegistration(true, true));
        assertFalse(ItemFrameParticleHandler.shouldRemoveFrameRegistration(true, false));
        assertFalse(ItemFrameParticleHandler.shouldRemoveFrameRegistration(false, false));
        assertFalse(ItemFrameParticleHandler.shouldRemoveFrameRegistration(false, true));
    }
}
