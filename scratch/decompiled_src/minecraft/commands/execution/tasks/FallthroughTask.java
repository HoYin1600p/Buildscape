package net.minecraft.commands.execution.tasks;

import net.minecraft.commands.execution.EntryAction;
import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.execution.Frame;

public class FallthroughTask implements EntryAction {
   private static final FallthroughTask INSTANCE = new FallthroughTask();

   public static EntryAction instance() {
      return INSTANCE;
   }

   public void execute(final ExecutionContext context, final Frame frame) {
      frame.returnFailure();
      frame.discard();
   }
}
