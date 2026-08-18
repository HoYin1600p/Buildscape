package net.minecraft.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;

public class FeatureTags {
   public static final TagKey CAN_SPAWN_FROM_BONE_MEAL = create("can_spawn_from_bone_meal");

   private FeatureTags() {
   }

   private static TagKey create(final String name) {
      return TagKey.create(Registries.FEATURE, Identifier.withDefaultNamespace(name));
   }
}
