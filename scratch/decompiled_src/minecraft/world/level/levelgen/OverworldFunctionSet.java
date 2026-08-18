package net.minecraft.world.level.levelgen;

import java.util.function.Function;

public record OverworldFunctionSet(Object temperature, Object vegetation, Object continents, Object erosion, Object offset, Object factor, Object jaggedness, Object depth, Object slopedCheese, Object preliminarySurfaceLevel, Object finalDensity) {
   public OverworldFunctionSet map(final Function function) {
      return new OverworldFunctionSet(function.apply(this.temperature), function.apply(this.vegetation), function.apply(this.continents), function.apply(this.erosion), function.apply(this.offset), function.apply(this.factor), function.apply(this.jaggedness), function.apply(this.depth), function.apply(this.slopedCheese), function.apply(this.preliminarySurfaceLevel), function.apply(this.finalDensity));
   }
}
