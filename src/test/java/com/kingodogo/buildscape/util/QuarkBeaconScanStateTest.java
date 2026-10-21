package com.kingodogo.buildscape.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public final class QuarkBeaconScanStateTest {

    @Test
    public void ordinaryBlockersStillClearTheBeam() {
        BeaconBeamScanState state = new BeaconBeamScanState();
        state.beginScan();
        assertFalse(state.shouldClearBeam(false));
        assertTrue(state.shouldClearBeam(true));
    }

    @Test
    public void stopperKeepsTheFinalSegmentDespiteTheBlockingResult() {
        BeaconBeamScanState state = new BeaconBeamScanState();
        state.beginScan();
        state.markStopped();
        assertFalse(state.shouldClearBeam(true));
        assertFalse(state.shouldClearBeam(false));
        assertFalse(state.confirmedStopped());
    }

    @Test
    public void nextBeaconOrNextTickDoesNotInheritTheStopper() {
        BeaconBeamScanState state = new BeaconBeamScanState();
        state.beginScan();
        state.markStopped();
        state.beginScan();
        assertTrue(state.shouldClearBeam(true));
    }

    @Test
    public void previousPublishedStopDoesNotKeepAnOrdinaryBlockerActive() {
        BeaconBeamScanState state = new BeaconBeamScanState();
        state.beginScan();
        state.markStopped();
        state.completeScan();
        state.beginScan();
        assertTrue(state.confirmedStopped());
        assertTrue(state.shouldClearBeam(true));
    }

    @Test
    public void interleavedScansHaveIndependentDecisions() {
        BeaconBeamScanState client = new BeaconBeamScanState();
        BeaconBeamScanState server = new BeaconBeamScanState();
        client.beginScan();
        server.beginScan();
        client.markStopped();
        assertTrue(server.shouldClearBeam(true));
        server.beginScan();
        assertFalse(client.shouldClearBeam(true));
    }
}
