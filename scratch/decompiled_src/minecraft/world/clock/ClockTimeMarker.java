package net.minecraft.world.clock;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;

public record ClockTimeMarker(Holder clock, int ticks, Optional periodTicks, boolean showInCommands) {
   public static final Codec KEY_CODEC = ResourceKey.codec(ClockTimeMarkers.ROOT_ID);

   public long getRepetitionCount(final long totalTicks) {
      if (this.periodTicks.isEmpty()) {
         return totalTicks >= (long)this.ticks ? 1L : 0L;
      } else {
         int periodTicks = this.periodTicks.get();
         return totalTicks / (long)periodTicks + (long)(totalTicks % (long)periodTicks >= (long)this.ticks ? 1 : 0);
      }
   }

   public long resolveTimeToMoveTo(final long totalTicks) {
      if (this.periodTicks.isEmpty()) {
         return (long)this.ticks;
      } else {
         int periodTicks = this.periodTicks.get();
         return totalTicks + durationToNext(periodTicks, totalTicks % (long)periodTicks, (long)this.ticks);
      }
   }

   public boolean occursAt(final long totalTicks) {
      if (this.periodTicks.isEmpty()) {
         return (long)this.ticks == totalTicks;
      } else {
         return (long)this.ticks == totalTicks % (long)((Integer)this.periodTicks.get()).intValue();
      }
   }

   private static long durationToNext(final int periodTicks, final long from, final long to) {
      long duration = to - from;
      return duration > 0L ? duration : (long)periodTicks + duration;
   }
}
