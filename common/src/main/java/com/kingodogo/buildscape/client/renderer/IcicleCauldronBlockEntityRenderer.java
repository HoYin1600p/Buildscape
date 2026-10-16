package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.block.IcicleCauldronBlockEntity;
import com.kingodogo.buildscape.platform.Services;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class IcicleCauldronBlockEntityRenderer {
    public static void render(
            IcicleCauldronBlockEntity blockEntity,
            float partialTicks,
            PoseStack poseStack,
            Object bufferSource,
            int combinedLight,
            int combinedOverlay
    ) {
        if (blockEntity == null || blockEntity.getLevel() == null) {
            return;
        }

        poseStack.pushPose();
        BlockState cauldronState = Blocks.CAULDRON.defaultBlockState();
        Services.PLATFORM.renderBlockModel(cauldronState, poseStack, bufferSource, combinedLight, combinedOverlay);
        poseStack.popPose();

        ItemStack storedIcicle = blockEntity.getStoredIcicle();
        if (!storedIcicle.isEmpty() && storedIcicle.getItem() instanceof BlockItem blockItem) {
            BlockState icicleBlockState = blockItem.getBlock().defaultBlockState();

            poseStack.pushPose();
            poseStack.translate(0.125, 0.3125, 0.125);
            poseStack.scale(0.75f, 0.625f, 0.75f);

            Services.PLATFORM.renderBlockModel(icicleBlockState, poseStack, bufferSource, combinedLight, combinedOverlay);

            poseStack.popPose();
        }
    }
}

