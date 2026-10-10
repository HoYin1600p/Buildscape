package com.kingodogo.buildscape.cosmetics;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;

public abstract class CosFeet<T> {

    protected final ModelPart leftFoot;
    protected final ModelPart rightFoot;
    protected final CommonId texture;

    public CosFeet(ModelPart leftFoot, ModelPart rightFoot, CommonId texture) {
        this.leftFoot = leftFoot;
        this.rightFoot = rightFoot;
        this.texture = texture;
    }

    public CommonId getTexture() {
        return texture;
    }

    public ModelPart getLeftFoot() {
        return leftFoot;
    }

    public ModelPart getRightFoot() {
        return rightFoot;
    }

    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (leftFoot != null) {
            Services.PLATFORM.renderModelPart(leftFoot, poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        }
        if (rightFoot != null) {
            Services.PLATFORM.renderModelPart(rightFoot, poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        }
    }

    public void applyTransform(PoseStack poseStack) {
    }
}
