package net.minecraft.world.entity.ai.attributes;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class Attributes {
   public static final double DEFAULT_ATTACK_SPEED = 4.0D;
   public static final Holder AIR_DRAG_MODIFIER = register("air_drag_modifier", (new RangedAttribute("attribute.name.air_drag_modifier", 1.0D, 0.0D, 2048.0D)).setSyncable(true));
   public static final Holder ARMOR = register("armor", (new RangedAttribute("attribute.name.armor", 0.0D, 0.0D, 30.0D)).setSyncable(true));
   public static final Holder ARMOR_TOUGHNESS = register("armor_toughness", (new RangedAttribute("attribute.name.armor_toughness", 0.0D, 0.0D, 20.0D)).setSyncable(true));
   public static final Holder ATTACK_DAMAGE = register("attack_damage", new RangedAttribute("attribute.name.attack_damage", 2.0D, 0.0D, 2048.0D));
   public static final Holder ATTACK_KNOCKBACK = register("attack_knockback", new RangedAttribute("attribute.name.attack_knockback", 0.0D, 0.0D, 5.0D));
   public static final Holder ATTACK_SPEED = register("attack_speed", (new RangedAttribute("attribute.name.attack_speed", 4.0D, 0.0D, 1024.0D)).setSyncable(true));
   public static final Holder BELOW_NAME_DISTANCE = register("below_name_distance", (new RangedAttribute("attribute.name.below_name_distance", 10.0D, 0.0D, 512.0D)).setSyncable(true));
   public static final Holder BLOCK_BREAK_SPEED = register("block_break_speed", (new RangedAttribute("attribute.name.block_break_speed", 1.0D, 0.0D, 1024.0D)).setSyncable(true));
   public static final Holder BLOCK_INTERACTION_RANGE = register("block_interaction_range", (new RangedAttribute("attribute.name.block_interaction_range", 4.5D, 0.0D, 64.0D)).setSyncable(true));
   public static final Holder BOUNCINESS = register("bounciness", (new RangedAttribute("attribute.name.bounciness", 0.0D, 0.0D, 1.0D)).setSyncable(true));
   public static final Holder BURNING_TIME = register("burning_time", (new RangedAttribute("attribute.name.burning_time", 1.0D, 0.0D, 1024.0D)).setSyncable(true).setSentiment(Attribute.Sentiment.NEGATIVE));
   public static final Holder CAMERA_DISTANCE = register("camera_distance", (new RangedAttribute("attribute.name.camera_distance", 4.0D, 0.0D, 32.0D)).setSyncable(true));
   public static final Holder EXPLOSION_KNOCKBACK_RESISTANCE = register("explosion_knockback_resistance", (new RangedAttribute("attribute.name.explosion_knockback_resistance", 0.0D, 0.0D, 1.0D)).setSyncable(true));
   public static final Holder ENTITY_INTERACTION_RANGE = register("entity_interaction_range", (new RangedAttribute("attribute.name.entity_interaction_range", 3.0D, 0.0D, 64.0D)).setSyncable(true));
   public static final Holder FALL_DAMAGE_MULTIPLIER = register("fall_damage_multiplier", (new RangedAttribute("attribute.name.fall_damage_multiplier", 1.0D, 0.0D, 100.0D)).setSyncable(true).setSentiment(Attribute.Sentiment.NEGATIVE));
   public static final Holder FLYING_SPEED = register("flying_speed", (new RangedAttribute("attribute.name.flying_speed", 0.4D, 0.0D, 1024.0D)).setSyncable(true));
   public static final Holder FOLLOW_RANGE = register("follow_range", new RangedAttribute("attribute.name.follow_range", 32.0D, 0.0D, 2048.0D));
   public static final Holder FRICTION_MODIFIER = register("friction_modifier", (new RangedAttribute("attribute.name.friction_modifier", 1.0D, 0.0D, 2048.0D)).setSyncable(true));
   public static final Holder GRAVITY = register("gravity", (new RangedAttribute("attribute.name.gravity", 0.08D, -1.0D, 1.0D)).setSyncable(true).setSentiment(Attribute.Sentiment.NEUTRAL));
   public static final Holder JUMP_STRENGTH = register("jump_strength", (new RangedAttribute("attribute.name.jump_strength", (double)0.42F, 0.0D, 32.0D)).setSyncable(true));
   public static final Holder KNOCKBACK_RESISTANCE = register("knockback_resistance", new RangedAttribute("attribute.name.knockback_resistance", 0.0D, -2.0D, 1.0D));
   public static final Holder LUCK = register("luck", (new RangedAttribute("attribute.name.luck", 0.0D, -1024.0D, 1024.0D)).setSyncable(true));
   public static final Holder MAX_ABSORPTION = register("max_absorption", (new RangedAttribute("attribute.name.max_absorption", 0.0D, 0.0D, 2048.0D)).setSyncable(true));
   public static final Holder MAX_HEALTH = register("max_health", (new RangedAttribute("attribute.name.max_health", 20.0D, 1.0D, 1024.0D)).setSyncable(true));
   public static final Holder MINING_EFFICIENCY = register("mining_efficiency", (new RangedAttribute("attribute.name.mining_efficiency", 0.0D, 0.0D, 1024.0D)).setSyncable(true));
   public static final Holder MOVEMENT_EFFICIENCY = register("movement_efficiency", (new RangedAttribute("attribute.name.movement_efficiency", 0.0D, 0.0D, 1.0D)).setSyncable(true));
   public static final Holder MOVEMENT_SPEED = register("movement_speed", (new RangedAttribute("attribute.name.movement_speed", 0.7D, 0.0D, 1024.0D)).setSyncable(true));
   public static final Holder NAME_TAG_DISTANCE = register("name_tag_distance", (new RangedAttribute("attribute.name.name_tag_distance", 64.0D, 0.0D, 512.0D)).setSyncable(true));
   public static final Holder OXYGEN_BONUS = register("oxygen_bonus", (new RangedAttribute("attribute.name.oxygen_bonus", 0.0D, 0.0D, 1024.0D)).setSyncable(true));
   public static final Holder SAFE_FALL_DISTANCE = register("safe_fall_distance", (new RangedAttribute("attribute.name.safe_fall_distance", 3.0D, -1024.0D, 1024.0D)).setSyncable(true));
   public static final Holder SCALE = register("scale", (new RangedAttribute("attribute.name.scale", 1.0D, 0.0625D, 16.0D)).setSyncable(true).setSentiment(Attribute.Sentiment.NEUTRAL));
   public static final Holder SNEAKING_SPEED = register("sneaking_speed", (new RangedAttribute("attribute.name.sneaking_speed", 0.3D, 0.0D, 1.0D)).setSyncable(true));
   public static final Holder SPAWN_REINFORCEMENTS_CHANCE = register("spawn_reinforcements", new RangedAttribute("attribute.name.spawn_reinforcements", 0.0D, 0.0D, 1.0D));
   public static final Holder STEP_HEIGHT = register("step_height", (new RangedAttribute("attribute.name.step_height", 0.6D, 0.0D, 10.0D)).setSyncable(true));
   public static final Holder SUBMERGED_MINING_SPEED = register("submerged_mining_speed", (new RangedAttribute("attribute.name.submerged_mining_speed", 0.2D, 0.0D, 20.0D)).setSyncable(true));
   public static final Holder SWEEPING_DAMAGE_RATIO = register("sweeping_damage_ratio", (new RangedAttribute("attribute.name.sweeping_damage_ratio", 0.0D, 0.0D, 1.0D)).setSyncable(true));
   public static final Holder TEMPT_RANGE = register("tempt_range", new RangedAttribute("attribute.name.tempt_range", 10.0D, 0.0D, 2048.0D));
   public static final Holder WATER_MOVEMENT_EFFICIENCY = register("water_movement_efficiency", (new RangedAttribute("attribute.name.water_movement_efficiency", 0.0D, 0.0D, 1.0D)).setSyncable(true));
   public static final Holder WAYPOINT_TRANSMIT_RANGE = register("waypoint_transmit_range", (new RangedAttribute("attribute.name.waypoint_transmit_range", 0.0D, 0.0D, 6.0E7D)).setSentiment(Attribute.Sentiment.NEUTRAL));
   public static final Holder WAYPOINT_RECEIVE_RANGE = register("waypoint_receive_range", (new RangedAttribute("attribute.name.waypoint_receive_range", 0.0D, 0.0D, 6.0E7D)).setSentiment(Attribute.Sentiment.NEUTRAL));

   private static Holder register(final String name, final Attribute attribute) {
      return Registry.registerForHolder(BuiltInRegistries.ATTRIBUTE, Identifier.withDefaultNamespace(name), attribute);
   }

   public static Holder bootstrap(final Registry registry) {
      return MAX_HEALTH;
   }
}
