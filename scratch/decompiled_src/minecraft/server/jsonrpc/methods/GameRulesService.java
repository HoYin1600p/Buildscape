package net.minecraft.server.jsonrpc.methods;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.jsonrpc.internalapi.MinecraftApi;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleType;

public class GameRulesService {
   public static List get(final MinecraftApi minecraftApi) {
      List rules = new ArrayList();
      minecraftApi.gameRuleService().getAvailableGameRules().forEach((gameRule) -> addGameRule(minecraftApi, gameRule, rules));
      return rules;
   }

   private static void addGameRule(final MinecraftApi minecraftApi, final GameRule gameRule, final List rules) {
      Object value = (T)minecraftApi.gameRuleService().getRuleValue(gameRule);
      rules.add(getTypedRule(minecraftApi, gameRule, value));
   }

   public static GameRulesService.GameRuleUpdate getTypedRule(final MinecraftApi minecraftApi, final GameRule gameRule, final Object value) {
      return minecraftApi.gameRuleService().getTypedRule(gameRule, value);
   }

   public static GameRulesService.GameRuleUpdate update(final MinecraftApi minecraftApi, final GameRulesService.GameRuleUpdate update, final ClientInfo clientInfo) {
      return minecraftApi.gameRuleService().updateGameRule(update, clientInfo);
   }

   public static record GameRuleUpdate(GameRule gameRule, Object value) {
      public static final Codec TYPED_CODEC = BuiltInRegistries.GAME_RULE.byNameCodec().dispatch("key", GameRulesService.GameRuleUpdate::gameRule, GameRulesService.GameRuleUpdate::getValueAndTypeCodec);
      public static final Codec CODEC = BuiltInRegistries.GAME_RULE.byNameCodec().dispatch("key", GameRulesService.GameRuleUpdate::gameRule, GameRulesService.GameRuleUpdate::getValueCodec);

      private static MapCodec getValueCodec(final GameRule gameRule) {
         return gameRule.valueCodec().fieldOf("value").xmap((value) -> new GameRulesService.GameRuleUpdate(gameRule, value), GameRulesService.GameRuleUpdate::value);
      }

      private static MapCodec getValueAndTypeCodec(final GameRule gameRule) {
         return RecordCodecBuilder.mapCodec((i) -> i.group(StringRepresentable.fromEnum(GameRuleType::values).fieldOf("type").forGetter((r) -> r.gameRule.gameRuleType()), gameRule.valueCodec().fieldOf("value").forGetter(GameRulesService.GameRuleUpdate::value)).apply(i, (type, value) -> getUntypedRule(gameRule, type, value)));
      }

      private static GameRulesService.GameRuleUpdate getUntypedRule(final GameRule gameRule, final GameRuleType readType, final Object value) {
         if (gameRule.gameRuleType() != readType) {
            throw new InvalidParameterJsonRpcException("Stated type \"" + String.valueOf(readType) + "\" mismatches with actual type \"" + String.valueOf(gameRule.gameRuleType()) + "\" of gamerule \"" + gameRule.id() + "\"");
         } else {
            return new GameRulesService.GameRuleUpdate(gameRule, value);
         }
      }
   }
}
