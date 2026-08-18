package net.minecraft.core.component;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.objects.ObjectIterable;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public final class DataComponentPatch {
   public static final DataComponentPatch EMPTY = new DataComponentPatch(Reference2ObjectMaps.emptyMap());
   public static final Codec CODEC = Codec.dispatchedMap(DataComponentPatch.PatchKey.CODEC, DataComponentPatch.PatchKey::valueCodec).xmap((data) -> {
      if (data.isEmpty()) {
         return EMPTY;
      } else {
         Reference2ObjectMap map = new Reference2ObjectArrayMap(data.size());

         for(Map.Entry entry : data.entrySet()) {
            map.put(((DataComponentPatch.PatchKey)entry.getKey()).type(), entry.getValue());
         }

         return new DataComponentPatch(map);
      }
   }, (patch) -> {
      Reference2ObjectMap map = new Reference2ObjectArrayMap(patch.map.size());
      Iterator i$ = Reference2ObjectMaps.fastIterable(patch.map).iterator();

      while(i$.hasNext()) {
         Map.Entry entry = (Map.Entry)i$.next();
         DataComponentType type = (DataComponentType)entry.getKey();
         if (!type.isTransient()) {
            Object value = entry.getValue();
            map.put(new DataComponentPatch.PatchKey(type, Removed.isRemoved(value)), value);
         }
      }

      return map;
   });
   public static final StreamCodec STREAM_CODEC = createStreamCodec(new DataComponentPatch.CodecGetter() {
      public StreamCodec apply(final DataComponentType type) {
         return type.streamCodec().cast();
      }
   });
   public static final StreamCodec DELIMITED_STREAM_CODEC = createStreamCodec(new DataComponentPatch.CodecGetter() {
      public StreamCodec apply(final DataComponentType type) {
         StreamCodec original = type.streamCodec().cast();
         return original.apply(ByteBufCodecs.registryFriendlyLengthPrefixed(Integer.MAX_VALUE));
      }
   });
   private static final String REMOVED_PREFIX = "!";
   final Reference2ObjectMap map;

   private static StreamCodec createStreamCodec(final DataComponentPatch.CodecGetter codecGetter) {
      return new StreamCodec() {
         public DataComponentPatch decode(final RegistryFriendlyByteBuf input) {
            int positiveCount = input.readVarInt();
            int negativeCount = input.readVarInt();
            if (positiveCount == 0 && negativeCount == 0) {
               return DataComponentPatch.EMPTY;
            } else {
               int expectedSize = positiveCount + negativeCount;
               Reference2ObjectMap map = new Reference2ObjectArrayMap(Math.min(expectedSize, 65536));

               for(int i = 0; i < positiveCount; ++i) {
                  DataComponentType type = (DataComponentType)DataComponentType.STREAM_CODEC.decode(input);
                  Object value = codecGetter.apply(type).decode(input);
                  map.put(type, value);
               }

               for(int i = 0; i < negativeCount; ++i) {
                  DataComponentType type = (DataComponentType)DataComponentType.STREAM_CODEC.decode(input);
                  map.put(type, Removed.INSTANCE);
               }

               return new DataComponentPatch(map);
            }
         }

         public void encode(final RegistryFriendlyByteBuf output, final DataComponentPatch patch) {
            if (patch.isEmpty()) {
               output.writeVarInt(0);
               output.writeVarInt(0);
            } else {
               ObjectIterable fastEntries = Reference2ObjectMaps.fastIterable(patch.map);
               int positiveCount = 0;
               int negativeCount = 0;
               ObjectIterator var6 = fastEntries.iterator();

               while(var6.hasNext()) {
                  Reference2ObjectMap.Entry entry = (Reference2ObjectMap.Entry)var6.next();
                  if (Removed.isNotRemoved(entry.getValue())) {
                     ++positiveCount;
                  } else {
                     ++negativeCount;
                  }
               }

               output.writeVarInt(positiveCount);
               output.writeVarInt(negativeCount);
               var6 = fastEntries.iterator();

               while(var6.hasNext()) {
                  Reference2ObjectMap.Entry entry = (Reference2ObjectMap.Entry)var6.next();
                  Object value = entry.getValue();
                  if (Removed.isNotRemoved(value)) {
                     DataComponentType type = (DataComponentType)entry.getKey();
                     DataComponentType.STREAM_CODEC.encode(output, type);
                     this.encodeComponent(output, type, value);
                  }
               }

               var6 = fastEntries.iterator();

               while(var6.hasNext()) {
                  Reference2ObjectMap.Entry entry = (Reference2ObjectMap.Entry)var6.next();
                  if (Removed.isRemoved(entry.getValue())) {
                     DataComponentType type = (DataComponentType)entry.getKey();
                     DataComponentType.STREAM_CODEC.encode(output, type);
                  }
               }

            }
         }

         private void encodeComponent(final RegistryFriendlyByteBuf output, final DataComponentType type, final Object value) {
            codecGetter.apply(type).encode(output, value);
         }
      };
   }

   DataComponentPatch(final Reference2ObjectMap map) {
      this.map = map;
   }

   public static DataComponentPatch.Builder builder() {
      return new DataComponentPatch.Builder();
   }

   public @Nullable Object get(final DataComponentGetter prototype, final DataComponentType type) {
      return getFromPatchAndPrototype(this.map, prototype, type);
   }

   static Object getFromPatchAndPrototype(final Reference2ObjectMap patch, final DataComponentGetter prototype, final DataComponentType type) {
      Object value = patch.get(type);
      return value != null ? Removed.removedToNull(value) : prototype.get(type);
   }

   public int size() {
      return this.map.size();
   }

   public DataComponentPatch forget(final Predicate test) {
      if (this.isEmpty()) {
         return EMPTY;
      } else {
         Reference2ObjectMap newMap = new Reference2ObjectArrayMap(this.map);
         newMap.keySet().removeIf(test);
         return newMap.isEmpty() ? EMPTY : new DataComponentPatch(newMap);
      }
   }

   public boolean isEmpty() {
      return this.map.isEmpty();
   }

   public DataComponentPatch.SplitResult split() {
      if (this.isEmpty()) {
         return DataComponentPatch.SplitResult.EMPTY;
      } else {
         DataComponentMap.Builder added = DataComponentMap.builder();
         Set removed = Sets.newIdentityHashSet();
         this.map.forEach((type, value) -> {
            if (Removed.isNotRemoved(value)) {
               added.setUnchecked(type, value);
            } else {
               removed.add(type);
            }

         });
         return new DataComponentPatch.SplitResult(added.build(), removed);
      }
   }

   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      } else {
         if (obj instanceof DataComponentPatch) {
            DataComponentPatch patch = (DataComponentPatch)obj;
            if (this.map.equals(patch.map)) {
               return true;
            }
         }

         return false;
      }
   }

   public int hashCode() {
      return this.map.hashCode();
   }

   public String toString() {
      return toString(this.map);
   }

   static String toString(final Reference2ObjectMap map) {
      StringBuilder builder = new StringBuilder();
      builder.append('{');
      boolean first = true;
      ObjectIterator var3 = Reference2ObjectMaps.fastIterable(map).iterator();

      while(var3.hasNext()) {
         Map.Entry entry = (Map.Entry)var3.next();
         if (first) {
            first = false;
         } else {
            builder.append(", ");
         }

         Object value = entry.getValue();
         if (Removed.isNotRemoved(value)) {
            builder.append(entry.getKey());
            builder.append("=>");
            builder.append(value);
         } else {
            builder.append("!");
            builder.append(entry.getKey());
         }
      }

      builder.append('}');
      return builder.toString();
   }

   public static class Builder {
      private final Reference2ObjectMap map = new Reference2ObjectArrayMap();

      private Builder() {
      }

      public DataComponentPatch.Builder set(final DataComponentType type, final Object value) {
         this.map.put(type, value);
         return this;
      }

      public DataComponentPatch.Builder remove(final DataComponentType type) {
         this.map.put(type, Removed.INSTANCE);
         return this;
      }

      public DataComponentPatch.Builder set(final TypedDataComponent component) {
         return this.set(component.type(), component.value());
      }

      public DataComponentPatch.Builder set(final Iterable components) {
         for(TypedDataComponent component : components) {
            this.set(component);
         }

         return this;
      }

      public DataComponentPatch build() {
         return this.map.isEmpty() ? DataComponentPatch.EMPTY : new DataComponentPatch(this.map);
      }
   }

   @FunctionalInterface
   private interface CodecGetter {
      StreamCodec apply(DataComponentType type);
   }

   private static record PatchKey(DataComponentType type, boolean removed) {
      public static final Codec CODEC = Codec.STRING.flatXmap((string) -> {
         boolean removed = string.startsWith("!");
         if (removed) {
            string = string.substring("!".length());
         }

         Identifier id = Identifier.tryParse(string);
         DataComponentType type = (DataComponentType)BuiltInRegistries.DATA_COMPONENT_TYPE.getValue(id);
         if (type == null) {
            return DataResult.error(() -> "No component with type: '" + String.valueOf(id) + "'");
         } else {
            return type.isTransient() ? DataResult.error(() -> "'" + String.valueOf(id) + "' is not a persistent component") : DataResult.success(new DataComponentPatch.PatchKey(type, removed));
         }
      }, (key) -> {
         DataComponentType type = key.type();
         Identifier id = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(type);
         return id == null ? DataResult.error(() -> "Unregistered component: " + String.valueOf(type)) : DataResult.success(key.removed() ? "!" + String.valueOf(id) : id.toString());
      });

      public Codec valueCodec() {
         return this.removed ? Removed.CODEC : this.type.codecOrThrow();
      }
   }

   public static record SplitResult(DataComponentMap added, Set removed) {
      public static final DataComponentPatch.SplitResult EMPTY = new DataComponentPatch.SplitResult(DataComponentMap.EMPTY, Set.of());
   }
}
