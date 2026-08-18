package net.minecraft.util.parsing.packrat;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;

public class Dictionary {
   private final Map terms = new IdentityHashMap();

   public NamedRule put(final Atom name, final Rule entry) {
      Dictionary.Entry holder = (Dictionary.Entry)this.terms.computeIfAbsent(name, Dictionary.Entry::new);
      if (holder.value != null) {
         throw new IllegalArgumentException("Trying to override rule: " + String.valueOf(name));
      } else {
         holder.value = entry;
         return holder;
      }
   }

   public NamedRule putComplex(final Atom name, final Term term, final Rule.RuleAction action) {
      return this.put(name, Rule.fromTerm(term, action));
   }

   public NamedRule put(final Atom name, final Term term, final Rule.SimpleRuleAction action) {
      return this.put(name, Rule.fromTerm(term, action));
   }

   public void checkAllBound() {
      List unboundNames = this.terms.entrySet().stream().filter((e) -> ((Dictionary.Entry)e.getValue()).value == null).map(Map.Entry::getKey).toList();
      if (!unboundNames.isEmpty()) {
         throw new IllegalStateException("Unbound names: " + String.valueOf(unboundNames));
      }
   }

   public NamedRule getOrThrow(final Atom name) {
      return (NamedRule)Objects.requireNonNull((Dictionary.Entry)this.terms.get(name), () -> "No rule called " + String.valueOf(name));
   }

   public NamedRule forward(final Atom name) {
      return this.getOrCreateEntry(name);
   }

   private Dictionary.Entry getOrCreateEntry(final Atom name) {
      return (Dictionary.Entry)this.terms.computeIfAbsent(name, Dictionary.Entry::new);
   }

   public Term named(final Atom name) {
      return new Dictionary.Reference(this.getOrCreateEntry(name), name);
   }

   public Term namedWithAlias(final Atom nameToParse, final Atom nameToStore) {
      return new Dictionary.Reference(this.getOrCreateEntry(nameToParse), nameToStore);
   }

   private static class Entry implements NamedRule, Supplier {
      private final Atom name;
      private @Nullable Rule value;

      private Entry(final Atom name) {
         this.name = name;
      }

      public Atom name() {
         return this.name;
      }

      public Rule value() {
         return (Rule)Objects.requireNonNull(this.value, this);
      }

      public String get() {
         return "Unbound rule " + String.valueOf(this.name);
      }
   }

   private static record Reference(Dictionary.Entry ruleToParse, Atom nameToStore) implements Term {
      public boolean parse(final ParseState state, final Scope scope, final Control control) {
         Object result = (T)state.parse(this.ruleToParse);
         if (result == null) {
            return false;
         } else {
            scope.put(this.nameToStore, result);
            return true;
         }
      }
   }
}
