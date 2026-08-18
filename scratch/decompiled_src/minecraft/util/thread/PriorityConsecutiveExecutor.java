package net.minecraft.util.thread;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import net.minecraft.util.profiling.metrics.MetricsRegistry;

public class PriorityConsecutiveExecutor extends AbstractConsecutiveExecutor {
   public PriorityConsecutiveExecutor(final int priorityCount, final Executor executor, final String name) {
      super(new StrictQueue.FixedPriorityQueue(priorityCount), executor, name);
      MetricsRegistry.INSTANCE.add(this);
   }

   public StrictQueue.RunnableWithPriority wrapRunnable(final Runnable runnable) {
      return new StrictQueue.RunnableWithPriority(0, runnable);
   }

   public CompletableFuture scheduleWithResult(final int priority, final Consumer futureConsumer) {
      CompletableFuture future = new CompletableFuture();
      this.schedule(new StrictQueue.RunnableWithPriority(priority, () -> futureConsumer.accept(future)));
      return future;
   }
}
