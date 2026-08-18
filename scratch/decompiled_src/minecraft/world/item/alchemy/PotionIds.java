package net.minecraft.world.item.alchemy;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public class PotionIds {
   public static final ResourceKey WATER = register("water");
   public static final ResourceKey MUNDANE = register("mundane");
   public static final ResourceKey THICK = register("thick");
   public static final ResourceKey AWKWARD = register("awkward");
   public static final ResourceKey NIGHT_VISION = register("night_vision");
   public static final ResourceKey LONG_NIGHT_VISION = register("long_night_vision");
   public static final ResourceKey INVISIBILITY = register("invisibility");
   public static final ResourceKey LONG_INVISIBILITY = register("long_invisibility");
   public static final ResourceKey LEAPING = register("leaping");
   public static final ResourceKey LONG_LEAPING = register("long_leaping");
   public static final ResourceKey STRONG_LEAPING = register("strong_leaping");
   public static final ResourceKey FIRE_RESISTANCE = register("fire_resistance");
   public static final ResourceKey LONG_FIRE_RESISTANCE = register("long_fire_resistance");
   public static final ResourceKey SWIFTNESS = register("swiftness");
   public static final ResourceKey LONG_SWIFTNESS = register("long_swiftness");
   public static final ResourceKey STRONG_SWIFTNESS = register("strong_swiftness");
   public static final ResourceKey SLOWNESS = register("slowness");
   public static final ResourceKey LONG_SLOWNESS = register("long_slowness");
   public static final ResourceKey STRONG_SLOWNESS = register("strong_slowness");
   public static final ResourceKey TURTLE_MASTER = register("turtle_master");
   public static final ResourceKey LONG_TURTLE_MASTER = register("long_turtle_master");
   public static final ResourceKey STRONG_TURTLE_MASTER = register("strong_turtle_master");
   public static final ResourceKey WATER_BREATHING = register("water_breathing");
   public static final ResourceKey LONG_WATER_BREATHING = register("long_water_breathing");
   public static final ResourceKey HEALING = register("healing");
   public static final ResourceKey STRONG_HEALING = register("strong_healing");
   public static final ResourceKey HARMING = register("harming");
   public static final ResourceKey STRONG_HARMING = register("strong_harming");
   public static final ResourceKey POISON = register("poison");
   public static final ResourceKey LONG_POISON = register("long_poison");
   public static final ResourceKey STRONG_POISON = register("strong_poison");
   public static final ResourceKey REGENERATION = register("regeneration");
   public static final ResourceKey LONG_REGENERATION = register("long_regeneration");
   public static final ResourceKey STRONG_REGENERATION = register("strong_regeneration");
   public static final ResourceKey STRENGTH = register("strength");
   public static final ResourceKey LONG_STRENGTH = register("long_strength");
   public static final ResourceKey STRONG_STRENGTH = register("strong_strength");
   public static final ResourceKey WEAKNESS = register("weakness");
   public static final ResourceKey LONG_WEAKNESS = register("long_weakness");
   public static final ResourceKey LUCK = register("luck");
   public static final ResourceKey SLOW_FALLING = register("slow_falling");
   public static final ResourceKey LONG_SLOW_FALLING = register("long_slow_falling");
   public static final ResourceKey WIND_CHARGED = register("wind_charged");
   public static final ResourceKey WEAVING = register("weaving");
   public static final ResourceKey OOZING = register("oozing");
   public static final ResourceKey INFESTED = register("infested");

   private static ResourceKey register(final String name) {
      return ResourceKey.create(Registries.POTION, Identifier.withDefaultNamespace(name));
   }
}
