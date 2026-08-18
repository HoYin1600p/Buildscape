package net.minecraft.core;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.flag.FeatureFlagSet;

public interface HolderLookup extends HolderGetter {
   Stream listElements();

   default Stream listElementIds() {
      return this.listElements().map(Holder.Reference::key);
   }

   Stream listTags();

   default Stream listTagIds() {
      return this.listTags().map(HolderSet.Named::key);
   }

   public interface Provider extends HolderGetter.Provider {
      Stream listRegistryKeys();

      default Stream listRegistries() {
         return this.listRegistryKeys().map(this::lookupOrThrow);
      }

      Optional lookup(final ResourceKey key);

      default HolderLookup.RegistryLookup lookupOrThrow(final ResourceKey key) {
         return (HolderLookup.RegistryLookup)this.lookup(key).orElseThrow(() -> new IllegalStateException("Registry " + String.valueOf(key.identifier()) + " not found"));
      }

      default RegistryOps createSerializationContext(final DynamicOps parent) {
         return RegistryOps.create(parent, this);
      }

      static HolderLookup.Provider create(final Stream lookups) {
         final Map map = (Map)lookups.collect(Collectors.toUnmodifiableMap(HolderLookup.RegistryLookup::key, (e) -> e));
         return new HolderLookup.Provider() {
            public Stream listRegistryKeys() {
               return map.keySet().stream();
            }

            public Optional lookup(final ResourceKey key) {
               return Optional.ofNullable((HolderLookup.RegistryLookup)map.get(key));
            }
         };
      }

      default Lifecycle allRegistriesLifecycle() {
         return (Lifecycle)this.listRegistries().map(HolderLookup.RegistryLookup::registryLifecycle).reduce(Lifecycle.stable(), Lifecycle::add);
      }
   }

   public interface RegistryLookup extends HolderLookup {
      ResourceKey key();

      Lifecycle registryLifecycle();

      default HolderLookup.RegistryLookup filterFeatures(final FeatureFlagSet enabledFeatures) {
         return FeatureElement.FILTERED_REGISTRIES.contains(this.key()) ? this.filterElements((t) -> ((FeatureElement)t).isEnabled(enabledFeatures)) : this;
      }

      default HolderLookup.RegistryLookup filterElements(final Predicate filter) {
         return new HolderLookup.RegistryLookup.Delegate() {
            {
               Objects.requireNonNull(RegistryLookup.this);
            }

            public HolderLookup.RegistryLookup parent() {
               return RegistryLookup.this;
            }

            public Optional get(final ResourceKey id) {
               return this.parent().get(id).filter((holder) -> filter.test(holder.value()));
            }

            public Stream listElements() {
               return this.parent().listElements().filter((e) -> filter.test(e.value()));
            }
         };
      }

      public interface Delegate extends HolderLookup.RegistryLookup {
         HolderLookup.RegistryLookup parent();

         default ResourceKey key() {
            return this.parent().key();
         }

         default Lifecycle registryLifecycle() {
            return this.parent().registryLifecycle();
         }

         default Optional get(final ResourceKey id) {
            return this.parent().get(id);
         }

         default Stream listElements() {
            return this.parent().listElements();
         }

         default Optional get(final TagKey id) {
            return this.parent().get(id);
         }

         default Stream listTags() {
            return this.parent().listTags();
         }

         default boolean canSerialize(final HolderOwner owner) {
            return this.parent().canSerialize(owner);
         }
      }
   }
}
