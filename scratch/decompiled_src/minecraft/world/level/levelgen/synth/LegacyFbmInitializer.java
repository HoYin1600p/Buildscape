package net.minecraft.world.level.levelgen.synth;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Arrays;
import java.util.Objects;
import net.minecraft.util.RandomSource;

/** @deprecated */
@Deprecated
public class LegacyFbmInitializer {
   public static NoiseStack createForLegacyNetherBiome(final RandomSource random, final int firstOctave, final DoubleList amplitudes) {
      int octaves = amplitudes.size();
      int zeroOctaveIndex = -firstOctave;
      PerlinNoise[] noiseLevels = new PerlinNoise[octaves];
      PerlinNoise zeroOctave = new PerlinNoise(random);
      if (zeroOctaveIndex >= 0 && zeroOctaveIndex < octaves) {
         double zeroOctaveAmplitude = amplitudes.getDouble(zeroOctaveIndex);
         if (zeroOctaveAmplitude != 0.0D) {
            noiseLevels[zeroOctaveIndex] = zeroOctave;
         }
      }

      for(int i = zeroOctaveIndex - 1; i >= 0; --i) {
         if (i < octaves) {
            double amplitude = amplitudes.getDouble(i);
            if (amplitude != 0.0D) {
               noiseLevels[i] = new PerlinNoise(random);
            } else {
               skipOctave(random);
            }
         } else {
            skipOctave(random);
         }
      }

      if (Arrays.stream(noiseLevels).filter(Objects::nonNull).count() != amplitudes.stream().filter((a) -> a != 0.0D).count()) {
         throw new IllegalStateException("Failed to create correct number of noise levels for given non-zero amplitudes");
      } else if (zeroOctaveIndex < octaves - 1) {
         throw new IllegalArgumentException("Positive octaves are temporarily disabled");
      } else {
         double factor = Math.pow(2.0D, (double)(-zeroOctaveIndex));
         double valueFactor = Math.pow(2.0D, (double)(octaves - 1)) / (Math.pow(2.0D, (double)octaves) - 1.0D);
         NoiseStack.Builder stack = NoiseStack.builder();

         for(int i = 0; i < noiseLevels.length; ++i) {
            PerlinNoise noise = noiseLevels[i];
            if (noise != null) {
               stack.add(noise, factor, (float)(valueFactor * amplitudes.getDouble(i)));
            }

            factor *= 2.0D;
            valueFactor /= 2.0D;
         }

         return stack.build();
      }
   }

   private static void skipOctave(final RandomSource random) {
      random.consumeCount(262);
   }
}
