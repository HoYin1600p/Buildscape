package net.minecraft.tags;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.SequencedSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.DependencySorter;
import net.minecraft.util.StrictJsonParser;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class TagLoader {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final TagLoader.ElementLookup elementLookup;
   private final String directory;

   public TagLoader(final TagLoader.ElementLookup elementLookup, final String directory) {
      this.elementLookup = elementLookup;
      this.directory = directory;
   }

   public Map load(final ResourceManager resourceManager) {
      Map builders = new HashMap();
      FileToIdConverter lister = FileToIdConverter.json(this.directory);

      for(Map.Entry entry : lister.listMatchingResourceStacks(resourceManager).entrySet()) {
         Identifier location = (Identifier)entry.getKey();
         Identifier id = lister.fileToId(location);

         for(Resource resource : (List)entry.getValue()) {
            try {
               Reader reader = resource.openAsReader();

               try {
                  JsonElement element = StrictJsonParser.parse(reader);
                  List tagContents = (List)builders.computeIfAbsent(id, (key) -> new ArrayList());
                  TagFile parsedContents = (TagFile)TagFile.CODEC.parse(new Dynamic(JsonOps.INSTANCE, element)).getOrThrow();
                  if (parsedContents.replace()) {
                     tagContents.clear();
                  }

                  String sourceId = resource.sourcePackId();
                  parsedContents.entries().forEach((ex) -> tagContents.add(new TagLoader.EntryWithSource(ex, sourceId)));
               } catch (Throwable var16) {
                  if (reader != null) {
                     try {
                        reader.close();
                     } catch (Throwable var15) {
                        var16.addSuppressed(var15);
                     }
                  }

                  throw var16;
               }

               if (reader != null) {
                  reader.close();
               }
            } catch (Exception var17) {
               LOGGER.error("Couldn't read tag list {} from {} in data pack {}", new Object[]{id, location, resource.sourcePackId(), var17});
            }
         }
      }

      return builders;
   }

   private Either tryBuildTag(final TagEntry.Lookup lookup, final List entries) {
      SequencedSet values = new LinkedHashSet();
      List missingElements = new ArrayList();

      for(TagLoader.EntryWithSource entry : entries) {
         if (!entry.entry().build(lookup, values::add)) {
            missingElements.add(entry);
         }
      }

      return missingElements.isEmpty() ? Either.right(List.copyOf(values)) : Either.left(missingElements);
   }

   public Map build(final Map builders) {
      final Map newTags = new HashMap();
      TagEntry.Lookup lookup = new TagEntry.Lookup() {
         {
            Objects.requireNonNull(TagLoader.this);
         }

         public @Nullable Object element(final Identifier key, final boolean required) {
            return TagLoader.this.elementLookup.get(key, required).orElse((Object)null);
         }

         public @Nullable Collection tag(final Identifier key) {
            return (Collection)newTags.get(key);
         }
      };
      DependencySorter sorter = new DependencySorter();
      builders.forEach((id, entry) -> sorter.addEntry(id, new TagLoader.SortingEntry(entry)));
      sorter.orderByDependencies((id, contents) -> this.tryBuildTag(lookup, contents.entries).ifLeft((missing) -> LOGGER.error("Couldn't load tag {} as it is missing following references: {}", id, missing.stream().map(Objects::toString).collect(Collectors.joining(", ")))).ifRight((tag) -> newTags.put(id, tag)));
      return newTags;
   }

   public static Map loadTagsFromNetwork(final TagNetworkSerialization.NetworkPayload tags, final Registry registry) {
      return tags.resolve(registry).tags;
   }

   public static List loadTagsForExistingRegistries(final ResourceManager manager, final RegistryAccess layer) {
      return (List)layer.registries().map((entry) -> loadPendingTags(manager, entry.value())).flatMap(Optional::stream).collect(Collectors.toUnmodifiableList());
   }

   public static void loadTagsForRegistry(final ResourceManager manager, final WritableRegistry registry) {
      loadTagsForRegistry(manager, registry.key(), TagLoader.ElementLookup.fromWritableRegistry(registry));
   }

   public static Map loadTagsForRegistry(final ResourceManager manager, final ResourceKey registryKey, final TagLoader.ElementLookup lookup) {
      TagLoader loader = new TagLoader(lookup, Registries.tagsDirPath(registryKey));
      return wrapTags(registryKey, loader.build(loader.load(manager)));
   }

   private static Map wrapTags(final ResourceKey registryKey, final Map tags) {
      return (Map)tags.entrySet().stream().collect(Collectors.toUnmodifiableMap((e) -> TagKey.create(registryKey, (Identifier)e.getKey()), Map.Entry::getValue));
   }

   private static Optional loadPendingTags(final ResourceManager manager, final Registry registry) {
      ResourceKey key = registry.key();
      TagLoader loader = new TagLoader(TagLoader.ElementLookup.fromFrozenRegistry(registry), Registries.tagsDirPath(key));
      TagLoader.LoadResult tags = new TagLoader.LoadResult(key, wrapTags(registry.key(), loader.build(loader.load(manager))));
      return tags.tags().isEmpty() ? Optional.empty() : Optional.of(registry.prepareTagReload(tags));
   }

   public static List buildUpdatedLookups(final RegistryAccess.Frozen registries, final List tags) {
      List result = new ArrayList();
      registries.registries().forEach((lookup) -> {
         Registry.PendingTags foundTags = findTagsForRegistry(tags, lookup.key());
         result.add(foundTags != null ? foundTags.lookup() : lookup.value());
      });
      return result;
   }

   private static Registry.@Nullable PendingTags findTagsForRegistry(final List tags, final ResourceKey registryKey) {
      for(Registry.PendingTags tag : tags) {
         if (tag.key() == registryKey) {
            return tag;
         }
      }

      return null;
   }

   public interface ElementLookup {
      Optional get(Identifier id, boolean required);

      static TagLoader.ElementLookup fromFrozenRegistry(final Registry registry) {
         return (id, required) -> registry.get(id);
      }

      static TagLoader.ElementLookup fromWritableRegistry(final WritableRegistry registry) {
         return fromGetters(registry.key(), registry.createRegistrationLookup(), registry);
      }

      static TagLoader.ElementLookup fromGetters(final ResourceKey registryKey, final HolderGetter writable, final HolderGetter immutable) {
         return (id, required) -> (required ? writable : immutable).get(ResourceKey.create(registryKey, id));
      }
   }

   public static record EntryWithSource(TagEntry entry, String source) {
      public String toString() {
         return String.valueOf(this.entry) + " (from " + this.source + ")";
      }
   }

   public static record LoadResult(ResourceKey key, Map tags) {
   }

   private static record SortingEntry(List entries) implements DependencySorter.Entry {
      public void visitRequiredDependencies(final Consumer output) {
         this.entries.forEach((e) -> e.entry.visitRequiredDependencies(output));
      }

      public void visitOptionalDependencies(final Consumer output) {
         this.entries.forEach((e) -> e.entry.visitOptionalDependencies(output));
      }
   }
}
