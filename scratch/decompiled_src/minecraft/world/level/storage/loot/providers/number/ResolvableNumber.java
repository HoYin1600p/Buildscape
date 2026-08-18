package net.minecraft.world.level.storage.loot.providers.number;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

public sealed interface ResolvableNumber {
   Codec CODEC = Codec.either(ResolvableNumber.Constant.CODEC, ResolvableNumber.Reference.CODEC).xmap(Either::unwrap, ResolvableNumber::wrap);
   StreamCodec STREAM_CODEC = ByteBufCodecs.either(ResolvableNumber.Constant.STREAM_CODEC, ResolvableNumber.Reference.STREAM_CODEC).map(Either::unwrap, ResolvableNumber::wrap);

   private static Either wrap(final ResolvableNumber resolvableNumber) {
      Objects.requireNonNull(resolvableNumber);
      byte var2 = 0;
      Either var10000;
      switch (resolvableNumber.typeSwitch<invokedynamic>(resolvableNumber, var2)) {
         case 0:
            ResolvableNumber.Constant constant = (ResolvableNumber.Constant)resolvableNumber;
            var10000 = Either.left(constant);
            break;
         case 1:
            ResolvableNumber.Reference reference = (ResolvableNumber.Reference)resolvableNumber;
            var10000 = Either.right(reference);
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   float getFloat(LootContext context, float defaultValue);

   int getInt(LootContext context, int defaultValue);

   static ResolvableNumber fromKey(final ResourceKey key) {
      return new ResolvableNumber.Reference(key);
   }

   static float getFloatFromItem(final ItemStack itemStack, final DataComponentType componentType, final Function getter, final LootContext context, final float defaultValue) {
      Object component = (T)itemStack.get(componentType);
      return component != null ? ((ResolvableNumber)getter.apply(component)).getFloat(context, defaultValue) : defaultValue;
   }

   static int getIntFromItem(final ItemStack itemStack, final DataComponentType componentType, final Function getter, final LootContext context, final int defaultValue) {
      Object component = (T)itemStack.get(componentType);
      return component != null ? ((ResolvableNumber)getter.apply(component)).getInt(context, defaultValue) : defaultValue;
   }

   public static record Constant(float value) implements ResolvableNumber {
      private static final Codec CODEC = Codec.FLOAT.xmap(ResolvableNumber.Constant::new, ResolvableNumber.Constant::value);
      private static final StreamCodec STREAM_CODEC = ByteBufCodecs.FLOAT.map(ResolvableNumber.Constant::new, ResolvableNumber.Constant::value);

      public float getFloat(final LootContext context, final float defaultValue) {
         return this.value;
      }

      public int getInt(final LootContext context, final int defaultValue) {
         return Math.round(this.value);
      }
   }

   public static record Reference(ResourceKey key) implements ResolvableNumber {
      private static final Codec CODEC = ResourceKey.codec(Registries.NUMBER_PROVIDER).xmap(ResolvableNumber.Reference::new, ResolvableNumber.Reference::key);
      private static final StreamCodec STREAM_CODEC = ResourceKey.streamCodec(Registries.NUMBER_PROVIDER).map(ResolvableNumber.Reference::new, ResolvableNumber.Reference::key);

      public float getFloat(final LootContext context, final float defaultValue) {
         return this.getProvider(context).map((provider) -> provider.getFloat(context)).orElse(defaultValue);
      }

      public int getInt(final LootContext context, final int defaultValue) {
         return this.getProvider(context).map((provider) -> provider.getInt(context)).orElse(defaultValue);
      }

      private Optional getProvider(final LootContext context) {
         return context.getResolver().lookupOrThrow(Registries.NUMBER_PROVIDER).get(this.key).map(Holder.Reference::value);
      }
   }
}
