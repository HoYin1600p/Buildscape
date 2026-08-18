package net.minecraft.commands.execution;

public record CommandQueueEntry(Frame frame, EntryAction action) {
   public void execute(final ExecutionContext context) {
      this.action.execute(context, this.frame);
   }
}
