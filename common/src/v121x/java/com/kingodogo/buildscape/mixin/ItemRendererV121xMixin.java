package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.adapter.v121x.client.FestiveRenderTypes;
import com.kingodogo.buildscape.adapter.v121x.client.FestiveStackBufferSource;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererV121xMixin {

    @ModifyVariable(method = "render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private MultiBufferSource buildscape$itemStackBuffers(MultiBufferSource source, ItemStack stack) {
        return FestiveStackBufferSource.forStack(source, stack);
    }

    @Inject(method = "getFoilBufferDirect", at = @At("HEAD"), cancellable = true)
    private static void buildscape$festiveFoilBufferDirect(MultiBufferSource bufferSource, RenderType renderType, boolean isItem, boolean withFoil, CallbackInfoReturnable<VertexConsumer> cir) {
        if (withFoil && FestiveStackBufferSource.isFestive(bufferSource)) {
            cir.setReturnValue(VertexMultiConsumer.create(
                    bufferSource.getBuffer(isItem ? FestiveRenderTypes.festiveGlint() : FestiveRenderTypes.festiveEntityGlintDirect()),
                    bufferSource.getBuffer(renderType)));
        }
    }

    @Inject(method = "getFoilBuffer", at = @At("HEAD"), cancellable = true)
    private static void buildscape$festiveFoilBuffer(MultiBufferSource bufferSource, RenderType renderType, boolean isItem, boolean withFoil, CallbackInfoReturnable<VertexConsumer> cir) {
        if (withFoil && FestiveStackBufferSource.isFestive(bufferSource)) {
            cir.setReturnValue(Minecraft.useShaderTransparency() && renderType == Sheets.translucentItemSheet()
                    ? VertexMultiConsumer.create(bufferSource.getBuffer(FestiveRenderTypes.festiveGlintTranslucent()), bufferSource.getBuffer(renderType))
                    : VertexMultiConsumer.create(
                            bufferSource.getBuffer(isItem ? FestiveRenderTypes.festiveGlint() : FestiveRenderTypes.festiveEntityGlint()),
                            bufferSource.getBuffer(renderType)));
        }
    }

    @Inject(method = "getArmorFoilBuffer", at = @At("HEAD"), cancellable = true)
    private static void buildscape$festiveArmorFoilBuffer(MultiBufferSource bufferSource, RenderType renderType, boolean withFoil, CallbackInfoReturnable<VertexConsumer> cir) {
        if (withFoil && FestiveStackBufferSource.isFestive(bufferSource)) {
            cir.setReturnValue(VertexMultiConsumer.create(
                    bufferSource.getBuffer(FestiveRenderTypes.festiveArmorEntityGlint()),
                    bufferSource.getBuffer(renderType)));
        }
    }

    @Inject(method = "getCompassFoilBuffer", at = @At("HEAD"), cancellable = true)
    private static void buildscape$festiveCompassFoilBuffer(MultiBufferSource bufferSource, RenderType renderType, PoseStack.Pose pose, CallbackInfoReturnable<VertexConsumer> cir) {
        if (FestiveStackBufferSource.isFestive(bufferSource)) {
            cir.setReturnValue(VertexMultiConsumer.create(
                    // Main's festive texture uses the original unit-scale decal projection.
                    new SheetedDecalTextureGenerator(bufferSource.getBuffer(FestiveRenderTypes.festiveGlint()), pose, 1.0F),
                    bufferSource.getBuffer(renderType)));
        }
    }
}
