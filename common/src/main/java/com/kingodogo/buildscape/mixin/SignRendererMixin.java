package com.kingodogo.buildscape.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.renderer.blockentity.SignRenderer")
public class SignRendererMixin {

    @Dynamic
    @Inject(
            method = "render(Lnet/minecraft/world/level/block/entity/SignBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
            at = @At("TAIL"),
            require = 0
    )
    private void buildscape$renderSignFrame(
            SignBlockEntity blockEntity,
            float partialTicks,
            PoseStack poseStack,
            @Coerce Object bufferSource,
            int combinedLight,
            int combinedOverlay,
            CallbackInfo ci
    ) {
        MixinFactory.renderSignFrame(blockEntity, partialTicks, poseStack, bufferSource, combinedLight, combinedOverlay);
    }
}

