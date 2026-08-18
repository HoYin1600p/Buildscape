package net.minecraft.world.level.levelgen.densityfunction.generator;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;

public interface ShiftNoiseFunction extends DensityFunction {
   DensityFunction.NoiseHolder offsetNoise();

   default Interval range() {
      return Interval.mul(this.offsetNoise().range(), Interval.ofExact(4.0F));
   }

   default float compute(final double localX, final double localY, final double localZ) {
      return this.offsetNoise().getValue(localX * 0.25D, localY * 0.25D, localZ * 0.25D) * 4.0F;
   }

   default void fillArray(final float[] output, final DensityFunction.ContextProvider contextProvider) {
      contextProvider.fillAllDirectly(output, this);
   }

   MapCodec codec();

   public static record Shift(DensityFunction.NoiseHolder offsetNoise) implements ShiftNoiseFunction {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunction.NoiseHolder.CODEC.fieldOf("noise").forGetter(ShiftNoiseFunction.Shift::offsetNoise)).apply(i, ShiftNoiseFunction.Shift::new));

      public float compute(final DensityFunction.FunctionContext context) {
         return this.compute((double)context.blockX(), (double)context.blockY(), (double)context.blockZ());
      }

      public DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
         return new ShiftNoiseFunction.Shift(visitor.visitNoise(this.offsetNoise));
      }

      public @DensityFunction.Axes int domainAxes() {
         return 7;
      }

      public MapCodec codec() {
         return CODEC;
      }
   }

   public static record ShiftA(DensityFunction.NoiseHolder offsetNoise) implements ShiftNoiseFunction {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunction.NoiseHolder.CODEC.fieldOf("noise").forGetter(ShiftNoiseFunction.ShiftA::offsetNoise)).apply(i, ShiftNoiseFunction.ShiftA::new));

      public float compute(final DensityFunction.FunctionContext context) {
         return this.compute((double)context.blockX(), 0.0D, (double)context.blockZ());
      }

      public DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
         return new ShiftNoiseFunction.ShiftA(visitor.visitNoise(this.offsetNoise));
      }

      public @DensityFunction.Axes int domainAxes() {
         return 5;
      }

      public MapCodec codec() {
         return CODEC;
      }
   }

   public static record ShiftB(DensityFunction.NoiseHolder offsetNoise) implements ShiftNoiseFunction {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunction.NoiseHolder.CODEC.fieldOf("noise").forGetter(ShiftNoiseFunction.ShiftB::offsetNoise)).apply(i, ShiftNoiseFunction.ShiftB::new));

      public float compute(final DensityFunction.FunctionContext context) {
         return this.compute((double)context.blockZ(), (double)context.blockX(), 0.0D);
      }

      public DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
         return new ShiftNoiseFunction.ShiftB(visitor.visitNoise(this.offsetNoise));
      }

      public @DensityFunction.Axes int domainAxes() {
         return 5;
      }

      public MapCodec codec() {
         return CODEC;
      }
   }
}
