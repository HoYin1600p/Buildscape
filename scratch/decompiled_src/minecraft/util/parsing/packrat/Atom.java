package net.minecraft.util.parsing.packrat;

public record Atom(String name) {
   public String toString() {
      return "<" + this.name + ">";
   }

   public static Atom of(final String name) {
      return new Atom(name);
   }
}
