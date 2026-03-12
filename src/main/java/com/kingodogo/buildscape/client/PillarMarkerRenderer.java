package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.block.PillarBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

/**
 * Renders blinking borders around marked pillars in the world.
 */
@Mod.EventBusSubscriber(
        modid = BuildScape.MODID,
        bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT
)
public class PillarMarkerRenderer {
    private static final double MAX_RENDER_DISTANCE = 64.0; // Only render within 64 blocks
    
    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        try {
            // Only render after particles (which happens after entities)
            if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
                return;
            }
            
            Minecraft mc = Minecraft.getInstance();
            if (mc == null || mc.player == null || mc.level == null) {
                return;
            }
            
            Level level = mc.level;
            if (level == null) {
                return;
            }
            
            String dimension = level.dimension().location().toString();
            Map<String, PillarMarkerManager.MarkedPillar> markedPillars = 
                PillarMarkerManager.get().getMarkedPillars(dimension);
            
            if (markedPillars == null || markedPillars.isEmpty()) {
                return;
            }
            
            Camera camera = event.getCamera();
            if (camera == null) {
                return;
            }
            
            Vec3 cameraPos = camera.getPosition();
            PoseStack poseStack = event.getPoseStack();
            MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
            
            // Render bounding boxes for marked pillars
            for (PillarMarkerManager.MarkedPillar marked : markedPillars.values()) {
                if (marked == null || marked.pos == null) {
                    continue;
                }
                
                BlockPos pos = marked.pos;
                
                // Check distance
                double dx = pos.getX() + 0.5 - cameraPos.x;
                double dy = pos.getY() + 0.5 - cameraPos.y;
                double dz = pos.getZ() + 0.5 - cameraPos.z;
                double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
                
                if (distance > MAX_RENDER_DISTANCE) {
                    continue;
                }
                
                // Find the full pillar stack (connected pillars above and below)
                BlockPos bottomPos = findPillarStackBottom(level, pos);
                BlockPos topPos = findPillarStackTop(level, pos);
                
                // Render bounding box for the entire stack
                renderPillarStackBoundingBox(poseStack, bufferSource, cameraPos, bottomPos, topPos, marked);
            }
        } catch (Exception e) {
            // Suppressed debug log to prevent render loop spam
        }
    }
    
    private static BlockPos findPillarStackBottom(Level level, BlockPos startPos) {
        BlockPos current = startPos;
        int maxDepth = 256;
        int checked = 0;
        
        while (checked < maxDepth) {
            BlockPos below = current.below();
            if (!level.isLoaded(below)) {
                break;
            }
            if (!(level.getBlockState(below).getBlock() instanceof PillarBlock)) {
                break;
            }
            current = below;
            checked++;
        }
        return current;
    }
    
    private static BlockPos findPillarStackTop(Level level, BlockPos startPos) {
        BlockPos current = startPos;
        int maxHeight = 256;
        int checked = 0;
        
        while (checked < maxHeight) {
            BlockPos above = current.above();
            if (!level.isLoaded(above)) {
                break;
            }
            if (!(level.getBlockState(above).getBlock() instanceof PillarBlock)) {
                break;
            }
            current = above;
            checked++;
        }
        return current;
    }
    
    private static void renderPillarStackBoundingBox(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, 
            Vec3 cameraPos, BlockPos bottomPos, BlockPos topPos, PillarMarkerManager.MarkedPillar marked) {
        float alpha = marked.getBlinkAlpha();
        if (alpha < 0.1f) {
            return; // Don't render if too transparent
        }
        
        poseStack.pushPose();
        
        // Calculate the bounding box for the entire stack
        double minX = bottomPos.getX();
        double minY = bottomPos.getY();
        double minZ = bottomPos.getZ();
        double maxX = topPos.getX() + 1.0;
        double maxY = topPos.getY() + 1.0;
        double maxZ = topPos.getZ() + 1.0;
        
        // Offset by camera position for rendering
        double offsetX = -cameraPos.x;
        double offsetY = -cameraPos.y;
        double offsetZ = -cameraPos.z;
        
        // Calculate color with yellow-red gradient and alpha (blinking effect)
        float gradientProgress = marked.getGradientProgress(); // 0.0 = yellow, 1.0 = red
        // Yellow: RGB(255, 255, 0), Red: RGB(255, 0, 0)
        int r = 255;
        int g = (int)(255 * (1.0f - gradientProgress));
        int b = 0;
        // Apply alpha
        r = (int)(r * alpha);
        g = (int)(g * alpha);
        b = (int)(b * alpha);
        int color = (r << 24) | (g << 16) | (b << 8) | 0xFF;
        
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.lines());
        
        // Draw the 12 edges of the bounding box
        // Bottom face (4 edges)
        drawLine(buffer, poseStack, minX + offsetX, minY + offsetY, minZ + offsetZ, maxX + offsetX, minY + offsetY, minZ + offsetZ, color);
        drawLine(buffer, poseStack, maxX + offsetX, minY + offsetY, minZ + offsetZ, maxX + offsetX, minY + offsetY, maxZ + offsetZ, color);
        drawLine(buffer, poseStack, maxX + offsetX, minY + offsetY, maxZ + offsetZ, minX + offsetX, minY + offsetY, maxZ + offsetZ, color);
        drawLine(buffer, poseStack, minX + offsetX, minY + offsetY, maxZ + offsetZ, minX + offsetX, minY + offsetY, minZ + offsetZ, color);
        
        // Top face (4 edges)
        drawLine(buffer, poseStack, minX + offsetX, maxY + offsetY, minZ + offsetZ, maxX + offsetX, maxY + offsetY, minZ + offsetZ, color);
        drawLine(buffer, poseStack, maxX + offsetX, maxY + offsetY, minZ + offsetZ, maxX + offsetX, maxY + offsetY, maxZ + offsetZ, color);
        drawLine(buffer, poseStack, maxX + offsetX, maxY + offsetY, maxZ + offsetZ, minX + offsetX, maxY + offsetY, maxZ + offsetZ, color);
        drawLine(buffer, poseStack, minX + offsetX, maxY + offsetY, maxZ + offsetZ, minX + offsetX, maxY + offsetY, minZ + offsetZ, color);
        
        // Vertical edges (4 edges)
        drawLine(buffer, poseStack, minX + offsetX, minY + offsetY, minZ + offsetZ, minX + offsetX, maxY + offsetY, minZ + offsetZ, color);
        drawLine(buffer, poseStack, maxX + offsetX, minY + offsetY, minZ + offsetZ, maxX + offsetX, maxY + offsetY, minZ + offsetZ, color);
        drawLine(buffer, poseStack, maxX + offsetX, minY + offsetY, maxZ + offsetZ, maxX + offsetX, maxY + offsetY, maxZ + offsetZ, color);
        drawLine(buffer, poseStack, minX + offsetX, minY + offsetY, maxZ + offsetZ, minX + offsetX, maxY + offsetY, maxZ + offsetZ, color);
        
        bufferSource.endBatch();
        poseStack.popPose();
    }
    
    private static void drawLine(VertexConsumer buffer, PoseStack poseStack, 
            double x1, double y1, double z1, double x2, double y2, double z2, int color) {
        int r = (color >> 24) & 0xFF;
        int g = (color >> 16) & 0xFF;
        int b = (color >> 8) & 0xFF;
        int a = color & 0xFF;
        
        com.mojang.math.Matrix4f pose = poseStack.last().pose();
        com.mojang.math.Matrix3f normal = poseStack.last().normal();
        
        buffer.vertex(pose, (float)x1, (float)y1, (float)z1)
            .color(r, g, b, a)
            .normal(normal, 0, 1, 0)
            .endVertex();
        buffer.vertex(pose, (float)x2, (float)y2, (float)z2)
            .color(r, g, b, a)
            .normal(normal, 0, 1, 0)
            .endVertex();
    }
    
}

