package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.adapter.v121x.client.FestiveRenderTypes;
import com.kingodogo.buildscape.client.renderer.FestiveGlintHandler;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Worn armour and elytra take their glint straight from RenderType, so swap it while a festive stack is current. */
@Mixin(RenderType.class)
public abstract class RenderTypeArmorGlintV121xMixin {
    @Inject(method = "armorEntityGlint", at = @At("HEAD"), cancellable = true)
    private static void buildscape$festiveArmorEntityGlint(CallbackInfoReturnable<RenderType> cir) {
        if (FestiveGlintHandler.isCurrentFestive()) {
            cir.setReturnValue(FestiveRenderTypes.festiveArmorEntityGlint());
        }
    }
}
