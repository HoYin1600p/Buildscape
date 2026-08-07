package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.block.BuildersWorkbenchBlockEntity;
import com.kingodogo.buildscape.client.screen.workbench.WbRenderer;
import com.kingodogo.buildscape.network.BuildersWorkbenchActionPacket;
import com.kingodogo.buildscape.network.BuildersWorkbenchMenu;
import com.kingodogo.buildscape.network.ModMessages;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

/**
 * BuildersWorkbenchScreen — custom textured workstation GUI.
 * <p>
 * Width = 240
 * Height = 266
 */
public class BuildersWorkbenchScreen extends AbstractContainerScreen<BuildersWorkbenchMenu> {

    private static final int W = 240;
    private static final int H = 266;

    // Tab buttons inside the dip
    private static final int TAB_Y = -12;
    private static final int TAB_H = 12;
    private static final int TAB_W = 20;

    // Action buttons in Gradient Builder (tab 1 only)
    private static final int GRAD_BTN_Y = 10;
    private static final int GRAD_BTN_H = 12;

    private int activeTab = 0;
    private int filterMask = 0; // 0 = All, 1 = Filtered, 2 = Survival
    private float animProgress = 0f;
    private long openTime;

    public BuildersWorkbenchScreen(BuildersWorkbenchMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
        this.imageWidth = W;
        this.imageHeight = H;
        this.titleLabelX = -9999;
        this.titleLabelY = -9999;
        this.inventoryLabelX = -9999;
        this.inventoryLabelY = -9999;
    }

    @Override
    protected void init() {
        this.activeTab = this.menu.getActiveTab();
        this.imageWidth = this.activeTab == 0 ? 176 : 240;
        this.imageHeight = this.activeTab == 0 ? 192 : 266;
        super.init();
        openTime = System.currentTimeMillis();
        this.filterMask = this.menu.getFilterMask();
    }

    @Override
    public void render(PoseStack ps, int mouseX, int mouseY, float partialTick) {
        this.activeTab = this.menu.getActiveTab();
        this.imageWidth = this.activeTab == 0 ? 176 : 240;
        this.imageHeight = this.activeTab == 0 ? 192 : 266;
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        float scale = 1.0f;
        if (this.width < this.imageWidth || this.height < this.imageHeight) {
            scale = Math.min((float) this.width / (float) this.imageWidth, (float) this.height / (float) this.imageHeight);
        }

        int finalMouseX = mouseX;
        int finalMouseY = mouseY;
        if (scale < 1.0f) {
            ps.pushPose();
            ps.translate(this.width / 2.0f, this.height / 2.0f, 0.0f);
            ps.scale(scale, scale, 1.0f);
            ps.translate(-this.width / 2.0f, -this.height / 2.0f, 0.0f);
            finalMouseX = (int) ((mouseX - this.width / 2.0f) / scale + this.width / 2.0f);
            finalMouseY = (int) ((mouseY - this.height / 2.0f) / scale + this.height / 2.0f);
        }

        this.renderBackground(ps);
        super.render(ps, finalMouseX, finalMouseY, partialTick);

        // Render Cycle Buttons on top of the slot items
        int px = this.leftPos;
        int py = this.topPos;
        if (activeTab == 0) {
            for (int i = 0; i < 9; i++) {
                int col = i % 3;
                int row = i / 3;
                int sx = px + 63 + col * 18;
                int sy = py + 24 + row * 18;
                if (!this.menu.getSlot(BuildersWorkbenchMenu.MENU_COLOR_RESULT_START + i).getItem().isEmpty()) {
                    boolean hovered = isIn(finalMouseX, finalMouseY, sx + 12, sy + 12, 5, 5);
                    int color = hovered ? 0xFF00FFCC : 0xFF00AA88;
                    fill(ps, sx + 12, sy + 12, sx + 17, sy + 17, color);
                    fill(ps, sx + 14, sy + 14, sx + 15, sy + 15, 0xFF000000);
                }
            }
        } else if (activeTab == 1) {
            for (int i = 1; i < 8; i++) { // only slots 1-7 are result slots
                int sx = px + 8 + i * 18;
                int sy = py + 77;
                if (!this.menu.getSlot(BuildersWorkbenchMenu.MENU_GRADIENT_OUTPUT_START + i).getItem().isEmpty()) {
                    boolean hovered = isIn(finalMouseX, finalMouseY, sx + 12, sy + 12, 5, 5);
                    int color = hovered ? 0xFF00FFCC : 0xFF00AA88;
                    fill(ps, sx + 12, sy + 12, sx + 17, sy + 17, color);
                    fill(ps, sx + 14, sy + 14, sx + 15, sy + 15, 0xFF000000);
                }
            }
        }

        this.renderTooltip(ps, finalMouseX, finalMouseY);

        // Render tab button tooltips
        if (isIn(finalMouseX, finalMouseY, px + 8, py - 12, 16, 16)) {
            this.renderComponentTooltip(ps, java.util.List.of(new net.minecraft.network.chat.TranslatableComponent(
                    "screen.buildscape.builders_workbench.color_builder")), finalMouseX, finalMouseY);
        } else if (isIn(finalMouseX, finalMouseY, px + 26, py - 12, 16, 16)) {
            this.renderComponentTooltip(ps, java.util.List.of(new net.minecraft.network.chat.TranslatableComponent(
                    "screen.buildscape.builders_workbench.gradient_builder")), finalMouseX, finalMouseY);
        }

        if (scale < 1.0f) {
            ps.popPose();
        }
    }

    @Override
    protected void renderTooltip(PoseStack ps, int x, int y) {
        int px = this.leftPos;
        int py = this.topPos;

        // Check button hover tooltips first
        if (activeTab == 0) {
            for (int i = 0; i < 9; i++) {
                int col = i % 3;
                int row = i / 3;
                int sx = px + 63 + col * 18;
                int sy = py + 24 + row * 18;
                if (!this.menu.getSlot(BuildersWorkbenchMenu.MENU_COLOR_RESULT_START + i).getItem().isEmpty()
                        && isIn(x, y, sx + 12, sy + 12, 5, 5)) {
                    this.renderComponentTooltip(ps, java.util.List.of(new net.minecraft.network.chat.TranslatableComponent(
                            "screen.buildscape.builders_workbench.cycle")), x, y);
                    return;
                }
            }
        } else if (activeTab == 1) {
            for (int i = 1; i < 8; i++) {
                int sx = px + 8 + i * 18;
                int sy = py + 77;
                if (!this.menu.getSlot(BuildersWorkbenchMenu.MENU_GRADIENT_OUTPUT_START + i).getItem().isEmpty()
                        && isIn(x, y, sx + 12, sy + 12, 5, 5)) {
                    this.renderComponentTooltip(ps, java.util.List.of(new net.minecraft.network.chat.TranslatableComponent(
                            "screen.buildscape.builders_workbench.cycle")), x, y);
                    return;
                }
            }
        }

        // Normal slot tooltips
        if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            ItemStack stack = this.hoveredSlot.getItem();
            if (this.hoveredSlot.container == this.menu.getBlockEntity()) {
                int slotIndex = this.hoveredSlot.getContainerSlot();
                boolean isResult = (activeTab == 0 && slotIndex >= BuildersWorkbenchBlockEntity.SLOT_PRESETS_START && slotIndex <= BuildersWorkbenchBlockEntity.SLOT_PRESETS_END)
                        || (activeTab == 1 && slotIndex >= (BuildersWorkbenchBlockEntity.SLOT_GRADIENT_START + 1) && slotIndex <= (BuildersWorkbenchBlockEntity.SLOT_GRADIENT_END - 1));
                if (isResult) {
                    java.util.List<Component> tooltip = this.getTooltipFromItem(stack);
                    tooltip.add(new net.minecraft.network.chat.TranslatableComponent(
                            "screen.buildscape.builders_workbench.cycle_hint"));
                    this.renderComponentTooltip(ps, tooltip, x, y);
                    return;
                }
            }
        }
        super.renderTooltip(ps, x, y);
    }

    @Override
    protected void renderBg(PoseStack ps, float partialTick, int mouseX, int mouseY) {
        float tick = (System.currentTimeMillis() - openTime) / 50f;
        animProgress = Math.min(1f, animProgress + 0.003f);

        int px = this.leftPos;
        int py = this.topPos;

        // Ensure slot positions are synced correctly on the client
        this.menu.setupSlotPositions();

        this.activeTab = this.menu.getActiveTab();
        this.filterMask = this.menu.getFilterMask();

        RenderSystem.disableDepthTest();

        // ── 1. Tab buttons inside the dip ────────────────────────────────────────
        float scale = 1.0f;
        if (this.width < this.imageWidth || this.height < this.imageHeight) {
            scale = Math.min((float) this.width / (float) this.imageWidth, (float) this.height / (float) this.imageHeight);
        }
        int finalMouseX = mouseX;
        int finalMouseY = mouseY;
        if (scale < 1.0f) {
            finalMouseX = (int) ((mouseX - this.width / 2.0f) / scale + this.width / 2.0f);
            finalMouseY = (int) ((mouseY - this.height / 2.0f) / scale + this.height / 2.0f);
        }

        if (activeTab == 0) {
            boolean cpTabHov = isIn(finalMouseX, finalMouseY, px + 8, py - 8, 8, 8);
            WbRenderer.drawRescaledIcon(ps, px + 9, py - 7, WbRenderer.ICON_COLOR_BUILDER, 6, 6, 16, 16, cpTabHov);

            boolean gbTabHov = isIn(finalMouseX, finalMouseY, px + 18, py - 8, 8, 8);
            WbRenderer.drawRescaledIcon(ps, px + 19, py - 7, WbRenderer.ICON_GRADIENT_BUILDER, 6, 6, 18, 18, gbTabHov);
        } else {
            boolean cpTabHov = isIn(finalMouseX, finalMouseY, px + 8, py - 12, 16, 16);
            WbRenderer.drawTabButton(ps, px + 8, py - 12, activeTab == 0, cpTabHov, WbRenderer.ICON_COLOR_BUILDER, 16, 16);

            boolean gbTabHov = isIn(finalMouseX, finalMouseY, px + 26, py - 12, 16, 16);
            WbRenderer.drawTabButton(ps, px + 26, py - 12, activeTab == 1, gbTabHov, WbRenderer.ICON_GRADIENT_BUILDER, 18, 18);
        }

        // ── 2. Workstation and Slots Layout ──────────────────────────────────────
        if (activeTab == 0) {
            renderColorPickerTab(ps, px, py, finalMouseX, finalMouseY, tick);
        } else {
            renderGradientBuilderTab(ps, px, py, finalMouseX, finalMouseY, tick);
        }

        RenderSystem.enableDepthTest();
    }

    @Override
    protected void renderLabels(PoseStack ps, int mouseX, int mouseY) {
        // Labels are drawn in screen-relative space in renderBg
    }

    private void renderColorPickerTab(PoseStack ps, int px, int py,
                                      int mouseX, int mouseY, float tick) {
        // Workstation background (ears shape with bottom protrusion)
        WbRenderer.drawColorBuilderBG(ps, px, py, imageWidth);

        // Filter Buttons
        for (int i = 0; i < 3; i++) {
            boolean hov = isIn(mouseX, mouseY, px + 131, py + 22 + i * 22, 18, 18);
            WbRenderer.drawFilterButton(ps, px + 131, py + 22 + i * 22, i, filterMask, hov);
        }

        // Title text centered on the pre-drawn top wooden banner
        String titleText = new net.minecraft.network.chat.TranslatableComponent(
                "screen.buildscape.builders_workbench.color_builder").getString();
        int tw = this.font.width(titleText);
        this.font.draw(ps, new net.minecraft.network.chat.TextComponent(titleText), px + 56 + (64 - tw) / 2.0f, py - 4 + 3.0f, 0xFF372211);

        // Pouch slots backgrounds (drawn directly on tan BG without wooden frame)
        WbRenderer.drawSlotTexture(ps, WbRenderer.SLOT_INPUT, px + 46, py + 92); // Slot 10: Input Pouch
        WbRenderer.drawSlotTexture(ps, WbRenderer.SLOT_OUTPUT, px + 112, py + 92); // Slot 11: Output Pouch

        // Process Arrow (inactive by default, filled with active version based on progress)
        RenderSystem.setShaderTexture(0, WbRenderer.BUILDERS_ARROW);
        WbRenderer.blitFloat(ps, px + 64, py + 93, 48, 16, 0f, 0f, 1f, 1f);

        float progress = (float) this.menu.getCopyProgress() / 40.0f;
        if (progress > 0.0f) {
            int progressW = (int) (progress * 48);
            RenderSystem.setShaderTexture(0, WbRenderer.BUILDERS_ARROW_ACTIVE);
            WbRenderer.blitFloat(ps, px + 64, py + 93, progressW, 16, 0f, 0f, progress, 1f);
        }
    }

    private void renderGradientBuilderTab(PoseStack ps, int px, int py,
                                          int mouseX, int mouseY, float tick) {
        // Workstation background (ears shape) - extended to 138 to wrap slots fully at bottom
        WbRenderer.drawWorkstationBG(ps, px, py, imageWidth, 138);

        // Inset boxes
        WbRenderer.drawInsetBox(ps, px + 3, py + 42, 174, 30);
        WbRenderer.drawInsetBox(ps, px + 3, py + 72, 174, 30);
        WbRenderer.drawInsetBox(ps, px + 3, py + 106, 30, 30);
        WbRenderer.drawInsetBox(ps, px + 147, py + 106, 30, 30);
        WbRenderer.drawInsetBox(ps, px + 180, py + 16, 32, 80); // Filter box on right

        // Title Banner (wooden banner centered at top)
        WbRenderer.drawTitleBanner(ps, px + 80, py + 6, 80, 14);
        String titleText = new net.minecraft.network.chat.TranslatableComponent(
                "screen.buildscape.builders_workbench.gradient_builder").getString();
        int tw = this.font.width(titleText);
        this.font.draw(ps, new net.minecraft.network.chat.TextComponent(titleText), px + 80 + (80 - tw) / 2.0f, py + 6 + 3.0f, 0xFF372211);

        // Slot backgrounds (shifted by -1,-1 to align with container slot item positions)
        for (int i = 0; i < 9; i++) {
            WbRenderer.drawSlotTexture(ps, WbRenderer.SLOT_INPUT, px + 8 + i * 18, py + 47); // Slots 1-9: Inputs
            WbRenderer.drawSlotTexture(ps, WbRenderer.SLOT_RESULT, px + 8 + i * 18, py + 77); // Slots 12-20: Outputs
        }
        WbRenderer.drawSlotTexture(ps, WbRenderer.SLOT_INPUT, px + 8, py + 111); // Slot 10: Input Pouch
        WbRenderer.drawSlotTexture(ps, WbRenderer.SLOT_OUTPUT, px + 152, py + 111); // Slot 11: Output Pouch

        // Filter Buttons (reused on both tabs for consistency!)
        for (int i = 0; i < 3; i++) {
            boolean hov = isIn(mouseX, mouseY, px + 187, py + 22 + i * 24, 18, 18);
            WbRenderer.drawFilterButton(ps, px + 187, py + 22 + i * 24, i, filterMask, hov);
        }

        // Synced copy progress
        float progress = (float) this.menu.getCopyProgress() / 40.0f;
        // Faint process arrow between pouches
        WbRenderer.drawProcessArrow(ps, px + 26, py + 112, 126, progress);

        // Action buttons
        boolean solveHov = isIn(mouseX, mouseY, px + 9, py + GRAD_BTN_Y, 72, GRAD_BTN_H);
        boolean copyHov = isIn(mouseX, mouseY, px + 93, py + GRAD_BTN_Y, 72, GRAD_BTN_H);

        WbRenderer.drawActionButton(ps, this.font, px + 9, py + GRAD_BTN_Y, 72, GRAD_BTN_H,
                new net.minecraft.network.chat.TranslatableComponent(
                        "screen.buildscape.builders_workbench.solve").getString(), solveHov, false, 0xFF00BBCC);
        WbRenderer.drawActionButton(ps, this.font, px + 93, py + GRAD_BTN_Y, 72, GRAD_BTN_H,
                new net.minecraft.network.chat.TranslatableComponent(
                        "screen.buildscape.builders_workbench.copy").getString(), copyHov, false, 0xFF00CC66);

        // Separated Vanilla Player Inventory Panel (aligns perfectly with slots at py + 145)
        WbRenderer.drawVanillaInventory(ps, this.font, px + 31, py + 145, 176, 96);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        float scale = 1.0f;
        if (this.width < this.imageWidth || this.height < this.imageHeight) {
            scale = Math.min((float) this.width / (float) this.imageWidth, (float) this.height / (float) this.imageHeight);
        }

        double finalMouseX = mouseX;
        double finalMouseY = mouseY;
        if (scale < 1.0f) {
            finalMouseX = (mouseX - this.width / 2.0f) / scale + this.width / 2.0f;
            finalMouseY = (mouseY - this.height / 2.0f) / scale + this.height / 2.0f;
        }

        int px = this.leftPos;
        int py = this.topPos;
        int mx = (int) finalMouseX, my = (int) finalMouseY;

        // ── Tab clicks ────────────────────────────────────────────────────────
        boolean clickTab0 = activeTab == 0 ? isIn(mx, my, px + 8, py - 8, 8, 8) : isIn(mx, my, px + 8, py - 12, 16, 16);
        if (clickTab0) {
            if (activeTab != 0) {
                activeTab = 0;
                if (this.minecraft != null && this.minecraft.gameMode != null) {
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
                }
                playClick();
            }
            return true;
        }
        boolean clickTab1 = activeTab == 0 ? isIn(mx, my, px + 18, py - 8, 8, 8) : isIn(mx, my, px + 26, py - 12, 16, 16);
        if (clickTab1) {
            if (activeTab != 1) {
                activeTab = 1;
                if (this.minecraft != null && this.minecraft.gameMode != null) {
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 1);
                }
                playClick();
            }
            return true;
        }

        // ── Result slot clicks (Tab 0: slots 1-9) ────────────────────────────
        if (activeTab == 0) {
            for (int i = 0; i < 9; i++) {
                int col = i % 3;
                int row = i / 3;
                int sx = px + 63 + col * 18;
                int sy = py + 24 + row * 18;
                if (!this.menu.getSlot(BuildersWorkbenchMenu.MENU_COLOR_RESULT_START + i).getItem().isEmpty()
                        && isIn(mx, my, sx + 12, sy + 12, 5, 5)) {
                    ModMessages.INSTANCE.sendToServer(
                            new BuildersWorkbenchActionPacket(3, this.menu.getBlockEntity().getBlockPos(), i)
                    );
                    playClick();
                    return true;
                }
            }
        }

        // ── Result slot clicks (Tab 1: slots 12-20) ──────────────────────────
        if (activeTab == 1) {
            for (int i = 1; i < 8; i++) { // only slots 1-7 (offsets index 1-7) can cycle
                int sx = px + 8 + i * 18;
                int sy = py + 77;
                if (!this.menu.getSlot(BuildersWorkbenchMenu.MENU_GRADIENT_OUTPUT_START + i).getItem().isEmpty()
                        && isIn(mx, my, sx + 12, sy + 12, 5, 5)) {
                    ModMessages.INSTANCE.sendToServer(
                            new BuildersWorkbenchActionPacket(3, this.menu.getBlockEntity().getBlockPos(), i)
                    );
                    playClick();
                    return true;
                }
            }
        }

        // ── Filter Button clicks ──────────────────────────────────────────────
        for (int i = 0; i < 3; i++) {
            if (activeTab == 0) {
                if (isIn(mx, my, px + 131, py + 22 + i * 22, 18, 18)) {
                    if (filterMask != i) {
                        filterMask = i;
                        ModMessages.INSTANCE.sendToServer(
                                new BuildersWorkbenchActionPacket(2, this.menu.getBlockEntity().getBlockPos(), i)
                        );
                        playClick();
                    }
                    return true;
                }
            } else {
                if (isIn(mx, my, px + 187, py + 22 + i * 24, 18, 18)) {
                    if (filterMask != i) {
                        filterMask = i;
                        ModMessages.INSTANCE.sendToServer(
                                new BuildersWorkbenchActionPacket(2, this.menu.getBlockEntity().getBlockPos(), i)
                        );
                        playClick();
                    }
                    return true;
                }
            }
        }

        // ── Action buttons (Gradient Builder only) ────────────────────────────
        if (activeTab == 1) {
            BuildersWorkbenchBlockEntity be = this.menu.getBlockEntity();
            // Solve Gradient
            if (isIn(mx, my, px + 9, py + GRAD_BTN_Y, 72, GRAD_BTN_H)) {
                ModMessages.INSTANCE.sendToServer(
                        new BuildersWorkbenchActionPacket(0, be.getBlockPos(), filterMask));
                animProgress = 0f;
                playClick();
                return true;
            }
            // Copy to Pouch
            if (isIn(mx, my, px + 93, py + GRAD_BTN_Y, 72, GRAD_BTN_H)) {
                ModMessages.INSTANCE.sendToServer(
                        new BuildersWorkbenchActionPacket(1, be.getBlockPos(), filterMask));
                animProgress = 0f;
                playClick();
                return true;
            }
        }

        return super.mouseClicked(finalMouseX, finalMouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        float scale = 1.0f;
        if (this.width < this.imageWidth || this.height < this.imageHeight) {
            scale = Math.min((float) this.width / (float) this.imageWidth, (float) this.height / (float) this.imageHeight);
        }
        if (scale < 1.0f) {
            double scaledMouseX = (mouseX - this.width / 2.0f) / scale + this.width / 2.0f;
            double scaledMouseY = (mouseY - this.height / 2.0f) / scale + this.height / 2.0f;
            return super.mouseReleased(scaledMouseX, scaledMouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        float scale = 1.0f;
        if (this.width < this.imageWidth || this.height < this.imageHeight) {
            scale = Math.min((float) this.width / (float) this.imageWidth, (float) this.height / (float) this.imageHeight);
        }
        if (scale < 1.0f) {
            double scaledMouseX = (mouseX - this.width / 2.0f) / scale + this.width / 2.0f;
            double scaledMouseY = (mouseY - this.height / 2.0f) / scale + this.height / 2.0f;
            return super.mouseDragged(scaledMouseX, scaledMouseY, button, dragX / scale, dragY / scale);
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    private boolean isIn(int mx, int my, int rx, int ry, int rw, int rh) {
        return mx >= rx && mx < rx + rw && my >= ry && my < ry + rh;
    }

    private void playClick() {
        if (this.minecraft != null && this.minecraft.player != null) {
            this.minecraft.player.playSound(SoundEvents.UI_BUTTON_CLICK, 0.3f, 1.1f);
        }
    }
}
