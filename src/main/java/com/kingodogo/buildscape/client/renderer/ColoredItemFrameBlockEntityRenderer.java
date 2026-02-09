package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.block.ColoredItemFrameBlock;
import com.kingodogo.buildscape.block.ColoredItemFrameBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class ColoredItemFrameBlockEntityRenderer
                implements BlockEntityRenderer<ColoredItemFrameBlockEntity> {

        public ColoredItemFrameBlockEntityRenderer(
                        BlockEntityRendererProvider.Context context) {
        }

        private ResourceLocation getTextureForColor(String color) {
                return new ResourceLocation(
                                BuildScape.MODID,
                                "textures/block/" + color + "_item_frame.png");
        }

        @Override
        public void render(
                        ColoredItemFrameBlockEntity blockEntity,
                        float partialTicks,
                        PoseStack poseStack,
                        MultiBufferSource buffer,
                        int packedLight,
                        int packedOverlay) {
                if (blockEntity == null || blockEntity.getLevel() == null) {
                        return;
                }

                BlockState state = blockEntity.getBlockState();
                if (!(state.getBlock() instanceof ColoredItemFrameBlock block)) {
                        return;
                }

            Direction facing = state.getValue(ColoredItemFrameBlock.FACING);
                String colorName = block.getColor().getSerializedName();

                renderFrame(poseStack, buffer, packedLight, facing, colorName);

                ItemStack displayedItem = blockEntity.getDisplayedItem();
                if (!displayedItem.isEmpty()) {
                        renderItem(
                                        poseStack, buffer, packedLight, facing,
                                        displayedItem, blockEntity.getRotation());
                }
        }

        private void renderFrame(
                        PoseStack poseStack,
                        MultiBufferSource buffer,
                        int packedLight,
                        Direction facing,
                        String colorName) {
                poseStack.pushPose();

                ResourceLocation texture = getTextureForColor(colorName);
                VertexConsumer vertexConsumer = buffer.getBuffer(
                                RenderType.entityCutoutNoCull(texture));

                float rotationY = 0.0F;
                double posX = 0.5D;
                double posY = 0.5D;
                double posZ;
                double forwardOffset = 0.01D;

                switch (facing) {
                        case NORTH:
                                posZ = 1.0D - forwardOffset;
                                rotationY = 180.0F;
                                break;
                        case SOUTH:
                                posZ = forwardOffset;
                                rotationY = 0.0F;
                                break;
                        case WEST:
                                posX = 1.0D - forwardOffset;
                                posZ = 0.5D;
                                rotationY = 270.0F; // Fixed rotation
                                break;
                        case EAST:
                                posX = forwardOffset;
                                posZ = 0.5D;
                                rotationY = 90.0F; // Fixed rotation
                                break;
                        default:
                                posZ = 1.0D - forwardOffset;
                                rotationY = 180.0F;
                                break;
                }

                poseStack.translate(posX, posY, posZ);
                poseStack.mulPose(Vector3f.YP.rotationDegrees(rotationY));

                float halfSize = 0.375F;
                float thickness = 0.0625F;

                Matrix4f pose = poseStack.last().pose();
                Matrix3f normal = poseStack.last().normal();

                // Front Face (Z = thickness)
                vertexConsumer.vertex(pose, -halfSize, -halfSize, thickness).color(255, 255, 255, 255).uv(0.0F, 1.0F)
                                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight)
                                .normal(normal, 0.0F, 0.0F, 1.0F).endVertex();
                vertexConsumer.vertex(pose, halfSize, -halfSize, thickness).color(255, 255, 255, 255).uv(1.0F, 1.0F)
                                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight)
                                .normal(normal, 0.0F, 0.0F, 1.0F).endVertex();
                vertexConsumer.vertex(pose, halfSize, halfSize, thickness).color(255, 255, 255, 255).uv(1.0F, 0.0F)
                                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight)
                                .normal(normal, 0.0F, 0.0F, 1.0F).endVertex();
                vertexConsumer.vertex(pose, -halfSize, halfSize, thickness).color(255, 255, 255, 255).uv(0.0F, 0.0F)
                                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight)
                                .normal(normal, 0.0F, 0.0F, 1.0F).endVertex();

                // Back Face (Z = 0)
                vertexConsumer.vertex(pose, -halfSize, halfSize, 0.0F).color(255, 255, 255, 255).uv(0.0F, 0.0F)
                                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight)
                                .normal(normal, 0.0F, 0.0F, -1.0F).endVertex();
                vertexConsumer.vertex(pose, halfSize, halfSize, 0.0F).color(255, 255, 255, 255).uv(1.0F, 0.0F)
                                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight)
                                .normal(normal, 0.0F, 0.0F, -1.0F).endVertex();
                vertexConsumer.vertex(pose, halfSize, -halfSize, 0.0F).color(255, 255, 255, 255).uv(1.0F, 1.0F)
                                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight)
                                .normal(normal, 0.0F, 0.0F, -1.0F).endVertex();
                vertexConsumer.vertex(pose, -halfSize, -halfSize, 0.0F).color(255, 255, 255, 255).uv(0.0F, 1.0F)
                                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight)
                                .normal(normal, 0.0F, 0.0F, -1.0F).endVertex();

                // Top Face
                vertexConsumer.vertex(pose, -halfSize, halfSize, 0.0F).color(255, 255, 255, 255).uv(0.0F, 0.0F)
                                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight)
                                .normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
                vertexConsumer.vertex(pose, -halfSize, halfSize, thickness).color(255, 255, 255, 255).uv(0.0F, 0.0625F)
                                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight)
                                .normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
                vertexConsumer.vertex(pose, halfSize, halfSize, thickness).color(255, 255, 255, 255).uv(1.0F, 0.0625F)
                                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight)
                                .normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
                vertexConsumer.vertex(pose, halfSize, halfSize, 0.0F).color(255, 255, 255, 255).uv(1.0F, 0.0F)
                                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight)
                                .normal(normal, 0.0F, 1.0F, 0.0F).endVertex();

                // Bottom Face
                vertexConsumer.vertex(pose, -halfSize, -halfSize, thickness).color(255, 255, 255, 255).uv(0.0F, 0.9375F)
                                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight)
                                .normal(normal, 0.0F, -1.0F, 0.0F)
                                .endVertex();
                vertexConsumer.vertex(pose, -halfSize, -halfSize, 0.0F).color(255, 255, 255, 255).uv(0.0F, 1.0F)
                                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight)
                                .normal(normal, 0.0F, -1.0F, 0.0F)
                                .endVertex();
                vertexConsumer.vertex(pose, halfSize, -halfSize, 0.0F).color(255, 255, 255, 255).uv(1.0F, 1.0F)
                                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight)
                                .normal(normal, 0.0F, -1.0F, 0.0F)
                                .endVertex();
                vertexConsumer.vertex(pose, halfSize, -halfSize, thickness).color(255, 255, 255, 255).uv(1.0F, 0.9375F)
                                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight)
                                .normal(normal, 0.0F, -1.0F, 0.0F)
                                .endVertex();

                // Left Face
                vertexConsumer.vertex(pose, -halfSize, -halfSize, 0.0F).color(255, 255, 255, 255).uv(0.0F, 1.0F)
                                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight)
                                .normal(normal, -1.0F, 0.0F, 0.0F)
                                .endVertex();
                vertexConsumer.vertex(pose, -halfSize, -halfSize, thickness).color(255, 255, 255, 255).uv(0.0625F, 1.0F)
                                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight)
                                .normal(normal, -1.0F, 0.0F, 0.0F)
                                .endVertex();
                vertexConsumer.vertex(pose, -halfSize, halfSize, thickness).color(255, 255, 255, 255).uv(0.0625F, 0.0F)
                                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight)
                                .normal(normal, -1.0F, 0.0F, 0.0F)
                                .endVertex();
                vertexConsumer.vertex(pose, -halfSize, halfSize, 0.0F).color(255, 255, 255, 255).uv(0.0F, 0.0F)
                                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight)
                                .normal(normal, -1.0F, 0.0F, 0.0F)
                                .endVertex();

                // Right Face
                vertexConsumer.vertex(pose, halfSize, -halfSize, thickness).color(255, 255, 255, 255).uv(0.9375F, 1.0F)
                                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight)
                                .normal(normal, 1.0F, 0.0F, 0.0F).endVertex();
                vertexConsumer.vertex(pose, halfSize, -halfSize, 0.0F).color(255, 255, 255, 255).uv(1.0F, 1.0F)
                                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight)
                                .normal(normal, 1.0F, 0.0F, 0.0F).endVertex();
                vertexConsumer.vertex(pose, halfSize, halfSize, 0.0F).color(255, 255, 255, 255).uv(1.0F, 0.0F)
                                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight)
                                .normal(normal, 1.0F, 0.0F, 0.0F).endVertex();
                vertexConsumer.vertex(pose, halfSize, halfSize, thickness).color(255, 255, 255, 255).uv(0.9375F, 0.0F)
                                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight)
                                .normal(normal, 1.0F, 0.0F, 0.0F).endVertex();

                poseStack.popPose();
        }

        private void renderItem(
                        PoseStack poseStack,
                        MultiBufferSource buffer,
                        int packedLight,
                        Direction facing,
                        ItemStack item,
                        int rotation) {
                poseStack.pushPose();

                double posX = 0.5D;
                double posY = 0.5D;
                double posZ;
                float rotationY = 0.0F;
                double itemOffset = 0.0125D;

                switch (facing) {
                        case NORTH:
                                posZ = 1.0D - itemOffset;
                                rotationY = 180.0F;
                                break;
                        case SOUTH:
                                posZ = itemOffset;
                                rotationY = 0.0F;
                                break;
                        case WEST:
                                posX = 1.0D - itemOffset;
                                posZ = 0.5D;
                                rotationY = 90.0F;
                                break;
                        case EAST:
                                posX = itemOffset;
                                posZ = 0.5D;
                                rotationY = 270.0F;
                                break;
                        default:
                                posZ = 1.0D - itemOffset;
                                rotationY = 180.0F;
                                break;
                }

                poseStack.translate(posX, posY, posZ);
                poseStack.mulPose(Vector3f.YP.rotationDegrees(rotationY));
                poseStack.mulPose(Vector3f.ZP.rotationDegrees(rotation * -45.0F));
                poseStack.scale(0.375F, 0.375F, 0.375F);

                ItemRenderer itemRenderer = Minecraft.getInstance()
                                .getItemRenderer();
                itemRenderer.renderStatic(
                                item,
                                ItemTransforms.TransformType.FIXED,
                                packedLight,
                                OverlayTexture.NO_OVERLAY,
                                poseStack,
                                buffer,
                                0);

                poseStack.popPose();
        }
}
