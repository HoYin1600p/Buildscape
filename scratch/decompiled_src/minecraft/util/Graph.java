package net.minecraft.util;

import com.google.common.collect.ImmutableSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public final class Graph {
   private Graph() {
   }

   public static boolean depthFirstSearch(final Map edges, final Set discovered, final Set currentlyVisiting, final Consumer reverseTopologicalOrder, final Object current) {
      if (discovered.contains(current)) {
         return false;
      } else if (currentlyVisiting.contains(current)) {
         return true;
      } else {
         currentlyVisiting.add(current);

         for(Object next : (Set)edges.getOrDefault(current, ImmutableSet.of())) {
            if (depthFirstSearch(edges, discovered, currentlyVisiting, reverseTopologicalOrder, next)) {
               return true;
            }
         }

         currentlyVisiting.remove(current);
         discovered.add(current);
         reverseTopologicalOrder.accept(current);
         return false;
      }
   }
}
