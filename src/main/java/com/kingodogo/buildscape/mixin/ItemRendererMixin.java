package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.client.renderer.FestiveGlintHandler;
import com.kingodogo.buildscape.client.renderer.FestiveRenderTypes;
import com.kingodogo.buildscape.client.renderer.TransparentMultiBufferSource;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    private static final float GHOST_ALPHA = 0.3f;

    @ModifyVariable(method = "render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private MultiBufferSource wrapBufferSource(
            MultiBufferSource source,
            ItemStack stack,
            ItemTransforms.TransformType transformType) {
        if (!stack.isEmpty() && stack.hasTag() && stack.getTag().getBoolean("ghost")) {
            return new TransparentMultiBufferSource(source, GHOST_ALPHA);
        }
        return source;
    }

    @Inject(method = "render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V", at = @At("HEAD"))
    private void buildscape$pushCurrentStack(ItemStack stack, ItemTransforms.TransformType transformType, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, BakedModel model, CallbackInfo ci) {
        FestiveGlintHandler.push(stack);
    }

    @Inject(method = "render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V", at = @At("RETURN"))
    private void buildscape$popCurrentStack(ItemStack stack, ItemTransforms.TransformType transformType, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, BakedModel model, CallbackInfo ci) {
        FestiveGlintHandler.pop();
    }

    @Inject(method = "getFoilBufferDirect", at = @At("HEAD"), cancellable = true)
    private static void buildscape$getFestiveFoilBufferDirect(MultiBufferSource bufferSource, RenderType renderType, boolean noEntity, boolean withFoil, CallbackInfoReturnable<VertexConsumer> cir) {
        if (withFoil && FestiveGlintHandler.isCurrentFestive()) {
            cir.setReturnValue(
                    Minecraft.useShaderTransparency() && renderType == Sheets.translucentItemSheet()
                            ? VertexMultiConsumer.create(bufferSource.getBuffer(FestiveRenderTypes.festiveGlintTranslucent()), bufferSource.getBuffer(renderType))
                            : VertexMultiConsumer.create(bufferSource.getBuffer(noEntity ? FestiveRenderTypes.festiveGlintDirect() : FestiveRenderTypes.festiveEntityGlintDirect()), bufferSource.getBuffer(renderType))
            );
        }
    }

    @Inject(method = "getFoilBuffer", at = @At("HEAD"), cancellable = true)
    private static void buildscape$getFestiveFoilBuffer(MultiBufferSource bufferSource, RenderType renderType, boolean isItem, boolean withFoil, CallbackInfoReturnable<VertexConsumer> cir) {
        if (withFoil && FestiveGlintHandler.isCurrentFestive()) {
            cir.setReturnValue(
                    Minecraft.useShaderTransparency() && renderType == Sheets.translucentItemSheet()
                            ? VertexMultiConsumer.create(bufferSource.getBuffer(FestiveRenderTypes.festiveGlintTranslucent()), bufferSource.getBuffer(renderType))
                            : VertexMultiConsumer.create(bufferSource.getBuffer(isItem ? FestiveRenderTypes.festiveGlint() : FestiveRenderTypes.festiveEntityGlint()), bufferSource.getBuffer(renderType))
            );
        }
    }

    @Inject(method = "getArmorFoilBuffer", at = @At("HEAD"), cancellable = true)
    private static void buildscape$getFestiveArmorFoilBuffer(MultiBufferSource bufferSource, RenderType renderType, boolean isItem, boolean withFoil, CallbackInfoReturnable<VertexConsumer> cir) {
        if (withFoil && FestiveGlintHandler.isCurrentFestive()) {
            cir.setReturnValue(
                    VertexMultiConsumer.create(bufferSource.getBuffer(isItem ? FestiveRenderTypes.festiveArmorGlint() : FestiveRenderTypes.festiveArmorEntityGlint()), bufferSource.getBuffer(renderType))
            );
        }
    }

    @Inject(method = "getCompassFoilBuffer", at = @At("HEAD"), cancellable = true)
    private static void buildscape$getFestiveCompassFoilBuffer(MultiBufferSource bufferSource, RenderType renderType, PoseStack.Pose pose, CallbackInfoReturnable<VertexConsumer> cir) {
        if (FestiveGlintHandler.isCurrentFestive()) {
            cir.setReturnValue(
                    VertexMultiConsumer.create(
                            new SheetedDecalTextureGenerator(bufferSource.getBuffer(FestiveRenderTypes.festiveGlint()), pose.pose(), pose.normal()),
                            bufferSource.getBuffer(renderType)
                    )
            );
        }
    }

    @Inject(method = "getCompassFoilBufferDirect", at = @At("HEAD"), cancellable = true)
    private static void buildscape$getFestiveCompassFoilBufferDirect(MultiBufferSource bufferSource, RenderType renderType, PoseStack.Pose pose, CallbackInfoReturnable<VertexConsumer> cir) {
        if (FestiveGlintHandler.isCurrentFestive()) {
            cir.setReturnValue(
                    VertexMultiConsumer.create(
                            new SheetedDecalTextureGenerator(bufferSource.getBuffer(FestiveRenderTypes.festiveGlintDirect()), pose.pose(), pose.normal()),
                            bufferSource.getBuffer(renderType)
                    )
            );
        }
    }
}
