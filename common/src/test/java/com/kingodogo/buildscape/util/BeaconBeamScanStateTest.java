package com.kingodogo.buildscape.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BeaconBeamScanStateTest {
    @Test
    void keepsPreviousHeightUntilScanCompletesAndClearsRemovedStopper() {
        var scan = new BeaconBeamScanState();
        scan.beginScan();
        scan.markBlocked(18);
        assertEquals(BeaconBeamScanState.UNLIMITED, scan.confirmedHeight());
        scan.completeScan();
        assertEquals(18, scan.confirmedHeight());
        scan.beginScan();
        assertEquals(18, scan.confirmedHeight());
        scan.completeScan();
        assertEquals(BeaconBeamScanState.UNLIMITED, scan.confirmedHeight());
    }

    @Test
    void nearestStopperWinsAndOutOfScanUpdatesAreIgnored() {
        var scan = new BeaconBeamScanState();
        scan.markBlocked(2);
        scan.beginScan();
        scan.markBlocked(12);
        scan.markBlocked(7);
        scan.completeScan();
        scan.markBlocked(1);
        assertEquals(7, scan.confirmedHeight());
    }
}
