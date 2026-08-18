package net.minecraft.gametest.framework;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public interface GameTestEnvironments {
   String DEFAULT = "default";
   ResourceKey DEFAULT_KEY = create("default");

   private static ResourceKey create(final String name) {
      return ResourceKey.create(Registries.TEST_ENVIRONMENT, Identifier.withDefaultNamespace(name));
   }

   static void bootstrap(final BootstrapContext context) {
      context.register(DEFAULT_KEY, new TestEnvironmentDefinition.AllOf(List.of()));
   }
}
