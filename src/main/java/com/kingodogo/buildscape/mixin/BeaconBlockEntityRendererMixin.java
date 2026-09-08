package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.util.BeaconBeamScanState;
import com.kingodogo.buildscape.util.BeaconScanContext;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BeaconBlockEntity.BeaconBeamSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = BeaconRenderer.class, priority = 900)
public class BeaconBlockEntityRendererMixin {

    @Inject(
            method = "render(Lnet/minecraft/world/level/block/entity/BeaconBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void buildscape$renderClippedBeam(
            BeaconBlockEntity blockEntity,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int combinedLight,
            int combinedOverlay,
            CallbackInfo callback
    ) {
        int cutoff = BeaconScanContext.confirmedHeight(blockEntity.getLevel(), blockEntity.getBlockPos());
        if (cutoff >= BeaconBeamScanState.UNLIMITED) {
            return;
        }

        long gameTime = blockEntity.getLevel().getGameTime();
        List<BeaconBeamSection> sections = blockEntity.getBeamSections();
        int yOffset = 0;
        for (BeaconBeamSection section : sections) {
            int remaining = cutoff - yOffset;
            if (remaining <= 0) {
                break;
            }

            int sectionHeight = section.getHeight();
            BeaconRendererInvoker.invokeRenderBeaconBeam(
                    poseStack,
                    bufferSource,
                    partialTicks,
                    gameTime,
                    yOffset,
                    Math.min(sectionHeight, remaining),
                    section.getColor()
            );
            yOffset += sectionHeight;
        }
        callback.cancel();
    }
}
