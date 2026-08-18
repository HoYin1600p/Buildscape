package net.minecraft.commands.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.apache.commons.io.FilenameUtils;

public class ResourceSelectorArgument implements ArgumentType {
   private static final Collection EXAMPLES = List.of("minecraft:*", "*:asset", "*");
   public static final Dynamic2CommandExceptionType ERROR_NO_MATCHES = new Dynamic2CommandExceptionType((selector, registry) -> Component.translatableEscape("argument.resource_selector.not_found", selector, registry));
   private final ResourceKey registryKey;
   private final HolderLookup registryLookup;

   private ResourceSelectorArgument(final CommandBuildContext context, final ResourceKey registryKey) {
      this.registryKey = registryKey;
      this.registryLookup = context.lookupOrThrow(registryKey);
   }

   public Collection parse(final StringReader reader) throws CommandSyntaxException {
      String pattern = ensureNamespaced(readPattern(reader));
      List results = this.registryLookup.listElements().filter((element) -> matches(pattern, element.key().identifier())).toList();
      if (results.isEmpty()) {
         throw ERROR_NO_MATCHES.createWithContext(reader, pattern, this.registryKey.identifier());
      } else {
         return results;
      }
   }

   public static Collection parse(final StringReader reader, final HolderLookup registry) {
      String pattern = ensureNamespaced(readPattern(reader));
      return registry.listElements().filter((element) -> matches(pattern, element.key().identifier())).toList();
   }

   private static String readPattern(final StringReader reader) {
      int start = reader.getCursor();

      while(reader.canRead() && isAllowedPatternCharacter(reader.peek())) {
         reader.skip();
      }

      return reader.getString().substring(start, reader.getCursor());
   }

   private static boolean isAllowedPatternCharacter(final char character) {
      return Identifier.isAllowedInIdentifier(character) || character == '*' || character == '?';
   }

   private static String ensureNamespaced(final String input) {
      return !input.contains(":") ? "minecraft:" + input : input;
   }

   private static boolean matches(final String pattern, final Identifier key) {
      return FilenameUtils.wildcardMatch(key.toString(), pattern);
   }

   public static ResourceSelectorArgument resourceSelector(final CommandBuildContext context, final ResourceKey registry) {
      return new ResourceSelectorArgument(context, registry);
   }

   public static Collection getSelectedResources(final CommandContext context, final String name) {
      return (Collection)context.getArgument(name, Collection.class);
   }

   public CompletableFuture listSuggestions(final CommandContext context, final SuggestionsBuilder builder) {
      return SharedSuggestionProvider.listSuggestions(context, builder, this.registryKey, SharedSuggestionProvider.ElementSuggestionType.ELEMENTS);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   public static class Info implements ArgumentTypeInfo {
      public void serializeToNetwork(final ResourceSelectorArgument.Info.Template template, final FriendlyByteBuf out) {
         out.writeResourceKey(template.registryKey);
      }

      public ResourceSelectorArgument.Info.Template deserializeFromNetwork(final FriendlyByteBuf in) {
         return new ResourceSelectorArgument.Info.Template(in.readRegistryKey());
      }

      public void serializeToJson(final ResourceSelectorArgument.Info.Template template, final JsonObject out) {
         out.addProperty("registry", template.registryKey.identifier().toString());
      }

      public ResourceSelectorArgument.Info.Template unpack(final ResourceSelectorArgument argument) {
         return new ResourceSelectorArgument.Info.Template(argument.registryKey);
      }

      public final class Template implements ArgumentTypeInfo.Template {
         private final ResourceKey registryKey;

         private Template(final ResourceKey registryKey) {
            Objects.requireNonNull(Info.this);
            super();
            this.registryKey = registryKey;
         }

         public ResourceSelectorArgument instantiate(final CommandBuildContext context) {
            return new ResourceSelectorArgument(context, this.registryKey);
         }

         public ArgumentTypeInfo type() {
            return Info.this;
         }
      }
   }
}
