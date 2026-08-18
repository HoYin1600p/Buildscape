package net.minecraft.data.registries;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.packs.TradeRebalanceLootTableProvider;
import net.minecraft.world.item.trading.TradeRebalanceVillagerTrades;

public class TradeRebalanceRegistries {
   private static final RegistrySetBuilder WORLD_BUILDER = (new RegistrySetBuilder()).add(Registries.VILLAGER_TRADE, TradeRebalanceVillagerTrades::bootstrap);
   private static final RegistrySetBuilder RELOADABLE_BUILDER = (new RegistrySetBuilder()).add(Registries.LOOT_TABLE, TradeRebalanceLootTableProvider.create());

   public static CompletableFuture createPatchedWorldRegistries(final CompletableFuture vanillaWorld) {
      return RegistryPatchGenerator.createWorldLookup(vanillaWorld, WORLD_BUILDER);
   }

   public static CompletableFuture createPatchedReloadable(final CompletableFuture context, final CompletableFuture vanilla) {
      return RegistryPatchGenerator.createReloadableLookup(context, vanilla, RELOADABLE_BUILDER);
   }
}
