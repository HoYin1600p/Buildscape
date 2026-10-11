package com.kingodogo.buildscape.adapter.v26x.client;

import com.kingodogo.buildscape.adapter.v26x.PlatformAdapterBase;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;

public final class FestiveStockingEntityRenderer
        extends EntityRenderer<PlatformAdapterBase.FestiveStockingEntityImpl, FestiveStockingEntityRenderer.State> {
    public static final class State extends EntityRenderState {
        StockingRenderGeometry.Placement placement;
        Identifier texture;
    }

    public FestiveStockingEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 0.25F;
    }

    @Override public State createRenderState() { return new State(); }

    @Override public void extractRenderState(PlatformAdapterBase.FestiveStockingEntityImpl entity, State state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.placement = entity.getDirection() == null ? null : StockingRenderGeometry.entityPlacement(entity.getDirection());
        state.texture = StockingRenderGeometry.textureFor(entity.getColorVariant());
    }

    @Override public void submit(State state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.placement != null) {
            pose.pushPose();
            try {
                state.placement.apply(pose);
                pose.translate(0, 0, 0.0625);
                StockingRenderGeometry.submit(pose, collector, state.texture, state.lightCoords, false);
            } finally { pose.popPose(); }
        }
        super.submit(state, pose, collector, camera);
    }
}
