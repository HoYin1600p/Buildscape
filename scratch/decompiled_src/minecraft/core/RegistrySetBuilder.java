package net.minecraft.core;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Lifecycle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.registries.BootstrapRegistry;
import net.minecraft.core.registries.EmptyTagLookupWrapper;
import net.minecraft.core.registries.MultiRegistryBootstrap;
import net.minecraft.core.registries.PatchedRegistry;
import net.minecraft.core.registries.SingleRegistryBootstrap;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

public class RegistrySetBuilder {
   private final List entries = new ArrayList();

   private static RegistrySetBuilder.RegistryStub placeholderStub(final ResourceKey key) {
      return new RegistrySetBuilder.RegistryStub() {
         public Stream requiredRegistries() {
            return Stream.of(key);
         }

         public void apply(final RegistrySetBuilder.BuildState state) {
         }
      };
   }

   public RegistrySetBuilder add(final ResourceKey key, final SingleRegistryBootstrap bootstrap) {
      this.entries.add(new RegistrySetBuilder.RegistryStub(this) {
         {
            Objects.requireNonNull(this$0);
         }

         public Stream requiredRegistries() {
            return Stream.of(key);
         }

         public void apply(final RegistrySetBuilder.BuildState state) {
            bootstrap.run(state.createBootstrap(key));
         }
      });
      return this;
   }

   public RegistrySetBuilder add(final MultiRegistryBootstrap bootstrap) {
      this.entries.add(new RegistrySetBuilder.RegistryStub(this) {
         {
            Objects.requireNonNull(this$0);
         }

         public Stream requiredRegistries() {
            return bootstrap.requestedRegistries().stream();
         }

         public void apply(final RegistrySetBuilder.BuildState state) {
            bootstrap.run(state::createBootstrap);
         }
      });
      return this;
   }

   private static HolderLookup.Provider buildProviderWithContext(final HolderLookup.Provider context, final Stream newRegistries) {
      Map lookups = new HashMap();
      context.listRegistries().forEach((contextRegistry) -> lookups.put(contextRegistry.key(), EmptyTagLookupWrapper.wrap(contextRegistry)));
      newRegistries.forEach((newRegistry) -> lookups.put(newRegistry.key(), EmptyTagLookupWrapper.wrap(newRegistry)));
      return HolderLookup.Provider.create(lookups.values().stream());
   }

   public HolderLookup.Provider build(final HolderLookup.Provider context) {
      RegistrySetBuilder.BuildState state = RegistrySetBuilder.BuildState.createAndApply(context, this.entries);
      List bootstrappedRegistries = new ArrayList(state.bootstrappedRegistries.size());

      for(RegistrySetBuilder.BootstrappedRegistryState newRegistry : state.bootstrappedRegistries.values()) {
         newRegistry.bindHolders();
         newRegistry.freeze();
         newRegistry.errorOnMissingHolders(state);
         bootstrappedRegistries.add(newRegistry.registry());
      }

      state.throwOnError();
      return buildProviderWithContext(context, bootstrappedRegistries.stream());
   }

   private static Set findRegistriesMissingFromPatch(final HolderLookup.Provider contextRegistries, final HolderLookup.Provider baseRegistries, final List entries) {
      Set existingKeys = (Set)Stream.concat(newRegistryKeys(entries.stream()), contextRegistries.listRegistryKeys()).collect(Collectors.toSet());
      return (Set)baseRegistries.listRegistryKeys().filter((e) -> !existingKeys.contains(e)).collect(Collectors.toSet());
   }

   public RegistrySetBuilder.PatchedRegistries buildPatch(final HolderLookup.Provider context, final HolderLookup.Provider fallbackProvider, final Cloner.Factory clonerFactory) {
      Set missingFromPatch = findRegistriesMissingFromPatch(context, fallbackProvider, this.entries);
      List expandedEntries = Stream.concat(this.entries.stream(), missingFromPatch.stream().map(RegistrySetBuilder::placeholderStub)).toList();
      RegistrySetBuilder.BuildState state = RegistrySetBuilder.BuildState.createAndApply(context, expandedEntries);
      List bootstrappedRegistries = new ArrayList(state.bootstrappedRegistries.size());

      for(RegistrySetBuilder.BootstrappedRegistryState newRegistry : state.bootstrappedRegistries.values()) {
         newRegistry.bindHolders();
         newRegistry.validatePatchHolders(state, fallbackProvider);
         newRegistry.freeze();
         bootstrappedRegistries.add(newRegistry.registry());
      }

      HolderLookup.Provider patchOnlyRegistries = buildProviderWithContext(context, bootstrappedRegistries.stream());
      state.throwOnError();
      HolderLookup.Provider fullPatchedRegistries = EmptyTagLookupWrapper.wrap(PatchedRegistry.applyPatches(context, fallbackProvider, patchOnlyRegistries, clonerFactory, state.bootstrappedRegistries.keySet()));
      return new RegistrySetBuilder.PatchedRegistries(fullPatchedRegistries, patchOnlyRegistries);
   }

   private static ResourceKey eyerollCast(final ResourceKey registryKey) {
      return registryKey;
   }

   private static Stream newRegistryKeys(final Stream entries) {
      return entries.flatMap(RegistrySetBuilder.RegistryStub::requiredRegistries).distinct();
   }

   private static record BootstrappedRegistryState(BootstrapRegistry registry, Map registeredValues) {
      public static RegistrySetBuilder.BootstrappedRegistryState create(final ResourceKey key, final Lifecycle lifecycle) {
         BootstrapRegistry newRegistry = new BootstrapRegistry(key, lifecycle);
         return new RegistrySetBuilder.BootstrappedRegistryState(newRegistry, new HashMap());
      }

      public BootstrapContext createBootstrapContext(final RegistrySetBuilder.BuildState state) {
         return new BootstrapContext() {
            {
               Objects.requireNonNull(BootstrappedRegistryState.this);
            }

            public Holder.Reference register(final ResourceKey key, final Object value) {
               Object previousValue = (T)BootstrappedRegistryState.this.registeredValues.put(key, value);
               if (previousValue != null) {
                  state.errors.add(new IllegalStateException("Duplicate registration for " + String.valueOf(key) + ", new=" + String.valueOf(value) + ", old=" + String.valueOf(previousValue)));
               }

               return BootstrappedRegistryState.this.registry.getOrThrow(key);
            }

            public HolderGetter lookup(final ResourceKey key) {
               return state.allRegistries.lookupOrThrow(key);
            }

            public Stream listContextElements(final ResourceKey key) {
               return state.contextRegistries.lookupOrThrow(key).listElements();
            }
         };
      }

      public void bindHolders() {
         this.registeredValues.forEach((key, value) -> this.registry.getOrThrow(key).bindValue(value));
      }

      public void freeze() {
         this.registry.freeze();
      }

      public void errorOnMissingHolders(final RegistrySetBuilder.BuildState state) {
         this.registry.listElements().forEach((element) -> {
            if (!element.isBound()) {
               state.errors().add(new IllegalStateException("No value registered for key " + String.valueOf(element.key().identifier())));
            }

         });
      }

      public void validatePatchHolders(final RegistrySetBuilder.BuildState state, final HolderLookup.Provider fallback) {
         HolderLookup baseRegistry = fallback.lookupOrThrow(this.registry.key());
         this.registry.removeIf((element) -> {
            if (element.isBound()) {
               return false;
            } else {
               if (baseRegistry.get(element.key()).isEmpty()) {
                  state.errors().add(new IllegalStateException("Value " + String.valueOf(element.key().identifier()) + " referenced by patched element is not present in base"));
               }

               return true;
            }
         });
      }
   }

   private static record BuildState(HolderLookup.Provider contextRegistries, HolderLookup.Provider allRegistries, Map bootstrappedRegistries, List errors) {
      public static RegistrySetBuilder.BuildState createAndApply(final HolderLookup.Provider context, final List entries) {
         RegistrySetBuilder.BuildState state = create(context, entries);
         entries.forEach((e) -> e.apply(state));
         return state;
      }

      private static RegistrySetBuilder.BuildState create(final HolderLookup.Provider context, final List entries) {
         List errors = new ArrayList();
         ImmutableMap.Builder allRegistries = ImmutableMap.builder();
         ImmutableMap.Builder bootstrappedRegistries = ImmutableMap.builder();
         context.listRegistries().forEach((contextRegistry) -> allRegistries.put(contextRegistry.key(), EmptyTagLookupWrapper.wrap(contextRegistry)));
         RegistrySetBuilder.newRegistryKeys(entries.stream()).forEach((newRegistryKey) -> {
            RegistrySetBuilder.BootstrappedRegistryState newRegistryEntry = RegistrySetBuilder.BootstrappedRegistryState.create(RegistrySetBuilder.eyerollCast(newRegistryKey), Lifecycle.stable());
            BootstrapRegistry newRegistry = newRegistryEntry.registry();
            bootstrappedRegistries.put(newRegistry.key(), newRegistryEntry);
            allRegistries.put(newRegistry.key(), newRegistry);
         });
         return new RegistrySetBuilder.BuildState(context, HolderLookup.Provider.create(allRegistries.build().values().stream()), bootstrappedRegistries.build(), errors);
      }

      public void throwOnError() {
         if (!this.errors.isEmpty()) {
            IllegalStateException result = new IllegalStateException("Errors during registry creation");

            for(RuntimeException error : this.errors) {
               result.addSuppressed(error);
            }

            throw result;
         }
      }

      public BootstrapContext createBootstrap(final ResourceKey key) {
         RegistrySetBuilder.BootstrappedRegistryState targetRegistry = (RegistrySetBuilder.BootstrappedRegistryState)Objects.requireNonNull((RegistrySetBuilder.BootstrappedRegistryState)this.bootstrappedRegistries.get(key), () -> "No registry named " + String.valueOf(key.identifier()));
         return targetRegistry.createBootstrapContext(this);
      }
   }

   public static record PatchedRegistries(HolderLookup.Provider full, HolderLookup.Provider patches) {
   }

   private interface RegistryStub {
      Stream requiredRegistries();

      void apply(RegistrySetBuilder.BuildState state);
   }
}
