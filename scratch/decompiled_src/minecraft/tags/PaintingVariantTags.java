package net.minecraft.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;

public class PaintingVariantTags {
   public static final TagKey PLACEABLE = create("placeable");

   private PaintingVariantTags() {
   }

   private static TagKey create(final String name) {
      return TagKey.create(Registries.PAINTING_VARIANT, Identifier.withDefaultNamespace(name));
   }
}
