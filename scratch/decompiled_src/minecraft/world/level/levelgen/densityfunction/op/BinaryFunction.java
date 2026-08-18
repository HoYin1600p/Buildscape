package net.minecraft.world.level.levelgen.densityfunction.op;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.generator.ConstantFunction;
import org.slf4j.Logger;

public record BinaryFunction(BinaryFunction.Type type, DensityFunction left, DensityFunction right, float rightMinValue, float rightMaxValue) implements DensityFunction {
   private static final Logger LOGGER = LogUtils.getLogger();

   public BinaryFunction(final BinaryFunction.Type type, final DensityFunction left, final DensityFunction right) {
      Interval rightRange = right.range();
      this(type, left, right, rightRange.min(), rightRange.max());
      if ((type == BinaryFunction.Type.MIN || type == BinaryFunction.Type.MAX) && !left.range().intersects(rightRange)) {
         LOGGER.warn("Creating a {} function between two non-overlapping inputs: {} and {}", new Object[]{type, left, right});
      }

   }

   public DensityFunction trySimplify() {
      if (this.type == BinaryFunction.Type.MUL || this.type == BinaryFunction.Type.ADD) {
         float var15;
         label51: {
            label56: {
               DensityFunction var3 = this.left;
               if (var3 instanceof ConstantFunction) {
                  ConstantFunction var1 = (ConstantFunction)var3;
                  ConstantFunction var10000 = var1;

                  try {
                     var13 = var10000.value();
                  } catch (Throwable var7) {
                     var12 = var7;
                     boolean var10001 = false;
                     break label56;
                  }

                  float var4 = var13;
                  if (true) {
                     return new BinaryFunction.MulOrAdd(this.type == BinaryFunction.Type.ADD ? BinaryFunction.MulOrAdd.Type.ADD : BinaryFunction.MulOrAdd.Type.MUL, this.right, var4);
                  }
               }

               var3 = this.right;
               if (!(var3 instanceof ConstantFunction)) {
                  return this;
               }

               ConstantFunction var8 = (ConstantFunction)var3;
               ConstantFunction var14 = var8;

               try {
                  var15 = var14.value();
                  break label51;
               } catch (Throwable var6) {
                  var12 = var6;
                  boolean var16 = false;
               }
            }

            Throwable var9 = var12;
            throw new MatchException(var9.toString(), var9);
         }

         float var11 = var15;
         if (true) {
            return new BinaryFunction.MulOrAdd(this.type == BinaryFunction.Type.ADD ? BinaryFunction.MulOrAdd.Type.ADD : BinaryFunction.MulOrAdd.Type.MUL, this.left, var11);
         }
      }

      return this;
   }

   public float compute(final DensityFunction.FunctionContext context) {
      float left = this.left.compute(context);
      float var10000;
      switch (this.type.ordinal()) {
         case 0:
            var10000 = left + this.right.compute(context);
            break;
         case 1:
            var10000 = left - this.right.compute(context);
            break;
         case 2:
            var10000 = left == 0.0F ? 0.0F : left * this.right.compute(context);
            break;
         case 3:
            var10000 = left == 0.0F ? 0.0F : left / this.right.compute(context);
            break;
         case 4:
            var10000 = left < this.rightMinValue ? left : Math.min(left, this.right.compute(context));
            break;
         case 5:
            var10000 = left > this.rightMaxValue ? left : Math.max(left, this.right.compute(context));
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public void fillArray(final float[] output, final DensityFunction.ContextProvider contextProvider) {
      this.left.fillArray(output, contextProvider);
      switch (this.type.ordinal()) {
         case 0:
            float[] right = new float[output.length];
            this.right.fillArray(right, contextProvider);

            for(int i = 0; i < output.length; ++i) {
               output[i] += right[i];
            }
            break;
         case 1:
            float[] right = new float[output.length];
            this.right.fillArray(right, contextProvider);

            for(int i = 0; i < output.length; ++i) {
               output[i] -= right[i];
            }
            break;
         case 2:
            for(int i = 0; i < output.length; ++i) {
               float left = output[i];
               output[i] = left == 0.0F ? 0.0F : left * this.right.compute(contextProvider.forIndex(i));
            }
            break;
         case 3:
            for(int i = 0; i < output.length; ++i) {
               float left = output[i];
               output[i] = left == 0.0F ? 0.0F : left / this.right.compute(contextProvider.forIndex(i));
            }
            break;
         case 4:
            for(int i = 0; i < output.length; ++i) {
               float left = output[i];
               output[i] = left < this.rightMinValue ? left : Math.min(left, this.right.compute(contextProvider.forIndex(i)));
            }
            break;
         case 5:
            for(int i = 0; i < output.length; ++i) {
               float left = output[i];
               output[i] = left > this.rightMaxValue ? left : Math.max(left, this.right.compute(contextProvider.forIndex(i)));
            }
      }

   }

   public DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
      return new BinaryFunction(this.type, visitor.apply(this.left), visitor.apply(this.right));
   }

   public Interval range() {
      Interval left = this.left.range();
      Interval right = this.right.range();
      Interval var10000;
      switch (this.type.ordinal()) {
         case 0:
            var10000 = Interval.add(left, right);
            break;
         case 1:
            var10000 = Interval.sub(left, right);
            break;
         case 2:
            var10000 = Interval.mul(left, right);
            break;
         case 3:
            var10000 = Interval.div(left, right);
            break;
         case 4:
            var10000 = Interval.min(left, right);
            break;
         case 5:
            var10000 = Interval.max(left, right);
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public boolean equals(final Object obj) {
      if (obj instanceof BinaryFunction binary) {
         if (this.type == binary.type && this.left.equals(binary.left) && this.right.equals(binary.right)) {
            return true;
         }
      }

      return false;
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.type, this.left, this.right});
   }

   public @DensityFunction.Axes int domainAxes() {
      return this.left.domainAxes() | this.right.domainAxes();
   }

   public MapCodec codec() {
      return this.type().codec;
   }

   public static record MulOrAdd(BinaryFunction.MulOrAdd.Type specificType, DensityFunction right, float leftValue) implements DensityFunction {
      public float compute(final DensityFunction.FunctionContext context) {
         float input = this.right.compute(context);
         float var10000;
         switch (this.specificType.ordinal()) {
            case 0:
               var10000 = input * this.leftValue;
               break;
            case 1:
               var10000 = input + this.leftValue;
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      public void fillArray(final float[] output, final DensityFunction.ContextProvider contextProvider) {
         this.right.fillArray(output, contextProvider);
         switch (this.specificType.ordinal()) {
            case 0:
               for(int i = 0; i < output.length; ++i) {
                  output[i] *= this.leftValue;
               }
               break;
            case 1:
               for(int i = 0; i < output.length; ++i) {
                  output[i] += this.leftValue;
               }
         }

      }

      public DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
         return new BinaryFunction.MulOrAdd(this.specificType, visitor.apply(this.right), this.leftValue);
      }

      public Interval range() {
         Interval var10000;
         switch (this.specificType.ordinal()) {
            case 0:
               var10000 = Interval.mul(this.right.range(), Interval.ofExact(this.leftValue));
               break;
            case 1:
               var10000 = Interval.add(this.right.range(), Interval.ofExact(this.leftValue));
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      public @DensityFunction.Axes int domainAxes() {
         return this.right.domainAxes();
      }

      public MapCodec codec() {
         throw new UnsupportedOperationException();
      }

      public static enum Type {
         MUL,
         ADD;

         // $FF: synthetic method
         private static BinaryFunction.MulOrAdd.Type[] $values() {
            return new BinaryFunction.MulOrAdd.Type[]{MUL, ADD};
         }
      }
   }

   public static enum Type {
      ADD("add"),
      SUB("sub"),
      MUL("mul"),
      DIV("div"),
      MIN("min"),
      MAX("max");

      public final String id;
      public final MapCodec codec = RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunction.CODEC.fieldOf("left").forGetter(BinaryFunction::left), DensityFunction.CODEC.fieldOf("right").forGetter(BinaryFunction::right)).apply(i, (left, right) -> new BinaryFunction(this, left, right)));

      private Type(final String id) {
         this.id = id;
      }

      // $FF: synthetic method
      private static BinaryFunction.Type[] $values() {
         return new BinaryFunction.Type[]{ADD, SUB, MUL, DIV, MIN, MAX};
      }
   }
}
