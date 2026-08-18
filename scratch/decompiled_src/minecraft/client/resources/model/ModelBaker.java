package net.minecraft.client.resources.model;

import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.MaterialBaker;
import net.minecraft.resources.Identifier;
import org.joml.Vector3fc;

public interface ModelBaker {
   ResolvedModel getModel(Identifier location);

   BlockStateModelPart missingBlockModelPart();

   MaterialBaker materials();

   ModelBaker.Interner interner();

   Object compute(ModelBaker.SharedOperationKey key);

   public interface Interner {
      Vector3fc vector(Vector3fc vector);

      BakedQuad.MaterialInfo materialInfo(BakedQuad.MaterialInfo material);
   }

   @FunctionalInterface
   public interface SharedOperationKey {
      Object compute(ModelBaker modelBakery);
   }
}
