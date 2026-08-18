package net.minecraft.world.ticks;

import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import org.jspecify.annotations.Nullable;

public class LevelChunkTicks implements TickContainerAccess, SerializableTickContainer {
   private static final Comparator SUB_TICK_ORDERING = Comparator.comparingLong(ScheduledTick::subTickOrder);
   private final Queue tickQueue = new PriorityQueue(ScheduledTick.DRAIN_ORDER);
   private @Nullable List pendingTicks;
   private final Set ticksPerPosition = new ObjectOpenCustomHashSet(ScheduledTick.UNIQUE_TICK_HASH);
   private @Nullable BiConsumer onTickAdded;

   public LevelChunkTicks() {
   }

   public LevelChunkTicks(final List pendingTicks) {
      this.pendingTicks = pendingTicks;

      for(SavedTick pendingTick : pendingTicks) {
         this.ticksPerPosition.add(ScheduledTick.probe(pendingTick.type(), pendingTick.pos()));
      }

   }

   public void setOnTickAdded(final @Nullable BiConsumer onTickAdded) {
      this.onTickAdded = onTickAdded;
   }

   public @Nullable ScheduledTick peek() {
      return (ScheduledTick)this.tickQueue.peek();
   }

   public @Nullable ScheduledTick poll() {
      ScheduledTick result = (ScheduledTick)this.tickQueue.poll();
      if (result != null) {
         this.ticksPerPosition.remove(result);
      }

      return result;
   }

   public void schedule(final ScheduledTick tick) {
      if (this.ticksPerPosition.add(tick)) {
         this.scheduleUnchecked(tick);
      }

   }

   private void scheduleUnchecked(final ScheduledTick tick) {
      this.tickQueue.add(tick);
      if (this.onTickAdded != null) {
         this.onTickAdded.accept(this, tick);
      }

   }

   public boolean hasScheduledTick(final BlockPos pos, final Object type) {
      return this.ticksPerPosition.contains(ScheduledTick.probe(type, pos));
   }

   public void removeIf(final Predicate test) {
      Iterator iterator = this.tickQueue.iterator();

      while(iterator.hasNext()) {
         ScheduledTick tick = (ScheduledTick)iterator.next();
         if (test.test(tick)) {
            iterator.remove();
            this.ticksPerPosition.remove(tick);
         }
      }

   }

   public Stream getAll() {
      return this.tickQueue.stream();
   }

   public int count() {
      return this.tickQueue.size() + (this.pendingTicks != null ? this.pendingTicks.size() : 0);
   }

   public List pack(final long currentTick) {
      List ticks = new ArrayList(this.tickQueue.size());
      if (this.pendingTicks != null) {
         ticks.addAll(this.pendingTicks);
      }

      List sortedTicks = new ArrayList(this.tickQueue);
      sortedTicks.sort(SUB_TICK_ORDERING);

      for(ScheduledTick tick : sortedTicks) {
         ticks.add(tick.toSavedTick(currentTick));
      }

      return ticks;
   }

   public void unpack(final long currentTick) {
      if (this.pendingTicks != null) {
         int subTickBase = -this.pendingTicks.size();

         for(SavedTick pendingTick : this.pendingTicks) {
            this.scheduleUnchecked(pendingTick.unpack(currentTick, (long)(subTickBase++)));
         }
      }

      this.pendingTicks = null;
   }
}
