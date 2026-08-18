package net.minecraft.world.level.levelgen;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;

public record SpawnTargetPoint(Map parameters) {
   public static final Codec CODEC = Codec.unboundedMap(DensityFunction.REFERENCE_CODEC, Climate.Parameter.CODEC).xmap(SpawnTargetPoint::new, SpawnTargetPoint::parameters);

   public SpawnTargetPoint.Wired wire(final DensityFunction.Visitor noiseWirer, final DensityFunction.Visitor flattener) {
      return new SpawnTargetPoint.Wired(this.parameters.entrySet().stream().map((entry) -> {
         DensityFunction wiredFunction = ((DensityFunction)((Holder)entry.getKey()).value()).mapAll(noiseWirer);
         DensityFunction flattenedFunction = wiredFunction.mapAll(flattener);
         return Pair.of(flattenedFunction, (Climate.Parameter)entry.getValue());
      }).toList());
   }

   public static record Wired(List parameters) {
      public long sampleFitness(final DensityFunction.SinglePointContext context) {
         long fitness = 0L;

         for(Pair parameter : this.parameters) {
            DensityFunction function = (DensityFunction)parameter.getFirst();
            long value = Climate.quantizeCoord(function.compute(context));
            fitness += Mth.square(((Climate.Parameter)parameter.getSecond()).distance(value));
         }

         return fitness;
      }
   }
}
