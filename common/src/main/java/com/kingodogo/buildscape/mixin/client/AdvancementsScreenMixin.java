package com.kingodogo.buildscape.mixin.client;

import com.kingodogo.buildscape.client.ClientAdvancementEvents;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AdvancementsScreen.class)
public abstract class AdvancementsScreenMixin {
    @Shadow private AdvancementTab selectedTab;

    @Dynamic
    @Inject(method = "mouseScrolled(DDDD)Z", at = @At("HEAD"), cancellable = true, require = 0)
    private void buildscape$pan(double mouseX, double mouseY, double horizontal, double vertical,
                                CallbackInfoReturnable<Boolean> cir) {
        ClientAdvancementEvents.pan(selectedTab, horizontal, vertical);
        cir.setReturnValue(true);
    }
}
