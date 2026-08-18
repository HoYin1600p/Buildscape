package net.minecraft.world.attribute;

import com.google.common.collect.ImmutableBiMap;
import com.mojang.serialization.Codec;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.ToFloatFunction;
import net.minecraft.util.Util;
import net.minecraft.world.attribute.modifier.AttributeModifier;
import org.jspecify.annotations.Nullable;

public record AttributeType(Codec valueCodec, Map modifierLibrary, Codec modifierCodec, LerpFunction keyframeLerp, LerpFunction stateChangeLerp, LerpFunction spatialLerp, LerpFunction partialTickLerp, @Nullable ToFloatFunction toFloat) {
   public static AttributeType ofInterpolated(final Codec valueCodec, final Map modifierLibrary, final LerpFunction lerp) {
      return ofInterpolated(valueCodec, modifierLibrary, lerp, lerp, (ToFloatFunction)null);
   }

   public static AttributeType ofInterpolated(final Codec valueCodec, final Map modifierLibrary, final LerpFunction lerp, final LerpFunction partialTickLerp, final @Nullable ToFloatFunction toFloat) {
      return new AttributeType(valueCodec, modifierLibrary, createModifierCodec(modifierLibrary), lerp, lerp, lerp, partialTickLerp, toFloat);
   }

   public static AttributeType ofNotInterpolated(final Codec valueCodec, final Map modifierLibrary) {
      return new AttributeType(valueCodec, modifierLibrary, createModifierCodec(modifierLibrary), LerpFunction.ofStep(1.0F), LerpFunction.ofStep(0.0F), LerpFunction.ofStep(0.5F), LerpFunction.ofStep(0.0F), (ToFloatFunction)null);
   }

   public static AttributeType ofNotInterpolated(final Codec valueCodec) {
      return ofNotInterpolated(valueCodec, Map.of());
   }

   private static Codec createModifierCodec(final Map modifiers) {
      ImmutableBiMap modifierLookup = ImmutableBiMap.builder().put(AttributeModifier.OperationId.OVERRIDE, AttributeModifier.override()).putAll(modifiers).buildOrThrow();
      return ExtraCodecs.idResolverCodec(AttributeModifier.OperationId.CODEC, modifierLookup::get, modifierLookup.inverse()::get);
   }

   public void checkAllowedModifier(final AttributeModifier modifier) {
      if (modifier != AttributeModifier.override() && !this.modifierLibrary.containsValue(modifier)) {
         throw new IllegalArgumentException("Modifier " + String.valueOf(modifier) + " is not valid for " + String.valueOf(this));
      }
   }

   public float toFloat(final Object value) {
      if (this.toFloat == null) {
         throw new IllegalStateException(String.valueOf(value) + " cannot be represented as a float");
      } else {
         return this.toFloat.applyAsFloat(value);
      }
   }

   public String toString() {
      return Util.getRegisteredName(BuiltInRegistries.ATTRIBUTE_TYPE, this);
   }
}
