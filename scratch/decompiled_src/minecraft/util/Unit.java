package net.minecraft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.codec.StreamCodec;

public enum Unit {
   INSTANCE;

   public static final Codec CODEC = MapCodec.unitCodec(INSTANCE);
   public static final StreamCodec STREAM_CODEC = StreamCodec.unit(INSTANCE);

   // $FF: synthetic method
   private static Unit[] $values() {
      return new Unit[]{INSTANCE};
   }
}
