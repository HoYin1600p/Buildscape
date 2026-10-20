package com.kingodogo.buildscape.fluid;

/**
 * Null-safe identity matching for the experience fluids. Another mod, a registry sync or a registry
 * reload can leave the registry objects without a value; a missing value never matches anything.
 */
public final class ExperienceFluidMatch {
    private ExperienceFluidMatch() {}

    /** True when {@code fluid} is one of the candidates; null fluid or null candidates never match. */
    public static boolean matches(Object fluid, Object still, Object flowing) {
        if (fluid == null) {
            return false;
        }
        return (still != null && fluid == still) || (flowing != null && fluid == flowing);
    }

    /** True when {@code fluid} is the (non-null) candidate. */
    public static boolean matches(Object fluid, Object candidate) {
        return fluid != null && candidate != null && fluid == candidate;
    }
}
