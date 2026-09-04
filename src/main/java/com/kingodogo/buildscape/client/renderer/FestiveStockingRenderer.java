package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.entity.FestiveStockingEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class FestiveStockingRenderer
        extends EntityRenderer<FestiveStockingEntity> {

    private static final ResourceLocation STOCKING_TEXTURE = new ResourceLocation(
            BuildScape.MODID,
            "textures/entity/festive_stocking.png"
    );

    private ResourceLocation getTextureForColor(String color) {
        if (color == null || color.equals("festive")) {
            return STOCKING_TEXTURE;
        }
        return new ResourceLocation(
                BuildScape.MODID,
                "textures/entity/" + color + "_festive_stocking.png"
        );
    }

    public FestiveStockingRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.25F;
    }

    @Override
    public void render(
            FestiveStockingEntity entity,
            float entityYaw,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight
    ) {
        Direction direction = entity.getDirection();
        if (direction == null) {
            return;
        }

        poseStack.pushPose();

        float rotationY = 0.0F;
        float rotationX = 0.0F;

        if (direction == Direction.SOUTH) {
            rotationY = 180.0F;
        } else if (direction == Direction.WEST) {
            rotationY = 90.0F;
        } else if (direction == Direction.EAST) {
            rotationY = 270.0F;
        } else if (direction == Direction.UP) {
            rotationX = -90.0F;
            rotationY = 180.0F;
        } else if (direction == Direction.DOWN) {
            rotationX = 90.0F;
            rotationY = 180.0F;
        }

        poseStack.translate(0.0D, 0.0D, 0.0D);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(rotationY));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(rotationX));
        poseStack.translate(0.0D, 0.0D, 0.0625D);

        ResourceLocation texture = getTextureForColor(entity.getColorVariant());
        VertexConsumer vertexConsumer = buffer.getBuffer(
                RenderType.entityCutoutNoCull(texture)
        );

        float width = 0.5F;
        float height = 0.5F;

        float uMin = 0.0F;
        float uMax = 1.0F;
        float vMin = 0.0F;
        float vMax = 1.0F;

        com.mojang.math.Matrix4f pose = poseStack.last().pose();
        com.mojang.math.Matrix3f normal = poseStack.last().normal();

        vertexConsumer
                .vertex(pose, -width, -height, 0.0F)
                .color(255, 255, 255, 255)
                .uv(uMin, vMax)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(normal, 0.0F, 0.0F, 1.0F)
                .endVertex();
        vertexConsumer
                .vertex(pose, width, -height, 0.0F)
                .color(255, 255, 255, 255)
                .uv(uMax, vMax)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(normal, 0.0F, 0.0F, 1.0F)
                .endVertex();
        vertexConsumer
                .vertex(pose, width, height, 0.0F)
                .color(255, 255, 255, 255)
                .uv(uMax, vMin)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(normal, 0.0F, 0.0F, 1.0F)
                .endVertex();
        vertexConsumer
                .vertex(pose, -width, height, 0.0F)
                .color(255, 255, 255, 255)
                .uv(uMin, vMin)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(normal, 0.0F, 0.0F, 1.0F)
                .endVertex();

        poseStack.popPose();
        super.render(
                entity,
                entityYaw,
                partialTicks,
                poseStack,
                buffer,
                packedLight
        );
    }

    @Override
    public ResourceLocation getTextureLocation(FestiveStockingEntity entity) {
        return getTextureForColor(entity.getColorVariant());
    }
}
