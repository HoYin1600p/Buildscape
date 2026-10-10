package com.kingodogo.buildscape.adapter.v26x.client;

import com.mojang.blaze3d.vertex.VertexConsumer;

/** Sends a single mesh to both its textured pass and its foil pass. */
final class DualVertexConsumer implements VertexConsumer {
    private final VertexConsumer base, foil;
    DualVertexConsumer(VertexConsumer base, VertexConsumer foil) { this.base = base; this.foil = foil; }
    public VertexConsumer addVertex(float x, float y, float z) { base.addVertex(x, y, z); foil.addVertex(x, y, z); return this; }
    public VertexConsumer setColor(int r, int g, int b, int a) { base.setColor(r, g, b, a); foil.setColor(r, g, b, a); return this; }
    public VertexConsumer setColor(int color) { base.setColor(color); foil.setColor(color); return this; }
    public VertexConsumer setUv(float u, float v) { base.setUv(u, v); foil.setUv(u, v); return this; }
    public VertexConsumer setUv1(int u, int v) { base.setUv1(u, v); foil.setUv1(u, v); return this; }
    public VertexConsumer setUv2(int u, int v) { base.setUv2(u, v); foil.setUv2(u, v); return this; }
    public VertexConsumer setNormal(float x, float y, float z) { base.setNormal(x, y, z); foil.setNormal(x, y, z); return this; }
    public VertexConsumer setLineWidth(float width) { base.setLineWidth(width); foil.setLineWidth(width); return this; }
}
