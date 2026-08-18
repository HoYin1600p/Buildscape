package net.minecraft.commands.arguments.item;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.parsing.packrat.Atom;
import net.minecraft.util.parsing.packrat.Dictionary;
import net.minecraft.util.parsing.packrat.NamedRule;
import net.minecraft.util.parsing.packrat.Scope;
import net.minecraft.util.parsing.packrat.Term;
import net.minecraft.util.parsing.packrat.commands.Grammar;
import net.minecraft.util.parsing.packrat.commands.IdentifierParseRule;
import net.minecraft.util.parsing.packrat.commands.ResourceLookupRule;
import net.minecraft.util.parsing.packrat.commands.StringReaderTerms;
import net.minecraft.util.parsing.packrat.commands.TagParseRule;

public class ComponentPredicateParser {
   public static Grammar createGrammar(final ComponentPredicateParser.Context context) {
      Atom top = Atom.of("top");
      Atom type = Atom.of("type");
      Atom anyType = Atom.of("any_type");
      Atom elementType = Atom.of("element_type");
      Atom tagType = Atom.of("tag_type");
      Atom conditions = Atom.of("conditions");
      Atom alternatives = Atom.of("alternatives");
      Atom term = Atom.of("term");
      Atom negation = Atom.of("negation");
      Atom test = Atom.of("test");
      Atom componentType = Atom.of("component_type");
      Atom predicateType = Atom.of("predicate_type");
      Atom id = Atom.of("id");
      Atom tag = Atom.of("tag");
      Dictionary rules = new Dictionary();
      NamedRule idRule = rules.put(id, IdentifierParseRule.INSTANCE);
      NamedRule topRule = rules.put(top, Term.alternative(Term.sequence(rules.named(type), StringReaderTerms.character('['), Term.cut(), Term.optional(rules.named(conditions)), StringReaderTerms.character(']')), rules.named(type)), (scope) -> {
         ImmutableList.Builder builder = ImmutableList.builder();
         ((Optional)scope.getOrThrow(type)).ifPresent(builder::add);
         List parsedConditions = (List)scope.get(conditions);
         if (parsedConditions != null) {
            builder.addAll(parsedConditions);
         }

         return builder.build();
      });
      rules.put(type, Term.alternative(rules.named(elementType), Term.sequence(StringReaderTerms.character('#'), Term.cut(), rules.named(tagType)), rules.named(anyType)), (scope) -> Optional.ofNullable(scope.getAny(elementType, tagType)));
      rules.put(anyType, StringReaderTerms.character('*'), (s) -> Unit.INSTANCE);
      rules.put(elementType, new ComponentPredicateParser.ElementLookupRule(idRule, context));
      rules.put(tagType, new ComponentPredicateParser.TagLookupRule(idRule, context));
      rules.put(conditions, Term.sequence(rules.named(alternatives), Term.optional(Term.sequence(StringReaderTerms.character(','), rules.named(conditions)))), (scope) -> {
         Object parsedCondition = (T)context.anyOf((List)scope.getOrThrow(alternatives));
         return (List)Optional.ofNullable((List)scope.get(conditions)).map((rest) -> Util.copyAndAdd(parsedCondition, rest)).orElse(List.of(parsedCondition));
      });
      rules.put(alternatives, Term.sequence(rules.named(term), Term.optional(Term.sequence(StringReaderTerms.character('|'), rules.named(alternatives)))), (scope) -> {
         Object alternative = (T)scope.getOrThrow(term);
         return (List)Optional.ofNullable((List)scope.get(alternatives)).map((rest) -> Util.copyAndAdd(alternative, rest)).orElse(List.of(alternative));
      });
      rules.put(term, Term.alternative(rules.named(test), Term.sequence(StringReaderTerms.character('!'), rules.named(negation))), (scope) -> scope.getAnyOrThrow(test, negation));
      rules.put(negation, rules.named(test), (scope) -> context.negate(scope.getOrThrow(test)));
      rules.putComplex(test, Term.alternative(Term.sequence(rules.named(componentType), StringReaderTerms.character('='), Term.cut(), rules.named(tag)), Term.sequence(rules.named(predicateType), StringReaderTerms.character('~'), Term.cut(), rules.named(tag)), rules.named(componentType)), (state) -> {
         Scope scope = state.scope();
         Object predicate = (P)scope.get(predicateType);

         try {
            if (predicate != null) {
               Dynamic value = (Dynamic)scope.getOrThrow(tag);
               return context.createPredicateTest((ImmutableStringReader)state.input(), predicate, value);
            } else {
               Object component = (C)scope.getOrThrow(componentType);
               Dynamic value = (Dynamic)scope.get(tag);
               return value != null ? context.createComponentTest((ImmutableStringReader)state.input(), component, value) : context.createComponentTest((ImmutableStringReader)state.input(), component);
            }
         } catch (CommandSyntaxException var9) {
            state.errorCollector().store(state.mark(), var9);
            return null;
         }
      });
      rules.put(componentType, new ComponentPredicateParser.ComponentLookupRule(idRule, context));
      rules.put(predicateType, new ComponentPredicateParser.PredicateLookupRule(idRule, context));
      rules.put(tag, new TagParseRule(NbtOps.INSTANCE));
      return new Grammar(rules, topRule);
   }

   private static class ComponentLookupRule extends ResourceLookupRule {
      private ComponentLookupRule(final NamedRule idParser, final ComponentPredicateParser.Context context) {
         super(idParser, context);
      }

      protected Object validateElement(final ImmutableStringReader reader, final Identifier id) throws Exception {
         return ((ComponentPredicateParser.Context)this.context).lookupComponentType(reader, id);
      }

      public Stream possibleResources() {
         return ((ComponentPredicateParser.Context)this.context).listComponentTypes();
      }
   }

   public interface Context {
      Object forElementType(ImmutableStringReader reader, Identifier id) throws CommandSyntaxException;

      Stream listElementTypes();

      Object forTagType(ImmutableStringReader reader, Identifier id) throws CommandSyntaxException;

      Stream listTagTypes();

      Object lookupComponentType(ImmutableStringReader reader, Identifier id) throws CommandSyntaxException;

      Stream listComponentTypes();

      Object createComponentTest(ImmutableStringReader reader, Object componentType, Dynamic value) throws CommandSyntaxException;

      Object createComponentTest(ImmutableStringReader reader, Object componentType);

      Object lookupPredicateType(ImmutableStringReader reader, Identifier id) throws CommandSyntaxException;

      Stream listPredicateTypes();

      Object createPredicateTest(ImmutableStringReader reader, Object predicateType, Dynamic value) throws CommandSyntaxException;

      Object negate(Object value);

      Object anyOf(List alternatives);
   }

   private static class ElementLookupRule extends ResourceLookupRule {
      private ElementLookupRule(final NamedRule idParser, final ComponentPredicateParser.Context context) {
         super(idParser, context);
      }

      protected Object validateElement(final ImmutableStringReader reader, final Identifier id) throws Exception {
         return ((ComponentPredicateParser.Context)this.context).forElementType(reader, id);
      }

      public Stream possibleResources() {
         return ((ComponentPredicateParser.Context)this.context).listElementTypes();
      }
   }

   private static class PredicateLookupRule extends ResourceLookupRule {
      private PredicateLookupRule(final NamedRule idParser, final ComponentPredicateParser.Context context) {
         super(idParser, context);
      }

      protected Object validateElement(final ImmutableStringReader reader, final Identifier id) throws Exception {
         return ((ComponentPredicateParser.Context)this.context).lookupPredicateType(reader, id);
      }

      public Stream possibleResources() {
         return ((ComponentPredicateParser.Context)this.context).listPredicateTypes();
      }
   }

   private static class TagLookupRule extends ResourceLookupRule {
      private TagLookupRule(final NamedRule idParser, final ComponentPredicateParser.Context context) {
         super(idParser, context);
      }

      protected Object validateElement(final ImmutableStringReader reader, final Identifier id) throws Exception {
         return ((ComponentPredicateParser.Context)this.context).forTagType(reader, id);
      }

      public Stream possibleResources() {
         return ((ComponentPredicateParser.Context)this.context).listTagTypes();
      }
   }
}
