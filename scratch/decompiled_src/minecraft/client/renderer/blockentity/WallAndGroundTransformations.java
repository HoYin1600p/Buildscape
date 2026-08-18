package net.minecraft.client.renderer.blockentity;

import java.util.Map;
import java.util.function.Function;
import java.util.function.IntFunction;
import net.minecraft.core.Direction;
import net.minecraft.util.Util;

public class WallAndGroundTransformations {
   private final Map wallTransforms;
   private final Object[] freeTransformations;

   public WallAndGroundTransformations(final Function wallTransformationFactory, final IntFunction freeTransformationFactory, final int segments) {
      this.wallTransforms = Util.makeEnumMap(Direction.class, wallTransformationFactory);
      this.freeTransformations = new Object[segments];

      for(int segment = 0; segment < segments; ++segment) {
         this.freeTransformations[segment] = freeTransformationFactory.apply(segment);
      }

   }

   public Object wallTransformation(final Direction facing) {
      return this.wallTransforms.get(facing);
   }

   public Object freeTransformations(final int segment) {
      return this.freeTransformations[segment];
   }
}
