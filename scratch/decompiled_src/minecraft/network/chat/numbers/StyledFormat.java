package net.minecraft.network.chat.numbers;

import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.StreamCodec;

public record StyledFormat(Style style) implements NumberFormat {
   public static final NumberFormatType TYPE = new NumberFormatType() {
      private static final MapCodec CODEC = Style.Serializer.MAP_CODEC.xmap(StyledFormat::new, StyledFormat::style);
      private static final StreamCodec STREAM_CODEC = StreamCodec.composite(Style.Serializer.TRUSTED_STREAM_CODEC, StyledFormat::style, StyledFormat::new);

      public MapCodec mapCodec() {
         return CODEC;
      }

      public StreamCodec streamCodec() {
         return STREAM_CODEC;
      }
   };
   public static final StyledFormat NO_STYLE = new StyledFormat(Style.EMPTY);
   public static final StyledFormat SIDEBAR_DEFAULT = new StyledFormat(Style.EMPTY.withColor(ChatFormatting.RED));
   public static final StyledFormat PLAYER_LIST_DEFAULT = new StyledFormat(Style.EMPTY.withColor(ChatFormatting.YELLOW));

   public MutableComponent format(final int value) {
      return Component.literal(Integer.toString(value)).withStyle(this.style);
   }

   public NumberFormatType type() {
      return TYPE;
   }
}
