package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;

public record PlacedFeature(Holder feature, List placement) {
   public static final Codec DIRECT_CODEC = RecordCodecBuilder.create((i) -> i.group(Feature.CODEC.fieldOf("feature").forGetter((c) -> c.feature), PlacementModifier.CODEC.listOf().fieldOf("placement").forGetter((c) -> c.placement)).apply(i, PlacedFeature::new));
   public static final Codec CODEC = RegistryCodecs.holder(Registries.PLACED_FEATURE, DIRECT_CODEC);
   public static final Codec LIST_CODEC = RegistryCodecs.holderSet(Registries.PLACED_FEATURE, DIRECT_CODEC);
   public static final Codec LIST_OF_LISTS_CODEC = RegistryCodecs.holderSet(Registries.PLACED_FEATURE, DIRECT_CODEC, true).listOf();

   public boolean place(final WorldGenLevel level, final ChunkGenerator generator, final RandomSource random, final BlockPos origin) {
      FeaturePlacer placer = new FeaturePlacer(level, generator);
      return placer.place(this, random, origin);
   }

   public Stream getFeatures() {
      return Stream.concat(Stream.of(this.feature), ((Feature)this.feature.value()).getSubFeatures());
   }

   public String toString() {
      return "Placed " + String.valueOf(this.feature);
   }
}
