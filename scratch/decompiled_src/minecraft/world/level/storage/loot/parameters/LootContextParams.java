package net.minecraft.world.level.storage.loot.parameters;

import net.minecraft.util.context.ContextKey;

public class LootContextParams {
   public static final ContextKey THIS_ENTITY = ContextKey.vanilla("this_entity");
   public static final ContextKey INTERACTING_ENTITY = ContextKey.vanilla("interacting_entity");
   public static final ContextKey TARGET_ENTITY = ContextKey.vanilla("target_entity");
   public static final ContextKey LAST_DAMAGE_PLAYER = ContextKey.vanilla("last_damage_player");
   public static final ContextKey DAMAGE_SOURCE = ContextKey.vanilla("damage_source");
   public static final ContextKey ATTACKING_ENTITY = ContextKey.vanilla("attacking_entity");
   public static final ContextKey DIRECT_ATTACKING_ENTITY = ContextKey.vanilla("direct_attacking_entity");
   public static final ContextKey ORIGIN = ContextKey.vanilla("origin");
   public static final ContextKey BLOCK_STATE = ContextKey.vanilla("block_state");
   public static final ContextKey BLOCK_ENTITY = ContextKey.vanilla("block_entity");
   public static final ContextKey TOOL = ContextKey.vanilla("tool");
   public static final ContextKey EXPLOSION_RADIUS = ContextKey.vanilla("explosion_radius");
   public static final ContextKey ENCHANTMENT_LEVEL = ContextKey.vanilla("enchantment_level");
   public static final ContextKey ENCHANTMENT_ACTIVE = ContextKey.vanilla("enchantment_active");
   public static final ContextKey ADDITIONAL_COST_COMPONENT_ALLOWED = ContextKey.vanilla("additional_cost_component_allowed");
   public static final ContextKey CONTAINER = ContextKey.vanilla("container");
}
