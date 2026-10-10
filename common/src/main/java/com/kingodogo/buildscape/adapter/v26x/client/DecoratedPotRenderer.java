package com.kingodogo.buildscape.adapter.v26x.client;

import com.kingodogo.buildscape.block.DecoratedPotBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;

/** Buildscape pots use reference block-model decorations rather than vanilla pot sherd components. */
public final class DecoratedPotRenderer<T extends DecoratedPotBlockEntity>
        implements BlockEntityRenderer<T, DecoratedPotRenderer.State> {
    public static final class State extends BlockEntityRenderState {
        final BlockModelRenderState body = new BlockModelRenderState();
        float wobbleAngle;
    }

    private final BlockModelResolver models = new BlockModelResolver(Minecraft.getInstance().getModelManager());

    @Override public State createRenderState() { return new State(); }

    @Override public void extractRenderState(T entity, State state, float partialTick,
            Vec3 camera, ModelFeatureRenderer.CrumblingOverlay breaking) {
        BlockEntityRenderState.extractBase(entity, state, breaking);
        state.body.clear();
        state.wobbleAngle = 0;
        if (entity.getLevel() == null) return;
        models.update(state.body, entity.getBlockState(), BlockDisplayContext.create());
        var tints = state.body.tintLayers();
        tints.clear();
        for (var source : Minecraft.getInstance().getBlockColors().getTintSources(entity.getBlockState())) {
            tints.add(source.color(entity.getBlockState()));
        }
        state.wobbleAngle = wobbleAngle(entity.getLevel().getGameTime(), entity.getWobbleStartedAtTick(),
                entity.getLastWobbleStyle() != DecoratedPotBlockEntity.WobbleStyle.NONE, partialTick);
    }

    public static float wobbleAngle(long tick, long startedAt, boolean wobbling, float partialTick) {
        if (!wobbling || startedAt <= 0) return 0;
        float elapsed = (float) (tick - startedAt) + partialTick;
        if (elapsed <= 0 || elapsed >= 10) return 0;
        float progress = elapsed / 10;
        return 8 * (1 - progress) * (float) Math.sin(progress * Math.PI * 6);
    }

    @Override public void submit(State state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        pose.pushPose();
        try {
            pose.translate(0.5, 0, 0.5);
            pose.mulPose(Axis.YP.rotationDegrees(state.wobbleAngle));
            pose.translate(-0.5, 0, -0.5);
            state.body.submit(pose, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        } finally { pose.popPose(); }
    }
}
