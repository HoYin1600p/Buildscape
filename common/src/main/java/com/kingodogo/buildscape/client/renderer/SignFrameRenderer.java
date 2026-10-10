package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.mixin.MixinFactory;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.level.block.entity.SignBlockEntity;

public class SignFrameRenderer {

    public static void render(
            SignBlockEntity blockEntity,
            float partialTicks,
            PoseStack poseStack,
            Object bufferSource,
            int combinedLight,
            int combinedOverlay
    ) {
        MixinFactory.renderSignFrame(blockEntity, partialTicks, poseStack, bufferSource, combinedLight, combinedOverlay);
    }
}
