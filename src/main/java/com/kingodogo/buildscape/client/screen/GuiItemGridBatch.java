package com.kingodogo.buildscape.client.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** GUI item geometry shares a buffer submission instead of flushing once per visible slot. */
final class GuiItemGridBatch {
    record Icon(ItemStack stack, BakedModel model, int x, int y) {}

    private final List<Icon> blockLit = new ArrayList<>();
    private final List<Icon> flatLit = new ArrayList<>();
    private final List<Icon> vanilla = new ArrayList<>();

    void clear() {
        blockLit.clear();
        flatLit.clear();
        vanilla.clear();
    }

    void add(ItemStack stack, int x, int y) {
        ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
        // Resolve overrides every frame: clocks, compasses and other changing models must stay live.
        BakedModel model = renderer.getModel(stack, null, null, 0);
        Icon icon = new Icon(stack, model, x, y);
        if (renderer.getClass() != ItemRenderer.class || !canBatch(model)) vanilla.add(icon);
        else (model.usesBlockLight() ? blockLit : flatLit).add(icon);
    }

    static boolean canBatch(BakedModel model) {
        // Unknown wrappers can modify GPU state or invoke custom renderers during camera transforms.
        return model != null && model.getClass() == SimpleBakedModel.class && !model.isCustomRenderer();
    }

    void render(float scale, Consumer<Icon> renderVanilla) {
        ConfigGuiBatch.flush();
        Minecraft mc = Minecraft.getInstance();
        ItemRenderer renderer = mc.getItemRenderer();
        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
        if (!blockLit.isEmpty() || !flatLit.isEmpty()) {
            // Finish anything belonging to the caller before changing the shared model-view matrix.
            buffers.endBatch();
            drawPass(blockLit, false, scale, renderer, buffers);
            drawPass(flatLit, true, scale, renderer, buffers);
        }
        for (Icon icon : vanilla) renderVanilla.accept(icon);
        clear();
    }

    private static void drawPass(List<Icon> icons, boolean flat, float scale,
                                 ItemRenderer renderer, MultiBufferSource.BufferSource buffers) {
        if (icons.isEmpty()) return;
        Minecraft mc = Minecraft.getInstance();
        mc.getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableDepthTest();
        PoseStack modelView = RenderSystem.getModelViewStack();
        modelView.pushPose();
        try {
            // Keep the Y flip in the global matrix, as vanilla does, so model normals/light remain unchanged.
            applyGridTransform(modelView, scale);
            RenderSystem.applyModelViewMatrix();
            if (flat) Lighting.setupForFlatItems(); else Lighting.setupFor3DItems();
            PoseStack pose = new PoseStack();
            try {
                for (Icon icon : icons) {
                    pose.pushPose();
                    try {
                        positionIcon(pose, icon.x, icon.y, 100 + renderer.blitOffset);
                        renderer.render(icon.stack, ItemTransforms.TransformType.GUI, false, pose, buffers,
                                15728880, OverlayTexture.NO_OVERLAY, icon.model);
                    } finally {
                        pose.popPose();
                    }
                }
            } finally {
                // Submit while this pass's model-view matrix and lighting are still active.
                buffers.endBatch();
            }
        } finally {
            modelView.popPose();
            RenderSystem.applyModelViewMatrix();
            Lighting.setupFor3DItems();
            RenderSystem.enableDepthTest();
        }
    }

    static void applyGridTransform(PoseStack modelView, float scale) {
        modelView.scale(scale, scale, 1);
        modelView.scale(16, -16, 16);
    }

    static void positionIcon(PoseStack pose, int x, int y, float depth) {
        pose.translate((x + 8) / 16.0, -(y + 8) / 16.0, depth / 16.0);
    }
}
