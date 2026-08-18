package net.minecraft.world.level.levelgen.structure;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public interface BuiltinStructures {
   ResourceKey PILLAGER_OUTPOST = createKey("pillager_outpost");
   ResourceKey MINESHAFT = createKey("mineshaft");
   ResourceKey MINESHAFT_MESA = createKey("mineshaft_mesa");
   ResourceKey WOODLAND_MANSION = createKey("mansion");
   ResourceKey JUNGLE_TEMPLE = createKey("jungle_pyramid");
   ResourceKey DESERT_PYRAMID = createKey("desert_pyramid");
   ResourceKey IGLOO = createKey("igloo");
   ResourceKey SHIPWRECK = createKey("shipwreck");
   ResourceKey SHIPWRECK_BEACHED = createKey("shipwreck_beached");
   ResourceKey SWAMP_HUT = createKey("swamp_hut");
   ResourceKey STRONGHOLD = createKey("stronghold");
   ResourceKey OCEAN_MONUMENT = createKey("monument");
   ResourceKey OCEAN_RUIN_COLD = createKey("ocean_ruin_cold");
   ResourceKey OCEAN_RUIN_WARM = createKey("ocean_ruin_warm");
   ResourceKey FORTRESS = createKey("fortress");
   ResourceKey NETHER_FOSSIL = createKey("nether_fossil");
   ResourceKey END_CITY = createKey("end_city");
   ResourceKey BURIED_TREASURE = createKey("buried_treasure");
   ResourceKey BASTION_REMNANT = createKey("bastion_remnant");
   ResourceKey VILLAGE_PLAINS = createKey("village_plains");
   ResourceKey VILLAGE_DESERT = createKey("village_desert");
   ResourceKey VILLAGE_SAVANNA = createKey("village_savanna");
   ResourceKey VILLAGE_SNOWY = createKey("village_snowy");
   ResourceKey VILLAGE_TAIGA = createKey("village_taiga");
   ResourceKey RUINED_PORTAL_STANDARD = createKey("ruined_portal");
   ResourceKey RUINED_PORTAL_DESERT = createKey("ruined_portal_desert");
   ResourceKey RUINED_PORTAL_JUNGLE = createKey("ruined_portal_jungle");
   ResourceKey RUINED_PORTAL_SWAMP = createKey("ruined_portal_swamp");
   ResourceKey RUINED_PORTAL_MOUNTAIN = createKey("ruined_portal_mountain");
   ResourceKey RUINED_PORTAL_OCEAN = createKey("ruined_portal_ocean");
   ResourceKey RUINED_PORTAL_NETHER = createKey("ruined_portal_nether");
   ResourceKey ANCIENT_CITY = createKey("ancient_city");
   ResourceKey TRAIL_RUINS = createKey("trail_ruins");
   ResourceKey TRIAL_CHAMBERS = createKey("trial_chambers");
   ResourceKey ABANDONED_CAMP_BAMBOO_JUNGLE = createKey("abandoned_camp_bamboo_jungle");
   ResourceKey ABANDONED_CAMP_BIRCH_FOREST = createKey("abandoned_camp_birch_forest");
   ResourceKey ABANDONED_CAMP_CHERRY_GROVE = createKey("abandoned_camp_cherry_grove");
   ResourceKey ABANDONED_CAMP_DAPPLED_FOREST = createKey("abandoned_camp_dappled_forest");
   ResourceKey ABANDONED_CAMP_FLOWER_FOREST = createKey("abandoned_camp_flower_forest");
   ResourceKey ABANDONED_CAMP_FOREST = createKey("abandoned_camp_forest");
   ResourceKey ABANDONED_CAMP_MEADOW = createKey("abandoned_camp_meadow");
   ResourceKey ABANDONED_CAMP_OLD_GROWTH_BIRCH_FOREST = createKey("abandoned_camp_old_growth_birch_forest");
   ResourceKey ABANDONED_CAMP_OLD_GROWTH_PINE_TAIGA = createKey("abandoned_camp_old_growth_pine_taiga");
   ResourceKey ABANDONED_CAMP_OLD_GROWTH_SPRUCE_TAIGA = createKey("abandoned_camp_old_growth_spruce_taiga");
   ResourceKey ABANDONED_CAMP_PALE_GARDEN = createKey("abandoned_camp_pale_garden");
   ResourceKey ABANDONED_CAMP_SAVANNA = createKey("abandoned_camp_savanna");
   ResourceKey ABANDONED_CAMP_SNOWY_TAIGA = createKey("abandoned_camp_snowy_taiga");
   ResourceKey ABANDONED_CAMP_SPARSE_JUNGLE = createKey("abandoned_camp_sparse_jungle");
   ResourceKey ABANDONED_CAMP_SWAMP = createKey("abandoned_camp_swamp");
   ResourceKey ABANDONED_CAMP_TAIGA = createKey("abandoned_camp_taiga");
   ResourceKey ABANDONED_CAMP_WINDSWEPT_FOREST = createKey("abandoned_camp_windswept_forest");
   ResourceKey ABANDONED_CAMP_WOODED_BADLANDS = createKey("abandoned_camp_wooded_badlands");

   private static ResourceKey createKey(final String name) {
      return ResourceKey.create(Registries.STRUCTURE, Identifier.withDefaultNamespace(name));
   }
}
