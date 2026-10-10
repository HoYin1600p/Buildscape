package com.kingodogo.buildscape.util;

public final class BeaconBeamScanState {
    public static final int UNLIMITED = 1024;

    private int confirmedHeight = UNLIMITED;
    private int pendingHeight = UNLIMITED;
    private boolean scanning;

    public int confirmedHeight() {
        return this.confirmedHeight;
    }

    public int pendingHeight() {
        return this.pendingHeight;
    }

    public void beginScan() {
        this.pendingHeight = UNLIMITED;
        this.scanning = true;
    }

    public void markBlocked(int height) {
        if (this.scanning) {
            this.pendingHeight = Math.min(this.pendingHeight, Math.max(0, height));
        }
    }

    public void completeScan() {
        if (this.scanning) {
            this.confirmedHeight = this.pendingHeight;
            this.scanning = false;
        }
    }
}
