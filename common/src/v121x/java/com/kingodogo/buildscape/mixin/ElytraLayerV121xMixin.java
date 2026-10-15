package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.adapter.v121x.client.FestiveStackBufferSource;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ElytraLayer.class)
public abstract class ElytraLayerV121xMixin {

    @ModifyVariable(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private MultiBufferSource buildscape$elytraStackBuffers(MultiBufferSource source, PoseStack poseStack, MultiBufferSource bufferSource, int light, LivingEntity entity) {
        return FestiveStackBufferSource.forStack(source, entity.getItemBySlot(EquipmentSlot.CHEST));
    }
}
