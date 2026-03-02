package com.kingodogo.buildscape.world;

import com.kingodogo.buildscape.network.ModMessages;
import com.kingodogo.buildscape.network.SyncGameRulesPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.network.PacketDistributor;

import java.lang.reflect.Method;
import java.util.function.BiConsumer;

public class ModGameRules {
    public static GameRules.Key<GameRules.BooleanValue> FAST_LEAF_DECAY;
    public static GameRules.Key<GameRules.BooleanValue> CREATIVE_TREE_BREAKER;

    public static void register() {
        try {
            // Callback that triggers when either gamerule is changed on the server
            BiConsumer<MinecraftServer, GameRules.BooleanValue> onRuleChange = (server, value) -> {
                if (server != null) {
                    ModMessages.INSTANCE.send(PacketDistributor.ALL.noArg(),
                            new SyncGameRulesPacket(
                                    server.getGameRules().getBoolean(FAST_LEAF_DECAY),
                                    server.getGameRules().getBoolean(CREATIVE_TREE_BREAKER)
                            )
                    );
                }
            };

            // Access the private create method with callback: create(boolean, BiConsumer)
            // m_46252_ is the SRG name for the overload taking a BiConsumer
            Method createMethod = ObfuscationReflectionHelper.findMethod(GameRules.BooleanValue.class, "m_46252_", boolean.class, BiConsumer.class);

            // Create the boolean value type with default value false and the sync callback
            GameRules.Type<GameRules.BooleanValue> booleanType =
                    (GameRules.Type<GameRules.BooleanValue>) createMethod.invoke(null, false, onRuleChange);

            // Register the gamerules with MISC category for visibility
            FAST_LEAF_DECAY = GameRules.register(
                    "fastLeafDecay",
                    GameRules.Category.MISC,
                    booleanType
            );

            CREATIVE_TREE_BREAKER = GameRules.register(
                    "creativeTreeBreaker",
                    GameRules.Category.MISC,
                    booleanType
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to register gamerules", e);
        }
    }
}

