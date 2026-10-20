package com.kingodogo.buildscape.core;

import java.util.List;

/**
 * The particle pattern names exactly as they are stored in config files, presets, block entity data and packets.
 * Do not change a value: it would break saved data.
 */
public final class ParticlePatterns {
    /** No particles. */
    public static final String NONE = "none";
    /** Use the pattern configured for the whole mod. */
    public static final String DEFAULT = "default";
    public static final String BEAM = "beam";
    public static final String SPIRAL = "spiral";
    public static final String FOUNTAIN = "fountain";
    public static final String PULSE = "pulse";
    public static final String RING = "ring";
    public static final String BURST = "burst";
    public static final String SNOWFLAKE = "snowflake";

    /** Marker for an item frame that has no pattern chosen yet; it is not a pattern a player can select. */
    public static final String NOT_SET = "__not_set__";

    /** Every pattern in the order the pickers cycle through them. */
    public static final List<String> ALL = List.of(NONE, DEFAULT, BEAM, SPIRAL, FOUNTAIN, PULSE, RING, BURST,
            SNOWFLAKE);

    private ParticlePatterns() {}

    /** Whether {@code name} is one of the stored pattern names. */
    public static boolean isKnown(String name) {
        return ALL.contains(name);
    }
}
