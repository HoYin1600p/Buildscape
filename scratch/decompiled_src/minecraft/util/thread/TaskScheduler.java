package net.minecraft.util.thread;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public interface TaskScheduler extends AutoCloseable {
   String name();

   void schedule(final Runnable r);

   default void close() {
   }

   Runnable wrapRunnable(final Runnable runnable);

   default CompletableFuture scheduleWithResult(final Consumer futureConsumer) {
      CompletableFuture future = new CompletableFuture();
      this.schedule(this.wrapRunnable(() -> futureConsumer.accept(future)));
      return future;
   }

   static TaskScheduler wrapExecutor(final String name, final Executor executor) {
      return new TaskScheduler() {
         public String name() {
            return name;
         }

         public void schedule(final Runnable runnable) {
            executor.execute(runnable);
         }

         public Runnable wrapRunnable(final Runnable runnable) {
            return runnable;
         }

         public String toString() {
            return name;
         }
      };
   }
}
