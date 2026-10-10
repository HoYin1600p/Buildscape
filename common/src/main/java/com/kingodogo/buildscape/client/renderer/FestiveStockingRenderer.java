package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.entity.FestiveStockingEntity;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;

public class FestiveStockingRenderer {

    private static final CommonId STOCKING_TEXTURE = CommonId.of(
            BuildscapeCommon.MOD_ID, "textures/entity/festive_stocking.png"
    );

    public static CommonId getTextureForColor(String color) {
        if (color == null || color.equals("festive")) {
            return STOCKING_TEXTURE;
        }
        return CommonId.of(
                BuildscapeCommon.MOD_ID, "textures/entity/" + color + "_festive_stocking.png"
        );
    }

    public static <T extends Entity & FestiveStockingEntity> void render(
            T entity,
            float entityYaw,
            float partialTicks,
            PoseStack poseStack,
            Object buffer,
            int packedLight
    ) {
        Direction direction = entity.getDirection();
        if (direction == null) {
            return;
        }

        poseStack.pushPose();

        float rotationY = 0.0F;
        float rotationX = 0.0F;

        if (direction == Direction.SOUTH) {
            rotationY = 180.0F;
        } else if (direction == Direction.WEST) {
            rotationY = 90.0F;
        } else if (direction == Direction.EAST) {
            rotationY = 270.0F;
        } else if (direction == Direction.UP) {
            rotationX = -90.0F;
            rotationY = 180.0F;
        } else if (direction == Direction.DOWN) {
            rotationX = 90.0F;
            rotationY = 180.0F;
        }

        poseStack.translate(0.0D, 0.0D, 0.0D);
        Services.PLATFORM.rotateY(poseStack, rotationY);
        Services.PLATFORM.rotateX(poseStack, rotationX);
        poseStack.translate(0.0D, 0.0D, 0.0625D);

        CommonId texture = getTextureForColor(entity.getColorVariant());
        Services.PLATFORM.renderStockingQuad(poseStack, buffer, packedLight, texture, false);

        poseStack.popPose();
    }
}

