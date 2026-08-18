package net.minecraft.world.entity.animal.pig;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvent;

public record PigSoundVariant(PigSoundVariant.PigSoundSet adultSounds, PigSoundVariant.PigSoundSet babySounds) {
   public static final Codec DIRECT_CODEC = codec();
   public static final Codec NETWORK_CODEC = codec();
   public static final Codec CODEC = RegistryCodecs.holder(Registries.PIG_SOUND_VARIANT);
   public static final StreamCodec STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.PIG_SOUND_VARIANT);

   private static Codec codec() {
      return RecordCodecBuilder.create((i) -> i.group(PigSoundVariant.PigSoundSet.CODEC.fieldOf("adult_sounds").forGetter(PigSoundVariant::adultSounds), PigSoundVariant.PigSoundSet.CODEC.fieldOf("baby_sounds").forGetter(PigSoundVariant::babySounds)).apply(i, PigSoundVariant::new));
   }

   public static record PigSoundSet(Holder ambientSound, Holder hurtSound, Holder deathSound, Holder stepSound, Holder eatSound) {
      public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(SoundEvent.CODEC.fieldOf("ambient_sound").forGetter(PigSoundVariant.PigSoundSet::ambientSound), SoundEvent.CODEC.fieldOf("hurt_sound").forGetter(PigSoundVariant.PigSoundSet::hurtSound), SoundEvent.CODEC.fieldOf("death_sound").forGetter(PigSoundVariant.PigSoundSet::deathSound), SoundEvent.CODEC.fieldOf("step_sound").forGetter(PigSoundVariant.PigSoundSet::stepSound), SoundEvent.CODEC.fieldOf("eat_sound").forGetter(PigSoundVariant.PigSoundSet::eatSound)).apply(i, PigSoundVariant.PigSoundSet::new));
   }
}
