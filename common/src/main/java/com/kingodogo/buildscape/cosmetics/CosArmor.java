package com.kingodogo.buildscape.cosmetics;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;

import java.util.Map;

public abstract class CosArmor<T> {

    protected final Map<String, ModelPart> parts;
    protected final CommonId texture;

    public CosArmor(Map<String, ModelPart> parts, CommonId texture) {
        this.parts = parts;
        this.texture = texture;
    }

    public CommonId getTexture() {
        return texture;
    }

    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (parts != null) {
            for (ModelPart part : parts.values()) {
                if (part != null) {
                    Services.PLATFORM.renderModelPart(part, poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
                }
            }
        }
    }

    public Map<String, ModelPart> getParts() {
        return parts;
    }

    public void applyTransform(PoseStack poseStack) {
    }
}
