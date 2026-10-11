package com.kingodogo.buildscape.pipe.transport;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

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
        if (name == null) return NONE;
        try {
            return BubbleColumnState.valueOf(name.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return NONE;
        }
    }
}
