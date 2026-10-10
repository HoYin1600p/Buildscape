package com.kingodogo.buildscape.adapter.v26x.client;

import com.kingodogo.buildscape.adapter.v26x.PlatformAdapterBase;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.FallingBlockRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.RenderShape;

/** Uses vanilla's moving-block state and submission, including world tint and terrain lighting. */
public final class FallingIcicleEntityRenderer
        extends EntityRenderer<PlatformAdapterBase.FallingIcicleEntityImpl, FallingBlockRenderState> {
    public FallingIcicleEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 0.5F;
    }

    @Override public FallingBlockRenderState createRenderState() { return new FallingBlockRenderState(); }

    @Override public void extractRenderState(PlatformAdapterBase.FallingIcicleEntityImpl entity,
            FallingBlockRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        var moving = state.movingBlockRenderState;
        moving.blockPos = samplePosition(entity.getX(), entity.getBoundingBox().maxY, entity.getZ());
        moving.randomSeedPos = entity.getStartPos().immutable();
        moving.blockState = entity.getBlockState();
        // These are the same lighting/tint inputs extracted by 26.2's FallingBlockRenderer.
        // No entity or Level reference is stored in the render state.
        if (entity.level() instanceof ClientLevel level) {
            moving.biome = level.getBiome(moving.blockPos);
            moving.cardinalLighting = level.cardinalLighting();
            moving.lightEngine = level.getLightEngine();
        }
    }

    public static BlockPos samplePosition(double x, double maxY, double z) {
        return BlockPos.containing(x, maxY, z);
    }

    @Override public void submit(FallingBlockRenderState state, PoseStack pose,
            SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.movingBlockRenderState.blockState.getRenderShape() != RenderShape.MODEL) return;
        pose.pushPose();
        try {
            pose.translate(-0.5, 0, -0.5);
            collector.submitMovingBlock(pose, state.movingBlockRenderState, state.outlineColor);
        } finally { pose.popPose(); }
        super.submit(state, pose, collector, camera);
    }
}
