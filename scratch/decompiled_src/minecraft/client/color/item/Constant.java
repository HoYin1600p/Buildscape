package net.minecraft.client.color.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public record Constant(int value) implements ItemTintSource {
   public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(ExtraCodecs.RGB_COLOR_CODEC.fieldOf("value").forGetter(Constant::value)).apply(i, Constant::new));

   public Constant {
      value = ARGB.opaque(value);
   }

   public int calculate(final ItemStack itemStack, final @Nullable ClientLevel level, final @Nullable LivingEntity owner) {
      return this.value;
   }

   public MapCodec type() {
      return MAP_CODEC;
   }
}
