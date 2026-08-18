package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.concurrent.CompletableFuture;

public abstract class ParserBasedArgument implements ArgumentType {
   private final CommandArgumentParser parser;

   public ParserBasedArgument(final CommandArgumentParser parser) {
      this.parser = parser;
   }

   public Object parse(final StringReader reader) throws CommandSyntaxException {
      return this.parser.parseForCommands(reader);
   }

   public CompletableFuture listSuggestions(final CommandContext context, final SuggestionsBuilder builder) {
      return this.parser.parseForSuggestions(builder);
   }
}
