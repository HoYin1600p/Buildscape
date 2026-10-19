package com.kingodogo.buildscape.feature.sculk;

public final class SculkCharge {
    public static final int MAX_CURSORS = 32;
    public static final int MAX_CHARGE = 1000;

    private SculkCharge() {
    }

    public static int[] split(int experience, int availableCursors) {
        int capacity = Math.max(0, Math.min(MAX_CURSORS, availableCursors));
        int accepted = Math.max(0, Math.min(experience, capacity * MAX_CHARGE));
        int[] charges = new int[(accepted + MAX_CHARGE - 1) / MAX_CHARGE];
        for (int i = 0; i < charges.length; i++) {
            charges[i] = Math.min(accepted, MAX_CHARGE);
            accepted -= charges[i];
        }
        return charges;
    }
}
