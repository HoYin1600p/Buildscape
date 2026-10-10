package com.kingodogo.buildscape.world;

import net.minecraft.world.level.Level;

/** VersionCluster operations needed by the loader-neutral rule definitions. */
public interface GameRuleAccess {
    Object registerBooleanRule(ModGameRules.Definition definition);

    /** Returns the running server's overworld only on its own thread. */
    Level currentServerRuleLevel();
}
