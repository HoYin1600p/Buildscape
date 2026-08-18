package net.minecraft.util.datafix;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.RewriteResult;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.View;
import com.mojang.datafixers.functions.PointFreeRule;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.BitSet;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;
import net.minecraft.util.Util;

public class ExtraDataFixUtils {
   public static Dynamic fixBlockPos(final Dynamic pos) {
      Optional x = pos.get("X").asNumber().result();
      Optional y = pos.get("Y").asNumber().result();
      Optional z = pos.get("Z").asNumber().result();
      return !x.isEmpty() && !y.isEmpty() && !z.isEmpty() ? createBlockPos(pos, ((Number)x.get()).intValue(), ((Number)y.get()).intValue(), ((Number)z.get()).intValue()) : pos;
   }

   public static Dynamic fixInlineBlockPos(final Dynamic input, final String fieldX, final String fieldY, final String fieldZ, final String newField) {
      Optional x = input.get(fieldX).asNumber().result();
      Optional y = input.get(fieldY).asNumber().result();
      Optional z = input.get(fieldZ).asNumber().result();
      return !x.isEmpty() && !y.isEmpty() && !z.isEmpty() ? input.remove(fieldX).remove(fieldY).remove(fieldZ).set(newField, createBlockPos(input, ((Number)x.get()).intValue(), ((Number)y.get()).intValue(), ((Number)z.get()).intValue())) : input;
   }

   public static Dynamic createBlockPos(final Dynamic dynamic, final int x, final int y, final int z) {
      return dynamic.createIntList(IntStream.of(new int[]{x, y, z}));
   }

   public static Typed cast(final Type type, final Typed typed) {
      return new Typed(type, typed.getOps(), typed.getValue());
   }

   public static Typed cast(final Type type, final Object value, final DynamicOps ops) {
      return new Typed(type, ops, value);
   }

   public static Type patchSubType(final Type type, final Type find, final Type replace) {
      return type.all(typePatcher(find, replace), true, false).view().newType();
   }

   private static TypeRewriteRule typePatcher(final Type inputEntityType, final Type outputEntityType) {
      RewriteResult view = RewriteResult.create(View.create("Patcher", inputEntityType, outputEntityType, (ops) -> (a) -> {
            throw new UnsupportedOperationException();
         }), new BitSet());
      return TypeRewriteRule.everywhere(TypeRewriteRule.ifSame(inputEntityType, view), PointFreeRule.nop(), true, true);
   }

   @SafeVarargs
   public static Function chainAllFilters(final Function... fixers) {
      return (typed) -> {
         for(Function fixer : fixers) {
            typed = (Typed)fixer.apply(typed);
         }

         return typed;
      };
   }

   public static Dynamic fixStringField(final Dynamic dynamic, final String fieldName, final UnaryOperator fix) {
      return dynamic.update(fieldName, (field) -> (Dynamic)DataFixUtils.orElse(field.asString().map(fix).map(dynamic::createString).result(), field));
   }

   public static Optional getPlainTranslationKey(final Dynamic textComponent) {
      return textComponent.getMapValues().result().filter((fields) -> fields.size() == 1).flatMap((fields) -> textComponent.get("translate").asString().result());
   }

   public static String dyeColorIdToName(final int id) {
      String var10000;
      switch (id) {
         case 1:
            var10000 = "orange";
            break;
         case 2:
            var10000 = "magenta";
            break;
         case 3:
            var10000 = "light_blue";
            break;
         case 4:
            var10000 = "yellow";
            break;
         case 5:
            var10000 = "lime";
            break;
         case 6:
            var10000 = "pink";
            break;
         case 7:
            var10000 = "gray";
            break;
         case 8:
            var10000 = "light_gray";
            break;
         case 9:
            var10000 = "cyan";
            break;
         case 10:
            var10000 = "purple";
            break;
         case 11:
            var10000 = "blue";
            break;
         case 12:
            var10000 = "brown";
            break;
         case 13:
            var10000 = "green";
            break;
         case 14:
            var10000 = "red";
            break;
         case 15:
            var10000 = "black";
            break;
         default:
            var10000 = "white";
      }

      return var10000;
   }

   public static Typed readAndSet(final Typed target, final OpticFinder optic, final Dynamic value) {
      return target.set(optic, Util.readTypedOrThrow(optic.type(), value, true));
   }
}
