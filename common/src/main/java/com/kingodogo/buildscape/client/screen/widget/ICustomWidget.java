package com.kingodogo.buildscape.client.screen.widget;

public interface ICustomWidget {
    int getX();
    int getY();
    int getWidth();
    int getHeight();

    void render(Object poseStackOrGraphics, int mouseX, int mouseY, float partialTick);
    default boolean isFocused() { return false; }
    default void setFocused(boolean focused) {}
    default boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= getX() && mouseX < getX() + getWidth()
                && mouseY >= getY() && mouseY < getY() + getHeight();
    }
    default boolean mouseClicked(double mouseX, double mouseY, int button) { return false; }
    default boolean mouseReleased(double mouseX, double mouseY, int button) { return false; }
    default boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) { return false; }
    default boolean mouseScrolled(double mouseX, double mouseY, double delta) { return false; }
    default boolean keyPressed(int keyCode, int scanCode, int modifiers) { return false; }
    default boolean charTyped(char codePoint, int modifiers) { return false; }
}
