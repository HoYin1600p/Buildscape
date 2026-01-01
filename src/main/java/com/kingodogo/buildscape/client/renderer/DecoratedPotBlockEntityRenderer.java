package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.block.DecoratedPotBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;

public class DecoratedPotBlockEntityRenderer
        implements BlockEntityRenderer<DecoratedPotBlockEntity> {

    private final BlockRenderDispatcher blockRenderer;

    private static final int WOBBLE_DURATION = 10;
    private static final float MAX_ROTATION_DEGREES = 8.0f;

    public DecoratedPotBlockEntityRenderer(
            BlockEntityRendererProvider.Context context
    ) {
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(
            DecoratedPotBlockEntity blockEntity,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int combinedLight,
            int combinedOverlay
    ) {
        if (blockEntity.getLevel() == null) {
            return;
        }

        long currentTick = blockEntity.getLevel().getGameTime();
        long wobbleStartTick = blockEntity.getWobbleStartedAtTick();
        DecoratedPotBlockEntity.WobbleStyle wobbleStyle =
                blockEntity.getLastWobbleStyle();

        float wobbleProgress = 0.0f;
        if (
                wobbleStyle != DecoratedPotBlockEntity.WobbleStyle.NONE &&
                        wobbleStartTick > 0
        ) {
            float ticksSinceWobble =
                    (float) (currentTick - wobbleStartTick) + partialTicks;
            if (ticksSinceWobble < WOBBLE_DURATION) {
                wobbleProgress = ticksSinceWobble / WOBBLE_DURATION;
            }
        }

        poseStack.pushPose();

        if (wobbleProgress > 0.0f && wobbleProgress < 1.0f) {
            float dampening = 1.0f - wobbleProgress;
            float oscillation = (float) Math.sin(wobbleProgress * Math.PI * 6);
            float rotationAngle = MAX_ROTATION_DEGREES * dampening * oscillation;

            poseStack.translate(0.5, 0.0, 0.5);
            poseStack.mulPose(Vector3f.YP.rotationDegrees(rotationAngle));
            poseStack.translate(-0.5, 0.0, -0.5);
        }

        BlockState blockState = blockEntity.getBlockState();
        BakedModel model = blockRenderer.getBlockModel(blockState);
        RenderType renderType = ItemBlockRenderTypes.getRenderType(
                blockState,
                true
        );
        blockRenderer
                .getModelRenderer()
                .renderModel(
                        poseStack.last(),
                        bufferSource.getBuffer(renderType),
                        blockState,
                        model,
                        1.0f,
                        1.0f,
                        1.0f,
                        combinedLight,
                        combinedOverlay,
                        EmptyModelData.INSTANCE
                );

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(DecoratedPotBlockEntity blockEntity) {
        return false;
    }
}
// Kingodogo Finished this File on 2025-12-10 20-50-05
