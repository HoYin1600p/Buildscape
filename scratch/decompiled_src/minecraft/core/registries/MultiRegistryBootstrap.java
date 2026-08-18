package net.minecraft.core.registries;

import java.util.Set;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

public interface MultiRegistryBootstrap {
   Set requestedRegistries();

   void run(MultiRegistryBootstrap.BootstrapGetter registries);

   public interface BootstrapGetter {
      BootstrapContext get(ResourceKey key);
   }
}
