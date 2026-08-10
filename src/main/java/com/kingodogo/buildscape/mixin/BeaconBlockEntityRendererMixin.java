package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.util.BeaconBeamHeightAccessor;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BeaconRenderer.class)
public class BeaconBlockEntityRendererMixin {

    @Redirect(
        method = "render(Lnet/minecraft/world/level/block/entity/BeaconBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/blockentity/BeaconRenderer;renderBeaconBeam(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;FJII[F)V"
        )
    )
    private void buildscape$redirectRenderBeaconBeam(
        PoseStack poseStack,
        MultiBufferSource bufferSource,
        float partialTicks,
        long gameTime,
        int yOffset,
        int height,
        float[] color,
        BeaconBlockEntity blockEntity,
        float outerPartialTicks,
        PoseStack outerPoseStack,
        MultiBufferSource outerBufferSource,
        int combinedLight,
        int combinedOverlay
    ) {
        int newHeight = height;
        if (blockEntity instanceof BeaconBeamHeightAccessor) {
            int customHeight = ((BeaconBeamHeightAccessor) blockEntity).buildscape$getBeamHeight();
            if (customHeight < 1024) {
                newHeight = Math.min(height, customHeight);
            }
        }
        
        BeaconRendererInvoker.invokeRenderBeaconBeam(
            poseStack,
            bufferSource,
            partialTicks,
            gameTime,
            yOffset,
            newHeight,
            color
        );
    }
}
