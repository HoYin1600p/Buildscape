package net.minecraft.server.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public class CommandResponseTracker {
   private int totalValue;
   private @Nullable Object onlyElement;
   private int elementCount;
   private @Nullable Object onlyNonZeroElement;
   private int nonZeroElementCount;

   public static CommandResponseTracker create() {
      return new CommandResponseTracker();
   }

   public void track(final Object element, final int value) {
      this.totalValue += value;
      if (++this.elementCount == 1) {
         this.onlyElement = element;
      } else {
         this.onlyElement = null;
      }

      if (value != 0) {
         if (++this.nonZeroElementCount == 1) {
            this.onlyNonZeroElement = element;
         } else {
            this.onlyNonZeroElement = null;
         }
      }

   }

   public void track(final Object element, final boolean value) {
      this.track(element, value ? 1 : 0);
   }

   public void track(final Object element) {
      this.track(element, 1);
   }

   private @Nullable Object firstElement(final CommandResponseTracker.ElementType type) {
      Object var10000;
      switch (type.ordinal()) {
         case 0:
            var10000 = this.onlyElement;
            break;
         case 1:
            var10000 = this.onlyNonZeroElement;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   private int elementCount(final CommandResponseTracker.ElementType type) {
      int var10000;
      switch (type.ordinal()) {
         case 0:
            var10000 = this.elementCount;
            break;
         case 1:
            var10000 = this.nonZeroElementCount;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public int totalValue() {
      return this.totalValue;
   }

   public static CommandResponseTracker.Messages messages(final CommandResponseTracker.SingleHandler onSingle, final CommandResponseTracker.MultipleHandler onMultiple) {
      return new CommandResponseTracker.Messages((CommandResponseTracker.ErrorHandler)null, new CommandResponseTracker.Dispatch(onSingle, onMultiple));
   }

   public static CommandResponseTracker.Messages messages(final CommandResponseTracker.ErrorHandler onZero, final CommandResponseTracker.SingleHandler onSingle, final CommandResponseTracker.MultipleHandler onMultiple) {
      return new CommandResponseTracker.Messages(onZero, new CommandResponseTracker.Dispatch(onSingle, onMultiple));
   }

   public static CommandResponseTracker.Messages messages(final SimpleCommandExceptionType onZero, final CommandResponseTracker.SingleHandler onSingle, final CommandResponseTracker.MultipleHandler onMultiple) {
      return new CommandResponseTracker.Messages(onZero::create, new CommandResponseTracker.Dispatch(onSingle, onMultiple));
   }

   public Object dispatch(final CommandResponseTracker.ElementType elementType, final CommandResponseTracker.Dispatch dispatch) {
      Object firstElement = (Element)this.firstElement(elementType);
      return firstElement != null ? dispatch.onSingle().create(firstElement, this.totalValue) : dispatch.onMultiple().create(this.elementCount(elementType), this.totalValue);
   }

   public int sendFeedback(final CommandSourceStack sourceStack, final boolean broadcast, final CommandResponseTracker.ElementType elementType, final CommandResponseTracker.Messages messages) throws CommandSyntaxException {
      messages.throwIfZero(this.elementCount(elementType));
      sourceStack.sendSuccess(() -> (Component)this.dispatch(elementType, messages.onSuccess()), broadcast);
      return this.totalValue;
   }

   public int sendFeedback(final CommandSourceStack sourceStack, final boolean broadcast, final CommandResponseTracker.Messages messages) throws CommandSyntaxException {
      return this.sendFeedback(sourceStack, broadcast, CommandResponseTracker.ElementType.NON_ZERO, messages);
   }

   public static CommandResponseTracker.MessagesWithArg messages(final CommandResponseTracker.SingleHandlerWithArg onSingle, final CommandResponseTracker.MultipleHandlerWithArg onMultiple) {
      return new CommandResponseTracker.MessagesWithArg((CommandResponseTracker.ErrorHandlerWithArg)null, new CommandResponseTracker.DispatchWithArg(onSingle, onMultiple));
   }

   public static CommandResponseTracker.MessagesWithArg messages(final CommandResponseTracker.ErrorHandlerWithArg onZero, final CommandResponseTracker.SingleHandlerWithArg onSingle, final CommandResponseTracker.MultipleHandlerWithArg onMultiple) {
      return new CommandResponseTracker.MessagesWithArg(onZero, new CommandResponseTracker.DispatchWithArg(onSingle, onMultiple));
   }

   public static CommandResponseTracker.MessagesWithArg messages(final SimpleCommandExceptionType onZero, final CommandResponseTracker.SingleHandlerWithArg onSingle, final CommandResponseTracker.MultipleHandlerWithArg onMultiple) {
      return new CommandResponseTracker.MessagesWithArg((var1) -> onZero.create(), new CommandResponseTracker.DispatchWithArg(onSingle, onMultiple));
   }

   public Object dispatch(final CommandResponseTracker.ElementType elementType, final CommandResponseTracker.DispatchWithArg dispatch, final Object argument) {
      Object firstElement = (Element)this.firstElement(elementType);
      return firstElement != null ? dispatch.onSingle().create(firstElement, this.totalValue, argument) : dispatch.onMultiple().create(this.elementCount(elementType), this.totalValue, argument);
   }

   public int sendFeedback(final CommandSourceStack sourceStack, final boolean broadcast, final CommandResponseTracker.ElementType elementType, final CommandResponseTracker.MessagesWithArg messages, final Object argument) throws CommandSyntaxException {
      messages.throwIfZero(this.elementCount(elementType), argument);
      sourceStack.sendSuccess(() -> (Component)this.dispatch(elementType, messages.onSuccess(), argument), broadcast);
      return this.totalValue;
   }

   public int sendFeedback(final CommandSourceStack sourceStack, final boolean broadcast, final CommandResponseTracker.MessagesWithArg messages, final Object argument) throws CommandSyntaxException {
      return this.sendFeedback(sourceStack, broadcast, CommandResponseTracker.ElementType.NON_ZERO, messages, argument);
   }

   public static CommandResponseTracker.MessagesWithArgs messages(final CommandResponseTracker.SingleHandlerWithArgs onSingle, final CommandResponseTracker.MultipleHandlerWithArgs onMultiple) {
      return new CommandResponseTracker.MessagesWithArgs((CommandResponseTracker.ErrorHandlerWithArgs)null, new CommandResponseTracker.DispatchWithArgs(onSingle, onMultiple));
   }

   public static CommandResponseTracker.MessagesWithArgs messages(final CommandResponseTracker.ErrorHandlerWithArgs onZero, final CommandResponseTracker.SingleHandlerWithArgs onSingle, final CommandResponseTracker.MultipleHandlerWithArgs onMultiple) {
      return new CommandResponseTracker.MessagesWithArgs(onZero, new CommandResponseTracker.DispatchWithArgs(onSingle, onMultiple));
   }

   public static CommandResponseTracker.MessagesWithArgs messages(final SimpleCommandExceptionType onZero, final CommandResponseTracker.SingleHandlerWithArgs onSingle, final CommandResponseTracker.MultipleHandlerWithArgs onMultiple) {
      return new CommandResponseTracker.MessagesWithArgs((var1, var2) -> onZero.create(), new CommandResponseTracker.DispatchWithArgs(onSingle, onMultiple));
   }

   public Object dispatch(final CommandResponseTracker.ElementType elementType, final CommandResponseTracker.DispatchWithArgs messages, final Object argument0, final Object argument1) {
      Object firstElement = (Element)this.firstElement(elementType);
      return firstElement != null ? messages.onSingle().create(firstElement, this.totalValue, argument0, argument1) : messages.onMultiple().create(this.elementCount(elementType), this.totalValue, argument0, argument1);
   }

   public int sendFeedback(final CommandSourceStack sourceStack, final boolean broadcast, final CommandResponseTracker.ElementType elementType, final CommandResponseTracker.MessagesWithArgs messages, final Object argument0, final Object argument1) throws CommandSyntaxException {
      messages.throwIfZero(this.elementCount(elementType), argument0, argument1);
      sourceStack.sendSuccess(() -> (Component)this.dispatch(elementType, messages.onSuccess(), argument0, argument1), broadcast);
      return this.totalValue;
   }

   public int sendFeedback(final CommandSourceStack sourceStack, final boolean broadcast, final CommandResponseTracker.MessagesWithArgs messages, final Object argument0, final Object argument1) throws CommandSyntaxException {
      return this.sendFeedback(sourceStack, broadcast, CommandResponseTracker.ElementType.NON_ZERO, messages, argument0, argument1);
   }

   public static record Dispatch(CommandResponseTracker.SingleHandler onSingle, CommandResponseTracker.MultipleHandler onMultiple) {
   }

   public static record DispatchWithArg(CommandResponseTracker.SingleHandlerWithArg onSingle, CommandResponseTracker.MultipleHandlerWithArg onMultiple) {
   }

   public static record DispatchWithArgs(CommandResponseTracker.SingleHandlerWithArgs onSingle, CommandResponseTracker.MultipleHandlerWithArgs onMultiple) {
   }

   public static enum ElementType {
      ANY,
      NON_ZERO;

      // $FF: synthetic method
      private static CommandResponseTracker.ElementType[] $values() {
         return new CommandResponseTracker.ElementType[]{ANY, NON_ZERO};
      }
   }

   @FunctionalInterface
   public interface ErrorHandler {
      CommandSyntaxException get();
   }

   @FunctionalInterface
   public interface ErrorHandlerWithArg {
      CommandSyntaxException get(Object argument);
   }

   @FunctionalInterface
   public interface ErrorHandlerWithArgs {
      CommandSyntaxException get(Object argument0, Object argument1);
   }

   public static record Messages(CommandResponseTracker.@Nullable ErrorHandler onZero, CommandResponseTracker.Dispatch onSuccess) {
      public void throwIfZero(final int value) throws CommandSyntaxException {
         if (this.onZero != null && value == 0) {
            throw this.onZero.get();
         }
      }
   }

   public static record MessagesWithArg(CommandResponseTracker.@Nullable ErrorHandlerWithArg onZero, CommandResponseTracker.DispatchWithArg onSuccess) {
      public void throwIfZero(final int value, final Object argument) throws CommandSyntaxException {
         if (this.onZero != null && value == 0) {
            throw this.onZero.get(argument);
         }
      }
   }

   public static record MessagesWithArgs(CommandResponseTracker.@Nullable ErrorHandlerWithArgs onZero, CommandResponseTracker.DispatchWithArgs onSuccess) {
      public void throwIfZero(final int value, final Object argument0, final Object argument1) throws CommandSyntaxException {
         if (this.onZero != null && value == 0) {
            throw this.onZero.get(argument0, argument1);
         }
      }
   }

   @FunctionalInterface
   public interface MultipleHandler {
      Object create(int elementCount, int totalValue);
   }

   @FunctionalInterface
   public interface MultipleHandlerWithArg {
      Object create(int elementCount, int totalValue, Object argument);
   }

   @FunctionalInterface
   public interface MultipleHandlerWithArgs {
      Object create(int elementCount, int totalValue, Object argument0, Object argument1);
   }

   @FunctionalInterface
   public interface SingleHandler {
      Object create(Object element, int totalValue);
   }

   @FunctionalInterface
   public interface SingleHandlerWithArg {
      Object create(Object element, int totalValue, Object argument);
   }

   @FunctionalInterface
   public interface SingleHandlerWithArgs {
      Object create(Object element, int totalValue, Object argument0, Object argument1);
   }
}
