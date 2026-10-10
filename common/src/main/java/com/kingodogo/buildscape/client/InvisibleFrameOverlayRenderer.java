package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.item.ModItems;
import com.kingodogo.buildscape.platform.Services;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class InvisibleFrameOverlayRenderer {

    private static final double SEARCH_RANGE = 16.0;

    public static void renderOverlay(PoseStack poseStack, Camera camera, Object bufferSource) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            return;
        }

        Player player = mc.player;
        if (!player.isShiftKeyDown()) {
            return;
        }

        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        boolean holdingInvisibleFrame =
                (!mainHand.isEmpty() && mainHand.getItem() == ModItems.INVISIBLE_ITEM_FRAME.get().asItem()) ||
                        (!offHand.isEmpty() && offHand.getItem() == ModItems.INVISIBLE_ITEM_FRAME.get().asItem());

        if (!holdingInvisibleFrame) {
            return;
        }

        AABB searchBox = player.getBoundingBox().inflate(SEARCH_RANGE);
        List<ItemFrame> vanillaFrames = mc.level.getEntitiesOfClass(
                ItemFrame.class, searchBox, Entity::isInvisible
        );

        if (vanillaFrames.isEmpty()) {
            return;
        }

        Vec3 cameraPos = Services.PLATFORM.getCameraPosition(camera);

        float time = (System.currentTimeMillis() % 2000) / 2000.0f;
        float pulse = (float) Math.sin(time * Math.PI * 2.0) * 0.5f + 0.5f;
        float lineAlpha = 0.4f + pulse * 0.4f;

        float r = 0.0f;
        float g = 200.0f / 255.0f;
        float b = 1.0f;

        for (ItemFrame frame : vanillaFrames) {
            AABB bb = frame.getBoundingBox().inflate(0.002);
            Services.PLATFORM.renderLineBox(
                    poseStack, bufferSource,
                    bb.minX - cameraPos.x, bb.minY - cameraPos.y, bb.minZ - cameraPos.z,
                    bb.maxX - cameraPos.x, bb.maxY - cameraPos.y, bb.maxZ - cameraPos.z,
                    r, g, b, lineAlpha
            );
        }

    }
}
