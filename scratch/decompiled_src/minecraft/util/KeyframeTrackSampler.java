package net.minecraft.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.attribute.LerpFunction;

public class KeyframeTrackSampler {
   private final Optional periodTicks;
   private final LerpFunction lerp;
   private final List segments;

   KeyframeTrackSampler(final KeyframeTrack track, final Optional periodTicks, final LerpFunction lerp) {
      this.periodTicks = periodTicks;
      this.lerp = lerp;
      this.segments = bakeSegments(track, periodTicks);
   }

   private static List bakeSegments(final KeyframeTrack track, final Optional periodTicks) {
      List keyframes = track.keyframes();
      if (keyframes.size() == 1) {
         Object value = (T)((Keyframe)keyframes.getFirst()).value();
         return List.of(new KeyframeTrackSampler.Segment(EasingType.CONSTANT, value, 0, value, 0));
      } else {
         List segments = new ArrayList();
         if (periodTicks.isPresent()) {
            Keyframe firstKeyframe = (Keyframe)keyframes.getFirst();
            Keyframe lastKeyframe = (Keyframe)keyframes.getLast();
            segments.add(new KeyframeTrackSampler.Segment(track, lastKeyframe, lastKeyframe.ticks() - periodTicks.get(), firstKeyframe, firstKeyframe.ticks()));
            addSegmentsFromKeyframes(track, keyframes, segments);
            segments.add(new KeyframeTrackSampler.Segment(track, lastKeyframe, lastKeyframe.ticks(), firstKeyframe, firstKeyframe.ticks() + periodTicks.get()));
         } else {
            addSegmentsFromKeyframes(track, keyframes, segments);
         }

         return List.copyOf(segments);
      }
   }

   private static void addSegmentsFromKeyframes(final KeyframeTrack track, final List keyframes, final List output) {
      for(int i = 0; i < keyframes.size() - 1; ++i) {
         Keyframe keyframe = (Keyframe)keyframes.get(i);
         Keyframe nextKeyframe = (Keyframe)keyframes.get(i + 1);
         output.add(new KeyframeTrackSampler.Segment(track, keyframe, keyframe.ticks(), nextKeyframe, nextKeyframe.ticks()));
      }

   }

   public Object sample(final long ticks) {
      long sampleTicks = this.loopTicks(ticks);
      KeyframeTrackSampler.Segment segment = this.getSegmentAt(sampleTicks);
      if (sampleTicks <= (long)segment.fromTicks) {
         return segment.fromValue;
      } else if (sampleTicks >= (long)segment.toTicks) {
         return segment.toValue;
      } else {
         float alpha = (float)(sampleTicks - (long)segment.fromTicks) / (float)(segment.toTicks - segment.fromTicks);
         float easedAlpha = segment.easing.apply(alpha);
         return this.lerp.apply(easedAlpha, segment.fromValue, segment.toValue);
      }
   }

   private KeyframeTrackSampler.Segment getSegmentAt(final long currentTicks) {
      for(KeyframeTrackSampler.Segment segment : this.segments) {
         if (currentTicks < (long)segment.toTicks) {
            return segment;
         }
      }

      return (KeyframeTrackSampler.Segment)this.segments.getLast();
   }

   private long loopTicks(final long ticks) {
      return this.periodTicks.isPresent() ? (long)Math.floorMod(ticks, this.periodTicks.get()) : ticks;
   }

   private static record Segment(EasingType easing, Object fromValue, int fromTicks, Object toValue, int toTicks) {
      public Segment(final KeyframeTrack track, final Keyframe from, final int fromTicks, final Keyframe to, final int toTicks) {
         this(track.easingType(), from.value(), fromTicks, to.value(), toTicks);
      }
   }
}
