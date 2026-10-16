package com.kingodogo.buildscape.adapter.v26x.client;

import com.kingodogo.buildscape.block.ShelfBlock;
import com.kingodogo.buildscape.block.ShelfBlockEntity;
import com.kingodogo.buildscape.client.renderer.ShelfRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;

public final class ShelfBlockRenderer implements BlockEntityRenderer<ShelfBlockEntity, ShelfBlockRenderer.State> {
    public record Offset(double x, double y, double z) {}

    public static final class Slot {
        final ItemStackRenderState item = new ItemStackRenderState();
        ShelfRenderer.OnShelf transform;
        Offset offset;
    }

    public static final class State extends BlockEntityRenderState {
        final Slot[] slots = new Slot[ShelfBlockEntity.MAX_ITEMS];
        float yaw;
        boolean alignToBottom;

        State() {
            for (int i = 0; i < slots.length; i++) slots[i] = new Slot();
        }
    }

    @Override public State createRenderState() { return new State(); }

    @Override public void extractRenderState(ShelfBlockEntity entity, State state, float partialTick,
            Vec3 camera, ModelFeatureRenderer.CrumblingOverlay breaking) {
        BlockEntityRenderState.extractBase(entity, state, breaking);
        Direction facing = entity.getBlockState().getValue(ShelfBlock.FACING);
        state.yaw = facing.getAxis().isHorizontal() ? -facing.toYRot() : 180;
        state.alignToBottom = entity.getAlignItemsToBottom();
        long position = entity.getBlockPos().asLong();
        int seed = (int) (position ^ position >>> 32);
        var resolver = Minecraft.getInstance().getItemModelResolver();
        for (int i = 0; i < state.slots.length; i++) {
            Slot slot = state.slots[i];
            slot.item.clear();
            slot.transform = null;
            slot.offset = null;
            var stack = entity.getItem(i);
            if (stack.isEmpty()) continue;
            resolver.updateForTopItem(slot.item, stack, ItemDisplayContext.NONE, entity.getLevel(), null, seed + i);
            slot.transform = ShelfRenderer.getOnShelfTransform(stack, slot.item);
            double[] bounds = slot.transform.transformedBounds(slot.item.getModelBoundingBox());
            slot.offset = offset(bounds, state.alignToBottom, slot.transform.recentreXZ());
        }
    }

    public static Offset offset(double[] bounds, boolean alignToBottom, boolean recentreXZ) {
        double y = -bounds[1];
        if (!alignToBottom) y -= (bounds[4] - bounds[1]) / 2;
        return new Offset(recentreXZ ? -(bounds[0] + bounds[3]) / 2 : 0, y,
                recentreXZ ? -(bounds[2] + bounds[5]) / 2 : 0);
    }

    @Override public void submit(State state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        for (int i = 0; i < state.slots.length; i++) {
            Slot slot = state.slots[i];
            if (slot.item.isEmpty()) continue;
            pose.pushPose();
            try {
                pose.translate(0.5, 0.5, 0.5);
                pose.mulPose(Axis.YP.rotationDegrees(state.yaw));
                pose.translate((i - 1) * 0.3125F, state.alignToBottom ? -0.25F : 0, -0.25F);
                pose.scale(0.25F, 0.25F, 0.25F);
                pose.translate(slot.offset.x(), slot.offset.y(), slot.offset.z());
                slot.transform.apply(pose);
                slot.item.submit(pose, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            } finally { pose.popPose(); }
        }
    }
}
