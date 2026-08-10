package com.kingodogo.buildscape.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BeaconRenderer.class)
public interface BeaconRendererInvoker {
    @Invoker("renderBeaconBeam")
    static void invokeRenderBeaconBeam(
        PoseStack poseStack,
        MultiBufferSource bufferSource,
        float partialTicks,
        long gameTime,
        int yOffset,
        int height,
        float[] color
    ) {
        throw new AssertionError();
    }
}
