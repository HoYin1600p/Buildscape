package com.kingodogo.buildscape.world;

import com.kingodogo.buildscape.config.BuildscapeClientConfig;
import com.kingodogo.buildscape.network.PacketFactory;
import com.kingodogo.buildscape.network.SyncGameRulesPacket;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.registry.StartupOnce;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public final class ModGameRules {

    public record Definition(String name, String registryPath, String category, boolean defaultValue) {}

    /** Names remain compatible with the reference packets; registry paths must be lowercase on 26.2. */
    public static List<Definition> definitions(boolean cakeStacking, boolean waterBottleStacking) {
        return List.of(
                new Definition("fastLeafDecay", "fast_leaf_decay", "MISC", false),
                new Definition("disableEndermanGriefing", "disable_enderman_griefing", "MISC", false),
                new Definition("disableCreeperGriefing", "disable_creeper_griefing", "MISC", false),
                new Definition("disableGhastGriefing", "disable_ghast_griefing", "MISC", false),
                new Definition("isCakeStack", "is_cake_stack", "MISC", cakeStacking),
                new Definition("isWaterbottleStack", "is_waterbottle_stack", "MISC", waterBottleStacking));
    }

    private static final StartupOnce REGISTRATION = new StartupOnce();
    private static final Map<Level, List<Boolean>> LAST_SENT = new WeakHashMap<>();

    public static void register() {
        REGISTRATION.run(() -> {
            try {
                GameRuleAccess access = (GameRuleAccess) Services.PLATFORM;
                BuildscapeClientConfig config = BuildscapeClientConfig.get();
                List<Definition> definitions = definitions(config.isCakeStackingEnabled(), config.isWaterBottleStackingEnabled());
                FAST_LEAF_DECAY = access.registerBooleanRule(definitions.get(0));
                DISABLE_ENDERMAN_GRIEFING = access.registerBooleanRule(definitions.get(1));
                DISABLE_CREEPER_GRIEFING = access.registerBooleanRule(definitions.get(2));
                DISABLE_GHAST_GRIEFING = access.registerBooleanRule(definitions.get(3));
                IS_CAKE_STACK = access.registerBooleanRule(definitions.get(4));
                IS_WATER_BOTTLE_STACK = access.registerBooleanRule(definitions.get(5));
                clientCakeStacking = definitions.get(4).defaultValue();
                clientWaterBottleStacking = definitions.get(5).defaultValue();
            } catch (RuntimeException exception) {
                com.kingodogo.buildscape.BuildscapeCommon.LOGGER.error("Failed to register Buildscape game rules", exception);
                throw exception;
            }
        });
    }

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
        return currentValue(IS_WATER_BOTTLE_STACK, clientWaterBottleStacking);
    }

    public static boolean isCakeStackingEnabled() {
        return currentValue(IS_CAKE_STACK, clientCakeStacking);
    }

    public static boolean isFastLeafDecayEnabled() {
        return currentValue(FAST_LEAF_DECAY, clientFastLeafDecay);
    }

    public static boolean isEndermanGriefingDisabled() {
        return currentValue(DISABLE_ENDERMAN_GRIEFING, clientDisableEndermanGriefing);
    }

    public static boolean isCreeperGriefingDisabled() {
        return currentValue(DISABLE_CREEPER_GRIEFING, clientDisableCreeperGriefing);
    }

    public static boolean isGhastGriefingDisabled() {
        return currentValue(DISABLE_GHAST_GRIEFING, clientDisableGhastGriefing);
    }

    private static boolean currentValue(Object rule, boolean fallback) {
        Level level = ((GameRuleAccess) Services.PLATFORM).currentServerRuleLevel();
        return level == null ? fallback : Services.PLATFORM.getGameRuleBoolean(level, rule, fallback);
    }

    public static Object ruleByName(String name) {
        if (name == null) return null;
        return switch (name) {
            case "fastLeafDecay", "buildscape:fast_leaf_decay" -> FAST_LEAF_DECAY;
            case "disableEndermanGriefing", "buildscape:disable_enderman_griefing" -> DISABLE_ENDERMAN_GRIEFING;
            case "disableCreeperGriefing", "buildscape:disable_creeper_griefing" -> DISABLE_CREEPER_GRIEFING;
            case "disableGhastGriefing", "buildscape:disable_ghast_griefing" -> DISABLE_GHAST_GRIEFING;
            case "isCakeStack", "buildscape:is_cake_stack" -> IS_CAKE_STACK;
            case "isWaterbottleStack", "buildscape:is_waterbottle_stack" -> IS_WATER_BOTTLE_STACK;
            default -> null;
        };
    }

    public static SyncGameRulesPacket snapshot(Level level) {
        return new SyncGameRulesPacket(
                Services.PLATFORM.getGameRuleBoolean(level, FAST_LEAF_DECAY, false),
                Services.PLATFORM.getGameRuleBoolean(level, DISABLE_ENDERMAN_GRIEFING, false),
                Services.PLATFORM.getGameRuleBoolean(level, DISABLE_CREEPER_GRIEFING, false),
                Services.PLATFORM.getGameRuleBoolean(level, DISABLE_GHAST_GRIEFING, false),
                Services.PLATFORM.getGameRuleBoolean(level, IS_CAKE_STACK, clientCakeStacking),
                Services.PLATFORM.getGameRuleBoolean(level, IS_WATER_BOTTLE_STACK, clientWaterBottleStacking));
    }

    /** Also corrects legacy join callers that construct a packet from client caches. */
    public static SyncGameRulesPacket serverSnapshotOr(SyncGameRulesPacket fallback) {
        Level level = ((GameRuleAccess) Services.PLATFORM).currentServerRuleLevel();
        return level == null ? fallback : snapshot(level);
    }

    /** Call on the server thread with its overworld to include vanilla /gamerule changes. */
    public static void syncIfChanged(Level level) {
        if (level == null) return;
        SyncGameRulesPacket packet = snapshot(level);
        List<Boolean> values = List.of(packet.fastLeafDecay, packet.disableEndermanGriefing,
                packet.disableCreeperGriefing, packet.disableGhastGriefing, packet.cakeStacking, packet.waterBottleStacking);
        if (!values.equals(LAST_SENT.put(level, values))) PacketFactory.sendToAll(packet);
    }

    private ModGameRules() {}
}
