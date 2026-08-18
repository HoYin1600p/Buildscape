package net.minecraft.world.level.levelgen.structure.pools;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public interface StructurePoolElementType {
   StructurePoolElementType SINGLE = register("single_pool_element", SinglePoolElement.CODEC);
   StructurePoolElementType LIST = register("list_pool_element", ListPoolElement.CODEC);
   StructurePoolElementType FEATURE = register("feature_pool_element", FeaturePoolElement.CODEC);
   StructurePoolElementType EMPTY = register("empty_pool_element", EmptyPoolElement.CODEC);
   StructurePoolElementType LEGACY = register("legacy_single_pool_element", LegacySinglePoolElement.CODEC);

   MapCodec codec();

   static StructurePoolElementType register(final String id, final MapCodec codec) {
      return (StructurePoolElementType)Registry.register(BuiltInRegistries.STRUCTURE_POOL_ELEMENT, id, (StructurePoolElementType)() -> codec);
   }
}
