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
    public static GameRules.Key<GameRules.BooleanValue> DISABLE_ENDERMAN_GRIEFING;
    public static GameRules.Key<GameRules.BooleanValue> DISABLE_CREEPER_GRIEFING;
    public static GameRules.Key<GameRules.BooleanValue> DISABLE_GHAST_GRIEFING;

    public static void register() {
        try {
            BiConsumer<MinecraftServer, GameRules.BooleanValue> onRuleChange = (server, value) -> {
                if (server != null) {
                    ModMessages.INSTANCE.send(PacketDistributor.ALL.noArg(),
                            new SyncGameRulesPacket(
                                    server.getGameRules().getBoolean(FAST_LEAF_DECAY),
                                    server.getGameRules().getBoolean(DISABLE_ENDERMAN_GRIEFING),
                                    server.getGameRules().getBoolean(DISABLE_CREEPER_GRIEFING),
                                    server.getGameRules().getBoolean(DISABLE_GHAST_GRIEFING)
                            )
                    );
                }
            };

            Method createMethod = ObfuscationReflectionHelper.findMethod(GameRules.BooleanValue.class, "m_46252_", boolean.class, BiConsumer.class);

            GameRules.Type<GameRules.BooleanValue> booleanType =
                    (GameRules.Type<GameRules.BooleanValue>) createMethod.invoke(null, false, onRuleChange);

            FAST_LEAF_DECAY = GameRules.register(
                    "fastLeafDecay",
                    GameRules.Category.MISC,
                    booleanType
            );

            DISABLE_ENDERMAN_GRIEFING = GameRules.register(
                    "disableEndermanGriefing",
                    GameRules.Category.MISC,
                    booleanType
            );

            DISABLE_CREEPER_GRIEFING = GameRules.register(
                    "disableCreeperGriefing",
                    GameRules.Category.MISC,
                    booleanType
            );

            DISABLE_GHAST_GRIEFING = GameRules.register(
                    "disableGhastGriefing",
                    GameRules.Category.MISC,
                    booleanType
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to register gamerules", e);
        }
    }
}
