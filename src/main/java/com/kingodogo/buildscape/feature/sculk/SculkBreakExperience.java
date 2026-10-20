package com.kingodogo.buildscape.feature.sculk;

public final class SculkBreakExperience {
    private SculkBreakExperience() {}

    /** Experience for breaking the sculk-family block with this registry path, or -1 if it is not one. */
    public static int forBlock(String path) {
        if (path == null) {
            return -1;
        }
        switch (path) {
            case "sculk" :
            case "sculk_slab" :
            case "sculk_stairs" :
            case "sculk_wall" :
            case "sculk_vertical_slab" :
            case "sculk_pillar" :
                return 1;
            case "sculk_vein" :
                return 0;
            case "sculk_catalyst" :
            case "sculk_shrieker" :
            case "sculk_sensor" :
                return 5;
            default :
                return -1;
        }
    }

    public static int withTool(int experience, int silkTouchLevel) {
        return silkTouchLevel > 0 ? 0 : experience;
    }
}
