package net.minecraft.commands.execution;

@FunctionalInterface
public interface UnboundEntryAction {
   void execute(Object sender, ExecutionContext context, Frame frame);

   default EntryAction bind(final Object sender) {
      return (context, frame) -> this.execute(sender, context, frame);
   }
}
