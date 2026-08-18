package net.minecraft.world.level.biome;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public abstract class Biomes {
   public static final ResourceKey THE_VOID = register("the_void");
   public static final ResourceKey PLAINS = register("plains");
   public static final ResourceKey SUNFLOWER_PLAINS = register("sunflower_plains");
   public static final ResourceKey SNOWY_PLAINS = register("snowy_plains");
   public static final ResourceKey ICE_SPIKES = register("ice_spikes");
   public static final ResourceKey DESERT = register("desert");
   public static final ResourceKey SWAMP = register("swamp");
   public static final ResourceKey MANGROVE_SWAMP = register("mangrove_swamp");
   public static final ResourceKey FOREST = register("forest");
   public static final ResourceKey FLOWER_FOREST = register("flower_forest");
   public static final ResourceKey BIRCH_FOREST = register("birch_forest");
   public static final ResourceKey DAPPLED_FOREST = register("dappled_forest");
   public static final ResourceKey DARK_FOREST = register("dark_forest");
   public static final ResourceKey PALE_GARDEN = register("pale_garden");
   public static final ResourceKey OLD_GROWTH_BIRCH_FOREST = register("old_growth_birch_forest");
   public static final ResourceKey OLD_GROWTH_PINE_TAIGA = register("old_growth_pine_taiga");
   public static final ResourceKey OLD_GROWTH_SPRUCE_TAIGA = register("old_growth_spruce_taiga");
   public static final ResourceKey TAIGA = register("taiga");
   public static final ResourceKey SNOWY_TAIGA = register("snowy_taiga");
   public static final ResourceKey SAVANNA = register("savanna");
   public static final ResourceKey SAVANNA_PLATEAU = register("savanna_plateau");
   public static final ResourceKey WINDSWEPT_HILLS = register("windswept_hills");
   public static final ResourceKey WINDSWEPT_GRAVELLY_HILLS = register("windswept_gravelly_hills");
   public static final ResourceKey WINDSWEPT_FOREST = register("windswept_forest");
   public static final ResourceKey WINDSWEPT_SAVANNA = register("windswept_savanna");
   public static final ResourceKey JUNGLE = register("jungle");
   public static final ResourceKey SPARSE_JUNGLE = register("sparse_jungle");
   public static final ResourceKey BAMBOO_JUNGLE = register("bamboo_jungle");
   public static final ResourceKey BADLANDS = register("badlands");
   public static final ResourceKey ERODED_BADLANDS = register("eroded_badlands");
   public static final ResourceKey WOODED_BADLANDS = register("wooded_badlands");
   public static final ResourceKey MEADOW = register("meadow");
   public static final ResourceKey CHERRY_GROVE = register("cherry_grove");
   public static final ResourceKey GROVE = register("grove");
   public static final ResourceKey SNOWY_SLOPES = register("snowy_slopes");
   public static final ResourceKey FROZEN_PEAKS = register("frozen_peaks");
   public static final ResourceKey JAGGED_PEAKS = register("jagged_peaks");
   public static final ResourceKey STONY_PEAKS = register("stony_peaks");
   public static final ResourceKey RIVER = register("river");
   public static final ResourceKey FROZEN_RIVER = register("frozen_river");
   public static final ResourceKey BEACH = register("beach");
   public static final ResourceKey SNOWY_BEACH = register("snowy_beach");
   public static final ResourceKey STONY_SHORE = register("stony_shore");
   public static final ResourceKey WARM_OCEAN = register("warm_ocean");
   public static final ResourceKey LUKEWARM_OCEAN = register("lukewarm_ocean");
   public static final ResourceKey DEEP_LUKEWARM_OCEAN = register("deep_lukewarm_ocean");
   public static final ResourceKey OCEAN = register("ocean");
   public static final ResourceKey DEEP_OCEAN = register("deep_ocean");
   public static final ResourceKey COLD_OCEAN = register("cold_ocean");
   public static final ResourceKey DEEP_COLD_OCEAN = register("deep_cold_ocean");
   public static final ResourceKey FROZEN_OCEAN = register("frozen_ocean");
   public static final ResourceKey DEEP_FROZEN_OCEAN = register("deep_frozen_ocean");
   public static final ResourceKey MUSHROOM_FIELDS = register("mushroom_fields");
   public static final ResourceKey DRIPSTONE_CAVES = register("dripstone_caves");
   public static final ResourceKey LUSH_CAVES = register("lush_caves");
   public static final ResourceKey DEEP_DARK = register("deep_dark");
   public static final ResourceKey SULFUR_CAVES = register("sulfur_caves");
   public static final ResourceKey NETHER_WASTES = register("nether_wastes");
   public static final ResourceKey WARPED_FOREST = register("warped_forest");
   public static final ResourceKey CRIMSON_FOREST = register("crimson_forest");
   public static final ResourceKey SOUL_SAND_VALLEY = register("soul_sand_valley");
   public static final ResourceKey BASALT_DELTAS = register("basalt_deltas");
   public static final ResourceKey THE_END = register("the_end");
   public static final ResourceKey END_HIGHLANDS = register("end_highlands");
   public static final ResourceKey END_MIDLANDS = register("end_midlands");
   public static final ResourceKey SMALL_END_ISLANDS = register("small_end_islands");
   public static final ResourceKey END_BARRENS = register("end_barrens");

   private static ResourceKey register(final String name) {
      return ResourceKey.create(Registries.BIOME, Identifier.withDefaultNamespace(name));
   }
}
