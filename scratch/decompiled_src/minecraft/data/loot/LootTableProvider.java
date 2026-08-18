package net.minecraft.data.loot;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.SingleRegistryBootstrap;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.RandomSequence;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.storage.loot.LootTable;

public class LootTableProvider implements SingleRegistryBootstrap {
   private final Set requiredTables;
   private final List subProviders;

   public LootTableProvider(final Set requiredTables, final List subProviders) {
      this.subProviders = subProviders;
      this.requiredTables = requiredTables;
   }

   public void run(final BootstrapContext context) {
      Map randomSequenceSeeds = new Object2ObjectOpenHashMap();
      HolderGetter lootTables = context.lookup(Registries.LOOT_TABLE);
      this.requiredTables.forEach(lootTables::get);
      this.subProviders.forEach((subProvider) -> subProvider.bootstrap().create(new LootTableSubProvider.Context(this) {
            {
               Objects.requireNonNull(this$0);
            }

            public Holder.Reference accept(final ResourceKey key, final LootTable.Builder lootTable) {
               Identifier sequenceId = LootTableProvider.sequenceIdForLootTable(key);
               Identifier previous = (Identifier)randomSequenceSeeds.put(RandomSequence.seedForKey(sequenceId), sequenceId);
               if (previous != null) {
                  Util.logAndPauseIfInIde("Loot table random sequence seed collision on " + String.valueOf(previous) + " and " + String.valueOf(key.identifier()));
               }

               LootTable table = lootTable.setRandomSequence(sequenceId).setParamSet(subProvider.paramSet).build();
               return context.register(key, table);
            }

            public HolderGetter lookup(final ResourceKey key) {
               return context.lookup(key);
            }

            /** @deprecated */
            @Deprecated
            public Stream listContextElements(final ResourceKey key) {
               return context.listContextElements(key);
            }
         }).run());
   }

   private static Identifier sequenceIdForLootTable(final ResourceKey id) {
      return id.identifier();
   }

   public static record SubProviderEntry(LootTableSubProvider.Factory bootstrap, ContextKeySet paramSet) {
   }
}
