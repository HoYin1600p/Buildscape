package com.kingodogo.buildscape.cosmetics;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

/**
 * Base class for all custom chest cosmetics.
 */
public abstract class CosChest<T extends Entity> extends EntityModel<T> {
    
    protected final ModelPart body;
    protected final ModelPart leftArm;
    protected final ModelPart rightArm;
    protected final ResourceLocation texture;

    public CosChest(ModelPart body, ModelPart leftArm, ModelPart rightArm, ResourceLocation texture) {
        this.body = body;
        this.leftArm = leftArm;
        this.rightArm = rightArm;
        this.texture = texture;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        // Body
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        // Arms
        leftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        rightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public void applyTransform(PoseStack poseStack) {
    }
}
