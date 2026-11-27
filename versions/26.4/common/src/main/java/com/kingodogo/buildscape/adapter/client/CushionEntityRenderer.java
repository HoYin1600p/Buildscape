package com.kingodogo.buildscape.adapter.client;

import com.kingodogo.buildscape.adapter.PlatformAdapterBase;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.FallingBlockRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.RenderShape;

public final class CushionEntityRenderer
        extends EntityRenderer<PlatformAdapterBase.CushionEntityImpl, FallingBlockRenderState> {
    public CushionEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    @Override
    public FallingBlockRenderState createRenderState() {
        return new FallingBlockRenderState();
    }

    @Override
    public void extractRenderState(PlatformAdapterBase.CushionEntityImpl entity,
                                   FallingBlockRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        var moving = state.movingBlockRenderState;
        moving.blockPos = entity.blockPosition();
        moving.randomSeedPos = entity.blockPosition();
        moving.blockState = entity.getRenderState();
        if (entity.level() instanceof ClientLevel level) {
            moving.biome = level.getBiome(moving.blockPos);
            moving.cardinalLighting = level.cardinalLighting();
            moving.lightEngine = level.getLightEngine();
        }
    }

    @Override
    public void submit(FallingBlockRenderState state, PoseStack pose,
                       SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.movingBlockRenderState.blockState.getRenderShape() != RenderShape.MODEL) return;
        pose.pushPose();
        try {
            pose.translate(-0.5, 0, -0.5);
            collector.submitMovingBlock(pose, state.movingBlockRenderState, state.outlineColor);
        } finally {
            pose.popPose();
        }
        super.submit(state, pose, collector, camera);
    }
}
