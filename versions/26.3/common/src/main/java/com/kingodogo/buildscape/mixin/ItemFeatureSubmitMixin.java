package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.client.renderer.FestiveGlintHandler;
import com.kingodogo.buildscape.mixinsupport.FestiveSubmission;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.world.item.ItemDisplayContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ItemFeatureRenderer.Submit.class)
public abstract class ItemFeatureSubmitMixin {
    @Inject(method = "<init>(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lnet/minecraft/world/item/ItemDisplayContext;III[ILjava/util/List;Lnet/minecraft/client/renderer/item/ItemStackRenderState$FoilType;)V", at = @At("RETURN"))
    private void buildscape$rememberSubmittedStack(PoseStack.Pose pose, ItemDisplayContext displayContext,
            int lightCoords, int overlayCoords, int outlineColor, int[] tintLayers, List<BakedQuad> quads,
            ItemStackRenderState.FoilType foilType, CallbackInfo ci) {
        FestiveSubmission.remember((ItemFeatureRenderer.Submit) (Object) this, FestiveGlintHandler.getCurrent());
    }
}
