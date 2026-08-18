package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.Const;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;
import net.minecraft.resources.Identifier;

public class NamespacedSchema extends Schema {
   public static final PrimitiveCodec NAMESPACED_STRING_CODEC = new PrimitiveCodec() {
      public DataResult read(final DynamicOps ops, final Object input) {
         return ops.getStringValue(input).map(NamespacedSchema::ensureNamespaced);
      }

      public Object write(final DynamicOps ops, final String value) {
         return ops.createString(value);
      }

      public String toString() {
         return "NamespacedString";
      }
   };
   private static final Type NAMESPACED_STRING = new Const.PrimitiveType(NAMESPACED_STRING_CODEC);

   public NamespacedSchema(final int versionKey, final Schema parent) {
      super(versionKey, parent);
   }

   public static String ensureNamespaced(final String input) {
      Identifier identifier = Identifier.tryParse(input);
      return identifier != null ? identifier.toString() : input;
   }

   public static Type namespacedString() {
      return NAMESPACED_STRING;
   }

   public Type getChoiceType(final DSL.TypeReference type, final String choiceName) {
      return super.getChoiceType(type, ensureNamespaced(choiceName));
   }
}
