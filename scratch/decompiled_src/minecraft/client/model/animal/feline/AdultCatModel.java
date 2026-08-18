package net.minecraft.client.model.animal.feline;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.MeshTransformer;

public class AdultCatModel extends AdultFelineModel {
   public static final MeshTransformer CAT_TRANSFORMER = MeshTransformer.scaling(0.8F);
   public static final CubeDeformation COLLAR_DEFORMATION = new CubeDeformation(0.01F);

   public AdultCatModel(final ModelPart root) {
      super(root);
   }
}
