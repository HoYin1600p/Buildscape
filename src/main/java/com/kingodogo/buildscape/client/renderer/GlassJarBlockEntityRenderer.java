package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.block.GlassJarBlockEntity;
import com.kingodogo.buildscape.platform.Services;
import com.mojang.blaze3d.vertex.PoseStack;

public class GlassJarBlockEntityRenderer {
    public static void render(
            GlassJarBlockEntity blockEntity,
            float partialTicks,
            PoseStack poseStack,
            Object bufferSource,
            int combinedLight,
            int combinedOverlay) {
        if (blockEntity == null) return;
        Services.PLATFORM.renderGlassJar(blockEntity, partialTicks, poseStack, bufferSource, combinedLight, combinedOverlay);
    }
}

