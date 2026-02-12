package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.entity.ColoredItemFrameEntity;
import com.kingodogo.buildscape.item.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(
        modid = BuildScape.MODID,
        bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT
)
public class InvisibleFrameOverlayRenderer {

    private static final double SEARCH_RANGE = 16.0; // 1 chunk

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            return;
        }

        Player player = mc.player;

        // Must be sneaking and holding an invisible item frame
        if (!player.isShiftKeyDown()) {
            return;
        }

        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        boolean holdingInvisibleFrame =
                (!mainHand.isEmpty() && mainHand.getItem() == ModItems.INVISIBLE_ITEM_FRAME.get()) ||
                        (!offHand.isEmpty() && offHand.getItem() == ModItems.INVISIBLE_ITEM_FRAME.get());

        if (!holdingInvisibleFrame) {
            return;
        }

        // Find all invisible item frames within 1 chunk range (vanilla + modded)
        AABB searchBox = player.getBoundingBox().inflate(SEARCH_RANGE);
        List<ItemFrame> vanillaFrames = mc.level.getEntitiesOfClass(
                ItemFrame.class, searchBox, Entity::isInvisible
        );
        List<ColoredItemFrameEntity> moddedFrames = mc.level.getEntitiesOfClass(
                ColoredItemFrameEntity.class, searchBox, Entity::isInvisible
        );

        if (vanillaFrames.isEmpty() && moddedFrames.isEmpty()) {
            return;
        }

        Camera camera = event.getCamera();
        Vec3 cameraPos = camera.getPosition();
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

        VertexConsumer lineBuffer = bufferSource.getBuffer(RenderType.lines());

        for (ItemFrame frame : vanillaFrames) {
            renderOutline(poseStack, lineBuffer, cameraPos, frame.getBoundingBox());
        }
        for (ColoredItemFrameEntity frame : moddedFrames) {
            renderOutline(poseStack, lineBuffer, cameraPos, frame.getBoundingBox());
        }

        bufferSource.endBatch(RenderType.lines());
    }

    private static void renderOutline(PoseStack poseStack, VertexConsumer buffer,
                                      Vec3 cameraPos, AABB bb) {
        float minX = (float) (bb.minX - cameraPos.x);
        float minY = (float) (bb.minY - cameraPos.y);
        float minZ = (float) (bb.minZ - cameraPos.z);
        float maxX = (float) (bb.maxX - cameraPos.x);
        float maxY = (float) (bb.maxY - cameraPos.y);
        float maxZ = (float) (bb.maxZ - cameraPos.z);

        // Light cyan color: R=0, G=200, B=255, A=255
        int r = 0;
        int g = 200;
        int b = 255;
        int a = 255;

        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();

        // Bottom face edges
        drawLine(buffer, pose, normal, minX, minY, minZ, maxX, minY, minZ, r, g, b, a);
        drawLine(buffer, pose, normal, maxX, minY, minZ, maxX, minY, maxZ, r, g, b, a);
        drawLine(buffer, pose, normal, maxX, minY, maxZ, minX, minY, maxZ, r, g, b, a);
        drawLine(buffer, pose, normal, minX, minY, maxZ, minX, minY, minZ, r, g, b, a);

        // Top face edges
        drawLine(buffer, pose, normal, minX, maxY, minZ, maxX, maxY, minZ, r, g, b, a);
        drawLine(buffer, pose, normal, maxX, maxY, minZ, maxX, maxY, maxZ, r, g, b, a);
        drawLine(buffer, pose, normal, maxX, maxY, maxZ, minX, maxY, maxZ, r, g, b, a);
        drawLine(buffer, pose, normal, minX, maxY, maxZ, minX, maxY, minZ, r, g, b, a);

        // Vertical edges
        drawLine(buffer, pose, normal, minX, minY, minZ, minX, maxY, minZ, r, g, b, a);
        drawLine(buffer, pose, normal, maxX, minY, minZ, maxX, maxY, minZ, r, g, b, a);
        drawLine(buffer, pose, normal, maxX, minY, maxZ, maxX, maxY, maxZ, r, g, b, a);
        drawLine(buffer, pose, normal, minX, minY, maxZ, minX, maxY, maxZ, r, g, b, a);
    }

    private static void drawLine(VertexConsumer buffer, Matrix4f pose, Matrix3f normal,
                                 float x1, float y1, float z1,
                                 float x2, float y2, float z2,
                                 int r, int g, int b, int a) {
        buffer.vertex(pose, x1, y1, z1)
                .color(r, g, b, a)
                .normal(normal, 0, 1, 0)
                .endVertex();
        buffer.vertex(pose, x2, y2, z2)
                .color(r, g, b, a)
                .normal(normal, 0, 1, 0)
                .endVertex();
    }
}
