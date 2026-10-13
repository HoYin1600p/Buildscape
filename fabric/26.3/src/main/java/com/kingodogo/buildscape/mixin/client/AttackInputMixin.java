package com.kingodogo.buildscape.mixin.client;

import com.kingodogo.buildscape.client.BiomeBrushClientHandler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class AttackInputMixin {
    // Fabric's block/entity attack callbacks do not cover an attack into air.
    @Inject(method = "startAttack()Z", at = @At("HEAD"))
    private void buildscape$brushAttack(CallbackInfoReturnable<Boolean> cir) {
        Minecraft mc = Minecraft.getInstance();
        BiomeBrushClientHandler.onAttack(mc.player, mc.hitResult);
    }
}
