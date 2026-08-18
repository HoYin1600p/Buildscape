package net.minecraft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Keyframe(int ticks, Object value) {
   public static Codec codec(final Codec valueCodec) {
      return RecordCodecBuilder.create((i) -> i.group(ExtraCodecs.NON_NEGATIVE_INT.fieldOf("ticks").forGetter(Keyframe::ticks), valueCodec.fieldOf("value").forGetter(Keyframe::value)).apply(i, Keyframe::new));
   }
}
