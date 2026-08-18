package net.minecraft.world.item.slot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderSet;

public class GroupSlotSource extends CompositeSlotSource {
   public static final MapCodec MAP_CODEC = createCodec(GroupSlotSource::new);
   public static final Codec INLINE_CODEC = createInlineCodec(GroupSlotSource::new);

   private GroupSlotSource(final HolderSet terms) {
      super(terms);
   }

   public MapCodec codec() {
      return MAP_CODEC;
   }
}
