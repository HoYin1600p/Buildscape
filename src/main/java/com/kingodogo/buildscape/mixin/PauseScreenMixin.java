package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PauseScreen.class)
public abstract class PauseScreenMixin extends Screen {
    protected PauseScreenMixin() {
        super(null);
    }

    @Inject(method = "init", at = @At("TAIL"))
    public void addBuildScapeConfigButton(CallbackInfo ci) {
        PauseScreen screen = (PauseScreen) (Object) this;

        // Calculate button position - place it below "Options..." button
        int buttonWidth = 200;
        int buttonHeight = 20;
        int x = (this.width - buttonWidth) / 2;
        int y = this.height / 4 + 120 + 24 * 2; // Position below "Open to LAN" button

        // Create BuildScape Config button using ScaledTextButton for consistent styling
        com.kingodogo.buildscape.client.screen.widget.ScaledTextButton configButton = new com.kingodogo.buildscape.client.screen.widget.ScaledTextButton(
                x, y, buttonWidth, buttonHeight,
                new TranslatableComponent("buildscape.config.title"),
                (button) -> net.minecraft.client.Minecraft.getInstance().setScreen(new BuildScapeConfigScreen(screen))
        );

        // Add the button using the standard method instead of reflection
        this.addRenderableWidget(configButton);
    }
}

