package com.kingodogo.buildscape.event;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemFrameIdentityTest {
    @Test
    void persistedEntityIdTakesPriorityOverPositionRecovery() {
        assertEquals("F-1001", ItemFrameParticleHandler.selectExistingFrameId("F-1001", "F-2002"));
    }

    @Test
    void positionRecoveryKeepsAnIdStableWhenEntityDataIsMissing() {
        assertEquals("F-2002", ItemFrameParticleHandler.selectExistingFrameId("", "F-2002"));
        assertNull(ItemFrameParticleHandler.selectExistingFrameId(null, "P-1001"));
        assertNull(ItemFrameParticleHandler.selectExistingFrameId(null, null));
    }

    @Test
    void ordinaryChunkUnloadDoesNotDeleteAFrameRegistration() {
        assertFalse(ItemFrameParticleHandler.shouldRemoveFrameRegistration(true, false));
        assertFalse(ItemFrameParticleHandler.shouldRemoveFrameRegistration(false, true));
        assertTrue(ItemFrameParticleHandler.shouldRemoveFrameRegistration(true, true));
    }
}
