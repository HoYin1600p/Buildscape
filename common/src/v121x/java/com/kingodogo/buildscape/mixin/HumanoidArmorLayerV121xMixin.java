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
public abstract class HumanoidArmorLayerV121xMixin {
    private static final String PIECE = "renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;)V";
    // NeoForge adds an overload that carries the limb animation values; it does the real work there.
    private static final String PIECE_NEO = "renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;FFFFFF)V";

    @Inject(method = PIECE, at = @At("HEAD"))
    private void buildscape$pushArmorStack(PoseStack poseStack, MultiBufferSource bufferSource, LivingEntity entity, EquipmentSlot slot, int light, HumanoidModel<?> model, CallbackInfo ci) {
        FestiveGlintHandler.push(entity.getItemBySlot(slot));
    }

    @Inject(method = PIECE, at = @At("RETURN"))
    private void buildscape$popArmorStack(PoseStack poseStack, MultiBufferSource bufferSource, LivingEntity entity, EquipmentSlot slot, int light, HumanoidModel<?> model, CallbackInfo ci) {
        FestiveGlintHandler.pop();
    }

    @Inject(method = PIECE_NEO, at = @At("HEAD"), require = 0)
    private void buildscape$pushNeoArmorStack(PoseStack poseStack, MultiBufferSource bufferSource, LivingEntity entity, EquipmentSlot slot, int light, HumanoidModel<?> model, float a, float b, float c, float d, float e, float f, CallbackInfo ci) {
        FestiveGlintHandler.push(entity.getItemBySlot(slot));
    }

    @Inject(method = PIECE_NEO, at = @At("RETURN"), require = 0)
    private void buildscape$popNeoArmorStack(PoseStack poseStack, MultiBufferSource bufferSource, LivingEntity entity, EquipmentSlot slot, int light, HumanoidModel<?> model, float a, float b, float c, float d, float e, float f, CallbackInfo ci) {
        FestiveGlintHandler.pop();
    }
}
