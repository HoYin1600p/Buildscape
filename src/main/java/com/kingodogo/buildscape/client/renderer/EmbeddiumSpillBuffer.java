package com.kingodogo.buildscape.client.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import me.jellysquid.mods.sodium.client.model.quad.properties.ModelQuadFacing;
import me.jellysquid.mods.sodium.client.model.quad.properties.ModelQuadWinding;
import me.jellysquid.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuilder;
import me.jellysquid.mods.sodium.client.render.chunk.format.ModelVertexSink;
import net.minecraft.core.BlockPos;

/** Bridges the outlet's Forge water mesh into Embeddium's chunk buffers. Author: HoYin1600p. */
public final class EmbeddiumSpillBuffer implements VertexConsumer {
    private final ChunkModelBuilder builder;
    private final ModelVertexSink sink;
    private final BlockPos offset;
    private float x, y, z, u, v;
    private int color, light, count, first;

    public EmbeddiumSpillBuffer(ChunkModelBuilder builder, BlockPos worldPos, BlockPos renderOffset) {
        this.builder = builder;
        sink = builder.getVertexSink();
        offset = renderOffset.offset(-(worldPos.getX() & 15), -(worldPos.getY() & 15), -(worldPos.getZ() & 15));
    }

    @Override public VertexConsumer vertex(double x, double y, double z) {
        this.x = (float)x; this.y = (float)y; this.z = (float)z; return this;
    }
    @Override public VertexConsumer color(int r, int g, int b, int a) {
        color = r | (g << 8) | (b << 16) | (a << 24); return this;
    }
    @Override public VertexConsumer uv(float u, float v) { this.u = u; this.v = v; return this; }
    @Override public VertexConsumer uv2(int u, int v) { light = u | (v << 16); return this; }
    @Override public VertexConsumer overlayCoords(int u, int v) { return this; }
    @Override public VertexConsumer normal(float x, float y, float z) { return this; }
    @Override public void defaultColor(int r, int g, int b, int a) { color(r, g, b, a); }
    @Override public void unsetDefaultColor() {}

    @Override public void endVertex() {
        if (count == 0) {
            sink.ensureCapacity(4);
            first = sink.getVertexCount();
        }
        sink.writeVertex(offset, x, y, z, color, u, v, light, builder.getChunkId());
        if (++count == 4) {
            sink.flush();
            builder.getIndexBufferBuilder(ModelQuadFacing.UNASSIGNED).add(first, ModelQuadWinding.CLOCKWISE);
            count = 0;
        }
    }
}
