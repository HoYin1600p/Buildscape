package net.minecraft.commands.execution;

@FunctionalInterface
public interface EntryAction {
   void execute(ExecutionContext context, Frame frame);
}
