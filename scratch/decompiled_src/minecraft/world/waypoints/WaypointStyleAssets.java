package net.minecraft.world.waypoints;

import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public interface WaypointStyleAssets {
   ResourceKey ROOT_ID = ResourceKey.createRegistryKey(Identifier.withDefaultNamespace("waypoint_style_asset"));
   ResourceKey DEFAULT = createId("default");
   ResourceKey BOWTIE = createId("bowtie");

   static ResourceKey createId(final String name) {
      return ResourceKey.create(ROOT_ID, Identifier.withDefaultNamespace(name));
   }
}
