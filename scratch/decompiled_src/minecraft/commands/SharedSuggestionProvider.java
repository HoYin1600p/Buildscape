package net.minecraft.commands;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.permissions.PermissionSetSupplier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureFlagSet;

public interface SharedSuggestionProvider extends PermissionSetSupplier {
   CharMatcher MATCH_SPLITTER = CharMatcher.anyOf("._/");

   Collection getOnlinePlayerNames();

   default Collection getCustomTabSuggestions() {
      return this.getOnlinePlayerNames();
   }

   default Collection getSelectedEntities() {
      return Collections.emptyList();
   }

   Collection getAllTeams();

   Stream getAvailableSounds();

   Stream getAvailablePostEffects();

   CompletableFuture customSuggestion(CommandContext context);

   default Collection getRelevantCoordinates() {
      return Collections.singleton(SharedSuggestionProvider.TextCoordinates.DEFAULT_GLOBAL);
   }

   default Collection getAbsoluteCoordinates() {
      return Collections.singleton(SharedSuggestionProvider.TextCoordinates.DEFAULT_GLOBAL);
   }

   Set levels();

   RegistryAccess registryAccess();

   FeatureFlagSet enabledFeatures();

   default void suggestRegistryElements(final HolderLookup registry, final SharedSuggestionProvider.ElementSuggestionType elements, final SuggestionsBuilder builder, final Predicate filter) {
      if (elements.shouldSuggestTags()) {
         suggestResource(registry.listTagIds().map(TagKey::location), builder, "#");
      }

      if (elements.shouldSuggestElements()) {
         suggestResource(registry.listElements().filter((holder) -> filter.test(holder.value())).map(Holder.Reference::key).map(ResourceKey::identifier), builder);
      }

   }

   static CompletableFuture listSuggestions(final CommandContext context, final SuggestionsBuilder builder, final ResourceKey registryKey, final SharedSuggestionProvider.ElementSuggestionType type) {
      return listSuggestions(context, builder, registryKey, type, (var0) -> true);
   }

   static CompletableFuture listSuggestions(final CommandContext context, final SuggestionsBuilder builder, final ResourceKey registryKey, final SharedSuggestionProvider.ElementSuggestionType type, final Predicate filter) {
      Object var6 = context.getSource();
      if (var6 instanceof SharedSuggestionProvider suggestionProvider) {
         return suggestionProvider.suggestRegistryElements(registryKey, type, builder, context, filter);
      } else {
         return builder.buildFuture();
      }
   }

   CompletableFuture suggestRegistryElements(final ResourceKey key, final SharedSuggestionProvider.ElementSuggestionType elements, final SuggestionsBuilder builder, final CommandContext context, final Predicate filter);

   static void filterResources(final Iterable values, final String contents, final Function converter, final Consumer consumer) {
      boolean hasNamespace = contents.indexOf(58) > -1;

      for(Object value : values) {
         Identifier id = (Identifier)converter.apply(value);
         if (hasNamespace) {
            String name = id.toString();
            if (matchesSubStr(contents, name)) {
               consumer.accept(value);
            }
         } else if (matchesSubStr(contents, id.getNamespace()) || matchesSubStr(contents, id.getPath())) {
            consumer.accept(value);
         }
      }

   }

   static void filterResources(final Iterable values, final String contents, final String prefix, final Function converter, final Consumer consumer) {
      if (contents.isEmpty()) {
         values.forEach(consumer);
      } else {
         String commonPrefix = Strings.commonPrefix(contents, prefix);
         if (!commonPrefix.isEmpty()) {
            String strippedContents = contents.substring(commonPrefix.length());
            filterResources(values, strippedContents, converter, consumer);
         }
      }

   }

   static CompletableFuture suggestResource(final Iterable values, final SuggestionsBuilder builder, final String prefix) {
      String contents = builder.getRemaining().toLowerCase(Locale.ROOT);
      filterResources(values, contents, prefix, (t) -> t, (v) -> builder.suggest(prefix + String.valueOf(v)));
      return builder.buildFuture();
   }

   static CompletableFuture suggestResource(final Stream values, final SuggestionsBuilder builder, final String prefix) {
      return suggestResource(values::iterator, builder, prefix);
   }

   static CompletableFuture suggestResource(final Iterable values, final SuggestionsBuilder builder) {
      String contents = builder.getRemaining().toLowerCase(Locale.ROOT);
      filterResources(values, contents, (t) -> t, (v) -> builder.suggest(v.toString()));
      return builder.buildFuture();
   }

   static CompletableFuture suggestResource(final Iterable values, final SuggestionsBuilder builder, final Function id, final Function tooltip) {
      String contents = builder.getRemaining().toLowerCase(Locale.ROOT);
      filterResources(values, contents, id, (v) -> builder.suggest(((Identifier)id.apply(v)).toString(), (Message)tooltip.apply(v)));
      return builder.buildFuture();
   }

   static CompletableFuture suggestResource(final Stream values, final SuggestionsBuilder builder) {
      return suggestResource(values::iterator, builder);
   }

   static CompletableFuture suggestResource(final Stream values, final SuggestionsBuilder builder, final Function id, final Function tooltip) {
      return suggestResource(values::iterator, builder, id, tooltip);
   }

   static CompletableFuture suggestCoordinates(final String currentInput, final Collection allSuggestions, final SuggestionsBuilder builder, final Predicate validator) {
      List result = Lists.newArrayList();
      if (Strings.isNullOrEmpty(currentInput)) {
         for(SharedSuggestionProvider.TextCoordinates coordinate : allSuggestions) {
            String fullValue = coordinate.x + " " + coordinate.y + " " + coordinate.z;
            if (validator.test(fullValue)) {
               result.add(coordinate.x);
               result.add(coordinate.x + " " + coordinate.y);
               result.add(fullValue);
            }
         }
      } else {
         String[] fields = currentInput.split(" ");
         if (fields.length == 1) {
            for(SharedSuggestionProvider.TextCoordinates coordinate : allSuggestions) {
               String fullValue = fields[0] + " " + coordinate.y + " " + coordinate.z;
               if (validator.test(fullValue)) {
                  result.add(fields[0] + " " + coordinate.y);
                  result.add(fullValue);
               }
            }
         } else if (fields.length == 2) {
            for(SharedSuggestionProvider.TextCoordinates coordinate : allSuggestions) {
               String fullValue = fields[0] + " " + fields[1] + " " + coordinate.z;
               if (validator.test(fullValue)) {
                  result.add(fullValue);
               }
            }
         }
      }

      return suggest(result, builder);
   }

   static CompletableFuture suggest2DCoordinates(final String currentInput, final Collection allSuggestions, final SuggestionsBuilder builder, final Predicate validator) {
      List result = Lists.newArrayList();
      if (Strings.isNullOrEmpty(currentInput)) {
         for(SharedSuggestionProvider.TextCoordinates coordinate : allSuggestions) {
            String fullValue = coordinate.x + " " + coordinate.z;
            if (validator.test(fullValue)) {
               result.add(coordinate.x);
               result.add(fullValue);
            }
         }
      } else {
         String[] fields = currentInput.split(" ");
         if (fields.length == 1) {
            for(SharedSuggestionProvider.TextCoordinates coordinate : allSuggestions) {
               String fullValue = fields[0] + " " + coordinate.z;
               if (validator.test(fullValue)) {
                  result.add(fullValue);
               }
            }
         }
      }

      return suggest(result, builder);
   }

   static CompletableFuture suggest(final Iterable values, final SuggestionsBuilder builder) {
      String lowerPrefix = builder.getRemaining().toLowerCase(Locale.ROOT);

      for(String name : values) {
         if (matchesSubStr(lowerPrefix, name.toLowerCase(Locale.ROOT))) {
            builder.suggest(name);
         }
      }

      return builder.buildFuture();
   }

   static CompletableFuture suggest(final Stream values, final SuggestionsBuilder builder) {
      String lowerPrefix = builder.getRemaining().toLowerCase(Locale.ROOT);
      values.filter((v) -> matchesSubStr(lowerPrefix, v.toLowerCase(Locale.ROOT))).forEach(builder::suggest);
      return builder.buildFuture();
   }

   static CompletableFuture suggest(final String[] values, final SuggestionsBuilder builder) {
      String lowerPrefix = builder.getRemaining().toLowerCase(Locale.ROOT);

      for(String name : values) {
         if (matchesSubStr(lowerPrefix, name.toLowerCase(Locale.ROOT))) {
            builder.suggest(name);
         }
      }

      return builder.buildFuture();
   }

   static CompletableFuture suggest(final Iterable values, final SuggestionsBuilder builder, final Function toString, final Function tooltip) {
      String lowerPrefix = builder.getRemaining().toLowerCase(Locale.ROOT);

      for(Object value : values) {
         String name = (String)toString.apply(value);
         if (matchesSubStr(lowerPrefix, name.toLowerCase(Locale.ROOT))) {
            builder.suggest(name, (Message)tooltip.apply(value));
         }
      }

      return builder.buildFuture();
   }

   static boolean matchesSubStr(final String pattern, final String input) {
      int indexOfSplitter;
      for(int index = 0; !input.startsWith(pattern, index); index = indexOfSplitter + 1) {
         indexOfSplitter = MATCH_SPLITTER.indexIn(input, index);
         if (indexOfSplitter < 0) {
            return false;
         }
      }

      return true;
   }

   public static enum ElementSuggestionType {
      TAGS,
      ELEMENTS,
      ALL;

      public boolean shouldSuggestTags() {
         return this == TAGS || this == ALL;
      }

      public boolean shouldSuggestElements() {
         return this == ELEMENTS || this == ALL;
      }

      // $FF: synthetic method
      private static SharedSuggestionProvider.ElementSuggestionType[] $values() {
         return new SharedSuggestionProvider.ElementSuggestionType[]{TAGS, ELEMENTS, ALL};
      }
   }

   public static class TextCoordinates {
      public static final SharedSuggestionProvider.TextCoordinates DEFAULT_LOCAL = new SharedSuggestionProvider.TextCoordinates("^", "^", "^");
      public static final SharedSuggestionProvider.TextCoordinates DEFAULT_GLOBAL = new SharedSuggestionProvider.TextCoordinates("~", "~", "~");
      public final String x;
      public final String y;
      public final String z;

      public TextCoordinates(final String x, final String y, final String z) {
         this.x = x;
         this.y = y;
         this.z = z;
      }
   }
}
