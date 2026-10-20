package com.kingodogo.buildscape.feature.pillar;

import net.minecraft.util.StringRepresentable;

/**
 * Position of a pillar block within its vertical stack: {@code SINGLE}, {@code BOTTOM}, {@code MIDDLE} or {@code TOP}.
 * Used as a block state property.
 */
public enum PillarPart implements StringRepresentable {
    SINGLE("single"),
    BOTTOM("bottom"),
    MIDDLE("middle"),
    TOP("top");

    private final String name;

    PillarPart(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
