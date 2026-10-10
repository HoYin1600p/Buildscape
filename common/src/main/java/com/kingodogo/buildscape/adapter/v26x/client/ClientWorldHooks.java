package com.kingodogo.buildscape.adapter.v26x.client;

import com.kingodogo.buildscape.adapter.v26x.RenderCapture;
import com.kingodogo.buildscape.block.PillarBlock;
import com.kingodogo.buildscape.client.PillarMarkerManager;
import com.kingodogo.buildscape.platform.Services;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;

/** Scoped world submission context for common render callbacks. */
public final class ClientWorldHooks {
    private static final java.util.Map<net.minecraft.client.renderer.state.level.LevelRenderState, RenderCapture> OVERLAYS =
            java.util.Collections.synchronizedMap(new java.util.WeakHashMap<>());
    private ClientWorldHooks() {}

    public static void extractWorldOverlays(net.minecraft.client.renderer.state.level.LevelRenderState state, Camera camera) {
        RenderCapture capture = new RenderCapture();
        com.kingodogo.buildscape.client.ClientEvents.renderLevel(new PoseStack(), camera, capture);
        OVERLAYS.put(state, capture);
    }

    public static void collectWorldOverlays(PoseStack pose, SubmitNodeCollector collector,
            net.minecraft.client.renderer.state.level.LevelRenderState state) {
        RenderCapture capture = OVERLAYS.get(state);
        if (capture != null) capture.submit(pose, collector, state.cameraRenderState);
    }

    public static void renderPillarMarkers(PoseStack pose, Camera camera) {
        var level = Minecraft.getInstance().level;
        Object buffer = com.kingodogo.buildscape.client.PillarMarkerRenderer.currentBuffer();
        if (level == null || buffer == null || camera == null) return;
        var cameraPos = camera.position();
        var marks = PillarMarkerManager.get().getMarkedPillars(Services.PLATFORM.getDimensionId(level).toString());
        for (var mark : marks.values()) {
            BlockPos pos = mark.pos;
            if (pos == null || !level.isLoaded(pos) || !(level.getBlockState(pos).getBlock() instanceof PillarBlock)
                    || pos.distToCenterSqr(cameraPos.x, cameraPos.y, cameraPos.z) > 64 * 64) continue;
            BlockPos bottom = pos, top = pos;
            for (int i = 0; i < 256; i++) {
                BlockPos next = bottom.below();
                if (!level.isLoaded(next) || !(level.getBlockState(next).getBlock() instanceof PillarBlock)) break;
                bottom = next;
            }
            for (int i = 0; i < 256; i++) {
                BlockPos next = top.above();
                if (!level.isLoaded(next) || !(level.getBlockState(next).getBlock() instanceof PillarBlock)) break;
                top = next;
            }
            float alpha = mark.getBlinkAlpha();
            renderLineBox(pose, buffer, bottom.getX() - cameraPos.x, bottom.getY() - cameraPos.y, bottom.getZ() - cameraPos.z,
                    top.getX() + 1 - cameraPos.x, top.getY() + 1 - cameraPos.y, top.getZ() + 1 - cameraPos.z,
                    alpha, (1 - mark.getGradientProgress()) * alpha, 0, 1);
        }
    }

    public static void renderLineBox(PoseStack pose, Object buffer, double minX, double minY, double minZ,
            double maxX, double maxY, double maxZ, float r, float g, float b, float a) {
        VertexConsumer consumer;
        if (buffer instanceof RenderCapture capture) consumer = capture.geometry(RenderTypes.lines());
        else if (buffer instanceof VertexConsumer vertices) consumer = vertices;
        else if (buffer instanceof SubmitNodeCollector collector) {
            RenderCapture capture = new RenderCapture();
            consumer = capture.geometry(RenderTypes.lines());
            capture.submit(new PoseStack(), collector, null);
        } else return;
        float[] xs = {(float) minX, (float) maxX}, ys = {(float) minY, (float) maxY}, zs = {(float) minZ, (float) maxZ};
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                line(pose, consumer, xs[0], ys[i], zs[j], xs[1], ys[i], zs[j], r, g, b, a);
                line(pose, consumer, xs[i], ys[0], zs[j], xs[i], ys[1], zs[j], r, g, b, a);
                line(pose, consumer, xs[i], ys[j], zs[0], xs[i], ys[j], zs[1], r, g, b, a);
            }
        }
    }

    private static void line(PoseStack pose, VertexConsumer consumer, float x0, float y0, float z0,
            float x1, float y1, float z1, float r, float g, float b, float a) {
        float dx = x1 - x0, dy = y1 - y0, dz = z1 - z0;
        float length = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (length == 0) return;
        consumer.addVertex(pose.last(), x0, y0, z0).setColor(r, g, b, a)
                .setNormal(pose.last(), dx / length, dy / length, dz / length).setLineWidth(1);
        consumer.addVertex(pose.last(), x1, y1, z1).setColor(r, g, b, a)
                .setNormal(pose.last(), dx / length, dy / length, dz / length).setLineWidth(1);
    }
}
