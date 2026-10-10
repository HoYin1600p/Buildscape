package com.kingodogo.buildscape.adapter.v26x.client;

import com.kingodogo.buildscape.block.CopperChestBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.object.chest.ChestModel;
import net.minecraft.client.renderer.MultiblockChestResources;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.Vec3;

/** Retains vanilla paired-lid extraction, but submits the reference copper textures directly. */
public final class CopperChestRenderer extends ChestRenderer<CopperChestBlockEntity> {
    private final MultiblockChestResources<ChestModel> models;

    public static final class State extends ChestRenderState {
        public Identifier texture;
    }

    public CopperChestRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
        models = LAYERS.map(layer -> new ChestModel(context.bakeLayer(layer)));
    }

    @Override public State createRenderState() { return new State(); }

    @Override public void extractRenderState(CopperChestBlockEntity entity, ChestRenderState state,
            float partialTick, Vec3 camera, ModelFeatureRenderer.CrumblingOverlay overlay) {
        super.extractRenderState(entity, state, partialTick, camera, overlay);
        ((State) state).texture = textureFor(BuiltInRegistries.BLOCK.getKey(entity.getBlockState().getBlock()).getPath(), state.type);
    }

    public static Identifier textureFor(String blockPath, ChestType type) {
        String oxidation = blockPath.contains("oxidized") ? "oxidized_"
                : blockPath.contains("weathered") ? "weathered_"
                : blockPath.contains("exposed") ? "exposed_" : "";
        String half = switch (type) {
            case LEFT -> "_left";
            case RIGHT -> "_right";
            case SINGLE -> "";
        };
        return Identifier.fromNamespaceAndPath("buildscape", "textures/entity/chest/" + oxidation + "copper_chest" + half + ".png");
    }

    @Override public void submit(ChestRenderState state, PoseStack pose, SubmitNodeCollector collector,
            CameraRenderState camera) {
        pose.pushPose();
        try {
            pose.mulPose(modelTransformation(state.facing));
            float closed = 1 - state.open;
            collector.submitModel(models.select(state.type), 1 - closed * closed * closed, pose,
                    ((State) state).texture, state.lightCoords, OverlayTexture.NO_OVERLAY, 0, state.breakProgress);
        } finally {
            pose.popPose();
        }
    }
}
