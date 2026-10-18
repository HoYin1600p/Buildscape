package com.kingodogo.buildscape.client.screen.widget;

public final class ColorPickerGeometry {
    private ColorPickerGeometry() {
    }

    public static float fraction(double coordinate, int origin, int size) {
        if (size < 2) {
            return 0.0f;
        }
        return (float) Math.max(0.0, Math.min(1.0, (coordinate - origin) / (size - 1)));
    }
}
