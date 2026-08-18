package net.minecraft.resources;

import java.util.Map;
import net.minecraft.core.Registry;

@FunctionalInterface
public interface RegistryValidator {
   RegistryValidator NONE = (var0, var1) -> {
   };
   RegistryValidator NON_EMPTY = (registry, loadingErrors) -> {
      if (registry.size() == 0) {
         loadingErrors.put(registry.key(), new IllegalStateException("Registry must be non-empty: " + String.valueOf(registry.key().identifier())));
      }

   };

   static RegistryValidator none() {
      return NONE;
   }

   static RegistryValidator nonEmpty() {
      return NON_EMPTY;
   }

   void validate(Registry registry, Map loadingErrors);
}
