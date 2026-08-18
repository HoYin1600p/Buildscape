package net.minecraft.client.searchtree;

import com.google.common.collect.ImmutableList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

public class IdSearchTree implements SearchTree {
   protected final Comparator additionOrder;
   protected final IdentifierSearchTree identifierSearchTree;

   public IdSearchTree(final Function idGetter, final List contents) {
      ToIntFunction indexLookup = Util.createIndexLookup(contents);
      this.additionOrder = Comparator.comparingInt(indexLookup);
      this.identifierSearchTree = IdentifierSearchTree.create(contents, idGetter);
   }

   public List search(final String text) {
      int colon = text.indexOf(58);
      return colon == -1 ? this.searchPlainText(text) : this.searchIdentifier(text.substring(0, colon).trim(), text.substring(colon + 1).trim());
   }

   protected List searchPlainText(final String text) {
      return this.identifierSearchTree.searchPath(text);
   }

   protected List searchIdentifier(final String namespace, final String path) {
      List namespaces = this.identifierSearchTree.searchNamespace(namespace);
      List paths = this.identifierSearchTree.searchPath(path);
      return ImmutableList.copyOf(new IntersectionIterator(namespaces.iterator(), paths.iterator(), this.additionOrder));
   }
}
