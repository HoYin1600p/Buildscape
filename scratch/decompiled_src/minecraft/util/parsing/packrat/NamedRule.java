package net.minecraft.util.parsing.packrat;

public interface NamedRule {
   Atom name();

   Rule value();
}
