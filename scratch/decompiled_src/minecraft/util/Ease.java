package net.minecraft.util;

public class Ease {
   public static float inBack(final float x) {
      float c1 = 1.70158F;
      float c3 = 2.70158F;
      return Mth.square(x) * (2.70158F * x - 1.70158F);
   }

   public static float inBounce(final float x) {
      return 1.0F - outBounce(1.0F - x);
   }

   public static float inCubic(final float x) {
      return Mth.cube(x);
   }

   public static float inElastic(final float x) {
      if (x == 0.0F) {
         return 0.0F;
      } else if (x == 1.0F) {
         return 1.0F;
      } else {
         float c4 = 2.0943952F;
         return (float)(-Math.pow(2.0D, 10.0D * (double)x - 10.0D) * Math.sin(((double)x * 10.0D - 10.75D) * (double)2.0943952F));
      }
   }

   public static float inExpo(final float x) {
      return x == 0.0F ? 0.0F : (float)Math.pow(2.0D, 10.0D * (double)x - 10.0D);
   }

   public static float inQuart(final float x) {
      return Mth.square(Mth.square(x));
   }

   public static float inQuint(final float x) {
      return Mth.square(Mth.square(x)) * x;
   }

   public static float inSine(final float x) {
      return 1.0F - Mth.cos((double)(x * ((float)Math.PI / 2F)));
   }

   public static float inOutBounce(final float x) {
      return x < 0.5F ? (1.0F - outBounce(1.0F - 2.0F * x)) / 2.0F : (1.0F + outBounce(2.0F * x - 1.0F)) / 2.0F;
   }

   public static float inOutCirc(final float x) {
      return x < 0.5F ? (float)((1.0D - Math.sqrt(1.0D - Math.pow(2.0D * (double)x, 2.0D))) / 2.0D) : (float)((Math.sqrt(1.0D - Math.pow(-2.0D * (double)x + 2.0D, 2.0D)) + 1.0D) / 2.0D);
   }

   public static float inOutCubic(final float x) {
      return x < 0.5F ? 4.0F * Mth.cube(x) : (float)(1.0D - Math.pow(-2.0D * (double)x + 2.0D, 3.0D) / 2.0D);
   }

   public static float inOutQuad(final float x) {
      return x < 0.5F ? 2.0F * Mth.square(x) : (float)(1.0D - Math.pow(-2.0D * (double)x + 2.0D, 2.0D) / 2.0D);
   }

   public static float inOutQuart(final float x) {
      return x < 0.5F ? 8.0F * Mth.square(Mth.square(x)) : (float)(1.0D - Math.pow(-2.0D * (double)x + 2.0D, 4.0D) / 2.0D);
   }

   public static float inOutQuint(final float x) {
      return (double)x < 0.5D ? 16.0F * x * x * x * x * x : (float)(1.0D - Math.pow(-2.0D * (double)x + 2.0D, 5.0D) / 2.0D);
   }

   public static float outBounce(final float x) {
      float n1 = 7.5625F;
      float d1 = 2.75F;
      if (x < 0.36363637F) {
         return 7.5625F * Mth.square(x);
      } else if (x < 0.72727275F) {
         return 7.5625F * Mth.square(x - 0.54545456F) + 0.75F;
      } else {
         return (double)x < 0.9090909090909091D ? 7.5625F * Mth.square(x - 0.8181818F) + 0.9375F : 7.5625F * Mth.square(x - 0.95454544F) + 0.984375F;
      }
   }

   public static float outElastic(final float x) {
      float c4 = 2.0943952F;
      if (x == 0.0F) {
         return 0.0F;
      } else {
         return x == 1.0F ? 1.0F : (float)(Math.pow(2.0D, -10.0D * (double)x) * Math.sin(((double)x * 10.0D - 0.75D) * (double)2.0943952F) + 1.0D);
      }
   }

   public static float outExpo(final float x) {
      return x == 1.0F ? 1.0F : 1.0F - (float)Math.pow(2.0D, -10.0D * (double)x);
   }

   public static float outQuad(final float x) {
      return 1.0F - Mth.square(1.0F - x);
   }

   public static float outQuint(final float x) {
      return 1.0F - (float)Math.pow(1.0D - (double)x, 5.0D);
   }

   public static float outSine(final float x) {
      return Mth.sin((double)(x * ((float)Math.PI / 2F)));
   }

   public static float inOutSine(final float x) {
      return -(Mth.cos((double)((float)Math.PI * x)) - 1.0F) / 2.0F;
   }

   public static float outBack(final float x) {
      float c1 = 1.70158F;
      float c3 = 2.70158F;
      return 1.0F + 2.70158F * Mth.cube(x - 1.0F) + 1.70158F * Mth.square(x - 1.0F);
   }

   public static float outQuart(final float x) {
      return 1.0F - Mth.square(Mth.square(1.0F - x));
   }

   public static float outCubic(final float x) {
      return 1.0F - Mth.cube(1.0F - x);
   }

   public static float inOutExpo(final float x) {
      if (x < 0.5F) {
         return x == 0.0F ? 0.0F : (float)(Math.pow(2.0D, 20.0D * (double)x - 10.0D) / 2.0D);
      } else {
         return x == 1.0F ? 1.0F : (float)((2.0D - Math.pow(2.0D, -20.0D * (double)x + 10.0D)) / 2.0D);
      }
   }

   public static float inQuad(final float x) {
      return x * x;
   }

   public static float outCirc(final float x) {
      return (float)Math.sqrt((double)(1.0F - Mth.square(x - 1.0F)));
   }

   public static float inOutElastic(final float x) {
      float c5 = 1.3962635F;
      if (x == 0.0F) {
         return 0.0F;
      } else if (x == 1.0F) {
         return 1.0F;
      } else {
         double sin = Math.sin((20.0D * (double)x - 11.125D) * (double)1.3962635F);
         return x < 0.5F ? (float)(-(Math.pow(2.0D, 20.0D * (double)x - 10.0D) * sin) / 2.0D) : (float)(Math.pow(2.0D, -20.0D * (double)x + 10.0D) * sin / 2.0D + 1.0D);
      }
   }

   public static float inCirc(final float x) {
      return (float)(-Math.sqrt((double)(1.0F - x * x))) + 1.0F;
   }

   public static float inOutBack(final float x) {
      float c1 = 1.70158F;
      float c2 = 2.5949094F;
      if (x < 0.5F) {
         return 4.0F * x * x * (7.189819F * x - 2.5949094F) / 2.0F;
      } else {
         float dt = 2.0F * x - 2.0F;
         return (dt * dt * (3.5949094F * dt + 2.5949094F) + 2.0F) / 2.0F;
      }
   }
}
