package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.block.ModBlocks;
import com.kingodogo.buildscape.block.MuffBlock;
import com.kingodogo.buildscape.item.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;

@Mod.EventBusSubscriber(
        modid = BuildScape.MODID,
        bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT
)
public class MuffBlockRenderer {

    private static final double MAX_RENDER_DISTANCE = 128.0;

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            return;
        }

        // Only render when holding the muff block in offhand
        if (!mc.player.getOffhandItem().is(ModItems.MUFF_BLOCK.get())) {
            return;
        }

        Level level = mc.level;
        Camera camera = event.getCamera();
        if (camera == null) return;

        Vec3 cameraPos = camera.getPosition();
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

        Set<BlockPos> activeMuffs = MuffBlockManager.getActiveMuffs();
        if (activeMuffs == null || activeMuffs.isEmpty()) {
            return;
        }

        poseStack.pushPose();

        // Offset relative to camera
        double offsetX = -cameraPos.x;
        double offsetY = -cameraPos.y;
        double offsetZ = -cameraPos.z;

        VertexConsumer buffer = bufferSource.getBuffer(RenderType.lines());
        // Cyan color: R=0, G=220, B=255, A=180
        int color = (0 << 24) | (220 << 16) | (255 << 8) | 180;

        for (BlockPos pos : activeMuffs) {
            double dx = pos.getX() + 0.5 - cameraPos.x;
            double dy = pos.getY() + 0.5 - cameraPos.y;
            double dz = pos.getZ() + 0.5 - cameraPos.z;
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

            if (distance > MAX_RENDER_DISTANCE) {
                continue;
            }

            BlockState state = level.getBlockState(pos);
            if (state.is(ModBlocks.MUFF_BLOCK.get())) {
                int radius = state.getValue(MuffBlock.RADIUS);
                double cx = pos.getX() + 0.5 + offsetX;
                double cy = pos.getY() + 0.5 + offsetY;
                double cz = pos.getZ() + 0.5 + offsetZ;

                double minX = pos.getX() + 0.5 - radius + offsetX;
                double minY = pos.getY() + 0.5 - radius + offsetY;
                double minZ = pos.getZ() + 0.5 - radius + offsetZ;
                double maxX = pos.getX() + 0.5 + radius + offsetX;
                double maxY = pos.getY() + 0.5 + radius + offsetY;
                double maxZ = pos.getZ() + 0.5 + radius + offsetZ;

                // Draw the 12 edges of the bounding box
                // Bottom face (4 edges)
                drawLine(buffer, poseStack, minX, minY, minZ, maxX, minY, minZ, color);
                drawLine(buffer, poseStack, maxX, minY, minZ, maxX, minY, maxZ, color);
                drawLine(buffer, poseStack, maxX, minY, maxZ, minX, minY, maxZ, color);
                drawLine(buffer, poseStack, minX, minY, maxZ, minX, minY, minZ, color);

                // Top face (4 edges)
                drawLine(buffer, poseStack, minX, maxY, minZ, maxX, maxY, minZ, color);
                drawLine(buffer, poseStack, maxX, maxY, minZ, maxX, maxY, maxZ, color);
                drawLine(buffer, poseStack, maxX, maxY, maxZ, minX, maxY, maxZ, color);
                drawLine(buffer, poseStack, minX, maxY, maxZ, minX, maxY, minZ, color);

                // Vertical edges (4 edges)
                drawLine(buffer, poseStack, minX, minY, minZ, minX, maxY, minZ, color);
                drawLine(buffer, poseStack, maxX, minY, minZ, maxX, maxY, minZ, color);
                drawLine(buffer, poseStack, maxX, minY, maxZ, maxX, maxY, maxZ, color);
                drawLine(buffer, poseStack, minX, minY, maxZ, minX, maxY, maxZ, color);
            }
        }

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

        buffer.vertex(pose, (float) x1, (float) y1, (float) z1)
                .color(r, g, b, a)
                .normal(normal, 0, 1, 0)
                .endVertex();
        buffer.vertex(pose, (float) x2, (float) y2, (float) z2)
                .color(r, g, b, a)
                .normal(normal, 0, 1, 0)
                .endVertex();
    }
}
