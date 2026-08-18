package net.minecraft.world.timeline;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.KeyframeTrack;
import net.minecraft.util.Util;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.attribute.modifier.AttributeModifier;
import net.minecraft.world.clock.ClockManager;
import net.minecraft.world.clock.ClockTimeMarker;
import net.minecraft.world.clock.WorldClock;

public class Timeline {
   public static final Codec CODEC = RegistryCodecs.holder(Registries.TIMELINE);
   private static final Codec TRACKS_CODEC = Codec.dispatchedMap(EnvironmentAttributes.CODEC, Util.memoize(AttributeTrack::createCodec));
   public static final Codec DIRECT_CODEC = RecordCodecBuilder.create((i) -> i.group(WorldClock.CODEC.fieldOf("clock").forGetter((t) -> t.clock), ExtraCodecs.POSITIVE_INT.optionalFieldOf("period_ticks").forGetter((t) -> t.periodTicks), TRACKS_CODEC.optionalFieldOf("tracks", Map.of()).forGetter((t) -> t.tracks), Codec.unboundedMap(ClockTimeMarker.KEY_CODEC, Timeline.TimeMarkerInfo.CODEC).optionalFieldOf("time_markers", Map.of()).forGetter((t) -> t.timeMarkers)).apply(i, Timeline::new)).validate(Timeline::validateInternal);
   public static final Codec NETWORK_CODEC = DIRECT_CODEC.xmap(Timeline::filterSyncableTracks, Timeline::filterSyncableTracks);
   private final Holder clock;
   private final Optional periodTicks;
   private final Map tracks;
   private final Map timeMarkers;

   private static Timeline filterSyncableTracks(final Timeline timeline) {
      Map syncableTracks = Map.copyOf(Maps.filterKeys(timeline.tracks, EnvironmentAttribute::isSyncable));
      return new Timeline(timeline.clock, timeline.periodTicks, syncableTracks, timeline.timeMarkers);
   }

   private Timeline(final Holder clock, final Optional periodTicks, final Map tracks, final Map timeMarkers) {
      this.clock = clock;
      this.periodTicks = periodTicks;
      this.tracks = tracks;
      this.timeMarkers = timeMarkers;
   }

   public static void validateRegistry(final Registry timelines, final Map loadingErrors) {
      Multimap timeMarkersByClock = HashMultimap.create();
      timelines.listElements().forEach((timeline) -> {
         Holder clock = ((Timeline)timeline.value()).clock();

         for(ResourceKey timeMarker : ((Timeline)timeline.value()).timeMarkers.keySet()) {
            if (!timeMarkersByClock.put(clock, timeMarker)) {
               loadingErrors.put(timeline.key(), new IllegalStateException(String.valueOf(timeMarker) + " was defined multiple times in " + clock.getRegisteredName()));
            }
         }

      });
   }

   private static DataResult validateInternal(final Timeline timeline) {
      if (timeline.periodTicks.isEmpty()) {
         return DataResult.success(timeline);
      } else {
         int periodTicks = timeline.periodTicks.get();

         for(Map.Entry entry : timeline.timeMarkers.entrySet()) {
            int ticks = ((Timeline.TimeMarkerInfo)entry.getValue()).ticks();
            if (ticks < 0 || ticks >= periodTicks) {
               return DataResult.error(() -> "Time Marker " + String.valueOf(entry.getKey()) + " must be in range [0; " + periodTicks + ")");
            }
         }

         DataResult result = DataResult.success(timeline);

         for(AttributeTrack track : timeline.tracks.values()) {
            result = result.apply2stable((t, $) -> t, AttributeTrack.validatePeriod(track, periodTicks));
         }

         return result;
      }
   }

   public static Timeline.Builder builder(final Holder clock) {
      return new Timeline.Builder(clock);
   }

   public int getPeriodCount(final ClockManager clockManager) {
      if (this.periodTicks.isEmpty()) {
         return 0;
      } else {
         long totalTicks = this.getTotalTicks(clockManager);
         return (int)(totalTicks / (long)((Integer)this.periodTicks.get()).intValue());
      }
   }

   public long getCurrentTicks(final ClockManager clockManager) {
      long totalTicks = this.getTotalTicks(clockManager);
      return this.periodTicks.isEmpty() ? totalTicks : totalTicks % (long)((Integer)this.periodTicks.get()).intValue();
   }

   public long getTotalTicks(final ClockManager clockManager) {
      return clockManager.getInstance(this.clock).totalTicks();
   }

   public Holder clock() {
      return this.clock;
   }

   public Optional periodTicks() {
      return this.periodTicks;
   }

   public void registerTimeMarkers(final BiConsumer output) {
      for(Map.Entry entry : this.timeMarkers.entrySet()) {
         Timeline.TimeMarkerInfo info = (Timeline.TimeMarkerInfo)entry.getValue();
         output.accept((ResourceKey)entry.getKey(), new ClockTimeMarker(this.clock, info.ticks, this.periodTicks, info.showInCommands));
      }

   }

   public Set attributes() {
      return this.tracks.keySet();
   }

   public AttributeTrackSampler createTrackSampler(final EnvironmentAttribute attribute, final ClockManager clockManager) {
      AttributeTrack track = (AttributeTrack)this.tracks.get(attribute);
      if (track == null) {
         throw new IllegalStateException("Timeline has no track for " + String.valueOf(attribute));
      } else {
         return track.bakeSampler(attribute, this.clock, this.periodTicks, clockManager);
      }
   }

   public static class Builder {
      private final Holder clock;
      private Optional periodTicks = Optional.empty();
      private final ImmutableMap.Builder tracks = ImmutableMap.builder();
      private final ImmutableMap.Builder timeMarkers = ImmutableMap.builder();

      private Builder(final Holder clock) {
         this.clock = clock;
      }

      public Timeline.Builder setPeriodTicks(final int periodTicks) {
         this.periodTicks = Optional.of(periodTicks);
         return this;
      }

      public Timeline.Builder addModifierTrack(final EnvironmentAttribute attribute, final AttributeModifier modifier, final Consumer builder) {
         attribute.type().checkAllowedModifier(modifier);
         KeyframeTrack.Builder argumentTrack = new KeyframeTrack.Builder();
         builder.accept(argumentTrack);
         this.tracks.put(attribute, new AttributeTrack(modifier, argumentTrack.build()));
         return this;
      }

      public Timeline.Builder addTrack(final EnvironmentAttribute attribute, final Consumer builder) {
         return this.addModifierTrack(attribute, AttributeModifier.override(), builder);
      }

      public Timeline.Builder addTimeMarker(final ResourceKey id, final int ticks) {
         return this.addTimeMarker(id, ticks, false);
      }

      public Timeline.Builder addTimeMarker(final ResourceKey id, final int ticks, final boolean showInCommands) {
         this.timeMarkers.put(id, new Timeline.TimeMarkerInfo(ticks, showInCommands));
         return this;
      }

      public Timeline build() {
         return new Timeline(this.clock, this.periodTicks, this.tracks.build(), this.timeMarkers.build());
      }
   }

   private static record TimeMarkerInfo(int ticks, boolean showInCommands) {
      private static final Codec FULL_CODEC = RecordCodecBuilder.create((i) -> i.group(ExtraCodecs.NON_NEGATIVE_INT.fieldOf("ticks").forGetter(Timeline.TimeMarkerInfo::ticks), Codec.BOOL.optionalFieldOf("show_in_commands", false).forGetter(Timeline.TimeMarkerInfo::showInCommands)).apply(i, Timeline.TimeMarkerInfo::new));
      public static final Codec CODEC = Codec.either(ExtraCodecs.NON_NEGATIVE_INT, FULL_CODEC).xmap((either) -> (Timeline.TimeMarkerInfo)either.map((t) -> new Timeline.TimeMarkerInfo(t, false), (t) -> t), (timeMarker) -> timeMarker.showInCommands ? Either.right(timeMarker) : Either.left(timeMarker.ticks));
   }
}
