package com.kingodogo.buildscape.adapter.v26x.client;

import com.kingodogo.buildscape.cosmetic.sign.SignFrameAttachment;
import com.kingodogo.buildscape.cosmetic.sign.SignFrameType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.SignRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
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
    private static final Map<SignRenderState, FrameState> FRAMES = Collections.synchronizedMap(new WeakHashMap<>());
    private ClientSignFrames() {}

    private static final class FrameState {
        final ItemStackRenderState model = new ItemStackRenderState();
        float rotation;
        boolean standing;
        int light, overlay;
    }

    public static void extract(SignBlockEntity sign, Object renderState, float partialTick) {
        SignRenderState state = (SignRenderState) renderState;
        FrameState frame = extractFrame(sign, state.lightCoords, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY);
        if (frame == null) FRAMES.remove(state);
        else FRAMES.put(state, frame);
    }

    public static void submit(Object renderState, PoseStack pose, Object collector, Object camera) {
        FrameState frame = FRAMES.get((SignRenderState) renderState);
        if (frame != null) submitFrame(frame, pose, (SubmitNodeCollector) collector);
    }

    public static void render(SignBlockEntity sign, float partialTick, PoseStack pose, Object buffer, int light, int overlay) {
        FrameState frame = extractFrame(sign, light, overlay);
        if (frame != null && buffer instanceof SubmitNodeCollector collector) submitFrame(frame, pose, collector);
    }

    private static FrameState extractFrame(SignBlockEntity sign, int light, int overlay) {
        if (sign == null || sign.getLevel() == null) return null;
        SignFrameType frame = SignFrameAttachment.getFrame(sign);
        if (frame == SignFrameType.NONE || frame.getItem() == null) return null;
        var state = sign.getBlockState();
        float rotation;
        boolean standing;
        if (state.getBlock() instanceof WallSignBlock) {
            rotation = state.getValue(WallSignBlock.FACING).toYRot();
            standing = false;
        } else if (state.getBlock() instanceof StandingSignBlock) {
            rotation = state.getValue(StandingSignBlock.ROTATION) * 360.0F / 16;
            standing = true;
        } else return null;
        ItemStack item = new ItemStack(frame.getItem());
        // Resolve the frame's block mesh, not its flat inventory icon.
        item.set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath("buildscape", "sign_frame"));
        FrameState extracted = new FrameState();
        extracted.rotation = rotation;
        extracted.standing = standing;
        extracted.light = light;
        extracted.overlay = overlay;
        Minecraft.getInstance().getItemModelResolver().updateForTopItem(extracted.model, item,
                ItemDisplayContext.NONE, sign.getLevel(), null, 0);
        return extracted;
    }

    private static void submitFrame(FrameState frame, PoseStack pose, SubmitNodeCollector collector) {
        pose.pushPose();
        try {
            pose.translate(0.5, 0.5, 0.5);
            pose.mulPose(Axis.YP.rotationDegrees(-((frame.rotation + 180) % 360)));
            if (frame.standing) pose.translate(0, 0.3125, -0.4375);
            // Item models are centred; this is the reference block-model origin.
            frame.model.submit(pose, collector, frame.light, frame.overlay, 0);
        } finally { pose.popPose(); }
    }
}
