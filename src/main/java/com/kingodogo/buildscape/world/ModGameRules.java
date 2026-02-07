package com.kingodogo.buildscape.world;

import net.minecraft.world.level.GameRules;

import java.lang.reflect.Method;

public class ModGameRules {
    public static GameRules.Key<GameRules.BooleanValue> FAST_LEAF_DECAY;

    public static void register() {
        try {
            // Access the private create method using reflection
            Method createMethod = GameRules.BooleanValue.class.getDeclaredMethod("create", boolean.class);
            createMethod.setAccessible(true);

            // Create the boolean value type with default value false
            GameRules.Type<GameRules.BooleanValue> booleanType =
                    (GameRules.Type<GameRules.BooleanValue>) createMethod.invoke(null, false);

            // Register the gamerule
            FAST_LEAF_DECAY = GameRules.register(
                    "fastLeafDecay",
                    GameRules.Category.UPDATES,
                    booleanType
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to register fastLeafDecay gamerule", e);
        }
    }
}

