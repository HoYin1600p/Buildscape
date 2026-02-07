package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PauseScreen.class)
public abstract class PauseScreenMixin extends Screen {
    protected PauseScreenMixin() {
        super(null);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addBuildScapeConfigButton(CallbackInfo ci) {
        PauseScreen screen = (PauseScreen) (Object) this;

        // Calculate button position - place it below "Options..." button
        int buttonWidth = 200;
        int buttonHeight = 20;
        int x = (this.width - buttonWidth) / 2;
        int y = this.height / 4 + 120 + 24 * 2; // Position below "Open to LAN" button

        // Create BuildScape Config button
        Button configButton = new Button(
                x, y, buttonWidth, buttonHeight,
                new TranslatableComponent("buildscape.config.title"),
                (button) -> net.minecraft.client.Minecraft.getInstance().setScreen(new BuildScapeConfigScreen(screen))
        );

        // Use reflection to access private fields and add the button
        try {
            // Access renderables field
            java.lang.reflect.Field renderablesField = Screen.class.getDeclaredField("renderables");
            renderablesField.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<Object> renderablesList = (List<Object>) renderablesField.get(this);
            renderablesList.add(configButton);

            // Access children field  
            java.lang.reflect.Field childrenField = Screen.class.getDeclaredField("children");
            childrenField.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<GuiEventListener> childrenList = (List<GuiEventListener>) childrenField.get(this);
            childrenList.add(configButton);

            // Access narratables field
            java.lang.reflect.Field narratablesField = Screen.class.getDeclaredField("narratables");
            narratablesField.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<NarratableEntry> narratablesList = (List<NarratableEntry>) narratablesField.get(this);
            narratablesList.add(configButton);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

