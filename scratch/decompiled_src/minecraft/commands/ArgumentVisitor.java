package net.minecraft.commands;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import java.util.Map;
import org.jspecify.annotations.Nullable;

public class ArgumentVisitor {
   public static void visitArguments(final ParseResults command, final ArgumentVisitor.Output output, final boolean rejectRootRedirects) {
      CommandContextBuilder rootContext = command.getContext();
      CommandContextBuilder context = rootContext;
      visitNodeArguments(rootContext, output);

      CommandContextBuilder child;
      while((child = context.getChild()) != null && (!rejectRootRedirects || child.getRootNode() != rootContext.getRootNode())) {
         visitNodeArguments(child, output);
         context = child;
      }

   }

   private static void visitNodeArguments(final CommandContextBuilder context, final ArgumentVisitor.Output output) {
      Map values = context.getArguments();

      for(ParsedCommandNode node : context.getNodes()) {
         CommandNode value = node.getNode();
         if (value instanceof ArgumentCommandNode argument) {
            ParsedArgument value = (ParsedArgument)values.get(argument.getName());
            callVisitor(context, output, argument, value);
         }
      }

   }

   private static void callVisitor(final CommandContextBuilder context, final ArgumentVisitor.Output output, final ArgumentCommandNode argument, final @Nullable ParsedArgument value) {
      output.accept(context, argument, value);
   }

   @FunctionalInterface
   public interface Output {
      void accept(CommandContextBuilder context, ArgumentCommandNode argument, final @Nullable ParsedArgument value);
   }
}
