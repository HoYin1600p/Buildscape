package com.kingodogo.buildscape.p1misc;

import com.kingodogo.buildscape.client.renderer.PillarDisplayEffects;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PillarDisplayEffectsTest {
    @Test
    public void outlineRequestsDoNotLeakIntoFollowingFramesOrDisabledViews() {
        PillarDisplayEffects.beginFrame(true);
        assertTrue(PillarDisplayEffects.outlinesEnabled());
        assertFalse(PillarDisplayEffects.hasOutline());
        PillarDisplayEffects.markOutlineRendered();
        assertTrue(PillarDisplayEffects.hasOutline());
        PillarDisplayEffects.beginFrame(true);
        assertFalse(PillarDisplayEffects.hasOutline());
        PillarDisplayEffects.beginFrame(false);
        assertFalse(PillarDisplayEffects.outlinesEnabled());
        assertFalse(PillarDisplayEffects.hasOutline());
    }
}
