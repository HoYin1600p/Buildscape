package com.kingodogo.buildscape.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.renderer.blockentity.BeaconRenderer", priority = 900)
public class BeaconBlockEntityRendererMixin {

    @Dynamic
    @Inject(
            method = "render(Lnet/minecraft/world/level/block/entity/BeaconBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void buildscape$renderClippedBeam(
            BeaconBlockEntity blockEntity,
            float partialTicks,
            PoseStack poseStack,
            @Coerce Object bufferSource,
            int combinedLight,
            int combinedOverlay,
            CallbackInfo callback
    ) {
        MixinFactory.renderClippedBeaconBeam(blockEntity, partialTicks, poseStack, bufferSource, combinedLight, combinedOverlay);
    }
}

