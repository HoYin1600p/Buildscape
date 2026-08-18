package net.minecraft.client.renderer.item.properties.select;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public record ContextDimension() implements SelectItemModelProperty {
   public static final Codec VALUE_CODEC = ResourceKey.codec(Registries.DIMENSION);
   public static final SelectItemModelProperty.Type TYPE = SelectItemModelProperty.Type.create(MapCodec.unit(new ContextDimension()), VALUE_CODEC);

   public @Nullable ResourceKey get(final ItemStack itemStack, final @Nullable ClientLevel level, final @Nullable LivingEntity owner, final int seed, final ItemDisplayContext displayContext) {
      return level != null ? level.dimension() : null;
   }

   public SelectItemModelProperty.Type type() {
      return TYPE;
   }

   public Codec valueCodec() {
      return VALUE_CODEC;
   }
}
