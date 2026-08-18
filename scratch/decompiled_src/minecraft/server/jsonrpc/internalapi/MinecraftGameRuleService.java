package net.minecraft.server.jsonrpc.internalapi;

import java.util.stream.Stream;
import net.minecraft.server.jsonrpc.methods.ClientInfo;
import net.minecraft.server.jsonrpc.methods.GameRulesService;
import net.minecraft.world.level.gamerules.GameRule;

public interface MinecraftGameRuleService {
   GameRulesService.GameRuleUpdate updateGameRule(GameRulesService.GameRuleUpdate update, ClientInfo clientInfo);

   Object getRuleValue(GameRule gameRule);

   GameRulesService.GameRuleUpdate getTypedRule(GameRule gameRule, Object value);

   Stream getAvailableGameRules();
}
