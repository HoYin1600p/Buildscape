package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.adapter.v121x.client.FestiveStackBufferSource;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerV121xMixin {
    private static final String PIECE = "renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;)V";
    // NeoForge adds an overload that carries the limb animation values; it does the real work there.
    private static final String PIECE_NEO = "renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;FFFFFF)V";

    @ModifyVariable(method = PIECE, at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private MultiBufferSource buildscape$armorStackBuffers(MultiBufferSource source, PoseStack poseStack, MultiBufferSource bufferSource, LivingEntity entity, EquipmentSlot slot) {
        return FestiveStackBufferSource.forStack(source, entity.getItemBySlot(slot));
    }

    @Dynamic("NeoForge armour rendering overload")
    @ModifyVariable(method = PIECE_NEO, at = @At("HEAD"), argsOnly = true, ordinal = 0, require = 0)
    private MultiBufferSource buildscape$neoArmorStackBuffers(MultiBufferSource source, PoseStack poseStack, MultiBufferSource bufferSource, LivingEntity entity, EquipmentSlot slot) {
        return FestiveStackBufferSource.forStack(source, entity.getItemBySlot(slot));
    }
}
