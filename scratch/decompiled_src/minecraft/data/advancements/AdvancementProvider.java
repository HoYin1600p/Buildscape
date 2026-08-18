package net.minecraft.data.advancements;

import java.util.List;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.registries.SingleRegistryBootstrap;
import net.minecraft.data.worldgen.BootstrapContext;

public class AdvancementProvider implements SingleRegistryBootstrap {
   private final List subProviders;

   public AdvancementProvider(final List subProviders) {
      this.subProviders = subProviders;
   }

   public void run(final BootstrapContext output) {
      for(AdvancementSubProvider.Factory subProvider : this.subProviders) {
         subProvider.create(output).generate();
      }

   }
}
