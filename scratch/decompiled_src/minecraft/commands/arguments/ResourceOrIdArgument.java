package net.minecraft.commands.arguments;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.SnbtGrammar;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.util.parsing.packrat.Atom;
import net.minecraft.util.parsing.packrat.Dictionary;
import net.minecraft.util.parsing.packrat.NamedRule;
import net.minecraft.util.parsing.packrat.Term;
import net.minecraft.util.parsing.packrat.commands.Grammar;
import net.minecraft.util.parsing.packrat.commands.IdentifierParseRule;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jspecify.annotations.Nullable;

public class ResourceOrIdArgument implements ArgumentType {
   static final Collection EXAMPLES = List.of("foo", "foo:bar", "012", "{}", "true");
   public static final DynamicCommandExceptionType ERROR_FAILED_TO_PARSE = new DynamicCommandExceptionType((error) -> Component.translatableEscape("argument.resource_or_id.failed_to_parse", error));
   public static final Dynamic2CommandExceptionType ERROR_NO_SUCH_ELEMENT = new Dynamic2CommandExceptionType((id, registry) -> Component.translatableEscape("argument.resource_or_id.no_such_element", id, registry));
   public static final DynamicOps OPS = NbtOps.INSTANCE;
   private final HolderLookup.Provider registryLookup;
   private final Optional elementLookup;
   private final Codec codec;
   private final Grammar grammar;
   private final ResourceKey registryKey;

   protected ResourceOrIdArgument(final CommandBuildContext context, final ResourceKey registryKey, final Codec codec) {
      this.registryLookup = context;
      this.elementLookup = context.lookup(registryKey);
      this.registryKey = registryKey;
      this.codec = codec;
      this.grammar = createGrammar(registryKey, OPS);
   }

   public static Grammar createGrammar(final ResourceKey registryKey, final DynamicOps ops) {
      Grammar inlineValueGrammar = SnbtGrammar.createParser(ops);
      Dictionary rules = new Dictionary();
      Atom result = Atom.of("result");
      Atom id = Atom.of("id");
      Atom value = Atom.of("value");
      rules.put(id, IdentifierParseRule.INSTANCE);
      rules.put(value, inlineValueGrammar.top().value());
      NamedRule topRule = rules.put(result, Term.alternative(rules.named(id), rules.named(value)), (scope) -> {
         Identifier parsedId = (Identifier)scope.get(id);
         if (parsedId != null) {
            return new ResourceOrIdArgument.ReferenceResult(ResourceKey.create(registryKey, parsedId));
         } else {
            Object parsedInline = (O)scope.getOrThrow(value);
            return new ResourceOrIdArgument.InlineResult(parsedInline);
         }
      });
      return new Grammar(rules, topRule);
   }

   public static ResourceOrIdArgument.LootTableArgument lootTable(final CommandBuildContext context) {
      return new ResourceOrIdArgument.LootTableArgument(context);
   }

   public static Holder getLootTable(final CommandContext context, final String name) throws CommandSyntaxException {
      return getResource(context, name);
   }

   public static ResourceOrIdArgument.LootModifierArgument lootModifier(final CommandBuildContext context) {
      return new ResourceOrIdArgument.LootModifierArgument(context);
   }

   public static Holder getLootModifier(final CommandContext context, final String name) {
      return getResource(context, name);
   }

   public static ResourceOrIdArgument.LootPredicateArgument lootPredicate(final CommandBuildContext context) {
      return new ResourceOrIdArgument.LootPredicateArgument(context);
   }

   public static Holder getLootPredicate(final CommandContext context, final String name) {
      return getResource(context, name);
   }

   public static ResourceOrIdArgument.FeatureArgument feature(final CommandBuildContext context) {
      return new ResourceOrIdArgument.FeatureArgument(context);
   }

   public static Holder getFeature(final CommandContext context, final String name) throws CommandSyntaxException {
      return getResource(context, name);
   }

   public static ResourceOrIdArgument.DialogArgument dialog(final CommandBuildContext context) {
      return new ResourceOrIdArgument.DialogArgument(context);
   }

   public static Holder getDialog(final CommandContext context, final String name) {
      return getResource(context, name);
   }

   private static Holder getResource(final CommandContext context, final String name) {
      return (Holder)context.getArgument(name, Holder.class);
   }

   public @Nullable Holder parse(final StringReader reader) throws CommandSyntaxException {
      return this.parse(reader, this.grammar, OPS);
   }

   private @Nullable Holder parse(final StringReader reader, final Grammar grammar, final DynamicOps ops) throws CommandSyntaxException {
      ResourceOrIdArgument.Result contents = (ResourceOrIdArgument.Result)grammar.parseForCommands(reader);
      return this.elementLookup.isEmpty() ? null : contents.parse(reader, this.registryLookup, ops, this.codec, (HolderLookup.RegistryLookup)this.elementLookup.get());
   }

   public CompletableFuture listSuggestions(final CommandContext context, final SuggestionsBuilder builder) {
      return SharedSuggestionProvider.listSuggestions(context, builder, this.registryKey, SharedSuggestionProvider.ElementSuggestionType.ELEMENTS);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   public static class DialogArgument extends ResourceOrIdArgument {
      protected DialogArgument(final CommandBuildContext context) {
         super(context, Registries.DIALOG, Dialog.DIRECT_CODEC);
      }
   }

   public static class FeatureArgument extends ResourceOrIdArgument {
      protected FeatureArgument(final CommandBuildContext context) {
         super(context, Registries.FEATURE, Feature.DIRECT_CODEC);
      }
   }

   public static record InlineResult(Object value) implements ResourceOrIdArgument.Result {
      public Holder parse(final ImmutableStringReader reader, final HolderLookup.Provider lookup, final DynamicOps ops, final Codec codec, final HolderLookup.RegistryLookup elementLookup) throws CommandSyntaxException {
         return Holder.direct(codec.parse(lookup.createSerializationContext(ops), this.value).getOrThrow((msg) -> ResourceOrIdArgument.ERROR_FAILED_TO_PARSE.createWithContext(reader, msg)));
      }
   }

   public static class LootModifierArgument extends ResourceOrIdArgument {
      protected LootModifierArgument(final CommandBuildContext context) {
         super(context, Registries.ITEM_MODIFIER, LootItemFunctions.DIRECT_CODEC);
      }
   }

   public static class LootPredicateArgument extends ResourceOrIdArgument {
      protected LootPredicateArgument(final CommandBuildContext context) {
         super(context, Registries.PREDICATE, LootItemCondition.DIRECT_CODEC);
      }
   }

   public static class LootTableArgument extends ResourceOrIdArgument {
      protected LootTableArgument(final CommandBuildContext context) {
         super(context, Registries.LOOT_TABLE, LootTable.DIRECT_CODEC);
      }
   }

   public static record ReferenceResult(ResourceKey key) implements ResourceOrIdArgument.Result {
      public Holder parse(final ImmutableStringReader reader, final HolderLookup.Provider lookup, final DynamicOps ops, final Codec codec, final HolderLookup.RegistryLookup elementLookup) throws CommandSyntaxException {
         return (Holder)elementLookup.get(this.key).orElseThrow(() -> ResourceOrIdArgument.ERROR_NO_SUCH_ELEMENT.createWithContext(reader, this.key.identifier(), this.key.registry()));
      }
   }

   public sealed interface Result permits ResourceOrIdArgument.InlineResult, ResourceOrIdArgument.ReferenceResult {
      Holder parse(ImmutableStringReader reader, HolderLookup.Provider lookup, DynamicOps ops, Codec codec, HolderLookup.RegistryLookup elementLookup) throws CommandSyntaxException;
   }
}
