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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;

public class ColoredItemFrameRenderer extends EntityRenderer<ColoredItemFrameEntity> {

    private static final ResourceLocation BIRCH_PLANKS = new ResourceLocation("minecraft", "textures/block/birch_planks.png");
    private static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation(BuildScape.MODID, "textures/entity/white_item_frame.png");

    private final net.minecraft.client.renderer.entity.ItemRenderer itemRenderer;

    public ColoredItemFrameRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    private ResourceLocation getTextureForColor(String color) {
        if (color == null || color.isEmpty()) {
            return DEFAULT_TEXTURE;
        }
        return new ResourceLocation(BuildScape.MODID, "textures/entity/" + color + "_item_frame.png");
    }

    @Override
    public Vec3 getRenderOffset(ColoredItemFrameEntity entity, float partialTicks) {
        return new Vec3(
                (double) entity.getDirection().getStepX() * 0.3D,
                -0.25D,
                (double) entity.getDirection().getStepZ() * 0.3D
        );
    }

    @Override
    public void render(ColoredItemFrameEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);

        Direction direction = entity.getDirection();
        if (direction == null) {
            return;
        }

        // Fix dark/black rendering on ceiling and floor by sampling light from the open-air side
        if (direction == Direction.UP || direction == Direction.DOWN) {
            BlockPos lightPos = entity.blockPosition().relative(direction);
            packedLight = net.minecraft.client.renderer.LevelRenderer.getLightColor(entity.level, lightPos);
        }

        poseStack.pushPose();

        // Undo the render offset applied by EntityRenderDispatcher, matching vanilla
        Vec3 renderOffset = this.getRenderOffset(entity, partialTicks);
        poseStack.translate(-renderOffset.x(), -renderOffset.y(), -renderOffset.z());

        // Translate by direction * 0.46875 to move render origin to block center
        poseStack.translate(
                (double) direction.getStepX() * 0.46875D,
                (double) direction.getStepY() * 0.46875D,
                (double) direction.getStepZ() * 0.46875D
        );

        // Apply rotation using entity's xRot/yRot (set by setDirection), matching vanilla
        poseStack.mulPose(Vector3f.XP.rotationDegrees(entity.getXRot()));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F - entity.getYRot()));

        boolean isInvisible = entity.isInvisible();
        ItemStack itemStack = entity.getItem();
        boolean hasMap = !itemStack.isEmpty() && MapItem.getSavedData(itemStack, entity.level) != null;

        // Skip frame rendering if invisible (item still renders), matching vanilla
        if (!isInvisible) {
            ResourceLocation backTexture = getTextureForColor(entity.getColorVariant());
            if (hasMap) {
                renderMapFrame(poseStack, buffer, packedLight, backTexture);
            } else {
                renderNormalFrame(poseStack, buffer, packedLight, backTexture);
            }
        }

        // Render the item if present
        if (!itemStack.isEmpty()) {
            renderItem(entity, itemStack, poseStack, buffer, packedLight, hasMap, isInvisible);
        }

        poseStack.popPose();
    }

    private void renderNormalFrame(PoseStack poseStack, MultiBufferSource buffer, int packedLight, ResourceLocation backTexture) {
        poseStack.pushPose();
        poseStack.translate(-0.5D, -0.5D, -0.5D);

        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();

        // Back panel: from [3, 3, 15.5] to [13, 13, 16] in pixel coords (0-16 maps to 0-1)
        float backZ1 = 15.5F / 16F;
        float backZ2 = 1.0F;

        // Frame border z positions: from 15 to 16
        float frameZ1 = 15F / 16F;
        float frameZ2 = 1.0F;

        // Render back panel with colored texture
        VertexConsumer backConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(backTexture));

        // Back panel - front face (facing the viewer)
        renderQuadWithUV(backConsumer, pose, normal, packedLight,
                3F / 16F, 3F / 16F, backZ1,
                13F / 16F, 3F / 16F, backZ1,
                13F / 16F, 13F / 16F, backZ1,
                3F / 16F, 13F / 16F, backZ1,
                3F / 16F, 13F / 16F, 13F / 16F, 3F / 16F,
                0, 0, -1);

        // Back panel - back face
        renderQuadWithUV(backConsumer, pose, normal, packedLight,
                13F / 16F, 3F / 16F, backZ2,
                3F / 16F, 3F / 16F, backZ2,
                3F / 16F, 13F / 16F, backZ2,
                13F / 16F, 13F / 16F, backZ2,
                3F / 16F, 13F / 16F, 13F / 16F, 3F / 16F,
                0, 0, 1);

        // Render frame border with birch planks
        VertexConsumer frameConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(BIRCH_PLANKS));

        // Bottom border: from [2, 2, 15] to [14, 3, 16]
        renderBoxFaces(frameConsumer, pose, normal, packedLight,
                2F / 16F, 2F / 16F, frameZ1,
                14F / 16F, 3F / 16F, frameZ2);

        // Top border: from [2, 13, 15] to [14, 14, 16]
        renderBoxFaces(frameConsumer, pose, normal, packedLight,
                2F / 16F, 13F / 16F, frameZ1,
                14F / 16F, 14F / 16F, frameZ2);

        // Left border: from [2, 3, 15] to [3, 13, 16]
        renderBoxFaces(frameConsumer, pose, normal, packedLight,
                2F / 16F, 3F / 16F, frameZ1,
                3F / 16F, 13F / 16F, frameZ2);

        // Right border: from [13, 3, 15] to [14, 13, 16]
        renderBoxFaces(frameConsumer, pose, normal, packedLight,
                13F / 16F, 3F / 16F, frameZ1,
                14F / 16F, 13F / 16F, frameZ2);

        poseStack.popPose();
    }

    private void renderMapFrame(PoseStack poseStack, MultiBufferSource buffer, int packedLight, ResourceLocation backTexture) {
        poseStack.pushPose();
        poseStack.translate(-0.5D, -0.5D, -0.5D);

        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();

        // Back panel: from [1, 1, 15.001] to [15, 15, 16]
        float backZ1 = 15.001F / 16F;
        float backZ2 = 1.0F;

        // Frame border
        float frameZ1 = 15.001F / 16F;
        float frameZ2 = 1.0F;

        // Render back panel with colored texture
        VertexConsumer backConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(backTexture));

        // Back panel - front face
        renderQuadWithUV(backConsumer, pose, normal, packedLight,
                1F / 16F, 1F / 16F, backZ1,
                15F / 16F, 1F / 16F, backZ1,
                15F / 16F, 15F / 16F, backZ1,
                1F / 16F, 15F / 16F, backZ1,
                1F / 16F, 15F / 16F, 15F / 16F, 1F / 16F,
                0, 0, -1);

        // Back panel - back face
        renderQuadWithUV(backConsumer, pose, normal, packedLight,
                15F / 16F, 1F / 16F, backZ2,
                1F / 16F, 1F / 16F, backZ2,
                1F / 16F, 15F / 16F, backZ2,
                15F / 16F, 15F / 16F, backZ2,
                1F / 16F, 15F / 16F, 15F / 16F, 1F / 16F,
                0, 0, 1);

        // Render frame border with birch planks
        VertexConsumer frameConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(BIRCH_PLANKS));

        // Bottom border: from [0, 0, 15.001] to [16, 1, 16]
        renderBoxFaces(frameConsumer, pose, normal, packedLight,
                0F, 0F, frameZ1,
                1F, 1F / 16F, frameZ2);

        // Top border: from [0, 15, 15.001] to [16, 16, 16]
        renderBoxFaces(frameConsumer, pose, normal, packedLight,
                0F, 15F / 16F, frameZ1,
                1F, 1F, frameZ2);

        // Left border: from [0, 1, 15.001] to [1, 15, 16]
        renderBoxFaces(frameConsumer, pose, normal, packedLight,
                0F, 1F / 16F, frameZ1,
                1F / 16F, 15F / 16F, frameZ2);

        // Right border: from [15, 1, 15.001] to [16, 15, 16]
        renderBoxFaces(frameConsumer, pose, normal, packedLight,
                15F / 16F, 1F / 16F, frameZ1,
                1F, 15F / 16F, frameZ2);

        poseStack.popPose();
    }

    /**
     * Renders all 6 faces of a box defined by two corners.
     */
    private void renderBoxFaces(VertexConsumer consumer, Matrix4f pose, Matrix3f normal, int packedLight,
                                float x1, float y1, float z1,
                                float x2, float y2, float z2) {
        float w = x2 - x1;
        float h =
                y2 -
                        y1;

        // Front face (-Z)
        renderQuadWithUV(consumer, pose, normal, packedLight,
                x2, y1, z1, x1, y1, z1, x1, y2, z1, x2, y2, z1,
                x1, 1F - y2, x2, 1F - y1,
                0, 0, -1);

        // Back face (+Z)
        renderQuadWithUV(consumer, pose, normal, packedLight,
                x1, y1, z2, x2, y1, z2, x2, y2, z2, x1, y2, z2,
                x1, 1F - y2, x2, 1F - y1,
                0, 0, 1);

        // Bottom face (-Y)
        renderQuadWithUV(consumer, pose, normal, packedLight,
                x1, y1, z2, x2, y1, z2, x2, y1, z1, x1, y1, z1,
                x1, z1, x2, z2,
                0, -1, 0);

        // Top face (+Y)
        renderQuadWithUV(consumer, pose, normal, packedLight,
                x1, y2, z1, x2, y2, z1, x2, y2, z2, x1, y2, z2,
                x1, z1, x2, z2,
                0, 1, 0);

        // Left face (-X)
        renderQuadWithUV(consumer, pose, normal, packedLight,
                x1, y1, z1, x1, y1, z2, x1, y2, z2, x1, y2, z1,
                z1, 1F - y2, z2, 1F - y1,
                -1, 0, 0);

        // Right face (+X)
        renderQuadWithUV(consumer, pose, normal, packedLight,
                x2, y1, z2, x2, y1, z1, x2, y2, z1, x2, y2, z2,
                z1, 1F - y2, z2, 1F - y1,
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
                            PoseStack poseStack, MultiBufferSource buffer, int packedLight, boolean isMap, boolean isInvisible) {
        MapItemSavedData mapData = isMap ? MapItem.getSavedData(itemStack, entity.level) : null;

        // When invisible, item sits at 0.5 (block face); otherwise 0.4375 (in front of frame surface)
        poseStack.translate(0.0D, 0.0D, isInvisible ? 0.5D : 0.4375D);

        // Apply rotation based on item rotation value (0-7)
        int rotation;
        if (mapData != null) {
            rotation = entity.getRotation() % 4 * 2;
        } else {
            rotation = entity.getRotation();
        }
        poseStack.mulPose(Vector3f.ZP.rotationDegrees((float) rotation * 360.0F / 8.0F));

        if (mapData != null) {
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
            this.itemRenderer.renderStatic(
                    itemStack,
                    ItemTransforms.TransformType.FIXED,
                    packedLight,
                    OverlayTexture.NO_OVERLAY,
                    poseStack,
                    buffer,
                    entity.getId()
            );
        }
    }

    @Override
    public ResourceLocation getTextureLocation(ColoredItemFrameEntity entity) {
        return getTextureForColor(entity.getColorVariant());
    }
}
