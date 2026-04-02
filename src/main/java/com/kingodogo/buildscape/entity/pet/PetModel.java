package com.kingodogo.buildscape.entity.pet;

import com.kingodogo.buildscape.entity.pet.animations.AnimationTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

public class PetModel<T extends PetEntity> extends HierarchicalModel<T> implements ArmedModel {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation("buildscape", "pet"), "main");

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    private final AnimationTarget target = new AnimationTarget();

    // Smooth absolute targets to prevent infinite cascading when game is paused
    private float smHeadX, smHeadY, smHeadZ, smHeadYPos, smHeadZPos;
    private float smBodyX, smBodyY, smBodyZ, smBodyYPos, smBodyZPos;
    private float smRArmX, smRArmY, smRArmZ, smRArmYPos, smRArmZPos;
    private float smLArmX, smLArmY, smLArmZ, smLArmYPos, smLArmZPos;
    private float smRLegX, smRLegY, smRLegZ, smRLegYPos;
    private float smLLegX, smLLegY, smLLegZ, smLLegYPos;
    private float smRootX, smRootY, smRootZ, smRootXPos, smRootYPos, smRootZPos;

    public PetModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.offset(0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F), PartPose.offset(0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offset(-5.0F, 2.0F, 0.0F));
        root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offset(5.0F, 2.0F, 0.0F));
        root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offset(-1.9F, 12.0F, 0.0F));
        root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offset(1.9F, 12.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // --- 1. RESET TARGET STATE ---
        target.resetDefaults(headPitch, netHeadYaw);

        // --- 2. COMPILE TARGET OFFSETS FROM OOP ANIMATIONS ---
        PetAnimation animState = entity.getCurrentAnimation();

        if (entity.isOrderedToSit()) {
            target.rightArmX = -0.62831855F;
            target.leftArmX = -0.62831855F;
            target.rightLegX = -1.4137167F;
            target.leftLegX = -1.4137167F;
            target.rightLegY = 0.31415927F;
            target.leftLegY = -0.31415927F;
            target.headYPos = 8.0F;
            target.bodyYPos = 8.0F;
            target.rightArmYPos = 10.0F;
            target.leftArmYPos = 10.0F;
            target.rightLegYPos = 20.0F;
            target.leftLegYPos = 20.0F;
            target.rightLegZ = 4.0F;
            target.leftLegZ = 4.0F;
        } else {
            animState.applyMath(target, ageInTicks, limbSwing, limbSwingAmount, entity.isOnGround());
        }

        // --- 5. COMMUNICATE ABSOLUTE LERPS ---
        float smooth = 0.35F; // Base lerp strength

        smHeadX = Mth.lerp(smooth, smHeadX, target.headX);
        this.head.xRot = smHeadX;
        smHeadY = Mth.lerp(smooth, smHeadY, target.headY);
        this.head.yRot = smHeadY;
        smHeadZ = Mth.lerp(smooth, smHeadZ, target.headZ);
        this.head.zRot = smHeadZ;
        smHeadYPos = Mth.lerp(smooth, smHeadYPos, target.headYPos);
        this.head.y = smHeadYPos;
        smHeadZPos = Mth.lerp(smooth, smHeadZPos, target.headZPos);
        this.head.z = smHeadZPos;
        smBodyX = Mth.lerp(smooth, smBodyX, target.bodyX);
        this.body.xRot = smBodyX;
        smBodyY = Mth.lerp(smooth, smBodyY, target.bodyY);
        this.body.yRot = smBodyY;
        smBodyZ = Mth.lerp(smooth, smBodyZ, target.bodyZ);
        this.body.zRot = smBodyZ;
        smBodyYPos = Mth.lerp(smooth, smBodyYPos, target.bodyYPos);
        this.body.y = smBodyYPos;
        smBodyZPos = Mth.lerp(smooth, smBodyZPos, target.bodyZPos);
        this.body.z = smBodyZPos;
        smRArmX = Mth.lerp(smooth, smRArmX, target.rightArmX);
        this.rightArm.xRot = smRArmX;
        smRArmY = Mth.lerp(smooth, smRArmY, target.rightArmY);
        this.rightArm.yRot = smRArmY;
        smRArmZ = Mth.lerp(smooth, smRArmZ, target.rightArmZ);
        this.rightArm.zRot = smRArmZ;
        smRArmYPos = Mth.lerp(smooth, smRArmYPos, target.rightArmYPos);
        this.rightArm.y = smRArmYPos;
        smRArmZPos = Mth.lerp(smooth, smRArmZPos, target.rightArmZPos);
        this.rightArm.z = smRArmZPos;
        smLArmX = Mth.lerp(smooth, smLArmX, target.leftArmX);
        this.leftArm.xRot = smLArmX;
        smLArmY = Mth.lerp(smooth, smLArmY, target.leftArmY);
        this.leftArm.yRot = smLArmY;
        smLArmZ = Mth.lerp(smooth, smLArmZ, target.leftArmZ);
        this.leftArm.zRot = smLArmZ;
        smLArmYPos = Mth.lerp(smooth, smLArmYPos, target.leftArmYPos);
        this.leftArm.y = smLArmYPos;
        smLArmZPos = Mth.lerp(smooth, smLArmZPos, target.leftArmZPos);
        this.leftArm.z = smLArmZPos;
        smRLegX = Mth.lerp(smooth, smRLegX, target.rightLegX);
        this.rightLeg.xRot = smRLegX;
        smRLegY = Mth.lerp(smooth, smRLegY, target.rightLegY);
        this.rightLeg.yRot = smRLegY;
        smRLegZ = Mth.lerp(smooth, smRLegZ, target.rightLegZ);
        this.rightLeg.zRot = smRLegZ;
        smRLegYPos = Mth.lerp(smooth, smRLegYPos, target.rightLegYPos);
        this.rightLeg.y = smRLegYPos;
        smLLegX = Mth.lerp(smooth, smLLegX, target.leftLegX);
        this.leftLeg.xRot = smLLegX;
        smLLegY = Mth.lerp(smooth, smLLegY, target.leftLegY);
        this.leftLeg.yRot = smLLegY;
        smLLegZ = Mth.lerp(smooth, smLLegZ, target.leftLegZ);
        this.leftLeg.zRot = smLLegZ;
        smLLegYPos = Mth.lerp(smooth, smLLegYPos, target.leftLegYPos);
        this.leftLeg.y = smLLegYPos;

        float rootSm = smooth * 0.5f;
        smRootX = Mth.lerp(rootSm, smRootX, target.rootX);
        this.root.xRot = smRootX;
        smRootY = Mth.lerp(rootSm, smRootY, target.rootY);
        this.root.yRot = smRootY;
        smRootZ = Mth.lerp(rootSm, smRootZ, target.rootZ);
        this.root.zRot = smRootZ;
        smRootXPos = Mth.lerp(rootSm, smRootXPos, target.rootXPos);
        this.root.x = smRootXPos;
        smRootYPos = Mth.lerp(rootSm, smRootYPos, target.rootYPos);
        this.root.y = smRootYPos;
        smRootZPos = Mth.lerp(rootSm, smRootZPos, target.rootZPos);
        this.root.z = smRootZPos;

        // Note: Part z vectors are safely overwritten every tick by the new smZPos assignments,
        // but resetting legs ensures clean procedural compilation.
        this.rightLeg.z = 0.0F;
        this.leftLeg.z = 0.0F;
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void translateToHand(HumanoidArm arm, PoseStack poseStack) {
        this.root().translateAndRotate(poseStack);
        if (arm == HumanoidArm.RIGHT) {
            this.rightArm.translateAndRotate(poseStack);
            // 0.625D is exactly the bottom of the arm's bounding box
            poseStack.translate(0.0D, 0.625D, 0.0D);
        } else {
            this.leftArm.translateAndRotate(poseStack);
            poseStack.translate(0.0D, 0.625D, 0.0D);
        }
    }
}
