package net.minecraft.core;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceKey;
import org.slf4j.Logger;

public interface RegistryAccess extends HolderLookup.Provider {
   Logger LOGGER = LogUtils.getLogger();
   RegistryAccess.Frozen EMPTY = (new RegistryAccess.ImmutableRegistryAccess(Map.of())).freeze();

   Optional lookup(final ResourceKey registryKey);

   default Registry lookupOrThrow(final ResourceKey name) {
      return (Registry)this.lookup(name).orElseThrow(() -> new IllegalStateException("Missing registry: " + String.valueOf(name)));
   }

   Stream registries();

   default Stream listRegistryKeys() {
      return this.registries().map((e) -> e.key);
   }

   static RegistryAccess.Frozen fromRegistryOfRegistries(final Registry registries) {
      return new RegistryAccess.Frozen() {
         public Optional lookup(final ResourceKey registryKey) {
            Registry registry = registries;
            return registry.getOptional(registryKey);
         }

         public Stream registries() {
            return registries.entrySet().stream().map(RegistryAccess.RegistryEntry::fromMapEntry);
         }

         public RegistryAccess.Frozen freeze() {
            return this;
         }
      };
   }

   default RegistryAccess.Frozen freeze() {
      class FrozenAccess extends RegistryAccess.ImmutableRegistryAccess implements RegistryAccess.Frozen {
         protected FrozenAccess(final RegistryAccess this$0, final Stream entries) {
            Objects.requireNonNull(this$0);
            super(entries);
         }
      }

      return new FrozenAccess(this, this.registries().map(RegistryAccess.RegistryEntry::freeze));
   }

   public interface Frozen extends RegistryAccess {
   }

   public static class ImmutableRegistryAccess implements RegistryAccess {
      private final Map registries;

      public ImmutableRegistryAccess(final List registries) {
         this.registries = (Map)registries.stream().collect(Collectors.toUnmodifiableMap(Registry::key, (v) -> v));
      }

      public ImmutableRegistryAccess(final Map registries) {
         this.registries = Map.copyOf(registries);
      }

      public ImmutableRegistryAccess(final Stream entries) {
         this.registries = (Map)entries.collect(ImmutableMap.toImmutableMap(RegistryAccess.RegistryEntry::key, RegistryAccess.RegistryEntry::value));
      }

      public Optional lookup(final ResourceKey registryKey) {
         return Optional.ofNullable((Registry)this.registries.get(registryKey)).map((r) -> r);
      }

      public Stream registries() {
         return this.registries.entrySet().stream().map(RegistryAccess.RegistryEntry::fromMapEntry);
      }
   }

   public static record RegistryEntry(ResourceKey key, Registry value) {
      private static RegistryAccess.RegistryEntry fromMapEntry(final Map.Entry e) {
         return fromUntyped((ResourceKey)e.getKey(), (Registry)e.getValue());
      }

      private static RegistryAccess.RegistryEntry fromUntyped(final ResourceKey key, final Registry value) {
         return new RegistryAccess.RegistryEntry(key, value);
      }

      private RegistryAccess.RegistryEntry freeze() {
         return new RegistryAccess.RegistryEntry(this.key, this.value.freeze());
      }
   }
}
