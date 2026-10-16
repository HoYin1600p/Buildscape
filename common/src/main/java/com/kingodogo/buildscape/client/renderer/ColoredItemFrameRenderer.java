package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.entity.ColoredItemFrameEntity;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class ColoredItemFrameRenderer {

    private static final CommonId DEFAULT_TEXTURE =
            CommonId.of(BuildscapeCommon.MOD_ID, "textures/entity/white_item_frame.png");

    public static CommonId getTextureForColor(String color) {
        if (color == null || color.isEmpty()) {
            return DEFAULT_TEXTURE;
        }
        return CommonId.of(BuildscapeCommon.MOD_ID, "textures/entity/" + color + "_item_frame.png");
    }

    public static <T extends Entity & ColoredItemFrameEntity> Vec3 getRenderOffset(T entity, float partialTicks) {
        Direction dir = entity.getDirection();
        if (dir == null) return Vec3.ZERO;
        return new Vec3(
                (double) dir.getStepX() * 0.3D,
                -0.25D,
                (double) dir.getStepZ() * 0.3D
        );
    }

    public static <T extends Entity & ColoredItemFrameEntity> void render(
            T entity, float entityYaw, float partialTicks,
            PoseStack poseStack, Object buffer, int packedLight) {
        Direction direction = entity.getDirection();
        if (direction == null) {
            return;
        }

        if (direction == Direction.UP || direction == Direction.DOWN) {
            BlockPos lightPos = entity.blockPosition().relative(direction);
            packedLight = Services.PLATFORM.getLightColor(Services.PLATFORM.getEntityLevel(entity), lightPos);
        }

        poseStack.pushPose();

        Vec3 renderOffset = getRenderOffset(entity, partialTicks);
        poseStack.translate(-renderOffset.x(), -renderOffset.y(), -renderOffset.z());

        double wallOffset = 0.469D;
        poseStack.translate(
                (double) direction.getStepX() * wallOffset,
                (double) direction.getStepY() * wallOffset,
                (double) direction.getStepZ() * wallOffset
        );

        Services.PLATFORM.rotateX(poseStack, entity.getXRot());
        Services.PLATFORM.rotateY(poseStack, 180.0F - entity.getYRot());

        boolean isInvisible = entity.isInvisible();
        ItemStack itemStack = entity.getItem();
        boolean hasMap = Services.PLATFORM.isMapItemWithData(itemStack, Services.PLATFORM.getEntityLevel(entity));

        if (!isInvisible) {
            CommonId backTexture = getTextureForColor(entity.getColorVariant());
            Services.PLATFORM.renderColoredFrame(poseStack, buffer, packedLight, backTexture, hasMap);
        }

        if (itemStack != null && !itemStack.isEmpty()) {
            Services.PLATFORM.renderColoredFrameItem(entity, itemStack, poseStack, buffer, packedLight, hasMap, isInvisible);
        }

        poseStack.popPose();
    }
}

