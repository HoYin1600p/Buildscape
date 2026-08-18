package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class TrunkPlacerType {
   public static final TrunkPlacerType STRAIGHT_TRUNK_PLACER = register("straight_trunk_placer", StraightTrunkPlacer.CODEC);
   public static final TrunkPlacerType FORKING_TRUNK_PLACER = register("forking_trunk_placer", ForkingTrunkPlacer.CODEC);
   public static final TrunkPlacerType GIANT_TRUNK_PLACER = register("giant_trunk_placer", GiantTrunkPlacer.CODEC);
   public static final TrunkPlacerType MEGA_JUNGLE_TRUNK_PLACER = register("mega_jungle_trunk_placer", MegaJungleTrunkPlacer.CODEC);
   public static final TrunkPlacerType DARK_OAK_TRUNK_PLACER = register("dark_oak_trunk_placer", DarkOakTrunkPlacer.CODEC);
   public static final TrunkPlacerType FANCY_TRUNK_PLACER = register("fancy_trunk_placer", FancyTrunkPlacer.CODEC);
   public static final TrunkPlacerType BENDING_TRUNK_PLACER = register("bending_trunk_placer", BendingTrunkPlacer.CODEC);
   public static final TrunkPlacerType UPWARDS_BRANCHING_TRUNK_PLACER = register("upwards_branching_trunk_placer", UpwardsBranchingTrunkPlacer.CODEC);
   public static final TrunkPlacerType CHERRY_TRUNK_PLACER = register("cherry_trunk_placer", CherryTrunkPlacer.CODEC);
   public static final TrunkPlacerType POPLAR_TRUNK_PLACER = register("poplar_trunk_placer", PoplarTrunkPlacer.CODEC);
   private final MapCodec codec;

   private static TrunkPlacerType register(final String name, final MapCodec codec) {
      return (TrunkPlacerType)Registry.register(BuiltInRegistries.TRUNK_PLACER_TYPE, name, new TrunkPlacerType(codec));
   }

   private TrunkPlacerType(final MapCodec codec) {
      this.codec = codec;
   }

   public MapCodec codec() {
      return this.codec;
   }
}
