package com.kingodogo.buildscape.cosmetics;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.Map;

public abstract class CosArmor<T extends Entity> extends EntityModel<T> {

    protected final Map<String, ModelPart> parts;
    protected final ResourceLocation texture;

    public CosArmor(Map<String, ModelPart> parts, ResourceLocation texture) {
        this.parts = parts;
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
        for (ModelPart part : parts.values()) {
            part.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        }
    }

    public Map<String, ModelPart> getParts() {
        return parts;
    }


    public void applyTransform(PoseStack poseStack) {
    }
}
