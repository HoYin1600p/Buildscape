package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.adapter.client.FestiveStackBufferSource;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.WingsLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(WingsLayer.class)
public abstract class ElytraLayerMixin {

    @ModifyVariable(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;FF)V", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private MultiBufferSource buildscape$elytraStackBuffers(MultiBufferSource source, PoseStack poseStack, MultiBufferSource bufferSource, int light, HumanoidRenderState state) {
        return FestiveStackBufferSource.forStack(source, state.chestEquipment);
    }
}
