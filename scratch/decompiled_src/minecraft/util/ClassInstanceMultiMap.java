package net.minecraft.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ClassInstanceMultiMap extends AbstractCollection {
   private final Map byClass = Maps.newHashMap();
   private final Class baseClass;
   private final List allInstances = Lists.newArrayList();

   public ClassInstanceMultiMap(final Class baseClass) {
      this.baseClass = baseClass;
      this.byClass.put(baseClass, this.allInstances);
   }

   public boolean add(final Object instance) {
      boolean success = false;

      for(Map.Entry entry : this.byClass.entrySet()) {
         if (((Class)entry.getKey()).isInstance(instance)) {
            success |= ((List)entry.getValue()).add(instance);
         }
      }

      return success;
   }

   public boolean remove(final Object object) {
      boolean success = false;

      for(Map.Entry entry : this.byClass.entrySet()) {
         if (((Class)entry.getKey()).isInstance(object)) {
            List list = (List)entry.getValue();
            success |= list.remove(object);
         }
      }

      return success;
   }

   public boolean contains(final Object o) {
      return this.find(o.getClass()).contains(o);
   }

   public Collection find(final Class index) {
      if (!this.baseClass.isAssignableFrom(index)) {
         throw new IllegalArgumentException("Don't know how to search for " + String.valueOf(index));
      } else {
         List instances = (List)this.byClass.computeIfAbsent(index, (k) -> (List)this.allInstances.stream().filter(k::isInstance).collect(Util.toMutableList()));
         return Collections.unmodifiableCollection(instances);
      }
   }

   public Iterator iterator() {
      return (Iterator)(this.allInstances.isEmpty() ? Collections.emptyIterator() : Iterators.unmodifiableIterator(this.allInstances.iterator()));
   }

   public List getAllInstances() {
      return ImmutableList.copyOf(this.allInstances);
   }

   public int size() {
      return this.allInstances.size();
   }
}
