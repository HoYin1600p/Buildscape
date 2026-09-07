package com.kingodogo.buildscape.util;

public final class BeaconBeamScanStateTest {
    private BeaconBeamScanStateTest() {
    }

    public static void main(String[] args) {
        BeaconBeamScanState state = new BeaconBeamScanState();
        expect(BeaconBeamScanState.UNLIMITED, state.confirmedHeight(), "initial beam height");

        state.beginScan();
        state.markBlocked(18);
        expect(BeaconBeamScanState.UNLIMITED, state.confirmedHeight(), "incomplete first scan");
        expect(18, state.pendingHeight(), "pending first obstruction");
        state.completeScan();
        expect(18, state.confirmedHeight(), "completed first scan");

        state.beginScan();
        expect(18, state.confirmedHeight(), "confirmed obstruction during rescan");
        expect(BeaconBeamScanState.UNLIMITED, state.pendingHeight(), "clear pending rescan");
        state.completeScan();
        expect(BeaconBeamScanState.UNLIMITED, state.confirmedHeight(), "removed obstruction");

        state.beginScan();
        state.markBlocked(12);
        state.markBlocked(7);
        state.completeScan();
        expect(7, state.confirmedHeight(), "nearest obstruction");
    }

    private static void expect(int expected, int actual, String label) {
        if (expected != actual) {
            throw new AssertionError(label + ": expected " + expected + ", got " + actual);
        }
    }
}
