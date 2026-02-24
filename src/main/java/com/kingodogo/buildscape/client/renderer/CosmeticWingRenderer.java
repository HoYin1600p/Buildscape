package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.particle.ParticleShapeLibrary;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.List;

/**
 * CosmeticWingRenderer - Foundational renderer that uses registered particle sprites.
 * Fixed for transparency from both sides and proper particle spacing.
 */
public class CosmeticWingRenderer {

    private static final ResourceLocation PARTICLE_ATLAS = new ResourceLocation("textures/atlas/particles.png");

    public static void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, 
                              Player player, float ageInTicks, float[] color, String shapeId) {
        
        SpriteSet spriteSet = WingParticleAssets.getSprites(shapeId);
        if (spriteSet == null) return;

        // Use entityTranslucent which usually has culling disabled (visible from both sides)
        // If it still culls, we render each quad twice.
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(PARTICLE_ATLAS));
        List<ParticleShapeLibrary.WingParticlePos> particles = ParticleShapeLibrary.getWingParticles(shapeId);

        poseStack.pushPose();
        
        // Attachment point relative to body pivot (neck)
        // Y moves down from neck, Z moves back from torso center
        // 0.18 blocks = ~3 units back, Steve's back surface is at 2 units.
        // Attachment point moved higher up (Y=-0.2)
        poseStack.translate(0, -0.2, 0.18);
        
        // Invert angle to ensure wings rotate backward (Z+) instead of forward (Z-)
        float wingAngle = -calculateWingAngle(player, ageInTicks);

        renderWingMesh(poseStack, consumer, color, particles, wingAngle, 1.0f, spriteSet, shapeId, ageInTicks);   // Left
        renderWingMesh(poseStack, consumer, color, particles, wingAngle, -1.0f, spriteSet, shapeId, ageInTicks);  // Right

        poseStack.popPose();
    }

    private static void renderWingMesh(PoseStack poseStack, VertexConsumer consumer, 
                                      float[] color, List<ParticleShapeLibrary.WingParticlePos> particles,
                                      float angle, float side, SpriteSet spriteSet, String shapeId, float ageInTicks) {
        poseStack.pushPose();
        
        float angleRad = (float) Math.toRadians(angle * side);
        poseStack.mulPose(com.mojang.math.Vector3f.YP.rotation(angleRad));
        
        // Simple offset to leave a clean gap between the wings
        poseStack.translate(0.08 * side, 0, 0);

        // Adjusted size to ensure gaps with the new density
        float quadSize = 0.16f; 
        float hs = quadSize / 2f;
        float r = color[0], g = color[1], b = color[2], a = 0.95f; 

        // Full bright lighting
        int emissiveLight = 0xF000F0;

        var last = poseStack.last();
        var pose = last.pose();
        var normal = last.normal();

        for (int i = 0; i < particles.size(); i++) {
            ParticleShapeLibrary.WingParticlePos p = particles.get(i);
            float vx = p.x() * side;
            float vy = p.y();
            float vz = 0;

            // Pick sprite variant
            TextureAtlasSprite sprite = spriteSet.get(i % 10, 10); 
            
            float u0 = sprite.getU0();
            float u1 = sprite.getU1();
            float v0 = sprite.getV0();
            float v1 = sprite.getV1();

            // Handle multi-frame textures (Sparkle has a 10-frame horizontal strip)
            if ("sparkle".equalsIgnoreCase(shapeId)) {
                int frame = (i + (int)(ageInTicks * 0.4f)) % 10;
                float uScale = (u1 - u0) / 10f;
                u0 = u0 + frame * uScale;
                u1 = u0 + uScale;
                // V remains same for horizontal strip
            }

            // Render front side with tiny offset to prevent Z-fighting
            // Reversed V coordinates to fix orientation
            addQuad(consumer, pose, normal, vx, vy, vz + 0.001f, hs, u0, u1, v1, v0, r, g, b, a, emissiveLight, 1f);
            // Render back side with reverse offset and opposite normal
            addQuad(consumer, pose, normal, vx, vy, vz - 0.001f, hs, u0, u1, v1, v0, r, g, b, a, emissiveLight, -1f);
        }

        poseStack.popPose();
    }

    private static void addQuad(VertexConsumer consumer, com.mojang.math.Matrix4f pose, com.mojang.math.Matrix3f normal,
                               float x, float y, float z, float hs,
                               float u0, float u1, float v0, float v1,
                               float r, float g, float b, float a, int light, float side) {
        
        if (side > 0) {
            consumer.vertex(pose, x - hs, y + hs, z).color(r, g, b, a).uv(u0, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, 0, 1).endVertex();
            consumer.vertex(pose, x + hs, y + hs, z).color(r, g, b, a).uv(u1, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, 0, 1).endVertex();
            consumer.vertex(pose, x + hs, y - hs, z).color(r, g, b, a).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, 0, 1).endVertex();
            consumer.vertex(pose, x - hs, y - hs, z).color(r, g, b, a).uv(u0, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, 0, 1).endVertex();
        } else {
            // Reverse order for back side visibility
            consumer.vertex(pose, x - hs, y - hs, z).color(r, g, b, a).uv(u0, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, 0, -1).endVertex();
            consumer.vertex(pose, x + hs, y - hs, z).color(r, g, b, a).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, 0, -1).endVertex();
            consumer.vertex(pose, x + hs, y + hs, z).color(r, g, b, a).uv(u1, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, 0, -1).endVertex();
            consumer.vertex(pose, x - hs, y + hs, z).color(r, g, b, a).uv(u0, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, 0, -1).endVertex();
        }
    }

    private static float calculateWingAngle(Player player, float ageInTicks) {
        if (player.isCrouching()) return 8.0f;
        float velocityY = (float) player.getDeltaMovement().y;
        if (velocityY < -0.15f) return 75.0f + (float) Math.sin(ageInTicks * 0.15f) * 10f;
        if (player.isSprinting()) return 50.0f + (float) Math.sin(ageInTicks * 0.12f) * 18f;
        if (player.getDeltaMovement().horizontalDistanceSqr() > 0.001) return 32.0f + (float) Math.sin(ageInTicks * 0.08f) * 12f;
        return 18.0f + (float) Math.sin(ageInTicks * 0.04f) * 8f;
    }
}
