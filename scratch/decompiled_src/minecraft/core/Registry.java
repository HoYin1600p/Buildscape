package net.minecraft.core;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.Lifecycle;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.core.component.DataComponentLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;

public interface Registry extends IdMap, Keyable, HolderLookup.RegistryLookup {
   ResourceKey key();

   default Codec byNameCodec() {
      return this.referenceHolderWithLifecycle().flatComapMap(Holder.Reference::value, (value) -> this.safeCastToReference(this.wrapAsHolder(value)));
   }

   default Codec holderByNameCodec() {
      return this.referenceHolderWithLifecycle().flatComapMap((holder) -> holder, this::safeCastToReference);
   }

   private Codec referenceHolderWithLifecycle() {
      Codec referenceCodec = Identifier.CODEC.comapFlatMap((name) -> (DataResult)this.get(name).map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Unknown registry key in " + String.valueOf(this.key()) + ": " + String.valueOf(name))), (holder) -> holder.key().identifier());
      return ExtraCodecs.overrideLifecycle(referenceCodec, (e) -> (Lifecycle)this.registrationInfo(e.key()).map(RegistrationInfo::lifecycle).orElse(Lifecycle.experimental()));
   }

   private DataResult safeCastToReference(final Holder holder) {
      DataResult var10000;
      if (holder instanceof Holder.Reference reference) {
         var10000 = DataResult.success(reference);
      } else {
         var10000 = DataResult.error(() -> "Unregistered holder in " + String.valueOf(this.key()) + ": " + String.valueOf(holder));
      }

      return var10000;
   }

   default Stream keys(final DynamicOps ops) {
      return this.keySet().stream().map((k) -> ops.createString(k.toString()));
   }

   @Nullable Identifier getKey(Object thing);

   Optional getResourceKey(Object thing);

   int getId(@Nullable Object thing);

   @Nullable Object getValue(@Nullable ResourceKey key);

   @Nullable Object getValue(@Nullable Identifier key);

   Optional registrationInfo(ResourceKey element);

   default Optional getOptional(final @Nullable Identifier key) {
      return Optional.ofNullable(this.getValue(key));
   }

   default Optional getOptional(final @Nullable ResourceKey key) {
      return Optional.ofNullable(this.getValue(key));
   }

   Optional getAny();

   default Object getValueOrThrow(final ResourceKey key) {
      Object value = (T)this.getValue(key);
      if (value == null) {
         throw new IllegalStateException("Missing key in " + String.valueOf(this.key()) + ": " + String.valueOf(key));
      } else {
         return value;
      }
   }

   Set keySet();

   Set entrySet();

   Set registryKeySet();

   Optional getRandom(RandomSource random);

   default Stream stream() {
      return StreamSupport.stream(this.spliterator(), false);
   }

   boolean containsKey(Identifier key);

   boolean containsKey(ResourceKey key);

   static Object register(final Registry registry, final String name, final Object value) {
      return register(registry, Identifier.parse(name), value);
   }

   static Object register(final Registry registry, final Identifier location, final Object value) {
      return register(registry, ResourceKey.create(registry.key(), location), value);
   }

   static Object register(final Registry registry, final ResourceKey key, final Object value) {
      ((WritableRegistry)registry).register(key, value, RegistrationInfo.BUILT_IN);
      return value;
   }

   static Holder.Reference registerForHolder(final Registry registry, final ResourceKey key, final Object value) {
      return ((WritableRegistry)registry).register(key, value, RegistrationInfo.BUILT_IN);
   }

   static Holder.Reference registerForHolder(final Registry registry, final Identifier location, final Object value) {
      return registerForHolder(registry, ResourceKey.create(registry.key(), location), value);
   }

   Registry freeze();

   Holder.Reference createIntrusiveHolder(Object value);

   Optional get(int id);

   Optional get(Identifier id);

   Holder wrapAsHolder(Object value);

   default Iterable getTagOrEmpty(final TagKey id) {
      return (Iterable)DataFixUtils.orElse(this.get(id), List.of());
   }

   Stream getTags();

   default IdMap asHolderIdMap() {
      return new IdMap() {
         {
            Objects.requireNonNull(Registry.this);
         }

         public int getId(final Holder thing) {
            return Registry.this.getId(thing.value());
         }

         public @Nullable Holder byId(final int id) {
            return (Holder)Registry.this.get(id).orElse((Object)null);
         }

         public int size() {
            return Registry.this.size();
         }

         public Iterator iterator() {
            return Registry.this.listElements().map((e) -> e).iterator();
         }
      };
   }

   Registry.PendingTags prepareTagReload(TagLoader.LoadResult tags);

   DataComponentLookup componentLookup();

   public interface PendingTags {
      ResourceKey key();

      HolderLookup.RegistryLookup lookup();

      void apply();

      int size();
   }
}
