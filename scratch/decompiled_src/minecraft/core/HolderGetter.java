package net.minecraft.core;

import java.util.Optional;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;

public interface HolderGetter extends HolderOwner {
   Optional get(final ResourceKey id);

   default Holder.Reference getOrThrow(final ResourceKey id) {
      return (Holder.Reference)this.get(id).orElseThrow(() -> new IllegalStateException("Missing element " + String.valueOf(id)));
   }

   Optional get(final TagKey id);

   default HolderSet.Named getOrThrow(final TagKey id) {
      return (HolderSet.Named)this.get(id).orElseThrow(() -> new IllegalStateException("Missing tag " + String.valueOf(id)));
   }

   default Optional getRandomElementOf(final TagKey tag, final RandomSource random) {
      return this.get(tag).flatMap((holderSet) -> holderSet.getRandomElement(random));
   }

   public interface Provider {
      Optional lookup(final ResourceKey key);

      default HolderGetter lookupOrThrow(final ResourceKey key) {
         return (HolderGetter)this.lookup(key).orElseThrow(() -> new IllegalStateException("Registry " + String.valueOf(key.identifier()) + " not found"));
      }

      default Optional get(final ResourceKey id) {
         return this.lookup(id.registryKey()).flatMap((l) -> l.get(id));
      }

      default Holder.Reference getOrThrow(final ResourceKey id) {
         return (Holder.Reference)this.lookup(id.registryKey()).flatMap((l) -> l.get(id)).orElseThrow(() -> new IllegalStateException("Missing element " + String.valueOf(id)));
      }

      default Optional get(final TagKey id) {
         return this.lookup(id.registry()).flatMap((l) -> l.get(id));
      }

      default HolderSet.Named getOrThrow(final TagKey id) {
         return (HolderSet.Named)this.lookup(id.registry()).flatMap((l) -> l.get(id)).orElseThrow(() -> new IllegalStateException("Missing tag " + String.valueOf(id)));
      }
   }
}
