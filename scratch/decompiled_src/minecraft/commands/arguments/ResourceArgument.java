package net.minecraft.commands.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;

public class ResourceArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("foo", "foo:bar", "012");
   private static final DynamicCommandExceptionType ERROR_NOT_SUMMONABLE_ENTITY = new DynamicCommandExceptionType((value) -> Component.translatableEscape("entity.not_summonable", value));
   public static final Dynamic2CommandExceptionType ERROR_UNKNOWN_RESOURCE = new Dynamic2CommandExceptionType((id, registry) -> Component.translatableEscape("argument.resource.not_found", id, registry));
   public static final Dynamic3CommandExceptionType ERROR_INVALID_RESOURCE_TYPE = new Dynamic3CommandExceptionType((id, actualRegistry, expectedRegistry) -> Component.translatableEscape("argument.resource.invalid_type", id, actualRegistry, expectedRegistry));
   private final ResourceKey registryKey;
   private final HolderLookup registryLookup;

   public ResourceArgument(final CommandBuildContext context, final ResourceKey registryKey) {
      this.registryKey = registryKey;
      this.registryLookup = context.lookupOrThrow(registryKey);
   }

   public static ResourceArgument resource(final CommandBuildContext context, final ResourceKey key) {
      return new ResourceArgument(context, key);
   }

   public static Holder.Reference getResource(final CommandContext context, final String name, final ResourceKey registryKey) throws CommandSyntaxException {
      Holder.Reference argument = (Holder.Reference)context.getArgument(name, Holder.Reference.class);
      ResourceKey argumentKey = argument.key();
      if (argumentKey.isFor(registryKey)) {
         return argument;
      } else {
         throw ERROR_INVALID_RESOURCE_TYPE.create(argumentKey.identifier(), argumentKey.registry(), registryKey.identifier());
      }
   }

   public static Holder.Reference getAttribute(final CommandContext context, final String name) throws CommandSyntaxException {
      return getResource(context, name, Registries.ATTRIBUTE);
   }

   public static Holder.Reference getSummonableEntityType(final CommandContext context, final String name) throws CommandSyntaxException {
      Holder.Reference result = getResource(context, name, Registries.ENTITY_TYPE);
      if (!((EntityType)result.value()).canSummon()) {
         throw ERROR_NOT_SUMMONABLE_ENTITY.create(result.key().identifier().toString());
      } else {
         return result;
      }
   }

   public static Holder.Reference getMobEffect(final CommandContext context, final String name) throws CommandSyntaxException {
      return getResource(context, name, Registries.MOB_EFFECT);
   }

   public static Holder.Reference getEnchantment(final CommandContext context, final String name) throws CommandSyntaxException {
      return getResource(context, name, Registries.ENCHANTMENT);
   }

   public static Holder.Reference getClock(final CommandContext context, final String name) throws CommandSyntaxException {
      return getResource(context, name, Registries.WORLD_CLOCK);
   }

   public static Holder.Reference getTimeline(final CommandContext context, final String name) throws CommandSyntaxException {
      return getResource(context, name, Registries.TIMELINE);
   }

   public Holder.Reference parse(final StringReader reader) throws CommandSyntaxException {
      Identifier resourceId = Identifier.read(reader);
      ResourceKey keyInRegistry = ResourceKey.create(this.registryKey, resourceId);
      return (Holder.Reference)this.registryLookup.get(keyInRegistry).orElseThrow(() -> ERROR_UNKNOWN_RESOURCE.createWithContext(reader, resourceId, this.registryKey.identifier()));
   }

   public CompletableFuture listSuggestions(final CommandContext context, final SuggestionsBuilder builder) {
      return SharedSuggestionProvider.listSuggestions(context, builder, this.registryKey, SharedSuggestionProvider.ElementSuggestionType.ELEMENTS);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   public static class Info implements ArgumentTypeInfo {
      public void serializeToNetwork(final ResourceArgument.Info.Template template, final FriendlyByteBuf out) {
         out.writeResourceKey(template.registryKey);
      }

      public ResourceArgument.Info.Template deserializeFromNetwork(final FriendlyByteBuf in) {
         return new ResourceArgument.Info.Template(in.readRegistryKey());
      }

      public void serializeToJson(final ResourceArgument.Info.Template template, final JsonObject out) {
         out.addProperty("registry", template.registryKey.identifier().toString());
      }

      public ResourceArgument.Info.Template unpack(final ResourceArgument argument) {
         return new ResourceArgument.Info.Template(argument.registryKey);
      }

      public final class Template implements ArgumentTypeInfo.Template {
         private final ResourceKey registryKey;

         private Template(final ResourceKey registryKey) {
            Objects.requireNonNull(Info.this);
            super();
            this.registryKey = registryKey;
         }

         public ResourceArgument instantiate(final CommandBuildContext context) {
            return new ResourceArgument(context, this.registryKey);
         }

         public ArgumentTypeInfo type() {
            return Info.this;
         }
      }
   }
}
