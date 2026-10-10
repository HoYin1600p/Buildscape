package com.kingodogo.buildscape.adapter.v26x.client;

import com.kingodogo.buildscape.block.FestiveStockingBlock;
import com.kingodogo.buildscape.block.FestiveStockingBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

public final class FestiveStockingBlockRenderer
        implements BlockEntityRenderer<FestiveStockingBlockEntity, FestiveStockingBlockRenderer.State> {
    public static final class State extends BlockEntityRenderState {
        StockingRenderGeometry.Placement placement;
        Identifier texture;
        boolean flipped;
    }

    @Override public State createRenderState() { return new State(); }

    @Override public void extractRenderState(FestiveStockingBlockEntity entity, State state, float partialTick,
            Vec3 camera, ModelFeatureRenderer.CrumblingOverlay breaking) {
        BlockEntityRenderState.extractBase(entity, state, breaking);
        state.placement = null;
        state.texture = null;
        state.flipped = false;
        if (entity.getLevel() == null || !(entity.getBlockState().getBlock() instanceof FestiveStockingBlock block)) return;
        state.placement = StockingRenderGeometry.blockPlacement(entity.getBlockState().getValue(FestiveStockingBlock.FACING));
        state.texture = StockingRenderGeometry.textureFor(block.getColorVariant());
        state.flipped = entity.getBlockState().getValue(FestiveStockingBlock.FLIPPED);
    }

    @Override public void submit(State state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.placement == null) return;
        pose.pushPose();
        try {
            state.placement.apply(pose);
            StockingRenderGeometry.submit(pose, collector, state.texture, state.lightCoords, state.flipped);
        } finally { pose.popPose(); }
    }
}
