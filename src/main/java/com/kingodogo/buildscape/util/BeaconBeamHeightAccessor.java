package com.kingodogo.buildscape.util;

public interface BeaconBeamHeightAccessor {
    int buildscape$getBeamHeight();
    int buildscape$getPendingBeamHeight();
    void buildscape$beginBeamScan();
    void buildscape$markBeamBlocked(int height);
    void buildscape$completeBeamScan();
}
