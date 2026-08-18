package net.minecraft.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;

public interface EnchantmentTags {
   TagKey TOOLTIP_ORDER = create("tooltip_order");
   TagKey ARMOR_EXCLUSIVE = create("exclusive_set/armor");
   TagKey BOOTS_EXCLUSIVE = create("exclusive_set/boots");
   TagKey BOW_EXCLUSIVE = create("exclusive_set/bow");
   TagKey CROSSBOW_EXCLUSIVE = create("exclusive_set/crossbow");
   TagKey DAMAGE_EXCLUSIVE = create("exclusive_set/damage");
   TagKey MINING_EXCLUSIVE = create("exclusive_set/mining");
   TagKey RIPTIDE_EXCLUSIVE = create("exclusive_set/riptide");
   TagKey TRADEABLE = create("tradeable");
   TagKey DOUBLE_TRADE_PRICE = create("double_trade_price");
   TagKey IN_ENCHANTING_TABLE = create("in_enchanting_table");
   TagKey ON_MOB_SPAWN_EQUIPMENT = create("on_mob_spawn_equipment");
   TagKey ON_TRADED_EQUIPMENT = create("on_traded_equipment");
   TagKey ON_RANDOM_LOOT = create("on_random_loot");
   TagKey CURSE = create("curse");
   TagKey SMELTS_LOOT = create("smelts_loot");
   TagKey PREVENTS_BEE_SPAWNS_WHEN_MINING = create("prevents_bee_spawns_when_mining");
   TagKey PREVENTS_DECORATED_POT_SHATTERING = create("prevents_decorated_pot_shattering");
   TagKey PREVENTS_ICE_MELTING = create("prevents_ice_melting");
   TagKey PREVENTS_INFESTED_SPAWNS = create("prevents_infested_spawns");
   TagKey TREASURE = create("treasure");
   TagKey NON_TREASURE = create("non_treasure");
   TagKey TRADES_DESERT_COMMON = create("trades/desert_common");
   TagKey TRADES_JUNGLE_COMMON = create("trades/jungle_common");
   TagKey TRADES_PLAINS_COMMON = create("trades/plains_common");
   TagKey TRADES_SAVANNA_COMMON = create("trades/savanna_common");
   TagKey TRADES_SNOW_COMMON = create("trades/snow_common");
   TagKey TRADES_SWAMP_COMMON = create("trades/swamp_common");
   TagKey TRADES_TAIGA_COMMON = create("trades/taiga_common");

   private static TagKey create(final String name) {
      return TagKey.create(Registries.ENCHANTMENT, Identifier.withDefaultNamespace(name));
   }
}
