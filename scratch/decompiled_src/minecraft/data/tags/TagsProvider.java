package net.minecraft.data.tags;

import com.google.common.collect.Maps;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagFile;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Util;

public abstract class TagsProvider implements DataProvider {
   protected final PackOutput.PathProvider pathProvider;
   private final CompletableFuture lookupProvider;
   private final CompletableFuture contentsDone = new CompletableFuture();
   private final CompletableFuture parentProvider;
   protected final ResourceKey registryKey;
   private final Map builders = Maps.newLinkedHashMap();

   protected TagsProvider(final PackOutput output, final ResourceKey registryKey, final CompletableFuture lookupProvider) {
      this(output, registryKey, lookupProvider, CompletableFuture.completedFuture(TagsProvider.TagLookup.empty()));
   }

   protected TagsProvider(final PackOutput output, final ResourceKey registryKey, final CompletableFuture lookupProvider, final CompletableFuture parentProvider) {
      this.pathProvider = output.createRegistryTagsPathProvider(registryKey);
      this.registryKey = registryKey;
      this.parentProvider = parentProvider;
      this.lookupProvider = lookupProvider;
   }

   public final String getName() {
      return "Tags for " + String.valueOf(this.registryKey.identifier());
   }

   protected abstract void addTags(HolderLookup.Provider registries);

   public CompletableFuture run(final CachedOutput cache) {
      return this.createContentsProvider().thenApply((provider) -> {
         this.contentsDone.complete((Object)null);
         return provider;
      }).thenCombineAsync(this.parentProvider, (x$0, x$1) -> {
         record CombinedData(HolderLookup.Provider contents, TagsProvider.TagLookup parent) {
         }

         return new CombinedData(x$0, x$1);
      }, Util.backgroundExecutor()).thenCompose((c) -> {
         HolderLookup.RegistryLookup lookup = c.contents.lookupOrThrow(this.registryKey);
         Predicate elementCheck = (id) -> lookup.get(ResourceKey.create(this.registryKey, id)).isPresent();
         Predicate tagCheck = (id) -> this.builders.containsKey(id) || c.parent.contains(TagKey.create(this.registryKey, id));
         return CompletableFuture.allOf((CompletableFuture[])this.builders.entrySet().stream().map((entry) -> {
            Identifier id = (Identifier)entry.getKey();
            TagBuilder builder = (TagBuilder)entry.getValue();
            List entries = builder.build();
            List unresolvedEntries = entries.stream().filter((e) -> !e.verifyIfPresent(elementCheck, tagCheck)).toList();
            if (!unresolvedEntries.isEmpty()) {
               throw new IllegalArgumentException(String.format(Locale.ROOT, "Couldn't define tag %s as it is missing following references: %s", id, unresolvedEntries.stream().map(Objects::toString).collect(Collectors.joining(","))));
            } else {
               Path path = this.pathProvider.json(id);
               return DataProvider.saveStable(cache, c.contents, TagFile.CODEC, new TagFile(entries, builder.shouldReplace()), path);
            }
         }).toArray((x$0) -> new CompletableFuture[x$0]));
      });
   }

   protected TagBuilder getOrCreateRawBuilder(final TagKey tag) {
      return (TagBuilder)this.builders.computeIfAbsent(tag.location(), (k) -> TagBuilder.create());
   }

   public CompletableFuture contentsGetter() {
      return this.contentsDone.thenApply((ignore) -> (id) -> Optional.ofNullable((TagBuilder)this.builders.get(id.location())));
   }

   protected CompletableFuture createContentsProvider() {
      return this.lookupProvider.thenApply((registries) -> {
         this.builders.clear();
         this.addTags(registries);
         return registries;
      });
   }

   protected TagAppender tag(final TagKey tag) {
      TagBuilder builder = this.getOrCreateRawBuilder(tag);
      return TagAppender.forBuilder(builder);
   }

   protected TagAppender tag(final TagKey tag, final boolean replace) {
      TagBuilder builder = this.getOrCreateRawBuilder(tag);
      builder.setReplace(replace);
      return TagAppender.forBuilder(builder);
   }

   @FunctionalInterface
   public interface TagLookup extends Function {
      static TagsProvider.TagLookup empty() {
         return (id) -> Optional.empty();
      }

      default boolean contains(final TagKey key) {
         return ((Optional)this.apply(key)).isPresent();
      }
   }
}
