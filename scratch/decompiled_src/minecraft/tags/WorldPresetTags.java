package net.minecraft.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;

public class WorldPresetTags {
   public static final TagKey NORMAL = create("normal");
   public static final TagKey EXTENDED = create("extended");

   private WorldPresetTags() {
   }

   private static TagKey create(final String name) {
      return TagKey.create(Registries.WORLD_PRESET, Identifier.withDefaultNamespace(name));
   }
}
