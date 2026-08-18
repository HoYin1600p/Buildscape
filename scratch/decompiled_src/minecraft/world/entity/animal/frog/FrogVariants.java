package net.minecraft.world.entity.animal.frog;

import net.minecraft.core.ClientAsset;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.animal.TemperatureVariants;
import net.minecraft.world.entity.variant.BiomeCheck;
import net.minecraft.world.entity.variant.SpawnPrioritySelectors;
import net.minecraft.world.level.biome.Biome;

public interface FrogVariants {
   ResourceKey TEMPERATE = createKey(TemperatureVariants.TEMPERATE);
   ResourceKey WARM = createKey(TemperatureVariants.WARM);
   ResourceKey COLD = createKey(TemperatureVariants.COLD);

   private static ResourceKey createKey(final Identifier id) {
      return ResourceKey.create(Registries.FROG_VARIANT, id);
   }

   static void bootstrap(final BootstrapContext registry) {
      register(registry, TEMPERATE, "entity/frog/frog_temperate", SpawnPrioritySelectors.fallback(0));
      register(registry, WARM, "entity/frog/frog_warm", BiomeTags.SPAWNS_WARM_VARIANT_FROGS);
      register(registry, COLD, "entity/frog/frog_cold", BiomeTags.SPAWNS_COLD_VARIANT_FROGS);
   }

   private static void register(final BootstrapContext context, final ResourceKey name, final String assetId, final TagKey limitToBiome) {
      HolderSet biomes = context.lookup(Registries.BIOME).getOrThrow(limitToBiome);
      register(context, name, assetId, SpawnPrioritySelectors.single(new BiomeCheck(biomes), 1));
   }

   private static void register(final BootstrapContext context, final ResourceKey name, final String assetId, final SpawnPrioritySelectors selectors) {
      context.register(name, new FrogVariant(new ClientAsset.ResourceTexture(Identifier.withDefaultNamespace(assetId)), selectors));
   }
}
