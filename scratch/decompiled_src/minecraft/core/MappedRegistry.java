package net.minecraft.core;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import net.minecraft.core.component.DataComponentLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

public class MappedRegistry implements WritableRegistry {
   private final ResourceKey key;
   private final ObjectList byId = new ObjectArrayList(256);
   private final Reference2IntMap toId = (Reference2IntMap)Util.make(new Reference2IntOpenHashMap(), (t) -> t.defaultReturnValue(-1));
   private final Map byLocation = new HashMap();
   private final Map byKey = new HashMap();
   private final Map byValue = new IdentityHashMap();
   private final Map registrationInfos = new IdentityHashMap();
   private Lifecycle registryLifecycle;
   private final Map frozenTags = new IdentityHashMap();
   private MappedRegistry.TagSet allTags = MappedRegistry.TagSet.unbound();
   private @Nullable DataComponentLookup componentLookup;
   private boolean frozen;
   private @Nullable Map unregisteredIntrusiveHolders;

   public Stream listTags() {
      return this.getTags();
   }

   public MappedRegistry(final ResourceKey key, final Lifecycle lifecycle) {
      this(key, lifecycle, false);
   }

   public MappedRegistry(final ResourceKey key, final Lifecycle initialLifecycle, final boolean intrusiveHolders) {
      this.key = key;
      this.registryLifecycle = initialLifecycle;
      if (intrusiveHolders) {
         this.unregisteredIntrusiveHolders = new IdentityHashMap();
      }

   }

   public ResourceKey key() {
      return this.key;
   }

   public String toString() {
      return "Registry[" + String.valueOf(this.key) + " (" + String.valueOf(this.registryLifecycle) + ")]";
   }

   private void validateWrite() {
      if (this.frozen) {
         throw new IllegalStateException("Registry is already frozen");
      }
   }

   private void validateWrite(final ResourceKey key) {
      if (this.frozen) {
         throw new IllegalStateException("Registry is already frozen (trying to add key " + String.valueOf(key) + ")");
      }
   }

   public Holder.Reference register(final ResourceKey key, final Object value, final RegistrationInfo registrationInfo) {
      this.validateWrite(key);
      Objects.requireNonNull(key);
      Objects.requireNonNull(value);
      if (this.byLocation.containsKey(key.identifier())) {
         throw (IllegalStateException)Util.pauseInIde(new IllegalStateException("Adding duplicate key '" + String.valueOf(key) + "' to registry"));
      } else if (this.byValue.containsKey(value)) {
         throw (IllegalStateException)Util.pauseInIde(new IllegalStateException("Adding duplicate value '" + String.valueOf(value) + "' to registry"));
      } else {
         Holder.Reference holder;
         if (this.unregisteredIntrusiveHolders != null) {
            holder = (Holder.Reference)this.unregisteredIntrusiveHolders.remove(value);
            if (holder == null) {
               throw new AssertionError("Missing intrusive holder for " + String.valueOf(key) + ":" + String.valueOf(value));
            }

            holder.bindKey(key);
         } else {
            holder = (Holder.Reference)this.byKey.computeIfAbsent(key, (k) -> Holder.Reference.createStandAlone(this, k));
         }

         this.byKey.put(key, holder);
         this.byLocation.put(key.identifier(), holder);
         this.byValue.put(value, holder);
         int newId = this.byId.size();
         this.byId.add(holder);
         this.toId.put(value, newId);
         this.registrationInfos.put(key, registrationInfo);
         this.registryLifecycle = this.registryLifecycle.add(registrationInfo.lifecycle());
         return holder;
      }
   }

   public @Nullable Identifier getKey(final Object thing) {
      Holder.Reference holder = (Holder.Reference)this.byValue.get(thing);
      return holder != null ? holder.key().identifier() : null;
   }

   public Optional getResourceKey(final Object thing) {
      return Optional.ofNullable((Holder.Reference)this.byValue.get(thing)).map(Holder.Reference::key);
   }

   public int getId(final @Nullable Object thing) {
      return this.toId.getInt(thing);
   }

   public @Nullable Object getValue(final @Nullable ResourceKey key) {
      return getValueFromNullable((Holder.Reference)this.byKey.get(key));
   }

   public @Nullable Object byId(final int id) {
      return id >= 0 && id < this.byId.size() ? ((Holder.Reference)this.byId.get(id)).value() : null;
   }

   public Optional get(final int id) {
      return id >= 0 && id < this.byId.size() ? Optional.ofNullable((Holder.Reference)this.byId.get(id)) : Optional.empty();
   }

   public Optional get(final Identifier id) {
      return Optional.ofNullable((Holder.Reference)this.byLocation.get(id));
   }

   public Optional get(final ResourceKey id) {
      return Optional.ofNullable((Holder.Reference)this.byKey.get(id));
   }

   public Optional getAny() {
      return this.byId.isEmpty() ? Optional.empty() : Optional.of((Holder.Reference)this.byId.getFirst());
   }

   public Holder wrapAsHolder(final Object value) {
      Holder.Reference existingHolder = (Holder.Reference)this.byValue.get(value);
      return (Holder)(existingHolder != null ? existingHolder : Holder.direct(value));
   }

   private Holder.Reference getOrCreateHolderOrThrow(final ResourceKey key) {
      return (Holder.Reference)this.byKey.computeIfAbsent(key, (id) -> {
         if (this.unregisteredIntrusiveHolders != null) {
            throw new IllegalStateException("This registry can't create new holders without value");
         } else {
            this.validateWrite(id);
            return Holder.Reference.createStandAlone(this, id);
         }
      });
   }

   public int size() {
      return this.byKey.size();
   }

   public Optional registrationInfo(final ResourceKey element) {
      return Optional.ofNullable((RegistrationInfo)this.registrationInfos.get(element));
   }

   public Lifecycle registryLifecycle() {
      return this.registryLifecycle;
   }

   public Iterator iterator() {
      return Iterators.transform(this.byId.iterator(), Holder::value);
   }

   public @Nullable Object getValue(final @Nullable Identifier key) {
      Holder.Reference result = (Holder.Reference)this.byLocation.get(key);
      return getValueFromNullable(result);
   }

   private static @Nullable Object getValueFromNullable(final Holder.@Nullable Reference result) {
      return result != null ? result.value() : null;
   }

   public Set keySet() {
      return Collections.unmodifiableSet(this.byLocation.keySet());
   }

   public Set registryKeySet() {
      return Collections.unmodifiableSet(this.byKey.keySet());
   }

   public Set entrySet() {
      return Collections.unmodifiableSet(Util.mapValuesLazy(this.byKey, Holder::value).entrySet());
   }

   public Stream listElements() {
      return this.byId.stream();
   }

   public Stream getTags() {
      return this.allTags.getTags();
   }

   private HolderSet.Named getOrCreateTagForRegistration(final TagKey tag) {
      return (HolderSet.Named)this.frozenTags.computeIfAbsent(tag, this::createTag);
   }

   private HolderSet.Named createTag(final TagKey tag) {
      return new HolderSet.Named(this, tag);
   }

   public boolean isEmpty() {
      return this.byKey.isEmpty();
   }

   public Optional getRandom(final RandomSource random) {
      return Util.getRandomSafe(this.byId, random);
   }

   public boolean containsKey(final Identifier key) {
      return this.byLocation.containsKey(key);
   }

   public boolean containsKey(final ResourceKey key) {
      return this.byKey.containsKey(key);
   }

   public DataComponentLookup componentLookup() {
      return (DataComponentLookup)Objects.requireNonNull(this.componentLookup, "Registry not frozen yet");
   }

   public Registry freeze() {
      if (this.frozen) {
         return this;
      } else {
         this.frozen = true;
         this.byValue.forEach((value, holder) -> holder.bindValue(value));
         List unboundEntries = this.byKey.entrySet().stream().filter((e) -> !((Holder.Reference)e.getValue()).isBound()).map((e) -> ((ResourceKey)e.getKey()).identifier()).sorted().toList();
         if (!unboundEntries.isEmpty()) {
            throw new IllegalStateException("Unbound values in registry " + String.valueOf(this.key()) + ": " + String.valueOf(unboundEntries));
         } else {
            if (this.unregisteredIntrusiveHolders != null) {
               if (!this.unregisteredIntrusiveHolders.isEmpty()) {
                  throw new IllegalStateException("Some intrusive holders were not registered: " + String.valueOf(this.unregisteredIntrusiveHolders.values()));
               }

               this.unregisteredIntrusiveHolders = null;
            }

            if (this.allTags.isBound()) {
               throw new IllegalStateException("Tags already present before freezing");
            } else {
               List unboundTags = this.frozenTags.entrySet().stream().filter((e) -> !((HolderSet.Named)e.getValue()).isBound()).map((e) -> ((TagKey)e.getKey()).location()).sorted().toList();
               if (!unboundTags.isEmpty()) {
                  throw new IllegalStateException("Unbound tags in registry " + String.valueOf(this.key()) + ": " + String.valueOf(unboundTags));
               } else {
                  this.componentLookup = new DataComponentLookup(this.byId);
                  this.allTags = MappedRegistry.TagSet.fromMap(this.frozenTags);
                  this.refreshTagsInHolders();
                  return this;
               }
            }
         }
      }
   }

   public Holder.Reference createIntrusiveHolder(final Object value) {
      if (this.unregisteredIntrusiveHolders == null) {
         throw new IllegalStateException("This registry can't create intrusive holders");
      } else {
         this.validateWrite();
         return (Holder.Reference)this.unregisteredIntrusiveHolders.computeIfAbsent(value, (v) -> Holder.Reference.createIntrusive(this, v));
      }
   }

   public Optional get(final TagKey id) {
      return this.allTags.get(id);
   }

   private Holder.Reference validateAndUnwrapTagElement(final TagKey id, final Holder value) {
      if (!value.canSerializeIn(this)) {
         throw new IllegalStateException("Can't create named set " + String.valueOf(id) + " containing value " + String.valueOf(value) + " from outside registry " + String.valueOf(this));
      } else if (value instanceof Holder.Reference) {
         return (Holder.Reference)value;
      } else {
         throw new IllegalStateException("Found direct holder " + String.valueOf(value) + " value in tag " + String.valueOf(id));
      }
   }

   public void bindTags(final Map pendingTags) {
      this.validateWrite();
      pendingTags.forEach((id, values) -> this.getOrCreateTagForRegistration(id).bind(values));
   }

   private void refreshTagsInHolders() {
      Map tagsForElement = new IdentityHashMap();
      this.byKey.values().forEach((h) -> tagsForElement.put(h, new ArrayList()));
      this.allTags.forEach((id, values) -> {
         for(Holder value : values) {
            Holder.Reference reference = this.validateAndUnwrapTagElement(id, value);
            ((List)tagsForElement.get(reference)).add(id);
         }

      });
      tagsForElement.forEach(Holder.Reference::bindTags);
   }

   public void bindAllTagsToEmpty() {
      this.validateWrite();
      this.frozenTags.values().forEach((e) -> e.bind(List.of()));
   }

   public HolderGetter createRegistrationLookup() {
      this.validateWrite();
      return new HolderGetter() {
         {
            Objects.requireNonNull(MappedRegistry.this);
         }

         public Optional get(final ResourceKey id) {
            return Optional.of(this.getOrThrow(id));
         }

         public Holder.Reference getOrThrow(final ResourceKey id) {
            return MappedRegistry.this.getOrCreateHolderOrThrow(id);
         }

         public Optional get(final TagKey id) {
            return Optional.of(this.getOrThrow(id));
         }

         public HolderSet.Named getOrThrow(final TagKey id) {
            return MappedRegistry.this.getOrCreateTagForRegistration(id);
         }

         public boolean canSerialize(final HolderOwner owner) {
            return MappedRegistry.this.canSerialize(owner);
         }
      };
   }

   public Registry.PendingTags prepareTagReload(final TagLoader.LoadResult tags) {
      if (!this.frozen) {
         throw new IllegalStateException("Invalid method used for tag loading");
      } else {
         ImmutableMap.Builder pendingTagsBuilder = ImmutableMap.builder();
         final Map pendingContents = new HashMap();
         tags.tags().forEach((id, contents) -> {
            HolderSet.Named tagToAdd = (HolderSet.Named)this.frozenTags.get(id);
            if (tagToAdd == null) {
               tagToAdd = this.createTag(id);
            }

            pendingTagsBuilder.put(id, tagToAdd);
            pendingContents.put(id, List.copyOf(contents));
         });
         final ImmutableMap pendingTags = pendingTagsBuilder.build();
         final HolderLookup.RegistryLookup patchedHolder = new HolderLookup.RegistryLookup.Delegate() {
            {
               Objects.requireNonNull(MappedRegistry.this);
            }

            public HolderLookup.RegistryLookup parent() {
               return MappedRegistry.this;
            }

            public Optional get(final TagKey id) {
               return Optional.ofNullable((HolderSet.Named)pendingTags.get(id));
            }

            public Stream listTags() {
               return pendingTags.values().stream();
            }
         };
         return new Registry.PendingTags() {
            {
               Objects.requireNonNull(MappedRegistry.this);
            }

            public ResourceKey key() {
               return MappedRegistry.this.key();
            }

            public int size() {
               return pendingContents.size();
            }

            public HolderLookup.RegistryLookup lookup() {
               return patchedHolder;
            }

            public void apply() {
               pendingTags.forEach((id, tag) -> {
                  List values = (List)pendingContents.getOrDefault(id, List.of());
                  tag.bind(values);
               });
               MappedRegistry.this.allTags = MappedRegistry.TagSet.fromMap(pendingTags);
               MappedRegistry.this.refreshTagsInHolders();
            }
         };
      }
   }

   private interface TagSet {
      static MappedRegistry.TagSet unbound() {
         return new MappedRegistry.TagSet() {
            public boolean isBound() {
               return false;
            }

            public Optional get(final TagKey id) {
               throw new IllegalStateException("Tags not bound, trying to access " + String.valueOf(id));
            }

            public void forEach(final BiConsumer action) {
               throw new IllegalStateException("Tags not bound");
            }

            public Stream getTags() {
               throw new IllegalStateException("Tags not bound");
            }
         };
      }

      static MappedRegistry.TagSet fromMap(final Map tags) {
         return new MappedRegistry.TagSet() {
            public boolean isBound() {
               return true;
            }

            public Optional get(final TagKey id) {
               return Optional.ofNullable((HolderSet.Named)tags.get(id));
            }

            public void forEach(final BiConsumer action) {
               tags.forEach(action);
            }

            public Stream getTags() {
               return tags.values().stream();
            }
         };
      }

      boolean isBound();

      Optional get(TagKey id);

      void forEach(BiConsumer action);

      Stream getTags();
   }
}
