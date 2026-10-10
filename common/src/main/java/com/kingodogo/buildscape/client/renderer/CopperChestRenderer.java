package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.block.CopperChestBlockEntity;
import com.kingodogo.buildscape.platform.Services;
import com.mojang.blaze3d.vertex.PoseStack;

public class CopperChestRenderer {
    public static void render(
            CopperChestBlockEntity blockEntity,
            float partialTicks,
            PoseStack poseStack,
            Object bufferSource,
            int combinedLight,
            int combinedOverlay) {
        if (blockEntity == null) return;
        Services.PLATFORM.renderCopperChest(blockEntity, partialTicks, poseStack, bufferSource, combinedLight, combinedOverlay);
    }
}

