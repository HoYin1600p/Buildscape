package net.minecraft.client.renderer.item.properties.select;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import org.jspecify.annotations.Nullable;

public record ItemBlockState(String property) implements SelectItemModelProperty {
   public static final PrimitiveCodec VALUE_CODEC = Codec.STRING;
   public static final SelectItemModelProperty.Type TYPE = SelectItemModelProperty.Type.create(RecordCodecBuilder.mapCodec((i) -> i.group(Codec.STRING.fieldOf("block_state_property").forGetter(ItemBlockState::property)).apply(i, ItemBlockState::new)), VALUE_CODEC);

   public @Nullable String get(final ItemStack itemStack, final @Nullable ClientLevel level, final @Nullable LivingEntity owner, final int seed, final ItemDisplayContext displayContext) {
      BlockItemStateProperties blockItemStateProperties = (BlockItemStateProperties)itemStack.get(DataComponents.BLOCK_STATE);
      return blockItemStateProperties == null ? null : (String)blockItemStateProperties.properties().get(this.property);
   }

   public SelectItemModelProperty.Type type() {
      return TYPE;
   }

   public Codec valueCodec() {
      return VALUE_CODEC;
   }
}
