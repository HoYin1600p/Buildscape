package com.kingodogo.buildscape.adapter.v26x.client;

import com.kingodogo.buildscape.block.IcicleCauldronBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public final class IcicleCauldronRenderer implements BlockEntityRenderer<IcicleCauldronBlockEntity, IcicleCauldronRenderer.State> {
    public static final class State extends BlockEntityRenderState {
        final BlockModelRenderState cauldron = new BlockModelRenderState();
        final BlockModelRenderState icicle = new BlockModelRenderState();
    }

    private final BlockModelResolver models = new BlockModelResolver(Minecraft.getInstance().getModelManager());

    @Override public State createRenderState() { return new State(); }

    @Override public void extractRenderState(IcicleCauldronBlockEntity entity, State state, float partialTick,
            Vec3 camera, ModelFeatureRenderer.CrumblingOverlay breaking) {
        BlockEntityRenderState.extractBase(entity, state, breaking);
        state.cauldron.clear();
        state.icicle.clear();
        if (entity.getLevel() == null) return;
        models.update(state.cauldron, Blocks.CAULDRON.defaultBlockState(), BlockDisplayContext.create());
        ItemStack stored = entity.getStoredIcicle();
        if (!stored.isEmpty() && stored.getItem() instanceof BlockItem block) {
            var icicleState = block.getBlock().defaultBlockState();
            models.update(state.icicle, icicleState, BlockDisplayContext.create());
            // Preserve the reference's item-style block tint (not a world-biome tint).
            var tints = state.icicle.tintLayers();
            tints.clear();
            for (var source : Minecraft.getInstance().getBlockColors().getTintSources(icicleState)) {
                tints.add(source.color(icicleState));
            }
        }
    }

    @Override public void submit(State state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        state.cauldron.submit(pose, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        if (state.icicle.isEmpty()) return;
        pose.pushPose();
        try {
            pose.translate(0.125, 0.3125, 0.125);
            pose.scale(0.75F, 0.625F, 0.75F);
            state.icicle.submit(pose, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        } finally { pose.popPose(); }
    }
}
