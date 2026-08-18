package net.minecraft.world.entity.ai.memory;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;

public record ExpirableValue(Object value, Optional timeToLive) {
   public static ExpirableValue of(final Object value) {
      return new ExpirableValue(value, Optional.empty());
   }

   public static ExpirableValue of(final Object value, final long ticksUntilExpiry) {
      return new ExpirableValue(value, Optional.of(ticksUntilExpiry));
   }

   public String toString() {
      return String.valueOf(this.value) + (this.timeToLive.isPresent() ? " (ttl: " + String.valueOf(this.timeToLive.get()) + ")" : "");
   }

   public static Codec codec(final Codec valueCodec) {
      return RecordCodecBuilder.create((i) -> i.group(valueCodec.fieldOf("value").forGetter(ExpirableValue::value), Codec.LONG.lenientOptionalFieldOf("ttl").forGetter(ExpirableValue::timeToLive)).apply(i, ExpirableValue::new));
   }
}
