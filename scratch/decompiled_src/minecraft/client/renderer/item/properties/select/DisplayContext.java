package net.minecraft.client.renderer.item.properties.select;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public record DisplayContext() implements SelectItemModelProperty {
   public static final Codec VALUE_CODEC = ItemDisplayContext.CODEC;
   public static final SelectItemModelProperty.Type TYPE = SelectItemModelProperty.Type.create(MapCodec.unit(new DisplayContext()), VALUE_CODEC);

   public ItemDisplayContext get(final ItemStack itemStack, final @Nullable ClientLevel level, final @Nullable LivingEntity owner, final int seed, final ItemDisplayContext displayContext) {
      return displayContext;
   }

   public SelectItemModelProperty.Type type() {
      return TYPE;
   }

   public Codec valueCodec() {
      return VALUE_CODEC;
   }
}
