package net.minecraft.world.entity.animal.cow;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvent;

public record CowSoundVariant(Holder ambientSound, Holder hurtSound, Holder deathSound, Holder stepSound) {
   public static final Codec DIRECT_CODEC = codec();
   public static final Codec NETWORK_CODEC = codec();
   public static final Codec CODEC = RegistryCodecs.holder(Registries.COW_SOUND_VARIANT);
   public static final StreamCodec STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.COW_SOUND_VARIANT);

   private static Codec codec() {
      return RecordCodecBuilder.create((i) -> i.group(SoundEvent.CODEC.fieldOf("ambient_sound").forGetter(CowSoundVariant::ambientSound), SoundEvent.CODEC.fieldOf("hurt_sound").forGetter(CowSoundVariant::hurtSound), SoundEvent.CODEC.fieldOf("death_sound").forGetter(CowSoundVariant::deathSound), SoundEvent.CODEC.fieldOf("step_sound").forGetter(CowSoundVariant::stepSound)).apply(i, CowSoundVariant::new));
   }
}
