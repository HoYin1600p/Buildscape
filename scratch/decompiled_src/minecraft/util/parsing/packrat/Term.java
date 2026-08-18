package net.minecraft.util.parsing.packrat;

import java.util.ArrayList;
import java.util.List;

public interface Term {
   boolean parse(ParseState state, Scope scope, Control control);

   static Term marker(final Atom name, final Object value) {
      return new Term.Marker(name, value);
   }

   @SafeVarargs
   static Term sequence(final Term... terms) {
      return new Term.Sequence(terms);
   }

   @SafeVarargs
   static Term alternative(final Term... terms) {
      return new Term.Alternative(terms);
   }

   static Term optional(final Term term) {
      return new Term.Maybe(term);
   }

   static Term repeated(final NamedRule element, final Atom listName) {
      return repeated(element, listName, 0);
   }

   static Term repeated(final NamedRule element, final Atom listName, final int minRepetitions) {
      return new Term.Repeated(element, listName, minRepetitions);
   }

   static Term repeatedWithTrailingSeparator(final NamedRule element, final Atom listName, final Term separator) {
      return repeatedWithTrailingSeparator(element, listName, separator, 0);
   }

   static Term repeatedWithTrailingSeparator(final NamedRule element, final Atom listName, final Term separator, final int minRepetitions) {
      return new Term.RepeatedWithSeparator(element, listName, separator, minRepetitions, true);
   }

   static Term repeatedWithoutTrailingSeparator(final NamedRule element, final Atom listName, final Term separator) {
      return repeatedWithoutTrailingSeparator(element, listName, separator, 0);
   }

   static Term repeatedWithoutTrailingSeparator(final NamedRule element, final Atom listName, final Term separator, final int minRepetitions) {
      return new Term.RepeatedWithSeparator(element, listName, separator, minRepetitions, false);
   }

   static Term positiveLookahead(final Term term) {
      return new Term.LookAhead(term, true);
   }

   static Term negativeLookahead(final Term term) {
      return new Term.LookAhead(term, false);
   }

   static Term cut() {
      return new Term() {
         public boolean parse(final ParseState state, final Scope scope, final Control control) {
            control.cut();
            return true;
         }

         public String toString() {
            return "↑";
         }
      };
   }

   static Term empty() {
      return new Term() {
         public boolean parse(final ParseState state, final Scope scope, final Control control) {
            return true;
         }

         public String toString() {
            return "ε";
         }
      };
   }

   static Term fail(final Object message) {
      return new Term() {
         public boolean parse(final ParseState state, final Scope scope, final Control control) {
            state.errorCollector().store(state.mark(), message);
            return false;
         }

         public String toString() {
            return "fail";
         }
      };
   }

   public static record Alternative(Term[] elements) implements Term {
      public boolean parse(final ParseState state, final Scope scope, final Control control) {
         Control controlForThis = state.acquireControl();

         try {
            int mark = state.mark();
            scope.splitFrame();

            for(Term element : this.elements) {
               if (element.parse(state, scope, controlForThis)) {
                  scope.mergeFrame();
                  return true;
               }

               scope.clearFrameValues();
               state.restore(mark);
               if (controlForThis.hasCut()) {
                  break;
               }
            }

            scope.popFrame();
            return false;
         } finally {
            state.releaseControl();
         }
      }
   }

   public static record LookAhead(Term term, boolean positive) implements Term {
      public boolean parse(final ParseState state, final Scope scope, final Control control) {
         int mark = state.mark();
         boolean result = this.term.parse(state.silent(), scope, control);
         state.restore(mark);
         return this.positive == result;
      }
   }

   public static record Marker(Atom name, Object value) implements Term {
      public boolean parse(final ParseState state, final Scope scope, final Control control) {
         scope.put(this.name, this.value);
         return true;
      }
   }

   public static record Maybe(Term term) implements Term {
      public boolean parse(final ParseState state, final Scope scope, final Control control) {
         int mark = state.mark();
         if (!this.term.parse(state, scope, control)) {
            state.restore(mark);
         }

         return true;
      }
   }

   public static record Repeated(NamedRule element, Atom listName, int minRepetitions) implements Term {
      public boolean parse(final ParseState state, final Scope scope, final Control control) {
         int mark = state.mark();
         List elements = new ArrayList(this.minRepetitions);

         while(true) {
            int entryMark = state.mark();
            Object parsedElement = (T)state.parse(this.element);
            if (parsedElement == null) {
               state.restore(entryMark);
               if (elements.size() < this.minRepetitions) {
                  state.restore(mark);
                  return false;
               } else {
                  scope.put(this.listName, elements);
                  return true;
               }
            }

            elements.add(parsedElement);
         }
      }
   }

   public static record RepeatedWithSeparator(NamedRule element, Atom listName, Term separator, int minRepetitions, boolean allowTrailingSeparator) implements Term {
      public boolean parse(final ParseState state, final Scope scope, final Control control) {
         int listMark = state.mark();
         List elements = new ArrayList(this.minRepetitions);
         boolean first = true;

         while(true) {
            int markBeforeSeparator = state.mark();
            if (!first && !this.separator.parse(state, scope, control)) {
               state.restore(markBeforeSeparator);
               break;
            }

            int markAfterSeparator = state.mark();
            Object parsedElement = (T)state.parse(this.element);
            if (parsedElement == null) {
               if (first) {
                  state.restore(markAfterSeparator);
               } else {
                  if (!this.allowTrailingSeparator) {
                     state.restore(listMark);
                     return false;
                  }

                  state.restore(markAfterSeparator);
               }
               break;
            }

            elements.add(parsedElement);
            first = false;
         }

         if (elements.size() < this.minRepetitions) {
            state.restore(listMark);
            return false;
         } else {
            scope.put(this.listName, elements);
            return true;
         }
      }
   }

   public static record Sequence(Term[] elements) implements Term {
      public boolean parse(final ParseState state, final Scope scope, final Control control) {
         int mark = state.mark();

         for(Term element : this.elements) {
            if (!element.parse(state, scope, control)) {
               state.restore(mark);
               return false;
            }
         }

         return true;
      }
   }
}
