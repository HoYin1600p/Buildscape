package net.minecraft.server.jsonrpc.internalapi;

import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.jsonrpc.JsonRpcLogger;
import net.minecraft.server.jsonrpc.methods.ClientInfo;
import net.minecraft.server.jsonrpc.methods.GameRulesService;
import net.minecraft.server.notifications.NotificationManager;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;

public class MinecraftGameRuleServiceImpl implements MinecraftGameRuleService {
   private final NotificationManager notificationManager;
   private final JsonRpcLogger jsonrpcLogger;

   public MinecraftGameRuleServiceImpl(final NotificationManager notificationManager, final JsonRpcLogger jsonrpcLogger) {
      this.notificationManager = notificationManager;
      this.jsonrpcLogger = jsonrpcLogger;
   }

   private DedicatedServer server() {
      return (DedicatedServer)Objects.requireNonNull(this.notificationManager.server());
   }

   public GameRulesService.GameRuleUpdate updateGameRule(final GameRulesService.GameRuleUpdate update, final ClientInfo clientInfo) {
      GameRule gameRule = update.gameRule();
      MinecraftServer server = this.server();
      GameRules gameRules = server.getGameRules();
      Object oldValue = (T)gameRules.get(gameRule);
      Object newValue = (T)update.value();
      gameRules.set(gameRule, newValue, server);
      this.jsonrpcLogger.log(clientInfo, "Game rule '{}' updated from '{}' to '{}'", gameRule.id(), gameRule.serialize(oldValue), gameRule.serialize(newValue));
      return update;
   }

   public GameRulesService.GameRuleUpdate getTypedRule(final GameRule gameRule, final Object value) {
      return new GameRulesService.GameRuleUpdate(gameRule, value);
   }

   public Stream getAvailableGameRules() {
      return this.server().getGameRules().availableRules();
   }

   public Object getRuleValue(final GameRule gameRule) {
      return this.server().getGameRules().get(gameRule);
   }
}
