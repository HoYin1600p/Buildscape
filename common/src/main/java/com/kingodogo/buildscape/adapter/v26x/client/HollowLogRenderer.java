package com.kingodogo.buildscape.adapter.v26x.client;

import com.kingodogo.buildscape.block.HollowLogBlock;
import com.kingodogo.buildscape.block.HollowLogBlockEntity;
import com.kingodogo.buildscape.client.renderer.HollowLogBlockEntityRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public final class HollowLogRenderer implements BlockEntityRenderer<HollowLogBlockEntity, HollowLogRenderer.State> {
    public static final class State extends BlockEntityRenderState {
        final BlockModelRenderState decoration = new BlockModelRenderState();
        final BlockModelRenderState negativeGlass = new BlockModelRenderState();
        final BlockModelRenderState positiveGlass = new BlockModelRenderState();
        Direction.Axis axis;
        int decorationKind;
        HollowLogBlockEntityRenderer.FluidDisplay fluid;
    }

    private final BlockModelResolver models = new BlockModelResolver(Minecraft.getInstance().getModelManager());

    @Override public State createRenderState() { return new State(); }
    @Override public int getViewDistance() { return 48; }

    @Override public void extractRenderState(HollowLogBlockEntity entity, State state, float partialTick,
            Vec3 camera, ModelFeatureRenderer.CrumblingOverlay breaking) {
        BlockEntityRenderState.extractBase(entity, state, breaking);
        BlockState block = entity.getBlockState();
        state.axis = block.hasProperty(HollowLogBlock.AXIS) ? block.getValue(HollowLogBlock.AXIS) : Direction.Axis.Y;
        BlockState decoration = entity.getDecorationState();
        state.decorationKind = decoration != null && decoration.getBlock() instanceof FlowerPotBlock ? 1
                : decoration != null && decoration.getBlock() instanceof HollowLogBlock ? 2 : 0;
        extractModel(entity, decoration, state.decoration);
        BlockState negative = entity.getGlassCoverNeg(), positive = entity.getGlassCoverPos();
        extractModel(entity, negative, state.negativeGlass);
        extractModel(entity, positive, state.positiveGlass);
        boolean glassNeg = negative != null && !negative.isAir()
                || block.hasProperty(HollowLogBlock.HAS_GLASS_NEG) && block.getValue(HollowLogBlock.HAS_GLASS_NEG);
        boolean glassPos = positive != null && !positive.isAir()
                || block.hasProperty(HollowLogBlock.HAS_GLASS_POS) && block.getValue(HollowLogBlock.HAS_GLASS_POS);
        state.fluid = HollowLogBlockEntityRenderer.extractFluid(entity, glassNeg, glassPos);
    }

    private void extractModel(HollowLogBlockEntity entity, BlockState block, BlockModelRenderState model) {
        model.clear();
        if (block == null || block.isAir()) return;
        models.update(model, block, BlockDisplayContext.create());
        var view = new MovingBlockRenderState();
        view.blockState = block;
        view.blockPos = entity.getBlockPos().immutable();
        view.randomSeedPos = view.blockPos;
        if (entity.getLevel() != null) view.biome = entity.getLevel().getBiome(view.blockPos);
        var tints = model.tintLayers();
        tints.clear();
        for (var tint : Minecraft.getInstance().getBlockColors().getTintSources(block)) {
            tints.add(entity.getLevel() == null ? tint.color(block) : tint.colorInWorld(block, view, view.blockPos));
        }
    }

    @Override public void submit(State state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.fluid != null) {
            var fluid = state.fluid;
            int light = state.lightCoords;
            collector.submitCustomGeometry(pose, RenderTypes.entityTranslucent(TextureAtlas.LOCATION_BLOCKS),
                    (transform, consumer) -> {
                        PoseStack drawing = new PoseStack();
                        drawing.mulPose(transform.pose());
                        HollowLogBlockEntityRenderer.submitFluid(fluid, drawing, consumer, light, OverlayTexture.NO_OVERLAY);
                    });
        }
        if (!state.decoration.isEmpty()) {
            pose.pushPose();
            try {
                if (state.decorationKind == 1) {
                    pose.translate(0.5, 0.125, 0.5);
                    pose.scale(0.85F, 0.85F, 0.85F);
                    pose.translate(-0.5, 0, -0.5);
                } else if (state.decorationKind != 2) {
                    pose.translate(0.0625, 0.0625, 0.0625);
                    pose.scale(0.875F, 0.875F, 0.875F);
                }
                state.decoration.submit(pose, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            } finally { pose.popPose(); }
        }
        submitGlass(state.negativeGlass, state, false, pose, collector);
        submitGlass(state.positiveGlass, state, true, pose, collector);
    }

    private static void submitGlass(BlockModelRenderState glass, State state, boolean positive,
            PoseStack pose, SubmitNodeCollector collector) {
        if (glass.isEmpty()) return;
        pose.pushPose();
        try {
            HollowLogBlockEntityRenderer.positionGlassCover(pose, state.axis, positive);
            glass.submit(pose, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        } finally { pose.popPose(); }
    }
}
