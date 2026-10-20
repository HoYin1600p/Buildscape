package com.kingodogo.buildscape.util;

public final class BeaconBeamScanState {
    private boolean confirmedStopped;
    private boolean pendingStopped;
    private boolean scanning;

    public boolean confirmedStopped() {
        return this.confirmedStopped;
    }

    public boolean pendingStopped() {
        return this.pendingStopped;
    }

    public void beginScan() {
        this.pendingStopped = false;
        this.scanning = true;
    }

    public void markStopped() {
        if (this.scanning) {
            this.pendingStopped = true;
        }
    }

    public void completeScan() {
        if (this.scanning) {
            this.confirmedStopped = this.pendingStopped;
            this.scanning = false;
        }
    }
}
