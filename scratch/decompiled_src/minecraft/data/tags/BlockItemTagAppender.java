package net.minecraft.data.tags;

import java.util.Arrays;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.ColorCollection;
import net.minecraft.world.level.block.WeatheringCopperCollection;

public abstract class BlockItemTagAppender implements TagAppender {
   private final TagAppender original;

   public BlockItemTagAppender(final TagAppender original) {
      this.original = original;
   }

   protected abstract ResourceKey convertElement(BlockItemId element);

   public BlockItemTagAppender add(final ResourceKey element) {
      this.original.add(element);
      return this;
   }

   public BlockItemTagAppender add(final BlockItemId... ids) {
      this.original.addAll(Arrays.stream(ids).map(this::convertElement));
      return this;
   }

   public BlockItemTagAppender addAll(final ColorCollection collection) {
      collection.forEach(this::add);
      return this;
   }

   public BlockItemTagAppender addAll(final WeatheringCopperCollection collection) {
      collection.forEach(this::add);
      return this;
   }

   @SafeVarargs
   public final BlockItemTagAppender add(final ResourceKey... elements) {
      this.original.add(elements);
      return this;
   }

   public BlockItemTagAppender addOptional(final ResourceKey element) {
      this.original.addOptional(element);
      return this;
   }

   public BlockItemTagAppender addTag(final TagKey tag) {
      this.original.addTag(tag);
      return this;
   }

   public BlockItemTagAppender addOptionalTag(final TagKey tag) {
      this.original.addOptionalTag(tag);
      return this;
   }
}
