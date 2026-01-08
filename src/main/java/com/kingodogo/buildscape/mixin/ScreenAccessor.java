package com.kingodogo.buildscape.mixin;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Screen.class)
public abstract class ScreenAccessor {
    @Shadow
    protected abstract <T extends AbstractWidget> T addRenderableWidget(T widget);
    
    // Expose the method for use outside the mixin
    public <T extends AbstractWidget> T buildscape$addRenderableWidget(T widget) {
        return this.addRenderableWidget(widget);
    }
}

