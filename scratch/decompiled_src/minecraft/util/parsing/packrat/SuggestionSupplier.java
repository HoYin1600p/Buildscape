package net.minecraft.util.parsing.packrat;

import java.util.stream.Stream;

public interface SuggestionSupplier {
   Stream possibleValues(ParseState state);

   static SuggestionSupplier empty() {
      return (state) -> Stream.empty();
   }
}
