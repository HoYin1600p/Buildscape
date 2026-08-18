package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class FoliagePlacerType {
   public static final FoliagePlacerType BLOB_FOLIAGE_PLACER = register("blob_foliage_placer", BlobFoliagePlacer.CODEC);
   public static final FoliagePlacerType SPRUCE_FOLIAGE_PLACER = register("spruce_foliage_placer", SpruceFoliagePlacer.CODEC);
   public static final FoliagePlacerType PINE_FOLIAGE_PLACER = register("pine_foliage_placer", PineFoliagePlacer.CODEC);
   public static final FoliagePlacerType ACACIA_FOLIAGE_PLACER = register("acacia_foliage_placer", AcaciaFoliagePlacer.CODEC);
   public static final FoliagePlacerType BUSH_FOLIAGE_PLACER = register("bush_foliage_placer", BushFoliagePlacer.CODEC);
   public static final FoliagePlacerType FANCY_FOLIAGE_PLACER = register("fancy_foliage_placer", FancyFoliagePlacer.CODEC);
   public static final FoliagePlacerType MEGA_JUNGLE_FOLIAGE_PLACER = register("jungle_foliage_placer", MegaJungleFoliagePlacer.CODEC);
   public static final FoliagePlacerType MEGA_PINE_FOLIAGE_PLACER = register("mega_pine_foliage_placer", MegaPineFoliagePlacer.CODEC);
   public static final FoliagePlacerType DARK_OAK_FOLIAGE_PLACER = register("dark_oak_foliage_placer", DarkOakFoliagePlacer.CODEC);
   public static final FoliagePlacerType RANDOM_SPREAD_FOLIAGE_PLACER = register("random_spread_foliage_placer", RandomSpreadFoliagePlacer.CODEC);
   public static final FoliagePlacerType CHERRY_FOLIAGE_PLACER = register("cherry_foliage_placer", CherryFoliagePlacer.CODEC);
   public static final FoliagePlacerType POPLAR_FOLIAGE_PLACER = register("poplar_foliage_placer", PoplarFoliagePlacer.CODEC);
   private final MapCodec codec;

   private static FoliagePlacerType register(final String name, final MapCodec codec) {
      return (FoliagePlacerType)Registry.register(BuiltInRegistries.FOLIAGE_PLACER_TYPE, name, new FoliagePlacerType(codec));
   }

   private FoliagePlacerType(final MapCodec codec) {
      this.codec = codec;
   }

   public MapCodec codec() {
      return this.codec;
   }
}
