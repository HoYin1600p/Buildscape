package com.kingodogo.buildscape.cosmetics;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;

public abstract class CosChest<T> {

    protected final ModelPart body;
    protected final ModelPart leftArm;
    protected final ModelPart rightArm;
    protected final CommonId texture;

    public CosChest(ModelPart body, ModelPart leftArm, ModelPart rightArm, CommonId texture) {
        this.body = body;
        this.leftArm = leftArm;
        this.rightArm = rightArm;
        this.texture = texture;
    }

    public CommonId getTexture() {
        return texture;
    }

    public ModelPart getBody() {
        return body;
    }

    public ModelPart getLeftArm() {
        return leftArm;
    }

    public ModelPart getRightArm() {
        return rightArm;
    }

    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (body != null) Services.PLATFORM.renderModelPart(body, poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        if (leftArm != null) Services.PLATFORM.renderModelPart(leftArm, poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        if (rightArm != null) Services.PLATFORM.renderModelPart(rightArm, poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public void applyTransform(PoseStack poseStack) {
    }
}
