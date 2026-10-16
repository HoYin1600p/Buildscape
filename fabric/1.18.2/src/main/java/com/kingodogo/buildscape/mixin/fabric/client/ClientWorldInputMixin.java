package com.kingodogo.buildscape.mixin.fabric.client;

import com.kingodogo.buildscape.adapter.v118x.client.HazeBushParticleHandler;
import com.kingodogo.buildscape.client.BiomeBrushClientHandler;
import com.kingodogo.buildscape.client.ClientEvents;
import com.kingodogo.buildscape.client.TreeChopHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class ClientWorldInputMixin {
    @Inject(method = "setLevel(Lnet/minecraft/client/multiplayer/ClientLevel;)V", at = @At("HEAD"))
    private void buildscape$worldUnload(ClientLevel level, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && mc.level != level) {
            ClientEvents.onClientWorldUnload();
            HazeBushParticleHandler.clear();
        }
    }

    @Inject(method = "startAttack()Z", at = @At("HEAD"), cancellable = true)
    private void buildscape$attack(CallbackInfoReturnable<Boolean> cir) {
        Minecraft mc = Minecraft.getInstance();
        BiomeBrushClientHandler.onAttack(mc.player, mc.hitResult);
        if (mc.screen == null && TreeChopHandler.shouldCancelLeftClick(mc.player, mc.hitResult)) {
            if (mc.player != null) mc.player.swing(InteractionHand.MAIN_HAND);
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "continueAttack(Z)V", at = @At("HEAD"), cancellable = true)
    private void buildscape$heldAttack(boolean attacking, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (attacking && mc.screen == null && TreeChopHandler.shouldCancelLeftClick(mc.player, mc.hitResult)) {
            if (mc.gameMode != null) mc.gameMode.stopDestroyBlock();
            ci.cancel();
        }
    }
}
