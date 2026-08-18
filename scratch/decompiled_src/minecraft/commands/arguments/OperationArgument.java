package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.scores.ScoreAccess;

public class OperationArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("=", ">", "<");
   private static final SimpleCommandExceptionType ERROR_INVALID_OPERATION = new SimpleCommandExceptionType(Component.translatable("arguments.operation.invalid"));
   private static final SimpleCommandExceptionType ERROR_DIVIDE_BY_ZERO = new SimpleCommandExceptionType(Component.translatable("arguments.operation.div0"));

   public static OperationArgument operation() {
      return new OperationArgument();
   }

   public static OperationArgument.Operation getOperation(final CommandContext context, final String name) {
      return (OperationArgument.Operation)context.getArgument(name, OperationArgument.Operation.class);
   }

   public OperationArgument.Operation parse(final StringReader reader) throws CommandSyntaxException {
      if (!reader.canRead()) {
         throw ERROR_INVALID_OPERATION.createWithContext(reader);
      } else {
         int start = reader.getCursor();

         while(reader.canRead() && reader.peek() != ' ') {
            reader.skip();
         }

         return getOperation(reader.getString().substring(start, reader.getCursor()));
      }
   }

   public CompletableFuture listSuggestions(final CommandContext context, final SuggestionsBuilder builder) {
      return SharedSuggestionProvider.suggest(new String[]{"=", "+=", "-=", "*=", "/=", "%=", "<", ">", "><"}, builder);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   private static OperationArgument.Operation getOperation(final String op) throws CommandSyntaxException {
      return (OperationArgument.Operation)(op.equals("><") ? (a, b) -> {
         int swap = a.get();
         a.set(b.get());
         b.set(swap);
      } : getSimpleOperation(op));
   }

   private static OperationArgument.SimpleOperation getSimpleOperation(final String op) throws CommandSyntaxException {
      OperationArgument.SimpleOperation var10000;
      switch (op) {
         case "=":
            var10000 = (a, b) -> b;
            break;
         case "+=":
            var10000 = Integer::sum;
            break;
         case "-=":
            var10000 = (a, b) -> a - b;
            break;
         case "*=":
            var10000 = (a, b) -> a * b;
            break;
         case "/=":
            var10000 = (a, b) -> {
               if (b == 0) {
                  throw ERROR_DIVIDE_BY_ZERO.create();
               } else {
                  return Mth.floorDiv(a, b);
               }
            };
            break;
         case "%=":
            var10000 = (a, b) -> {
               if (b == 0) {
                  throw ERROR_DIVIDE_BY_ZERO.create();
               } else {
                  return Mth.positiveModulo(a, b);
               }
            };
            break;
         case "<":
            var10000 = Math::min;
            break;
         case ">":
            var10000 = Math::max;
            break;
         default:
            throw ERROR_INVALID_OPERATION.create();
      }

      return var10000;
   }

   @FunctionalInterface
   public interface Operation {
      void apply(ScoreAccess a, ScoreAccess b) throws CommandSyntaxException;
   }

   @FunctionalInterface
   private interface SimpleOperation extends OperationArgument.Operation {
      int apply(int a, int b) throws CommandSyntaxException;

      default void apply(final ScoreAccess a, final ScoreAccess b) throws CommandSyntaxException {
         a.set(this.apply(a.get(), b.get()));
      }
   }
}
