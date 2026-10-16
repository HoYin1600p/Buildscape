package com.kingodogo.buildscape.adapter.v26x.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;

/** The reference stocking is a two-sided, one-block textured quad, with no visible contents. */
public final class StockingRenderGeometry {
    private StockingRenderGeometry() {}

    public record Placement(double x, double y, double z, float yaw, float pitch) {
        void apply(PoseStack pose) {
            pose.translate(x, y, z);
            pose.mulPose(Axis.YP.rotationDegrees(yaw));
            pose.mulPose(Axis.XP.rotationDegrees(pitch));
        }
    }

    public static Placement blockPlacement(Direction facing) {
        return switch (facing) {
            case NORTH -> new Placement(0.5, 0.5, 0.999, 180, 0);
            case SOUTH -> new Placement(0.5, 0.5, 0.001, 0, 0);
            case WEST -> new Placement(0.999, 0.5, 0.5, 90, 0);
            case EAST -> new Placement(0.001, 0.5, 0.5, 270, 0);
            case UP -> new Placement(0.5, 0.5 / 16, 0.5, 180, -90);
            case DOWN -> new Placement(0.5, 15.5 / 16, 0.5, 180, 90);
        };
    }

    public static Placement entityPlacement(Direction facing) {
        return switch (facing) {
            case NORTH -> new Placement(0, 0, 0, 0, 0);
            case SOUTH -> new Placement(0, 0, 0, 180, 0);
            case WEST -> new Placement(0, 0, 0, 90, 0);
            case EAST -> new Placement(0, 0, 0, 270, 0);
            case UP -> new Placement(0, 0, 0, 180, -90);
            case DOWN -> new Placement(0, 0, 0, 180, 90);
        };
    }

    public static Identifier textureFor(String color) {
        String prefix = color == null || color.equals("festive") ? "" : color + "_";
        return Identifier.fromNamespaceAndPath("buildscape", "textures/entity/" + prefix + "festive_stocking.png");
    }

    static void submit(PoseStack pose, SubmitNodeCollector collector, Identifier texture, int light, boolean flipped) {
        // entityCutout is the 26.2 no-cull variant; entityCutoutCull would hide the back of the stocking.
        collector.submitCustomGeometry(pose, RenderTypes.entityCutout(texture),
                (transform, consumer) -> quad(transform, consumer, light, flipped));
    }

    private static void quad(PoseStack.Pose pose, VertexConsumer consumer, int light, boolean flipped) {
        float leftU = flipped ? 1 : 0;
        float rightU = flipped ? 0 : 1;
        vertex(pose, consumer, light, -0.5F, -0.5F, leftU, 1);
        vertex(pose, consumer, light, 0.5F, -0.5F, rightU, 1);
        vertex(pose, consumer, light, 0.5F, 0.5F, rightU, 0);
        vertex(pose, consumer, light, -0.5F, 0.5F, leftU, 0);
    }

    private static void vertex(PoseStack.Pose pose, VertexConsumer consumer, int light,
            float x, float y, float u, float v) {
        consumer.addVertex(pose, x, y, 0).setColor(-1).setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0, 0, 1);
    }
}
