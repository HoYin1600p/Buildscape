package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.block.FestiveStockingBlock;
import com.kingodogo.buildscape.block.FestiveStockingBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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
        implements BlockEntityRenderer<FestiveStockingBlockEntity> {

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

    public FestiveStockingBlockEntityRenderer(
            BlockEntityRendererProvider.Context context
    ) {
    }

    @Override
    public void render(
            FestiveStockingBlockEntity blockEntity,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            int packedOverlay
    ) {
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
        double posX = 0.5D;
        double posY = 0.5D;
        double posZ = 0.0D;
        double forwardOffset = 0.001D;

        switch (facing) {
            case NORTH:
                posX = 0.5D;
                posY = 0.5D;
                posZ = 1.0D - forwardOffset;
                rotationY = 180.0F;
                break;
            case SOUTH:
                posX = 0.5D;
                posY = 0.5D;
                posZ = forwardOffset;
                rotationY = 0.0F;
                break;
            case WEST:
                posX = 1.0D - forwardOffset;
                posY = 0.5D;
                posZ = 0.5D;
                rotationY = 90.0F;
                break;
            case EAST:
                posX = forwardOffset;
                posY = 0.5D;
                posZ = 0.5D;
                rotationY = 270.0F;
                break;
            case UP:
                posX = 0.5D;
                posY = 0.5D / 16.0D;
                posZ = 0.5D;
                rotationX = -90.0F;
                rotationY = 180.0F;
                break;
            case DOWN:
                posX = 0.5D;
                posY = 15.5D / 16.0D;
                posZ = 0.5D;
                rotationX = 90.0F;
                rotationY = 180.0F;
                break;
        }

        poseStack.translate(posX, posY, posZ);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(rotationY));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(rotationX));

        ResourceLocation texture = getTextureForColor(block.getColorVariant());
        VertexConsumer vertexConsumer = buffer.getBuffer(
                RenderType.entityCutoutNoCull(texture)
        );

        float width = 0.5F;
        float height = 0.5F;
        float uMin = flipped ? 1.0F : 0.0F;
        float uMax = flipped ? 0.0F : 1.0F;
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
    }
}
