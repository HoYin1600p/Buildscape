package net.minecraft.core.registries;

import net.minecraft.data.worldgen.BootstrapContext;

@FunctionalInterface
public interface SingleRegistryBootstrap {
   void run(BootstrapContext registry);
}
