package net.minecraft.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.execution.TraceCallbacks;
import net.minecraft.server.permissions.PermissionSetSupplier;
import org.jspecify.annotations.Nullable;

public interface ExecutionCommandSource extends PermissionSetSupplier {
   ExecutionCommandSource withCallback(CommandResultCallback resultCallback);

   CommandResultCallback callback();

   default ExecutionCommandSource clearCallbacks() {
      return this.withCallback(CommandResultCallback.EMPTY);
   }

   CommandDispatcher dispatcher();

   void handleError(CommandExceptionType type, Message message, boolean forked, @Nullable TraceCallbacks tracer);

   boolean isSilent();

   default void handleError(final CommandSyntaxException e, final boolean forked, final @Nullable TraceCallbacks tracer) {
      this.handleError(e.getType(), e.getRawMessage(), forked, tracer);
   }

   static ResultConsumer resultConsumer() {
      return (context, success, result) -> ((ExecutionCommandSource)context.getSource()).callback().onResult(success, result);
   }
}
