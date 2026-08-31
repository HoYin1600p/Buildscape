package com.kingodogo.buildscape.firework;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

public abstract class CustomFireworkShape {
    private final ResourceLocation id;
    private final byte numericId;

    public CustomFireworkShape(ResourceLocation id, byte numericId) {
        this.id = id;
        this.numericId = numericId;
    }

    public ResourceLocation getId() {
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
