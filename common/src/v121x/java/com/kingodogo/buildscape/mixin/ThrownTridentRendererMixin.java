package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.adapter.v121x.client.FestiveStackBufferSource;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ThrownTridentRenderer;
import net.minecraft.world.entity.projectile.ThrownTrident;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ThrownTridentRenderer.class)
public class ThrownTridentRendererMixin {

    @ModifyVariable(method = "render(Lnet/minecraft/world/entity/projectile/ThrownTrident;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private MultiBufferSource buildscape$tridentStackBuffers(MultiBufferSource source, ThrownTrident thrownTrident) {
        return FestiveStackBufferSource.forStack(source,
                com.kingodogo.buildscape.mixinsupport.MixinFactory.getThrownTridentItem(thrownTrident));
    }
}
