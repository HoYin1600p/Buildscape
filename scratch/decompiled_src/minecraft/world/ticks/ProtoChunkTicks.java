package net.minecraft.world.ticks;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;

public class ProtoChunkTicks implements TickContainerAccess, SerializableTickContainer {
   private final List ticks = Lists.newArrayList();
   private final Set ticksPerPosition = new ObjectOpenCustomHashSet(SavedTick.UNIQUE_TICK_HASH);

   public void schedule(final ScheduledTick tick) {
      SavedTick newTick = new SavedTick(tick.type(), tick.pos(), 0, tick.priority());
      this.schedule(newTick);
   }

   private void schedule(final SavedTick newTick) {
      if (this.ticksPerPosition.add(newTick)) {
         this.ticks.add(newTick);
      }

   }

   public boolean hasScheduledTick(final BlockPos pos, final Object type) {
      return this.ticksPerPosition.contains(SavedTick.probe(type, pos));
   }

   public int count() {
      return this.ticks.size();
   }

   public List pack(final long currentTick) {
      return this.ticks;
   }

   public List scheduledTicks() {
      return List.copyOf(this.ticks);
   }

   public static ProtoChunkTicks load(final List ticks) {
      ProtoChunkTicks result = new ProtoChunkTicks();
      ticks.forEach(result::schedule);
      return result;
   }
}
