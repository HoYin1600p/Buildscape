package com.kingodogo.buildscape.mixin.fabric.client;

import com.kingodogo.buildscape.client.ClientAdvancementEvents;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AdvancementsScreen.class)
public abstract class AdvancementsScreenMixin extends Screen {
    @Shadow private AdvancementTab selectedTab;

    protected AdvancementsScreenMixin(Component title) {
        super(title);
    }

    // This screen inherits the default scroll handler in 1.18.2.
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        ClientAdvancementEvents.pan(selectedTab, 0.0D, delta);
        return true;
    }
}
