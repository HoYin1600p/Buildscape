package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.mixinsupport.ISulfurCubeCushionRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.TntRenderer;
import net.minecraft.client.renderer.entity.layers.SulfurCubeInnerLayer;
import net.minecraft.client.renderer.entity.state.SulfurCubeRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SulfurCubeInnerLayer.class)
public abstract class SulfurCubeInnerLayerMixin {

    @Inject(
            method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/SulfurCubeRenderState;FF)V",
            at = @At("TAIL")
    )
    private void buildscape$renderCushion(
            PoseStack poseStack,
            SubmitNodeCollector bufferSource,
            int packedLight,
            SulfurCubeRenderState renderState,
            float yRot,
            float xRot,
            CallbackInfo ci
    ) {
        if (renderState instanceof ISulfurCubeCushionRenderState cushionStateHolder) {
            BlockModelRenderState cushionBlock = cushionStateHolder.buildscape$getCushionBlock();
            if (!cushionBlock.isEmpty()) {
                int overlay = renderState.fuseRemainingTicks > 0.0F && TntRenderer.isLit(renderState.fuseRemainingTicks)
                        ? OverlayTexture.pack(OverlayTexture.u(1.0F), 10)
                        : LivingEntityRenderer.getOverlayCoords(renderState, 0.0F);

                poseStack.pushPose();
                poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
                if (renderState.isBaby) {
                    poseStack.scale(0.5F, 0.5F, 0.5F);
                }
                // Render flush on the top surface of the sulfur cube outer box
                poseStack.translate(-0.5F, 0.5625F, -0.5F);
                cushionBlock.submit(poseStack, bufferSource, renderState.lightCoords, overlay, renderState.outlineColor);
                poseStack.popPose();
            }
        }
    }
}
