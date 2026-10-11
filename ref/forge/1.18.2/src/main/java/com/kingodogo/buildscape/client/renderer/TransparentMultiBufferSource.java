package com.kingodogo.buildscape.client.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;

public class TransparentMultiBufferSource implements MultiBufferSource {
    private final MultiBufferSource parent;
    private final float alpha;

    public TransparentMultiBufferSource(MultiBufferSource parent, float alpha) {
        this.parent = parent;
        this.alpha = alpha;
    }

    @Override
    public VertexConsumer getBuffer(RenderType renderType) {
        return new AlphaVertexConsumer(parent.getBuffer(translucentVersion(renderType)), alpha);
    }

    private static RenderType translucentVersion(RenderType renderType) {
        if (renderType == Sheets.solidBlockSheet() || renderType == Sheets.cutoutBlockSheet()
                || renderType == Sheets.translucentCullBlockSheet()) {
            return Sheets.translucentItemSheet();
        }
        if (renderType == Sheets.chestSheet()) return RenderType.entityTranslucentCull(Sheets.CHEST_SHEET);
        if (renderType == Sheets.shulkerBoxSheet()) return RenderType.entityTranslucentCull(Sheets.SHULKER_SHEET);
        if (renderType == Sheets.signSheet()) return RenderType.entityTranslucentCull(Sheets.SIGN_SHEET);
        if (renderType == Sheets.bannerSheet()) return RenderType.entityTranslucentCull(Sheets.BANNER_SHEET);
        if (renderType == Sheets.shieldSheet()) return RenderType.entityTranslucentCull(Sheets.SHIELD_SHEET);
        if (renderType == Sheets.bedSheet()) return RenderType.entityTranslucentCull(Sheets.BED_SHEET);
        return renderType;
    }

    private static final class AlphaVertexConsumer implements VertexConsumer {
        private final VertexConsumer parent;
        private final float alpha;

        private AlphaVertexConsumer(VertexConsumer parent, float alpha) {
            this.parent = parent;
            this.alpha = alpha;
        }

        @Override
        public VertexConsumer vertex(double x, double y, double z) {
            parent.vertex(x, y, z);
            return this;
        }

        @Override
        public VertexConsumer color(int red, int green, int blue, int alpha) {
            parent.color(red, green, blue, Math.round(alpha * this.alpha));
            return this;
        }

        @Override
        public VertexConsumer uv(float u, float v) {
            parent.uv(u, v);
            return this;
        }

        @Override
        public VertexConsumer overlayCoords(int u, int v) {
            parent.overlayCoords(u, v);
            return this;
        }

        @Override
        public VertexConsumer uv2(int u, int v) {
            parent.uv2(u, v);
            return this;
        }

        @Override
        public VertexConsumer normal(float x, float y, float z) {
            parent.normal(x, y, z);
            return this;
        }

        @Override
        public void endVertex() {
            parent.endVertex();
        }

        @Override
        public void defaultColor(int red, int green, int blue, int alpha) {
            parent.defaultColor(red, green, blue, Math.round(alpha * this.alpha));
        }

        @Override
        public void unsetDefaultColor() {
            parent.unsetDefaultColor();
        }
    }
}
