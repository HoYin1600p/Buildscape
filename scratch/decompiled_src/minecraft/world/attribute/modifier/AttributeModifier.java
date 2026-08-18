package net.minecraft.world.attribute.modifier;

import com.mojang.serialization.Codec;
import java.util.Map;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.LerpFunction;

public interface AttributeModifier {
   Map BOOLEAN_LIBRARY = Map.of(AttributeModifier.OperationId.AND, BooleanModifier.AND, AttributeModifier.OperationId.NAND, BooleanModifier.NAND, AttributeModifier.OperationId.OR, BooleanModifier.OR, AttributeModifier.OperationId.NOR, BooleanModifier.NOR, AttributeModifier.OperationId.XOR, BooleanModifier.XOR, AttributeModifier.OperationId.XNOR, BooleanModifier.XNOR);
   Map FLOAT_LIBRARY = Map.of(AttributeModifier.OperationId.ALPHA_BLEND, FloatModifier.ALPHA_BLEND, AttributeModifier.OperationId.ADD, FloatModifier.ADD, AttributeModifier.OperationId.SUBTRACT, FloatModifier.SUBTRACT, AttributeModifier.OperationId.MULTIPLY, FloatModifier.MULTIPLY, AttributeModifier.OperationId.MINIMUM, FloatModifier.MINIMUM, AttributeModifier.OperationId.MAXIMUM, FloatModifier.MAXIMUM);
   Map RGB_COLOR_LIBRARY = Map.of(AttributeModifier.OperationId.ALPHA_BLEND, ColorModifier.ALPHA_BLEND, AttributeModifier.OperationId.ADD, ColorModifier.ADD, AttributeModifier.OperationId.SUBTRACT, ColorModifier.SUBTRACT, AttributeModifier.OperationId.MULTIPLY, ColorModifier.MULTIPLY_RGB, AttributeModifier.OperationId.BLEND_TO_GRAY, ColorModifier.BLEND_TO_GRAY);
   Map ARGB_COLOR_LIBRARY = Map.of(AttributeModifier.OperationId.ALPHA_BLEND, ColorModifier.ALPHA_BLEND, AttributeModifier.OperationId.ADD, ColorModifier.ADD, AttributeModifier.OperationId.SUBTRACT, ColorModifier.SUBTRACT, AttributeModifier.OperationId.MULTIPLY, ColorModifier.MULTIPLY_ARGB, AttributeModifier.OperationId.BLEND_TO_GRAY, ColorModifier.BLEND_TO_GRAY);
   Map INTEGER_LIBRARY = Map.of(AttributeModifier.OperationId.ADD, IntegerModifier.ADD, AttributeModifier.OperationId.SUBTRACT, IntegerModifier.SUBTRACT, AttributeModifier.OperationId.MULTIPLY, IntegerModifier.MULTIPLY, AttributeModifier.OperationId.MINIMUM, IntegerModifier.MINIMUM, AttributeModifier.OperationId.MAXIMUM, IntegerModifier.MAXIMUM);
   Map MOB_SPAWN_SETTINGS_LIBRARY = Map.of(AttributeModifier.OperationId.OVERLAY, MobSpawnSettingsModifier.overlay());

   static AttributeModifier override() {
      return AttributeModifier.OverrideModifier.INSTANCE;
   }

   static Map listLibrary() {
      return Map.of(AttributeModifier.OperationId.APPEND, ListModifier.append());
   }

   Object apply(Object subject, Object argument);

   Codec argumentCodec(EnvironmentAttribute attribute);

   LerpFunction argumentKeyframeLerp(EnvironmentAttribute attribute);

   public static enum OperationId implements StringRepresentable {
      OVERRIDE("override"),
      ALPHA_BLEND("alpha_blend"),
      ADD("add"),
      SUBTRACT("subtract"),
      MULTIPLY("multiply"),
      BLEND_TO_GRAY("blend_to_gray"),
      MINIMUM("minimum"),
      MAXIMUM("maximum"),
      AND("and"),
      NAND("nand"),
      OR("or"),
      NOR("nor"),
      XOR("xor"),
      XNOR("xnor"),
      APPEND("append"),
      OVERLAY("overlay");

      public static final Codec CODEC = StringRepresentable.fromEnum(AttributeModifier.OperationId::values);
      private final String name;

      private OperationId(final String name) {
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static AttributeModifier.OperationId[] $values() {
         return new AttributeModifier.OperationId[]{OVERRIDE, ALPHA_BLEND, ADD, SUBTRACT, MULTIPLY, BLEND_TO_GRAY, MINIMUM, MAXIMUM, AND, NAND, OR, NOR, XOR, XNOR, APPEND, OVERLAY};
      }
   }

   public static record OverrideModifier() implements AttributeModifier {
      private static final AttributeModifier.OverrideModifier INSTANCE = new AttributeModifier.OverrideModifier();

      public Object apply(final Object subject, final Object argument) {
         return argument;
      }

      public Codec argumentCodec(final EnvironmentAttribute attribute) {
         return attribute.valueCodec();
      }

      public LerpFunction argumentKeyframeLerp(final EnvironmentAttribute attribute) {
         return attribute.type().keyframeLerp();
      }
   }
}
