package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.util.BeaconBeamHeightAccessor;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.state.BeaconRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

@Mixin(BeaconRenderer.class)
public abstract class BeaconBlockEntityRendererMixin {
    @Unique
    private static final Map<BeaconRenderState, Integer> buildscape$beamHeights = Collections.synchronizedMap(new WeakHashMap<>());

    @Inject(method = "extract(Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/client/renderer/blockentity/state/BeaconRenderState;FLnet/minecraft/world/phys/Vec3;)V", at = @At("RETURN"))
    private static void buildscape$captureHeight(BlockEntity entity, BeaconRenderState state, float partialTick, Vec3 camera, CallbackInfo ci) {
        if (entity instanceof BeaconBeamHeightAccessor accessor) buildscape$beamHeights.put(state, accessor.buildscape$getBeamHeight());
        else buildscape$beamHeights.remove(state);
    }

    @Inject(method = "submit(Lnet/minecraft/client/renderer/blockentity/state/BeaconRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V", at = @At("HEAD"), cancellable = true)
    private void buildscape$submitClippedBeam(BeaconRenderState state, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera, CallbackInfo ci) {
        Integer height = buildscape$beamHeights.get(state);
        if (height == null || height < 0 || height >= com.kingodogo.buildscape.util.BeaconBeamScanState.UNLIMITED) return;
        int offset = 0;
        for (int i = 0; i < state.sections.size() && offset < height; i++) {
            var section = state.sections.get(i);
            int length = Math.min(height - offset, i == state.sections.size() - 1 ? height - offset : section.height());
            if (length > 0) BeaconRenderer.submitBeaconBeam(poses, collector, BeaconRenderer.BEAM_LOCATION,
                    state.beamRadiusScale, state.animationTime, offset, length, section.color(),
                    BeaconRenderer.SOLID_BEAM_RADIUS, BeaconRenderer.BEAM_GLOW_RADIUS);
            offset += length;
        }
        ci.cancel();
    }
}
