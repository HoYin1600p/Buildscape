package com.kingodogo.buildscape.client.screen;

/**
 * Configuration for debug rendering options in the UI.
 * Set these flags to enable/disable visual debugging aids.
 */
public class DebugRenderConfig {

    /**
     * When true, renders 1-pixel grey borders around all panels.
     * Useful for debugging panel boundaries and scrollbar clipping.
     * Set to false before shipping to production.
     */
    public static final boolean RENDER_PANEL_BORDERS = true;

    /**
     * Border color for panel boundaries (grey).
     */
    public static final int PANEL_BORDER_COLOR = 0xFF666666;

    private DebugRenderConfig() {
        // Utility class, prevent instantiation
    }
}
