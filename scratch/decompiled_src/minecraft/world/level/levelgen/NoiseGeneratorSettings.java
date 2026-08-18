package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.material.EndMaterialRules;
import net.minecraft.data.worldgen.material.NetherMaterialRules;
import net.minecraft.data.worldgen.material.OverworldMaterialRules;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public record NoiseGeneratorSettings(NoiseSettings noiseSettings, BlockState defaultBlock, BlockState defaultFluid, NoiseRouter noiseRouter, Holder materialRule, List spawnTarget, int seaLevel, boolean disableMobGeneration, Optional aquifers, List oreVeins, boolean useLegacyRandomSource) {
   public static final Codec DIRECT_CODEC = RecordCodecBuilder.create((i) -> i.group(NoiseSettings.CODEC.fieldOf("noise").forGetter(NoiseGeneratorSettings::noiseSettings), BlockState.CODEC.fieldOf("default_block").forGetter(NoiseGeneratorSettings::defaultBlock), BlockState.CODEC.fieldOf("default_fluid").forGetter(NoiseGeneratorSettings::defaultFluid), NoiseRouter.CODEC.fieldOf("noise_router").forGetter(NoiseGeneratorSettings::noiseRouter), SurfaceRules.RuleSource.HOLDER_CODEC.fieldOf("material_rule").forGetter(NoiseGeneratorSettings::materialRule), SpawnTargetPoint.CODEC.listOf().fieldOf("spawn_target").forGetter(NoiseGeneratorSettings::spawnTarget), Codec.INT.fieldOf("sea_level").forGetter(NoiseGeneratorSettings::seaLevel), Codec.BOOL.fieldOf("disable_mob_generation").forGetter(NoiseGeneratorSettings::disableMobGeneration), Aquifer.Config.CODEC.optionalFieldOf("aquifers").forGetter(NoiseGeneratorSettings::aquifers), OreVeinifier.CODEC.listOf().optionalFieldOf("ore_veins", List.of()).forGetter(NoiseGeneratorSettings::oreVeins), Codec.BOOL.fieldOf("legacy_random_source").forGetter(NoiseGeneratorSettings::useLegacyRandomSource)).apply(i, NoiseGeneratorSettings::new));
   public static final Codec CODEC = RegistryCodecs.holder(Registries.NOISE_SETTINGS, DIRECT_CODEC);
   public static final ResourceKey OVERWORLD = ResourceKey.create(Registries.NOISE_SETTINGS, Identifier.withDefaultNamespace("overworld"));
   public static final ResourceKey LARGE_BIOMES = ResourceKey.create(Registries.NOISE_SETTINGS, Identifier.withDefaultNamespace("large_biomes"));
   public static final ResourceKey AMPLIFIED = ResourceKey.create(Registries.NOISE_SETTINGS, Identifier.withDefaultNamespace("amplified"));
   public static final ResourceKey NETHER = ResourceKey.create(Registries.NOISE_SETTINGS, Identifier.withDefaultNamespace("nether"));
   public static final ResourceKey END = ResourceKey.create(Registries.NOISE_SETTINGS, Identifier.withDefaultNamespace("end"));
   public static final ResourceKey CAVES = ResourceKey.create(Registries.NOISE_SETTINGS, Identifier.withDefaultNamespace("caves"));
   public static final ResourceKey FLOATING_ISLANDS = ResourceKey.create(Registries.NOISE_SETTINGS, Identifier.withDefaultNamespace("floating_islands"));

   public WorldgenRandom.Algorithm getRandomSource() {
      return this.useLegacyRandomSource ? WorldgenRandom.Algorithm.LEGACY : WorldgenRandom.Algorithm.XOROSHIRO;
   }

   public static void bootstrap(final BootstrapContext context) {
      context.register(OVERWORLD, overworld(context, false, false));
      context.register(LARGE_BIOMES, overworld(context, false, true));
      context.register(AMPLIFIED, overworld(context, true, false));
      context.register(NETHER, nether(context));
      context.register(END, end(context));
      context.register(CAVES, caves(context));
      context.register(FLOATING_ISLANDS, floatingIslands(context));
   }

   private static NoiseGeneratorSettings end(final BootstrapContext context) {
      return new NoiseGeneratorSettings(NoiseSettings.END_NOISE_SETTINGS, Blocks.END_STONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), NoiseRouterData.end(context.lookup(Registries.DENSITY_FUNCTION)), context.lookup(Registries.MATERIAL_RULE).getOrThrow(EndMaterialRules.END), List.of(), 0, true, Optional.empty(), List.of(), true);
   }

   private static NoiseGeneratorSettings nether(final BootstrapContext context) {
      return new NoiseGeneratorSettings(NoiseSettings.NETHER_NOISE_SETTINGS, Blocks.NETHERRACK.defaultBlockState(), Blocks.LAVA.defaultBlockState(), NoiseRouterData.nether(context.lookup(Registries.DENSITY_FUNCTION), context.lookup(Registries.NOISE)), context.lookup(Registries.MATERIAL_RULE).getOrThrow(NetherMaterialRules.NETHER), List.of(), 32, false, Optional.empty(), List.of(), true);
   }

   private static NoiseGeneratorSettings overworld(final BootstrapContext context, final boolean isAmplified, final boolean largeBiomes) {
      HolderGetter functions = context.lookup(Registries.DENSITY_FUNCTION);
      HolderGetter noises = context.lookup(Registries.NOISE);
      OverworldFunctionSet functionNames;
      if (isAmplified) {
         functionNames = NoiseRouterData.AMPLIFIED_OVERWORLD_FUNCTIONS;
      } else if (largeBiomes) {
         functionNames = NoiseRouterData.LARGE_OVERWORLD_FUNCTIONS;
      } else {
         functionNames = NoiseRouterData.OVERWORLD_FUNCTIONS;
      }

      Holder weirdness = functions.getOrThrow(NoiseRouterData.RIDGES);
      List spawnTarget = (new OverworldBiomeBuilder()).spawnTarget(functionNames.map(functions::getOrThrow), weirdness);
      return new NoiseGeneratorSettings(NoiseSettings.OVERWORLD_NOISE_SETTINGS, Blocks.STONE.defaultBlockState(), Blocks.WATER.defaultBlockState(), NoiseRouterData.overworld(functions, functionNames), context.lookup(Registries.MATERIAL_RULE).getOrThrow(OverworldMaterialRules.OVERWORLD), spawnTarget, 63, false, Optional.of(NoiseRouterData.overworldAquifers(functions, noises, functionNames)), NoiseRouterData.overworldOreVeins(functions), false);
   }

   private static NoiseGeneratorSettings caves(final BootstrapContext context) {
      return new NoiseGeneratorSettings(NoiseSettings.CAVES_NOISE_SETTINGS, Blocks.STONE.defaultBlockState(), Blocks.WATER.defaultBlockState(), NoiseRouterData.caves(context.lookup(Registries.DENSITY_FUNCTION)), context.lookup(Registries.MATERIAL_RULE).getOrThrow(OverworldMaterialRules.OVERWORLD_CAVES), List.of(), 32, false, Optional.empty(), List.of(), true);
   }

   private static NoiseGeneratorSettings floatingIslands(final BootstrapContext context) {
      return new NoiseGeneratorSettings(NoiseSettings.FLOATING_ISLANDS_NOISE_SETTINGS, Blocks.STONE.defaultBlockState(), Blocks.WATER.defaultBlockState(), NoiseRouterData.floatingIslands(context.lookup(Registries.DENSITY_FUNCTION), context.lookup(Registries.NOISE)), context.lookup(Registries.MATERIAL_RULE).getOrThrow(OverworldMaterialRules.OVERWORLD_FLOATING_ISLANDS), List.of(), -64, false, Optional.empty(), List.of(), true);
   }
}
