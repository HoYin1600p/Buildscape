package net.minecraft.util.parsing.packrat;

import org.jspecify.annotations.Nullable;

public interface Rule {
   @Nullable Object parse(ParseState state);

   static Rule fromTerm(final Term child, final Rule.RuleAction action) {
      return new Rule.WrappedTerm(action, child);
   }

   static Rule fromTerm(final Term child, final Rule.SimpleRuleAction action) {
      return new Rule.WrappedTerm(action, child);
   }

   @FunctionalInterface
   public interface RuleAction {
      @Nullable Object run(ParseState state);
   }

   @FunctionalInterface
   public interface SimpleRuleAction extends Rule.RuleAction {
      Object run(Scope ruleScope);

      default Object run(final ParseState state) {
         return this.run(state.scope());
      }
   }

   public static record WrappedTerm(Rule.RuleAction action, Term child) implements Rule {
      public @Nullable Object parse(final ParseState state) {
         Scope scope = state.scope();
         scope.pushFrame();

         Object var3;
         try {
            if (!this.child.parse(state, scope, Control.UNBOUND)) {
               return null;
            }

            var3 = this.action.run(state);
         } finally {
            scope.popFrame();
         }

         return var3;
      }
   }
}
