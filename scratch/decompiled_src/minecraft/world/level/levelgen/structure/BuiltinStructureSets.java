package net.minecraft.world.level.levelgen.structure;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public interface BuiltinStructureSets {
   ResourceKey VILLAGES = register("villages");
   ResourceKey DESERT_PYRAMIDS = register("desert_pyramids");
   ResourceKey IGLOOS = register("igloos");
   ResourceKey JUNGLE_TEMPLES = register("jungle_temples");
   ResourceKey SWAMP_HUTS = register("swamp_huts");
   ResourceKey PILLAGER_OUTPOSTS = register("pillager_outposts");
   ResourceKey OCEAN_MONUMENTS = register("ocean_monuments");
   ResourceKey WOODLAND_MANSIONS = register("woodland_mansions");
   ResourceKey BURIED_TREASURES = register("buried_treasures");
   ResourceKey MINESHAFTS = register("mineshafts");
   ResourceKey RUINED_PORTALS = register("ruined_portals");
   ResourceKey SHIPWRECKS = register("shipwrecks");
   ResourceKey OCEAN_RUINS = register("ocean_ruins");
   ResourceKey NETHER_COMPLEXES = register("nether_complexes");
   ResourceKey NETHER_FOSSILS = register("nether_fossils");
   ResourceKey END_CITIES = register("end_cities");
   ResourceKey ANCIENT_CITIES = register("ancient_cities");
   ResourceKey STRONGHOLDS = register("strongholds");
   ResourceKey TRAIL_RUINS = register("trail_ruins");
   ResourceKey TRIAL_CHAMBERS = register("trial_chambers");
   ResourceKey ABANDONED_CAMP = register("abandoned_camp");

   private static ResourceKey register(final String name) {
      return ResourceKey.create(Registries.STRUCTURE_SET, Identifier.withDefaultNamespace(name));
   }
}
