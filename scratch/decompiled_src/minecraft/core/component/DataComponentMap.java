package net.minecraft.core.component;

import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.jspecify.annotations.Nullable;

public interface DataComponentMap extends Iterable, DataComponentGetter {
   DataComponentMap EMPTY = new DataComponentMap() {
      public @Nullable Object get(final DataComponentType type) {
         return null;
      }

      public Set keySet() {
         return Set.of();
      }

      public Iterator iterator() {
         return Collections.emptyIterator();
      }
   };
   Codec CODEC = makeCodecFromMap(DataComponentType.VALUE_MAP_CODEC);

   static Codec makeCodec(final Codec componentTypeCodec) {
      return makeCodecFromMap(Codec.dispatchedMap(componentTypeCodec, DataComponentType::codecOrThrow));
   }

   static Codec makeCodecFromMap(final Codec mapCodec) {
      return mapCodec.flatComapMap(DataComponentMap.Builder::buildFromMapTrusted, (components) -> {
         int size = components.size();
         if (size == 0) {
            return DataResult.success(Reference2ObjectMaps.emptyMap());
         } else {
            Reference2ObjectMap map = new Reference2ObjectArrayMap(size);

            for(TypedDataComponent entry : components) {
               if (!entry.type().isTransient()) {
                  map.put(entry.type(), entry.value());
               }
            }

            return DataResult.success(map);
         }
      });
   }

   static DataComponentMap composite(final DataComponentMap prototype, final DataComponentMap overrides) {
      return new DataComponentMap() {
         public @Nullable Object get(final DataComponentType type) {
            Object value = (T)overrides.get(type);
            return value != null ? value : prototype.get(type);
         }

         public Set keySet() {
            return Sets.union(prototype.keySet(), overrides.keySet());
         }
      };
   }

   static DataComponentMap.Builder builder() {
      return new DataComponentMap.Builder();
   }

   Set keySet();

   default boolean has(final DataComponentType type) {
      return this.get(type) != null;
   }

   default Iterator iterator() {
      return Iterators.transform(this.keySet().iterator(), (type) -> (TypedDataComponent)Objects.requireNonNull(this.getTyped(type)));
   }

   default Stream stream() {
      return StreamSupport.stream(Spliterators.spliterator(this.iterator(), (long)this.size(), 1345), false);
   }

   default int size() {
      return this.keySet().size();
   }

   default boolean isEmpty() {
      return this.size() == 0;
   }

   default DataComponentMap filter(final Predicate predicate) {
      return new DataComponentMap() {
         {
            Objects.requireNonNull(DataComponentMap.this);
         }

         public @Nullable Object get(final DataComponentType type) {
            return predicate.test(type) ? DataComponentMap.this.get(type) : null;
         }

         public Set keySet() {
            return Sets.filter(DataComponentMap.this.keySet(), predicate::test);
         }
      };
   }

   public static class Builder {
      private final Reference2ObjectMap map = new Reference2ObjectArrayMap();
      private Consumer validator = (components) -> {
      };

      private Builder() {
      }

      public DataComponentMap.Builder set(final DataComponentType type, final @Nullable Object value) {
         this.setUnchecked(type, value);
         return this;
      }

      void setUnchecked(final DataComponentType type, final @Nullable Object value) {
         if (value != null) {
            this.map.put(type, value);
         } else {
            this.map.remove(type);
         }

      }

      public DataComponentMap.Builder addAll(final DataComponentMap map) {
         for(TypedDataComponent entry : map) {
            this.map.put(entry.type(), entry.value());
         }

         return this;
      }

      public DataComponentMap.Builder addValidator(final Consumer newValidator) {
         this.validator = this.validator.andThen(newValidator);
         return this;
      }

      public DataComponentMap build() {
         DataComponentMap result = buildFromMapTrusted(this.map);
         this.validator.accept(result);
         return result;
      }

      private static DataComponentMap buildFromMapTrusted(final Map map) {
         if (map.isEmpty()) {
            return DataComponentMap.EMPTY;
         } else {
            return map.size() < 8 ? new DataComponentMap.Builder.SimpleMap(new Reference2ObjectArrayMap(map)) : new DataComponentMap.Builder.SimpleMap(new Reference2ObjectOpenHashMap(map));
         }
      }

      private static record SimpleMap(Reference2ObjectMap map) implements DataComponentMap {
         public @Nullable Object get(final DataComponentType type) {
            return this.map.get(type);
         }

         public boolean has(final DataComponentType type) {
            return this.map.containsKey(type);
         }

         public Set keySet() {
            return this.map.keySet();
         }

         public Iterator iterator() {
            return Iterators.transform(Reference2ObjectMaps.fastIterator(this.map), TypedDataComponent::fromEntryUnchecked);
         }

         public int size() {
            return this.map.size();
         }

         public String toString() {
            return this.map.toString();
         }
      }
   }
}
