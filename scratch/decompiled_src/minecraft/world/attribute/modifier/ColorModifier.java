package net.minecraft.world.attribute.modifier;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.LerpFunction;

public interface ColorModifier extends AttributeModifier {
   ColorModifier ALPHA_BLEND = new ColorModifier() {
      public Integer apply(final Integer subject, final Integer argument) {
         return ARGB.alphaBlend(subject, argument);
      }

      public Codec argumentCodec(final EnvironmentAttribute type) {
         return ExtraCodecs.STRING_ARGB_COLOR;
      }

      public LerpFunction argumentKeyframeLerp(final EnvironmentAttribute type) {
         return LerpFunction.ofColor();
      }
   };
   ColorModifier ADD = ARGB::addRgb;
   ColorModifier SUBTRACT = ARGB::subtractRgb;
   ColorModifier MULTIPLY_RGB = ARGB::multiply;
   ColorModifier MULTIPLY_ARGB = ARGB::multiply;
   ColorModifier BLEND_TO_GRAY = new ColorModifier() {
      public Integer apply(final Integer subject, final ColorModifier.BlendToGray argument) {
         int multipliedGreyscale = ARGB.scaleRGB(ARGB.greyscale(subject), argument.brightness);
         return ARGB.srgbLerp(argument.factor, subject, multipliedGreyscale);
      }

      public Codec argumentCodec(final EnvironmentAttribute type) {
         return ColorModifier.BlendToGray.CODEC;
      }

      public LerpFunction argumentKeyframeLerp(final EnvironmentAttribute type) {
         return (alpha, from, to) -> new ColorModifier.BlendToGray(Mth.lerp(alpha, from.brightness, to.brightness), Mth.lerp(alpha, from.factor, to.factor));
      }
   };

   @FunctionalInterface
   public interface ArgbModifier extends ColorModifier {
      default Codec argumentCodec(final EnvironmentAttribute type) {
         return Codec.either(ExtraCodecs.STRING_ARGB_COLOR, ExtraCodecs.RGB_COLOR_CODEC).xmap(Either::unwrap, (color) -> ARGB.alpha(color) == 255 ? Either.right(color) : Either.left(color));
      }

      default LerpFunction argumentKeyframeLerp(final EnvironmentAttribute type) {
         return LerpFunction.ofColor();
      }
   }

   public static record BlendToGray(float brightness, float factor) {
      public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(Codec.floatRange(0.0F, 1.0F).fieldOf("brightness").forGetter(ColorModifier.BlendToGray::brightness), Codec.floatRange(0.0F, 1.0F).fieldOf("factor").forGetter(ColorModifier.BlendToGray::factor)).apply(i, ColorModifier.BlendToGray::new));
   }

   @FunctionalInterface
   public interface RgbModifier extends ColorModifier {
      default Codec argumentCodec(final EnvironmentAttribute type) {
         return ExtraCodecs.STRING_RGB_COLOR;
      }

      default LerpFunction argumentKeyframeLerp(final EnvironmentAttribute type) {
         return LerpFunction.ofColor();
      }
   }
}
