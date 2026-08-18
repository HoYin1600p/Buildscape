package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class FunctionArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("foo", "foo:bar", "#foo");
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_TAG = new DynamicCommandExceptionType((tag) -> Component.translatableEscape("arguments.function.tag.unknown", tag));
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_FUNCTION = new DynamicCommandExceptionType((value) -> Component.translatableEscape("arguments.function.unknown", value));

   public static FunctionArgument functions() {
      return new FunctionArgument();
   }

   public FunctionArgument.Result parse(final StringReader reader) throws CommandSyntaxException {
      if (reader.canRead() && reader.peek() == '#') {
         reader.skip();
         final Identifier id = Identifier.read(reader);
         return new FunctionArgument.Result(this) {
            {
               Objects.requireNonNull(this$0);
            }

            public Collection create(final CommandContext c) throws CommandSyntaxException {
               return FunctionArgument.getFunctionTag(c, id);
            }

            public Pair unwrap(final CommandContext context) throws CommandSyntaxException {
               return Pair.of(id, Either.right(FunctionArgument.getFunctionTag(context, id)));
            }

            public Pair unwrapToCollection(final CommandContext context) throws CommandSyntaxException {
               return Pair.of(id, FunctionArgument.getFunctionTag(context, id));
            }
         };
      } else {
         final Identifier id = Identifier.read(reader);
         return new FunctionArgument.Result(this) {
            {
               Objects.requireNonNull(this$0);
            }

            public Collection create(final CommandContext c) throws CommandSyntaxException {
               return Collections.singleton(FunctionArgument.getFunction(c, id));
            }

            public Pair unwrap(final CommandContext context) throws CommandSyntaxException {
               return Pair.of(id, Either.left(FunctionArgument.getFunction(context, id)));
            }

            public Pair unwrapToCollection(final CommandContext context) throws CommandSyntaxException {
               return Pair.of(id, Collections.singleton(FunctionArgument.getFunction(context, id)));
            }
         };
      }
   }

   private static CommandFunction getFunction(final CommandContext c, final Identifier id) throws CommandSyntaxException {
      return (CommandFunction)((CommandSourceStack)c.getSource()).getServer().getFunctions().get(id).orElseThrow(() -> ERROR_UNKNOWN_FUNCTION.create(id.toString()));
   }

   private static Collection getFunctionTag(final CommandContext c, final Identifier id) throws CommandSyntaxException {
      Collection tag = ((CommandSourceStack)c.getSource()).getServer().getFunctions().getTag(id);
      if (tag == null) {
         throw ERROR_UNKNOWN_TAG.create(id.toString());
      } else {
         return tag;
      }
   }

   public static Collection getFunctions(final CommandContext context, final String name) throws CommandSyntaxException {
      return ((FunctionArgument.Result)context.getArgument(name, FunctionArgument.Result.class)).create(context);
   }

   public static Pair getFunctionOrTag(final CommandContext context, final String name) throws CommandSyntaxException {
      return ((FunctionArgument.Result)context.getArgument(name, FunctionArgument.Result.class)).unwrap(context);
   }

   public static Pair getFunctionCollection(final CommandContext context, final String name) throws CommandSyntaxException {
      return ((FunctionArgument.Result)context.getArgument(name, FunctionArgument.Result.class)).unwrapToCollection(context);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   public interface Result {
      Collection create(CommandContext context) throws CommandSyntaxException;

      Pair unwrap(CommandContext context) throws CommandSyntaxException;

      Pair unwrapToCollection(CommandContext context) throws CommandSyntaxException;
   }
}
