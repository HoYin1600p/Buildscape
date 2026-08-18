package net.minecraft.world.item.consume_effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public record PlaySoundConsumeEffect(Holder sound) implements ConsumeEffect {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(SoundEvent.CODEC.fieldOf("sound").forGetter(PlaySoundConsumeEffect::sound)).apply(i, PlaySoundConsumeEffect::new));
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(SoundEvent.STREAM_CODEC, PlaySoundConsumeEffect::sound, PlaySoundConsumeEffect::new);

   public ConsumeEffect.Type getType() {
      return ConsumeEffect.Type.PLAY_SOUND;
   }

   public boolean apply(final Level level, final ItemStack stack, final LivingEntity user) {
      level.playSound((Entity)null, user.blockPosition(), (SoundEvent)this.sound.value(), user.getSoundSource(), 1.0F, 1.0F);
      return true;
   }
}
