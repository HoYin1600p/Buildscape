package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.client.screen.workbench.WbRenderer;
import com.kingodogo.buildscape.menu.BuildersPouchMenu;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class BuildersPouchScreen {

    public static final CommonId BACKGROUND =
            new CommonId("buildscape", "textures/gui/builders_pouch/builders_pouch_bg.png");
    public static final CommonId TITLE =
            new CommonId("buildscape", "textures/gui/builders_pouch/builders_pouch_title.png");

    public static final int GUI_WIDTH = 188;
    public static final int GUI_HEIGHT = 134;
    public static final int SHEET_SIZE = 256;

    public static final int TITLE_X = 56;
    public static final int TITLE_Y = 2;
    public static final int TITLE_W = 76;
    public static final int TITLE_H = 13;

    private final BuildersPouchMenu menu;
    private final Inventory inventory;
    private final Component title;

    public BuildersPouchScreen(BuildersPouchMenu menu, Inventory inventory, Component title) {
        this.menu = menu;
        this.inventory = inventory;
        this.title = title;
    }

    public BuildersPouchMenu getMenu() {
        return menu;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Component getTitle() {
        return title;
    }

    public void renderBg(Object poseStackOrGraphics, float partialTick, int mouseX, int mouseY, int leftPos, int topPos) {
        Services.PLATFORM.resetShaderColor();
        Services.PLATFORM.blit(poseStackOrGraphics, BACKGROUND, leftPos, topPos, 0f, 0f, GUI_WIDTH, GUI_HEIGHT, SHEET_SIZE, SHEET_SIZE);

        WbRenderer.drawTitleImage(poseStackOrGraphics, TITLE, WbRenderer.TITLE_INK_POUCH,
                leftPos + TITLE_X, topPos + TITLE_Y, TITLE_W, TITLE_H);
    }
}
