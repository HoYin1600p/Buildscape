package net.minecraft.sounds;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public record SoundEvent(Identifier location, Optional fixedRange) {
   public static final Codec DIRECT_CODEC = RecordCodecBuilder.create((i) -> i.group(Identifier.CODEC.fieldOf("sound_id").forGetter(SoundEvent::location), Codec.FLOAT.lenientOptionalFieldOf("range").forGetter(SoundEvent::fixedRange)).apply(i, SoundEvent::create));
   public static final Codec CODEC = RegistryCodecs.holder(Registries.SOUND_EVENT, DIRECT_CODEC);
   public static final StreamCodec DIRECT_STREAM_CODEC = StreamCodec.composite(Identifier.STREAM_CODEC, SoundEvent::location, ByteBufCodecs.FLOAT.apply(ByteBufCodecs::optional), SoundEvent::fixedRange, SoundEvent::create);
   public static final StreamCodec STREAM_CODEC = ByteBufCodecs.holder(Registries.SOUND_EVENT, DIRECT_STREAM_CODEC);

   private static SoundEvent create(final Identifier location, final Optional range) {
      return (SoundEvent)range.map((r) -> createFixedRangeEvent(location, r)).orElseGet(() -> createVariableRangeEvent(location));
   }

   public static SoundEvent createVariableRangeEvent(final Identifier location) {
      return new SoundEvent(location, Optional.empty());
   }

   public static SoundEvent createFixedRangeEvent(final Identifier location, final float range) {
      return new SoundEvent(location, Optional.of(range));
   }

   public float getRange(final float volume) {
      return this.fixedRange.orElse(volume > 1.0F ? 16.0F * volume : 16.0F);
   }
}
