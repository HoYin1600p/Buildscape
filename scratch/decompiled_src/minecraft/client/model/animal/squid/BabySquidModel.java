package net.minecraft.client.model.animal.squid;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class BabySquidModel extends SquidModel {
   public BabySquidModel(final ModelPart root) {
      super(root);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition root = meshdefinition.getRoot();
      PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -5.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 13.0F, 0.0F));
      int tentacleCount = 8;
      CubeListBuilder tentacle = CubeListBuilder.create().texOffs(0, 18).addBox(-1.0F, -0.5F, -1.0F, 2.0F, 6.0F, 2.0F);

      for(int i = 0; i < 8; ++i) {
         double angle = (double)i * Math.PI * 2.0D / 8.0D;
         float x = (float)Math.cos(angle) * 3.0F;
         float y = 18.5F;
         float z = (float)Math.sin(angle) * 3.0F;
         angle = (double)i * Math.PI * -2.0D / 8.0D + (Math.PI / 2D);
         float yRot = (float)angle;
         root.addOrReplaceChild(createTentacleName(i), tentacle, PartPose.offsetAndRotation(x, 18.5F, z, 0.0F, yRot, 0.0F));
      }

      return LayerDefinition.create(meshdefinition, 32, 32);
   }
}
