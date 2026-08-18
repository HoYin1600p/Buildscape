package net.minecraft.client.searchtree;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.resources.Identifier;

public interface IdentifierSearchTree {
   static IdentifierSearchTree empty() {
      return new IdentifierSearchTree() {
         public List searchNamespace(final String namespace) {
            return List.of();
         }

         public List searchPath(final String path) {
            return List.of();
         }
      };
   }

   static IdentifierSearchTree create(final List elements, final Function idGetter) {
      if (elements.isEmpty()) {
         return empty();
      } else {
         final SuffixArray namespaceTree = new SuffixArray();
         final SuffixArray pathTree = new SuffixArray();

         for(Object element : elements) {
            ((Stream)idGetter.apply(element)).forEach((elementId) -> {
               namespaceTree.add(element, elementId.getNamespace().toLowerCase(Locale.ROOT));
               pathTree.add(element, elementId.getPath().toLowerCase(Locale.ROOT));
            });
         }

         namespaceTree.generate();
         pathTree.generate();
         return new IdentifierSearchTree() {
            public List searchNamespace(final String namespace) {
               return namespaceTree.search(namespace);
            }

            public List searchPath(final String path) {
               return pathTree.search(path);
            }
         };
      }
   }

   List searchNamespace(String namespace);

   List searchPath(String path);
}
