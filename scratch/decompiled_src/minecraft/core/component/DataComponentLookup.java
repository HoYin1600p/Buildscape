package net.minecraft.core.component;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

public class DataComponentLookup {
   private final Iterable elements;
   private volatile Map cache = Map.of();

   public DataComponentLookup(final Iterable elements) {
      this.elements = elements;
   }

   private DataComponentLookup.@Nullable ComponentStorage getFromCache(final DataComponentType type) {
      return (DataComponentLookup.ComponentStorage)this.cache.get(type);
   }

   private DataComponentLookup.ComponentStorage getOrCreateStorage(final DataComponentType type) {
      DataComponentLookup.ComponentStorage existingStorage = this.getFromCache(type);
      if (existingStorage != null) {
         return existingStorage;
      } else {
         DataComponentLookup.ComponentStorage newStorage = this.scanForComponents(type);
         synchronized(this) {
            DataComponentLookup.ComponentStorage foreignStorage = this.getFromCache(type);
            if (foreignStorage != null) {
               return foreignStorage;
            } else {
               this.cache = Util.copyAndPut(this.cache, type, newStorage);
               return newStorage;
            }
         }
      }
   }

   private DataComponentLookup.ComponentStorage scanForComponents(final DataComponentType type) {
      ImmutableMultimap.Builder results = ImmutableMultimap.builder();

      for(Holder element : this.elements) {
         Object componentValue = (C)element.components().get(type);
         if (componentValue != null) {
            results.put(componentValue, element);
         }
      }

      return new DataComponentLookup.ComponentStorage(results.build());
   }

   public Stream findMatching(final DataComponentType type, final Predicate predicate) {
      return this.getOrCreateStorage(type).findMatching(predicate);
   }

   public Collection findAll(final DataComponentType type, final Object value) {
      return this.getOrCreateStorage(type).findAll(value);
   }

   public Collection findAll(final DataComponentType type) {
      return this.getOrCreateStorage(type).valueToComponent.values();
   }

   private static record ComponentStorage(Multimap valueToComponent) {
      public Collection findAll(final Object value) {
         return this.valueToComponent.get(value);
      }

      public Stream findMatching(final Predicate predicate) {
         return this.valueToComponent.isEmpty() ? Stream.empty() : this.valueToComponent.entries().stream().filter((e) -> predicate.test(e.getKey())).map(Map.Entry::getValue);
      }
   }
}
