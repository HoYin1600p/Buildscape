package net.minecraft.tags;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Optional;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public record TagKey(ResourceKey registry, Identifier location) {
   private static final Interner VALUES = Interners.newWeakInterner();

   public static Codec codec(final ResourceKey registryName) {
      return Identifier.CODEC.xmap((name) -> create(registryName, name), TagKey::location);
   }

   public static Codec hashedCodec(final ResourceKey registryName) {
      return Codec.STRING.comapFlatMap((name) -> name.startsWith("#") ? Identifier.read(name.substring(1)).map((id) -> create(registryName, id)) : DataResult.error(() -> "Not a tag id"), (e) -> "#" + String.valueOf(e.location));
   }

   public static StreamCodec streamCodec(final ResourceKey registryName) {
      return Identifier.STREAM_CODEC.map((location) -> create(registryName, location), TagKey::location);
   }

   public static TagKey create(final ResourceKey registry, final Identifier location) {
      return (TagKey)VALUES.intern(new TagKey(registry, location));
   }

   public boolean isFor(final ResourceKey registry) {
      return this.registry == registry;
   }

   public Optional cast(final ResourceKey registry) {
      return this.isFor(registry) ? Optional.of(this) : Optional.empty();
   }

   public String toString() {
      return "TagKey[" + String.valueOf(this.registry.identifier()) + " / " + String.valueOf(this.location) + "]";
   }
}
