package net.minecraft.network.chat.numbers;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;

public class BlankFormat implements NumberFormat {
   public static final BlankFormat INSTANCE = new BlankFormat();
   public static final NumberFormatType TYPE = new NumberFormatType() {
      private static final MapCodec CODEC = MapCodec.unit(BlankFormat.INSTANCE);
      private static final StreamCodec STREAM_CODEC = StreamCodec.unit(BlankFormat.INSTANCE);

      public MapCodec mapCodec() {
         return CODEC;
      }

      public StreamCodec streamCodec() {
         return STREAM_CODEC;
      }
   };

   private BlankFormat() {
   }

   public MutableComponent format(final int value) {
      return Component.empty();
   }

   public NumberFormatType type() {
      return TYPE;
   }
}
