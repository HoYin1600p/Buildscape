package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.entity.ColoredItemFrameEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class ColoredItemFrameRenderer extends EntityRenderer<ColoredItemFrameEntity> {

    private static final ResourceLocation BIRCH_PLANKS = new ResourceLocation("minecraft", "textures/block/birch_planks.png");
    private static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation(BuildScape.MODID, "textures/entity/white_item_frame.png");

    public ColoredItemFrameRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    private ResourceLocation getTextureForColor(String color) {
        if (color == null || color.isEmpty()) {
            return DEFAULT_TEXTURE;
        }
        return new ResourceLocation(BuildScape.MODID, "textures/entity/" + color + "_item_frame.png");
    }

    @Override
    public void render(ColoredItemFrameEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        Direction direction = entity.getDirection();
        if (direction == null) {
            return;
        }

        poseStack.pushPose();

        // Rotate based on facing direction FIRST
        switch (direction) {
            case SOUTH:
                poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                break;
            case WEST:
                poseStack.mulPose(Vector3f.YP.rotationDegrees(90.0F));
                break;
            case NORTH:
                // No rotation needed
                break;
            case EAST:
                poseStack.mulPose(Vector3f.YP.rotationDegrees(270.0F));
                break;
            case UP:
                poseStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
                break;
            case DOWN:
                poseStack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
                break;
        }

        // Check if item is a map
        ItemStack itemStack = entity.getItem();
        boolean hasMap = !itemStack.isEmpty() && MapItem.getSavedData(itemStack, entity.level) != null;

        // Translate so the frame's back is against the wall
        // Adjusted to sit flush against surfaces
        float zOffset = hasMap ? -px(16F) : -px(16F);
        poseStack.translate(0.0D, 0.0D, zOffset);

        // Render the frame
        ResourceLocation backTexture = getTextureForColor(entity.getColorVariant());

        if (hasMap) {
            renderMapFrame(poseStack, buffer, packedLight, backTexture);
        } else {
            renderNormalFrame(poseStack, buffer, packedLight, backTexture);
        }

        // Render the item if present
        if (!itemStack.isEmpty()) {
            renderItem(entity, itemStack, poseStack, buffer, packedLight, hasMap);
        }

        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private void renderNormalFrame(PoseStack poseStack, MultiBufferSource buffer, int packedLight, ResourceLocation backTexture) {
        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();

        // Based on template_item_frame.json - coordinates converted from 0-16 to -0.5 to 0.5 centered
        // Back panel: from [3, 3, 15.5] to [13, 13, 16]
        float backZ1 = px(15.5F);  // 0.46875
        float backZ2 = px(16F);    // 0.5

        // Frame border z positions: from 15 to 16
        float frameZ1 = px(15F);   // 0.4375
        float frameZ2 = px(16F);   // 0.5

        // Render back panel with colored texture
        VertexConsumer backConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(backTexture));

        // Back panel - visible from front (north face when frame faces north)
        // Quad vertices: bottom-left, bottom-right, top-right, top-left
        renderQuadWithUV(backConsumer, pose, normal, packedLight,
                px(3), px(3), backZ1,
                px(13), px(3), backZ1,
                px(13), px(13), backZ1,
                px(3), px(13), backZ1,
                3 / 16F, 13 / 16F, 13 / 16F, 3 / 16F,
                0, 0, -1);

        // Back panel - south face (back side)
        renderQuadWithUV(backConsumer, pose, normal, packedLight,
                px(13), px(3), backZ2,
                px(3), px(3), backZ2,
                px(3), px(13), backZ2,
                px(13), px(13), backZ2,
                3 / 16F, 13 / 16F, 13 / 16F, 3 / 16F,
                0, 0, 1);

        // Render frame border with birch planks
        VertexConsumer frameConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(BIRCH_PLANKS));

        // Bottom border: from [2, 2, 15] to [14, 3, 16]
        renderBottomBorder(frameConsumer, pose, normal, packedLight, frameZ1, frameZ2, false);

        // Top border: from [2, 13, 15] to [14, 14, 16]
        renderTopBorder(frameConsumer, pose, normal, packedLight, frameZ1, frameZ2, false);

        // Left border: from [2, 3, 15] to [3, 13, 16]
        renderLeftBorder(frameConsumer, pose, normal, packedLight, frameZ1, frameZ2, false);

        // Right border: from [13, 3, 15] to [14, 13, 16]
        renderRightBorder(frameConsumer, pose, normal, packedLight, frameZ1, frameZ2, false);
    }

    private void renderMapFrame(PoseStack poseStack, MultiBufferSource buffer, int packedLight, ResourceLocation backTexture) {
        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();

        // Based on template_item_frame_map.json
        // Back panel: from [1, 1, 15.001] to [15, 15, 16]
        float backZ1 = px(15.001F);
        float backZ2 = px(16F);

        // Frame border: from [0, 0, 15.001] to [16, 16, 16]
        float frameZ1 = px(15.001F);
        float frameZ2 = px(16F);

        // Render back panel with colored texture
        VertexConsumer backConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(backTexture));

        // Back panel - front face
        renderQuadWithUV(backConsumer, pose, normal, packedLight,
                px(1), px(1), backZ1,
                px(15), px(1), backZ1,
                px(15), px(15), backZ1,
                px(1), px(15), backZ1,
                1 / 16F, 15 / 16F, 15 / 16F, 1 / 16F,
                0, 0, -1);

        // Back panel - back face
        renderQuadWithUV(backConsumer, pose, normal, packedLight,
                px(15), px(1), backZ2,
                px(1), px(1), backZ2,
                px(1), px(15), backZ2,
                px(15), px(15), backZ2,
                1 / 16F, 15 / 16F, 15 / 16F, 1 / 16F,
                0, 0, 1);

        // Render frame border with birch planks
        VertexConsumer frameConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(BIRCH_PLANKS));

        // Bottom border: from [0, 0, 15.001] to [16, 1, 16]
        renderBottomBorder(frameConsumer, pose, normal, packedLight, frameZ1, frameZ2, true);

        // Top border: from [0, 15, 15.001] to [16, 16, 16]
        renderTopBorder(frameConsumer, pose, normal, packedLight, frameZ1, frameZ2, true);

        // Left border: from [0, 1, 15.001] to [1, 15, 16]
        renderLeftBorder(frameConsumer, pose, normal, packedLight, frameZ1, frameZ2, true);

        // Right border: from [15, 1, 15.001] to [16, 15, 16]
        renderRightBorder(frameConsumer, pose, normal, packedLight, frameZ1, frameZ2, true);
    }

    // Convert pixel coordinate (0-16) to world coordinate (-0.5 to 0.5)
    private float px(float pixel) {
        return -0.5F + pixel / 16F;
    }

    private void renderBottomBorder(VertexConsumer consumer, Matrix4f pose, Matrix3f normal, int packedLight,
                                    float z1, float z2, boolean mapMode) {
        float x1, x2, y1, y2;
        if (mapMode) {
            x1 = px(0);
            x2 = px(16);
            y1 = px(0);
            y2 = px(1);
        } else {
            x1 = px(2);
            x2 = px(14);
            y1 = px(2);
            y2 = px(3);
        }

        // Down face
        renderQuadWithUV(consumer, pose, normal, packedLight,
                x1, y1, z2, x2, y1, z2, x2, y1, z1, x1, y1, z1,
                mapMode ? 0 / 16F : 2 / 16F, mapMode ? 0 / 16F : 0 / 16F,
                mapMode ? 16 / 16F : 14 / 16F, mapMode ? 1 / 16F : 1 / 16F,
                0, -1, 0);
        // Up face
        renderQuadWithUV(consumer, pose, normal, packedLight,
                x1, y2, z1, x2, y2, z1, x2, y2, z2, x1, y2, z2,
                mapMode ? 0 / 16F : 2 / 16F, mapMode ? 15 / 16F : 15 / 16F,
                mapMode ? 16 / 16F : 14 / 16F, mapMode ? 16 / 16F : 16 / 16F,
                0, 1, 0);
        // North face (front)
        renderQuadWithUV(consumer, pose, normal, packedLight,
                x2, y1, z1, x1, y1, z1, x1, y2, z1, x2, y2, z1,
                mapMode ? 0 / 16F : 2 / 16F, mapMode ? 15 / 16F : 13 / 16F,
                mapMode ? 16 / 16F : 14 / 16F, mapMode ? 16 / 16F : 14 / 16F,
                0, 0, -1);
        // South face (back)
        renderQuadWithUV(consumer, pose, normal, packedLight,
                x1, y1, z2, x2, y1, z2, x2, y2, z2, x1, y2, z2,
                mapMode ? 0 / 16F : 2 / 16F, mapMode ? 15 / 16F : 13 / 16F,
                mapMode ? 16 / 16F : 14 / 16F, mapMode ? 16 / 16F : 14 / 16F,
                0, 0, 1);
        // West face
        renderQuadWithUV(consumer, pose, normal, packedLight,
                x1, y1, z1, x1, y1, z2, x1, y2, z2, x1, y2, z1,
                mapMode ? 15 / 16F : 15 / 16F, mapMode ? 15 / 16F : 13 / 16F,
                mapMode ? 16 / 16F : 16 / 16F, mapMode ? 16 / 16F : 14 / 16F,
                -1, 0, 0);
        // East face
        renderQuadWithUV(consumer, pose, normal, packedLight,
                x2, y1, z2, x2, y1, z1, x2, y2, z1, x2, y2, z2,
                mapMode ? 0 / 16F : 0 / 16F, mapMode ? 15 / 16F : 13 / 16F,
                mapMode ? 1 / 16F : 1 / 16F, mapMode ? 16 / 16F : 14 / 16F,
                1, 0, 0);
    }

    private void renderTopBorder(VertexConsumer consumer, Matrix4f pose, Matrix3f normal, int packedLight,
                                 float z1, float z2, boolean mapMode) {
        float x1, x2, y1, y2;
        if (mapMode) {
            x1 = px(0);
            x2 = px(16);
            y1 = px(15);
            y2 = px(16);
        } else {
            x1 = px(2);
            x2 = px(14);
            y1 = px(13);
            y2 = px(14);
        }

        // Down face
        renderQuadWithUV(consumer, pose, normal, packedLight,
                x1, y1, z2, x2, y1, z2, x2, y1, z1, x1, y1, z1,
                mapMode ? 0 / 16F : 2 / 16F, mapMode ? 0 / 16F : 0 / 16F,
                mapMode ? 16 / 16F : 14 / 16F, mapMode ? 1 / 16F : 1 / 16F,
                0, -1, 0);
        // Up face
        renderQuadWithUV(consumer, pose, normal, packedLight,
                x1, y2, z1, x2, y2, z1, x2, y2, z2, x1, y2, z2,
                mapMode ? 0 / 16F : 2 / 16F, mapMode ? 15 / 16F : 15 / 16F,
                mapMode ? 16 / 16F : 14 / 16F, mapMode ? 16 / 16F : 16 / 16F,
                0, 1, 0);
        // North face (front)
        renderQuadWithUV(consumer, pose, normal, packedLight,
                x2, y1, z1, x1, y1, z1, x1, y2, z1, x2, y2, z1,
                mapMode ? 0 / 16F : 2 / 16F, mapMode ? 0 / 16F : 2 / 16F,
                mapMode ? 16 / 16F : 14 / 16F, mapMode ? 1 / 16F : 3 / 16F,
                0, 0, -1);
        // South face (back)
        renderQuadWithUV(consumer, pose, normal, packedLight,
                x1, y1, z2, x2, y1, z2, x2, y2, z2, x1, y2, z2,
                mapMode ? 0 / 16F : 2 / 16F, mapMode ? 0 / 16F : 2 / 16F,
                mapMode ? 16 / 16F : 14 / 16F, mapMode ? 1 / 16F : 3 / 16F,
                0, 0, 1);
        // West face
        renderQuadWithUV(consumer, pose, normal, packedLight,
                x1, y1, z1, x1, y1, z2, x1, y2, z2, x1, y2, z1,
                mapMode ? 15 / 16F : 15 / 16F, mapMode ? 0 / 16F : 2 / 16F,
                mapMode ? 16 / 16F : 16 / 16F, mapMode ? 1 / 16F : 3 / 16F,
                -1, 0, 0);
        // East face
        renderQuadWithUV(consumer, pose, normal, packedLight,
                x2, y1, z2, x2, y1, z1, x2, y2, z1, x2, y2, z2,
                mapMode ? 0 / 16F : 0 / 16F, mapMode ? 0 / 16F : 2 / 16F,
                mapMode ? 1 / 16F : 1 / 16F, mapMode ? 1 / 16F : 3 / 16F,
                1, 0, 0);
    }

    private void renderLeftBorder(VertexConsumer consumer, Matrix4f pose, Matrix3f normal, int packedLight,
                                  float z1, float z2, boolean mapMode) {
        float x1, x2, y1, y2;
        if (mapMode) {
            x1 = px(0);
            x2 = px(1);
            y1 = px(1);
            y2 = px(15);
        } else {
            x1 = px(2);
            x2 = px(3);
            y1 = px(3);
            y2 = px(13);
        }

        // North face (front)
        renderQuadWithUV(consumer, pose, normal, packedLight,
                x2, y1, z1, x1, y1, z1, x1, y2, z1, x2, y2, z1,
                mapMode ? 15 / 16F : 13 / 16F, mapMode ? 1 / 16F : 3 / 16F,
                mapMode ? 16 / 16F : 14 / 16F, mapMode ? 15 / 16F : 13 / 16F,
                0, 0, -1);
        // South face (back)
        renderQuadWithUV(consumer, pose, normal, packedLight,
                x1, y1, z2, x2, y1, z2, x2, y2, z2, x1, y2, z2,
                mapMode ? 0 / 16F : 2 / 16F, mapMode ? 1 / 16F : 3 / 16F,
                mapMode ? 1 / 16F : 3 / 16F, mapMode ? 15 / 16F : 13 / 16F,
                0, 0, 1);
        // West face
        renderQuadWithUV(consumer, pose, normal, packedLight,
                x1, y1, z1, x1, y1, z2, x1, y2, z2, x1, y2, z1,
                mapMode ? 15 / 16F : 15 / 16F, mapMode ? 1 / 16F : 3 / 16F,
                mapMode ? 16 / 16F : 16 / 16F, mapMode ? 15 / 16F : 13 / 16F,
                -1, 0, 0);
        // East face
        renderQuadWithUV(consumer, pose, normal, packedLight,
                x2, y1, z2, x2, y1, z1, x2, y2, z1, x2, y2, z2,
                mapMode ? 0 / 16F : 0 / 16F, mapMode ? 1 / 16F : 3 / 16F,
                mapMode ? 1 / 16F : 1 / 16F, mapMode ? 15 / 16F : 13 / 16F,
                1, 0, 0);
    }

    private void renderRightBorder(VertexConsumer consumer, Matrix4f pose, Matrix3f normal, int packedLight,
                                   float z1, float z2, boolean mapMode) {
        float x1, x2, y1, y2;
        if (mapMode) {
            x1 = px(15);
            x2 = px(16);
            y1 = px(1);
            y2 = px(15);
        } else {
            x1 = px(13);
            x2 = px(14);
            y1 = px(3);
            y2 = px(13);
        }

        // North face (front)
        renderQuadWithUV(consumer, pose, normal, packedLight,
                x2, y1, z1, x1, y1, z1, x1, y2, z1, x2, y2, z1,
                mapMode ? 0 / 16F : 2 / 16F, mapMode ? 1 / 16F : 3 / 16F,
                mapMode ? 1 / 16F : 3 / 16F, mapMode ? 15 / 16F : 13 / 16F,
                0, 0, -1);
        // South face (back)
        renderQuadWithUV(consumer, pose, normal, packedLight,
                x1, y1, z2, x2, y1, z2, x2, y2, z2, x1, y2, z2,
                mapMode ? 15 / 16F : 13 / 16F, mapMode ? 1 / 16F : 3 / 16F,
                mapMode ? 16 / 16F : 14 / 16F, mapMode ? 15 / 16F : 13 / 16F,
                0, 0, 1);
        // West face
        renderQuadWithUV(consumer, pose, normal, packedLight,
                x1, y1, z1, x1, y1, z2, x1, y2, z2, x1, y2, z1,
                mapMode ? 15 / 16F : 15 / 16F, mapMode ? 1 / 16F : 3 / 16F,
                mapMode ? 16 / 16F : 16 / 16F, mapMode ? 15 / 16F : 13 / 16F,
                -1, 0, 0);
        // East face
        renderQuadWithUV(consumer, pose, normal, packedLight,
                x2, y1, z2, x2, y1, z1, x2, y2, z1, x2, y2, z2,
                mapMode ? 0 / 16F : 0 / 16F, mapMode ? 1 / 16F : 3 / 16F,
                mapMode ? 1 / 16F : 1 / 16F, mapMode ? 15 / 16F : 13 / 16F,
                1, 0, 0);
    }

    private void renderQuadWithUV(VertexConsumer consumer, Matrix4f pose, Matrix3f normal, int packedLight,
                                  float x1, float y1, float z1,
                                  float x2, float y2, float z2,
                                  float x3, float y3, float z3,
                                  float x4, float y4, float z4,
                                  float u1, float v1, float u2, float v2,
                                  float nx, float ny, float nz) {
        consumer.vertex(pose, x1, y1, z1).color(255, 255, 255, 255)
                .uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight).normal(normal, nx, ny, nz).endVertex();
        consumer.vertex(pose, x2, y2, z2).color(255, 255, 255, 255)
                .uv(u2, v1).overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight).normal(normal, nx, ny, nz).endVertex();
        consumer.vertex(pose, x3, y3, z3).color(255, 255, 255, 255)
                .uv(u2, v2).overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight).normal(normal, nx, ny, nz).endVertex();
        consumer.vertex(pose, x4, y4, z4).color(255, 255, 255, 255)
                .uv(u1, v2).overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight).normal(normal, nx, ny, nz).endVertex();
    }

    private void renderItem(ColoredItemFrameEntity entity, ItemStack itemStack,
                            PoseStack poseStack, MultiBufferSource buffer, int packedLight, boolean isMap) {
        poseStack.pushPose();

        // After frame translation of -0.5, the frame front (model Z=0.5) is now at Z=0
        // Item should be positioned at the front surface of the frame (Z=0) plus a tiny offset
        // This matches vanilla ItemFrameRenderer behavior
        float itemZ = 0.03125F; // 0.5 pixels in front of frame surface
        poseStack.translate(0.0D, 0.0D, itemZ);

        // Apply rotation based on item rotation value (0-7)
        int rotation = entity.getRotation();
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(rotation * -45.0F));

        if (isMap) {
            MapItemSavedData mapData = MapItem.getSavedData(itemStack, entity.level);
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
            poseStack.scale(0.0078125F, 0.0078125F, 0.0078125F);
            poseStack.translate(-64.0D, -64.0D, 0.0D);
            poseStack.translate(0.0D, 0.0D, -1.0D);
            Integer mapId = MapItem.getMapId(itemStack);
            if (mapId != null && mapData != null) {
                Minecraft.getInstance().gameRenderer.getMapRenderer()
                        .render(poseStack, buffer, mapId, mapData, true, packedLight);
            }
        } else {
            poseStack.scale(0.5F, 0.5F, 0.5F);
            Minecraft.getInstance().getItemRenderer().renderStatic(
                    itemStack,
                    ItemTransforms.TransformType.FIXED,
                    packedLight,
                    OverlayTexture.NO_OVERLAY,
                    poseStack,
                    buffer,
                    entity.getId()
            );
        }

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(ColoredItemFrameEntity entity) {
        return getTextureForColor(entity.getColorVariant());
    }
}
