package net.minecraft.commands.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

public class ResourceKeyArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("foo", "foo:bar", "012");
   private static final DynamicCommandExceptionType ERROR_INVALID_STRUCTURE = new DynamicCommandExceptionType((value) -> Component.translatableEscape("commands.place.structure.invalid", value));
   private static final DynamicCommandExceptionType ERROR_INVALID_TEMPLATE_POOL = new DynamicCommandExceptionType((value) -> Component.translatableEscape("commands.place.jigsaw.invalid", value));
   private static final DynamicCommandExceptionType ERROR_INVALID_RECIPE = new DynamicCommandExceptionType((value) -> Component.translatableEscape("recipe.notFound", value));
   private static final DynamicCommandExceptionType ERROR_INVALID_ADVANCEMENT = new DynamicCommandExceptionType((value) -> Component.translatableEscape("advancement.advancementNotFound", value));
   private final ResourceKey registryKey;
   private final Predicate filter;

   public ResourceKeyArgument(final ResourceKey registryKey) {
      this.registryKey = registryKey;
      this.filter = (var0) -> true;
   }

   public ResourceKeyArgument(final ResourceKey registryKey, final Predicate filter) {
      this.registryKey = registryKey;
      this.filter = filter;
   }

   public static ResourceKeyArgument key(final ResourceKey key) {
      return new ResourceKeyArgument(key);
   }

   public static ResourceKeyArgument key(final ResourceKey key, final Predicate filter) {
      return new ResourceKeyArgument(key, filter);
   }

   public static ResourceKey getRegistryKey(final CommandContext context, final String name, final ResourceKey registryKey, final DynamicCommandExceptionType exceptionType) throws CommandSyntaxException {
      ResourceKey argument = (ResourceKey)context.getArgument(name, ResourceKey.class);
      Optional value = argument.cast(registryKey);
      return (ResourceKey)value.orElseThrow(() -> exceptionType.create(argument.identifier()));
   }

   private static Registry getRegistry(final CommandContext context, final ResourceKey registryKey) {
      return ((CommandSourceStack)context.getSource()).getServer().registryAccess().lookupOrThrow(registryKey);
   }

   private static Holder.Reference resolveKey(final CommandContext context, final String name, final ResourceKey registryKey, final DynamicCommandExceptionType exception) throws CommandSyntaxException {
      ResourceKey key = getRegistryKey(context, name, registryKey, exception);
      return (Holder.Reference)getRegistry(context, registryKey).get(key).orElseThrow(() -> exception.create(key.identifier()));
   }

   public static Holder.Reference getStructure(final CommandContext context, final String name) throws CommandSyntaxException {
      return resolveKey(context, name, Registries.STRUCTURE, ERROR_INVALID_STRUCTURE);
   }

   public static Holder.Reference getStructureTemplatePool(final CommandContext context, final String name) throws CommandSyntaxException {
      return resolveKey(context, name, Registries.TEMPLATE_POOL, ERROR_INVALID_TEMPLATE_POOL);
   }

   public static RecipeHolder getRecipe(final CommandContext context, final String name) throws CommandSyntaxException {
      RecipeManager recipeManager = ((CommandSourceStack)context.getSource()).getServer().getRecipeManager();
      ResourceKey key = getRegistryKey(context, name, Registries.RECIPE, ERROR_INVALID_RECIPE);
      return (RecipeHolder)recipeManager.byKey(key).filter((r) -> !r.value().isSpecial()).orElseThrow(() -> ERROR_INVALID_RECIPE.create(key.identifier()));
   }

   public static AdvancementHolder getAdvancement(final CommandContext context, final String name) throws CommandSyntaxException {
      ResourceKey key = getRegistryKey(context, name, Registries.ADVANCEMENT, ERROR_INVALID_ADVANCEMENT);
      AdvancementHolder advancement = ((CommandSourceStack)context.getSource()).getServer().getAdvancements().get(key.identifier());
      if (advancement == null) {
         throw ERROR_INVALID_ADVANCEMENT.create(key.identifier());
      } else {
         return advancement;
      }
   }

   public ResourceKey parse(final StringReader reader) throws CommandSyntaxException {
      Identifier resourceId = Identifier.read(reader);
      return ResourceKey.create(this.registryKey, resourceId);
   }

   public CompletableFuture listSuggestions(final CommandContext context, final SuggestionsBuilder builder) {
      return SharedSuggestionProvider.listSuggestions(context, builder, this.registryKey, SharedSuggestionProvider.ElementSuggestionType.ELEMENTS, this.filter);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   public static class Info implements ArgumentTypeInfo {
      public void serializeToNetwork(final ResourceKeyArgument.Info.Template template, final FriendlyByteBuf out) {
         out.writeResourceKey(template.registryKey);
      }

      public ResourceKeyArgument.Info.Template deserializeFromNetwork(final FriendlyByteBuf in) {
         return new ResourceKeyArgument.Info.Template(in.readRegistryKey());
      }

      public void serializeToJson(final ResourceKeyArgument.Info.Template template, final JsonObject out) {
         out.addProperty("registry", template.registryKey.identifier().toString());
      }

      public ResourceKeyArgument.Info.Template unpack(final ResourceKeyArgument argument) {
         return new ResourceKeyArgument.Info.Template(argument.registryKey);
      }

      public final class Template implements ArgumentTypeInfo.Template {
         private final ResourceKey registryKey;

         private Template(final ResourceKey registryKey) {
            Objects.requireNonNull(Info.this);
            super();
            this.registryKey = registryKey;
         }

         public ResourceKeyArgument instantiate(final CommandBuildContext context) {
            return new ResourceKeyArgument(this.registryKey);
         }

         public ArgumentTypeInfo type() {
            return Info.this;
         }
      }
   }
}
