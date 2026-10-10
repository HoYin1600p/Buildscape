package com.kingodogo.buildscape.adapter.v26x;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/** Captures the existing helpers' drawing during extraction, without retaining live entities. */
final class RenderCapture {
    @FunctionalInterface
    interface Draw {
        void submit(PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera);
    }

    private final List<Draw> draws = new ArrayList<>();
    private VertexConsumer translucent;

    void record(PoseStack pose, Draw draw) {
        Matrix4f transform = new Matrix4f(pose.last().pose());
        draws.add((targetPose, collector, camera) -> {
            targetPose.pushPose();
            try {
                targetPose.mulPose(transform);
                draw.submit(targetPose, collector, camera);
            } finally {
                targetPose.popPose();
            }
        });
    }

    void submit(PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        for (Draw draw : draws) draw.submit(pose, collector, camera);
    }

    VertexConsumer translucent() {
        if (translucent == null) translucent = geometry(RenderTypes.entityTranslucent(TextureAtlas.LOCATION_BLOCKS));
        return translucent;
    }

    VertexConsumer geometry(RenderType type) {
        List<Vertex> vertices = new ArrayList<>();
        draws.add((pose, collector, camera) -> collector.submitCustomGeometry(pose, type, (transform, consumer) -> {
            for (Vertex vertex : vertices) {
                consumer.addVertex(transform, vertex.x, vertex.y, vertex.z).setColor(vertex.color)
                        .setUv(vertex.u, vertex.v).setUv1(vertex.overlayU, vertex.overlayV)
                        .setUv2(vertex.lightU, vertex.lightV)
                        .setNormal(transform, vertex.nx, vertex.ny, vertex.nz).setLineWidth(vertex.lineWidth);
            }
        }));
        return new VertexConsumer() {
            private Vertex current;
            public VertexConsumer addVertex(float x, float y, float z) {
                current = new Vertex(x, y, z);
                vertices.add(current);
                return this;
            }
            public VertexConsumer setColor(int r, int g, int b, int a) {
                return setColor((a << 24) | (r << 16) | (g << 8) | b);
            }
            public VertexConsumer setColor(int color) { current.color = color; return this; }
            public VertexConsumer setUv(float u, float v) { current.u = u; current.v = v; return this; }
            public VertexConsumer setUv1(int u, int v) { current.overlayU = u; current.overlayV = v; return this; }
            public VertexConsumer setUv2(int u, int v) { current.lightU = u; current.lightV = v; return this; }
            public VertexConsumer setNormal(float x, float y, float z) {
                current.nx = x; current.ny = y; current.nz = z; return this;
            }
            public VertexConsumer setLineWidth(float width) { current.lineWidth = width; return this; }
        };
    }

    private static final class Vertex {
        final float x, y, z;
        int color = -1, overlayU, overlayV, lightU, lightV;
        float u, v, nx, ny, nz, lineWidth = 1;
        Vertex(float x, float y, float z) { this.x = x; this.y = y; this.z = z; }
    }
}
