package net.minecraft.server.commands;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public interface ArgProvider {
   Object access(CommandContext context) throws CommandSyntaxException;

   ArgumentBuilder wrap(ArgumentBuilder parent, Function function);

   static ArgProvider create(final String key, final Supplier child, final InCommandFunction access) {
      return new ArgProvider() {
         public Object access(final CommandContext context) throws CommandSyntaxException {
            return access.apply(context);
         }

         public ArgumentBuilder wrap(final ArgumentBuilder parent, final Function function) {
            return parent.then(Commands.literal(key).then((ArgumentBuilder)function.apply((ArgumentBuilder)child.get())));
         }
      };
   }

   static List buildList(final String argName, final List factories) {
      ImmutableList.Builder result = ImmutableList.builderWithExpectedSize(factories.size());

      for(ArgProvider.Factory factory : factories) {
         result.add(factory.create(argName));
      }

      return result.build();
   }

   @FunctionalInterface
   public interface Factory {
      ArgProvider create(String arg);
   }
}
