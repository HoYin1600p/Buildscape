package com.kingodogo.buildscape.mixin.fabric.client;

import com.kingodogo.buildscape.adapter.v121x.client.HazeBushParticleHandler;
import com.kingodogo.buildscape.client.BiomeBrushClientHandler;
import com.kingodogo.buildscape.client.ClientEvents;
import com.kingodogo.buildscape.client.TreeChopHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class ClientWorldInputMixin {
    @Dynamic
    @Inject(method = "setLevel", at = @At("HEAD"), require = 0)
    private void buildscape$worldUnload(CallbackInfo ci) {
        if (Minecraft.getInstance().level != null) {
            ClientEvents.onClientWorldUnload();
            HazeBushParticleHandler.clear();
        }
    }
    @Dynamic
    @Inject(method = "startAttack()Z", at = @At("HEAD"), cancellable = true, require = 0)
    private void buildscape$attack(CallbackInfoReturnable<Boolean> cir) {
        Minecraft mc = Minecraft.getInstance();
        BiomeBrushClientHandler.onAttack(mc.player, mc.hitResult);
        if (mc.screen == null && TreeChopHandler.shouldCancelLeftClick(mc.player, mc.hitResult)) {
            if (mc.player != null) mc.player.swing(InteractionHand.MAIN_HAND);
            cir.setReturnValue(false);
        }
    }
    @Dynamic
    @Inject(method = "continueAttack(Z)V", at = @At("HEAD"), cancellable = true, require = 0)
    private void buildscape$heldAttack(boolean attacking, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (attacking && mc.screen == null && TreeChopHandler.shouldCancelLeftClick(mc.player, mc.hitResult)) {
            if (mc.gameMode != null) mc.gameMode.stopDestroyBlock();
            ci.cancel();
        }
    }
}
