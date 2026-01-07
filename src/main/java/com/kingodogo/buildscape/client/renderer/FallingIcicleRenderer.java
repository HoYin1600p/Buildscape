package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.entity.FallingIcicleEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public class FallingIcicleRenderer extends EntityRenderer<FallingIcicleEntity> {

    public FallingIcicleRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.5F;
    }

    private BlockRenderDispatcher getBlockRenderer() {
        return Minecraft.getInstance().getBlockRenderer();
    }

    @Override
    public void render(
            FallingIcicleEntity entity,
            float entityYaw,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight
    ) {
        BlockState blockState = entity.getBlockState();
        if (blockState.getRenderShape() == RenderShape.MODEL) {
            Level level = entity.level;
            if (blockState.getRenderShape() != RenderShape.INVISIBLE) {
                poseStack.pushPose();
                BlockPos blockPos = new BlockPos(
                        entity.getX(),
                        entity.getBoundingBox().maxY,
                        entity.getZ()
                );
                poseStack.translate(-0.5D, 0.0D, -0.5D);

                BlockRenderDispatcher blockRenderer = getBlockRenderer();
                net.minecraft.client.renderer.RenderType renderType =
                        net.minecraft.client.renderer.RenderType.cutout();
                blockRenderer
                        .getModelRenderer()
                        .tesselateBlock(
                                level,
                                blockRenderer.getBlockModel(blockState),
                                blockState,
                                blockPos,
                                poseStack,
                                bufferSource.getBuffer(renderType),
                                false,
                                level.random,
                                blockState.getSeed(entity.getStartPos()),
                                OverlayTexture.NO_OVERLAY
                        );

                poseStack.popPose();
                super.render(
                        entity,
                        entityYaw,
                        partialTicks,
                        poseStack,
                        bufferSource,
                        packedLight
                );
            }
        }
    }

    @Override
    public ResourceLocation getTextureLocation(FallingIcicleEntity entity) {
        return net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS;
    }
}
// Kingodogo Finished this File on 2025-12-10 20-50-05
