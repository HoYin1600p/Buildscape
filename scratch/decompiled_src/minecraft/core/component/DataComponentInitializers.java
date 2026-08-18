package net.minecraft.core.component;

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class DataComponentInitializers {
   private final List initializers = new ArrayList();

   public void add(final ResourceKey key, final DataComponentInitializers.Initializer initializer) {
      this.initializers.add(new DataComponentInitializers.InitializerEntry(key, initializer));
   }

   private Map runInitializers(final HolderLookup.Provider context) {
      Map results = new HashMap();

      for(DataComponentInitializers.InitializerEntry initializer : this.initializers) {
         DataComponentMap.Builder builder = (DataComponentMap.Builder)results.computeIfAbsent(initializer.key, (k) -> DataComponentMap.builder());
         initializer.run(builder, context);
      }

      return results;
   }

   private static void registryEmpty(final Map buildersByRegistry, final ResourceKey registryKey) {
      buildersByRegistry.put(registryKey, new DataComponentInitializers.PendingComponentBuilders(registryKey, new HashMap()));
   }

   private static void addBuilder(final Map buildersByRegistry, final ResourceKey key, final DataComponentMap.Builder builder) {
      DataComponentInitializers.PendingComponentBuilders buildersForRegistry = (DataComponentInitializers.PendingComponentBuilders)buildersByRegistry.get(key.registryKey());
      buildersForRegistry.builders.put(key, builder);
   }

   public List build(final HolderLookup.Provider context) {
      Map buildersByRegistry = new HashMap();
      context.listRegistryKeys().forEach((registryKey) -> registryEmpty(buildersByRegistry, registryKey));
      this.runInitializers(context).forEach((key, builder) -> addBuilder(buildersByRegistry, key, builder));
      return (List)buildersByRegistry.values().stream().map((elementBuilders) -> createInitializerForRegistry(context, elementBuilders)).collect(Collectors.toUnmodifiableList());
   }

   private static DataComponentInitializers.PendingComponents createInitializerForRegistry(final HolderLookup.Provider context, final DataComponentInitializers.PendingComponentBuilders elementBuilders) {
      final List entries = new ArrayList();
      final ResourceKey registryKey = elementBuilders.registryKey;
      HolderLookup.RegistryLookup registry = context.lookupOrThrow(registryKey);
      Set elementsWithComponents = Sets.newIdentityHashSet();
      elementBuilders.builders.forEach((elementKey, elementBuilder) -> {
         Holder.Reference element = registry.getOrThrow(elementKey);
         DataComponentMap components = elementBuilder.build();
         entries.add(new DataComponentInitializers.BakedEntry(element, components));
         elementsWithComponents.add(element);
      });
      registry.listElements().filter((e) -> !elementsWithComponents.contains(e)).forEach((elementWithoutComponents) -> entries.add(new DataComponentInitializers.BakedEntry(elementWithoutComponents, DataComponentMap.EMPTY)));
      return new DataComponentInitializers.PendingComponents() {
         public ResourceKey key() {
            return registryKey;
         }

         public void forEach(final BiConsumer output) {
            entries.forEach((e) -> output.accept(e.element, e.components));
         }

         public void apply() {
            entries.forEach(DataComponentInitializers.BakedEntry::apply);
         }
      };
   }

   private static record BakedEntry(Holder.Reference element, DataComponentMap components) {
      public void apply() {
         this.element.bindComponents(this.components);
      }
   }

   @FunctionalInterface
   public interface Initializer {
      void run(DataComponentMap.Builder components, HolderLookup.Provider context, ResourceKey key);

      default DataComponentInitializers.Initializer andThen(final DataComponentInitializers.Initializer other) {
         return (components, context, key) -> {
            this.run(components, context, key);
            other.run(components, context, key);
         };
      }

      default DataComponentInitializers.Initializer add(final DataComponentType type, final Object value) {
         return this.andThen((components, context, key) -> components.set(type, value));
      }
   }

   private static record InitializerEntry(ResourceKey key, DataComponentInitializers.Initializer initializer) {
      public void run(final DataComponentMap.Builder components, final HolderLookup.Provider context) {
         this.initializer.run(components, context, this.key);
      }
   }

   private static record PendingComponentBuilders(ResourceKey registryKey, Map builders) {
   }

   public interface PendingComponents {
      ResourceKey key();

      void forEach(BiConsumer output);

      void apply();
   }

   @FunctionalInterface
   public interface SingleComponentInitializer {
      Object create(HolderLookup.Provider context);

      default DataComponentInitializers.Initializer asInitializer(final DataComponentType type) {
         return (components, context, key) -> components.set(type, this.create(context));
      }
   }
}
