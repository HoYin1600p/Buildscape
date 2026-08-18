package net.minecraft.commands.execution;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.ExecutionCommandSource;
import org.jspecify.annotations.Nullable;

public interface CustomCommandExecutor {
   void run(Object sender, ContextChain currentStep, ChainModifiers modifiers, ExecutionControl output);

   public interface CommandAdapter extends CustomCommandExecutor, Command {
      default int run(final CommandContext context) throws CommandSyntaxException {
         throw new UnsupportedOperationException("This function should not run");
      }
   }

   public abstract static class WithErrorHandling implements CustomCommandExecutor {
      public final void run(final ExecutionCommandSource sender, final ContextChain currentStep, final ChainModifiers modifiers, final ExecutionControl output) {
         try {
            this.runGuarded(sender, currentStep, modifiers, output);
         } catch (CommandSyntaxException var6) {
            this.onError(var6, sender, modifiers, output.tracer());
            sender.callback().onFailure();
         }

      }

      protected void onError(final CommandSyntaxException e, final ExecutionCommandSource sender, final ChainModifiers modifiers, final @Nullable TraceCallbacks tracer) {
         sender.handleError(e, modifiers.isForked(), tracer);
      }

      protected abstract void runGuarded(ExecutionCommandSource sender, ContextChain currentStep, ChainModifiers modifiers, ExecutionControl output) throws CommandSyntaxException;
   }
}
