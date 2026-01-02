package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.block.IcicleCauldronBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;

public class IcicleCauldronBlockEntityRenderer
        implements BlockEntityRenderer<IcicleCauldronBlockEntity> {

    private final BlockRenderDispatcher blockRenderer;

    public IcicleCauldronBlockEntityRenderer(
            BlockEntityRendererProvider.Context context
    ) {
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(
            IcicleCauldronBlockEntity blockEntity,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int combinedLight,
            int combinedOverlay
    ) {
        if (blockEntity.getLevel() == null) {
            return;
        }

        poseStack.pushPose();
        BlockState cauldronState = Blocks.CAULDRON.defaultBlockState();
        BakedModel model = blockRenderer.getBlockModel(cauldronState);
        RenderType renderType = ItemBlockRenderTypes.getRenderType(
                cauldronState,
                true
        );
        blockRenderer
                .getModelRenderer()
                .renderModel(
                        poseStack.last(),
                        bufferSource.getBuffer(renderType),
                        cauldronState,
                        model,
                        1.0f,
                        1.0f,
                        1.0f,
                        combinedLight,
                        combinedOverlay,
                        EmptyModelData.INSTANCE
                );
        poseStack.popPose();

        ItemStack storedIcicle = blockEntity.getStoredIcicle();
        if (
                !storedIcicle.isEmpty() &&
                        storedIcicle.getItem() instanceof net.minecraft.world.item.BlockItem
        ) {
            net.minecraft.world.item.BlockItem blockItem =
                    (net.minecraft.world.item.BlockItem) storedIcicle.getItem();
            BlockState icicleBlockState = blockItem.getBlock().defaultBlockState();

            poseStack.pushPose();
            poseStack.translate(0.125, 0.3125, 0.125);
            poseStack.scale(0.75f, 0.625f, 0.75f);

            BakedModel icicleModel = blockRenderer.getBlockModel(icicleBlockState);
            RenderType icicleRenderType = ItemBlockRenderTypes.getRenderType(
                    icicleBlockState,
                    true
            );
            blockRenderer
                    .getModelRenderer()
                    .renderModel(
                            poseStack.last(),
                            bufferSource.getBuffer(icicleRenderType),
                            icicleBlockState,
                            icicleModel,
                            1.0f,
                            1.0f,
                            1.0f,
                            combinedLight,
                            combinedOverlay,
                            EmptyModelData.INSTANCE
                    );

            poseStack.popPose();
        }
    }

    @Override
    public boolean shouldRenderOffScreen(IcicleCauldronBlockEntity blockEntity) {
        return false;
    }
}
