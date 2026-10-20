package com.kingodogo.buildscape.client.renderer;

public final class PillarDisplayEffects {
    private static boolean outlinesEnabled;
    private static boolean outlineRendered;

    private PillarDisplayEffects() {}

    public static void beginFrame(boolean enabled) {
        outlinesEnabled = enabled;
        outlineRendered = false;
    }

    public static boolean outlinesEnabled() {
        return outlinesEnabled;
    }

    public static void markOutlineRendered() {
        outlineRendered = true;
    }

    public static boolean hasOutline() {
        return outlineRendered;
    }
}
