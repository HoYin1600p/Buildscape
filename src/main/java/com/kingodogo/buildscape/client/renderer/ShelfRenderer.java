package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.block.ShelfBlock;
import com.kingodogo.buildscape.block.ShelfBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

public class ShelfRenderer implements BlockEntityRenderer<ShelfBlockEntity> {
    private final ItemRenderer itemRenderer;

    public ShelfRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public void render(ShelfBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        Direction facing = blockEntity.getBlockState().getValue(ShelfBlock.FACING);
        float yRot = -facing.toYRot();
        boolean alignToBottom = blockEntity.getAlignItemsToBottom();

        for (int slot = 0; slot < 3; slot++) {
            ItemStack stack = blockEntity.getItem(slot);
            if (!stack.isEmpty()) {
                poseStack.pushPose();
                poseStack.translate(0.5D, 0.5D, 0.5D);
                poseStack.mulPose(Vector3f.YP.rotationDegrees(yRot));

                float itemSlotPosition = (float)(slot - 1) * 0.3125F;
                poseStack.translate(itemSlotPosition, alignToBottom ? -0.25D : 0.0D, -0.25D);
                poseStack.scale(0.375F, 0.375F, 0.375F);

                this.itemRenderer.renderStatic(stack, ItemTransforms.TransformType.FIXED, combinedLight, combinedOverlay, poseStack, bufferSource, blockEntity.getBlockPos().hashCode() + slot);
                poseStack.popPose();
            }
        }
    }
}
