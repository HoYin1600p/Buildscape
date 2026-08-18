package net.minecraft.commands.execution.tasks;

import java.util.function.Consumer;
import net.minecraft.commands.CommandResultCallback;
import net.minecraft.commands.execution.EntryAction;
import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.execution.ExecutionControl;
import net.minecraft.commands.execution.Frame;

public class IsolatedCall implements EntryAction {
   private final Consumer taskProducer;
   private final CommandResultCallback output;

   public IsolatedCall(final Consumer taskOutput, final CommandResultCallback output) {
      this.taskProducer = taskOutput;
      this.output = output;
   }

   public void execute(final ExecutionContext context, final Frame frame) {
      int newFrameDepth = frame.depth() + 1;
      Frame newFrame = new Frame(newFrameDepth, this.output, context.frameControlForDepth(newFrameDepth));
      this.taskProducer.accept(ExecutionControl.create(context, newFrame));
   }
}
