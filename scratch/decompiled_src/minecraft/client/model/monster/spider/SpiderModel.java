package net.minecraft.client.model.monster.spider;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

public class SpiderModel extends EntityModel {
   private static final String BODY_0 = "body0";
   private static final String BODY_1 = "body1";
   private static final String RIGHT_MIDDLE_FRONT_LEG = "right_middle_front_leg";
   private static final String LEFT_MIDDLE_FRONT_LEG = "left_middle_front_leg";
   private static final String RIGHT_MIDDLE_HIND_LEG = "right_middle_hind_leg";
   private static final String LEFT_MIDDLE_HIND_LEG = "left_middle_hind_leg";
   private final ModelPart head;
   private final ModelPart rightHindLeg;
   private final ModelPart leftHindLeg;
   private final ModelPart rightMiddleHindLeg;
   private final ModelPart leftMiddleHindLeg;
   private final ModelPart rightMiddleFrontLeg;
   private final ModelPart leftMiddleFrontLeg;
   private final ModelPart rightFrontLeg;
   private final ModelPart leftFrontLeg;

   public SpiderModel(final ModelPart root) {
      super(root);
      this.head = root.getChild("head");
      this.rightHindLeg = root.getChild("right_hind_leg");
      this.leftHindLeg = root.getChild("left_hind_leg");
      this.rightMiddleHindLeg = root.getChild("right_middle_hind_leg");
      this.leftMiddleHindLeg = root.getChild("left_middle_hind_leg");
      this.rightMiddleFrontLeg = root.getChild("right_middle_front_leg");
      this.leftMiddleFrontLeg = root.getChild("left_middle_front_leg");
      this.rightFrontLeg = root.getChild("right_front_leg");
      this.leftFrontLeg = root.getChild("left_front_leg");
   }

   public static LayerDefinition createSpiderBodyLayer() {
      MeshDefinition mesh = new MeshDefinition();
      PartDefinition root = mesh.getRoot();
      int yo = 15;
      root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(32, 4).addBox(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F), PartPose.offset(0.0F, 15.0F, -3.0F));
      root.addOrReplaceChild("body0", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F), PartPose.offset(0.0F, 15.0F, 0.0F));
      root.addOrReplaceChild("body1", CubeListBuilder.create().texOffs(0, 12).addBox(-5.0F, -4.0F, -6.0F, 10.0F, 8.0F, 12.0F), PartPose.offset(0.0F, 15.0F, 9.0F));
      CubeListBuilder rightLeg = CubeListBuilder.create().texOffs(18, 0).addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F);
      CubeListBuilder leftLeg = CubeListBuilder.create().texOffs(18, 0).mirror().addBox(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F);
      float legZRot = ((float)Math.PI / 4F);
      float legYRotSpan = ((float)Math.PI / 8F);
      root.addOrReplaceChild("right_hind_leg", rightLeg, PartPose.offsetAndRotation(-4.0F, 15.0F, 2.0F, 0.0F, ((float)Math.PI / 4F), (-(float)Math.PI / 4F)));
      root.addOrReplaceChild("left_hind_leg", leftLeg, PartPose.offsetAndRotation(4.0F, 15.0F, 2.0F, 0.0F, (-(float)Math.PI / 4F), ((float)Math.PI / 4F)));
      root.addOrReplaceChild("right_middle_hind_leg", rightLeg, PartPose.offsetAndRotation(-4.0F, 15.0F, 1.0F, 0.0F, ((float)Math.PI / 8F), -0.58119464F));
      root.addOrReplaceChild("left_middle_hind_leg", leftLeg, PartPose.offsetAndRotation(4.0F, 15.0F, 1.0F, 0.0F, (-(float)Math.PI / 8F), 0.58119464F));
      root.addOrReplaceChild("right_middle_front_leg", rightLeg, PartPose.offsetAndRotation(-4.0F, 15.0F, 0.0F, 0.0F, (-(float)Math.PI / 8F), -0.58119464F));
      root.addOrReplaceChild("left_middle_front_leg", leftLeg, PartPose.offsetAndRotation(4.0F, 15.0F, 0.0F, 0.0F, ((float)Math.PI / 8F), 0.58119464F));
      root.addOrReplaceChild("right_front_leg", rightLeg, PartPose.offsetAndRotation(-4.0F, 15.0F, -1.0F, 0.0F, (-(float)Math.PI / 4F), (-(float)Math.PI / 4F)));
      root.addOrReplaceChild("left_front_leg", leftLeg, PartPose.offsetAndRotation(4.0F, 15.0F, -1.0F, 0.0F, ((float)Math.PI / 4F), ((float)Math.PI / 4F)));
      return LayerDefinition.create(mesh, 64, 32);
   }

   public void setupAnim(final LivingEntityRenderState state) {
      super.setupAnim(state);
      this.head.yRot = state.yRot * ((float)Math.PI / 180F);
      this.head.xRot = state.xRot * ((float)Math.PI / 180F);
      float animationPos = state.walkAnimationPos * 0.6662F;
      float animationSpeed = state.walkAnimationSpeed;
      float swingHind = -(Mth.cos((double)(animationPos * 2.0F + 0.0F)) * 0.4F) * animationSpeed;
      float swingMiddleHind = -(Mth.cos((double)(animationPos * 2.0F + (float)Math.PI)) * 0.4F) * animationSpeed;
      float swingMiddleFront = -(Mth.cos((double)(animationPos * 2.0F + ((float)Math.PI / 2F))) * 0.4F) * animationSpeed;
      float swingFront = -(Mth.cos((double)(animationPos * 2.0F + ((float)Math.PI * 1.5F))) * 0.4F) * animationSpeed;
      float stepHind = Math.abs(Mth.sin((double)(animationPos + 0.0F)) * 0.4F) * animationSpeed;
      float stepMiddleHind = Math.abs(Mth.sin((double)(animationPos + (float)Math.PI)) * 0.4F) * animationSpeed;
      float stepMiddleFrontHind = Math.abs(Mth.sin((double)(animationPos + ((float)Math.PI / 2F))) * 0.4F) * animationSpeed;
      float stepFront = Math.abs(Mth.sin((double)(animationPos + ((float)Math.PI * 1.5F))) * 0.4F) * animationSpeed;
      this.rightHindLeg.yRot += swingHind;
      this.leftHindLeg.yRot -= swingHind;
      this.rightMiddleHindLeg.yRot += swingMiddleHind;
      this.leftMiddleHindLeg.yRot -= swingMiddleHind;
      this.rightMiddleFrontLeg.yRot += swingMiddleFront;
      this.leftMiddleFrontLeg.yRot -= swingMiddleFront;
      this.rightFrontLeg.yRot += swingFront;
      this.leftFrontLeg.yRot -= swingFront;
      this.rightHindLeg.zRot += stepHind;
      this.leftHindLeg.zRot -= stepHind;
      this.rightMiddleHindLeg.zRot += stepMiddleHind;
      this.leftMiddleHindLeg.zRot -= stepMiddleHind;
      this.rightMiddleFrontLeg.zRot += stepMiddleFrontHind;
      this.leftMiddleFrontLeg.zRot -= stepMiddleFrontHind;
      this.rightFrontLeg.zRot += stepFront;
      this.leftFrontLeg.zRot -= stepFront;
   }
}
