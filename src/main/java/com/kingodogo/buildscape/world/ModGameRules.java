package com.kingodogo.buildscape.world;

import net.minecraft.world.level.GameRules;
import java.lang.reflect.Method;

public class ModGameRules {
    public static GameRules.Key<GameRules.BooleanValue> FAST_LEAF_DECAY;

    public static void register() {
        try {
            Method createMethod = GameRules.BooleanValue.class.getDeclaredMethod("create", boolean.class);
            createMethod.setAccessible(true);

            GameRules.Type<GameRules.BooleanValue> booleanType =
                    (GameRules.Type<GameRules.BooleanValue>) createMethod.invoke(null, false);

            FAST_LEAF_DECAY = GameRules.register(
                    "fastLeafDelay",
                    GameRules.Category.UPDATES,
                    booleanType
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to register fastLeafDelay gamerule", e);
        }
    }
}

