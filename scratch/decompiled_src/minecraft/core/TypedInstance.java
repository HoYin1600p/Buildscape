package net.minecraft.core;

import java.util.stream.Stream;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

public interface TypedInstance {
   Holder typeHolder();

   default Stream tags() {
      return this.typeHolder().tags();
   }

   default boolean is(final TagKey tag) {
      return this.typeHolder().is(tag);
   }

   default boolean is(final HolderSet set) {
      return set.contains(this.typeHolder());
   }

   default boolean is(final Object rawType) {
      return this.typeHolder().value() == rawType;
   }

   default boolean is(final Holder type) {
      return this.typeHolder() == type;
   }

   default boolean is(final ResourceKey type) {
      return this.typeHolder().is(type);
   }
}
