package net.minecraft.world.level.levelgen.carver;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;

public interface WorldCarverTypes {
   static MapCodec bootstrap(final Registry registry) {
      Registry.register(registry, "cave", CaveWorldCarver.MAP_CODEC);
      return (MapCodec)Registry.register(registry, "canyon", CanyonWorldCarver.MAP_CODEC);
   }
}
