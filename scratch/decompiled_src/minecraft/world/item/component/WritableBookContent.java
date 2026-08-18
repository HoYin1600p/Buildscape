package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.network.Filterable;

public record WritableBookContent(List pages) implements BookContent {
   public static final WritableBookContent EMPTY = new WritableBookContent(List.of());
   public static final int PAGE_EDIT_LENGTH = 1024;
   public static final int MAX_PAGES = 100;
   private static final Codec PAGE_CODEC = Filterable.codec(Codec.string(0, 1024));
   public static final Codec PAGES_CODEC = PAGE_CODEC.sizeLimitedListOf(100);
   public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(PAGES_CODEC.optionalFieldOf("pages", List.of()).forGetter(WritableBookContent::pages)).apply(i, WritableBookContent::new));
   public static final StreamCodec STREAM_CODEC = Filterable.streamCodec(ByteBufCodecs.stringUtf8(1024)).apply(ByteBufCodecs.list(100)).map(WritableBookContent::new, WritableBookContent::pages);

   public WritableBookContent {
      if (pages.size() > 100) {
         throw new IllegalArgumentException("Got " + pages.size() + " pages, but maximum is 100");
      }
   }

   public Stream getPages(final boolean filterEnabled) {
      return this.pages.stream().map((page) -> (String)page.get(filterEnabled));
   }

   public WritableBookContent withReplacedPages(final List newPages) {
      return new WritableBookContent(newPages);
   }
}
