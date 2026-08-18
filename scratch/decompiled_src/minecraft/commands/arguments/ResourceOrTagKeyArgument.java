package net.minecraft.commands.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Either;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

public class ResourceOrTagKeyArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("foo", "foo:bar", "012", "#skeletons", "#minecraft:skeletons");
   private final ResourceKey registryKey;

   public ResourceOrTagKeyArgument(final ResourceKey registryKey) {
      this.registryKey = registryKey;
   }

   public static ResourceOrTagKeyArgument resourceOrTagKey(final ResourceKey key) {
      return new ResourceOrTagKeyArgument(key);
   }

   public static ResourceOrTagKeyArgument.Result getResourceOrTagKey(final CommandContext context, final String name, final ResourceKey registryKey, final DynamicCommandExceptionType exceptionType) throws CommandSyntaxException {
      ResourceOrTagKeyArgument.Result argument = (ResourceOrTagKeyArgument.Result)context.getArgument(name, ResourceOrTagKeyArgument.Result.class);
      Optional value = argument.cast(registryKey);
      return (ResourceOrTagKeyArgument.Result)value.orElseThrow(() -> exceptionType.create(argument));
   }

   public ResourceOrTagKeyArgument.Result parse(final StringReader reader) throws CommandSyntaxException {
      if (reader.canRead() && reader.peek() == '#') {
         int cursor = reader.getCursor();

         try {
            reader.skip();
            Identifier tagId = Identifier.read(reader);
            return new ResourceOrTagKeyArgument.TagResult(TagKey.create(this.registryKey, tagId));
         } catch (CommandSyntaxException var4) {
            reader.setCursor(cursor);
            throw var4;
         }
      } else {
         Identifier resourceId = Identifier.read(reader);
         return new ResourceOrTagKeyArgument.ResourceResult(ResourceKey.create(this.registryKey, resourceId));
      }
   }

   public CompletableFuture listSuggestions(final CommandContext context, final SuggestionsBuilder builder) {
      return SharedSuggestionProvider.listSuggestions(context, builder, this.registryKey, SharedSuggestionProvider.ElementSuggestionType.ALL);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   public static class Info implements ArgumentTypeInfo {
      public void serializeToNetwork(final ResourceOrTagKeyArgument.Info.Template template, final FriendlyByteBuf out) {
         out.writeResourceKey(template.registryKey);
      }

      public ResourceOrTagKeyArgument.Info.Template deserializeFromNetwork(final FriendlyByteBuf in) {
         return new ResourceOrTagKeyArgument.Info.Template(in.readRegistryKey());
      }

      public void serializeToJson(final ResourceOrTagKeyArgument.Info.Template template, final JsonObject out) {
         out.addProperty("registry", template.registryKey.identifier().toString());
      }

      public ResourceOrTagKeyArgument.Info.Template unpack(final ResourceOrTagKeyArgument argument) {
         return new ResourceOrTagKeyArgument.Info.Template(argument.registryKey);
      }

      public final class Template implements ArgumentTypeInfo.Template {
         private final ResourceKey registryKey;

         private Template(final ResourceKey registryKey) {
            Objects.requireNonNull(Info.this);
            super();
            this.registryKey = registryKey;
         }

         public ResourceOrTagKeyArgument instantiate(final CommandBuildContext context) {
            return new ResourceOrTagKeyArgument(this.registryKey);
         }

         public ArgumentTypeInfo type() {
            return Info.this;
         }
      }
   }

   private static record ResourceResult(ResourceKey key) implements ResourceOrTagKeyArgument.Result {
      public Either unwrap() {
         return Either.left(this.key);
      }

      public Optional cast(final ResourceKey registryKey) {
         return this.key.cast(registryKey).map(ResourceOrTagKeyArgument.ResourceResult::new);
      }

      public boolean test(final Holder holder) {
         return holder.is(this.key);
      }

      public String asPrintable() {
         return this.key.identifier().toString();
      }
   }

   public interface Result extends Predicate {
      Either unwrap();

      Optional cast(final ResourceKey registryKey);

      String asPrintable();
   }

   private static record TagResult(TagKey key) implements ResourceOrTagKeyArgument.Result {
      public Either unwrap() {
         return Either.right(this.key);
      }

      public Optional cast(final ResourceKey registryKey) {
         return this.key.cast(registryKey).map(ResourceOrTagKeyArgument.TagResult::new);
      }

      public boolean test(final Holder holder) {
         return holder.is(this.key);
      }

      public String asPrintable() {
         return "#" + String.valueOf(this.key.location());
      }
   }
}
