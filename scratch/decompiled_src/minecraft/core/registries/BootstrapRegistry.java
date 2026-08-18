package net.minecraft.core.registries;

import com.mojang.serialization.Lifecycle;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.jspecify.annotations.Nullable;

public class BootstrapRegistry implements HolderLookup.RegistryLookup {
   private final ResourceKey key;
   private final Lifecycle lifecycle;
   private BootstrapRegistry.Storage storage = new BootstrapRegistry.RegistrationStorage();

   public BootstrapRegistry(final ResourceKey key, final Lifecycle lifecycle) {
      this.key = key;
      this.lifecycle = lifecycle;
   }

   public ResourceKey key() {
      return this.key;
   }

   public Lifecycle registryLifecycle() {
      return this.lifecycle;
   }

   public void freeze() {
      this.storage = this.storage.freeze();
   }

   public void removeIf(final Predicate predicate) {
      this.storage.removeIf(predicate);
   }

   public Optional get(final ResourceKey id) {
      return Optional.ofNullable(this.storage.get(id));
   }

   public Stream listElements() {
      return this.storage.listElements().stream();
   }

   public Optional get(final TagKey id) {
      return Optional.ofNullable(this.storage.get(id));
   }

   public Stream listTags() {
      return this.storage.listTags().stream();
   }

   private static class FrozenStorage implements BootstrapRegistry.Storage {
      private final Map holders;
      private final Map holderSets;

      private FrozenStorage(final Map holders, final Map holderSets) {
         this.holders = Map.copyOf(holders);
         this.holderSets = Map.copyOf(holderSets);
      }

      public BootstrapRegistry.Storage freeze() {
         return this;
      }

      public void removeIf(final Predicate predicate) {
         throw new UnsupportedOperationException("Registry is already frozen");
      }

      public Holder.@Nullable Reference get(final ResourceKey id) {
         return (Holder.Reference)this.holders.get(id);
      }

      public Collection listElements() {
         return this.holders.values();
      }

      public HolderSet.@Nullable Named get(final TagKey id) {
         return (HolderSet.Named)this.holderSets.get(id);
      }

      public Collection listTags() {
         return this.holderSets.values();
      }
   }

   private class RegistrationStorage implements BootstrapRegistry.Storage {
      private final Map holders;
      private final Map holderSets;

      private RegistrationStorage() {
         Objects.requireNonNull(BootstrapRegistry.this);
         super();
         this.holders = new HashMap();
         this.holderSets = new HashMap();
      }

      public BootstrapRegistry.Storage freeze() {
         return new BootstrapRegistry.FrozenStorage(this.holders, this.holderSets);
      }

      public void removeIf(final Predicate predicate) {
         this.holders.values().removeIf(predicate);
      }

      public Holder.Reference get(final ResourceKey id) {
         return (Holder.Reference)this.holders.computeIfAbsent(id, (key) -> Holder.Reference.createStandAlone(BootstrapRegistry.this, key));
      }

      public Collection listElements() {
         throw new UnsupportedOperationException("List is not available during bootstrap");
      }

      public HolderSet.Named get(final TagKey id) {
         return (HolderSet.Named)this.holderSets.computeIfAbsent(id, (key) -> HolderSet.emptyNamed(BootstrapRegistry.this, key));
      }

      public Collection listTags() {
         throw new UnsupportedOperationException("List is not available during bootstrap");
      }
   }

   private interface Storage {
      BootstrapRegistry.Storage freeze();

      void removeIf(Predicate predicate);

      Holder.@Nullable Reference get(ResourceKey id);

      Collection listElements();

      HolderSet.@Nullable Named get(TagKey id);

      Collection listTags();
   }
}
