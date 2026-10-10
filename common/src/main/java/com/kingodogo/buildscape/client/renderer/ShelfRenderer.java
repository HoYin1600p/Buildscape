package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.block.ShelfBlock;
import com.kingodogo.buildscape.block.ShelfBlockEntity;
import com.kingodogo.buildscape.platform.Services;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.phys.AABB;

public class ShelfRenderer {

    private static final float ITEM_SIZE = 0.25F;
    private static final float ALIGN_ITEMS_TO_BOTTOM = -0.25F;
    private static final float SLOT_SPACING = 0.3125F;
    private static final float ITEM_DEPTH = -0.25F;

    private static final OnShelf NO_TRANSFORM = new OnShelf(0.0F, 0.0F, 0.0F, 0.0F, 1.0F, false);
    private static final OnShelf BLOCK = new OnShelf(0.0F, 180.0F, 0.0F, 0.0F, 1.0F, false);
    private static final OnShelf FENCE_WALL_ANVIL = new OnShelf(0.0F, 90.0F, 0.0F, 0.0F, 1.0F, false);
    private static final OnShelf SHELF = new OnShelf(0.0F, 180.0F, 0.0F, 4.0F, 1.0F, false);
    private static final OnShelf SKULL = new OnShelf(0.0F, 0.0F, 0.0F, 0.0F, 2.0F, false);
    private static final OnShelf DRAGON_HEAD = new OnShelf(0.0F, 0.0F, 0.0F, 0.0F, 1.25F, false);
    private static final OnShelf BED = new OnShelf(90.0F, 180.0F, 0.0F, 0.0F, 0.9375F, false);
    private static final OnShelf SHIELD = new OnShelf(0.0F, 0.0F, 0.0F, 0.0F, 1.4F, true);

    public static void render(ShelfBlockEntity blockEntity, float partialTicks, PoseStack poseStack, Object bufferSource, int combinedLight, int combinedOverlay) {
        if (blockEntity == null) return;
        Direction facing = blockEntity.getBlockState().getValue(ShelfBlock.FACING);
        float yRot = facing.getAxis().isHorizontal() ? -facing.toYRot() : 180.0F;
        boolean alignToBottom = blockEntity.getAlignItemsToBottom();
        int seed = long2int(blockEntity.getBlockPos().asLong());

        for (int slot = 0; slot < ShelfBlockEntity.MAX_ITEMS; slot++) {
            ItemStack stack = blockEntity.getItem(slot);
            if (!stack.isEmpty()) {
                renderItem(blockEntity, stack, poseStack, bufferSource, combinedLight, combinedOverlay, slot, yRot, alignToBottom, seed + slot);
            }
        }
    }

    private static void renderItem(ShelfBlockEntity blockEntity, ItemStack stack, PoseStack poseStack, Object bufferSource, int combinedLight, int combinedOverlay, int slot, float yRot, boolean alignToBottom, int seed) {
        Object model = Services.PLATFORM.getItemModel(stack, blockEntity.getLevel(), seed);
        OnShelf onShelf = getOnShelfTransform(stack, model);
        AABB rawBounds = Services.PLATFORM.getModelBounds(model);
        double[] box = onShelf.transformedBounds(rawBounds);

        double dy = -box[1];
        if (!alignToBottom) {
            dy += -(box[4] - box[1]) / 2.0;
        }
        double dx = onShelf.recentreXZ ? -(box[0] + box[3]) / 2.0 : 0.0;
        double dz = onShelf.recentreXZ ? -(box[2] + box[5]) / 2.0 : 0.0;

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        Services.PLATFORM.rotateY(poseStack, yRot);
        poseStack.translate((float)(slot - 1) * SLOT_SPACING, alignToBottom ? ALIGN_ITEMS_TO_BOTTOM : 0.0F, ITEM_DEPTH);
        poseStack.scale(ITEM_SIZE, ITEM_SIZE, ITEM_SIZE);
        poseStack.translate(dx, dy, dz);

        onShelf.apply(poseStack);

        Services.PLATFORM.renderItemStatic(stack, poseStack, bufferSource, combinedLight, combinedOverlay, seed);
        poseStack.popPose();
    }

    private static OnShelf getOnShelfTransform(ItemStack stack, Object model) {
        if (stack.is(Items.SHIELD)) {
            return SHIELD;
        }
        if (stack.is(Items.DRAGON_HEAD)) {
            return DRAGON_HEAD;
        }

        Block block = stack.getItem() instanceof BlockItem ? ((BlockItem)stack.getItem()).getBlock() : null;
        if (block instanceof AbstractSkullBlock) {
            return SKULL;
        }
        if (block instanceof BedBlock) {
            return BED;
        }
        if (block instanceof ShelfBlock) {
            return SHELF;
        }
        if (block instanceof FenceBlock || block instanceof WallBlock || block instanceof AnvilBlock) {
            return FENCE_WALL_ANVIL;
        }

        if (Services.PLATFORM.isGui3dModel(model)) {
            return BLOCK;
        }
        return NO_TRANSFORM;
    }

    private static int long2int(long value) {
        return (int)(value ^ value >>> 32);
    }

    private static final class OnShelf {
        private final float xRot;
        private final float yRot;
        private final float zRot;
        private final float z;
        private final float scale;
        private final boolean recentreXZ;

        private OnShelf(float xRot, float yRot, float zRot, float z, float scale, boolean recentreXZ) {
            this.xRot = xRot;
            this.yRot = yRot;
            this.zRot = zRot;
            this.z = z;
            this.scale = scale;
            this.recentreXZ = recentreXZ;
        }

        private void apply(PoseStack poseStack) {
            if (this.z != 0.0F) {
                poseStack.translate(0.0D, 0.0D, this.z / 16.0F);
            }
            if (this.xRot != 0.0F) Services.PLATFORM.rotateX(poseStack, this.xRot);
            if (this.yRot != 0.0F) Services.PLATFORM.rotateY(poseStack, this.yRot);
            if (this.zRot != 0.0F) Services.PLATFORM.rotateZ(poseStack, this.zRot);
            if (this.scale != 1.0F) {
                poseStack.scale(this.scale, this.scale, this.scale);
            }
        }

        private double[] transformedBounds(AABB raw) {
            double[] bounds = new double[]{raw.minX, raw.minY, raw.minZ, raw.maxX, raw.maxY, raw.maxZ};
            double[] out = {Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY,
                    Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY};

            for (int corner = 0; corner < 8; corner++) {
                double px = bounds[(corner & 1) == 0 ? 0 : 3];
                double py = bounds[(corner & 2) == 0 ? 1 : 4];
                double pz = bounds[(corner & 4) == 0 ? 2 : 5];

                double[] rot = rotatePoint(px, py, pz, this.xRot, this.yRot, this.zRot);
                double x = rot[0] * this.scale;
                double y = rot[1] * this.scale;
                double z = rot[2] * this.scale + this.z / 16.0F;

                out[0] = Math.min(out[0], x);
                out[1] = Math.min(out[1], y);
                out[2] = Math.min(out[2], z);
                out[3] = Math.max(out[3], x);
                out[4] = Math.max(out[4], y);
                out[5] = Math.max(out[5], z);
            }
            return out;
        }

        private static double[] rotatePoint(double x, double y, double z, double xRot, double yRot, double zRot) {
            double rx = Math.toRadians(xRot);
            double ry = Math.toRadians(yRot);
            double rz = Math.toRadians(zRot);

            double cosX = Math.cos(rx);
            double sinX = Math.sin(rx);
            double y1 = y * cosX - z * sinX;
            double z1 = y * sinX + z * cosX;
            double x1 = x;

            double cosY = Math.cos(ry);
            double sinY = Math.sin(ry);
            double x2 = x1 * cosY + z1 * sinY;
            double z2 = -x1 * sinY + z1 * cosY;
            double y2 = y1;

            double cosZ = Math.cos(rz);
            double sinZ = Math.sin(rz);
            double x3 = x2 * cosZ - y2 * sinZ;
            double y3 = x2 * sinZ + y2 * cosZ;
            double z3 = z2;

            return new double[]{x3, y3, z3};
        }
    }
}
