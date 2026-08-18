package net.minecraft.world.level.material;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public class FluidIds {
   public static final ResourceKey EMPTY = create("empty");
   public static final ResourceKey FLOWING_WATER = create("flowing_water");
   public static final ResourceKey WATER = create("water");
   public static final ResourceKey FLOWING_LAVA = create("flowing_lava");
   public static final ResourceKey LAVA = create("lava");

   private static ResourceKey create(final String name) {
      return ResourceKey.create(Registries.FLUID, Identifier.withDefaultNamespace(name));
   }
}
