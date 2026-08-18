package net.minecraft.world.entity.animal.chicken;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvent;

public record ChickenSoundVariant(ChickenSoundVariant.ChickenSoundSet adultSounds, ChickenSoundVariant.ChickenSoundSet babySounds) {
   public static final Codec DIRECT_CODEC = codec();
   public static final Codec NETWORK_CODEC = codec();
   public static final Codec CODEC = RegistryCodecs.holder(Registries.CHICKEN_SOUND_VARIANT);
   public static final StreamCodec STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.CHICKEN_SOUND_VARIANT);

   private static Codec codec() {
      return RecordCodecBuilder.create((i) -> i.group(ChickenSoundVariant.ChickenSoundSet.CODEC.fieldOf("adult_sounds").forGetter(ChickenSoundVariant::adultSounds), ChickenSoundVariant.ChickenSoundSet.CODEC.fieldOf("baby_sounds").forGetter(ChickenSoundVariant::babySounds)).apply(i, ChickenSoundVariant::new));
   }

   public static record ChickenSoundSet(Holder ambientSound, Holder hurtSound, Holder deathSound, Holder stepSound) {
      private static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(SoundEvent.CODEC.fieldOf("ambient_sound").forGetter(ChickenSoundVariant.ChickenSoundSet::ambientSound), SoundEvent.CODEC.fieldOf("hurt_sound").forGetter(ChickenSoundVariant.ChickenSoundSet::hurtSound), SoundEvent.CODEC.fieldOf("death_sound").forGetter(ChickenSoundVariant.ChickenSoundSet::deathSound), SoundEvent.CODEC.fieldOf("step_sound").forGetter(ChickenSoundVariant.ChickenSoundSet::stepSound)).apply(i, ChickenSoundVariant.ChickenSoundSet::new));
   }
}
