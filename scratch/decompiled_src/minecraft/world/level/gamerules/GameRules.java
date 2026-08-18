package net.minecraft.world.level.gamerules;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Objects;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import net.minecraft.SharedConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class GameRules {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final GameRule ADVANCE_TIME = registerBoolean("advance_time", GameRuleCategory.UPDATES, !SharedConstants.DEBUG_WORLD_RECREATE);
   public static final GameRule ADVANCE_WEATHER = registerBoolean("advance_weather", GameRuleCategory.UPDATES, !SharedConstants.DEBUG_WORLD_RECREATE);
   public static final GameRule ALLOW_ENTERING_NETHER_USING_PORTALS = registerBoolean("allow_entering_nether_using_portals", GameRuleCategory.MISC, true);
   public static final GameRule BLOCK_DROPS = registerBoolean("block_drops", GameRuleCategory.DROPS, true);
   public static final GameRule BLOCK_EXPLOSION_DROP_DECAY = registerBoolean("block_explosion_drop_decay", GameRuleCategory.DROPS, true);
   public static final GameRule COMMAND_BLOCKS_WORK = registerBoolean("command_blocks_work", GameRuleCategory.MISC, true);
   public static final GameRule COMMAND_BLOCK_OUTPUT = registerBoolean("command_block_output", GameRuleCategory.CHAT, true);
   public static final GameRule DROWNING_DAMAGE = registerBoolean("drowning_damage", GameRuleCategory.PLAYER, true);
   public static final GameRule ELYTRA_MOVEMENT_CHECK = registerBoolean("elytra_movement_check", GameRuleCategory.PLAYER, true);
   public static final GameRule ENDER_PEARLS_VANISH_ON_DEATH = registerBoolean("ender_pearls_vanish_on_death", GameRuleCategory.PLAYER, true);
   public static final GameRule ENTITY_DROPS = registerBoolean("entity_drops", GameRuleCategory.DROPS, true);
   public static final GameRule FALL_DAMAGE = registerBoolean("fall_damage", GameRuleCategory.PLAYER, true);
   public static final GameRule FIRE_DAMAGE = registerBoolean("fire_damage", GameRuleCategory.PLAYER, true);
   public static final GameRule FIRE_SPREAD_RADIUS_AROUND_PLAYER = registerInteger("fire_spread_radius_around_player", GameRuleCategory.UPDATES, 128, -1);
   public static final GameRule FORGIVE_DEAD_PLAYERS = registerBoolean("forgive_dead_players", GameRuleCategory.MOBS, true);
   public static final GameRule FREEZE_DAMAGE = registerBoolean("freeze_damage", GameRuleCategory.PLAYER, true);
   public static final GameRule GLOBAL_SOUND_EVENTS = registerBoolean("global_sound_events", GameRuleCategory.MISC, true);
   public static final GameRule IMMEDIATE_RESPAWN = registerBoolean("immediate_respawn", GameRuleCategory.PLAYER, false);
   public static final GameRule KEEP_INVENTORY = registerBoolean("keep_inventory", GameRuleCategory.PLAYER, false);
   public static final GameRule LAVA_SOURCE_CONVERSION = registerBoolean("lava_source_conversion", GameRuleCategory.UPDATES, false);
   public static final GameRule LIMITED_CRAFTING = registerBoolean("limited_crafting", GameRuleCategory.PLAYER, false);
   public static final GameRule LOCATOR_BAR = registerBoolean("locator_bar", GameRuleCategory.PLAYER, true);
   public static final GameRule LOG_ADMIN_COMMANDS = registerBoolean("log_admin_commands", GameRuleCategory.CHAT, true);
   public static final GameRule MAX_BLOCK_MODIFICATIONS = registerInteger("max_block_modifications", GameRuleCategory.MISC, 32768, 1);
   public static final GameRule MAX_COMMAND_FORKS = registerInteger("max_command_forks", GameRuleCategory.MISC, 65536, 0);
   public static final GameRule MAX_COMMAND_SEQUENCE_LENGTH = registerInteger("max_command_sequence_length", GameRuleCategory.MISC, 65536, 0);
   public static final GameRule MAX_ENTITY_CRAMMING = registerInteger("max_entity_cramming", GameRuleCategory.MOBS, 24, 0);
   public static final GameRule MAX_MINECART_SPEED = registerInteger("max_minecart_speed", GameRuleCategory.MISC, 8, 1, 1000, FeatureFlagSet.of(FeatureFlags.MINECART_IMPROVEMENTS));
   public static final GameRule MAX_SNOW_ACCUMULATION_HEIGHT = registerInteger("max_snow_accumulation_height", GameRuleCategory.UPDATES, 1, 0, 8);
   public static final GameRule MOB_DROPS = registerBoolean("mob_drops", GameRuleCategory.DROPS, true);
   public static final GameRule MOB_EXPLOSION_DROP_DECAY = registerBoolean("mob_explosion_drop_decay", GameRuleCategory.DROPS, true);
   public static final GameRule MOB_GRIEFING = registerBoolean("mob_griefing", GameRuleCategory.MOBS, true);
   public static final GameRule NATURAL_HEALTH_REGENERATION = registerBoolean("natural_health_regeneration", GameRuleCategory.PLAYER, true);
   public static final GameRule PLAYER_MOVEMENT_CHECK = registerBoolean("player_movement_check", GameRuleCategory.PLAYER, true);
   public static final GameRule PLAYERS_NETHER_PORTAL_CREATIVE_DELAY = registerInteger("players_nether_portal_creative_delay", GameRuleCategory.PLAYER, 0, 0);
   public static final GameRule PLAYERS_NETHER_PORTAL_DEFAULT_DELAY = registerInteger("players_nether_portal_default_delay", GameRuleCategory.PLAYER, 80, 0);
   public static final GameRule PLAYERS_SLEEPING_PERCENTAGE = registerInteger("players_sleeping_percentage", GameRuleCategory.PLAYER, 100, 0);
   public static final GameRule PROJECTILES_CAN_BREAK_BLOCKS = registerBoolean("projectiles_can_break_blocks", GameRuleCategory.DROPS, true);
   public static final GameRule PVP = registerBoolean("pvp", GameRuleCategory.PLAYER, true);
   public static final GameRule RAIDS = registerBoolean("raids", GameRuleCategory.MOBS, true);
   public static final GameRule RANDOM_TICK_SPEED = registerInteger("random_tick_speed", GameRuleCategory.UPDATES, 3, 0);
   public static final GameRule REDUCED_DEBUG_INFO = registerBoolean("reduced_debug_info", GameRuleCategory.MISC, false);
   public static final GameRule RESPAWN_RADIUS = registerInteger("respawn_radius", GameRuleCategory.PLAYER, 10, 0);
   public static final GameRule SEND_COMMAND_FEEDBACK = registerBoolean("send_command_feedback", GameRuleCategory.CHAT, true);
   public static final GameRule SHOW_ADVANCEMENT_MESSAGES = registerBoolean("show_advancement_messages", GameRuleCategory.CHAT, true);
   public static final GameRule SHOW_DEATH_MESSAGES = registerBoolean("show_death_messages", GameRuleCategory.CHAT, true);
   public static final GameRule SPAWNER_BLOCKS_WORK = registerBoolean("spawner_blocks_work", GameRuleCategory.MISC, true);
   public static final GameRule SPAWN_MOBS = registerBoolean("spawn_mobs", GameRuleCategory.SPAWNING, true);
   public static final GameRule SPAWN_MONSTERS = registerBoolean("spawn_monsters", GameRuleCategory.SPAWNING, true);
   public static final GameRule SPAWN_PATROLS = registerBoolean("spawn_patrols", GameRuleCategory.SPAWNING, true);
   public static final GameRule SPAWN_PHANTOMS = registerBoolean("spawn_phantoms", GameRuleCategory.SPAWNING, true);
   public static final GameRule SPAWN_WANDERING_TRADERS = registerBoolean("spawn_wandering_traders", GameRuleCategory.SPAWNING, true);
   public static final GameRule SPAWN_WARDENS = registerBoolean("spawn_wardens", GameRuleCategory.SPAWNING, true);
   public static final GameRule SPECTATORS_GENERATE_CHUNKS = registerBoolean("spectators_generate_chunks", GameRuleCategory.PLAYER, true);
   public static final GameRule SPREAD_VINES = registerBoolean("spread_vines", GameRuleCategory.UPDATES, true);
   public static final GameRule TNT_EXPLODES = registerBoolean("tnt_explodes", GameRuleCategory.MISC, true);
   public static final GameRule TNT_EXPLOSION_DROP_DECAY = registerBoolean("tnt_explosion_drop_decay", GameRuleCategory.DROPS, false);
   public static final GameRule UNIVERSAL_ANGER = registerBoolean("universal_anger", GameRuleCategory.MOBS, false);
   public static final GameRule WATER_SOURCE_CONVERSION = registerBoolean("water_source_conversion", GameRuleCategory.UPDATES, true);
   private final GameRuleMap rules;

   public static Codec codec(final FeatureFlagSet enabledFeatures) {
      return GameRuleMap.CODEC.xmap((map) -> new GameRules(enabledFeatures, map), (gameRules) -> gameRules.rules);
   }

   public GameRules(final FeatureFlagSet enabledFeatures, final GameRuleMap map) {
      BuiltInRegistries.GAME_RULE.stream().forEach((gameRule) -> {
         if (gameRule.isEnabled(enabledFeatures)) {
            if (!map.has(gameRule)) {
               map.reset(gameRule);
            }
         } else if (map.has(gameRule)) {
            map.remove(gameRule);
         }

      });
      this.rules = map;
   }

   public GameRules(final FeatureFlagSet enabledFeatures) {
      this.rules = GameRuleMap.of(BuiltInRegistries.GAME_RULE.filterFeatures(enabledFeatures).listElements().map(Holder::value));
   }

   public GameRules(final List rules) {
      this.rules = GameRuleMap.of(rules.stream());
   }

   public Stream availableRules() {
      return this.rules.keySet().stream();
   }

   public Object get(final GameRule gameRule) {
      Object value = (T)this.rules.get(gameRule);
      if (value == null) {
         throw new IllegalArgumentException("Tried to access invalid game rule " + String.valueOf(gameRule.getIdentifierWithFallback()));
      } else {
         return value;
      }
   }

   public void set(final GameRule gameRule, final Object value, final @Nullable MinecraftServer server) {
      if (!this.rules.has(gameRule)) {
         LOGGER.warn("Tried to set invalid game rule '{}' to value '{}'", gameRule.getIdentifierWithFallback(), value);
      } else {
         this.rules.set(gameRule, value);
         if (server != null) {
            server.onGameRuleChanged(gameRule, value);
         }

      }
   }

   public GameRules copy(final FeatureFlagSet enabledFeatures) {
      return new GameRules(enabledFeatures, GameRuleMap.copyOf(this.rules));
   }

   public void setAll(final GameRules other, final @Nullable MinecraftServer server) {
      this.setAll(other.rules, server);
   }

   public void setAll(final GameRuleMap gameRulesMap, final @Nullable MinecraftServer server) {
      gameRulesMap.keySet().forEach((gameRule) -> this.setFromOther(gameRulesMap, gameRule, server));
   }

   private void setFromOther(final GameRuleMap gameRulesMap, final GameRule gameRule, final @Nullable MinecraftServer server) {
      this.set(gameRule, Objects.requireNonNull(gameRulesMap.get(gameRule)), server);
   }

   public void visitGameRuleTypes(final GameRuleTypeVisitor visitor) {
      this.rules.keySet().forEach((gameRule) -> {
         visitor.visit(gameRule);
         gameRule.callVisitor(visitor);
      });
   }

   private static GameRule registerBoolean(final String id, final GameRuleCategory category, final boolean defaultValue) {
      return register(id, category, GameRuleType.BOOL, BoolArgumentType.bool(), Codec.BOOL, defaultValue, FeatureFlagSet.of(), GameRuleTypeVisitor::visitBoolean, (b) -> b ? 1 : 0);
   }

   private static GameRule registerInteger(final String id, final GameRuleCategory category, final int defaultValue, final int min) {
      return registerInteger(id, category, defaultValue, min, Integer.MAX_VALUE, FeatureFlagSet.of());
   }

   private static GameRule registerInteger(final String id, final GameRuleCategory category, final int defaultValue, final int min, final int max) {
      return registerInteger(id, category, defaultValue, min, max, FeatureFlagSet.of());
   }

   private static GameRule registerInteger(final String id, final GameRuleCategory category, final int defaultValue, final int min, final int max, final FeatureFlagSet requiredFeatures) {
      return register(id, category, GameRuleType.INT, IntegerArgumentType.integer(min, max), Codec.intRange(min, max), defaultValue, requiredFeatures, GameRuleTypeVisitor::visitInteger, (i) -> i);
   }

   private static GameRule register(final String id, final GameRuleCategory category, final GameRuleType typeHint, final ArgumentType argumentType, final Codec codec, final Object defaultValue, final FeatureFlagSet requiredFeatures, final GameRules.VisitorCaller visitorCaller, final ToIntFunction commandResultFunction) {
      return (GameRule)Registry.register(BuiltInRegistries.GAME_RULE, id, new GameRule(category, typeHint, argumentType, visitorCaller, codec, commandResultFunction, defaultValue, requiredFeatures));
   }

   public static GameRule bootstrap(final Registry registry) {
      return ADVANCE_TIME;
   }

   public String getAsString(final GameRule gameRule) {
      return gameRule.serialize(this.get(gameRule));
   }

   public interface VisitorCaller {
      void call(GameRuleTypeVisitor visitor, GameRule key);
   }
}
