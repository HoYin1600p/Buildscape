package com.kingodogo.buildscape.client.model;

import com.kingodogo.buildscape.BuildScape;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

/**
 * Model for the Builder's Hat cosmetic.
 * Created from Blockbench export.
 */
public class BuildersHatModel<T extends Entity> extends EntityModel<T> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation(BuildScape.MODID, "builders_hat"), "main");
    
    private final ModelPart Head;
    private final ModelPart bone;

    public BuildersHatModel(ModelPart root) {
        this.Head = root.getChild("Head");
        this.bone = this.Head.getChild("bone");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition bone = Head.addOrReplaceChild("bone", 
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-14.0F, -5.0F, 0.0F, 11.0F, 0.0F, 16.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 17).addBox(-13.0F, -10.0F, 5.0F, 9.0F, 5.0F, 9.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 32).addBox(-10.0F, -11.0F, 4.0F, 3.0F, 6.0F, 11.0F, new CubeDeformation(0.0F)), 
                PartPose.offset(8.5F, -1.0F, -9.75F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // No animation needed for static hat
    }

    @Override
    public void renderToBuffer(com.mojang.blaze3d.vertex.PoseStack poseStack, 
                              com.mojang.blaze3d.vertex.VertexConsumer vertexConsumer, 
                              int packedLight, int packedOverlay, 
                              float red, float green, float blue, float alpha) {
        Head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}

