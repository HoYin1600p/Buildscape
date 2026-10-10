package com.kingodogo.buildscape.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;

import java.util.function.Consumer;
public interface IScreenDelegate {
    void init(Minecraft mc, int width, int height, Consumer<AbstractWidget> addWidget, Consumer<GuiEventListener> removeWidget);

    void render(Object poseStackOrGraphics, int mouseX, int mouseY, float partialTick);

    default void renderAfterWidgets(Object poseStackOrGraphics, int mouseX, int mouseY, float partialTick) {}

    default boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    default boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    default boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return false;
    }

    default boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        return false;
    }

    default boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    default boolean charTyped(char codePoint, int modifiers) {
        return false;
    }

    default void onClose() {}

    default void tick() {}

    default void resize(Minecraft mc, int width, int height) {}

    default boolean isPauseScreen() {
        return false;
    }

    default void setWrapperScreen(Object screen) {}

    default Object getWrapperScreen() {
        return null;
    }
}
