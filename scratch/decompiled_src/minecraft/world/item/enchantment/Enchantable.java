package net.minecraft.world.item.enchantment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

public record Enchantable(int value) {
   public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(ExtraCodecs.POSITIVE_INT.fieldOf("value").forGetter(Enchantable::value)).apply(i, Enchantable::new));
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, Enchantable::value, Enchantable::new);

   public Enchantable {
      if (value <= 0) {
         throw new IllegalArgumentException("Enchantment value must be positive, but was " + value);
      }
   }
}
