package com.kingodogo.buildscape;

public final class BuildScape {
    public static final String MODID = BuildscapeCommon.MOD_ID;
    public static final String MOD_ID = BuildscapeCommon.MOD_ID;

    private BuildScape() {}

    public static BuildscapeCommon.ModLogger getLogger() {
        return BuildscapeCommon.LOGGER;
    }
}
