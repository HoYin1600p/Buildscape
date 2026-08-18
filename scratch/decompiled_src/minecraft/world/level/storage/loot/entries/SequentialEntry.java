package net.minecraft.world.level.storage.loot.entries;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SequentialEntry extends CompositeEntryBase {
   public static final MapCodec MAP_CODEC = createCodec(SequentialEntry::new);

   public SequentialEntry(final List children, final Optional condition, final Optional modifier) {
      super(children, condition, modifier);
   }

   public MapCodec codec() {
      return MAP_CODEC;
   }

   protected ComposableEntryContainer compose(final List entries) {
      ComposableEntryContainer var10000;
      switch (entries.size()) {
         case 0:
            var10000 = ALWAYS_TRUE;
            break;
         case 1:
            var10000 = (ComposableEntryContainer)entries.get(0);
            break;
         case 2:
            var10000 = ((ComposableEntryContainer)entries.get(0)).and((ComposableEntryContainer)entries.get(1));
            break;
         default:
            var10000 = (context, output) -> {
               for(ComposableEntryContainer entry : entries) {
                  if (!entry.expand(context, output)) {
                     return false;
                  }
               }

               return true;
            };
      }

      return var10000;
   }

   public static SequentialEntry.Builder sequential(final LootPoolEntryContainer.Builder... entries) {
      return new SequentialEntry.Builder(entries);
   }

   public static class Builder extends CompositeEntryBase.Builder {
      public Builder(final LootPoolEntryContainer.Builder... entries) {
         super(entries);
      }

      protected SequentialEntry.Builder getThis() {
         return this;
      }

      public SequentialEntry.Builder then(final LootPoolEntryContainer.Builder other) {
         this.addEntry(other);
         return this;
      }

      public LootPoolEntryContainer build() {
         return this.build(SequentialEntry::new);
      }
   }
}
