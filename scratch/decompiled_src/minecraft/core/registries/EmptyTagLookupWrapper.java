package net.minecraft.core.registries;

import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;

public record EmptyTagLookupWrapper(HolderLookup.RegistryLookup parent) implements HolderLookup.RegistryLookup.Delegate {
   public static HolderLookup.RegistryLookup wrap(final HolderLookup.RegistryLookup registryLookup) {
      return (HolderLookup.RegistryLookup)(registryLookup instanceof EmptyTagLookupWrapper ? registryLookup : new EmptyTagLookupWrapper(registryLookup));
   }

   public static HolderLookup.Provider wrap(final HolderLookup.Provider provider) {
      return HolderLookup.Provider.create(provider.listRegistries().map(EmptyTagLookupWrapper::wrap));
   }

   public Optional get(final TagKey id) {
      return Optional.of(this.getOrThrow(id));
   }

   public HolderSet.Named getOrThrow(final TagKey id) {
      return HolderSet.emptyNamed(this.parent, id);
   }

   public boolean canSerialize(final HolderOwner owner) {
      return this.parent.canSerialize(owner);
   }

   public Stream listTags() {
      throw new UnsupportedOperationException("Tags are not available in datagen");
   }
}
