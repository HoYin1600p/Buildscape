package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen;
import com.kingodogo.buildscape.config.BuildscapeClientConfig;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
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
        if (BuildscapeClientConfig.get().isConfigButtonHidden()) {
            return;
        }

        PauseScreen screen = (PauseScreen) (Object) this;

        // Default fallback positions
        int targetX = this.width / 2 + 102;
        int targetY = this.height / 4 + 48;

        boolean isFullPauseMenu = false;
        AbstractWidget statsButton = null;
		
		// Dynamically find the Statistics button to align perfectly next to it
        for (GuiEventListener listener : this.children()) {
            if (listener instanceof AbstractWidget widget) {
                Component msg = widget.getMessage();
                if (msg instanceof TranslatableComponent tc) {
                    String key = tc.getKey();
                    if (key.equals("menu.returnToGame")) {
                        isFullPauseMenu = true;
                    } else if (statsButton == null && (key.equals("menu.statistics") || key.equals("gui.stats"))) {
                        statsButton = widget;
                    }
                }
            }
        }

        if (!isFullPauseMenu) {
            return;
        }

        // Align perfectly next to the Statistics button when it is there.
        if (statsButton != null) {
            targetX = statsButton.x + statsButton.getWidth() + 4;
            targetY = statsButton.y;
        }

        Button configButton = new Button(
                targetX - 2,
                targetY,
                20,
                20,
                new TextComponent("B"),
                (button) -> net.minecraft.client.Minecraft.getInstance().setScreen(new BuildScapeConfigScreen(screen))
        );

        this.addRenderableWidget(configButton);
    }
}
