package com.kingodogo.buildscape.client;

import java.util.List;

/** Selects a glow-light colour using the release's stable position hash rule. */
public final class GlowLightTint {
    private GlowLightTint() { }

    public static int color(int hash, List<String> colors) {
        if (colors == null || colors.isEmpty()) return -1;
        String value = colors.get(Math.abs(hash % colors.size()));
        if (value == null || value.length() != 7 || !value.startsWith("#")) return -1;
        try {
            return 0xff000000 | Integer.parseInt(value.substring(1), 16);
        } catch (NumberFormatException invalid) {
            return -1;
        }
    }
}
