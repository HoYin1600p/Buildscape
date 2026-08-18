package net.minecraft.world.level.levelgen.presets;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;

public class WorldPreset {
   public static final Codec DIRECT_CODEC = RecordCodecBuilder.create((i) -> i.group(Codec.unboundedMap(ResourceKey.codec(Registries.LEVEL_STEM), LevelStem.CODEC).fieldOf("dimensions").forGetter((e) -> e.dimensions)).apply(i, WorldPreset::new)).validate(WorldPreset::requireOverworld);
   public static final Codec CODEC = RegistryCodecs.holder(Registries.WORLD_PRESET, DIRECT_CODEC);
   private final Map dimensions;

   public WorldPreset(final Map dimensions) {
      this.dimensions = dimensions;
   }

   private ImmutableMap dimensionsInOrder() {
      ImmutableMap.Builder builder = ImmutableMap.builder();
      WorldDimensions.keysInOrder(this.dimensions.keySet()).forEach((key) -> {
         LevelStem levelStem = (LevelStem)this.dimensions.get(key);
         if (levelStem != null) {
            builder.put(key, levelStem);
         }

      });
      return builder.build();
   }

   public WorldDimensions createWorldDimensions() {
      return new WorldDimensions(this.dimensionsInOrder());
   }

   public Optional overworld() {
      return Optional.ofNullable((LevelStem)this.dimensions.get(LevelStem.OVERWORLD));
   }

   private static DataResult requireOverworld(final WorldPreset preset) {
      return preset.overworld().isEmpty() ? DataResult.error(() -> "Missing overworld dimension") : DataResult.success(preset, Lifecycle.stable());
   }
}
