package com.kingodogo.buildscape.cosmetics;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;

public abstract class CosLegs<T> {

    protected final ModelPart body;
    protected final ModelPart leftLeg;
    protected final ModelPart rightLeg;
    protected final CommonId texture;

    public CosLegs(ModelPart body, ModelPart leftLeg, ModelPart rightLeg, CommonId texture) {
        this.body = body;
        this.leftLeg = leftLeg;
        this.rightLeg = rightLeg;
        this.texture = texture;
    }

    public CommonId getTexture() {
        return texture;
    }

    public ModelPart getBody() {
        return body;
    }

    public ModelPart getLeftLeg() {
        return leftLeg;
    }

    public ModelPart getRightLeg() {
        return rightLeg;
    }

    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (body != null) Services.PLATFORM.renderModelPart(body, poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        if (leftLeg != null) Services.PLATFORM.renderModelPart(leftLeg, poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        if (rightLeg != null) Services.PLATFORM.renderModelPart(rightLeg, poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public void applyTransform(PoseStack poseStack) {
    }
}
