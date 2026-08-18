package net.minecraft.util.datafix.fixes;

import com.google.gson.JsonElement;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.JsonOps;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Util;

public class LegacyHoverEventFix extends DataFix {
   public LegacyHoverEventFix(final Schema outputSchema) {
      super(outputSchema, false);
   }

   protected TypeRewriteRule makeRule() {
      Type hoverEventType = this.getInputSchema().getType(References.TEXT_COMPONENT).findFieldType("hoverEvent");
      return this.createFixer(this.getInputSchema().getTypeRaw(References.TEXT_COMPONENT), hoverEventType);
   }

   private TypeRewriteRule createFixer(final Type rawTextComponentType, final Type hoverEventType) {
      Type textComponentType = DSL.named(References.TEXT_COMPONENT.typeName(), DSL.or(DSL.or(DSL.string(), DSL.list(rawTextComponentType)), DSL.and(DSL.optional(DSL.field("extra", DSL.list(rawTextComponentType))), DSL.optional(DSL.field("separator", rawTextComponentType)), DSL.optional(DSL.field("hoverEvent", hoverEventType)), DSL.remainderType())));
      if (!textComponentType.equals(this.getInputSchema().getType(References.TEXT_COMPONENT))) {
         throw new IllegalStateException("Text component type did not match, expected " + String.valueOf(textComponentType) + " but got " + String.valueOf(this.getInputSchema().getType(References.TEXT_COMPONENT)));
      } else {
         return this.fixTypeEverywhere("LegacyHoverEventFix", textComponentType, (ops) -> (named) -> named.mapSecond((simpleOrFull) -> simpleOrFull.mapRight((full) -> full.mapSecond((separatorHoverRemainder) -> separatorHoverRemainder.mapSecond((hoverAndRemainder) -> {
                           Dynamic remainder = (Dynamic)hoverAndRemainder.getSecond();
                           Optional hoverEvent = remainder.get("hoverEvent").result();
                           if (hoverEvent.isEmpty()) {
                              return hoverAndRemainder;
                           } else {
                              Optional legacyHoverValue = ((Dynamic)hoverEvent.get()).get("value").result();
                              if (legacyHoverValue.isEmpty()) {
                                 return hoverAndRemainder;
                              } else {
                                 String hoverAction = (String)((Either)hoverAndRemainder.getFirst()).left().map(Pair::getFirst).orElse("");
                                 Pair newHoverEvent = (H)((Pair)this.fixHoverEvent(hoverEventType, hoverAction, (Dynamic)hoverEvent.get()));
                                 return hoverAndRemainder.mapFirst((ignored) -> Either.left(newHoverEvent));
                              }
                           }
                        })))));
      }
   }

   private Object fixHoverEvent(final Type hoverEventType, final String action, final Dynamic oldHoverEvent) {
      return "show_text".equals(action) ? fixShowTextHover(hoverEventType, oldHoverEvent) : createPlaceholderHover(hoverEventType, oldHoverEvent);
   }

   private static Object fixShowTextHover(final Type hoverEventType, final Dynamic oldHoverEvent) {
      Dynamic newHoverEvent = oldHoverEvent.renameField("value", "contents");
      return Util.readTypedOrThrow(hoverEventType, newHoverEvent).getValue();
   }

   private static Object createPlaceholderHover(final Type hoverEventType, final Dynamic oldHoverEvent) {
      JsonElement oldJson = (JsonElement)oldHoverEvent.convert(JsonOps.INSTANCE).getValue();
      Dynamic placeholderHoverEvent = new Dynamic(JavaOps.INSTANCE, Map.of("action", "show_text", "contents", Map.of("text", "Legacy hoverEvent: " + GsonHelper.toStableString(oldJson))));
      return Util.readTypedOrThrow(hoverEventType, placeholderHoverEvent).getValue();
   }
}
