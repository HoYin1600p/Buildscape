package net.minecraft.data.tags;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;

public interface TagAppender {
   TagAppender add(ResourceKey element);

   default TagAppender add(final ResourceKey... elements) {
      return this.addAll(Arrays.stream(elements));
   }

   default TagAppender addAll(final Collection elements) {
      elements.forEach(this::add);
      return this;
   }

   default TagAppender addAll(final Stream elements) {
      elements.forEach(this::add);
      return this;
   }

   TagAppender addOptional(ResourceKey element);

   TagAppender addTag(TagKey tag);

   TagAppender addOptionalTag(TagKey tag);

   static TagAppender forBuilder(final TagBuilder builder) {
      return new TagAppender() {
         public TagAppender add(final ResourceKey element) {
            builder.addElement(element.identifier());
            return this;
         }

         public TagAppender addOptional(final ResourceKey element) {
            builder.addOptionalElement(element.identifier());
            return this;
         }

         public TagAppender addTag(final TagKey tag) {
            builder.addTag(tag.location());
            return this;
         }

         public TagAppender addOptionalTag(final TagKey tag) {
            builder.addOptionalTag(tag.location());
            return this;
         }
      };
   }
}
