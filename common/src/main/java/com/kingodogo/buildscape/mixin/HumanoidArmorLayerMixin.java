package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.client.renderer.FestiveGlintHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer")
public class HumanoidArmorLayerMixin {

    @Dynamic
    @Inject(method = "renderArmorPiece", at = @At("HEAD"), require = 0)
    private void buildscape$pushArmorStack(PoseStack poseStack, @Coerce Object bufferSource, LivingEntity entity, EquipmentSlot slot, int combinedLight, @Coerce Object model, CallbackInfo ci) {
        if (entity != null) {
            FestiveGlintHandler.push(entity.getItemBySlot(slot));
        }
    }

    @Dynamic
    @Inject(method = "renderArmorPiece", at = @At("RETURN"), require = 0)
    private void buildscape$popArmorStack(PoseStack poseStack, @Coerce Object bufferSource, LivingEntity entity, EquipmentSlot slot, int combinedLight, @Coerce Object model, CallbackInfo ci) {
        FestiveGlintHandler.pop();
    }
}

