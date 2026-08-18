package net.minecraft.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;

public interface EntityTypeTags {
   TagKey SKELETONS = create("skeletons");
   TagKey ZOMBIES = create("zombies");
   TagKey RAIDERS = create("raiders");
   TagKey UNDEAD = create("undead");
   TagKey BURN_IN_DAYLIGHT = create("burn_in_daylight");
   TagKey BEEHIVE_INHABITORS = create("beehive_inhabitors");
   TagKey ARROWS = create("arrows");
   TagKey IMPACT_PROJECTILES = create("impact_projectiles");
   TagKey POWDER_SNOW_WALKABLE_MOBS = create("powder_snow_walkable_mobs");
   TagKey AXOLOTL_ALWAYS_HOSTILES = create("axolotl_always_hostiles");
   TagKey AXOLOTL_HUNT_TARGETS = create("axolotl_hunt_targets");
   TagKey FREEZE_IMMUNE_ENTITY_TYPES = create("freeze_immune_entity_types");
   TagKey FREEZE_HURTS_EXTRA_TYPES = create("freeze_hurts_extra_types");
   TagKey CAN_BREATHE_UNDER_WATER = create("can_breathe_under_water");
   TagKey FROG_FOOD = create("frog_food");
   TagKey FALL_DAMAGE_IMMUNE = create("fall_damage_immune");
   TagKey DISMOUNTS_UNDERWATER = create("dismounts_underwater");
   TagKey NON_CONTROLLING_RIDER = create("non_controlling_rider");
   TagKey DEFLECTS_PROJECTILES = create("deflects_projectiles");
   TagKey CAN_TURN_IN_BOATS = create("can_turn_in_boats");
   TagKey ILLAGER = create("illager");
   TagKey AQUATIC = create("aquatic");
   TagKey ARTHROPOD = create("arthropod");
   TagKey IGNORES_POISON_AND_REGEN = create("ignores_poison_and_regen");
   TagKey INVERTED_HEALING_AND_HARM = create("inverted_healing_and_harm");
   TagKey WITHER_FRIENDS = create("wither_friends");
   TagKey ILLAGER_FRIENDS = create("illager_friends");
   TagKey NOT_SCARY_FOR_PUFFERFISH = create("not_scary_for_pufferfish");
   TagKey SENSITIVE_TO_IMPALING = create("sensitive_to_impaling");
   TagKey SENSITIVE_TO_BANE_OF_ARTHROPODS = create("sensitive_to_bane_of_arthropods");
   TagKey SENSITIVE_TO_SMITE = create("sensitive_to_smite");
   TagKey NO_ANGER_FROM_WIND_CHARGE = create("no_anger_from_wind_charge");
   TagKey IMMUNE_TO_OOZING = create("immune_to_oozing");
   TagKey IMMUNE_TO_INFESTED = create("immune_to_infested");
   TagKey REDIRECTABLE_PROJECTILE = create("redirectable_projectile");
   TagKey BOAT = create("boat");
   TagKey CAN_EQUIP_SADDLE = create("can_equip_saddle");
   TagKey CAN_EQUIP_HARNESS = create("can_equip_harness");
   TagKey CAN_WEAR_HORSE_ARMOR = create("can_wear_horse_armor");
   TagKey CAN_WEAR_NAUTILUS_ARMOR = create("can_wear_nautilus_armor");
   TagKey FOLLOWABLE_FRIENDLY_MOBS = create("followable_friendly_mobs");
   TagKey CANNOT_BE_PUSHED_ONTO_BOATS = create("cannot_be_pushed_onto_boats");
   TagKey ACCEPTS_IRON_GOLEM_GIFT = create("accepts_iron_golem_gift");
   TagKey CANDIDATE_FOR_IRON_GOLEM_GIFT = create("candidate_for_iron_golem_gift");
   TagKey NAUTILUS_HOSTILES = create("nautilus_hostiles");
   TagKey CAN_FLOAT_WHILE_RIDDEN = create("can_float_while_ridden");
   TagKey CANNOT_BE_AGE_LOCKED = create("cannot_be_age_locked");
   TagKey NOT_AFFECTED_BY_GEYSERS = create("not_affected_by_geysers");
   TagKey CANNOT_BE_DISMOUNTED_BY_ITEM_USAGE = create("cannot_be_dismounted_by_item_usage");

   private static TagKey create(final String name) {
      return TagKey.create(Registries.ENTITY_TYPE, Identifier.withDefaultNamespace(name));
   }
}
