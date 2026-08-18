package net.minecraft.util;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public sealed interface CubicSpline {
   void forEachCoordinate(Consumer consumer);

   CubicSpline mapCoordinates(Function mapper);

   Interval range();

   @VisibleForDebug
   String parityString();

   static float sample(final CubicSpline spline, final Object coordinate) {
      Objects.requireNonNull(spline);
      byte var3 = 0;
      float var10000;
      switch (spline.typeSwitch<invokedynamic>(spline, var3)) {
         case 0:
            CubicSpline.Multipoint multipoint = (CubicSpline.Multipoint)spline;
            var10000 = CubicSpline.Multipoint.sample(multipoint, coordinate);
            break;
         case 1:
            CubicSpline.Constant constant = (CubicSpline.Constant)spline;
            var10000 = constant.value();
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   static BoundedFloatFunction asSampler(final CubicSpline spline) {
      Objects.requireNonNull(spline);
      byte var2 = 0;
      BoundedFloatFunction var10000;
      switch (spline.typeSwitch<invokedynamic>(spline, var2)) {
         case 0:
            final CubicSpline.Multipoint multipoint = (CubicSpline.Multipoint)spline;
            var10000 = new BoundedFloatFunction() {
               public float apply(final Object c) {
                  return CubicSpline.Multipoint.sample(multipoint, c);
               }

               public Interval range() {
                  return multipoint.range();
               }
            };
            break;
         case 1:
            CubicSpline.Constant constant = (CubicSpline.Constant)spline;
            var10000 = BoundedFloatFunction.constant(constant.value());
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   static Codec codec(final Codec coordinateCodec) {
      return Codec.recursive("CubicSpline", (subSplineCodec) -> Codec.either(Codec.FLOAT, CubicSpline.Multipoint.codec(coordinateCodec, subSplineCodec)).xmap((e) -> (CubicSpline)e.map(CubicSpline.Constant::new, (m) -> m), (spline) -> {
            Objects.requireNonNull(spline);
            CubicSpline selector1$temp = spline;
            int index$2 = 0;

            while(true) {
               Either var10000;
               switch (selector1$temp.typeSwitch<invokedynamic>(selector1$temp, index$2)) {
                  case 0:
                     CubicSpline.Constant $b$0 = (CubicSpline.Constant)selector1$temp;
                     CubicSpline.Constant var9 = $b$0;

                     try {
                        var10 = var9.value();
                     } catch (Throwable var7) {
                        throw new MatchException(var7.toString(), var7);
                     }

                     float patt3$temp = var10;
                     if (false) {
                        index$2 = 1;
                        continue;
                     }

                     var10000 = Either.left(patt3$temp);
                     break;
                  case 1:
                     CubicSpline.Multipoint multipoint = (CubicSpline.Multipoint)selector1$temp;
                     var10000 = Either.right(multipoint);
                     break;
                  default:
                     throw new MatchException((String)null, (Throwable)null);
               }

               return var10000;
            }
         }));
   }

   static CubicSpline constant(final float value) {
      return new CubicSpline.Constant(value);
   }

   static CubicSpline.Builder builder(final BoundedFloatFunction coordinate) {
      return new CubicSpline.Builder(coordinate);
   }

   static CubicSpline.Builder builder(final BoundedFloatFunction coordinate, final Float2FloatFunction valueTransformer) {
      return new CubicSpline.Builder(coordinate, valueTransformer);
   }

   public static final class Builder {
      private final BoundedFloatFunction coordinate;
      private final Float2FloatFunction valueTransformer;
      private final FloatList locations = new FloatArrayList();
      private final List values = Lists.newArrayList();
      private final FloatList derivatives = new FloatArrayList();

      private Builder(final BoundedFloatFunction coordinate) {
         this(coordinate, Float2FloatFunction.identity());
      }

      private Builder(final BoundedFloatFunction coordinate, final Float2FloatFunction valueTransformer) {
         this.coordinate = coordinate;
         this.valueTransformer = valueTransformer;
      }

      public CubicSpline.Builder addPoint(final float location, final float value) {
         return this.addPoint(location, new CubicSpline.Constant(this.valueTransformer.apply(value)), 0.0F);
      }

      public CubicSpline.Builder addPoint(final float location, final float value, final float derivative) {
         return this.addPoint(location, new CubicSpline.Constant(this.valueTransformer.apply(value)), derivative);
      }

      public CubicSpline.Builder addPoint(final float location, final CubicSpline sampler) {
         return this.addPoint(location, sampler, 0.0F);
      }

      private CubicSpline.Builder addPoint(final float location, final CubicSpline sampler, final float derivative) {
         if (!this.locations.isEmpty() && location <= this.locations.getFloat(this.locations.size() - 1)) {
            throw new IllegalArgumentException("Please register points in ascending order");
         } else {
            this.locations.add(location);
            this.values.add(sampler);
            this.derivatives.add(derivative);
            return this;
         }
      }

      public CubicSpline build() {
         if (this.locations.isEmpty()) {
            throw new IllegalStateException("No elements added");
         } else {
            return new CubicSpline.Multipoint(this.coordinate, this.locations.toFloatArray(), List.copyOf(this.values), this.derivatives.toFloatArray());
         }
      }
   }

   @VisibleForDebug
   public static record Constant(float value) implements CubicSpline {
      public String parityString() {
         return String.format(Locale.ROOT, "k=%.3f", this.value);
      }

      public Interval range() {
         return Interval.ofExact(this.value);
      }

      public void forEachCoordinate(final Consumer consumer) {
      }

      public CubicSpline mapCoordinates(final Function mapper) {
         return this;
      }
   }

   @VisibleForDebug
   public static record Multipoint(BoundedFloatFunction coordinate, float[] locations, List values, float[] derivatives) implements CubicSpline {
      public Multipoint {
         validateSizes(locations, values, derivatives);
      }

      public Interval range() {
         int lastIndex = this.locations.length - 1;
         float minValue = Float.POSITIVE_INFINITY;
         float maxValue = Float.NEGATIVE_INFINITY;
         Interval inputRange = this.coordinate.range();
         if (inputRange.isNaI()) {
            return inputRange;
         } else {
            if (inputRange.min() < this.locations[0]) {
               Interval firstRange = ((CubicSpline)this.values.getFirst()).range();
               float edge1 = linearExtend(inputRange.min(), this.locations, firstRange.min(), this.derivatives, 0);
               float edge2 = linearExtend(inputRange.min(), this.locations, firstRange.max(), this.derivatives, 0);
               minValue = Math.min(minValue, Math.min(edge1, edge2));
               maxValue = Math.max(maxValue, Math.max(edge1, edge2));
            }

            if (inputRange.max() > this.locations[lastIndex]) {
               Interval lastRange = ((CubicSpline)this.values.get(lastIndex)).range();
               float edge1 = linearExtend(inputRange.max(), this.locations, lastRange.min(), this.derivatives, lastIndex);
               float edge2 = linearExtend(inputRange.max(), this.locations, lastRange.max(), this.derivatives, lastIndex);
               minValue = Math.min(minValue, Math.min(edge1, edge2));
               maxValue = Math.max(maxValue, Math.max(edge1, edge2));
            }

            List valueRanges = List.copyOf(Lists.transform(this.values, CubicSpline::range));

            for(Interval range : valueRanges) {
               minValue = Math.min(minValue, range.min());
               maxValue = Math.max(maxValue, range.max());
            }

            for(int i = 0; i < lastIndex; ++i) {
               float x1 = this.locations[i];
               float x2 = this.locations[i + 1];
               float xDiff = x2 - x1;
               Interval range1 = (Interval)valueRanges.get(i);
               Interval range2 = (Interval)valueRanges.get(i + 1);
               float min1 = range1.min();
               float max1 = range1.max();
               float min2 = range2.min();
               float max2 = range2.max();
               float d1 = this.derivatives[i];
               float d2 = this.derivatives[i + 1];
               if (d1 != 0.0F || d2 != 0.0F) {
                  float p1 = d1 * xDiff;
                  float p2 = d2 * xDiff;
                  float minLerp1 = Math.min(min1, min2);
                  float maxLerp1 = Math.max(max1, max2);
                  float minA = p1 - max2 + min1;
                  float maxA = p1 - min2 + max1;
                  float minB = -p2 + min2 - max1;
                  float maxB = -p2 + max2 - min1;
                  float minLerp2 = Math.min(minA, minB);
                  float maxLerp2 = Math.max(maxA, maxB);
                  minValue = Math.min(minValue, minLerp1 + 0.25F * minLerp2);
                  maxValue = Math.max(maxValue, maxLerp1 + 0.25F * maxLerp2);
               }
            }

            return Interval.of(minValue, maxValue);
         }
      }

      private static float linearExtend(final float input, final float[] locations, final float value, final float[] derivatives, final int index) {
         float derivative = derivatives[index];
         return derivative == 0.0F ? value : value + derivative * (input - locations[index]);
      }

      private static void validateSizes(final float[] locations, final List values, final float[] derivatives) {
         if (locations.length == values.size() && locations.length == derivatives.length) {
            if (locations.length == 0) {
               throw new IllegalArgumentException("Cannot create a multipoint spline with no points");
            }
         } else {
            throw new IllegalArgumentException("All lengths must be equal, got: " + locations.length + " " + values.size() + " " + derivatives.length);
         }
      }

      public static float sample(final CubicSpline.Multipoint sampler, final Object c) {
         return sample(sampler.coordinate, sampler.derivatives, sampler.locations, sampler.values, c);
      }

      private static float sample(final BoundedFloatFunction coordinate, final float[] derivatives, final float[] locations, final List values, final Object c) {
         float input = coordinate.apply(c);
         int start = findIntervalStart(locations, input);
         int lastIndex = locations.length - 1;
         if (start < 0) {
            return linearExtend(input, locations, CubicSpline.sample((CubicSpline)values.getFirst(), c), derivatives, 0);
         } else if (start == lastIndex) {
            return linearExtend(input, locations, CubicSpline.sample((CubicSpline)values.get(lastIndex), c), derivatives, lastIndex);
         } else {
            float x1 = locations[start];
            float x2 = locations[start + 1];
            float t = (input - x1) / (x2 - x1);
            CubicSpline f1 = (CubicSpline)values.get(start);
            CubicSpline f2 = (CubicSpline)values.get(start + 1);
            float d1 = derivatives[start];
            float d2 = derivatives[start + 1];
            float y1 = CubicSpline.sample(f1, c);
            float y2 = CubicSpline.sample(f2, c);
            float a = d1 * (x2 - x1) - (y2 - y1);
            float b = -d2 * (x2 - x1) + (y2 - y1);
            return Mth.lerp(t, y1, y2) + t * (1.0F - t) * Mth.lerp(t, a, b);
         }
      }

      private static int findIntervalStart(final float[] locations, final float input) {
         return Mth.binarySearch(0, locations.length, (i) -> input < locations[i]) - 1;
      }

      @VisibleForTesting
      public String parityString() {
         return "Spline{coordinate=" + String.valueOf(this.coordinate) + ", locations=" + toString(this.locations) + ", derivatives=" + toString(this.derivatives) + ", values=" + (String)this.values.stream().map(CubicSpline::parityString).collect(Collectors.joining(", ", "[", "]")) + "}";
      }

      private static String toString(final float[] arr) {
         return "[" + (String)IntStream.range(0, arr.length).mapToDouble((i) -> (double)arr[i]).mapToObj((f) -> String.format(Locale.ROOT, "%.3f", f)).collect(Collectors.joining(", ")) + "]";
      }

      public void forEachCoordinate(final Consumer consumer) {
         consumer.accept(this.coordinate);

         for(CubicSpline spline : this.values) {
            spline.forEachCoordinate(consumer);
         }

      }

      public CubicSpline mapCoordinates(final Function mapper) {
         return new CubicSpline.Multipoint((BoundedFloatFunction)mapper.apply(this.coordinate), this.locations, this.values.stream().map((v) -> v.mapCoordinates(mapper)).toList(), this.derivatives);
      }

      public boolean equals(final Object obj) {
         if (!(obj instanceof CubicSpline.Multipoint multipoint)) {
            return false;
         } else {
            return Objects.equals(this.coordinate, multipoint.coordinate) && Arrays.equals(this.locations, multipoint.locations) && Arrays.equals(this.derivatives, multipoint.derivatives) && Objects.equals(this.values, multipoint.values);
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.coordinate, Arrays.hashCode(this.locations), this.values, Arrays.hashCode(this.derivatives)});
      }

      public static Codec codec(final Codec coordinateCodec, final Codec subSplineCodec) {
         return RecordCodecBuilder.create((i) -> i.group(coordinateCodec.fieldOf("coordinate").forGetter(CubicSpline.Multipoint::coordinate), ExtraCodecs.nonEmptyList(CubicSpline.Multipoint.Point.codec(subSplineCodec).listOf()).fieldOf("points").forGetter(CubicSpline.Multipoint::packToPoints)).apply(i, CubicSpline.Multipoint::createFromPoints));
      }

      private List packToPoints() {
         int pointCount = this.locations.length;
         List list = new ArrayList(pointCount);

         for(int p = 0; p < pointCount; ++p) {
            list.add(new CubicSpline.Multipoint.Point(this.locations[p], (CubicSpline)this.values.get(p), this.derivatives[p]));
         }

         return list;
      }

      private static CubicSpline.Multipoint createFromPoints(final BoundedFloatFunction coordinate, final List points) {
         int pointCount = points.size();
         float[] locations = new float[pointCount];
         ImmutableList.Builder values = ImmutableList.builderWithExpectedSize(pointCount);
         float[] derivatives = new float[pointCount];

         for(int p = 0; p < pointCount; ++p) {
            CubicSpline.Multipoint.Point point = (CubicSpline.Multipoint.Point)points.get(p);
            locations[p] = point.location();
            values.add(point.value());
            derivatives[p] = point.derivative();
         }

         return new CubicSpline.Multipoint(coordinate, locations, values.build(), derivatives);
      }

      private static record Point(float location, CubicSpline value, float derivative) {
         public static Codec codec(final Codec subSplineCodec) {
            return RecordCodecBuilder.create((i) -> i.group(Codec.FLOAT.fieldOf("location").forGetter(CubicSpline.Multipoint.Point::location), subSplineCodec.fieldOf("value").forGetter(CubicSpline.Multipoint.Point::value), Codec.FLOAT.fieldOf("derivative").forGetter(CubicSpline.Multipoint.Point::derivative)).apply(i, CubicSpline.Multipoint.Point::new));
         }
      }
   }
}
