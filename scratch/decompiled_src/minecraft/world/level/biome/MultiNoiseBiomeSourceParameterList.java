package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;

public class MultiNoiseBiomeSourceParameterList {
   public static final Codec DIRECT_CODEC = RecordCodecBuilder.create((i) -> i.group(MultiNoiseBiomeSourceParameterList.Preset.CODEC.fieldOf("preset").forGetter((e) -> e.preset), RegistryOps.retrieveGetter(Registries.BIOME)).apply(i, MultiNoiseBiomeSourceParameterList::new));
   public static final Codec CODEC = RegistryCodecs.holder(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST, DIRECT_CODEC);
   private final MultiNoiseBiomeSourceParameterList.Preset preset;
   private final Climate.ParameterList parameters;

   public MultiNoiseBiomeSourceParameterList(final MultiNoiseBiomeSourceParameterList.Preset preset, final HolderGetter biomes) {
      this.preset = preset;
      this.parameters = preset.provider.apply(biomes::getOrThrow);
   }

   public Climate.ParameterList parameters() {
      return this.parameters;
   }

   public static Map knownPresets() {
      return (Map)MultiNoiseBiomeSourceParameterList.Preset.BY_NAME.values().stream().collect(Collectors.toMap((e) -> e, (e) -> e.provider().apply((k) -> k)));
   }

   public static record Preset(Identifier id, MultiNoiseBiomeSourceParameterList.Preset.SourceProvider provider) {
      public static final MultiNoiseBiomeSourceParameterList.Preset NETHER = new MultiNoiseBiomeSourceParameterList.Preset(Identifier.withDefaultNamespace("nether"), new MultiNoiseBiomeSourceParameterList.Preset.SourceProvider() {
         public Climate.ParameterList apply(final Function lookup) {
            return new Climate.ParameterList(List.of(Pair.of(Climate.parameters(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), lookup.apply(Biomes.NETHER_WASTES)), Pair.of(Climate.parameters(0.0F, -0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), lookup.apply(Biomes.SOUL_SAND_VALLEY)), Pair.of(Climate.parameters(0.4F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), lookup.apply(Biomes.CRIMSON_FOREST)), Pair.of(Climate.parameters(0.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.375F), lookup.apply(Biomes.WARPED_FOREST)), Pair.of(Climate.parameters(-0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.175F), lookup.apply(Biomes.BASALT_DELTAS))));
         }
      });
      public static final MultiNoiseBiomeSourceParameterList.Preset OVERWORLD = new MultiNoiseBiomeSourceParameterList.Preset(Identifier.withDefaultNamespace("overworld"), new MultiNoiseBiomeSourceParameterList.Preset.SourceProvider() {
         public Climate.ParameterList apply(final Function lookup) {
            return MultiNoiseBiomeSourceParameterList.Preset.generateOverworldBiomes(lookup);
         }
      });
      private static final Map BY_NAME = (Map)Stream.of(NETHER, OVERWORLD).collect(Collectors.toMap(MultiNoiseBiomeSourceParameterList.Preset::id, (p) -> p));
      public static final Codec CODEC = Identifier.CODEC.flatXmap((name) -> (DataResult)Optional.ofNullable((MultiNoiseBiomeSourceParameterList.Preset)BY_NAME.get(name)).map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Unknown preset: " + String.valueOf(name))), (p) -> DataResult.success(p.id));

      private static Climate.ParameterList generateOverworldBiomes(final Function lookup) {
         ImmutableList.Builder builder = ImmutableList.builder();
         (new OverworldBiomeBuilder()).addBiomes((p) -> builder.add(p.mapSecond(lookup)));
         return new Climate.ParameterList(builder.build());
      }

      public Stream usedBiomes() {
         return this.provider.apply((e) -> e).values().stream().map(Pair::getSecond).distinct();
      }

      @FunctionalInterface
      private interface SourceProvider {
         Climate.ParameterList apply(final Function lookup);
      }
   }
}
