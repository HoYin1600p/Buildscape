package com.kingodogo.buildscape.world;

public final class ModGameRules {

    public static volatile boolean clientFastLeafDecay = false;
    public static volatile boolean clientDisableEndermanGriefing = false;
    public static volatile boolean clientDisableCreeperGriefing = false;
    public static volatile boolean clientDisableGhastGriefing = false;
    public static volatile boolean clientCakeStacking = true;
    public static volatile boolean clientWaterBottleStacking = true;

    public static Object FAST_LEAF_DECAY;
    public static Object DISABLE_ENDERMAN_GRIEFING;
    public static Object DISABLE_CREEPER_GRIEFING;
    public static Object DISABLE_GHAST_GRIEFING;
    public static Object IS_CAKE_STACK;
    public static Object IS_WATER_BOTTLE_STACK;

    public static boolean isWaterBottleStackingEnabled() {
        return clientWaterBottleStacking;
    }

    public static boolean isCakeStackingEnabled() {
        return clientCakeStacking;
    }

    public static boolean isFastLeafDecayEnabled() {
        return clientFastLeafDecay;
    }

    private ModGameRules() {}
}
