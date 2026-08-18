package net.minecraft.util.context;

import net.minecraft.resources.Identifier;

public class ContextKey {
   private final Identifier name;

   public ContextKey(final Identifier name) {
      this.name = name;
   }

   public static ContextKey vanilla(final String name) {
      return new ContextKey(Identifier.withDefaultNamespace(name));
   }

   public Identifier name() {
      return this.name;
   }

   public String toString() {
      return "<parameter " + String.valueOf(this.name) + ">";
   }
}
