package net.minecraft.client.model.animal.parrot;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.ParrotRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.parrot.Parrot;

public class ParrotModel extends EntityModel {
   private static final String FEATHER = "feather";
   private final ModelPart body;
   private final ModelPart tail;
   private final ModelPart leftWing;
   private final ModelPart rightWing;
   private final ModelPart head;
   private final ModelPart leftLeg;
   private final ModelPart rightLeg;

   public ParrotModel(final ModelPart root) {
      super(root);
      this.body = root.getChild("body");
      this.tail = root.getChild("tail");
      this.leftWing = root.getChild("left_wing");
      this.rightWing = root.getChild("right_wing");
      this.head = root.getChild("head");
      this.leftLeg = root.getChild("left_leg");
      this.rightLeg = root.getChild("right_leg");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition mesh = new MeshDefinition();
      PartDefinition root = mesh.getRoot();
      root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(2, 8).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F), PartPose.offsetAndRotation(0.0F, 16.5F, -3.0F, 0.4937F, 0.0F, 0.0F));
      root.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(22, 1).addBox(-1.5F, -1.0F, -1.0F, 3.0F, 4.0F, 1.0F), PartPose.offsetAndRotation(0.0F, 21.07F, 1.16F, 1.015F, 0.0F, 0.0F));
      root.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(19, 8).addBox(-0.5F, 0.0F, -1.5F, 1.0F, 5.0F, 3.0F), PartPose.offsetAndRotation(1.5F, 16.94F, -2.76F, -0.6981F, -(float)Math.PI, 0.0F));
      root.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(19, 8).addBox(-0.5F, 0.0F, -1.5F, 1.0F, 5.0F, 3.0F), PartPose.offsetAndRotation(-1.5F, 16.94F, -2.76F, -0.6981F, -(float)Math.PI, 0.0F));
      PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(2, 2).addBox(-1.0F, -1.5F, -1.0F, 2.0F, 3.0F, 2.0F), PartPose.offset(0.0F, 15.69F, -2.76F));
      head.addOrReplaceChild("head2", CubeListBuilder.create().texOffs(10, 0).addBox(-1.0F, -0.5F, -2.0F, 2.0F, 1.0F, 4.0F), PartPose.offset(0.0F, -2.0F, -1.0F));
      head.addOrReplaceChild("beak1", CubeListBuilder.create().texOffs(11, 7).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F), PartPose.offset(0.0F, -0.5F, -1.5F));
      head.addOrReplaceChild("beak2", CubeListBuilder.create().texOffs(16, 7).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F), PartPose.offset(0.0F, -1.75F, -2.45F));
      head.addOrReplaceChild("feather", CubeListBuilder.create().texOffs(2, 18).addBox(0.0F, -4.0F, -2.0F, 0.0F, 5.0F, 4.0F), PartPose.offsetAndRotation(0.0F, -2.15F, 0.15F, -0.2214F, 0.0F, 0.0F));
      CubeListBuilder leg = CubeListBuilder.create().texOffs(14, 18).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F);
      root.addOrReplaceChild("left_leg", leg, PartPose.offsetAndRotation(1.0F, 22.0F, -1.05F, -0.0299F, 0.0F, 0.0F));
      root.addOrReplaceChild("right_leg", leg, PartPose.offsetAndRotation(-1.0F, 22.0F, -1.05F, -0.0299F, 0.0F, 0.0F));
      return LayerDefinition.create(mesh, 32, 32);
   }

   public void setupAnim(final ParrotRenderState state) {
      super.setupAnim(state);
      this.prepare(state.pose);
      this.head.xRot = state.xRot * ((float)Math.PI / 180F);
      this.head.yRot = state.yRot * ((float)Math.PI / 180F);
      switch (state.pose.ordinal()) {
         case 1:
            this.leftLeg.xRot += Mth.cos((double)(state.walkAnimationPos * 0.6662F)) * 1.4F * state.walkAnimationSpeed;
            this.rightLeg.xRot += Mth.cos((double)(state.walkAnimationPos * 0.6662F + (float)Math.PI)) * 1.4F * state.walkAnimationSpeed;
         case 0:
         case 4:
         default:
            float bobbingBody = state.flapAngle * 0.3F;
            this.head.y += bobbingBody;
            this.tail.xRot += Mth.cos((double)(state.walkAnimationPos * 0.6662F)) * 0.3F * state.walkAnimationSpeed;
            this.tail.y += bobbingBody;
            this.body.y += bobbingBody;
            this.leftWing.zRot = -0.0873F - state.flapAngle;
            this.leftWing.y += bobbingBody;
            this.rightWing.zRot = 0.0873F + state.flapAngle;
            this.rightWing.y += bobbingBody;
            this.leftLeg.y += bobbingBody;
            this.rightLeg.y += bobbingBody;
         case 2:
            break;
         case 3:
            float xPos = Mth.cos((double)state.ageInTicks);
            float yPos = Mth.sin((double)state.ageInTicks);
            this.head.x += xPos;
            this.head.y += yPos;
            this.head.xRot = 0.0F;
            this.head.yRot = 0.0F;
            this.head.zRot = Mth.sin((double)state.ageInTicks) * 0.4F;
            this.body.x += xPos;
            this.body.y += yPos;
            this.leftWing.zRot = -0.0873F - state.flapAngle;
            this.leftWing.x += xPos;
            this.leftWing.y += yPos;
            this.rightWing.zRot = 0.0873F + state.flapAngle;
            this.rightWing.x += xPos;
            this.rightWing.y += yPos;
            this.tail.x += xPos;
            this.tail.y += yPos;
      }

   }

   private void prepare(final ParrotModel.Pose pose) {
      switch (pose.ordinal()) {
         case 0:
            this.leftLeg.xRot += 0.6981317F;
            this.rightLeg.xRot += 0.6981317F;
         case 1:
         case 4:
         default:
            break;
         case 2:
            float sittingYOffset = 1.9F;
            ++this.head.y;
            this.tail.xRot += ((float)Math.PI / 6F);
            ++this.tail.y;
            ++this.body.y;
            this.leftWing.zRot = -0.0873F;
            ++this.leftWing.y;
            this.rightWing.zRot = 0.0873F;
            ++this.rightWing.y;
            ++this.leftLeg.y;
            ++this.rightLeg.y;
            ++this.leftLeg.xRot;
            ++this.rightLeg.xRot;
            break;
         case 3:
            this.leftLeg.zRot = -0.34906584F;
            this.rightLeg.zRot = 0.34906584F;
      }

   }

   public static ParrotModel.Pose getPose(final Parrot entity) {
      if (entity.isPartyParrot()) {
         return ParrotModel.Pose.PARTY;
      } else if (entity.isInSittingPose()) {
         return ParrotModel.Pose.SITTING;
      } else {
         return entity.isFlying() ? ParrotModel.Pose.FLYING : ParrotModel.Pose.STANDING;
      }
   }

   public static enum Pose {
      FLYING,
      STANDING,
      SITTING,
      PARTY,
      ON_SHOULDER;

      // $FF: synthetic method
      private static ParrotModel.Pose[] $values() {
         return new ParrotModel.Pose[]{FLYING, STANDING, SITTING, PARTY, ON_SHOULDER};
      }
   }
}
