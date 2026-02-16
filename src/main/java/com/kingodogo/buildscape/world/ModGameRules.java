package com.kingodogo.buildscape.world;

import net.minecraft.world.level.GameRules;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Method;

public class ModGameRules {
    public static GameRules.Key<GameRules.BooleanValue> FAST_LEAF_DECAY;

    public static void register() {
        try {
            // Access the private create method using ObfuscationReflectionHelper which handles SRG names
            Method createMethod = ObfuscationReflectionHelper.findMethod(GameRules.BooleanValue.class, "m_46250_", boolean.class);

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

