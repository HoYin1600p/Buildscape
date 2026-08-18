package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.block.ShelfBlock;
import com.kingodogo.buildscape.block.ShelfBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
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

import java.util.Map;
import java.util.WeakHashMap;

public class ShelfRenderer implements BlockEntityRenderer<ShelfBlockEntity> {

    /** Vanilla ShelfRenderer.ITEM_SIZE. */
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

    private static final float[] UNIT_CUBE = {-0.5F, -0.5F, -0.5F, 0.5F, 0.5F, 0.5F};

    private final ItemRenderer itemRenderer;
    private final Map<BakedModel, float[]> boundsCache = new WeakHashMap<>();

    public ShelfRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public void render(ShelfBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        Direction facing = blockEntity.getBlockState().getValue(ShelfBlock.FACING);
        float yRot = facing.getAxis().isHorizontal() ? -facing.toYRot() : 180.0F;
        boolean alignToBottom = blockEntity.getAlignItemsToBottom();
        int seed = long2int(blockEntity.getBlockPos().asLong());

        for (int slot = 0; slot < ShelfBlockEntity.MAX_ITEMS; slot++) {
            ItemStack stack = blockEntity.getItem(slot);
            if (!stack.isEmpty()) {
                this.renderItem(blockEntity, stack, poseStack, bufferSource, combinedLight, combinedOverlay, slot, yRot, alignToBottom, seed + slot);
            }
        }
    }

    private void renderItem(ShelfBlockEntity blockEntity, ItemStack stack, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, int slot, float yRot, boolean alignToBottom, int seed) {
        BakedModel model = this.itemRenderer.getModel(stack, blockEntity.getLevel(), null, seed);
        OnShelf onShelf = getOnShelfTransform(stack, model);
        float[] box = onShelf.transformedBounds(this.getModelBounds(stack, model, seed));

        double dy = -box[1];
        if (!alignToBottom) {
            dy += -(box[4] - box[1]) / 2.0;
        }
        double dx = onShelf.recentreXZ ? -(box[0] + box[3]) / 2.0 : 0.0;
        double dz = onShelf.recentreXZ ? -(box[2] + box[5]) / 2.0 : 0.0;

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(yRot));
        poseStack.translate((float)(slot - 1) * SLOT_SPACING, alignToBottom ? ALIGN_ITEMS_TO_BOTTOM : 0.0F, ITEM_DEPTH);
        poseStack.scale(ITEM_SIZE, ITEM_SIZE, ITEM_SIZE);
        poseStack.translate(dx, dy, dz);

        onShelf.apply(poseStack);

        this.itemRenderer.renderStatic(stack, ItemTransforms.TransformType.NONE, combinedLight, combinedOverlay, poseStack, bufferSource, seed);
        poseStack.popPose();
    }

    private static OnShelf getOnShelfTransform(ItemStack stack, BakedModel model) {
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

        if (model.isGui3d() && !model.isCustomRenderer()) {
            return BLOCK;
        }
        return NO_TRANSFORM;
    }

    private float[] getModelBounds(ItemStack stack, BakedModel model, int seed) {
        float[] cached = this.boundsCache.get(model);
        if (cached != null) {
            return cached;
        }

        BoundsCollector collector = new BoundsCollector();
        try {
            this.itemRenderer.renderStatic(stack, ItemTransforms.TransformType.NONE, LightTexture.FULL_BRIGHT,
                    OverlayTexture.NO_OVERLAY, new PoseStack(), collector, seed);
        } catch (Exception exception) {
            // A model that refuses to render off-screen is not worth crashing the world over.
        }

        float[] bounds = collector.result();
        this.boundsCache.put(model, bounds);
        return bounds;
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

        private Quaternion rotation() {
            Quaternion quaternion = Vector3f.XP.rotationDegrees(this.xRot);
            quaternion.mul(Vector3f.YP.rotationDegrees(this.yRot));
            quaternion.mul(Vector3f.ZP.rotationDegrees(this.zRot));
            return quaternion;
        }

        private void apply(PoseStack poseStack) {
            if (this.z != 0.0F) {
                poseStack.translate(0.0D, 0.0D, this.z / 16.0F);
            }
            if (this.xRot != 0.0F || this.yRot != 0.0F || this.zRot != 0.0F) {
                poseStack.mulPose(this.rotation());
            }
            if (this.scale != 1.0F) {
                poseStack.scale(this.scale, this.scale, this.scale);
            }
        }

        private float[] transformedBounds(float[] bounds) {
            Quaternion rotation = this.rotation();
            float[] out = {Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY,
                    Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY};
            for (int corner = 0; corner < 8; corner++) {
                Vector3f point = new Vector3f(
                        bounds[(corner & 1) == 0 ? 0 : 3],
                        bounds[(corner & 2) == 0 ? 1 : 4],
                        bounds[(corner & 4) == 0 ? 2 : 5]);
                point.transform(rotation);
                float x = point.x() * this.scale;
                float y = point.y() * this.scale;
                float z = point.z() * this.scale + this.z / 16.0F;
                out[0] = Math.min(out[0], x);
                out[1] = Math.min(out[1], y);
                out[2] = Math.min(out[2], z);
                out[3] = Math.max(out[3], x);
                out[4] = Math.max(out[4], y);
                out[5] = Math.max(out[5], z);
            }
            return out;
        }
    }

    private static final class BoundsCollector implements MultiBufferSource, VertexConsumer {
        private float minX = Float.POSITIVE_INFINITY;
        private float minY = Float.POSITIVE_INFINITY;
        private float minZ = Float.POSITIVE_INFINITY;
        private float maxX = Float.NEGATIVE_INFINITY;
        private float maxY = Float.NEGATIVE_INFINITY;
        private float maxZ = Float.NEGATIVE_INFINITY;

        private float[] result() {
            if (this.minY > this.maxY) {
                return UNIT_CUBE.clone();
            }
            return new float[]{this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ};
        }

        @Override
        public VertexConsumer getBuffer(net.minecraft.client.renderer.RenderType renderType) {
            return this;
        }

        @Override
        public VertexConsumer vertex(double x, double y, double z) {
            float fx = (float)x;
            float fy = (float)y;
            float fz = (float)z;
            if (fx < this.minX) {
                this.minX = fx;
            }
            if (fy < this.minY) {
                this.minY = fy;
            }
            if (fz < this.minZ) {
                this.minZ = fz;
            }
            if (fx > this.maxX) {
                this.maxX = fx;
            }
            if (fy > this.maxY) {
                this.maxY = fy;
            }
            if (fz > this.maxZ) {
                this.maxZ = fz;
            }
            return this;
        }

        @Override
        public VertexConsumer color(int red, int green, int blue, int alpha) {
            return this;
        }

        @Override
        public VertexConsumer uv(float u, float v) {
            return this;
        }

        @Override
        public VertexConsumer overlayCoords(int u, int v) {
            return this;
        }

        @Override
        public VertexConsumer uv2(int u, int v) {
            return this;
        }

        @Override
        public VertexConsumer normal(float x, float y, float z) {
            return this;
        }

        @Override
        public void endVertex() {
        }

        @Override
        public void defaultColor(int red, int green, int blue, int alpha) {
        }

        @Override
        public void unsetDefaultColor() {
        }
    }
}
