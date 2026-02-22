package com.kingodogo.buildscape.cosmetics;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

/**
 * Base class for all custom head cosmetics.
 * Extend this class and register it in CosmeticManager to add new custom head models.
 */
public abstract class CosHead<T extends Entity> extends EntityModel<T> {
    
    protected final ModelPart root;
    protected final ResourceLocation texture;

    public CosHead(ModelPart root, ResourceLocation texture) {
        this.root = root;
        this.texture = texture;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // Default: static cosmetic
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    /**
     * Optional: override this to apply custom transformations before rendering.
     */
    public void applyTransform(PoseStack poseStack) {
        // Default: no transform
    }
}
