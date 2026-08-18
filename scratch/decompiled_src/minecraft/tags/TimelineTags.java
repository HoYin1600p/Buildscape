package net.minecraft.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;

public interface TimelineTags {
   TagKey UNIVERSAL = create("universal");
   TagKey IN_OVERWORLD = create("in_overworld");
   TagKey IN_NETHER = create("in_nether");
   TagKey IN_END = create("in_end");

   private static TagKey create(final String name) {
      return TagKey.create(Registries.TIMELINE, Identifier.withDefaultNamespace(name));
   }
}
