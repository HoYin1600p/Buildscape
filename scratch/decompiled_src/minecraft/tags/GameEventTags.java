package net.minecraft.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;

public class GameEventTags {
   public static final TagKey VIBRATIONS = create("vibrations");
   public static final TagKey WARDEN_CAN_LISTEN = create("warden_can_listen");
   public static final TagKey SHRIEKER_CAN_LISTEN = create("shrieker_can_listen");
   public static final TagKey IGNORE_VIBRATIONS_SNEAKING = create("ignore_vibrations_sneaking");
   public static final TagKey ALLAY_CAN_LISTEN = create("allay_can_listen");

   private static TagKey create(final String name) {
      return TagKey.create(Registries.GAME_EVENT, Identifier.withDefaultNamespace(name));
   }
}
