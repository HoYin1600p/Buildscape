package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface CommandArgumentParser {
   Object parseForCommands(StringReader reader) throws CommandSyntaxException;

   CompletableFuture parseForSuggestions(SuggestionsBuilder suggestionsBuilder);

   default CommandArgumentParser mapResult(final Function mapper) {
      return new CommandArgumentParser() {
         {
            Objects.requireNonNull(CommandArgumentParser.this);
         }

         public Object parseForCommands(final StringReader reader) throws CommandSyntaxException {
            return mapper.apply(CommandArgumentParser.this.parseForCommands(reader));
         }

         public CompletableFuture parseForSuggestions(final SuggestionsBuilder suggestionsBuilder) {
            return CommandArgumentParser.this.parseForSuggestions(suggestionsBuilder);
         }
      };
   }

   default CommandArgumentParser withCodec(final DynamicOps ops, final CommandArgumentParser valueParser, final Codec codec, final DynamicCommandExceptionType exceptionType) {
      return new CommandArgumentParser() {
         {
            Objects.requireNonNull(CommandArgumentParser.this);
         }

         public Object parseForCommands(final StringReader reader) throws CommandSyntaxException {
            int cursor = reader.getCursor();
            Object tag = (O)valueParser.parseForCommands(reader);
            DataResult result = codec.parse(ops, tag);
            return result.getOrThrow((message) -> {
               reader.setCursor(cursor);
               return exceptionType.createWithContext(reader, message);
            });
         }

         public CompletableFuture parseForSuggestions(final SuggestionsBuilder suggestionsBuilder) {
            return CommandArgumentParser.this.parseForSuggestions(suggestionsBuilder);
         }
      };
   }
}
