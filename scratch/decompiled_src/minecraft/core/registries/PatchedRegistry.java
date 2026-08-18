package net.minecraft.core.registries;

import com.mojang.serialization.Lifecycle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.Cloner;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jspecify.annotations.Nullable;

public class PatchedRegistry implements HolderLookup.RegistryLookup {
   private final ResourceKey key;
   private final Lifecycle lifecycle;
   private final Map entries = new HashMap();

   private PatchedRegistry(final ResourceKey key, final Lifecycle lifecycle) {
      this.key = key;
      this.lifecycle = lifecycle;
   }

   public ResourceKey key() {
      return this.key;
   }

   public Lifecycle registryLifecycle() {
      return this.lifecycle;
   }

   public Stream listElements() {
      return this.entries.values().stream();
   }

   public Optional get(final ResourceKey id) {
      return Optional.ofNullable((Holder.Reference)this.entries.get(id));
   }

   public Stream listTags() {
      throw new UnsupportedOperationException("Tags cloning is not supported");
   }

   public Optional get(final TagKey id) {
      return Optional.of(HolderSet.emptyNamed(this, id));
   }

   private static HolderLookup.RegistryLookup createLazyFullPatchedRegistries(final Cloner.Factory clonerFactory, final ResourceKey registryKey, final HolderLookup.Provider baseProvider, final HolderLookup.Provider patchProvider, final MutableObject clonedRegistriesProvider) {
      Cloner cloner = clonerFactory.cloner(registryKey);
      if (cloner == null) {
         throw new NullPointerException("No cloner for " + String.valueOf(registryKey.identifier()));
      } else {
         HolderLookup.RegistryLookup patchContents = patchProvider.lookupOrThrow(registryKey);
         HolderLookup.RegistryLookup baseContents = baseProvider.lookupOrThrow(registryKey);
         Lifecycle lifecycle = patchContents.registryLifecycle().add(baseContents.registryLifecycle());
         PatchedRegistry result = new PatchedRegistry(registryKey, lifecycle);
         patchContents.listElements().forEach((elementHolder) -> {
            ResourceKey elementKey = elementHolder.key();
            PatchedRegistry.LazyHolder holder = new PatchedRegistry.LazyHolder(result, elementKey);
            holder.supplier = () -> cloner.clone(elementHolder.value(), patchProvider, (HolderLookup.Provider)clonedRegistriesProvider.get());
            result.entries.put(elementKey, holder);
         });
         baseContents.listElements().forEach((elementHolder) -> {
            ResourceKey elementKey = elementHolder.key();
            result.entries.computeIfAbsent(elementKey, (key) -> {
               PatchedRegistry.LazyHolder holder = new PatchedRegistry.LazyHolder(result, elementKey);
               holder.supplier = () -> cloner.clone(elementHolder.value(), baseProvider, (HolderLookup.Provider)clonedRegistriesProvider.get());
               return holder;
            });
         });
         return result;
      }
   }

   public static HolderLookup.Provider applyPatches(final HolderLookup.Provider context, final HolderLookup.Provider baseRegistries, final HolderLookup.Provider patchRegistries, final Cloner.Factory clonerFactory, final Set registriesToClone) {
      MutableObject resultHolder = new MutableObject();
      List lazyFullRegistries = (List)registriesToClone.stream().map((registryKey) -> createLazyFullPatchedRegistries(clonerFactory, registryKey, baseRegistries, patchRegistries, resultHolder)).collect(Collectors.toUnmodifiableList());
      HolderLookup.Provider result = HolderLookup.Provider.create(Stream.concat(context.listRegistries(), lazyFullRegistries.stream()));
      resultHolder.setValue(result);
      return result;
   }

   private static class LazyHolder extends Holder.Reference {
      private @Nullable Supplier supplier;

      protected LazyHolder(final HolderOwner owner, final @Nullable ResourceKey key) {
         super(Holder.Reference.Type.STAND_ALONE, owner, key, (Object)null);
      }

      protected void bindValue(final Object value) {
         super.bindValue(value);
         this.supplier = null;
      }

      public Object value() {
         if (this.supplier != null) {
            this.bindValue(this.supplier.get());
         }

         return super.value();
      }
   }
}
