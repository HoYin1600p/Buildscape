package com.kingodogo.buildscape.client.model;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.cosmetics.CosHead;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import com.mojang.blaze3d.vertex.PoseStack;

/**
 * Model for the Builder's Hat cosmetic.
 * Updated with new Blockbench coordinates (5.0.7).
 */
public class BuildersHatModel<T extends Entity> extends CosHead<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation(BuildScape.MODID, "builders_hat"), "main");
    
    public BuildersHatModel(ModelPart root) {
        super(root.getChild("Head"), new ResourceLocation(BuildScape.MODID, "textures/cosmatics/builders_hat.png"));
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        Head.addOrReplaceChild("bone", 
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-14.0F, -4.99F, 0.0F, 11.0F, 0.1F, 16.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 17).addBox(-13.0F, -10.0F, 5.0F, 9.0F, 5.0F, 9.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 32).addBox(-10.0F, -11.0F, 4.0F, 3.0F, 6.0F, 11.0F, new CubeDeformation(0.0F)), 
                PartPose.offset(8.5F, -1.0F, -9.75F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void applyTransform(PoseStack poseStack) {
        // No extra transforms needed - the model geometry and bone offset
        // from Blockbench already position it correctly on top of the head
    }
}
