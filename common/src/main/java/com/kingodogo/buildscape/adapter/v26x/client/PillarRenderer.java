package com.kingodogo.buildscape.adapter.v26x.client;

import com.kingodogo.buildscape.block.PillarBlockEntity;
import com.kingodogo.buildscape.client.renderer.PillarBlockEntityRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

/** Extracts a single resolved display, retaining the common reference placement/state rules. */
public final class PillarRenderer implements BlockEntityRenderer<PillarBlockEntity, PillarRenderer.State> {
    public static final class State extends BlockEntityRenderState {
        final ItemStackRenderState item = new ItemStackRenderState();
        final Matrix4f transform = new Matrix4f();
        EntityRenderState display;
        int overlay = OverlayTexture.NO_OVERLAY;

        public void extractItem(ItemStack stack, ItemDisplayContext context, PoseStack pose, int light, int overlay, int seed) {
            item.clear();
            display = null;
            this.lightCoords = light;
            this.overlay = overlay;
            transform.set(pose.last().pose());
            var client = Minecraft.getInstance();
            client.getItemModelResolver().updateForTopItem(item, stack, context, client.level, null, seed);
        }

        public void extractEntity(Entity entity, float partialTick, PoseStack pose, int light) {
            item.clear();
            display = Minecraft.getInstance().getEntityRenderDispatcher().extractEntity(entity, partialTick);
            display.lightCoords = light;
            transform.set(pose.last().pose());
        }
    }

    private final PillarBlockEntityRenderer placement = new PillarBlockEntityRenderer();

    @Override public State createRenderState() { return new State(); }

    @Override public void extractRenderState(PillarBlockEntity entity, State state, float partialTick,
            Vec3 camera, ModelFeatureRenderer.CrumblingOverlay breaking) {
        BlockEntityRenderState.extractBase(entity, state, breaking);
        state.item.clear();
        state.display = null;
        state.transform.identity();
        placement.render(entity, partialTick, new PoseStack(), state, state.lightCoords, OverlayTexture.NO_OVERLAY);
    }

    @Override public void submit(State state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        pose.pushPose();
        try {
            pose.mulPose(state.transform);
            if (state.display != null) Minecraft.getInstance().getEntityRenderDispatcher()
                    .submit(state.display, camera, 0, 0, 0, pose, collector);
            else state.item.submit(pose, collector, state.lightCoords, state.overlay, 0);
        } finally { pose.popPose(); }
    }
}
