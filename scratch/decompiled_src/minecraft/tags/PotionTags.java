package net.minecraft.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;

public class PotionTags {
   public static final TagKey TRADEABLE = create("tradeable");

   private PotionTags() {
   }

   private static TagKey create(final String name) {
      return TagKey.create(Registries.POTION, Identifier.withDefaultNamespace(name));
   }
}
