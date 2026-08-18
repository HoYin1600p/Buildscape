package net.minecraft.world.level.saveddata.maps;

import com.mojang.serialization.Codec;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.MapPostProcessing;
import net.minecraft.world.item.component.TooltipProvider;

public record MapId(int id) implements TooltipProvider {
   public static final Codec CODEC = Codec.INT.xmap(MapId::new, MapId::id);
   public static final StreamCodec STREAM_CODEC = ByteBufCodecs.VAR_INT.map(MapId::new, MapId::id);
   private static final Component LOCKED_TEXT = Component.translatable("filled_map.locked").withStyle(ChatFormatting.GRAY);

   public String key() {
      return "maps/" + this.id;
   }

   public void addToTooltip(final Item.TooltipContext context, final Consumer consumer, final TooltipFlag flag, final DataComponentGetter components) {
      MapItemSavedData data = context.mapData(this);
      if (data == null) {
         consumer.accept(Component.translatable("filled_map.unknown").withStyle(ChatFormatting.GRAY));
      } else {
         MapPostProcessing postProcessing = (MapPostProcessing)components.get(DataComponents.MAP_POST_PROCESSING);
         if (components.get(DataComponents.CUSTOM_NAME) == null && postProcessing == null) {
            consumer.accept(Component.translatable("filled_map.id", this.id).withStyle(ChatFormatting.GRAY));
         }

         if (data.locked || postProcessing == MapPostProcessing.LOCK) {
            consumer.accept(LOCKED_TEXT);
         }

         if (flag.isAdvanced()) {
            int scaleToAdd = postProcessing == MapPostProcessing.SCALE ? 1 : 0;
            int scale = Math.min(data.scale + scaleToAdd, 4);
            consumer.accept(Component.translatable("filled_map.scale", 1 << scale).withStyle(ChatFormatting.GRAY));
            consumer.accept(Component.translatable("filled_map.level", scale, 4).withStyle(ChatFormatting.GRAY));
         }

      }
   }
}
