package net.minecraft.world.clock;

import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public interface ClockTimeMarkers {
   ResourceKey ROOT_ID = ResourceKey.createRegistryKey(Identifier.withDefaultNamespace("clock_time_marker"));
   ResourceKey DAY = createKey("day");
   ResourceKey NOON = createKey("noon");
   ResourceKey NIGHT = createKey("night");
   ResourceKey MIDNIGHT = createKey("midnight");
   ResourceKey WAKE_UP_FROM_SLEEP = createKey("wake_up_from_sleep");
   ResourceKey ROLL_VILLAGE_SIEGE = createKey("roll_village_siege");

   static ResourceKey createKey(final String name) {
      return ResourceKey.create(ROOT_ID, Identifier.withDefaultNamespace(name));
   }
}
