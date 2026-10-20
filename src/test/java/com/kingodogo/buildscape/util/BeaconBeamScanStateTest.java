package com.kingodogo.buildscape.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public final class BeaconBeamScanStateTest {

    @Test
    public void stoppedStateChangesOnlyWhenTheScanCompletes() {
        BeaconBeamScanState state = new BeaconBeamScanState();
        assertFalse(state.confirmedStopped());
        state.markStopped();
        state.completeScan();
        assertFalse(state.confirmedStopped());

        state.beginScan();
        state.markStopped();
        assertTrue(state.pendingStopped());
        assertFalse(state.confirmedStopped());
        state.completeScan();
        assertTrue(state.confirmedStopped());

        state.beginScan();
        assertFalse(state.pendingStopped());
        assertTrue(state.confirmedStopped());
        state.completeScan();
        assertFalse(state.confirmedStopped());
    }
}
