package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Util;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import org.jspecify.annotations.Nullable;

public record BlockItemStateProperties(Map properties) implements TooltipProvider {
   public static final BlockItemStateProperties EMPTY = new BlockItemStateProperties(Map.of());
   public static final Codec CODEC = Codec.unboundedMap(Codec.STRING, Codec.STRING).xmap(BlockItemStateProperties::new, BlockItemStateProperties::properties);
   private static final StreamCodec PROPERTIES_STREAM_CODEC = ByteBufCodecs.map(Object2ObjectOpenHashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.STRING_UTF8);
   public static final StreamCodec STREAM_CODEC = PROPERTIES_STREAM_CODEC.map(BlockItemStateProperties::new, BlockItemStateProperties::properties);

   public BlockItemStateProperties with(final Property property, final Comparable value) {
      return new BlockItemStateProperties(Util.copyAndPut(this.properties, property.getName(), property.getName(value)));
   }

   public BlockItemStateProperties with(final Property property, final BlockState state) {
      return this.with(property, state.getValue(property));
   }

   public @Nullable Comparable get(final Property property) {
      String value = (String)this.properties.get(property.getName());
      return value == null ? null : (Comparable)property.getValue(value).orElse((Object)null);
   }

   public BlockState apply(BlockState state) {
      StateDefinition stateDefinition = state.getBlock().getStateDefinition();

      for(Map.Entry entry : this.properties.entrySet()) {
         Property property = stateDefinition.getProperty((String)entry.getKey());
         if (property != null) {
            state = updateState(state, property, (String)entry.getValue());
         }
      }

      return state;
   }

   private static BlockState updateState(final BlockState state, final Property property, final String value) {
      return (BlockState)property.getValue(value).map((v) -> (BlockState)state.setValue(property, v)).orElse(state);
   }

   public boolean isEmpty() {
      return this.properties.isEmpty();
   }

   public void addToTooltip(final Item.TooltipContext context, final Consumer consumer, final TooltipFlag flag, final DataComponentGetter components) {
      Integer honeyLevel = (Integer)this.get(BeehiveBlock.HONEY_LEVEL);
      if (honeyLevel != null) {
         consumer.accept(Component.translatable("container.beehive.honey", honeyLevel, 5).withStyle(ChatFormatting.GRAY));
      }

   }
}
