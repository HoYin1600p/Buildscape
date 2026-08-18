package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Streams;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.util.datafix.LegacyComponentDataFixUtils;

public class BlockEntitySignDoubleSidedEditableTextFix extends NamedEntityWriteReadFix {
   public static final List FIELDS_TO_DROP = List.of("Text1", "Text2", "Text3", "Text4", "FilteredText1", "FilteredText2", "FilteredText3", "FilteredText4", "Color", "GlowingText");
   public static final String FILTERED_CORRECT = "_filtered_correct";
   private static final String DEFAULT_COLOR = "black";

   public BlockEntitySignDoubleSidedEditableTextFix(final Schema outputSchema, final String name, final String entityName) {
      super(outputSchema, true, name, References.BLOCK_ENTITY, entityName);
   }

   protected Dynamic fix(Dynamic input) {
      input = input.set("front_text", fixFrontTextTag(input)).set("back_text", createDefaultText(input)).set("is_waxed", input.createBoolean(false)).set("_filtered_correct", input.createBoolean(true));

      for(String field : FIELDS_TO_DROP) {
         input = input.remove(field);
      }

      return input;
   }

   private static Dynamic fixFrontTextTag(final Dynamic tag) {
      Dynamic emptyLine = LegacyComponentDataFixUtils.createEmptyComponent(tag.getOps());
      List lines = getLines(tag, "Text").map((line) -> (Dynamic)line.orElse(emptyLine)).toList();
      Dynamic text = tag.emptyMap().set("messages", tag.createList(lines.stream())).set("color", (Dynamic)tag.get("Color").result().orElse(tag.createString("black"))).set("has_glowing_text", (Dynamic)tag.get("GlowingText").result().orElse(tag.createBoolean(false)));
      List filteredLines = getLines(tag, "FilteredText").toList();
      if (filteredLines.stream().anyMatch(Optional::isPresent)) {
         text = text.set("filtered_messages", tag.createList(Streams.mapWithIndex(filteredLines.stream(), (line, index) -> {
            Dynamic fallbackLine = (Dynamic)lines.get((int)index);
            return (Dynamic)line.orElse(fallbackLine);
         })));
      }

      return text;
   }

   private static Stream getLines(final Dynamic tag, final String linePrefix) {
      return Stream.of(tag.get(linePrefix + "1").result(), tag.get(linePrefix + "2").result(), tag.get(linePrefix + "3").result(), tag.get(linePrefix + "4").result());
   }

   private static Dynamic createDefaultText(final Dynamic tag) {
      return tag.emptyMap().set("messages", createEmptyLines(tag)).set("color", tag.createString("black")).set("has_glowing_text", tag.createBoolean(false));
   }

   private static Dynamic createEmptyLines(final Dynamic tag) {
      Dynamic emptyComponent = LegacyComponentDataFixUtils.createEmptyComponent(tag.getOps());
      return tag.createList(Stream.of(emptyComponent, emptyComponent, emptyComponent, emptyComponent));
   }
}
