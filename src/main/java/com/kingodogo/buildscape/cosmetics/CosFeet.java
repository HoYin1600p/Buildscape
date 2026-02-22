package com.kingodogo.buildscape.cosmetics;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

/**
 * Base class for all custom feet (boots) cosmetics.
 */
public abstract class CosFeet<T extends Entity> extends EntityModel<T> {
    
    protected final ModelPart leftFoot;
    protected final ModelPart rightFoot;
    protected final ResourceLocation texture;

    public CosFeet(ModelPart leftFoot, ModelPart rightFoot, ResourceLocation texture) {
        this.leftFoot = leftFoot;
        this.rightFoot = rightFoot;
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
        leftFoot.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        rightFoot.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public void applyTransform(PoseStack poseStack) {
    }
}
