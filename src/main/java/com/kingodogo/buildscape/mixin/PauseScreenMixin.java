package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PauseScreen.class)
public abstract class PauseScreenMixin extends Screen {
    protected PauseScreenMixin() {
        super(null);
    }
    
    @Shadow
    protected abstract <T extends AbstractWidget> T addRenderableWidget(T widget);
    
    @Inject(method = "init", at = @At("TAIL"))
    private void addBuildScapeConfigButton(CallbackInfo ci) {
        PauseScreen screen = (PauseScreen)(Object)this;
        
        // Calculate button position - place it below "Options..." button
        // Standard pause menu has buttons at center, we'll add ours there too
        int buttonWidth = 200;
        int buttonHeight = 20;
        int x = (screen.width - buttonWidth) / 2;
        int y = screen.height / 4 + 120 + 24 * 2; // Position below "Open to LAN" button
        
        // Create BuildScape Config button
        Button configButton = new Button(
            x, y, buttonWidth, buttonHeight,
            new TranslatableComponent("buildscape.config.title"),
            (button) -> net.minecraft.client.Minecraft.getInstance().setScreen(new BuildScapeConfigScreen(screen))
        );
        
        // Add the button using shadow method
        this.addRenderableWidget(configButton);
    }
}

