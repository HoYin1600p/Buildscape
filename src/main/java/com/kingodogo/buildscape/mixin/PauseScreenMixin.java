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

        // Default fallback positions
        int targetX = this.width / 2 + 102;
        int targetY = this.height / 4 + 48;

        // Dynamically find the Statistics button to align perfectly next to it
        for (net.minecraft.client.gui.components.events.GuiEventListener listener : this.children()) {
            if (listener instanceof net.minecraft.client.gui.components.AbstractWidget widget) {
                net.minecraft.network.chat.Component msg = widget.getMessage();
                if (msg instanceof net.minecraft.network.chat.TranslatableComponent tc && 
                    (tc.getKey().equals("menu.statistics") || tc.getKey().equals("gui.stats"))) {
                    targetX = widget.x + widget.getWidth() + 4;
                    targetY = widget.y;
                    break;
                }
            }
        }

        // Create BuildScape Config button using standard Button for vanilla look and feel
        net.minecraft.client.gui.components.Button configButton = new net.minecraft.client.gui.components.Button(
                targetX - 2, targetY, 20, 20,
                new net.minecraft.network.chat.TextComponent("B"),
                (button) -> net.minecraft.client.Minecraft.getInstance().setScreen(new com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen(screen))
        );

        // Add the button using the standard method instead of reflection
        this.addRenderableWidget(configButton);
    }
}

