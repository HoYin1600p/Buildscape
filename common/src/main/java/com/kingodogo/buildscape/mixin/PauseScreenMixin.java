package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.mixinsupport.MixinFactory;

import net.minecraft.client.gui.screens.PauseScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PauseScreen.class)
public abstract class PauseScreenMixin {

    @Inject(method = "init", at = @At("TAIL"))
    public void addBuildScapeConfigButton(CallbackInfo ci) {
        PauseScreen screen = (PauseScreen) (Object) this;
        MixinFactory.addPauseScreenButton(screen, screen.width, screen.height, screen.children());
    }
}
