package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.block.BuildersWorkbenchBlockEntity;
import com.kingodogo.buildscape.client.screen.workbench.WbRenderer;
import com.kingodogo.buildscape.client.workbench.ClientBlockColorCatalog;
import com.kingodogo.buildscape.menu.BuildersWorkbenchMenu;
import com.kingodogo.buildscape.network.BuildersWorkbenchResultsPacket;
import com.kingodogo.buildscape.network.PacketFactory;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.ColorGradientSolver;
import com.kingodogo.buildscape.util.ComponentHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BuildersWorkbenchScreen {
    public static final int COLOR_WIDTH = 184;
    public static final int GRADIENT_WIDTH = 206;
    public static final int GUI_HEIGHT = 193;

    private static final int TAB_SIZE = 17;
    private static final int TAB_Y = -11;
    private static final int TAB_COLOR_X = 6;
    private static final int TAB_GRADIENT_X = 26;

    private static final int TITLE_X = 54;
    private static final int TITLE_Y = 2;
    private static final int TITLE_W = 76;
    private static final int TITLE_H = 13;

    private static final int FILTER_Y = 23;
    private static final int FILTER_SPACING = 18;
    private static final int COLOR_FILTER_X = 141;
    private static final int GRADIENT_FILTER_X = 184;

    private static final int MODIFIER_Y = 84;
    private static final int SINGLE_TEXTURE_X = 13;
    private static final int MATCH_SHAPE_X = 27;

    private static final int ARROW_X = 68;
    private static final int ARROW_Y = 84;

    private static final int COLOR_RESULT_X = 66;
    private static final int COLOR_RESULT_Y = 24;
    private static final int GRADIENT_OUTPUT_X = 12;
    private static final int GRADIENT_OUTPUT_Y = 55;
    private static final int INITIAL_DATA_SYNC_TICKS = 3;
    private static final float REROLL_Z = 300.0f;

    private final BuildersWorkbenchMenu menu;
    private final Inventory inventory;
    private final Component title;

    private int imageWidth;
    private int imageHeight;
    private int leftPos;
    private int topPos;
    private int width;
    private int height;

    private int activeTab;
    private int filterMask;
    private final int[][] resultOffsetsByTab = new int[2][9];
    private int lastInputSignature = Integer.MIN_VALUE;
    private int lastSentSignature = Integer.MIN_VALUE;
    private int initialDataSyncTicks;

    public BuildersWorkbenchScreen(BuildersWorkbenchMenu menu, Inventory inventory, Component title) {
        this.menu = menu;
        this.inventory = inventory;
        this.title = title;
        this.imageWidth = COLOR_WIDTH;
        this.imageHeight = GUI_HEIGHT;
    }

    public BuildersWorkbenchMenu getMenu() {
        return menu;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Component getTitle() {
        return title;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void init(int leftPos, int topPos, int screenWidth, int screenHeight) {
        this.leftPos = leftPos;
        this.topPos = topPos;
        this.width = screenWidth;
        this.height = screenHeight;
        this.activeTab = menu.getActiveTab();
        this.filterMask = menu.getFilterMask();
        for (int tab = 0; tab < resultOffsetsByTab.length; tab++) {
            for (int i = 0; i < resultOffsetsByTab[tab].length; i++) {
                resultOffsetsByTab[tab][i] = menu.getResultOffset(tab, i);
            }
        }
        updateDimensions();
        ClientBlockColorCatalog.ensureReady();
        lastInputSignature = inputSignature();
        lastSentSignature = Integer.MIN_VALUE;
        initialDataSyncTicks = INITIAL_DATA_SYNC_TICKS;
    }

    public void containerTick() {
        if (initialDataSyncTicks > 0) {
            syncInitialMenuState();
            if (!ClientBlockColorCatalog.ensureReady()) return;
            initialDataSyncTicks--;
            if (initialDataSyncTicks == 0) {
                lastInputSignature = inputSignature();
                lastSentSignature = solveSignature(lastInputSignature);
            }
            return;
        }

        int syncedTab = menu.getActiveTab();
        if (syncedTab != activeTab) {
            activeTab = syncedTab;
            updateDimensions();
            lastInputSignature = inputSignature();
            lastSentSignature = Integer.MIN_VALUE;
        }
        if (!ClientBlockColorCatalog.ensureReady()) return;

        int inputSignature = inputSignature();
        if (inputSignature != lastInputSignature) {
            Arrays.fill(currentResultOffsets(), 0);
            lastInputSignature = inputSignature;
            lastSentSignature = Integer.MIN_VALUE;
        }
        int solveSignature = solveSignature(inputSignature);
        if (solveSignature != lastSentSignature) sendSolvedResults(solveSignature);
    }

    private void syncInitialMenuState() {
        activeTab = menu.getActiveTab() == 1 ? 1 : 0;
        filterMask = menu.getFilterMask();
        for (int tab = 0; tab < resultOffsetsByTab.length; tab++) {
            for (int i = 0; i < resultOffsetsByTab[tab].length; i++) {
                resultOffsetsByTab[tab][i] = menu.getResultOffset(tab, i);
            }
        }
        updateDimensions();
    }

    private int solveSignature(int inputSignature) {
        int signature = 31 * inputSignature + Arrays.hashCode(currentResultOffsets());
        return 31 * signature + ClientBlockColorCatalog.generation();
    }

    private void updateDimensions() {
        imageWidth = activeTab == 0 ? COLOR_WIDTH : GRADIENT_WIDTH;
        imageHeight = GUI_HEIGHT;
        if (width > 0 && height > 0) {
            leftPos = (width - COLOR_WIDTH) / 2;
            topPos = (height - imageHeight) / 2;
        }
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
        return 31 * hash + (stack == null || stack.isEmpty() ? 0 : Services.PLATFORM.getItemRawId(stack.getItem()) + 1);
    }

    private void sendSolvedResults(int solveSignature) {
        BuildersWorkbenchBlockEntity workbench = menu.getBlockEntity();
        List<ItemStack> solved;
        if (activeTab == 0) {
            solved = ColorGradientSolver.solveColorPicker(
                    workbench.getItem(BuildersWorkbenchBlockEntity.SLOT_COLOR_PICKER), filterMask,
                    currentResultOffsets());
        } else {
            List<ItemStack> anchors = new ArrayList<>(9);
            for (int i = 0; i < 9; i++) {
                anchors.add(workbench.getItem(BuildersWorkbenchBlockEntity.SLOT_GRADIENT_INPUT_START + i));
            }
            solved = ColorGradientSolver.solveGradient(anchors, filterMask, currentResultOffsets());
        }
        PacketFactory.sendToServer(new BuildersWorkbenchResultsPacket(
                workbench.getBlockPos(), activeTab, filterMask, currentResultOffsets(), solved));
        lastSentSignature = solveSignature;
    }

    private void solveNow() {
        lastInputSignature = inputSignature();
        sendSolvedResults(solveSignature(lastInputSignature));
    }

    public void renderBg(Object poseStackOrGraphics, float partialTick, int mouseX, int mouseY, int leftPos, int topPos) {
        this.leftPos = leftPos;
        this.topPos = topPos;
        int x = leftPos;
        int y = topPos;
        Services.PLATFORM.beginGuiOverlayRender();

        WbRenderer.drawBuilderBG(poseStackOrGraphics,
                activeTab == 0 ? WbRenderer.BG_COLOR_BUILDER : WbRenderer.BG_GRADIENT_BUILDER,
                x, y, imageWidth, imageHeight);

        drawTabs(poseStackOrGraphics, x, y, mouseX, mouseY);
        drawTitle(poseStackOrGraphics, x, y);
        drawFilters(poseStackOrGraphics, x + filterX(), y + FILTER_Y, mouseX, mouseY);
        drawModifierFilters(poseStackOrGraphics, x, y, mouseX, mouseY);
        drawCopyArrow(poseStackOrGraphics, x + ARROW_X, y + ARROW_Y);

        Services.PLATFORM.endGuiOverlayRender();
    }

    private void drawTabs(Object poseStackOrGraphics, int x, int y, int mouseX, int mouseY) {
        boolean colorHover = isIn(mouseX, mouseY, x + TAB_COLOR_X, y + TAB_Y, TAB_SIZE, TAB_SIZE);
        boolean gradientHover = isIn(mouseX, mouseY, x + TAB_GRADIENT_X, y + TAB_Y, TAB_SIZE, TAB_SIZE);
        WbRenderer.drawTabButton(poseStackOrGraphics, x + TAB_COLOR_X, y + TAB_Y, activeTab == 0, colorHover,
                WbRenderer.TAB_COLOR, WbRenderer.TAB_COLOR_HOVER, WbRenderer.TAB_COLOR_SEL);
        WbRenderer.drawTabButton(poseStackOrGraphics, x + TAB_GRADIENT_X, y + TAB_Y, activeTab == 1, gradientHover,
                WbRenderer.TAB_GRADIENT, WbRenderer.TAB_GRADIENT_HOVER, WbRenderer.TAB_GRADIENT_SEL);
    }

    private int filterX() {
        return activeTab == 0 ? COLOR_FILTER_X : GRADIENT_FILTER_X;
    }

    private void drawTitle(Object poseStackOrGraphics, int x, int y) {
        WbRenderer.drawTitleImage(poseStackOrGraphics,
                activeTab == 0 ? WbRenderer.TITLE_COLOR : WbRenderer.TITLE_GRADIENT,
                activeTab == 0 ? WbRenderer.TITLE_INK_COLOR : WbRenderer.TITLE_INK_GRADIENT,
                x + TITLE_X, y + TITLE_Y, TITLE_W, TITLE_H);
    }

    private void drawFilters(Object poseStackOrGraphics, int x, int y, int mouseX, int mouseY) {
        for (int i = 0; i < 3; i++) {
            boolean hovered = isIn(mouseX, mouseY, x, y + i * FILTER_SPACING, 18, 18);
            WbRenderer.drawFilterButton(poseStackOrGraphics, x, y + i * FILTER_SPACING, i, filterMask, hovered);
        }
    }

    private void drawModifierFilters(Object poseStackOrGraphics, int x, int y, int mouseX, int mouseY) {
        boolean singleHover = isIn(mouseX, mouseY, x + SINGLE_TEXTURE_X, y + MODIFIER_Y,
                WbRenderer.MODIFIER_SIZE, WbRenderer.MODIFIER_SIZE);
        WbRenderer.drawModifierButton(poseStackOrGraphics, x + SINGLE_TEXTURE_X, y + MODIFIER_Y,
                (filterMask & ColorGradientSolver.FILTER_SINGLE_TEXTURE) != 0, singleHover,
                WbRenderer.BTN_SINGLE_TEXTURE, WbRenderer.BTN_SINGLE_TEXTURE_HOVER,
                WbRenderer.BTN_SINGLE_TEXTURE_SEL);

        boolean shapeHover = isIn(mouseX, mouseY, x + MATCH_SHAPE_X, y + MODIFIER_Y,
                WbRenderer.MODIFIER_SIZE, WbRenderer.MODIFIER_SIZE);
        WbRenderer.drawModifierButton(poseStackOrGraphics, x + MATCH_SHAPE_X, y + MODIFIER_Y,
                (filterMask & ColorGradientSolver.FILTER_MATCH_SHAPE) != 0, shapeHover,
                WbRenderer.BTN_MATCH_SHAPE, WbRenderer.BTN_MATCH_SHAPE_HOVER,
                WbRenderer.BTN_MATCH_SHAPE_SEL);
    }

    private void drawCopyArrow(Object poseStackOrGraphics, int x, int y) {
        int copyProgress = menu.getCopyProgress();
        WbRenderer.drawCopyArrow(poseStackOrGraphics, x, y, copyProgress / 40.0f, copyProgress < 0);
    }

    public void renderRerollControls(Object poseStackOrGraphics, int mouseX, int mouseY, int leftPos, int topPos) {
        this.leftPos = leftPos;
        this.topPos = topPos;
        Services.PLATFORM.pushGuiPose(poseStackOrGraphics);
        Services.PLATFORM.translateGuiPose(poseStackOrGraphics, 0.0f, 0.0f, REROLL_Z);

        if (activeTab == 0) {
            for (int i = 0; i < 9; i++) drawRerollControl(poseStackOrGraphics, mouseX, mouseY,
                    leftPos + COLOR_RESULT_X + i % 3 * 18, topPos + COLOR_RESULT_Y + i / 3 * 18,
                    BuildersWorkbenchMenu.MENU_COLOR_RESULT_START + i);
        } else {
            for (int i = 0; i < 9; i++) {
                if (isGradientAnchor(i)) continue;
                drawRerollControl(poseStackOrGraphics, mouseX, mouseY,
                        leftPos + GRADIENT_OUTPUT_X + i * 18, topPos + GRADIENT_OUTPUT_Y,
                        BuildersWorkbenchMenu.MENU_GRADIENT_OUTPUT_START + i);
            }
        }

        Services.PLATFORM.popGuiPose(poseStackOrGraphics);
    }

    private void drawRerollControl(Object poseStackOrGraphics, int mouseX, int mouseY, int slotX, int slotY, int menuSlot) {
        if (menuSlot < 0 || menuSlot >= menu.slots.size() || menu.getSlot(menuSlot).getItem().isEmpty()) return;
        boolean hovered = isIn(mouseX, mouseY, slotX + 11, slotY + 11, 5, 5);
        Services.PLATFORM.fill(poseStackOrGraphics, slotX + 11, slotY + 11, slotX + 16, slotY + 16,
                hovered ? 0xFF00FFCC : 0xFF00AA88);
        Services.PLATFORM.fill(poseStackOrGraphics, slotX + 13, slotY + 13, slotX + 14, slotY + 14, 0xFF000000);
    }

    public boolean renderCustomTooltip(Object poseStackOrGraphics, int mouseX, int mouseY, int leftPos, int topPos) {
        this.leftPos = leftPos;
        this.topPos = topPos;
        Minecraft mc = Minecraft.getInstance();
        if (isIn(mouseX, mouseY, leftPos + TAB_COLOR_X, topPos + TAB_Y, TAB_SIZE, TAB_SIZE)) {
            Services.PLATFORM.renderComponentTooltip(poseStackOrGraphics, mc.font, List.of(ComponentHelper.translatable(
                    "screen.buildscape.builders_workbench.color_builder")), mouseX, mouseY);
            return true;
        }
        if (isIn(mouseX, mouseY, leftPos + TAB_GRADIENT_X, topPos + TAB_Y, TAB_SIZE, TAB_SIZE)) {
            Services.PLATFORM.renderComponentTooltip(poseStackOrGraphics, mc.font, List.of(ComponentHelper.translatable(
                    "screen.buildscape.builders_workbench.gradient_builder")), mouseX, mouseY);
            return true;
        }
        int filter = hoveredFilter(mouseX, mouseY);
        if (filter >= 0) {
            String key = switch (filter) {
                case 0 -> "screen.buildscape.builders_workbench.filter.solid";
                case 1 -> "screen.buildscape.builders_workbench.filter.transparent";
                default -> "screen.buildscape.builders_workbench.filter.non_full";
            };
            int strictBit = (1 << filter) << ColorGradientSolver.STRICT_SHIFT;
            String hintKey = (filterMask & strictBit) != 0
                    ? "screen.buildscape.builders_workbench.filter.shift_active"
                    : "screen.buildscape.builders_workbench.filter.shift_hint";
            Services.PLATFORM.renderComponentTooltip(poseStackOrGraphics, mc.font, List.of(
                    ComponentHelper.translatable(key), ComponentHelper.translatable(hintKey)), mouseX, mouseY);
            return true;
        }
        int modifier = hoveredModifier(mouseX, mouseY);
        if (modifier >= 0) {
            String key = modifier == 0
                    ? "screen.buildscape.builders_workbench.filter.single_texture"
                    : "screen.buildscape.builders_workbench.filter.match_shape";
            String description = key + ".description";
            Services.PLATFORM.renderComponentTooltip(poseStackOrGraphics, mc.font, List.of(
                    ComponentHelper.translatable(key), ComponentHelper.translatable(description)), mouseX, mouseY);
            return true;
        }
        int result = hoveredReroll(mouseX, mouseY);
        if (result >= 0) {
            Services.PLATFORM.renderComponentTooltip(poseStackOrGraphics, mc.font, List.of(ComponentHelper.translatable(
                    "screen.buildscape.builders_workbench.cycle_hint")), mouseX, mouseY);
            return true;
        }
        return false;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button, int leftPos, int topPos) {
        this.leftPos = leftPos;
        this.topPos = topPos;
        int x = leftPos;
        int y = topPos;
        if (isIn(mouseX, mouseY, x + TAB_COLOR_X, y + TAB_Y, TAB_SIZE, TAB_SIZE)) {
            switchTab(0);
            return true;
        }
        if (isIn(mouseX, mouseY, x + TAB_GRADIENT_X, y + TAB_Y, TAB_SIZE, TAB_SIZE)) {
            switchTab(1);
            return true;
        }

        int filter = hoveredFilter(mouseX, mouseY);
        if (filter >= 0) {
            toggleFilter(filter, Services.PLATFORM.hasShiftDown());
            playClick();
            return true;
        }

        int modifier = hoveredModifier(mouseX, mouseY);
        if (modifier >= 0) {
            toggleModifier(modifier);
            playClick();
            return true;
        }

        int result = hoveredReroll(mouseX, mouseY);
        if (result >= 0 && (button == 0 || button == 1)) {
            int[] resultOffsets = currentResultOffsets();
            if (button == 1) resultOffsets[result]++;
            else resultOffsets[result] = Math.max(0, resultOffsets[result] - 1);
            solveNow();
            playClick();
            return true;
        }
        return false;
    }

    private void switchTab(int tab) {
        if (tab == activeTab) return;
        activeTab = tab;
        lastInputSignature = inputSignature();
        lastSentSignature = Integer.MIN_VALUE;
        updateDimensions();
        Minecraft mc = Minecraft.getInstance();
        if (mc.gameMode != null) {
            mc.gameMode.handleInventoryButtonClick(menu.containerId, tab);
        }
        playClick();
    }

    private int[] currentResultOffsets() {
        return resultOffsetsByTab[activeTab == 1 ? 1 : 0];
    }

    private void resetAllResultOffsets() {
        for (int[] offsets : resultOffsetsByTab) Arrays.fill(offsets, 0);
    }

    private int hoveredFilter(double mouseX, double mouseY) {
        int x = leftPos + filterX();
        int y = topPos + FILTER_Y;
        for (int i = 0; i < 3; i++) {
            if (isIn(mouseX, mouseY, x, y + i * FILTER_SPACING, 18, 18)) return i;
        }
        return -1;
    }

    private int hoveredModifier(double mouseX, double mouseY) {
        int y = topPos + MODIFIER_Y;
        if (isIn(mouseX, mouseY, leftPos + SINGLE_TEXTURE_X, y,
                WbRenderer.MODIFIER_SIZE, WbRenderer.MODIFIER_SIZE)) return 0;
        if (isIn(mouseX, mouseY, leftPos + MATCH_SHAPE_X, y,
                WbRenderer.MODIFIER_SIZE, WbRenderer.MODIFIER_SIZE)) return 1;
        return -1;
    }

    private int hoveredReroll(double mouseX, double mouseY) {
        if (activeTab == 0) {
            for (int i = 0; i < 9; i++) {
                int slotX = leftPos + COLOR_RESULT_X + i % 3 * 18;
                int slotY = topPos + COLOR_RESULT_Y + i / 3 * 18;
                if (!menu.getSlot(BuildersWorkbenchMenu.MENU_COLOR_RESULT_START + i).getItem().isEmpty()
                        && isIn(mouseX, mouseY, slotX + 11, slotY + 11, 5, 5)) return i;
            }
        } else {
            for (int i = 0; i < 9; i++) {
                int slotX = leftPos + GRADIENT_OUTPUT_X + i * 18;
                int slotY = topPos + GRADIENT_OUTPUT_Y;
                if (!isGradientAnchor(i)
                        && !menu.getSlot(BuildersWorkbenchMenu.MENU_GRADIENT_OUTPUT_START + i).getItem().isEmpty()
                        && isIn(mouseX, mouseY, slotX + 11, slotY + 11, 5, 5)) return i;
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
        Services.PLATFORM.playButtonClick();
    }

    private void toggleFilter(int filter, boolean shiftDown) {
        int categoryBit = 1 << filter;
        int strictBit = categoryBit << ColorGradientSolver.STRICT_SHIFT;
        if (shiftDown) {
            filterMask ^= strictBit;
            if ((filterMask & strictBit) != 0) filterMask |= categoryBit;
        } else {
            filterMask ^= categoryBit;
            if ((filterMask & categoryBit) == 0) filterMask &= ~strictBit;
        }
        resetAllResultOffsets();
        solveNow();
    }

    private void toggleModifier(int modifier) {
        filterMask ^= modifier == 0 ? ColorGradientSolver.FILTER_SINGLE_TEXTURE : ColorGradientSolver.FILTER_MATCH_SHAPE;
        resetAllResultOffsets();
        solveNow();
    }
}
