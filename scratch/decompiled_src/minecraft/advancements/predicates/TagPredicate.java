package net.minecraft.advancements.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

public record TagPredicate(HolderSet tag, boolean expected) {
   public static Codec codec(final ResourceKey registryKey) {
      return RecordCodecBuilder.create((i) -> i.group(RegistryCodecs.holderSet(registryKey).fieldOf("id").forGetter(TagPredicate::tag), Codec.BOOL.fieldOf("expected").forGetter(TagPredicate::expected)).apply(i, TagPredicate::new));
   }

   public static TagPredicate is(final HolderGetter lookup, final TagKey tag) {
      return is(lookup.getOrThrow(tag));
   }

   public static TagPredicate is(final HolderSet tag) {
      return new TagPredicate(tag, true);
   }

   public static TagPredicate isNot(final HolderGetter lookup, final TagKey tag) {
      return isNot(lookup.getOrThrow(tag));
   }

   public static TagPredicate isNot(final HolderSet tag) {
      return new TagPredicate(tag, false);
   }

   public boolean matches(final Holder holder) {
      return this.tag.contains(holder) == this.expected;
   }
}
