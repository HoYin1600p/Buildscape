package net.minecraft.world.level.levelgen.structure.placement;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;

public interface StructurePlacements {
   static MapCodec bootstrap(final Registry registry) {
      Registry.register(registry, "concentric_rings", ConcentricRingsStructurePlacement.CODEC);
      Registry.register(registry, "dimension_origin", DimensionOriginStructurePlacement.CODEC);
      return (MapCodec)Registry.register(registry, "random_spread", RandomSpreadStructurePlacement.CODEC);
   }
}
