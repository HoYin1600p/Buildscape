package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.client.screen.workbench.WbRenderer;
import com.kingodogo.buildscape.network.BuildersPouchMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BuildersPouchScreen extends AbstractContainerScreen<BuildersPouchMenu> {

    private static final ResourceLocation BACKGROUND =
            new ResourceLocation("buildscape", "textures/gui/builders_pouch/builders_pouch_bg.png");
    private static final ResourceLocation TITLE =
            new ResourceLocation("buildscape", "textures/gui/builders_pouch/builders_pouch_title.png");

    /** Artwork size; the sheet itself is 256x256 with the panel anchored at (0,0). */
    private static final int GUI_WIDTH = 188;
    private static final int GUI_HEIGHT = 134;
    private static final int SHEET_SIZE = 256;

    // The flat face of the banner, excluding both the dark frame (x54/x133, y0/y16) and
    // the 1px bevel inside it (x55/x132, y1/y15) - same 76x13 area as the workbench.
    private static final int TITLE_X = 56;
    private static final int TITLE_Y = 2;
    private static final int TITLE_W = 76;
    private static final int TITLE_H = 13;

    public BuildersPouchScreen(BuildersPouchMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = GUI_WIDTH;
        this.imageHeight = GUI_HEIGHT;
        // Both captions are part of the artwork now, so the vanilla labels are pushed
        // off-screen rather than drawn on top of it.
        this.titleLabelX = -9999;
        this.titleLabelY = -9999;
        this.inventoryLabelX = -9999;
        this.inventoryLabelY = -9999;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTick);
        renderTooltip(poseStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        blit(poseStack, leftPos, topPos, 0f, 0f, GUI_WIDTH, GUI_HEIGHT, SHEET_SIZE, SHEET_SIZE);

        WbRenderer.drawTitleImage(poseStack, TITLE, WbRenderer.TITLE_INK_POUCH,
                leftPos + TITLE_X, topPos + TITLE_Y, TITLE_W, TITLE_H);
    }
}
