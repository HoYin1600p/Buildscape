package net.minecraft.commands.execution.tasks;

import java.util.List;
import net.minecraft.commands.execution.CommandQueueEntry;
import net.minecraft.commands.execution.EntryAction;
import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.execution.Frame;

public class ContinuationTask implements EntryAction {
   private final ContinuationTask.TaskProvider taskFactory;
   private final List arguments;
   private final CommandQueueEntry selfEntry;
   private int index;

   private ContinuationTask(final ContinuationTask.TaskProvider taskFactory, final List arguments, final Frame frame) {
      this.taskFactory = taskFactory;
      this.arguments = arguments;
      this.selfEntry = new CommandQueueEntry(frame, this);
   }

   public void execute(final ExecutionContext context, final Frame frame) {
      Object argument = (P)this.arguments.get(this.index);
      context.queueNext(this.taskFactory.create(frame, argument));
      if (++this.index < this.arguments.size()) {
         context.queueNext(this.selfEntry);
      }

   }

   public static void schedule(final ExecutionContext context, final Frame frame, final List arguments, final ContinuationTask.TaskProvider taskFactory) {
      int argumentCount = arguments.size();
      switch (argumentCount) {
         case 0:
            break;
         case 1:
            context.queueNext(taskFactory.create(frame, arguments.get(0)));
            break;
         case 2:
            context.queueNext(taskFactory.create(frame, arguments.get(0)));
            context.queueNext(taskFactory.create(frame, arguments.get(1)));
            break;
         default:
            context.queueNext((new ContinuationTask(taskFactory, arguments, frame)).selfEntry);
      }

   }

   @FunctionalInterface
   public interface TaskProvider {
      CommandQueueEntry create(Frame frame, Object argument);
   }
}
