package net.minecraft.util.thread;

public abstract class ReentrantBlockableEventLoop extends BlockableEventLoop {
   private int reentrantCount;

   public ReentrantBlockableEventLoop(final String name, final boolean propagatesCrashes) {
      super(name, propagatesCrashes);
   }

   protected boolean scheduleExecutables() {
      return this.runningTask() || super.scheduleExecutables();
   }

   protected boolean runningTask() {
      return this.reentrantCount != 0;
   }

   protected void doRunTask(final Runnable task) {
      ++this.reentrantCount;

      try {
         super.doRunTask(task);
      } finally {
         --this.reentrantCount;
      }

   }
}
