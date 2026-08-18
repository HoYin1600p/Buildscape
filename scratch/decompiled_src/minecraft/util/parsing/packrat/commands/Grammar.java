package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.util.parsing.packrat.DelayedException;
import net.minecraft.util.parsing.packrat.Dictionary;
import net.minecraft.util.parsing.packrat.ErrorCollector;
import net.minecraft.util.parsing.packrat.ErrorEntry;
import net.minecraft.util.parsing.packrat.NamedRule;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.SuggestionSupplier;

public record Grammar(Dictionary rules, NamedRule top) implements CommandArgumentParser {
   public Grammar {
      rules.checkAllBound();
   }

   public Optional parse(final ParseState state) {
      return state.parseTopRule(this.top);
   }

   public Object parseForCommands(final StringReader reader) throws CommandSyntaxException {
      ErrorCollector.LongestOnly errorCollector = new ErrorCollector.LongestOnly();
      StringReaderParserState state = new StringReaderParserState(errorCollector, reader);
      Optional result = this.parse(state);
      if (result.isPresent()) {
         return result.get();
      } else {
         List errorEntries = errorCollector.entries();
         List exceptions = errorEntries.stream().mapMulti((entry, output) -> {
            Object patt0$temp = entry.reason();
            if (patt0$temp instanceof DelayedException delayedException) {
               output.accept(delayedException.create(reader.getString(), entry.cursor()));
            } else {
               patt0$temp = entry.reason();
               if (patt0$temp instanceof Exception exception) {
                  output.accept(exception);
               }
            }

         }).toList();

         for(Exception exception : exceptions) {
            if (exception instanceof CommandSyntaxException) {
               CommandSyntaxException cse = (CommandSyntaxException)exception;
               throw cse;
            }
         }

         if (exceptions.size() == 1) {
            Object var11 = exceptions.get(0);
            if (var11 instanceof RuntimeException) {
               RuntimeException re = (RuntimeException)var11;
               throw re;
            }
         }

         throw new IllegalStateException("Failed to parse: " + (String)errorEntries.stream().map(ErrorEntry::toString).collect(Collectors.joining(", ")));
      }
   }

   public CompletableFuture parseForSuggestions(final SuggestionsBuilder suggestionsBuilder) {
      StringReader reader = new StringReader(suggestionsBuilder.getInput());
      reader.setCursor(suggestionsBuilder.getStart());
      ErrorCollector.LongestOnly errorCollector = new ErrorCollector.LongestOnly();
      StringReaderParserState state = new StringReaderParserState(errorCollector, reader);
      this.parse(state);
      List errorEntries = errorCollector.entries();
      if (errorEntries.isEmpty()) {
         return suggestionsBuilder.buildFuture();
      } else {
         SuggestionsBuilder offsetBuilder = suggestionsBuilder.createOffset(errorCollector.cursor());

         for(ErrorEntry entry : errorEntries) {
            SuggestionSupplier var10 = entry.suggestions();
            if (var10 instanceof ResourceSuggestion) {
               ResourceSuggestion resourceSuggestionTerm = (ResourceSuggestion)var10;
               SharedSuggestionProvider.suggestResource(resourceSuggestionTerm.possibleResources(), offsetBuilder);
            } else {
               SharedSuggestionProvider.suggest(entry.suggestions().possibleValues(state), offsetBuilder);
            }
         }

         return offsetBuilder.buildFuture();
      }
   }
}
