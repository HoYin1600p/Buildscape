package net.minecraft.world.attribute;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class GaussianSampler {
   private static final int GAUSSIAN_SAMPLE_RADIUS = 2;
   private static final int GAUSSIAN_SAMPLE_BREADTH = 6;
   private static final double[] GAUSSIAN_SAMPLE_KERNEL = new double[]{0.0D, 1.0D, 4.0D, 6.0D, 4.0D, 1.0D, 0.0D};

   public static void sample(Vec3 position, final GaussianSampler.Sampler sampler, final GaussianSampler.Accumulator accumulator) {
      position = position.subtract(0.5D, 0.5D, 0.5D);
      int integralX = Mth.floor(position.x());
      int integralY = Mth.floor(position.y());
      int integralZ = Mth.floor(position.z());
      double relativeX = position.x() - (double)integralX;
      double relativeY = position.y() - (double)integralY;
      double relativeZ = position.z() - (double)integralZ;

      for(int z = 0; z < 6; ++z) {
         double weightZ = Mth.lerp(relativeZ, GAUSSIAN_SAMPLE_KERNEL[z + 1], GAUSSIAN_SAMPLE_KERNEL[z]);
         int sampleZ = integralZ - 2 + z;

         for(int x = 0; x < 6; ++x) {
            double weightX = Mth.lerp(relativeX, GAUSSIAN_SAMPLE_KERNEL[x + 1], GAUSSIAN_SAMPLE_KERNEL[x]);
            int sampleX = integralX - 2 + x;

            for(int y = 0; y < 6; ++y) {
               double weightY = Mth.lerp(relativeY, GAUSSIAN_SAMPLE_KERNEL[y + 1], GAUSSIAN_SAMPLE_KERNEL[y]);
               int sampleY = integralY - 2 + y;
               double sampleWeight = weightX * weightY * weightZ;
               Object value = (V)sampler.get(sampleX, sampleY, sampleZ);
               accumulator.accumulate(sampleWeight, value);
            }
         }
      }

   }

   @FunctionalInterface
   public interface Accumulator {
      void accumulate(double weight, Object value);
   }

   @FunctionalInterface
   public interface Sampler {
      Object get(int x, int y, int z);
   }
}
