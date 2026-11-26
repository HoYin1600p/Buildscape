package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.util.StonecutterMenuExtension;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.StonecutterMenu;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.List;

@Mixin(StonecutterScreen.class)
public abstract class StonecutterScreenMixin extends AbstractContainerScreen<StonecutterMenu> {
    public StonecutterScreenMixin(StonecutterMenu p_97741_, Inventory p_97742_, Component p_97743_) {
        super(p_97741_, p_97742_, p_97743_);
    }

    @Override
    protected void init() {
        super.init();

        int x = this.leftPos + 142;
        int y = this.topPos + 10;

        final AbstractContainerScreen<StonecutterMenu> screen = this;
        this.addRenderableWidget(new Button(x, y, 18, 10, TextComponent.EMPTY,
                StonecutterScreenMixin::buildscape$toggleCutAll) {
            @Override
            public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
                boolean active = ((StonecutterMenuExtension) screen.getMenu()).buildscape$isCutAll();

                int borderColor = 0xFF000000;
                int bgColor = active ? 0xFF107C41 : 0xFF4A4A4A;

                fill(poseStack, this.x, this.y, this.x + this.width, this.y + this.height, borderColor);
                fill(poseStack, this.x + 1, this.y + 1, this.x + this.width - 1, this.y + this.height - 1, bgColor);

                int thumbX = active ? this.x + this.width - 7 : this.x + 1;
                int thumbColor = 0xFFFFFFFF;
                fill(poseStack, thumbX, this.y + 1, thumbX + 6, this.y + this.height - 1, thumbColor);

                if (this.isHovered) {
                    this.renderToolTip(poseStack, mouseX, mouseY);
                }
            }

            @Override
            public void renderToolTip(PoseStack poseStack, int mouseX, int mouseY) {
                boolean active = ((StonecutterMenuExtension) screen.getMenu()).buildscape$isCutAll();
                List<Component> tooltip = new ArrayList<>();
                tooltip.add(new TranslatableComponent("tooltip.buildscape.stonecutter.title")
                        .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
                tooltip.add(new TranslatableComponent(active
                        ? "tooltip.buildscape.cut_all.on"
                        : "tooltip.buildscape.cut_stack.off").withStyle(ChatFormatting.BOLD));
                tooltip.add(new TranslatableComponent(active
                        ? "tooltip.buildscape.cut_all.desc"
                        : "tooltip.buildscape.cut_stack.desc").withStyle(ChatFormatting.GRAY));
                screen.renderComponentTooltip(poseStack, tooltip, mouseX, mouseY);
            }
        });
    }

    private static void buildscape$toggleCutAll(Button button) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || !(minecraft.player.containerMenu instanceof StonecutterMenu menu)) {
            return;
        }
        StonecutterMenuExtension extension = (StonecutterMenuExtension) menu;
        extension.buildscape$setCutAll(!extension.buildscape$isCutAll());
        if (minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, -123);
        }
    }
}
