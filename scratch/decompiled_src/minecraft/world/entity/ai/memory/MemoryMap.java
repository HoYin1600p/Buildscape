package net.minecraft.world.entity.ai.memory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jspecify.annotations.Nullable;

public final class MemoryMap implements Iterable {
   private static final Codec SERIALIZABLE_MEMORY_MODULE_CODEC = BuiltInRegistries.MEMORY_MODULE_TYPE.byNameCodec().validate((type) -> type.canSerialize() ? DataResult.success(type) : DataResult.error(() -> "Memory module " + String.valueOf(type) + " cannot be encoded"));
   public static final Codec CODEC = Codec.dispatchedMap(SERIALIZABLE_MEMORY_MODULE_CODEC, (type) -> (Codec)type.getCodec().orElseThrow()).xmap(MemoryMap::new, (m) -> m.memories);
   public static final MemoryMap EMPTY = new MemoryMap(Map.of());
   private final Map memories;

   private MemoryMap(final Map memories) {
      this.memories = Map.copyOf(memories);
   }

   public static MemoryMap of(final Stream memories) {
      return new MemoryMap((Map)memories.collect(Collectors.toMap(MemoryMap.Value::type, MemoryMap.Value::value)));
   }

   public @Nullable ExpirableValue get(final MemoryModuleType type) {
      return (ExpirableValue)this.memories.get(type);
   }

   public boolean equals(final Object obj) {
      if (obj instanceof MemoryMap map) {
         if (this.memories.equals(map.memories)) {
            return true;
         }
      }

      return false;
   }

   public int hashCode() {
      return this.memories.hashCode();
   }

   public String toString() {
      return this.memories.toString();
   }

   public Iterator iterator() {
      return Iterators.transform(this.memories.entrySet().iterator(), (entry) -> MemoryMap.Value.createUnchecked((MemoryModuleType)entry.getKey(), (ExpirableValue)entry.getValue()));
   }

   public static class Builder {
      private final ImmutableMap.Builder builder = ImmutableMap.builder();

      public MemoryMap.Builder add(final MemoryModuleType type, final ExpirableValue value) {
         this.builder.put(type, value);
         return this;
      }

      public MemoryMap build() {
         return new MemoryMap(this.builder.buildOrThrow());
      }
   }

   public static record Value(MemoryModuleType type, ExpirableValue value) {
      public static MemoryMap.Value createUnchecked(final MemoryModuleType type, final ExpirableValue value) {
         return new MemoryMap.Value(type, value);
      }
   }
}
