package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.block.TrappedDecoratedPotBlockEntity;
import com.kingodogo.buildscape.platform.Services;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class TrappedDecoratedPotBlockEntityRenderer {
    public static void render(
            TrappedDecoratedPotBlockEntity blockEntity,
            float partialTicks,
            PoseStack poseStack,
            Object bufferSource,
            int combinedLight,
            int combinedOverlay
    ) {
        if (blockEntity == null || blockEntity.getLevel() == null) {
            return;
        }

        Level level = blockEntity.getLevel();
        long currentTick = level.getGameTime();
        long wobbleStartTick = blockEntity.getWobbleStartedAtTick();
        TrappedDecoratedPotBlockEntity.WobbleStyle wobbleStyle = blockEntity.getLastWobbleStyle();
        boolean hasWobble = wobbleStyle != TrappedDecoratedPotBlockEntity.WobbleStyle.NONE;
        BlockState blockState = blockEntity.getBlockState();

        Services.PLATFORM.renderWobblyBlock(blockState, currentTick, wobbleStartTick, hasWobble, partialTicks, poseStack, bufferSource, combinedLight, combinedOverlay);
    }
}

