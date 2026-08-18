package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class ReorganizePoi extends DataFix {
   public ReorganizePoi(final Schema outputSchema, final boolean changesType) {
      super(outputSchema, changesType);
   }

   protected TypeRewriteRule makeRule() {
      Type poiChunkType = DSL.named(References.POI_CHUNK.typeName(), DSL.remainderType());
      if (!Objects.equals(poiChunkType, this.getInputSchema().getType(References.POI_CHUNK))) {
         throw new IllegalStateException("Poi type is not what was expected.");
      } else {
         return this.fixTypeEverywhere("POI reorganization", poiChunkType, (ops) -> (input) -> input.mapSecond(ReorganizePoi::cap));
      }
   }

   private static Dynamic cap(Dynamic input) {
      Map sections = Maps.newHashMap();

      for(int i = 0; i < 16; ++i) {
         String key = String.valueOf(i);
         Optional section = input.get(key).result();
         if (section.isPresent()) {
            Dynamic sectionRecords = (Dynamic)section.get();
            Dynamic newSection = input.createMap(ImmutableMap.of(input.createString("Records"), sectionRecords));
            sections.put(input.createString(Integer.toString(i)), newSection);
            input = input.remove(key);
         }
      }

      return input.set("Sections", input.createMap(sections));
   }
}
