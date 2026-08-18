package net.minecraft.world.entity.variant;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;

public class SpawnConditions {
   public static MapCodec bootstrap(final Registry registry) {
      Registry.register(registry, "structure", StructureCheck.MAP_CODEC);
      Registry.register(registry, "moon_brightness", MoonBrightnessCheck.MAP_CODEC);
      return (MapCodec)Registry.register(registry, "biome", BiomeCheck.MAP_CODEC);
   }
}
