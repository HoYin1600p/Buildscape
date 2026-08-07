package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.block.BuildersWorkbenchBlockEntity;
import com.kingodogo.buildscape.client.screen.workbench.WbRenderer;
import com.kingodogo.buildscape.client.workbench.ClientBlockColorCatalog;
import com.kingodogo.buildscape.network.BuildersWorkbenchMenu;
import com.kingodogo.buildscape.network.BuildersWorkbenchResultsPacket;
import com.kingodogo.buildscape.network.ModMessages;
import com.kingodogo.buildscape.util.ColorGradientSolver;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BuildersWorkbenchScreen extends AbstractContainerScreen<BuildersWorkbenchMenu> {
    private static final int COLOR_WIDTH = 185;
    private static final int COLOR_HEIGHT = 193;
    private static final int GRADIENT_WIDTH = 216;
    private static final int GRADIENT_HEIGHT = 241;

    private int activeTab;
    private int filterMask;
    private final int[] resultOffsets = new int[9];
    private int lastInputSignature = Integer.MIN_VALUE;
    private int lastSentSignature = Integer.MIN_VALUE;

    public BuildersWorkbenchScreen(BuildersWorkbenchMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.titleLabelX = -9999;
        this.titleLabelY = -9999;
        this.inventoryLabelX = -9999;
        this.inventoryLabelY = -9999;
    }

    @Override
    protected void init() {
        this.activeTab = menu.getActiveTab();
        this.filterMask = menu.getFilterMask();
        for (int i = 0; i < resultOffsets.length; i++) resultOffsets[i] = menu.getResultOffset(i);
        updateDimensions();
        super.init();
        ClientBlockColorCatalog.ensureReady();
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        int syncedTab = menu.getActiveTab();
        if (syncedTab != activeTab) {
            activeTab = syncedTab;
            updateDimensions();
            lastInputSignature = Integer.MIN_VALUE;
            lastSentSignature = Integer.MIN_VALUE;
        }
        if (!ClientBlockColorCatalog.ensureReady()) return;

        int inputSignature = inputSignature();
        if (inputSignature != lastInputSignature) {
            Arrays.fill(resultOffsets, 0);
            lastInputSignature = inputSignature;
            lastSentSignature = Integer.MIN_VALUE;
        }
        int solveSignature = 31 * inputSignature + Arrays.hashCode(resultOffsets);
        solveSignature = 31 * solveSignature + ClientBlockColorCatalog.generation();
        if (solveSignature != lastSentSignature) sendSolvedResults(solveSignature);
    }

    private void updateDimensions() {
        imageWidth = activeTab == 0 ? COLOR_WIDTH : GRADIENT_WIDTH;
        imageHeight = activeTab == 0 ? COLOR_HEIGHT : GRADIENT_HEIGHT;
        leftPos = (width - imageWidth) / 2;
        topPos = (height - imageHeight) / 2;
    }

    private int inputSignature() {
        BuildersWorkbenchBlockEntity workbench = menu.getBlockEntity();
        int hash = 31 + activeTab;
        hash = 31 * hash + filterMask;
        if (activeTab == 0) {
            hash = stackHash(hash, workbench.getItem(BuildersWorkbenchBlockEntity.SLOT_COLOR_PICKER));
        } else {
            for (int i = 0; i < 9; i++) {
                hash = stackHash(hash, workbench.getItem(BuildersWorkbenchBlockEntity.SLOT_GRADIENT_INPUT_START + i));
            }
        }
        return hash;
    }

    private static int stackHash(int hash, ItemStack stack) {
        return 31 * hash + (stack == null || stack.isEmpty() ? 0 : net.minecraft.core.Registry.ITEM.getId(stack.getItem()) + 1);
    }

    private void sendSolvedResults(int solveSignature) {
        BuildersWorkbenchBlockEntity workbench = menu.getBlockEntity();
        List<ItemStack> solved;
        if (activeTab == 0) {
            solved = ColorGradientSolver.solveColorPicker(
                    workbench.getItem(BuildersWorkbenchBlockEntity.SLOT_COLOR_PICKER), filterMask, resultOffsets);
        } else {
            List<ItemStack> anchors = new ArrayList<>(9);
            for (int i = 0; i < 9; i++) {
                anchors.add(workbench.getItem(BuildersWorkbenchBlockEntity.SLOT_GRADIENT_INPUT_START + i));
            }
            solved = ColorGradientSolver.solveGradient(anchors, filterMask, resultOffsets);
        }
        ModMessages.INSTANCE.sendToServer(new BuildersWorkbenchResultsPacket(
                workbench.getBlockPos(), activeTab, filterMask, resultOffsets, solved));
        lastSentSignature = solveSignature;
    }

    private void solveNow() {
        lastInputSignature = inputSignature();
        int signature = 31 * lastInputSignature + Arrays.hashCode(resultOffsets);
        signature = 31 * signature + ClientBlockColorCatalog.generation();
        sendSolvedResults(signature);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        updateDimensions();
        renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTick);
        renderRerollControls(poseStack, mouseX, mouseY);
        renderTooltip(poseStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;
        RenderSystem.disableDepthTest();

        boolean colorHover = isIn(mouseX, mouseY, x + 8, y - 12, 16, 16);
        boolean gradientHover = isIn(mouseX, mouseY, x + 26, y - 12, 16, 16);
        WbRenderer.drawTabButton(poseStack, x + 8, y - 12, activeTab == 0, colorHover,
                WbRenderer.ICON_COLOR_BUILDER, 16, 16);
        WbRenderer.drawTabButton(poseStack, x + 26, y - 12, activeTab == 1, gradientHover,
                WbRenderer.ICON_GRADIENT_BUILDER, 18, 18);

        if (activeTab == 0) renderColorBuilder(poseStack, x, y, mouseX, mouseY);
        else renderGradientBuilder(poseStack, x, y, mouseX, mouseY);
        RenderSystem.enableDepthTest();
    }

    private void renderColorBuilder(PoseStack poseStack, int x, int y, int mouseX, int mouseY) {
        WbRenderer.drawColorBuilderBG(poseStack, x, y);
        WbRenderer.drawSlotTexture(poseStack, WbRenderer.SLOT_INPUT, x + 27, y + 37);
        for (int i = 0; i < 9; i++) {
            int column = i % 3;
            int row = i / 3;
            WbRenderer.drawSlotTexture(poseStack, WbRenderer.SLOT_BLOCK, x + 62 + column * 18, y + 23 + row * 18);
        }
        drawFilters(poseStack, x + 131, y + 22, 22, mouseX, mouseY);
        drawTitle(poseStack, x + 58, y + 1, 67, "screen.buildscape.builders_workbench.color_builder");
        WbRenderer.drawSlotTexture(poseStack, WbRenderer.SLOT_INPUT, x + 46, y + 92);
        WbRenderer.drawSlotTexture(poseStack, WbRenderer.SLOT_OUTPUT, x + 112, y + 92);
        drawCopyArrow(poseStack, x + 64, y + 93);
    }

    private void renderGradientBuilder(PoseStack poseStack, int x, int y, int mouseX, int mouseY) {
        WbRenderer.drawWorkstationBG(poseStack, x, y, imageWidth, 138);
        WbRenderer.drawInsetBox(poseStack, x + 3, y + 42, 174, 30);
        WbRenderer.drawInsetBox(poseStack, x + 3, y + 72, 174, 30);
        WbRenderer.drawInsetBox(poseStack, x + 3, y + 106, 30, 30);
        WbRenderer.drawInsetBox(poseStack, x + 147, y + 106, 30, 30);
        WbRenderer.drawInsetBox(poseStack, x + 180, y + 16, 32, 80);
        WbRenderer.drawTitleBanner(poseStack, x + 68, y + 6, 80, 14);
        drawTitle(poseStack, x + 68, y + 6, 80, "screen.buildscape.builders_workbench.gradient_builder");

        for (int i = 0; i < 9; i++) {
            WbRenderer.drawSlotTexture(poseStack, WbRenderer.SLOT_INPUT, x + 8 + i * 18, y + 47);
            WbRenderer.drawSlotTexture(poseStack, WbRenderer.SLOT_BLOCK, x + 8 + i * 18, y + 77);
        }
        WbRenderer.drawSlotTexture(poseStack, WbRenderer.SLOT_INPUT, x + 8, y + 111);
        WbRenderer.drawSlotTexture(poseStack, WbRenderer.SLOT_OUTPUT, x + 152, y + 111);
        drawFilters(poseStack, x + 187, y + 22, 24, mouseX, mouseY);
        WbRenderer.drawProcessArrow(poseStack, x + 26, y + 112, 126, menu.getCopyProgress() / 40.0f);
        WbRenderer.drawVanillaInventory(poseStack, font, x + 20, y + 145, 176, 96);
    }

    private void drawTitle(PoseStack poseStack, int x, int y, int width, String key) {
        String title = new TranslatableComponent(key).getString();
        font.draw(poseStack, title, x + (width - font.width(title)) / 2.0f, y + 3, 0xFF372211);
    }

    private void drawFilters(PoseStack poseStack, int x, int y, int spacing, int mouseX, int mouseY) {
        for (int i = 0; i < 3; i++) {
            boolean hovered = isIn(mouseX, mouseY, x, y + i * spacing, 18, 18);
            WbRenderer.drawFilterButton(poseStack, x, y + i * spacing, i, filterMask, hovered);
        }
    }

    private void drawCopyArrow(PoseStack poseStack, int x, int y) {
        RenderSystem.setShaderTexture(0, WbRenderer.BUILDERS_ARROW);
        WbRenderer.blitFloat(poseStack, x, y, 48, 16, 0, 0, 1, 1);
        float progress = Math.max(0, Math.min(1, menu.getCopyProgress() / 40.0f));
        if (progress > 0) {
            RenderSystem.setShaderTexture(0, WbRenderer.BUILDERS_ARROW_ACTIVE);
            WbRenderer.blitFloat(poseStack, x, y, (int) (48 * progress), 16, 0, 0, progress, 1);
        }
    }

    private void renderRerollControls(PoseStack poseStack, int mouseX, int mouseY) {
        if (activeTab == 0) {
            for (int i = 0; i < 9; i++) drawRerollControl(poseStack, mouseX, mouseY,
                    leftPos + 63 + i % 3 * 18, topPos + 24 + i / 3 * 18,
                    BuildersWorkbenchMenu.MENU_COLOR_RESULT_START + i);
        } else {
            for (int i = 0; i < 9; i++) {
                if (isGradientAnchor(i)) continue;
                drawRerollControl(poseStack, mouseX, mouseY,
                        leftPos + 9 + i * 18, topPos + 78,
                        BuildersWorkbenchMenu.MENU_GRADIENT_OUTPUT_START + i);
            }
        }
    }

    private void drawRerollControl(PoseStack poseStack, int mouseX, int mouseY, int slotX, int slotY, int menuSlot) {
        if (menuSlot < 0 || menuSlot >= menu.slots.size() || menu.getSlot(menuSlot).getItem().isEmpty()) return;
        boolean hovered = isIn(mouseX, mouseY, slotX + 12, slotY + 12, 5, 5);
        fill(poseStack, slotX + 12, slotY + 12, slotX + 17, slotY + 17,
                hovered ? 0xFF00FFCC : 0xFF00AA88);
        fill(poseStack, slotX + 14, slotY + 14, slotX + 15, slotY + 15, 0xFF000000);
    }

    @Override
    protected void renderTooltip(PoseStack poseStack, int mouseX, int mouseY) {
        if (isIn(mouseX, mouseY, leftPos + 8, topPos - 12, 16, 16)) {
            renderComponentTooltip(poseStack, List.of(new TranslatableComponent(
                    "screen.buildscape.builders_workbench.color_builder")), mouseX, mouseY);
            return;
        }
        if (isIn(mouseX, mouseY, leftPos + 26, topPos - 12, 16, 16)) {
            renderComponentTooltip(poseStack, List.of(new TranslatableComponent(
                    "screen.buildscape.builders_workbench.gradient_builder")), mouseX, mouseY);
            return;
        }
        int filter = hoveredFilter(mouseX, mouseY);
        if (filter >= 0) {
            String key = switch (filter) {
                case 0 -> "screen.buildscape.builders_workbench.filter.solid";
                case 1 -> "screen.buildscape.builders_workbench.filter.transparent";
                default -> "screen.buildscape.builders_workbench.filter.non_full";
            };
            renderComponentTooltip(poseStack, List.of(new TranslatableComponent(key)), mouseX, mouseY);
            return;
        }
        int result = hoveredReroll(mouseX, mouseY);
        if (result >= 0) {
            renderComponentTooltip(poseStack, List.of(new TranslatableComponent(
                    "screen.buildscape.builders_workbench.cycle_hint")), mouseX, mouseY);
            return;
        }
        super.renderTooltip(poseStack, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = leftPos;
        int y = topPos;
        if (isIn(mouseX, mouseY, x + 8, y - 12, 16, 16)) {
            switchTab(0);
            return true;
        }
        if (isIn(mouseX, mouseY, x + 26, y - 12, 16, 16)) {
            switchTab(1);
            return true;
        }

        int filter = hoveredFilter(mouseX, mouseY);
        if (filter >= 0 && (button == 0 || button == 1)) {
            filterMask ^= 1 << filter;
            Arrays.fill(resultOffsets, 0);
            lastInputSignature = inputSignature();
            solveNow();
            playClick();
            return true;
        }

        int result = hoveredReroll(mouseX, mouseY);
        if (result >= 0 && (button == 0 || button == 1)) {
            if (button == 1) resultOffsets[result]++;
            else resultOffsets[result] = Math.max(0, resultOffsets[result] - 1);
            solveNow();
            playClick();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void switchTab(int tab) {
        if (tab == activeTab) return;
        activeTab = tab;
        Arrays.fill(resultOffsets, 0);
        lastInputSignature = Integer.MIN_VALUE;
        lastSentSignature = Integer.MIN_VALUE;
        updateDimensions();
        if (minecraft != null && minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, tab);
        }
        playClick();
    }

    private int hoveredFilter(double mouseX, double mouseY) {
        int x = activeTab == 0 ? leftPos + 131 : leftPos + 187;
        int y = topPos + 22;
        int spacing = activeTab == 0 ? 22 : 24;
        for (int i = 0; i < 3; i++) {
            if (isIn(mouseX, mouseY, x, y + i * spacing, 18, 18)) return i;
        }
        return -1;
    }

    private int hoveredReroll(double mouseX, double mouseY) {
        if (activeTab == 0) {
            for (int i = 0; i < 9; i++) {
                int slotX = leftPos + 63 + i % 3 * 18;
                int slotY = topPos + 24 + i / 3 * 18;
                if (!menu.getSlot(BuildersWorkbenchMenu.MENU_COLOR_RESULT_START + i).getItem().isEmpty()
                        && isIn(mouseX, mouseY, slotX + 12, slotY + 12, 5, 5)) return i;
            }
        } else {
            for (int i = 0; i < 9; i++) {
                int slotX = leftPos + 9 + i * 18;
                int slotY = topPos + 78;
                if (!isGradientAnchor(i)
                        && !menu.getSlot(BuildersWorkbenchMenu.MENU_GRADIENT_OUTPUT_START + i).getItem().isEmpty()
                        && isIn(mouseX, mouseY, slotX + 12, slotY + 12, 5, 5)) return i;
            }
        }
        return -1;
    }

    private boolean isGradientAnchor(int slot) {
        return !menu.getBlockEntity().getItem(BuildersWorkbenchBlockEntity.SLOT_GRADIENT_INPUT_START + slot).isEmpty();
    }

    private static boolean isIn(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    private void playClick() {
        if (minecraft != null && minecraft.player != null) {
            minecraft.player.playSound(SoundEvents.UI_BUTTON_CLICK, 0.3f, 1.1f);
        }
    }
}
