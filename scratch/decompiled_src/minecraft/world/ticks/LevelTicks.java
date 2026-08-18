package net.minecraft.world.ticks;

import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongMaps;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.LongPredicate;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class LevelTicks implements LevelTickAccess {
   private static final Comparator CONTAINER_DRAIN_ORDER = (o1, o2) -> ScheduledTick.INTRA_TICK_DRAIN_ORDER.compare(o1.peek(), o2.peek());
   private final LongPredicate tickCheck;
   private final Long2ObjectMap allContainers = new Long2ObjectOpenHashMap();
   private final Long2LongMap nextTickForContainer = (Long2LongMap)Util.make(new Long2LongOpenHashMap(), (m) -> m.defaultReturnValue(Long.MAX_VALUE));
   private final Queue containersToTick = new PriorityQueue(CONTAINER_DRAIN_ORDER);
   private final Queue toRunThisTick = new ArrayDeque();
   private final List alreadyRunThisTick = new ArrayList();
   private final Set toRunThisTickSet = new ObjectOpenCustomHashSet(ScheduledTick.UNIQUE_TICK_HASH);
   private final BiConsumer chunkScheduleUpdater = (container, newTick) -> {
      if (newTick.equals(container.peek())) {
         this.updateContainerScheduling(newTick);
      }

   };

   public LevelTicks(final LongPredicate tickCheck) {
      this.tickCheck = tickCheck;
   }

   public void addContainer(final ChunkPos pos, final LevelChunkTicks container) {
      long posKey = pos.pack();
      this.allContainers.put(posKey, container);
      ScheduledTick nextTick = container.peek();
      if (nextTick != null) {
         this.nextTickForContainer.put(posKey, nextTick.triggerTick());
      }

      container.setOnTickAdded(this.chunkScheduleUpdater);
   }

   public void removeContainer(final ChunkPos pos) {
      long chunkKey = pos.pack();
      LevelChunkTicks removedContainer = (LevelChunkTicks)this.allContainers.remove(chunkKey);
      this.nextTickForContainer.remove(chunkKey);
      if (removedContainer != null) {
         removedContainer.setOnTickAdded((BiConsumer)null);
      }

   }

   public void schedule(final ScheduledTick tick) {
      long chunkKey = ChunkPos.pack(tick.pos());
      LevelChunkTicks tickContainer = (LevelChunkTicks)this.allContainers.get(chunkKey);
      if (tickContainer == null) {
         Util.logAndPauseIfInIde("Trying to schedule tick in not loaded position " + String.valueOf(tick.pos()));
      } else {
         tickContainer.schedule(tick);
      }
   }

   public void tick(final long currentTick, final int maxTicksToProcess, final BiConsumer output) {
      ProfilerFiller profiler = Profiler.get();
      profiler.push("collect");
      this.collectTicks(currentTick, maxTicksToProcess, profiler);
      profiler.popPush("run");
      profiler.incrementCounter("ticksToRun", this.toRunThisTick.size());
      this.runCollectedTicks(output);
      profiler.popPush("cleanup");
      this.cleanupAfterTick();
      profiler.pop();
   }

   private void collectTicks(final long currentTick, final int maxTicksToProcess, final ProfilerFiller profiler) {
      this.sortContainersToTick(currentTick);
      profiler.incrementCounter("containersToTick", this.containersToTick.size());
      this.drainContainers(currentTick, maxTicksToProcess);
      this.rescheduleLeftoverContainers();
   }

   private void sortContainersToTick(final long currentTick) {
      ObjectIterator it = Long2LongMaps.fastIterator(this.nextTickForContainer);

      while(it.hasNext()) {
         Long2LongMap.Entry entry = (Long2LongMap.Entry)it.next();
         long chunkPos = entry.getLongKey();
         long nextTick = entry.getLongValue();
         if (nextTick <= currentTick) {
            LevelChunkTicks candidateContainer = (LevelChunkTicks)this.allContainers.get(chunkPos);
            if (candidateContainer == null) {
               it.remove();
            } else {
               ScheduledTick scheduledTick = candidateContainer.peek();
               if (scheduledTick == null) {
                  it.remove();
               } else if (scheduledTick.triggerTick() > currentTick) {
                  entry.setValue(scheduledTick.triggerTick());
               } else if (this.tickCheck.test(chunkPos)) {
                  it.remove();
                  this.containersToTick.add(candidateContainer);
               }
            }
         }
      }

   }

   private void drainContainers(final long currentTick, final int maxTicksToProcess) {
      LevelChunkTicks topContainer;
      while(this.canScheduleMoreTicks(maxTicksToProcess) && (topContainer = (LevelChunkTicks)this.containersToTick.poll()) != null) {
         ScheduledTick tick = topContainer.poll();
         this.scheduleForThisTick(tick);
         this.drainFromCurrentContainer(this.containersToTick, topContainer, currentTick, maxTicksToProcess);
         ScheduledTick nextTick = topContainer.peek();
         if (nextTick != null) {
            if (nextTick.triggerTick() <= currentTick && this.canScheduleMoreTicks(maxTicksToProcess)) {
               this.containersToTick.add(topContainer);
            } else {
               this.updateContainerScheduling(nextTick);
            }
         }
      }

   }

   private void rescheduleLeftoverContainers() {
      for(LevelChunkTicks container : this.containersToTick) {
         this.updateContainerScheduling(container.peek());
      }

   }

   private void updateContainerScheduling(final ScheduledTick nextTick) {
      this.nextTickForContainer.put(ChunkPos.pack(nextTick.pos()), nextTick.triggerTick());
   }

   private void drainFromCurrentContainer(final Queue containersToTick, final LevelChunkTicks currentContainer, final long currentTick, final int maxTicksToProcess) {
      if (this.canScheduleMoreTicks(maxTicksToProcess)) {
         LevelChunkTicks nextBestContainer = (LevelChunkTicks)containersToTick.peek();
         ScheduledTick nextFromNextContainer = nextBestContainer != null ? nextBestContainer.peek() : null;

         while(this.canScheduleMoreTicks(maxTicksToProcess)) {
            ScheduledTick nextFromCurrentContainer = currentContainer.peek();
            if (nextFromCurrentContainer == null || nextFromCurrentContainer.triggerTick() > currentTick || nextFromNextContainer != null && ScheduledTick.INTRA_TICK_DRAIN_ORDER.compare(nextFromCurrentContainer, nextFromNextContainer) > 0) {
               break;
            }

            currentContainer.poll();
            this.scheduleForThisTick(nextFromCurrentContainer);
         }

      }
   }

   private void scheduleForThisTick(final ScheduledTick tick) {
      this.toRunThisTick.add(tick);
   }

   private boolean canScheduleMoreTicks(final int maxTicksToProcess) {
      return this.toRunThisTick.size() < maxTicksToProcess;
   }

   private void runCollectedTicks(final BiConsumer output) {
      while(!this.toRunThisTick.isEmpty()) {
         ScheduledTick entry = (ScheduledTick)this.toRunThisTick.poll();
         if (!this.toRunThisTickSet.isEmpty()) {
            this.toRunThisTickSet.remove(entry);
         }

         this.alreadyRunThisTick.add(entry);
         output.accept(entry.pos(), entry.type());
      }

   }

   private void cleanupAfterTick() {
      this.toRunThisTick.clear();
      this.containersToTick.clear();
      this.alreadyRunThisTick.clear();
      this.toRunThisTickSet.clear();
   }

   public boolean hasScheduledTick(final BlockPos pos, final Object block) {
      LevelChunkTicks tickContainer = (LevelChunkTicks)this.allContainers.get(ChunkPos.pack(pos));
      return tickContainer != null && tickContainer.hasScheduledTick(pos, block);
   }

   public boolean willTickThisTick(final BlockPos pos, final Object type) {
      this.calculateTickSetIfNeeded();
      return this.toRunThisTickSet.contains(ScheduledTick.probe(type, pos));
   }

   private void calculateTickSetIfNeeded() {
      if (this.toRunThisTickSet.isEmpty() && !this.toRunThisTick.isEmpty()) {
         this.toRunThisTickSet.addAll(this.toRunThisTick);
      }

   }

   private void forContainersInArea(final BoundingBox bb, final LevelTicks.PosAndContainerConsumer ouput) {
      int xMin = SectionPos.posToSectionCoord((double)bb.minX());
      int zMin = SectionPos.posToSectionCoord((double)bb.minZ());
      int xMax = SectionPos.posToSectionCoord((double)bb.maxX());
      int zMax = SectionPos.posToSectionCoord((double)bb.maxZ());

      for(int x = xMin; x <= xMax; ++x) {
         for(int z = zMin; z <= zMax; ++z) {
            long containerPos = ChunkPos.pack(x, z);
            LevelChunkTicks container = (LevelChunkTicks)this.allContainers.get(containerPos);
            if (container != null) {
               ouput.accept(containerPos, container);
            }
         }
      }

   }

   public void clearArea(final BoundingBox area) {
      Predicate tickInsideBB = (t) -> area.isInside(t.pos());
      this.forContainersInArea(area, (pos, container) -> {
         ScheduledTick previousTop = container.peek();
         container.removeIf(tickInsideBB);
         ScheduledTick newTop = container.peek();
         if (newTop != previousTop) {
            if (newTop != null) {
               this.updateContainerScheduling(newTop);
            } else {
               this.nextTickForContainer.remove(pos);
            }
         }

      });
      this.alreadyRunThisTick.removeIf(tickInsideBB);
      this.toRunThisTick.removeIf(tickInsideBB);
   }

   public void copyArea(final BoundingBox area, final Vec3i offset) {
      this.copyAreaFrom(this, area, offset);
   }

   public void copyAreaFrom(final LevelTicks source, final BoundingBox area, final Vec3i offset) {
      List ticksToAdd = new ArrayList();
      Predicate tickInsideBB = (t) -> area.isInside(t.pos());
      source.alreadyRunThisTick.stream().filter(tickInsideBB).forEach(ticksToAdd::add);
      source.toRunThisTick.stream().filter(tickInsideBB).forEach(ticksToAdd::add);
      source.forContainersInArea(area, (pos, container) -> container.getAll().filter(tickInsideBB).forEach(ticksToAdd::add));
      LongSummaryStatistics info = ticksToAdd.stream().mapToLong(ScheduledTick::subTickOrder).summaryStatistics();
      long minSubTick = info.getMin();
      long maxSubTick = info.getMax();
      ticksToAdd.forEach((tick) -> this.schedule(new ScheduledTick(tick.type(), tick.pos().offset(offset), tick.triggerTick(), tick.priority(), tick.subTickOrder() - minSubTick + maxSubTick + 1L)));
   }

   public int count() {
      return this.allContainers.values().stream().mapToInt(TickAccess::count).sum();
   }

   @FunctionalInterface
   private interface PosAndContainerConsumer {
      void accept(long pos, LevelChunkTicks container);
   }
}
