package net.minecraft.client.searchtree;

import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.resources.Identifier;

public class FullTextSearchTree extends IdSearchTree {
   private final SearchTree plainTextSearchTree;

   public FullTextSearchTree(final Function nameGetter, final Function idGetter, final List contents) {
      super(idGetter, contents);
      this.plainTextSearchTree = SearchTree.plainText(contents, nameGetter);
   }

   protected List searchPlainText(final String text) {
      return this.plainTextSearchTree.search(text);
   }

   protected List searchIdentifier(final String namespace, final String path) {
      List namespaces = this.identifierSearchTree.searchNamespace(namespace);
      List paths = this.identifierSearchTree.searchPath(path);
      List names = this.plainTextSearchTree.search(path);
      Iterator mergedPathsAndNames = new MergingUniqueIterator(paths.iterator(), names.iterator(), this.additionOrder);
      return ImmutableList.copyOf(new IntersectionIterator(namespaces.iterator(), mergedPathsAndNames, this.additionOrder));
   }
}
