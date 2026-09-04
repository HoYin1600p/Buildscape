package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.cosmetic.sign.SignFrameAttachment;
import com.kingodogo.buildscape.cosmetic.sign.SignFrameType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;

/**
 * Handles rendering cosmetic frames over signs.
 * Aligns properly with both standing signs (at any rotation) and wall signs (on any wall).
 */
public class SignFrameRenderer {

    public static void render(
            SignBlockEntity blockEntity,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int combinedLight,
            int combinedOverlay
    ) {
        if (blockEntity == null || blockEntity.getLevel() == null) {
            return;
        }

        SignFrameType frameType = SignFrameAttachment.getFrame(blockEntity);
        if (frameType == SignFrameType.NONE || frameType.getModelLocation() == null) {
            return;
        }

        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        BakedModel frameModel = modelManager.getModel(frameType.getModelLocation());
        if (frameModel == null || frameModel == modelManager.getMissingModel()) {
            return;
        }

        BlockState state = blockEntity.getBlockState();
        Block block = state.getBlock();

        poseStack.pushPose();

        if (block instanceof WallSignBlock) {
            Direction facing = state.getValue(WallSignBlock.FACING);
            float yRot = (facing.toYRot() + 180.0F) % 360.0F;

            poseStack.translate(0.5D, 0.5D, 0.5D);
            poseStack.mulPose(Vector3f.YP.rotationDegrees(-yRot));
            poseStack.translate(-0.5D, -0.5D, -0.5D);
        } else if (block instanceof StandingSignBlock) {
            float rotation = (float) (state.getValue(StandingSignBlock.ROTATION) * 360) / 16.0F;
            float yRot = (rotation + 180.0F) % 360.0F;

            poseStack.translate(0.5D, 0.5D, 0.5D);
            poseStack.mulPose(Vector3f.YP.rotationDegrees(-yRot));
            // Offset from North wall sign board position to standing sign board position
            poseStack.translate(0.0D, 0.3125D, -0.4375D);
            poseStack.translate(-0.5D, -0.5D, -0.5D);
        } else {
            poseStack.popPose();
            return;
        }

        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        dispatcher.getModelRenderer().renderModel(
                poseStack.last(),
                bufferSource.getBuffer(RenderType.cutout()),
                state,
                frameModel,
                1.0F, 1.0F, 1.0F,
                combinedLight,
                combinedOverlay,
                EmptyModelData.INSTANCE
        );

        poseStack.popPose();
    }
}
