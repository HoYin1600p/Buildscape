package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import org.slf4j.Logger;

public interface ListOperation {
   MapCodec UNLIMITED_CODEC = codec(Integer.MAX_VALUE);

   static MapCodec codec(final int maxSize) {
      return ListOperation.Type.CODEC.dispatchMap("mode", ListOperation::mode, (e) -> e.mapCodec).validate((op) -> {
         if (op instanceof ListOperation.ReplaceSection section) {
            if (section.size().isPresent()) {
               int size = section.size().get();
               if (size > maxSize) {
                  return DataResult.error(() -> "Size value too large: " + size + ", max size is " + maxSize);
               }
            }
         }

         return DataResult.success(op);
      });
   }

   ListOperation.Type mode();

   default List apply(final List original, final List replacement) {
      return this.apply(original, replacement, Integer.MAX_VALUE);
   }

   List apply(List original, List replacement, int maxSize);

   public static class Append implements ListOperation {
      private static final Logger LOGGER = LogUtils.getLogger();
      public static final ListOperation.Append INSTANCE = new ListOperation.Append();
      public static final MapCodec MAP_CODEC = MapCodec.unit(() -> INSTANCE);

      private Append() {
      }

      public ListOperation.Type mode() {
         return ListOperation.Type.APPEND;
      }

      public List apply(final List original, final List replacement, final int maxSize) {
         if (original.size() + replacement.size() > maxSize) {
            LOGGER.error("Contents overflow in section append");
            return original;
         } else {
            return Stream.concat(original.stream(), replacement.stream()).toList();
         }
      }
   }

   public static record Insert(int offset) implements ListOperation {
      private static final Logger LOGGER = LogUtils.getLogger();
      public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("offset", 0).forGetter(ListOperation.Insert::offset)).apply(i, ListOperation.Insert::new));

      public ListOperation.Type mode() {
         return ListOperation.Type.INSERT;
      }

      public List apply(final List original, final List replacement, final int maxSize) {
         int originalSize = original.size();
         if (this.offset > originalSize) {
            LOGGER.error("Cannot insert when offset is out of bounds");
            return original;
         } else if (originalSize + replacement.size() > maxSize) {
            LOGGER.error("Contents overflow in section insertion");
            return original;
         } else {
            ImmutableList.Builder newList = ImmutableList.builder();
            newList.addAll(original.subList(0, this.offset));
            newList.addAll(replacement);
            newList.addAll(original.subList(this.offset, originalSize));
            return newList.build();
         }
      }
   }

   public static class ReplaceAll implements ListOperation {
      public static final ListOperation.ReplaceAll INSTANCE = new ListOperation.ReplaceAll();
      public static final MapCodec MAP_CODEC = MapCodec.unit(() -> INSTANCE);

      private ReplaceAll() {
      }

      public ListOperation.Type mode() {
         return ListOperation.Type.REPLACE_ALL;
      }

      public List apply(final List original, final List replacement, final int maxSize) {
         return replacement;
      }
   }

   public static record ReplaceSection(int offset, Optional size) implements ListOperation {
      private static final Logger LOGGER = LogUtils.getLogger();
      public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("offset", 0).forGetter(ListOperation.ReplaceSection::offset), ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("size").forGetter(ListOperation.ReplaceSection::size)).apply(i, ListOperation.ReplaceSection::new));

      public ReplaceSection(final int offset) {
         this(offset, Optional.empty());
      }

      public ListOperation.Type mode() {
         return ListOperation.Type.REPLACE_SECTION;
      }

      public List apply(final List original, final List replacement, final int maxSize) {
         int originalSize = original.size();
         if (this.offset > originalSize) {
            LOGGER.error("Cannot replace when offset is out of bounds");
            return original;
         } else {
            ImmutableList.Builder newList = ImmutableList.builder();
            newList.addAll(original.subList(0, this.offset));
            newList.addAll(replacement);
            int resumeIndex = this.offset + this.size.orElse(replacement.size());
            if (resumeIndex < originalSize) {
               newList.addAll(original.subList(resumeIndex, originalSize));
            }

            List result = newList.build();
            if (result.size() > maxSize) {
               LOGGER.error("Contents overflow in section replacement");
               return original;
            } else {
               return result;
            }
         }
      }
   }

   public static record StandAlone(List value, ListOperation operation) {
      public static Codec codec(final Codec valueCodec, final int maxSize) {
         return RecordCodecBuilder.create((i) -> i.group(valueCodec.sizeLimitedListOf(maxSize).fieldOf("values").forGetter((f) -> f.value), ListOperation.codec(maxSize).forGetter((f) -> f.operation)).apply(i, ListOperation.StandAlone::new));
      }

      public List apply(final List input) {
         return this.operation.apply(input, this.value);
      }
   }

   public static enum Type implements StringRepresentable {
      REPLACE_ALL("replace_all", ListOperation.ReplaceAll.MAP_CODEC),
      REPLACE_SECTION("replace_section", ListOperation.ReplaceSection.MAP_CODEC),
      INSERT("insert", ListOperation.Insert.MAP_CODEC),
      APPEND("append", ListOperation.Append.MAP_CODEC);

      public static final Codec CODEC = StringRepresentable.fromEnum(ListOperation.Type::values);
      private final String id;
      private final MapCodec mapCodec;

      private Type(final String id, final MapCodec mapCodec) {
         this.id = id;
         this.mapCodec = mapCodec;
      }

      public MapCodec mapCodec() {
         return this.mapCodec;
      }

      public String getSerializedName() {
         return this.id;
      }

      // $FF: synthetic method
      private static ListOperation.Type[] $values() {
         return new ListOperation.Type[]{REPLACE_ALL, REPLACE_SECTION, INSERT, APPEND};
      }
   }
}
