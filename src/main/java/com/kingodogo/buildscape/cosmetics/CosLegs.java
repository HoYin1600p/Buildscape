package com.kingodogo.buildscape.cosmetics;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

/**
 * Base class for all custom legs cosmetics.
 */
public abstract class CosLegs<T extends Entity> extends EntityModel<T> {
    
    protected final ModelPart body; // Waist/Belt area
    protected final ModelPart leftLeg;
    protected final ModelPart rightLeg;
    protected final ResourceLocation texture;

    public CosLegs(ModelPart body, ModelPart leftLeg, ModelPart rightLeg, ResourceLocation texture) {
        this.body = body;
        this.leftLeg = leftLeg;
        this.rightLeg = rightLeg;
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
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        leftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        rightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public void applyTransform(PoseStack poseStack) {
    }
}
