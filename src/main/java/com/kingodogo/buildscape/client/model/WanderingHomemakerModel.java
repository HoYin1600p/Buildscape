package com.kingodogo.buildscape.client.model;

import com.kingodogo.buildscape.entity.WanderingHomemakerEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class WanderingHomemakerModel extends HierarchicalModel<WanderingHomemakerEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(com.kingodogo.buildscape.BuildScape.MODID, "wandering_homemaker"), "main");

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart arms;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public WanderingHomemakerModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.head = this.body.getChild("head");
        this.arms = this.body.getChild("arms");
        this.rightLeg = root.getChild("leg0");
        this.leftLeg = root.getChild("leg1");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // 1. Body and its children (head, arms, backpack, spyglass)
        PartDefinition bodyDef = partdefinition.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(32, 45).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F) // body upper
                .texOffs(0, 11).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 18.0F, 6.0F)  // body lower / robe
                .texOffs(28, 11).addBox(-6.0F, 4.5F, 3.25F, 12.0F, 10.0F, 6.0F) // backpack main
                .texOffs(0, 0).addBox(-7.0F, 0.5F, 3.25F, 14.0F, 4.0F, 7.0F)     // backpack top
                .texOffs(66, 0).addBox(-8.0F, 4.5F, 4.75F, 2.0F, 8.0F, 2.0F)    // spyglass body
                .texOffs(22, 13).addBox(-8.0F, 12.0F, 4.75F, 2.0F, 1.0F, 2.0F)  // spyglass lens
                , PartPose.offset(0.0F, 0.0F, 0.0F));

        // 2. Head and its children (nose, hat, brim/cube)
        PartDefinition headDef = bodyDef.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(28, 27).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F) // head
                .texOffs(0, 45).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)) // hat
                , PartPose.offset(0.0F, 0.0F, 0.0F));

        headDef.addOrReplaceChild("nose", CubeListBuilder.create()
                .texOffs(60, 55).addBox(-1.0F, -1.0F, -3.0F, 2.0F, 4.0F, 2.0F)
                , PartPose.offsetAndRotation(0.0F, -2.0F, -3.0F, 1.3090F, 0.0F, 0.0F));

        headDef.addOrReplaceChild("brim", CubeListBuilder.create()
                .texOffs(0, 35).addBox(-4.0F, -2.0F, -4.25F, 8.0F, 3.0F, 6.0F)
                , PartPose.offsetAndRotation(0.0F, 0.1534F, -2.4870F, -0.6981F, 0.0F, 0.0F));

        // 3. Arms (child of body)
        bodyDef.addOrReplaceChild("arms", CubeListBuilder.create()
                .texOffs(42, 0).addBox(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F)     // arms center
                .texOffs(60, 43).addBox(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F)     // right arm
                .texOffs(60, 43).addBox(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F)    // left arm
                , PartPose.offsetAndRotation(0.0F, 3.0F, -1.0F, 0.75F, 0.0F, 0.0F));

        // 4. Legs (root children)
        partdefinition.addOrReplaceChild("leg0", CubeListBuilder.create()
                .texOffs(60, 27).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F)
                , PartPose.offset(2.0F, 12.0F, 0.0F));

        partdefinition.addOrReplaceChild("leg1", CubeListBuilder.create()
                .texOffs(60, 27).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F)
                , PartPose.offset(-2.0F, 12.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(WanderingHomemakerEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = headPitch * ((float)Math.PI / 180F);
        this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
        this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount * 0.5F;
        this.rightLeg.yRot = 0.0F;
        this.leftLeg.yRot = 0.0F;
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}
