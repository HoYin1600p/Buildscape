package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.entity.FallingIcicleEntity;
import com.kingodogo.buildscape.platform.Services;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public class FallingIcicleRenderer {

    public static <T extends Entity & FallingIcicleEntity> void render(
            T entity,
            float entityYaw,
            float partialTicks,
            PoseStack poseStack,
            Object bufferSource,
            int packedLight
    ) {
        BlockState blockState = entity.getBlockState();
        if (blockState != null && blockState.getRenderShape() == RenderShape.MODEL) {
            Level level = Services.PLATFORM.getEntityLevel(entity);
            if (blockState.getRenderShape() != RenderShape.INVISIBLE) {
                poseStack.pushPose();
                BlockPos blockPos = new BlockPos(
                        Mth.floor(entity.getX()),
                        Mth.floor(entity.getBoundingBox().maxY),
                        Mth.floor(entity.getZ())
                );
                poseStack.translate(-0.5D, 0.0D, -0.5D);

                Services.PLATFORM.renderFallingIcicleBlock(
                        level,
                        blockState,
                        blockPos,
                        entity.getStartPos(),
                        poseStack,
                        bufferSource
                );

                poseStack.popPose();
            }
        }
    }
}

