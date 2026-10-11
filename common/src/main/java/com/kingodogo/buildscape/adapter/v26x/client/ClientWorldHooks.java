package com.kingodogo.buildscape.adapter.v26x.client;

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
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;

/** Scoped world submission context for common render callbacks. */
public final class ClientWorldHooks {
    private static final java.util.Map<net.minecraft.client.renderer.state.level.LevelRenderState, OutlineState> OVERLAYS =
            java.util.Collections.synchronizedMap(new java.util.WeakHashMap<>());
    private ClientWorldHooks() {}

    public record Outline(VoxelShape shape, int color, Matrix4f transform) {}

    /** Stores semantic boxes, never their line vertices or live world inputs. */
    public static final class OutlineState {
        private final java.util.List<Outline> outlines = new java.util.ArrayList<>();

        public java.util.List<Outline> outlines() { return java.util.List.copyOf(outlines); }

        public void submit(PoseStack pose, SubmitNodeCollector collector) {
            for (Outline outline : outlines) {
                pose.pushPose();
                try {
                    pose.mulPose(outline.transform());
                    collector.submitShapeOutline(pose, outline.shape(), RenderTypes.lines(), outline.color(), 1, false);
                } finally { pose.popPose(); }
            }
        }
    }

    public static int outlineColor(float r, float g, float b, float a) {
        return (Math.round(a * 255) << 24) | (Math.round(r * 255) << 16)
                | (Math.round(g * 255) << 8) | Math.round(b * 255);
    }

    public static void extractWorldOverlays(net.minecraft.client.renderer.state.level.LevelRenderState state, Camera camera) {
        OutlineState outlines = new OutlineState();
        com.kingodogo.buildscape.client.ClientEvents.renderLevel(new PoseStack(), camera, outlines);
        OVERLAYS.put(state, outlines);
    }

    public static void collectWorldOverlays(PoseStack pose, SubmitNodeCollector collector,
            net.minecraft.client.renderer.state.level.LevelRenderState state) {
        OutlineState outlines = OVERLAYS.get(state);
        if (outlines != null) outlines.submit(pose, collector);
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
        VoxelShape shape;
        if (buffer instanceof OutlineState outlines) {
            shape = Shapes.box(minX, minY, minZ, maxX, maxY, maxZ);
            outlines.outlines.add(new Outline(shape, outlineColor(r, g, b, a), new Matrix4f(pose.last().pose())));
            return;
        }
        if (buffer instanceof SubmitNodeCollector collector) {
            shape = Shapes.box(minX, minY, minZ, maxX, maxY, maxZ);
            collector.submitShapeOutline(pose, shape, RenderTypes.lines(), outlineColor(r, g, b, a), 1, false);
            return;
        }
        if (!(buffer instanceof VertexConsumer consumer)) return;
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
