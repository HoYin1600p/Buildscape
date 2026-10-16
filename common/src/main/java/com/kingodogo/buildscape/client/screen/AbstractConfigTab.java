package com.kingodogo.buildscape.client.screen;

import net.minecraft.client.gui.components.events.GuiEventListener;
import com.kingodogo.buildscape.client.screen.widget.ICustomWidget;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractConfigTab {
    protected final BuildScapeConfigScreen parent;
    private final List<GuiEventListener> tabWidgets = new ArrayList<>();

    public AbstractConfigTab(BuildScapeConfigScreen parent) {
        this.parent = parent;
    }

    public abstract void init();

    public abstract void render(Object poseStackOrGraphics, int mouseX, int mouseY, float partialTick);

    public void renderTooltips(Object poseStackOrGraphics, int mouseX, int mouseY, float partialTick) {
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    public boolean charTyped(char codePoint, int modifiers) {
        return false;
    }

    public void addTabWidget(GuiEventListener widget) {
        tabWidgets.add(widget);
        parent.addTabWidget(widget);
    }

    public void addTabWidget(ICustomWidget widget) {
        GuiEventListener nativeWidget = parent.addCustomWidget(widget);
        if (nativeWidget != null) {
            tabWidgets.add(nativeWidget);
        }
    }

    public void addTabWidget(com.kingodogo.buildscape.client.screen.widget.ScaledTextButton button) {
        addTabWidget(button.getButton());
    }

    public void addTabWidget(com.kingodogo.buildscape.client.screen.widget.ConfigCategoryButton button) {
        addTabWidget(button.getButton());
    }

    public void addTabWidget(com.kingodogo.buildscape.client.screen.widget.ColorSwatchButton button) {
        addTabWidget(button.getButton());
    }

    public void addTabWidget(com.kingodogo.buildscape.client.screen.widget.WideButton button) {
        addTabWidget(button.getButton());
    }

    public void addTabWidget(com.kingodogo.buildscape.client.screen.widget.FlatIconButton button) {
        addTabWidget(button.getButton());
    }

    public String getTabName() {
        String className = this.getClass().getSimpleName();
        if (className.endsWith("ConfigTab")) {
            return className.substring(0, className.length() - "ConfigTab".length());
        }
        return className;
    }

    public void onClose() {
        for (GuiEventListener widget : tabWidgets) {
            parent.removeTabWidget(widget);
        }
        tabWidgets.clear();
    }
}
