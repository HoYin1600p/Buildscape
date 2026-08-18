package net.minecraft.world.level.storage.loot.entries;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class TagEntry extends ExpandableContainerBase {
   public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(RegistryCodecs.holderSet(Registries.ITEM).fieldOf("items").forGetter((e) -> e.tag)).and(expandableFields(i)).apply(i, TagEntry::new));
   private final HolderSet tag;

   private TagEntry(final HolderSet tag, final boolean expand, final int weight, final int quality, final Optional condition, final Optional modifier) {
      super(expand, weight, quality, condition, modifier);
      this.tag = tag;
   }

   public MapCodec codec() {
      return MAP_CODEC;
   }

   protected boolean addExpandedEntries(final Consumer output) {
      for(final Holder item : this.tag) {
         output.accept(new UniformContainerBase.EntryBase(this) {
            {
               Objects.requireNonNull(this$0);
            }

            public void createItemStack(final Consumer output, final LootContext context) {
               output.accept(new ItemStack(item));
            }
         });
      }

      return true;
   }

   protected boolean addUnexpandedEntry(final Consumer output) {
      output.accept(new UniformContainerBase.EntryBase() {
         {
            Objects.requireNonNull(TagEntry.this);
         }

         public void createItemStack(final Consumer output, final LootContext context) {
            TagEntry.this.tag.forEach((item) -> output.accept(new ItemStack(item)));
         }
      });
      return true;
   }

   public static UniformContainerBase.Builder tagContents(final HolderSet tag) {
      return simpleBuilder((weight, quality, conditions, functions) -> new TagEntry(tag, false, weight, quality, conditions, functions));
   }

   public static UniformContainerBase.Builder expandTag(final HolderSet tag) {
      return simpleBuilder((weight, quality, conditions, functions) -> new TagEntry(tag, true, weight, quality, conditions, functions));
   }
}
