package net.minecraft.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;

public interface InstrumentTags {
   TagKey REGULAR_GOAT_HORNS = create("regular_goat_horns");
   TagKey SCREAMING_GOAT_HORNS = create("screaming_goat_horns");
   TagKey GOAT_HORNS = create("goat_horns");

   private static TagKey create(final String name) {
      return TagKey.create(Registries.INSTRUMENT, Identifier.withDefaultNamespace(name));
   }
}
