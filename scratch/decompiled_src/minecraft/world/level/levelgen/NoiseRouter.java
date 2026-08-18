package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;

public record NoiseRouter(DensityFunction temperature, DensityFunction vegetation, DensityFunction continents, DensityFunction erosion, DensityFunction depth, DensityFunction ridges, DensityFunction preliminarySurfaceLevel, DensityFunction finalDensity) {
   public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(field("temperature", NoiseRouter::temperature), field("vegetation", NoiseRouter::vegetation), field("continents", NoiseRouter::continents), field("erosion", NoiseRouter::erosion), field("depth", NoiseRouter::depth), field("ridges", NoiseRouter::ridges), field("preliminary_surface_level", NoiseRouter::preliminarySurfaceLevel), field("final_density", NoiseRouter::finalDensity)).apply(i, NoiseRouter::new));

   private static RecordCodecBuilder field(final String name, final Function getter) {
      return DensityFunction.CODEC.fieldOf(name).forGetter(getter);
   }

   public NoiseRouter mapAll(final DensityFunction.Visitor visitor) {
      return new NoiseRouter(this.temperature.mapAll(visitor), this.vegetation.mapAll(visitor), this.continents.mapAll(visitor), this.erosion.mapAll(visitor), this.depth.mapAll(visitor), this.ridges.mapAll(visitor), this.preliminarySurfaceLevel.mapAll(visitor), this.finalDensity.mapAll(visitor));
   }
}
