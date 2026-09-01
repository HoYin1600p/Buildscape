package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.client.renderer.FestiveGlintHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public class HumanoidArmorLayerMixin<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> {

    @Inject(method = "renderArmorPiece", at = @At("HEAD"))
    private void buildscape$pushArmorStack(PoseStack poseStack, MultiBufferSource bufferSource, T entity, EquipmentSlot slot, int combinedLight, A model, CallbackInfo ci) {
        FestiveGlintHandler.push(entity.getItemBySlot(slot));
    }

    @Inject(method = "renderArmorPiece", at = @At("RETURN"))
    private void buildscape$popArmorStack(PoseStack poseStack, MultiBufferSource bufferSource, T entity, EquipmentSlot slot, int combinedLight, A model, CallbackInfo ci) {
        FestiveGlintHandler.pop();
    }
}
