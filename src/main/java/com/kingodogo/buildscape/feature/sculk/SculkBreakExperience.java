package com.kingodogo.buildscape.feature.sculk;

public final class SculkBreakExperience {
    private SculkBreakExperience() {}

    /** Experience for breaking the sculk-family block with this registry path, or -1 if it is not one. */
    public static int forBlock(String path) {
        if (path == null) {
            return -1;
        }
        return switch (path) {
            case "sculk", "sculk_slab", "sculk_stairs", "sculk_wall", "sculk_vertical_slab", "sculk_pillar" -> 1;
            case "sculk_vein" -> 0;
            case "sculk_catalyst", "sculk_shrieker", "sculk_sensor" -> 5;
            default -> -1;
        };
    }

    public static int withTool(int experience, int silkTouchLevel) {
        return silkTouchLevel > 0 ? 0 : experience;
    }
}
