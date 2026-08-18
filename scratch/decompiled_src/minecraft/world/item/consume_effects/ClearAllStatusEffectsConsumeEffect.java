package net.minecraft.world.item.consume_effects;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public record ClearAllStatusEffectsConsumeEffect() implements ConsumeEffect {
   public static final ClearAllStatusEffectsConsumeEffect INSTANCE = new ClearAllStatusEffectsConsumeEffect();
   public static final MapCodec CODEC = MapCodec.unit(INSTANCE);
   public static final StreamCodec STREAM_CODEC = StreamCodec.unit(INSTANCE);

   public ConsumeEffect.Type getType() {
      return ConsumeEffect.Type.CLEAR_ALL_EFFECTS;
   }

   public boolean apply(final Level level, final ItemStack stack, final LivingEntity user) {
      return user.removeAllEffects();
   }
}
