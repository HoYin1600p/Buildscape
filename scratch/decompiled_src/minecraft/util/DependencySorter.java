package net.minecraft.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class DependencySorter {
   private final Map contents = new HashMap();

   public DependencySorter addEntry(final Object id, final DependencySorter.Entry value) {
      this.contents.put(id, value);
      return this;
   }

   private void visitDependenciesAndElement(final Multimap dependencies, final Set alreadyVisited, final Object id, final BiConsumer output) {
      if (alreadyVisited.add(id)) {
         dependencies.get(id).forEach((dependency) -> this.visitDependenciesAndElement(dependencies, alreadyVisited, dependency, output));
         DependencySorter.Entry current = (V)((DependencySorter.Entry)this.contents.get(id));
         if (current != null) {
            output.accept(id, current);
         }

      }
   }

   private static boolean isCyclic(final Multimap directDependencies, final Object from, final Object to) {
      Collection dependencies = directDependencies.get(to);
      return dependencies.contains(from) ? true : dependencies.stream().anyMatch((dep) -> isCyclic(directDependencies, from, dep));
   }

   private static void addDependencyIfNotCyclic(final Multimap directDependencies, final Object from, final Object to) {
      if (!isCyclic(directDependencies, from, to)) {
         directDependencies.put(from, to);
      }

   }

   public void orderByDependencies(final BiConsumer output) {
      Multimap directDependencies = HashMultimap.create();
      this.contents.forEach((id, value) -> value.visitRequiredDependencies((dep) -> addDependencyIfNotCyclic(directDependencies, id, dep)));
      this.contents.forEach((id, value) -> value.visitOptionalDependencies((dep) -> addDependencyIfNotCyclic(directDependencies, id, dep)));
      Set alreadyVisited = new HashSet();
      this.contents.keySet().forEach((topId) -> this.visitDependenciesAndElement(directDependencies, alreadyVisited, topId, output));
   }

   public interface Entry {
      void visitRequiredDependencies(final Consumer output);

      void visitOptionalDependencies(final Consumer output);
   }
}
