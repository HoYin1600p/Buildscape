package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.util.BeaconBeamScanState;
import com.kingodogo.buildscape.util.BeaconScanContext;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

        com.kingodogo.buildscape.mixinsupport.MixinFactory.renderClippedBeaconBeam(
                blockEntity, partialTicks, poseStack, bufferSource, combinedLight, combinedOverlay);
        callback.cancel();
    }
}
