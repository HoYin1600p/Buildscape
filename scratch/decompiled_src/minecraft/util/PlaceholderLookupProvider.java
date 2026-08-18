package net.minecraft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JavaOps;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

public class PlaceholderLookupProvider implements HolderGetter.Provider {
   private final HolderLookup.Provider context;
   private final PlaceholderLookupProvider.UniversalLookup lookup = new PlaceholderLookupProvider.UniversalLookup();
   private final Map holders = new HashMap();
   private final Map holderSets = new HashMap();

   public PlaceholderLookupProvider(final HolderLookup.Provider context) {
      this.context = context;
   }

   public Optional lookup(final ResourceKey key) {
      return Optional.of(this.lookup.castAsLookup());
   }

   public RegistryOps createSerializationContext(final DynamicOps parent) {
      return RegistryOps.create(parent, new RegistryOps.RegistryInfoLookup() {
         {
            Objects.requireNonNull(PlaceholderLookupProvider.this);
         }

         public Optional lookup(final ResourceKey registryKey) {
            Optional result = PlaceholderLookupProvider.this.context.lookup(registryKey).map((e) -> e);
            return result.or(() -> Optional.of(PlaceholderLookupProvider.this.lookup.castAsLookup()));
         }
      });
   }

   public RegistryContextSwapper createSwapper() {
      return new RegistryContextSwapper() {
         {
            Objects.requireNonNull(PlaceholderLookupProvider.this);
         }

         public DataResult swapTo(final Codec codec, final Object value, final HolderLookup.Provider newContext) {
            return codec.encodeStart(PlaceholderLookupProvider.this.createSerializationContext(JavaOps.INSTANCE), value).flatMap((v) -> codec.parse(newContext.createSerializationContext(JavaOps.INSTANCE), v));
         }
      };
   }

   public boolean hasRegisteredPlaceholders() {
      return !this.holders.isEmpty() || !this.holderSets.isEmpty();
   }

   private class UniversalLookup implements HolderGetter {
      private UniversalLookup() {
         Objects.requireNonNull(PlaceholderLookupProvider.this);
         super();
      }

      public Optional get(final ResourceKey id) {
         return Optional.of(this.getOrCreate(id));
      }

      public Holder.Reference getOrThrow(final ResourceKey id) {
         return this.getOrCreate(id);
      }

      private Holder.Reference getOrCreate(final ResourceKey id) {
         return (Holder.Reference)PlaceholderLookupProvider.this.holders.computeIfAbsent(id, (k) -> Holder.Reference.createStandAlone(this, k));
      }

      public Optional get(final TagKey id) {
         return Optional.of(this.getOrCreate(id));
      }

      public HolderSet.Named getOrThrow(final TagKey id) {
         return this.getOrCreate(id);
      }

      private HolderSet.Named getOrCreate(final TagKey id) {
         return (HolderSet.Named)PlaceholderLookupProvider.this.holderSets.computeIfAbsent(id, (k) -> HolderSet.emptyNamed(this, k));
      }

      public HolderGetter castAsLookup() {
         return this;
      }
   }
}
