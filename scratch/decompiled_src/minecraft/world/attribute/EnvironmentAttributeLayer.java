package net.minecraft.world.attribute;

import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public sealed interface EnvironmentAttributeLayer {
   @FunctionalInterface
   public non-sealed interface Constant extends EnvironmentAttributeLayer {
      Object applyConstant(Object baseValue);
   }

   @FunctionalInterface
   public non-sealed interface Positional extends EnvironmentAttributeLayer {
      Object applyPositional(Object baseValue, Vec3 pos, @Nullable SpatialAttributeInterpolator biomeInterpolator);
   }

   @FunctionalInterface
   public non-sealed interface TimeBased extends EnvironmentAttributeLayer {
      Object applyTimeBased(Object baseValue, int cacheTickId);
   }
}
