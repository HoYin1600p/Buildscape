package net.minecraft.world.level.levelgen.feature.rootplacers;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class RootPlacerType {
   public static final RootPlacerType MANGROVE_ROOT_PLACER = register("mangrove_root_placer", MangroveRootPlacer.CODEC);
   private final MapCodec codec;

   private static RootPlacerType register(final String name, final MapCodec codec) {
      return (RootPlacerType)Registry.register(BuiltInRegistries.ROOT_PLACER_TYPE, name, new RootPlacerType(codec));
   }

   private RootPlacerType(final MapCodec codec) {
      this.codec = codec;
   }

   public MapCodec codec() {
      return this.codec;
   }
}
