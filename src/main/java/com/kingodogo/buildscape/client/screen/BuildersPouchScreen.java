package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.network.BuildersPouchMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BuildersPouchScreen extends AbstractContainerScreen<BuildersPouchMenu> {
    private static final ResourceLocation BACKGROUND =
            new ResourceLocation("textures/gui/container/generic_54.png");

    public BuildersPouchScreen(BuildersPouchMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageHeight = 132;
        this.inventoryLabelY = 38;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTick);
        renderTooltip(poseStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        blit(poseStack, leftPos, topPos, 0, 0, imageWidth, 35);
        blit(poseStack, leftPos, topPos + 35, 0, 126, imageWidth, 96);
    }
}
