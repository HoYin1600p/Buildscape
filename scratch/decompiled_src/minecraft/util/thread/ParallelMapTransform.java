package net.minecraft.util.thread;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

public class ParallelMapTransform {
   private static final int DEFAULT_TASKS_PER_THREAD = 16;

   public static CompletableFuture schedule(final Map input, final BiFunction operation, final int maxTaskCount, final Executor executor) {
      int inputSize = input.size();
      if (inputSize == 0) {
         return CompletableFuture.completedFuture(Map.of());
      } else if (inputSize == 1) {
         Map.Entry element = (Map.Entry)input.entrySet().iterator().next();
         Object key = (K)element.getKey();
         Object value = (U)element.getValue();
         return CompletableFuture.supplyAsync(() -> {
            Object result = (V)operation.apply(key, value);
            return result != null ? Map.of(key, result) : Map.of();
         }, executor);
      } else {
         ParallelMapTransform.SplitterBase splitter = (ParallelMapTransform.SplitterBase<K, U, V>)(inputSize <= maxTaskCount ? new ParallelMapTransform.SingleTaskSplitter(operation, inputSize) : new ParallelMapTransform.BatchedTaskSplitter(operation, inputSize, maxTaskCount));
         return splitter.scheduleTasks(input, executor);
      }
   }

   public static CompletableFuture schedule(final Map input, final BiFunction operation, final Executor executor) {
      int maxTaskCount = Util.maxAllowedExecutorThreads() * 16;
      return schedule(input, operation, maxTaskCount, executor);
   }

   private static class BatchedTaskSplitter extends ParallelMapTransform.SplitterBase {
      private final Map result;
      private final int batchSize;
      private final int firstUndersizedBatchIndex;

      private BatchedTaskSplitter(final BiFunction operation, final int size, final int maxTasks) {
         super(operation, size, maxTasks);
         this.result = new HashMap(size);
         this.batchSize = Mth.positiveCeilDiv(size, maxTasks);
         int fullCapacity = this.batchSize * maxTasks;
         int leftoverCapacity = fullCapacity - size;
         this.firstUndersizedBatchIndex = maxTasks - leftoverCapacity;

         assert this.firstUndersizedBatchIndex > 0 && this.firstUndersizedBatchIndex <= maxTasks;
      }

      protected CompletableFuture scheduleBatch(final ParallelMapTransform.Container container, final int startIndex, final int endIndex, final Executor executor) {
         int batchSize = endIndex - startIndex;

         assert batchSize == this.batchSize || batchSize == this.batchSize - 1;

         return CompletableFuture.runAsync(createTask(this.result, startIndex, endIndex, container), executor);
      }

      protected int batchSize(final int index) {
         return index < this.firstUndersizedBatchIndex ? this.batchSize : this.batchSize - 1;
      }

      private static Runnable createTask(final Map result, final int startIndex, final int endIndex, final ParallelMapTransform.Container container) {
         return () -> {
            for(int i = startIndex; i < endIndex; ++i) {
               container.applyOperation(i);
            }

            synchronized(result) {
               for(int i = startIndex; i < endIndex; ++i) {
                  container.copyOut(i, result);
               }

            }
         };
      }

      protected CompletableFuture scheduleFinalOperation(final CompletableFuture allTasksDone, final ParallelMapTransform.Container container) {
         Map result = this.result;
         return allTasksDone.thenApply((ignored) -> result);
      }
   }

   private static record Container(BiFunction operation, @Nullable Object[] keys, @Nullable Object[] values) {
      public Container(final BiFunction operation, final int size) {
         this(operation, new Object[size], new Object[size]);
      }

      public void put(final int index, final Object key, final Object input) {
         this.keys[index] = key;
         this.values[index] = input;
      }

      private @Nullable Object key(final int index) {
         return this.keys[index];
      }

      private @Nullable Object output(final int index) {
         return this.values[index];
      }

      private @Nullable Object input(final int index) {
         return this.values[index];
      }

      public void applyOperation(final int index) {
         this.values[index] = this.operation.apply(this.key(index), this.input(index));
      }

      public void copyOut(final int index, final Map output) {
         Object value = (V)this.output(index);
         if (value != null) {
            Object key = (K)this.key(index);
            output.put(key, value);
         }

      }

      public int size() {
         return this.keys.length;
      }
   }

   private static class SingleTaskSplitter extends ParallelMapTransform.SplitterBase {
      private SingleTaskSplitter(final BiFunction operation, final int size) {
         super(operation, size, size);
      }

      protected int batchSize(final int index) {
         return 1;
      }

      protected CompletableFuture scheduleBatch(final ParallelMapTransform.Container container, final int startIndex, final int endIndex, final Executor executor) {
         assert startIndex + 1 == endIndex;

         return CompletableFuture.runAsync(() -> container.applyOperation(startIndex), executor);
      }

      protected CompletableFuture scheduleFinalOperation(final CompletableFuture allTasksDone, final ParallelMapTransform.Container container) {
         return allTasksDone.thenApply((ignored) -> {
            Map result = new HashMap(container.size());

            for(int i = 0; i < container.size(); ++i) {
               container.copyOut(i, result);
            }

            return result;
         });
      }
   }

   private abstract static class SplitterBase {
      private int lastScheduledIndex;
      private int currentIndex;
      private final CompletableFuture[] tasks;
      private int batchIndex;
      private final ParallelMapTransform.Container container;

      private SplitterBase(final BiFunction operation, final int size, final int taskCount) {
         this.container = new ParallelMapTransform.Container(operation, size);
         this.tasks = new CompletableFuture[taskCount];
      }

      private int pendingBatchSize() {
         return this.currentIndex - this.lastScheduledIndex;
      }

      public CompletableFuture scheduleTasks(final Map input, final Executor executor) {
         input.forEach((key, inputValue) -> {
            this.container.put(this.currentIndex++, key, inputValue);
            if (this.pendingBatchSize() == this.batchSize(this.batchIndex)) {
               this.tasks[this.batchIndex++] = this.scheduleBatch(this.container, this.lastScheduledIndex, this.currentIndex, executor);
               this.lastScheduledIndex = this.currentIndex;
            }

         });

         assert this.currentIndex == this.container.size();

         assert this.lastScheduledIndex == this.currentIndex;

         assert this.batchIndex == this.tasks.length;

         return this.scheduleFinalOperation(CompletableFuture.allOf(this.tasks), this.container);
      }

      protected abstract int batchSize(int index);

      protected abstract CompletableFuture scheduleBatch(ParallelMapTransform.Container container, int startIndex, int endIndex, Executor executor);

      protected abstract CompletableFuture scheduleFinalOperation(CompletableFuture allTasksDone, ParallelMapTransform.Container container);
   }
}
