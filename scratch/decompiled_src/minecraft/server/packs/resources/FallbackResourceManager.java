package net.minecraft.server.packs.resources;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class FallbackResourceManager implements ResourceManager {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final List fallbacks = new ArrayList();
   private final PackType type;
   private final String namespace;

   public FallbackResourceManager(final PackType type, final String namespace) {
      this.type = type;
      this.namespace = namespace;
   }

   public void push(final PackResources pack) {
      this.pushInternal(pack.packId(), pack, (PackResources.Filter)null);
   }

   public void push(final PackResources pack, final PackResources.Filter filter) {
      this.pushInternal(pack.packId(), pack, filter);
   }

   public void pushFilterOnly(final String name, final PackResources.Filter filter) {
      this.pushInternal(name, (PackResources)null, filter);
   }

   private void pushInternal(final String name, final @Nullable PackResources pack, final PackResources.@Nullable Filter contentFilter) {
      this.fallbacks.add(new FallbackResourceManager.PackEntry(name, pack, contentFilter));
   }

   public Set getNamespaces() {
      return ImmutableSet.of(this.namespace);
   }

   public Optional getResource(final Identifier location) {
      for(int i = this.fallbacks.size() - 1; i >= 0; --i) {
         FallbackResourceManager.PackEntry entry = (FallbackResourceManager.PackEntry)this.fallbacks.get(i);
         PackResources fallback = entry.resources;
         if (fallback != null) {
            IoSupplier resource = fallback.getResource(this.type, location);
            if (resource != null) {
               IoSupplier metadataGetter = this.createStackMetadataFinder(location, i);
               return Optional.of(createResource(fallback, location, resource, metadataGetter));
            }
         }

         if (entry.isFiltered(location)) {
            LOGGER.warn("Resource {} not found, but was filtered by pack {}", location, entry.name);
            return Optional.empty();
         }
      }

      return Optional.empty();
   }

   private static Resource createResource(final PackResources source, final Identifier location, final IoSupplier resource, final IoSupplier metadata) {
      return new Resource(source, wrapForDebug(location, source, resource), metadata);
   }

   private static IoSupplier wrapForDebug(final Identifier location, final PackResources source, final IoSupplier resource) {
      return LOGGER.isDebugEnabled() ? () -> new FallbackResourceManager.LeakedResourceWarningInputStream((InputStream)resource.get(), location, source.packId()) : resource;
   }

   public List getResourceStack(final Identifier location) {
      Identifier metadataLocation = getMetadataLocation(location);
      List result = new ArrayList();
      boolean filterMeta = false;
      String lastFilterName = null;

      for(int i = this.fallbacks.size() - 1; i >= 0; --i) {
         FallbackResourceManager.PackEntry entry = (FallbackResourceManager.PackEntry)this.fallbacks.get(i);
         PackResources fileSource = entry.resources;
         if (fileSource != null) {
            IoSupplier resource = fileSource.getResource(this.type, location);
            if (resource != null) {
               IoSupplier metadataGetter;
               if (filterMeta) {
                  metadataGetter = ResourceMetadata.EMPTY_SUPPLIER;
               } else {
                  metadataGetter = () -> {
                     IoSupplier metaResource = fileSource.getResource(this.type, metadataLocation);
                     return metaResource != null ? parseMetadata(metaResource) : ResourceMetadata.EMPTY;
                  };
               }

               result.add(new Resource(fileSource, resource, metadataGetter));
            }
         }

         if (entry.isFiltered(location)) {
            lastFilterName = entry.name;
            break;
         }

         if (entry.isFiltered(metadataLocation)) {
            filterMeta = true;
         }
      }

      if (result.isEmpty() && lastFilterName != null) {
         LOGGER.warn("Resource {} not found, but was filtered by pack {}", location, lastFilterName);
      }

      return Lists.reverse(result);
   }

   private static boolean isMetadata(final Identifier location) {
      return location.getPath().endsWith(".mcmeta");
   }

   private static Identifier getIdentifierFromMetadata(final Identifier identifier) {
      String newPath = identifier.getPath().substring(0, identifier.getPath().length() - ".mcmeta".length());
      return identifier.withPath(newPath);
   }

   private static Identifier getMetadataLocation(final Identifier identifier) {
      return identifier.withPath(identifier.getPath() + ".mcmeta");
   }

   public Map listResources(final String directory, final ResourceManager.Selector selector) {
      Map topResourceForFileLocation = new HashMap();
      Map topResourceForMetaLocation = new HashMap();
      int packCount = this.fallbacks.size();

      for(int i = 0; i < packCount; ++i) {
         FallbackResourceManager.PackEntry entry = (FallbackResourceManager.PackEntry)this.fallbacks.get(i);
         entry.filterAll(topResourceForFileLocation.keySet());
         entry.filterAll(topResourceForMetaLocation.keySet());
         PackResources packResources = entry.resources;
         if (packResources != null) {
            int packIndex = i;
            packResources.listResources(this.type, this.namespace, directory, (resource, streamSupplier) -> {
               record ResourceWithSourceAndIndex(PackResources packResources, IoSupplier resource, int packIndex) {
               }

               if (isMetadata(resource)) {
                  if (selector.isIncluded(getIdentifierFromMetadata(resource))) {
                     topResourceForMetaLocation.put(resource, new ResourceWithSourceAndIndex(packResources, streamSupplier, packIndex));
                  }
               } else if (selector.isIncluded(resource)) {
                  topResourceForFileLocation.put(resource, new ResourceWithSourceAndIndex(packResources, streamSupplier, packIndex));
               }

            });
         }
      }

      Map result = new TreeMap();
      topResourceForFileLocation.forEach((location, resource) -> {
         Identifier metadataLocation = getMetadataLocation(location);
         ResourceWithSourceAndIndex metaResource = (ResourceWithSourceAndIndex)topResourceForMetaLocation.get(metadataLocation);
         IoSupplier metaGetter;
         if (metaResource != null && metaResource.packIndex >= resource.packIndex) {
            metaGetter = convertToMetadata(metaResource.resource);
         } else {
            metaGetter = ResourceMetadata.EMPTY_SUPPLIER;
         }

         result.put(location, createResource(resource.packResources, location, resource.resource, metaGetter));
      });
      return result;
   }

   private IoSupplier createStackMetadataFinder(final Identifier location, final int finalPackIndex) {
      return () -> {
         Identifier metadataLocation = getMetadataLocation(location);

         for(int i = this.fallbacks.size() - 1; i >= finalPackIndex; --i) {
            FallbackResourceManager.PackEntry entry = (FallbackResourceManager.PackEntry)this.fallbacks.get(i);
            PackResources metadataPackCandidate = entry.resources;
            if (metadataPackCandidate != null) {
               IoSupplier resource = metadataPackCandidate.getResource(this.type, metadataLocation);
               if (resource != null) {
                  return parseMetadata(resource);
               }
            }

            if (entry.isFiltered(metadataLocation)) {
               break;
            }
         }

         return ResourceMetadata.EMPTY;
      };
   }

   private static IoSupplier convertToMetadata(final IoSupplier input) {
      return () -> parseMetadata(input);
   }

   private static ResourceMetadata parseMetadata(final IoSupplier input) throws IOException {
      InputStream metadata = (InputStream)input.get();

      ResourceMetadata var2;
      try {
         var2 = ResourceMetadata.fromJsonStream(metadata);
      } catch (Throwable var5) {
         if (metadata != null) {
            try {
               metadata.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (metadata != null) {
         metadata.close();
      }

      return var2;
   }

   private static void applyPackFiltersToExistingResources(final FallbackResourceManager.PackEntry entry, final Map foundResources) {
      for(FallbackResourceManager.EntryStack e : foundResources.values()) {
         if (entry.isFiltered(e.fileLocation)) {
            e.fileSources.clear();
         } else if (entry.isFiltered(e.metadataLocation())) {
            e.metaSources.clear();
         }
      }

   }

   private void listPackResources(final FallbackResourceManager.PackEntry entry, final String directory, final ResourceManager.Selector selector, final Map foundResources) {
      PackResources pack = entry.resources;
      if (pack != null) {
         pack.listResources(this.type, this.namespace, directory, (id, resource) -> {
            if (isMetadata(id)) {
               Identifier actualId = getIdentifierFromMetadata(id);
               if (!selector.isIncluded(actualId)) {
                  return;
               }

               ((FallbackResourceManager.EntryStack)foundResources.computeIfAbsent(actualId, FallbackResourceManager.EntryStack::new)).metaSources.put(pack, resource);
            } else {
               if (!selector.isIncluded(id)) {
                  return;
               }

               ((FallbackResourceManager.EntryStack)foundResources.computeIfAbsent(id, FallbackResourceManager.EntryStack::new)).fileSources.add(new FallbackResourceManager.ResourceWithSource(pack, resource));
            }

         });
      }
   }

   public Map listResourceStacks(final String directory, final ResourceManager.Selector selector) {
      Map foundResources = new HashMap();

      for(FallbackResourceManager.PackEntry entry : this.fallbacks) {
         applyPackFiltersToExistingResources(entry, foundResources);
         this.listPackResources(entry, directory, selector, foundResources);
      }

      SortedMap result = new TreeMap();

      for(FallbackResourceManager.EntryStack entry : foundResources.values()) {
         if (!entry.fileSources.isEmpty()) {
            List resources = new ArrayList();

            for(FallbackResourceManager.ResourceWithSource stackEntry : entry.fileSources) {
               PackResources source = stackEntry.source;
               IoSupplier metaSource = (IoSupplier)entry.metaSources.get(source);
               IoSupplier metaGetter = metaSource != null ? convertToMetadata(metaSource) : ResourceMetadata.EMPTY_SUPPLIER;
               resources.add(createResource(source, entry.fileLocation, stackEntry.resource, metaGetter));
            }

            result.put(entry.fileLocation, resources);
         }
      }

      return result;
   }

   public Stream listPacks() {
      return this.fallbacks.stream().map((p) -> p.resources).filter(Objects::nonNull);
   }

   private static record EntryStack(Identifier fileLocation, Identifier metadataLocation, List fileSources, Map metaSources) {
      public EntryStack(final Identifier fileLocation) {
         this(fileLocation, FallbackResourceManager.getMetadataLocation(fileLocation), new ArrayList(), new Object2ObjectArrayMap());
      }
   }

   private static class LeakedResourceWarningInputStream extends FilterInputStream {
      private final Supplier message;
      private boolean closed;

      public LeakedResourceWarningInputStream(final InputStream wrapped, final Identifier location, final String name) {
         super(wrapped);
         Exception exception = new Exception("Stacktrace");
         this.message = () -> {
            StringWriter data = new StringWriter();
            exception.printStackTrace(new PrintWriter(data));
            return "Leaked resource: '" + String.valueOf(location) + "' loaded from pack: '" + name + "'\n" + String.valueOf(data);
         };
      }

      public void close() throws IOException {
         super.close();
         this.closed = true;
      }

      protected void finalize() throws Throwable {
         if (!this.closed) {
            FallbackResourceManager.LOGGER.warn("{}", this.message.get());
         }

         super.finalize();
      }
   }

   private static record PackEntry(String name, @Nullable PackResources resources, PackResources.@Nullable Filter filter) {
      public void filterAll(final Collection collection) {
         if (this.filter != null) {
            collection.removeIf(this.filter::isFiltered);
         }

      }

      public boolean isFiltered(final Identifier location) {
         return this.filter != null && this.filter.isFiltered(location);
      }
   }

   private static record ResourceWithSource(PackResources source, IoSupplier resource) {
   }
}
