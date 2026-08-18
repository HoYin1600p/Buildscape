package net.minecraft.network.chat.numbers;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;

public record FixedFormat(Component value) implements NumberFormat {
   public static final NumberFormatType TYPE = new NumberFormatType() {
      private static final MapCodec CODEC = ComponentSerialization.CODEC.fieldOf("value").xmap(FixedFormat::new, FixedFormat::value);
      private static final StreamCodec STREAM_CODEC = StreamCodec.composite(ComponentSerialization.TRUSTED_STREAM_CODEC, FixedFormat::value, FixedFormat::new);

      public MapCodec mapCodec() {
         return CODEC;
      }

      public StreamCodec streamCodec() {
         return STREAM_CODEC;
      }
   };

   public MutableComponent format(final int value) {
      return this.value.copy();
   }

   public NumberFormatType type() {
      return TYPE;
   }
}
