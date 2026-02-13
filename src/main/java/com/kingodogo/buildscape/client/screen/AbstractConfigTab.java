package com.kingodogo.buildscape.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.components.events.GuiEventListener;

public abstract class AbstractConfigTab {
    protected final BuildScapeConfigScreen parent;
    private final List<GuiEventListener> tabWidgets = new ArrayList<>();
    
    public AbstractConfigTab(BuildScapeConfigScreen parent) {
        this.parent = parent;
    }
    
    public abstract void init();
    
    public abstract void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick);
    
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
    
    protected void addTabWidget(GuiEventListener widget) {
        tabWidgets.add(widget);
        parent.addTabWidget(widget);
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

