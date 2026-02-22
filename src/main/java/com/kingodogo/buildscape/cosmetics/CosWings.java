package com.kingodogo.buildscape.cosmetics;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.entity.player.Player;

/**
 * Wing cosmetic that renders animated elytra-style wings.
 */
public class CosWings {

    /**
     * Render wings on the player with animation based on movement.
     */
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Player player,
            float limbSwing, float limbSwingAmount, float ageInTicks) {

        poseStack.pushPose();

        // Position wings on player's back
        poseStack.translate(0, 0.4, 0.2);

        // Calculate wing angle based on player state
        float wingAngle = calculateWingAngle(player, ageInTicks);

        // Apply rotation for wing opening/closing (rotate around Z axis)
        poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(wingAngle));

        // Render left wing
        poseStack.pushPose();
        poseStack.translate(-0.25, 0, 0);
        renderWing(poseStack, buffer, packedLight, player, ageInTicks);
        poseStack.popPose();

        // Render right wing (mirrored)
        poseStack.pushPose();
        poseStack.translate(0.25, 0, 0);
        poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(180));
        renderWing(poseStack, buffer, packedLight, player, ageInTicks);
        poseStack.popPose();

        poseStack.popPose();
    }

    /**
     * Calculate wing angle based on player movement state.
     */
    private float calculateWingAngle(Player player, float ageInTicks) {
        boolean isMoving = false;
        boolean isSprinting = player.isSprinting();
        boolean isSneaking = player.isCrouching();
        boolean isOnGround = player.isOnGround();
        boolean isFalling = !isOnGround && player.getDeltaMovement().y < -0.1;
        boolean isJumping = !isOnGround && player.getDeltaMovement().y > 0.1;

        if (player instanceof net.minecraft.client.player.LocalPlayer localPlayer) {
            isMoving = localPlayer.input.forwardImpulse != 0 || localPlayer.input.leftImpulse != 0;
        }

        if (isSneaking) {
            // Closed when sneaking
            return 0;
        } else if (isFalling) {
            // Extended when falling with flutter
            return 75 + (float) Math.sin(ageInTicks * 0.1f) * 10f;
        } else if (isJumping) {
            // Extended when jumping
            return 65;
        } else if (isSprinting) {
            // Open with flutter when sprinting
            return 45 + (float) Math.sin(ageInTicks * 0.15f) * 15f;
        } else if (isMoving) {
            // Gently open when walking
            return 30 + (float) Math.sin(ageInTicks * 0.08f) * 10f;
        } else {
            // Idle - gently flap closed/open
            return 10 + (float) Math.sin(ageInTicks * 0.03f) * 8f;
        }
    }

    /**
     * Render a single wing using simple colored rectangles.
     */
    private void renderWing(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Player player, float ageInTicks) {
        poseStack.pushPose();

        // Get the vertex consumer for rendering solid colors
        var vertexConsumer = buffer.getBuffer(RenderType.solid());
        com.mojang.math.Matrix4f pose = poseStack.last().pose();
        com.mojang.math.Matrix3f normal = poseStack.last().normal();

        // Wing color - light blue for snowflake effect
        float r = 0.85f;
        float g = 0.95f;
        float b = 1.0f;
        int packedColor = 0xFF000000 | ((int)(r * 255) << 16) | ((int)(g * 255) << 8) | (int)(b * 255);

        // Draw wing as a series of feather-like rectangles
        // Top section
        drawFeatherQuad(vertexConsumer, pose, normal, 0.0f, 0.9f, 0.3f, 1.2f, 0.0f, packedColor);
        drawFeatherQuad(vertexConsumer, pose, normal, 0.25f, 1.1f, 0.6f, 1.3f, 0.05f, packedColor);
        drawFeatherQuad(vertexConsumer, pose, normal, 0.5f, 1.0f, 0.9f, 1.2f, 0.1f, packedColor);
        drawFeatherQuad(vertexConsumer, pose, normal, 0.75f, 0.8f, 1.2f, 1.0f, 0.15f, packedColor);
        drawFeatherQuad(vertexConsumer, pose, normal, 1.0f, 0.5f, 1.5f, 0.8f, 0.2f, packedColor);

        // Middle section
        drawFeatherQuad(vertexConsumer, pose, normal, 0.0f, 0.4f, 0.3f, 0.7f, 0.0f, packedColor);
        drawFeatherQuad(vertexConsumer, pose, normal, 0.25f, 0.3f, 0.6f, 0.6f, 0.05f, packedColor);
        drawFeatherQuad(vertexConsumer, pose, normal, 0.5f, 0.2f, 0.9f, 0.5f, 0.1f, packedColor);
        drawFeatherQuad(vertexConsumer, pose, normal, 0.75f, 0.0f, 1.2f, 0.3f, 0.15f, packedColor);
        drawFeatherQuad(vertexConsumer, pose, normal, 1.0f, -0.2f, 1.5f, 0.1f, 0.2f, packedColor);

        // Bottom section (trailing edge)
        drawFeatherQuad(vertexConsumer, pose, normal, 0.0f, -0.1f, 0.3f, 0.2f, 0.0f, packedColor);
        drawFeatherQuad(vertexConsumer, pose, normal, 0.25f, -0.2f, 0.6f, 0.1f, 0.05f, packedColor);
        drawFeatherQuad(vertexConsumer, pose, normal, 0.5f, -0.3f, 0.9f, 0.0f, 0.1f, packedColor);
        drawFeatherQuad(vertexConsumer, pose, normal, 0.75f, -0.4f, 1.2f, -0.1f, 0.15f, packedColor);
        drawFeatherQuad(vertexConsumer, pose, normal, 1.0f, -0.5f, 1.5f, -0.2f, 0.2f, packedColor);

        poseStack.popPose();
    }

    /**
     * Draw a single feather quad (rectangle).
     */
    private void drawFeatherQuad(com.mojang.blaze3d.vertex.VertexConsumer consumer, com.mojang.math.Matrix4f pose,
            com.mojang.math.Matrix3f normal, float x1, float y1, float x2, float y2, float z, int color) {

        int a = (color >> 24) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        // Top-left
        consumer.vertex(pose, x1, y2, z).color(r, g, b, a).normal(normal, 0, 0, 1).endVertex();

        // Top-right
        consumer.vertex(pose, x2, y2, z).color(r, g, b, a).normal(normal, 0, 0, 1).endVertex();

        // Bottom-right
        consumer.vertex(pose, x2, y1, z + 0.05f).color(r, g, b, a).normal(normal, 0, 0, 1).endVertex();

        // Bottom-left
        consumer.vertex(pose, x1, y1, z + 0.05f).color(r, g, b, a).normal(normal, 0, 0, 1).endVertex();
    }
}
