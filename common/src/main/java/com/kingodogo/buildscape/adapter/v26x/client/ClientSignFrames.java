package com.kingodogo.buildscape.adapter.v26x.client;

import com.kingodogo.buildscape.adapter.v26x.RenderCapture;
import com.kingodogo.buildscape.cosmetic.sign.SignFrameAttachment;
import com.kingodogo.buildscape.cosmetic.sign.SignFrameType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.SignRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/** Extracts a frame into the sign's render state, including vanilla signs on either loader. */
public final class ClientSignFrames {
    private static final Map<SignRenderState, RenderCapture> FRAMES = Collections.synchronizedMap(new WeakHashMap<>());
    private ClientSignFrames() {}

    public static void extract(SignBlockEntity sign, Object renderState, float partialTick) {
        SignRenderState state = (SignRenderState) renderState;
        RenderCapture capture = new RenderCapture();
        render(sign, partialTick, new PoseStack(), capture, state.lightCoords,
                net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY);
        FRAMES.put(state, capture);
    }

    public static void submit(Object renderState, PoseStack pose, Object collector, Object camera) {
        RenderCapture capture = FRAMES.get((SignRenderState) renderState);
        if (capture != null) capture.submit(pose, (SubmitNodeCollector) collector, (CameraRenderState) camera);
    }

    public static void render(SignBlockEntity sign, float partialTick, PoseStack pose, Object buffer, int light, int overlay) {
        if (sign == null || sign.getLevel() == null) return;
        SignFrameType frame = SignFrameAttachment.getFrame(sign);
        if (frame == SignFrameType.NONE || frame.getItem() == null) return;
        var state = sign.getBlockState();
        float rotation;
        boolean standing;
        if (state.getBlock() instanceof WallSignBlock) {
            rotation = state.getValue(WallSignBlock.FACING).toYRot();
            standing = false;
        } else if (state.getBlock() instanceof StandingSignBlock) {
            rotation = state.getValue(StandingSignBlock.ROTATION) * 360.0F / 16;
            standing = true;
        } else return;
        ItemStack item = new ItemStack(frame.getItem());
        // Resolve the frame's block mesh, not its flat inventory icon.
        item.set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath("buildscape", "sign_frame"));
        ItemStackRenderState model = new ItemStackRenderState();
        Minecraft.getInstance().getItemModelResolver().updateForTopItem(model, item,
                ItemDisplayContext.NONE, sign.getLevel(), null, 0);
        pose.pushPose();
        try {
            pose.translate(0.5, 0.5, 0.5);
            pose.mulPose(Axis.YP.rotationDegrees(-((rotation + 180) % 360)));
            if (standing) pose.translate(0, 0.3125, -0.4375);
            // Item models are centred; this is the reference block-model origin.
            if (buffer instanceof RenderCapture capture) {
                capture.record(pose, (target, collector, camera) -> model.submit(target, collector, light, overlay, 0));
            } else if (buffer instanceof SubmitNodeCollector collector) model.submit(pose, collector, light, overlay, 0);
        } finally { pose.popPose(); }
    }
}
