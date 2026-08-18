package net.minecraft.network.chat;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.commands.ArgumentVisitor;
import net.minecraft.commands.arguments.SignedArgument;
import org.jspecify.annotations.Nullable;

public record SignableCommand(List arguments) {
   public static boolean hasSignableArguments(final ParseResults command) {
      return !of(command).arguments().isEmpty();
   }

   public static SignableCommand of(final ParseResults command) {
      final String commandString = command.getReader().getString();
      final List arguments = new ArrayList();
      ArgumentVisitor.visitArguments(command, new ArgumentVisitor.Output() {
         public void accept(final CommandContextBuilder context, final ArgumentCommandNode argument, final @Nullable ParsedArgument value) {
            if (value != null && argument.getType() instanceof SignedArgument) {
               String stringValue = value.getRange().get(commandString);
               arguments.add(new SignableCommand.Argument(argument, stringValue));
            }

         }
      }, true);
      return new SignableCommand(arguments);
   }

   public SignableCommand.@Nullable Argument getArgument(final String name) {
      for(SignableCommand.Argument argument : this.arguments) {
         if (name.equals(argument.name())) {
            return argument;
         }
      }

      return null;
   }

   public static record Argument(ArgumentCommandNode node, String value) {
      public String name() {
         return this.node.getName();
      }
   }
}
