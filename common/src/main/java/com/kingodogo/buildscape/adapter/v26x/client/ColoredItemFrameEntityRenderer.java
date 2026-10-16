package com.kingodogo.buildscape.adapter.v26x.client;

import com.kingodogo.buildscape.adapter.v26x.PlatformAdapterBase;
import com.kingodogo.buildscape.platform.Services;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.entity.state.ItemFrameRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

/** Keeps vanilla item/map extraction and submission, replacing only the frame's coloured shell. */
public final class ColoredItemFrameEntityRenderer extends ItemFrameRenderer<PlatformAdapterBase.ColoredItemFrameEntityImpl> {
    public static final class State extends ItemFrameRenderState {
        Identifier texture;
        float pitch, yaw;
    }

    public ColoredItemFrameEntityRenderer(EntityRendererProvider.Context context) { super(context); }

    @Override public State createRenderState() { return new State(); }

    @Override public void extractRenderState(PlatformAdapterBase.ColoredItemFrameEntityImpl entity,
            ItemFrameRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        State frame = (State) state;
        frame.texture = textureFor(entity.getColorVariant());
        frame.pitch = entity.getXRot();
        frame.yaw = 180 - entity.getYRot();
        state.frameModel.clear();
        if (!state.direction.getAxis().isHorizontal()) {
            state.lightCoords = Services.PLATFORM.getLightColor(entity.level(), entity.blockPosition().relative(state.direction));
        }
    }

    public static Identifier textureFor(String color) {
        return Identifier.fromNamespaceAndPath("buildscape", "textures/entity/"
                + (color == null || color.isEmpty() ? "white" : color) + "_item_frame.png");
    }

    @Override public Vec3 getRenderOffset(ItemFrameRenderState state) {
        return new Vec3(state.direction.getStepX() * 0.3, -0.25, state.direction.getStepZ() * 0.3);
    }

    @Override public void submit(ItemFrameRenderState state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        State frame = (State) state;
        if (!state.isInvisible) {
            pose.pushPose();
            try {
                Vec3 offset = getRenderOffset(state);
                pose.translate(-offset.x, -offset.y, -offset.z);
                pose.translate(state.direction.getStepX() * 0.469, state.direction.getStepY() * 0.469,
                        state.direction.getStepZ() * 0.469);
                pose.mulPose(Axis.XP.rotationDegrees(frame.pitch));
                pose.mulPose(Axis.YP.rotationDegrees(frame.yaw));
                ClientPlatformHooks.submitColoredFrame(pose, collector, state.lightCoords, frame.texture, state.mapId != null);
            } finally { pose.popPose(); }
        }
        pose.pushPose();
        try {
            double adjustment = 0.469 - 0.46875;
            pose.translate(state.direction.getStepX() * adjustment, state.direction.getStepY() * adjustment,
                    state.direction.getStepZ() * adjustment);
            super.submit(state, pose, collector, camera);
        } finally { pose.popPose(); }
    }
}
