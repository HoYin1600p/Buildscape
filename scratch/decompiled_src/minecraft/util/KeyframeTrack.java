package net.minecraft.util;

import com.google.common.collect.Comparators;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.attribute.LerpFunction;

public record KeyframeTrack(List keyframes, EasingType easingType) {
   public KeyframeTrack {
      if (keyframes.isEmpty()) {
         throw new IllegalArgumentException("Track has no keyframes");
      }
   }

   public static MapCodec mapCodec(final Codec valueCodec) {
      Codec keyframesCodec = Keyframe.codec(valueCodec).listOf().validate(KeyframeTrack::validateKeyframes);
      return RecordCodecBuilder.mapCodec((i) -> i.group(keyframesCodec.fieldOf("keyframes").forGetter(KeyframeTrack::keyframes), EasingType.CODEC.optionalFieldOf("ease", EasingType.LINEAR).forGetter(KeyframeTrack::easingType)).apply(i, KeyframeTrack::new));
   }

   private static DataResult validateKeyframes(final List keyframes) {
      if (keyframes.isEmpty()) {
         return DataResult.error(() -> "Keyframes must not be empty");
      } else if (!Comparators.isInOrder(keyframes, Comparator.comparingInt(Keyframe::ticks))) {
         return DataResult.error(() -> "Keyframes must be ordered by ticks field");
      } else {
         if (keyframes.size() > 1) {
            int repeatCount = 0;
            int lastTicks = ((Keyframe)keyframes.getLast()).ticks();

            for(Keyframe keyframe : keyframes) {
               if (keyframe.ticks() == lastTicks) {
                  ++repeatCount;
                  if (repeatCount > 2) {
                     return DataResult.error(() -> "More than 2 keyframes on same tick: " + keyframe.ticks());
                  }
               } else {
                  repeatCount = 0;
               }

               lastTicks = keyframe.ticks();
            }
         }

         return DataResult.success(keyframes);
      }
   }

   public static DataResult validatePeriod(final KeyframeTrack track, final int periodTicks) {
      for(Keyframe keyframe : track.keyframes()) {
         int tick = keyframe.ticks();
         if (tick < 0 || tick > periodTicks) {
            return DataResult.error(() -> "Keyframe at tick " + keyframe.ticks() + " must be in range [0; " + periodTicks + "]");
         }
      }

      return DataResult.success(track);
   }

   public KeyframeTrackSampler bakeSampler(final Optional periodTicks, final LerpFunction lerp) {
      return new KeyframeTrackSampler(this, periodTicks, lerp);
   }

   public static class Builder {
      private final ImmutableList.Builder keyframes = ImmutableList.builder();
      private EasingType easing = EasingType.LINEAR;

      public KeyframeTrack.Builder addKeyframe(final int ticks, final Object value) {
         this.keyframes.add(new Keyframe(ticks, value));
         return this;
      }

      public KeyframeTrack.Builder setEasing(final EasingType easing) {
         this.easing = easing;
         return this;
      }

      public KeyframeTrack build() {
         List keyframes = (List)KeyframeTrack.validateKeyframes(this.keyframes.build()).getOrThrow();
         return new KeyframeTrack(keyframes, this.easing);
      }
   }
}
