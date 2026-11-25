package com.kingodogo.buildscape.cosmetics;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;

public abstract class CosHead<T> {

    protected final ModelPart root;
    protected final CommonId texture;

    public CosHead(ModelPart root, CommonId texture) {
        this.root = root;
        this.texture = texture;
    }

    public CommonId getTexture() {
        return texture;
    }

    public ModelPart getRoot() {
        return root;
    }

    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (root != null) {
            Services.PLATFORM.renderModelPart(root, poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        }
    }

    public void applyTransform(PoseStack poseStack) {
    }
}
