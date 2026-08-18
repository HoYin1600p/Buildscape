package net.minecraft.world.item.slot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.core.Holder;

public class FilteredSlotSource extends TransformedSlotSource {
   public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> commonFields(i).and(ItemPredicate.CODEC.fieldOf("item_filter").forGetter((t) -> t.filter)).apply(i, FilteredSlotSource::new));
   private final ItemPredicate filter;

   private FilteredSlotSource(final Holder slotSource, final ItemPredicate filter) {
      super(slotSource);
      this.filter = filter;
   }

   public MapCodec codec() {
      return MAP_CODEC;
   }

   protected SlotCollection transform(final SlotCollection slots) {
      return slots.filter(this.filter);
   }
}
