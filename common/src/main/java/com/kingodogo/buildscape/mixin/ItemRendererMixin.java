package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.client.renderer.FestiveGlintHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.client.renderer.entity.ItemRenderer")
public class ItemRendererMixin {

    @Dynamic
    @ModifyVariable(method = "render", at = @At("HEAD"), argsOnly = true, ordinal = 0, require = 0)
    private Object wrapBufferSource(@Coerce Object source, ItemStack stack) {
        return MixinFactory.wrapGhostBufferSource(source, stack);
    }

    @Dynamic
    @Inject(method = "getArmorFoilBuffer(Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/RenderType;ZZ)Lcom/mojang/blaze3d/vertex/VertexConsumer;", at = @At("HEAD"), cancellable = true, require = 0)
    private static void buildscape$getFestiveArmorFoilBuffer118(@Coerce Object bufferSource, @Coerce Object renderType, boolean isItem, boolean withFoil, CallbackInfoReturnable<VertexConsumer> cir) {
        if (withFoil && FestiveGlintHandler.isCurrentFestive()) {
            cir.setReturnValue(MixinFactory.getFestiveArmorFoilBuffer(bufferSource, renderType, isItem));
        }
    }

    @Dynamic
    @Inject(method = "getArmorFoilBuffer(Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/RenderType;Z)Lcom/mojang/blaze3d/vertex/VertexConsumer;", at = @At("HEAD"), cancellable = true, require = 0)
    private static void buildscape$getFestiveArmorFoilBuffer121(@Coerce Object bufferSource, @Coerce Object renderType, boolean hasFoil, CallbackInfoReturnable<VertexConsumer> cir) {
        if (hasFoil && FestiveGlintHandler.isCurrentFestive()) {
            cir.setReturnValue(MixinFactory.getFestiveArmorFoilBuffer(bufferSource, renderType, true));
        }
    }

    @Dynamic
    @Inject(method = "getFoilBufferDirect", at = @At("HEAD"), cancellable = true, require = 0)
    private static void buildscape$getFestiveFoilBufferDirect(@Coerce Object bufferSource, @Coerce Object renderType, boolean noEntity, boolean withFoil, CallbackInfoReturnable<VertexConsumer> cir) {
        if (withFoil && FestiveGlintHandler.isCurrentFestive()) {
            cir.setReturnValue(MixinFactory.getFestiveFoilBufferDirect(bufferSource, renderType, noEntity));
        }
    }

    @Dynamic
    @Inject(method = "getFoilBuffer", at = @At("HEAD"), cancellable = true, require = 0)
    private static void buildscape$getFestiveFoilBuffer(@Coerce Object bufferSource, @Coerce Object renderType, boolean isItem, boolean withFoil, CallbackInfoReturnable<VertexConsumer> cir) {
        if (withFoil && FestiveGlintHandler.isCurrentFestive()) {
            cir.setReturnValue(MixinFactory.getFestiveFoilBuffer(bufferSource, renderType, isItem));
        }
    }

    @Dynamic
    @Inject(method = "render", at = @At("HEAD"), require = 0)
    private void buildscape$pushCurrentStack(@Coerce Object p0, @Coerce Object p1, boolean p2, @Coerce Object p3, @Coerce Object p4, int p5, int p6, @Coerce Object p7, CallbackInfo ci) {
        if (p0 instanceof ItemStack stack) {
            FestiveGlintHandler.push(stack);
        }
    }

    @Dynamic
    @Inject(method = "render", at = @At("RETURN"), require = 0)
    private void buildscape$popCurrentStack(@Coerce Object p0, @Coerce Object p1, boolean p2, @Coerce Object p3, @Coerce Object p4, int p5, int p6, @Coerce Object p7, CallbackInfo ci) {
        FestiveGlintHandler.pop();
    }

    @Dynamic
    @Inject(method = "getCompassFoilBuffer", at = @At("HEAD"), cancellable = true, require = 0)
    private static void buildscape$getFestiveCompassFoilBuffer(@Coerce Object bufferSource, @Coerce Object renderType, PoseStack.Pose pose, CallbackInfoReturnable<VertexConsumer> cir) {
        if (FestiveGlintHandler.isCurrentFestive()) {
            cir.setReturnValue(MixinFactory.getFestiveCompassFoilBuffer(bufferSource, renderType, pose));
        }
    }

    @Dynamic
    @Inject(method = "getCompassFoilBufferDirect", at = @At("HEAD"), cancellable = true, require = 0)
    private static void buildscape$getFestiveCompassFoilBufferDirect(@Coerce Object bufferSource, @Coerce Object renderType, PoseStack.Pose pose, CallbackInfoReturnable<VertexConsumer> cir) {
        if (FestiveGlintHandler.isCurrentFestive()) {
            cir.setReturnValue(MixinFactory.getFestiveCompassFoilBufferDirect(bufferSource, renderType, pose));
        }
    }
}

