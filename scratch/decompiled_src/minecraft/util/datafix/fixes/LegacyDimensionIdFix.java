package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;

public class LegacyDimensionIdFix extends DataFix {
   public LegacyDimensionIdFix(final Schema outputSchema) {
      super(outputSchema, false);
   }

   public TypeRewriteRule makeRule() {
      TypeRewriteRule playerRule = this.fixTypeEverywhereTyped("PlayerLegacyDimensionFix", this.getInputSchema().getType(References.PLAYER), (input) -> input.update(DSL.remainderFinder(), this::fixPlayer));
      Type dataType = this.getInputSchema().getType(References.SAVED_DATA_MAP_DATA);
      OpticFinder mapDataF = dataType.findField("data");
      TypeRewriteRule mapRule = this.fixTypeEverywhereTyped("MapLegacyDimensionFix", dataType, (input) -> input.updateTyped(mapDataF, (data) -> data.update(DSL.remainderFinder(), this::fixMap)));
      return TypeRewriteRule.seq(playerRule, mapRule);
   }

   private Dynamic fixMap(final Dynamic remainder) {
      return remainder.update("dimension", this::fixDimensionId);
   }

   private Dynamic fixPlayer(final Dynamic remainder) {
      return remainder.update("Dimension", this::fixDimensionId);
   }

   private Dynamic fixDimensionId(final Dynamic id) {
      return (Dynamic)DataFixUtils.orElse(id.asNumber().result().map((legacyId) -> {
         Dynamic var10000;
         switch (legacyId.intValue()) {
            case -1:
               var10000 = id.createString("minecraft:the_nether");
               break;
            case 1:
               var10000 = id.createString("minecraft:the_end");
               break;
            default:
               var10000 = id.createString("minecraft:overworld");
         }

         return var10000;
      }), id);
   }
}
