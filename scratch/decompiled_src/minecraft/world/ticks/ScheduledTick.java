package net.minecraft.world.ticks;

import it.unimi.dsi.fastutil.Hash;
import java.util.Comparator;
import net.minecraft.core.BlockPos;
import org.jspecify.annotations.Nullable;

public record ScheduledTick(Object type, BlockPos pos, long triggerTick, TickPriority priority, long subTickOrder) {
   public static final Comparator DRAIN_ORDER = (o1, o2) -> {
      int compare = Long.compare(o1.triggerTick, o2.triggerTick);
      if (compare != 0) {
         return compare;
      } else {
         compare = o1.priority.compareTo(o2.priority);
         return compare != 0 ? compare : Long.compare(o1.subTickOrder, o2.subTickOrder);
      }
   };
   public static final Comparator INTRA_TICK_DRAIN_ORDER = (o1, o2) -> {
      int compare = o1.priority.compareTo(o2.priority);
      return compare != 0 ? compare : Long.compare(o1.subTickOrder, o2.subTickOrder);
   };
   public static final Hash.Strategy UNIQUE_TICK_HASH = new Hash.Strategy() {
      public int hashCode(final ScheduledTick o) {
         return 31 * o.pos().hashCode() + o.type().hashCode();
      }

      public boolean equals(final @Nullable ScheduledTick a, final @Nullable ScheduledTick b) {
         if (a == b) {
            return true;
         } else if (a != null && b != null) {
            return a.type() == b.type() && a.pos().equals(b.pos());
         } else {
            return false;
         }
      }
   };

   public ScheduledTick(final Object type, final BlockPos pos, final long triggerTick, final long subTickOrder) {
      this(type, pos, triggerTick, TickPriority.NORMAL, subTickOrder);
   }

   public ScheduledTick {
      pos = pos.immutable();
   }

   public static ScheduledTick probe(final Object type, final BlockPos pos) {
      return new ScheduledTick(type, pos, 0L, TickPriority.NORMAL, 0L);
   }

   public SavedTick toSavedTick(final long currentTick) {
      return new SavedTick(this.type, this.pos, (int)(this.triggerTick - currentTick), this.priority);
   }
}
