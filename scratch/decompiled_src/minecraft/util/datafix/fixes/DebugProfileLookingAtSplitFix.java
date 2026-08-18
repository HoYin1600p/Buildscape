package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DebugProfileLookingAtSplitFix extends DataFix {
   public DebugProfileLookingAtSplitFix(final Schema outputSchema) {
      super(outputSchema, false);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("DebugProfileLookingAtSplitFix", this.getInputSchema().getType(References.DEBUG_PROFILE), (input) -> input.update(DSL.remainderFinder(), (tag) -> tag.update("custom", DebugProfileLookingAtSplitFix::updateOptions)));
   }

   private static Dynamic updateOptions(final Dynamic custom) {
      return (Dynamic)DataFixUtils.orElse(custom.getMapValues().map((map) -> {
         Map newOptions = new HashMap();
         map.forEach((key, value) -> renamedKey(key).ifPresentOrElse((newKey) -> newOptions.putIfAbsent(newKey, value), () -> newOptions.put(key, value)));
         return custom.createMap(newOptions);
      }).result(), custom);
   }

   private static Optional renamedKey(final Dynamic keyDynamic) {
      return keyDynamic.asString().result().flatMap((key) -> {
         Optional var10000;
         switch (key) {
            case "minecraft:looking_at_block":
               var10000 = Optional.of("minecraft:looking_at_block_state");
               break;
            case "minecraft:looking_at_fluid":
               var10000 = Optional.of("minecraft:looking_at_fluid_state");
               break;
            default:
               var10000 = Optional.empty();
         }

         return var10000;
      }).map(keyDynamic::createString);
   }
}
