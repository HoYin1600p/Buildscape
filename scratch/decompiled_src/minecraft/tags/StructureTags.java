package net.minecraft.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;

public interface StructureTags {
   TagKey EYE_OF_ENDER_LOCATED = create("eye_of_ender_located");
   TagKey DOLPHIN_LOCATED = create("dolphin_located");
   TagKey ON_WOODLAND_EXPLORER_MAPS = create("on_woodland_explorer_maps");
   TagKey ON_OCEAN_EXPLORER_MAPS = create("on_ocean_explorer_maps");
   TagKey ON_SAVANNA_VILLAGE_MAPS = create("on_savanna_village_maps");
   TagKey ON_DESERT_VILLAGE_MAPS = create("on_desert_village_maps");
   TagKey ON_PLAINS_VILLAGE_MAPS = create("on_plains_village_maps");
   TagKey ON_TAIGA_VILLAGE_MAPS = create("on_taiga_village_maps");
   TagKey ON_SNOWY_VILLAGE_MAPS = create("on_snowy_village_maps");
   TagKey ON_JUNGLE_EXPLORER_MAPS = create("on_jungle_explorer_maps");
   TagKey ON_SWAMP_EXPLORER_MAPS = create("on_swamp_explorer_maps");
   TagKey ON_TREASURE_MAPS = create("on_treasure_maps");
   TagKey ON_TRIAL_CHAMBERS_MAPS = create("on_trial_chambers_maps");
   TagKey ON_ANCIENT_CITY_MAPS = create("on_ancient_city_maps");
   TagKey ON_MINESHAFT_MAPS = create("on_mineshaft_maps");
   TagKey ON_DESERT_PYRAMID_MAPS = create("on_desert_pyramid_maps");
   TagKey ON_OCEAN_RUIN_WARM_MAPS = create("on_ocean_ruin_warm_maps");
   TagKey CATS_SPAWN_IN = create("cats_spawn_in");
   TagKey CATS_SPAWN_AS_BLACK = create("cats_spawn_as_black");
   TagKey VILLAGE = create("village");
   TagKey MINESHAFT = create("mineshaft");
   TagKey SHIPWRECK = create("shipwreck");
   TagKey RUINED_PORTAL = create("ruined_portal");
   TagKey OCEAN_RUIN = create("ocean_ruin");
   TagKey ABANDONED_CAMP = create("abandoned_camp");
   TagKey ON_ABANDONED_CAMP_BAMBOO_JUNGLE_MAPS = create("on_abandoned_camp_bamboo_jungle");
   TagKey ON_ABANDONED_CAMP_CHERRY_GROVE_MAPS = create("on_abandoned_camp_cherry_grove");
   TagKey ON_ABANDONED_CAMP_BIRCH_FOREST_MAPS = create("on_abandoned_camp_birch_forest");
   TagKey ON_ABANDONED_CAMP_DAPPLED_FOREST_MAPS = create("on_abandoned_camp_dappled_forest");
   TagKey ON_ABANDONED_CAMP_FLOWER_FOREST_MAPS = create("on_abandoned_camp_flower_forest");
   TagKey ON_ABANDONED_CAMP_PALE_GARDEN_MAPS = create("on_abandoned_camp_pale_garden");
   TagKey ON_ABANDONED_CAMP_SWAMP_MAPS = create("on_abandoned_camp_swamp");
   TagKey ON_ABANDONED_CAMP_WINDSWEPT_FOREST_MAPS = create("on_abandoned_camp_windswept");

   private static TagKey create(final String name) {
      return TagKey.create(Registries.STRUCTURE, Identifier.withDefaultNamespace(name));
   }
}
