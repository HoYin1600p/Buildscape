package net.minecraft.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;

public class FlatLevelGeneratorPresetTags {
   public static final TagKey VISIBLE = create("visible");

   private FlatLevelGeneratorPresetTags() {
   }

   private static TagKey create(final String name) {
      return TagKey.create(Registries.FLAT_LEVEL_GENERATOR_PRESET, Identifier.withDefaultNamespace(name));
   }
}
