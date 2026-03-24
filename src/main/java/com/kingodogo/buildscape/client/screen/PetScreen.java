package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.network.PetMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class PetScreen extends AbstractContainerScreen<PetMenu> {

    public PetScreen(PetMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 172;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack); // Darkens the world slightly
        super.render(poseStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(poseStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;

        RenderSystem.setShaderTexture(0, new net.minecraft.resources.ResourceLocation("minecraft", "textures/gui/container/dispenser.png"));
        
        // 1. Draw Player Inventory Lower Half natively from Dispenser
        this.blit(poseStack, relX, relY + 83, 0, 83, this.imageWidth, 83);
        
        // 2. Draw Top Border
        this.blit(poseStack, relX, relY, 0, 0, this.imageWidth, 16);
        
        // 3. Draw Left Border for Top Half
        this.blit(poseStack, relX, relY + 16, 0, 16, 7, 67);
        
        // 4. Draw Right Border for Top Half
        this.blit(poseStack, relX + this.imageWidth - 7, relY + 16, this.imageWidth - 7, 16, 7, 67);
        
        // 5. Fill Interior Background tightly
        fill(poseStack, relX + 7, relY + 16, relX + this.imageWidth - 7, relY + 83, 0xFFC6C6C6);

        // 6. Blit the 10 custom slots procedurally to form the Pyramid
        int[] xPositions = {44, 62, 80, 98, 116, 44, 80, 116, 44, 116};
        int[] yPositions = {18, 18, 18, 18, 18, 36, 36, 36, 54, 54};
        
        for (int i = 0; i < 10; ++i) {
            this.blit(poseStack, relX + xPositions[i] - 1, relY + yPositions[i] - 1, 61, 16, 18, 18);
        }
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        // Title formatting
        this.font.draw(poseStack, this.title, (float)this.titleLabelX, (float)this.titleLabelY, 0xFF404040); 
        this.font.draw(poseStack, "Inventory", 8, 73, 0xFF404040);
    }
}
