package net.minecraft.util.debug;

import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

public class DebugSubscription {
   public static final int DOES_NOT_EXPIRE = 0;
   private final @Nullable StreamCodec valueStreamCodec;
   private final int expireAfterTicks;

   public DebugSubscription(final @Nullable StreamCodec valueStreamCodec, final int expireAfterTicks) {
      this.valueStreamCodec = valueStreamCodec;
      this.expireAfterTicks = expireAfterTicks;
   }

   public DebugSubscription(final @Nullable StreamCodec valueStreamCodec) {
      this(valueStreamCodec, 0);
   }

   public DebugSubscription.Update packUpdate(final @Nullable Object value) {
      return new DebugSubscription.Update(this, Optional.ofNullable(value));
   }

   public DebugSubscription.Update emptyUpdate() {
      return new DebugSubscription.Update(this, Optional.empty());
   }

   public DebugSubscription.Event packEvent(final Object value) {
      return new DebugSubscription.Event(this, value);
   }

   public String toString() {
      return Util.getRegisteredName(BuiltInRegistries.DEBUG_SUBSCRIPTION, this);
   }

   public @Nullable StreamCodec valueStreamCodec() {
      return this.valueStreamCodec;
   }

   public int expireAfterTicks() {
      return this.expireAfterTicks;
   }

   public static record Event(DebugSubscription subscription, Object value) {
      public static final StreamCodec STREAM_CODEC = ByteBufCodecs.registry(Registries.DEBUG_SUBSCRIPTION).dispatch(DebugSubscription.Event::subscription, DebugSubscription.Event::streamCodec);

      private static StreamCodec streamCodec(final DebugSubscription subscription) {
         return ((StreamCodec)Objects.requireNonNull(subscription.valueStreamCodec)).map((value) -> new DebugSubscription.Event(subscription, value), DebugSubscription.Event::value);
      }
   }

   public static record Update(DebugSubscription subscription, Optional value) {
      public static final StreamCodec STREAM_CODEC = ByteBufCodecs.registry(Registries.DEBUG_SUBSCRIPTION).dispatch(DebugSubscription.Update::subscription, DebugSubscription.Update::streamCodec);

      private static StreamCodec streamCodec(final DebugSubscription subscription) {
         return ByteBufCodecs.optional((StreamCodec)Objects.requireNonNull(subscription.valueStreamCodec)).map((value) -> new DebugSubscription.Update(subscription, value), DebugSubscription.Update::value);
      }
   }
}
