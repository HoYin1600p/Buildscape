package com.kingodogo.buildscape.block.behavior;
public final class FallingSandPhysics {

    public static final int DEFAULT_FALL_DELAY = 2;

    private FallingSandPhysics() {}
    public static boolean canFallThrough(boolean isReplaceable, boolean isFluidEmpty) {
        return isReplaceable && isFluidEmpty;
    }
}
