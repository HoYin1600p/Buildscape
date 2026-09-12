package com.kingodogo.buildscape.client.screen;

import java.util.List;

final class ParticleColorSlots {
    static final String DEFAULT_COLOR = "#FFFFFF";

    private ParticleColorSlots() {
    }

    static String colorAt(List<String> colors, int index) {
        if (colors == null || index < 0 || index >= colors.size()) {
            return DEFAULT_COLOR;
        }
        String color = colors.get(index);
        return color != null ? color : DEFAULT_COLOR;
    }

    static void ensureCapacity(List<String> colors, int count) {
        while (colors.size() < count) {
            colors.add(DEFAULT_COLOR);
        }
    }
}
