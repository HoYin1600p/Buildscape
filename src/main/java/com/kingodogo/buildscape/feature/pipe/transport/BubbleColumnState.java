package com.kingodogo.buildscape.feature.pipe.transport;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

/**
 * Whether a pipe carries a bubble column and in which direction: {@code NONE}, {@code UP} (soul sand) or
 * {@code DOWN} (magma). The serialized name is what {@link PipeFlowState} saves to NBT.
 */
public enum BubbleColumnState implements StringRepresentable {
    NONE("none"),
    UP("up"),
    DOWN("down");

    private final String name;

    BubbleColumnState(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public boolean isUp() {
        return this == UP;
    }

    public boolean isDown() {
        return this == DOWN;
    }

    public boolean isActive() {
        return this != NONE;
    }

    public static BubbleColumnState byName(String name) {
        if (name == null) {
            return NONE;
        }
        try {
            return BubbleColumnState.valueOf(name.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return NONE;
        }
    }
}
