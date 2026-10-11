package com.kingodogo.buildscape.firework;

import com.kingodogo.buildscape.util.CommonId;

import java.util.List;

public abstract class CustomFireworkShape {
    private final CommonId id;
    private final byte numericId;

    protected CustomFireworkShape(CommonId id, byte numericId) {
        this.id = id;
        this.numericId = numericId;
    }

    public CommonId getId() {
        return id;
    }

    public byte getNumericId() {
        return numericId;
    }

    public abstract List<FireworkPoint> generatePoints();

    public double getBaseScale() {
        return 0.5D;
    }
}
