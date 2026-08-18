package net.minecraft.advancements.predicates;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.Nullable;

public record MobEffectsPredicate(Map effectMap) implements Predicate {
   public static final Codec CODEC = Codec.unboundedMap(MobEffect.CODEC, MobEffectsPredicate.MobEffectInstancePredicate.CODEC).xmap(MobEffectsPredicate::new, MobEffectsPredicate::effectMap);
   public static final StreamCodec STREAM_CODEC = MobEffectsPredicate.MobEffectInstancePredicate.MAP_STREAM_CODEC.map(MobEffectsPredicate::new, MobEffectsPredicate::effectMap);

   public boolean matches(final Entity entity) {
      if (entity instanceof LivingEntity living) {
         if (this.matches(living.getActiveEffectsMap())) {
            return true;
         }
      }

      return false;
   }

   public boolean matches(final LivingEntity entity) {
      return this.matches(entity.getActiveEffectsMap());
   }

   public boolean matches(final Map effects) {
      for(Map.Entry entry : this.effectMap.entrySet()) {
         MobEffectInstance instance = (MobEffectInstance)effects.get(entry.getKey());
         if (!((MobEffectsPredicate.MobEffectInstancePredicate)entry.getValue()).matches(instance)) {
            return false;
         }
      }

      return true;
   }

   public boolean test(final MobEffectInstance mobEffect) {
      MobEffectsPredicate.MobEffectInstancePredicate predicate = (MobEffectsPredicate.MobEffectInstancePredicate)this.effectMap.get(mobEffect.getEffect());
      return predicate != null && predicate.matches(mobEffect);
   }

   public static class Builder {
      private final ImmutableMap.Builder effectMap = ImmutableMap.builder();

      public static MobEffectsPredicate.Builder effects() {
         return new MobEffectsPredicate.Builder();
      }

      public MobEffectsPredicate.Builder and(final Holder effect) {
         this.effectMap.put(effect, new MobEffectsPredicate.MobEffectInstancePredicate());
         return this;
      }

      public MobEffectsPredicate.Builder and(final Holder effect, final MobEffectsPredicate.MobEffectInstancePredicate predicate) {
         this.effectMap.put(effect, predicate);
         return this;
      }

      public MobEffectsPredicate build() {
         return new MobEffectsPredicate(this.effectMap.build());
      }
   }

   public static record MobEffectInstancePredicate(MinMaxBounds.Ints amplifier, MinMaxBounds.Ints duration, Optional ambient, Optional visible) {
      public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(MinMaxBounds.Ints.CODEC.optionalFieldOf("amplifier", MinMaxBounds.Ints.ANY).forGetter(MobEffectsPredicate.MobEffectInstancePredicate::amplifier), MinMaxBounds.Ints.CODEC.optionalFieldOf("duration", MinMaxBounds.Ints.ANY).forGetter(MobEffectsPredicate.MobEffectInstancePredicate::duration), Codec.BOOL.optionalFieldOf("ambient").forGetter(MobEffectsPredicate.MobEffectInstancePredicate::ambient), Codec.BOOL.optionalFieldOf("visible").forGetter(MobEffectsPredicate.MobEffectInstancePredicate::visible)).apply(i, MobEffectsPredicate.MobEffectInstancePredicate::new));
      public static final StreamCodec STREAM_CODEC = StreamCodec.composite(MinMaxBounds.Ints.STREAM_CODEC, MobEffectsPredicate.MobEffectInstancePredicate::amplifier, MinMaxBounds.Ints.STREAM_CODEC, MobEffectsPredicate.MobEffectInstancePredicate::duration, ByteBufCodecs.optional(ByteBufCodecs.BOOL), MobEffectsPredicate.MobEffectInstancePredicate::ambient, ByteBufCodecs.optional(ByteBufCodecs.BOOL), MobEffectsPredicate.MobEffectInstancePredicate::visible, MobEffectsPredicate.MobEffectInstancePredicate::new);
      public static final StreamCodec MAP_STREAM_CODEC = ByteBufCodecs.map(HashMap::new, MobEffect.STREAM_CODEC, STREAM_CODEC);

      public MobEffectInstancePredicate() {
         this(MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, Optional.empty(), Optional.empty());
      }

      public boolean matches(final @Nullable MobEffectInstance instance) {
         if (instance == null) {
            return false;
         } else if (!this.amplifier.matches(instance.getAmplifier())) {
            return false;
         } else if (!this.duration.matches(instance.getDuration())) {
            return false;
         } else if (this.ambient.isPresent() && this.ambient.get() != instance.isAmbient()) {
            return false;
         } else {
            return !this.visible.isPresent() || this.visible.get() == instance.isVisible();
         }
      }
   }
}
