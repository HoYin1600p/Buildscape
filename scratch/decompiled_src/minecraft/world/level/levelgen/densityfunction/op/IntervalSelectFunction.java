package net.minecraft.world.level.levelgen.densityfunction.op;

import com.google.common.collect.Comparators;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunctions;

public record IntervalSelectFunction(DensityFunction input, FloatList thresholds, List functions) implements DensityFunction {
   private static final Codec THRESHOLDS_CODEC = DensityFunctions.NOISE_VALUE_CODEC.listOf().xmap(FloatArrayList::new, Function.identity());
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunction.CODEC.fieldOf("input").forGetter(IntervalSelectFunction::input), THRESHOLDS_CODEC.fieldOf("thresholds").forGetter(IntervalSelectFunction::thresholds), DensityFunction.CODEC.listOf(2, Integer.MAX_VALUE).fieldOf("functions").forGetter(IntervalSelectFunction::functions)).apply(i, IntervalSelectFunction::new)).validate(IntervalSelectFunction::validate);

   private DataResult validate() {
      if (this.thresholds.size() != this.functions.size() - 1) {
         return DataResult.error(() -> "Expected " + (this.functions.size() - 1) + " thresholds for " + this.functions.size() + " functions, but got " + this.thresholds.size());
      } else {
         return !Comparators.isInOrder(this.thresholds, Float::compare) ? DataResult.error(() -> "Threshold values must be ordered from smallest to largest") : DataResult.success(this);
      }
   }

   private float compute(final DensityFunction.FunctionContext context, final float input) {
      for(int i = 0; i < this.thresholds.size(); ++i) {
         if (input < this.thresholds.getFloat(i)) {
            return ((DensityFunction)this.functions.get(i)).compute(context);
         }
      }

      return ((DensityFunction)this.functions.getLast()).compute(context);
   }

   public float compute(final DensityFunction.FunctionContext context) {
      return this.compute(context, this.input.compute(context));
   }

   public void fillArray(final float[] output, final DensityFunction.ContextProvider contextProvider) {
      this.input.fillArray(output, contextProvider);

      for(int i = 0; i < output.length; ++i) {
         output[i] = this.compute(contextProvider.forIndex(i), output[i]);
      }

   }

   public DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
      return new IntervalSelectFunction(visitor.apply(this.input), this.thresholds, List.copyOf(Lists.transform(this.functions, visitor::apply)));
   }

   public Interval range() {
      return Interval.encapsulating(Lists.transform(this.functions, DensityFunction::range));
   }

   public @DensityFunction.Axes int domainAxes() {
      int axes = this.input.domainAxes();

      for(DensityFunction function : this.functions) {
         axes |= function.domainAxes();
      }

      return axes;
   }

   public MapCodec codec() {
      return CODEC;
   }
}
