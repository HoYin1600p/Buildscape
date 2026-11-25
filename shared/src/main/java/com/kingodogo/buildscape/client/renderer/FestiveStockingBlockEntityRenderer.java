package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.block.FestiveStockingBlock;
import com.kingodogo.buildscape.block.FestiveStockingBlockEntity;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class FestiveStockingBlockEntityRenderer {

    private static final CommonId STOCKING_TEXTURE = CommonId.of(
            BuildscapeCommon.MOD_ID, "textures/entity/festive_stocking.png"
    );

    private static CommonId getTextureForColor(String color) {
        if (color == null || color.equals("festive")) {
            return STOCKING_TEXTURE;
        }
        return CommonId.of(
                BuildscapeCommon.MOD_ID, "textures/entity/" + color + "_festive_stocking.png"
        );
    }

    public static void render(
            FestiveStockingBlockEntity blockEntity,
            float partialTicks,
            PoseStack poseStack,
            Object buffer,
            int packedLight,
            int packedOverlay
    ) {

        if (blockEntity == null || blockEntity.getLevel() == null) {
            return;
        }

        BlockState state = blockEntity.getBlockState();
        if (!(state.getBlock() instanceof FestiveStockingBlock block)) {
            return;
        }

        Direction facing = state.getValue(FestiveStockingBlock.FACING);
        boolean flipped = state.getValue(FestiveStockingBlock.FLIPPED);

        poseStack.pushPose();

        float rotationY = 0.0F;
        float rotationX = 0.0F;
        double posX = 0.5D;
        double posY = 0.5D;
        double posZ = 0.0D;
        double forwardOffset = 0.001D;

        switch (facing) {
            case NORTH:
                posX = 0.5D;
                posY = 0.5D;
                posZ = 1.0D - forwardOffset;
                rotationY = 180.0F;
                break;
            case SOUTH:
                posX = 0.5D;
                posY = 0.5D;
                posZ = forwardOffset;
                rotationY = 0.0F;
                break;
            case WEST:
                posX = 1.0D - forwardOffset;
                posY = 0.5D;
                posZ = 0.5D;
                rotationY = 90.0F;
                break;
            case EAST:
                posX = forwardOffset;
                posY = 0.5D;
                posZ = 0.5D;
                rotationY = 270.0F;
                break;
            case UP:
                posX = 0.5D;
                posY = 0.5D / 16.0D;
                posZ = 0.5D;
                rotationX = -90.0F;
                rotationY = 180.0F;
                break;
            case DOWN:
                posX = 0.5D;
                posY = 15.5D / 16.0D;
                posZ = 0.5D;
                rotationX = 90.0F;
                rotationY = 180.0F;
                break;
        }

        poseStack.translate(posX, posY, posZ);
        Services.PLATFORM.rotateY(poseStack, rotationY);
        Services.PLATFORM.rotateX(poseStack, rotationX);
        CommonId texture = getTextureForColor(block.getColorVariant());
        Services.PLATFORM.renderStockingQuad(poseStack, buffer, packedLight, texture, flipped);

        poseStack.popPose();
    }
}

