package com.kingodogo.buildscape.world;

import net.minecraft.world.level.GameRules;

public class ModGameRules {
    public static GameRules.Key<GameRules.BooleanValue> FAST_LEAF_DECAY;

    public static void register() {
        // Create the boolean value type with default value false using the now
        // accessible method
        GameRules.Type<GameRules.BooleanValue> booleanType = GameRules.BooleanValue.create(false);

        // Register the gamerule
        FAST_LEAF_DECAY = GameRules.register(
                "fastLeafDecay",
                GameRules.Category.UPDATES,
                booleanType);
    }
}
