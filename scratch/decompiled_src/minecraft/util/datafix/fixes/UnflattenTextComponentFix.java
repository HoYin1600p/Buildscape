package net.minecraft.util.datafix.fixes;

import com.google.gson.JsonElement;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.util.LenientJsonParser;
import net.minecraft.util.Util;
import org.slf4j.Logger;

public class UnflattenTextComponentFix extends DataFix {
   private static final Logger LOGGER = LogUtils.getLogger();

   public UnflattenTextComponentFix(final Schema outputSchema) {
      super(outputSchema, true);
   }

   protected TypeRewriteRule makeRule() {
      Type textComponentType = this.getInputSchema().getType(References.TEXT_COMPONENT);
      Type newTextComponentType = this.getOutputSchema().getType(References.TEXT_COMPONENT);
      return this.createFixer(textComponentType, newTextComponentType);
   }

   private TypeRewriteRule createFixer(final Type textComponentType, final Type newTextComponentType) {
      return this.fixTypeEverywhere("UnflattenTextComponentFix", textComponentType, newTextComponentType, (ops) -> (input) -> Util.readTypedOrThrow(newTextComponentType, unflattenJson(ops, (String)input.getSecond()), true).getValue());
   }

   private static Dynamic unflattenJson(final DynamicOps ops, final String jsonString) {
      try {
         JsonElement json = LenientJsonParser.parse(jsonString);
         if (!json.isJsonNull()) {
            return new Dynamic(ops, JsonOps.INSTANCE.convertTo(ops, json));
         }
      } catch (Exception var3) {
         LOGGER.error("Failed to unflatten text component json: {}", jsonString, var3);
      }

      return new Dynamic(ops, ops.createString(jsonString));
   }
}
