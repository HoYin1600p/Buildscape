package net.minecraft.client.searchtree;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;

@FunctionalInterface
public interface SearchTree {
   static SearchTree empty() {
      return (text) -> List.of();
   }

   static SearchTree plainText(final List elements, final Function idGetter) {
      if (elements.isEmpty()) {
         return empty();
      } else {
         SuffixArray tree = new SuffixArray();

         for(Object element : elements) {
            ((Stream)idGetter.apply(element)).forEach((elementId) -> tree.add(element, elementId.toLowerCase(Locale.ROOT)));
         }

         tree.generate();
         return tree::search;
      }
   }

   List search(String text);
}
