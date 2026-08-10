package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.block.CopperChestBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class CopperChestItemRenderer extends BlockEntityWithoutLevelRenderer {
    public static final CopperChestItemRenderer INSTANCE = new CopperChestItemRenderer();
    private CopperChestBlockEntity dummyBE;

    public CopperChestItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(
            ItemStack stack,
            ItemTransforms.TransformType transformType,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int combinedLight,
            int combinedOverlay) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            BlockState state = blockItem.getBlock().defaultBlockState();

            if (dummyBE == null) {
                dummyBE = new CopperChestBlockEntity(BlockPos.ZERO, state);
            } else {
                dummyBE.setBlockState(state);
            }

            if (Minecraft.getInstance().level != null) {
                dummyBE.setLevel(Minecraft.getInstance().level);
            }

            net.minecraft.client.renderer.blockentity.BlockEntityRenderer<CopperChestBlockEntity> renderer = Minecraft
                    .getInstance()
                    .getBlockEntityRenderDispatcher()
                    .getRenderer(dummyBE);
            if (renderer != null) {
                poseStack.pushPose();
                poseStack.translate(0.5D, 0.5D, 0.5D);
                poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(270.0F));
                poseStack.translate(-0.5D, -0.5D, -0.5D);

                renderer.render(dummyBE, 0.0F, poseStack, bufferSource, combinedLight, combinedOverlay);

                poseStack.popPose();
            }
        }
    }
}
