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
        super.init();
        openTime = System.currentTimeMillis();
        this.activeTab = this.menu.getActiveTab();
        this.filterMask = this.menu.getFilterMask();
    }

    @Override
    public void render(PoseStack ps, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(ps);
        super.render(ps, mouseX, mouseY, partialTick);

        // Render Cycle Buttons on top of the slot items
        int px = this.leftPos;
        int py = this.topPos;
        if (activeTab == 0) {
            for (int i = 0; i < 9; i++) {
                int col = i % 3;
                int row = i / 3;
                int sx = px + 85 + col * 20;
                int sy = py + 21 + row * 20;
                if (!this.menu.getSlot(1 + i).getItem().isEmpty()) {
                    boolean hovered = isIn(mouseX, mouseY, sx + 12, sy + 12, 5, 5);
                    int color = hovered ? 0xFF00FFCC : 0xFF00AA88;
                    fill(ps, sx + 12, sy + 12, sx + 17, sy + 17, color);
                    fill(ps, sx + 14, sy + 14, sx + 15, sy + 15, 0xFF000000);
                }
            }
        } else if (activeTab == 1) {
            for (int i = 1; i < 8; i++) { // only slots 1-7 are result slots
                int sx = px + 8 + i * 18;
                int sy = py + 77;
                if (!this.menu.getSlot(12 + i).getItem().isEmpty()) {
                    boolean hovered = isIn(mouseX, mouseY, sx + 12, sy + 12, 5, 5);
                    int color = hovered ? 0xFF00FFCC : 0xFF00AA88;
                    fill(ps, sx + 12, sy + 12, sx + 17, sy + 17, color);
                    fill(ps, sx + 14, sy + 14, sx + 15, sy + 15, 0xFF000000);
                }
            }
        }

        this.renderTooltip(ps, mouseX, mouseY);

        // Render tab button tooltips
        if (isIn(mouseX, mouseY, px + 98, py + TAB_Y, 20, 12)) {
            this.renderComponentTooltip(ps, java.util.List.of(new net.minecraft.network.chat.TextComponent("Color Picker")), mouseX, mouseY);
        } else if (isIn(mouseX, mouseY, px + 122, py + TAB_Y, 20, 12)) {
            this.renderComponentTooltip(ps, java.util.List.of(new net.minecraft.network.chat.TextComponent("Gradient Builder")), mouseX, mouseY);
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
                int sx = px + 85 + col * 20;
                int sy = py + 21 + row * 20;
                if (!this.menu.getSlot(1 + i).getItem().isEmpty() && isIn(x, y, sx + 12, sy + 12, 5, 5)) {
                    this.renderComponentTooltip(ps, java.util.List.of(new net.minecraft.network.chat.TextComponent("Cycle Block (Reroll)")), x, y);
                    return;
                }
            }
        } else if (activeTab == 1) {
            for (int i = 1; i < 8; i++) {
                int sx = px + 8 + i * 18;
                int sy = py + 77;
                if (!this.menu.getSlot(12 + i).getItem().isEmpty() && isIn(x, y, sx + 12, sy + 12, 5, 5)) {
                    this.renderComponentTooltip(ps, java.util.List.of(new net.minecraft.network.chat.TextComponent("Cycle Block (Reroll)")), x, y);
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
                    tooltip.add(new net.minecraft.network.chat.TextComponent("§bClick corner button to cycle/reroll"));
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
        boolean cpTabHov = isIn(mouseX, mouseY, px + 98, py + TAB_Y, 20, 12);
        WbRenderer.drawTab(ps, this.font, px + 98, py + TAB_Y, 20, 12, "C", activeTab == 0, cpTabHov);

        boolean gbTabHov = isIn(mouseX, mouseY, px + 122, py + TAB_Y, 20, 12);
        WbRenderer.drawTab(ps, this.font, px + 122, py + TAB_Y, 20, 12, "G", activeTab == 1, gbTabHov);

        // ── 2. Workstation and Slots Layout ──────────────────────────────────────
        if (activeTab == 0) {
            renderColorPickerTab(ps, px, py, mouseX, mouseY, tick);
        } else {
            renderGradientBuilderTab(ps, px, py, mouseX, mouseY, tick);
        }

        RenderSystem.enableDepthTest();
    }

    @Override
    protected void renderLabels(PoseStack ps, int mouseX, int mouseY) {
        // Labels are drawn in screen-relative space in renderBg
    }

    private void renderColorPickerTab(PoseStack ps, int px, int py,
                                      int mouseX, int mouseY, float tick) {
        // Workstation background (ears shape) - ends at py + 148 on sides
        WbRenderer.drawWorkstationBG(ps, px, py, W, 148);

        // Pouch Transfer Box (rounded overlay at bottom of workstation panel) - goes to py + 154
        WbRenderer.drawInsetBox(ps, px + 10, py + 110, 220, 44);

        // Inset boxes
        WbRenderer.drawInsetBox(ps, px + 20, py + 35, 32, 32);
        WbRenderer.drawInsetBox(ps, px + 80, py + 16, 72, 72);
        WbRenderer.drawInsetBox(ps, px + 180, py + 16, 32, 80);

        // Slot backgrounds
        WbRenderer.drawSlotTexture(ps, WbRenderer.SLOT_INPUT, px + 26, py + 41); // Slot 0: Pipette
        for (int i = 0; i < 9; i++) {
            int col = i % 3;
            int row = i / 3;
            WbRenderer.drawSlotTexture(ps, WbRenderer.SLOT_RESULT, px + 85 + col * 20, py + 21 + row * 20); // Slots 1-9: Presets
        }

        // Filter Buttons
        for (int i = 0; i < 3; i++) {
            boolean hov = isIn(mouseX, mouseY, px + 187, py + 22 + i * 24, 18, 18);
            WbRenderer.drawFilterButton(ps, px + 187, py + 22 + i * 24, i, filterMask, hov);
        }

        // Pouch slots backgrounds inside bottom overlay (perfectly centered vertically)
        WbRenderer.drawInsetBox(ps, px + 40, py + 117, 30, 30);
        WbRenderer.drawInsetBox(ps, px + 168, py + 117, 30, 30);

        WbRenderer.drawSlotTexture(ps, WbRenderer.SLOT_INPUT, px + 46, py + 123); // Slot 10: Input Pouch
        WbRenderer.drawSlotTexture(ps, WbRenderer.SLOT_OUTPUT, px + 174, py + 123); // Slot 11: Output Pouch

        // Synced copy progress
        float progress = (float) this.menu.getCopyProgress() / 40.0f;
        // Faint process arrow between pouches
        WbRenderer.drawProcessArrow(ps, px + 76, py + 124, 88, progress);

        // Separated Vanilla Player Inventory Panel (aligns perfectly with slots at py + 161)
        WbRenderer.drawVanillaInventory(ps, this.font, px + 31, py + 161, 176, 96);
    }

    private void renderGradientBuilderTab(PoseStack ps, int px, int py,
                                          int mouseX, int mouseY, float tick) {
        // Workstation background (ears shape) - extended to 138 to wrap slots fully at bottom
        WbRenderer.drawWorkstationBG(ps, px, py, W, 138);

        // Inset boxes
        WbRenderer.drawInsetBox(ps, px + 3, py + 42, 174, 30);
        WbRenderer.drawInsetBox(ps, px + 3, py + 72, 174, 30);
        WbRenderer.drawInsetBox(ps, px + 3, py + 106, 30, 30);
        WbRenderer.drawInsetBox(ps, px + 147, py + 106, 30, 30);
        WbRenderer.drawInsetBox(ps, px + 180, py + 16, 32, 80); // Filter box on right

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
                "Solve Gradient", solveHov, false, 0xFF00BBCC);
        WbRenderer.drawActionButton(ps, this.font, px + 93, py + GRAD_BTN_Y, 72, GRAD_BTN_H,
                "Copy to Pouch", copyHov, false, 0xFF00CC66);

        // Separated Vanilla Player Inventory Panel (aligns perfectly with slots at py + 145)
        WbRenderer.drawVanillaInventory(ps, this.font, px + 31, py + 145, 176, 96);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int px = this.leftPos;
        int py = this.topPos;
        int mx = (int) mouseX, my = (int) mouseY;

        // ── Tab clicks ────────────────────────────────────────────────────────
        if (isIn(mx, my, px + 98, py + TAB_Y, 20, 12)) {
            if (activeTab != 0) {
                activeTab = 0;
                if (this.minecraft != null && this.minecraft.gameMode != null) {
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
                }
                playClick();
            }
            return true;
        }
        if (isIn(mx, my, px + 122, py + TAB_Y, 20, 12)) {
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
                int sx = px + 85 + col * 20;
                int sy = py + 21 + row * 20;
                if (!this.menu.getSlot(1 + i).getItem().isEmpty() && isIn(mx, my, sx + 12, sy + 12, 5, 5)) {
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
                if (!this.menu.getSlot(12 + i).getItem().isEmpty() && isIn(mx, my, sx + 12, sy + 12, 5, 5)) {
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

        return super.mouseClicked(mouseX, mouseY, button);
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
