package net.minecraft.util;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Queues;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Deque;
import org.jspecify.annotations.Nullable;

public final class SequencedPriorityIterator extends AbstractIterator {
   private static final int MIN_PRIO = Integer.MIN_VALUE;
   private @Nullable Deque highestPrioQueue = null;
   private int highestPrio = Integer.MIN_VALUE;
   private final Int2ObjectMap queuesByPriority = new Int2ObjectOpenHashMap();

   public void add(final Object data, final int priority) {
      if (priority == this.highestPrio && this.highestPrioQueue != null) {
         this.highestPrioQueue.addLast(data);
      } else {
         Deque queue = (Deque)this.queuesByPriority.computeIfAbsent(priority, (order) -> Queues.newArrayDeque());
         queue.addLast(data);
         if (priority >= this.highestPrio) {
            this.highestPrioQueue = queue;
            this.highestPrio = priority;
         }

      }
   }

   protected @Nullable Object computeNext() {
      if (this.highestPrioQueue == null) {
         return this.endOfData();
      } else {
         Object result = (T)this.highestPrioQueue.removeFirst();
         if (result == null) {
            return this.endOfData();
         } else {
            if (this.highestPrioQueue.isEmpty()) {
               this.switchCacheToNextHighestPrioQueue();
            }

            return result;
         }
      }
   }

   private void switchCacheToNextHighestPrioQueue() {
      int foundHighestPrio = Integer.MIN_VALUE;
      Deque foundHighestPrioQueue = null;
      ObjectIterator var3 = Int2ObjectMaps.fastIterable(this.queuesByPriority).iterator();

      while(var3.hasNext()) {
         Int2ObjectMap.Entry entry = (Int2ObjectMap.Entry)var3.next();
         Deque queue = (Deque)entry.getValue();
         int prio = entry.getIntKey();
         if (prio > foundHighestPrio && !queue.isEmpty()) {
            foundHighestPrio = prio;
            foundHighestPrioQueue = queue;
            if (prio == this.highestPrio - 1) {
               break;
            }
         }
      }

      this.highestPrio = foundHighestPrio;
      this.highestPrioQueue = foundHighestPrioQueue;
   }
}
