package net.minecraft.world.level.levelgen.densityfunction.op;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.BoundedFloatFunction;
import net.minecraft.util.CubicSpline;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import org.apache.commons.lang3.mutable.MutableInt;

public final class SplineFunction implements DensityFunction {
   private static final Codec SPLINE_CODEC = CubicSpline.codec(SplineFunction.Coordinate.CODEC);
   public static final MapCodec CODEC = SPLINE_CODEC.fieldOf("spline").xmap(SplineFunction::new, SplineFunction::spline);
   private final CubicSpline spline;
   private final BoundedFloatFunction sampler;

   public SplineFunction(final CubicSpline spline) {
      this.spline = spline;
      this.sampler = CubicSpline.asSampler(spline);
   }

   public float compute(final DensityFunction.FunctionContext context) {
      return this.sampler.apply(new SplineFunction.Point(context));
   }

   public Interval range() {
      return this.spline.range();
   }

   public @DensityFunction.Axes int domainAxes() {
      MutableInt axes = new MutableInt(0);
      this.spline.forEachCoordinate((coordinate) -> axes.setValue(axes.intValue() | coordinate.function().domainAxes()));
      return axes.intValue();
   }

   public void fillArray(final float[] output, final DensityFunction.ContextProvider contextProvider) {
      contextProvider.fillAllDirectly(output, this);
   }

   public DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
      return new SplineFunction(this.spline.mapCoordinates((c) -> c.mapChildren(visitor)));
   }

   public MapCodec codec() {
      return CODEC;
   }

   public CubicSpline spline() {
      return this.spline;
   }

   public boolean equals(final Object obj) {
      if (obj == this) {
         return true;
      } else {
         if (obj instanceof SplineFunction) {
            SplineFunction splineFunction = (SplineFunction)obj;
            if (this.spline.equals(splineFunction.spline)) {
               return true;
            }
         }

         return false;
      }
   }

   public int hashCode() {
      return this.spline.hashCode();
   }

   public String toString() {
      return this.spline.toString();
   }

   public static record Coordinate(DensityFunction function) implements BoundedFloatFunction {
      public static final Codec CODEC = DensityFunction.CODEC.xmap(SplineFunction.Coordinate::new, SplineFunction.Coordinate::function);

      public float apply(final SplineFunction.Point point) {
         return this.function.compute(point.context());
      }

      public Interval range() {
         return this.function.range();
      }

      public SplineFunction.Coordinate mapChildren(final DensityFunction.Visitor visitor) {
         return new SplineFunction.Coordinate(visitor.apply(this.function));
      }
   }

   public static record Point(DensityFunction.FunctionContext context) {
   }
}
