package net.minecraft.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;

public class DialogTags {
   public static final TagKey PAUSE_SCREEN_ADDITIONS = create("pause_screen_additions");
   public static final TagKey QUICK_ACTIONS = create("quick_actions");

   private DialogTags() {
   }

   private static TagKey create(final String name) {
      return TagKey.create(Registries.DIALOG, Identifier.withDefaultNamespace(name));
   }
}
