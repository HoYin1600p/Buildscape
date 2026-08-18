package net.minecraft.core.particles;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;

public class ColorParticleOption implements ParticleOptions {
   private final ParticleType type;
   private final int color;

   public static MapCodec codec(final ParticleType type) {
      return ExtraCodecs.ARGB_COLOR_CODEC.xmap((color) -> new ColorParticleOption(type, color), (o) -> o.color).fieldOf("color");
   }

   public static StreamCodec streamCodec(final ParticleType type) {
      return ByteBufCodecs.INT.map((color) -> new ColorParticleOption(type, color), (o) -> o.color);
   }

   private ColorParticleOption(final ParticleType type, final int color) {
      this.type = type;
      this.color = color;
   }

   public ParticleType getType() {
      return this.type;
   }

   public float getRed() {
      return (float)ARGB.red(this.color) / 255.0F;
   }

   public float getGreen() {
      return (float)ARGB.green(this.color) / 255.0F;
   }

   public float getBlue() {
      return (float)ARGB.blue(this.color) / 255.0F;
   }

   public float getAlpha() {
      return (float)ARGB.alpha(this.color) / 255.0F;
   }

   public static ColorParticleOption create(final ParticleType type, final int color) {
      return new ColorParticleOption(type, color);
   }

   public static ColorParticleOption create(final ParticleType type, final float red, final float green, final float blue) {
      return create(type, ARGB.colorFromFloat(1.0F, red, green, blue));
   }
}
