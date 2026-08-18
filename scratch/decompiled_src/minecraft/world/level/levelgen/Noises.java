package net.minecraft.world.level.levelgen;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.synth.Noise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class Noises {
   public static final ResourceKey TEMPERATURE = createKey("temperature");
   public static final ResourceKey VEGETATION = createKey("vegetation");
   public static final ResourceKey CONTINENTALNESS = createKey("continentalness");
   public static final ResourceKey EROSION = createKey("erosion");
   public static final ResourceKey TEMPERATURE_LARGE = createKey("temperature_large");
   public static final ResourceKey VEGETATION_LARGE = createKey("vegetation_large");
   public static final ResourceKey CONTINENTALNESS_LARGE = createKey("continentalness_large");
   public static final ResourceKey EROSION_LARGE = createKey("erosion_large");
   public static final ResourceKey RIDGE = createKey("ridge");
   public static final ResourceKey SHIFT = createKey("offset");
   public static final ResourceKey TEMPERATURE_NETHER = createKey("nether/temperature");
   public static final ResourceKey VEGETATION_NETHER = createKey("nether/vegetation");
   public static final ResourceKey AQUIFER_BARRIER = createKey("aquifer_barrier");
   public static final ResourceKey AQUIFER_FLUID_LEVEL_FLOODEDNESS = createKey("aquifer_fluid_level_floodedness");
   public static final ResourceKey AQUIFER_LAVA = createKey("aquifer_lava");
   public static final ResourceKey AQUIFER_FLUID_LEVEL_SPREAD = createKey("aquifer_fluid_level_spread");
   public static final ResourceKey PILLAR = createKey("pillar");
   public static final ResourceKey PILLAR_RARENESS = createKey("pillar_rareness");
   public static final ResourceKey PILLAR_THICKNESS = createKey("pillar_thickness");
   public static final ResourceKey SPAGHETTI_2D = createKey("spaghetti_2d");
   public static final ResourceKey SPAGHETTI_2D_ELEVATION = createKey("spaghetti_2d_elevation");
   public static final ResourceKey SPAGHETTI_2D_MODULATOR = createKey("spaghetti_2d_modulator");
   public static final ResourceKey SPAGHETTI_2D_THICKNESS = createKey("spaghetti_2d_thickness");
   public static final ResourceKey SPAGHETTI_3D_1 = createKey("spaghetti_3d_1");
   public static final ResourceKey SPAGHETTI_3D_2 = createKey("spaghetti_3d_2");
   public static final ResourceKey SPAGHETTI_3D_RARITY = createKey("spaghetti_3d_rarity");
   public static final ResourceKey SPAGHETTI_3D_THICKNESS = createKey("spaghetti_3d_thickness");
   public static final ResourceKey SPAGHETTI_ROUGHNESS = createKey("spaghetti_roughness");
   public static final ResourceKey SPAGHETTI_ROUGHNESS_MODULATOR = createKey("spaghetti_roughness_modulator");
   public static final ResourceKey CAVE_ENTRANCE = createKey("cave_entrance");
   public static final ResourceKey CAVE_LAYER = createKey("cave_layer");
   public static final ResourceKey CAVE_CHEESE = createKey("cave_cheese");
   public static final ResourceKey ORE_VEININESS = createKey("ore_veininess");
   public static final ResourceKey ORE_VEIN_A = createKey("ore_vein_a");
   public static final ResourceKey ORE_VEIN_B = createKey("ore_vein_b");
   public static final ResourceKey ORE_GAP = createKey("ore_gap");
   public static final ResourceKey NOODLE = createKey("noodle");
   public static final ResourceKey NOODLE_THICKNESS = createKey("noodle_thickness");
   public static final ResourceKey NOODLE_RIDGE_A = createKey("noodle_ridge_a");
   public static final ResourceKey NOODLE_RIDGE_B = createKey("noodle_ridge_b");
   public static final ResourceKey JAGGED = createKey("jagged");
   public static final ResourceKey SURFACE = createKey("surface");
   public static final ResourceKey SURFACE_SECONDARY = createKey("surface_secondary");
   public static final ResourceKey CLAY_BANDS_OFFSET = createKey("clay_bands_offset");
   public static final ResourceKey BADLANDS_PILLAR = createKey("badlands_pillar");
   public static final ResourceKey BADLANDS_PILLAR_ROOF = createKey("badlands_pillar_roof");
   public static final ResourceKey BADLANDS_SURFACE = createKey("badlands_surface");
   public static final ResourceKey ICEBERG_PILLAR = createKey("iceberg_pillar");
   public static final ResourceKey ICEBERG_PILLAR_ROOF = createKey("iceberg_pillar_roof");
   public static final ResourceKey ICEBERG_SURFACE = createKey("iceberg_surface");
   public static final ResourceKey SULFUR_CAVE_GRADIENT = createKey("sulfur_cave_gradient");
   public static final ResourceKey SWAMP = createKey("surface_swamp");
   public static final ResourceKey CALCITE = createKey("calcite");
   public static final ResourceKey GRAVEL = createKey("gravel");
   public static final ResourceKey POWDER_SNOW = createKey("powder_snow");
   public static final ResourceKey PACKED_ICE = createKey("packed_ice");
   public static final ResourceKey ICE = createKey("ice");
   public static final ResourceKey SOUL_SAND_LAYER = createKey("soul_sand_layer");
   public static final ResourceKey GRAVEL_LAYER = createKey("gravel_layer");
   public static final ResourceKey PATCH = createKey("patch");
   public static final ResourceKey SMALL_PATCH = createKey("small_patch");
   public static final ResourceKey NETHERRACK = createKey("netherrack");
   public static final ResourceKey NETHER_WART = createKey("nether_wart");
   public static final ResourceKey NETHER_STATE_SELECTOR = createKey("nether_state_selector");

   private static ResourceKey createKey(final String name) {
      return ResourceKey.create(Registries.NOISE, Identifier.withDefaultNamespace(name));
   }

   public static Noise instantiate(final HolderGetter noises, final PositionalRandomFactory context, final ResourceKey name) {
      Holder holder = noises.getOrThrow(name);
      return ((NormalNoise)holder.value()).create(context.fromHashOf(name.identifier()));
   }
}
