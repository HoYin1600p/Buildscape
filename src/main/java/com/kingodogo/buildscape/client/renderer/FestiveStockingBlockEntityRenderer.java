package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.block.FestiveStockingBlock;
import com.kingodogo.buildscape.block.FestiveStockingBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class FestiveStockingBlockEntityRenderer
        implements
            BlockEntityRenderer<FestiveStockingBlockEntity> {

    private static final ResourceLocation STOCKING_TEXTURE = new ResourceLocation(
            BuildScape.MODID,
            "textures/entity/festive_stocking.png");

    private ResourceLocation getTextureForColor(String color) {
        if (color == null || color.equals("festive")) {
            return STOCKING_TEXTURE;
        }
        return new ResourceLocation(
                BuildScape.MODID,
                "textures/entity/" + color + "_festive_stocking.png");
    }

    public FestiveStockingBlockEntityRenderer(
            BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(
            FestiveStockingBlockEntity blockEntity,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            int packedOverlay) {
        if (blockEntity == null || blockEntity.getLevel() == null) {
            return;
        }

        BlockState state = blockEntity.getBlockState();
        if (!(state.getBlock() instanceof FestiveStockingBlock)) {
            return;
        }

        FestiveStockingBlock block = (FestiveStockingBlock) state.getBlock();
        Direction facing = state.getValue(FestiveStockingBlock.FACING);
        boolean flipped = state.getValue(FestiveStockingBlock.FLIPPED);

        poseStack.pushPose();

        float rotationY = 0.0F;
        float rotationX = 0.0F;
        double posX = 0.5;
        double posY = 0.5;
        double posZ = 0.0;
        double forwardOffset = 0.001;

        switch (facing) {
            case NORTH -> {
                posX = 0.5;
                posY = 0.5;
                posZ = 1.0 - forwardOffset;
                rotationY = 180.0F;
            }
            case SOUTH -> {
                posX = 0.5;
                posY = 0.5;
                posZ = forwardOffset;
                rotationY = 0.0F;
            }
            case WEST -> {
                posX = 1.0 - forwardOffset;
                posY = 0.5;
                posZ = 0.5;
                rotationY = 90.0F;
            }
            case EAST -> {
                posX = forwardOffset;
                posY = 0.5;
                posZ = 0.5;
                rotationY = 270.0F;
            }
            case UP -> {
                posX = 0.5;
                posY = 0.5 / 16.0;
                posZ = 0.5;
                rotationX = -90.0F;
                rotationY = 180.0F;
            }
            case DOWN -> {
                posX = 0.5;
                posY = 15.5 / 16.0;
                posZ = 0.5;
                rotationX = 90.0F;
                rotationY = 180.0F;
            }
        }

        poseStack.translate(posX, posY, posZ);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(rotationY));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(rotationX));

        ResourceLocation texture = getTextureForColor(block.getColorVariant());
        VertexConsumer vertexConsumer = buffer.getBuffer(
                RenderType.entityCutoutNoCull(texture));

        float width = 0.5F;
        float height = 0.5F;
        float uMin = flipped ? 1.0F : 0.0F;
        float uMax = flipped ? 0.0F : 1.0F;
        float vMin = 0.0F;
        float vMax = 1.0F;

        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();

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
    }
}
