package net.minecraft.commands;

import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlagSet;

public interface CommandBuildContext extends HolderLookup.Provider {
   static CommandBuildContext simple(final HolderLookup.Provider access, final FeatureFlagSet enabledFeatures) {
      return new CommandBuildContext() {
         public Stream listRegistryKeys() {
            return access.listRegistryKeys();
         }

         public Optional lookup(final ResourceKey key) {
            return access.lookup(key).map((lookup) -> lookup.filterFeatures(enabledFeatures));
         }

         public FeatureFlagSet enabledFeatures() {
            return enabledFeatures;
         }
      };
   }

   FeatureFlagSet enabledFeatures();
}
