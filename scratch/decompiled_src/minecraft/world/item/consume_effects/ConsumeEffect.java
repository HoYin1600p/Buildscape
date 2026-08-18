package net.minecraft.world.item.consume_effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface ConsumeEffect {
   Codec CODEC = BuiltInRegistries.CONSUME_EFFECT_TYPE.byNameCodec().dispatch(ConsumeEffect::getType, ConsumeEffect.Type::codec);
   StreamCodec STREAM_CODEC = ByteBufCodecs.registry(Registries.CONSUME_EFFECT_TYPE).dispatch(ConsumeEffect::getType, ConsumeEffect.Type::streamCodec);

   ConsumeEffect.Type getType();

   boolean apply(final Level level, final ItemStack stack, final LivingEntity user);

   public static record Type(MapCodec codec, StreamCodec streamCodec) {
      public static final ConsumeEffect.Type APPLY_EFFECTS = register("apply_effects", ApplyStatusEffectsConsumeEffect.CODEC, ApplyStatusEffectsConsumeEffect.STREAM_CODEC);
      public static final ConsumeEffect.Type REMOVE_EFFECTS = register("remove_effects", RemoveStatusEffectsConsumeEffect.CODEC, RemoveStatusEffectsConsumeEffect.STREAM_CODEC);
      public static final ConsumeEffect.Type CLEAR_ALL_EFFECTS = register("clear_all_effects", ClearAllStatusEffectsConsumeEffect.CODEC, ClearAllStatusEffectsConsumeEffect.STREAM_CODEC);
      public static final ConsumeEffect.Type TELEPORT_RANDOMLY = register("teleport_randomly", TeleportRandomlyConsumeEffect.CODEC, TeleportRandomlyConsumeEffect.STREAM_CODEC);
      public static final ConsumeEffect.Type PLAY_SOUND = register("play_sound", PlaySoundConsumeEffect.CODEC, PlaySoundConsumeEffect.STREAM_CODEC);

      private static ConsumeEffect.Type register(final String name, final MapCodec codec, final StreamCodec streamCodec) {
         return (ConsumeEffect.Type)Registry.register(BuiltInRegistries.CONSUME_EFFECT_TYPE, name, new ConsumeEffect.Type(codec, streamCodec));
      }
   }
}
