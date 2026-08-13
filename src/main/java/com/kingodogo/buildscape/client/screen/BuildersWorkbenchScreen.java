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
    // ── Layout ────────────────────────────────────────────────────────────────
    // All values below are relative to leftPos/topPos and mirror the artwork in
    // textures/gui/builders_workbench/{color,gradient}_builder_bg.png (256x256 sheets,
    // artwork anchored at 0,0). Slot coordinates MUST stay in sync with
    // BuildersWorkbenchMenu - see the LAYOUT block there.
    private static final int COLOR_WIDTH = 184;
    private static final int GRADIENT_WIDTH = 206;
    private static final int GUI_HEIGHT = 203;

    // Tab sprites are 17x17 with the icon baked in. TAB_Y = -1 keeps their bottom edge
    // exactly where the artwork expects it (GUI y = 15, one row above the panel top),
    // and the 3px gap matches the spacing drawn in the mockup.
    private static final int TAB_SIZE = 17;
    private static final int TAB_Y = -1;
    private static final int TAB_COLOR_X = 6;
    private static final int TAB_GRADIENT_X = 26;

    // Title banner (baked into the background). Every title is scaled to the same
    // usable width, so both tabs end up with an identical margin on each side even
    // though their strings differ in length.
    private static final int TITLE_X = 58;
    private static final int TITLE_W = 68;
    private static final int TITLE_Y = 14;
    private static final int TITLE_PAD = 8;   // margin left and right, identical on both tabs
    private static final int TITLE_H = 8;     // vanilla glyph height
    private static final float TITLE_MIN_SCALE = 0.5f;
    /** Trailing glyph spacing that font.width() includes but nothing actually draws. */
    private static final int TITLE_TRAILING_ADVANCE = 1;
    private static final String TITLE_KEY_COLOR = "screen.buildscape.builders_workbench.color_builder";
    private static final String TITLE_KEY_GRADIENT = "screen.buildscape.builders_workbench.gradient_builder";

    // Filter buttons (18x18, stacked vertically)
    private static final int FILTER_Y = 33;
    private static final int FILTER_SPACING = 18;
    private static final int COLOR_FILTER_X = 141;
    private static final int GRADIENT_FILTER_X = 184;

    // Compact modifier controls occupy the unused lower-left strip of both panels.
    // Their 11x11 hitboxes do not overlap the workbench slots or the copy arrow.
    private static final int MODIFIER_Y = 94;
    private static final int SINGLE_TEXTURE_X = 13;
    private static final int MATCH_SHAPE_X = 27;

    // Copy arrow (48x16, animated) - identical position on both tabs. The sprite has
    // transparent padding: the ink sits at x 4..43, y 2..12, symmetric around row 7.
    // These values put that ink in the middle of the 46px gap between the pouch slots.
    private static final int ARROW_X = 68;
    private static final int ARROW_Y = 94;

    // Slot interiors used for the re-roll dots (must match the menu)
    private static final int COLOR_RESULT_X = 66;
    private static final int COLOR_RESULT_Y = 34;
    private static final int GRADIENT_OUTPUT_X = 12;
    private static final int GRADIENT_OUTPUT_Y = 65;
    private static final int INITIAL_DATA_SYNC_TICKS = 3;
    /** Above the item render layer (items blit around Z 100-200) so the dots stay visible. */
    private static final float REROLL_Z = 300.0f;

    private int activeTab;
    private int filterMask;
    private final int[][] resultOffsetsByTab = new int[2][9];
    private int lastInputSignature = Integer.MIN_VALUE;
    private int lastSentSignature = Integer.MIN_VALUE;
    private int initialDataSyncTicks;

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
        for (int tab = 0; tab < resultOffsetsByTab.length; tab++) {
            for (int i = 0; i < resultOffsetsByTab[tab].length; i++) {
                resultOffsetsByTab[tab][i] = menu.getResultOffset(tab, i);
            }
        }
        updateDimensions();
        super.init();
        // AbstractContainerScreen#init recentres on imageWidth, which would undo the
        // anchoring above, so re-apply it once the vanilla layout pass is done.
        updateDimensions();
        ClientBlockColorCatalog.ensureReady();
        lastInputSignature = inputSignature();
        lastSentSignature = Integer.MIN_VALUE;
        initialDataSyncTicks = INITIAL_DATA_SYNC_TICKS;
    }

    @Override
    protected void containerTick() {
        super.containerTick();
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

    /**
     * Sizes the screen for the active tab, but anchors it as if it were always the
     * colour builder.
     *
     * <p>The gradient artwork is wider only because of the filter panel hanging off its
     * right-hand side - the main body starts at x = 0 in both sheets. Centring on the
     * real imageWidth would therefore shove the whole panel {@code (GRADIENT_WIDTH -
     * COLOR_WIDTH) / 2} pixels to the left when switching tabs. Centring on COLOR_WIDTH
     * instead keeps the body, its slots and the player inventory perfectly still, and
     * lets the extra strip grow to the right.
     */
    private void updateDimensions() {
        imageWidth = activeTab == 0 ? COLOR_WIDTH : GRADIENT_WIDTH;
        imageHeight = GUI_HEIGHT;
        leftPos = (width - COLOR_WIDTH) / 2;
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
                    workbench.getItem(BuildersWorkbenchBlockEntity.SLOT_COLOR_PICKER), filterMask,
                    currentResultOffsets());
        } else {
            List<ItemStack> anchors = new ArrayList<>(9);
            for (int i = 0; i < 9; i++) {
                anchors.add(workbench.getItem(BuildersWorkbenchBlockEntity.SLOT_GRADIENT_INPUT_START + i));
            }
            solved = ColorGradientSolver.solveGradient(anchors, filterMask, currentResultOffsets());
        }
        ModMessages.INSTANCE.sendToServer(new BuildersWorkbenchResultsPacket(
                workbench.getBlockPos(), activeTab, filterMask, currentResultOffsets(), solved));
        lastSentSignature = solveSignature;
    }

    private void solveNow() {
        lastInputSignature = inputSignature();
        sendSolvedResults(solveSignature(lastInputSignature));
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

        // The whole static layout - panel, frames, every slot background, the player
        // inventory and the idle arrow - lives in a single background texture.
        WbRenderer.drawBuilderBG(poseStack,
                activeTab == 0 ? WbRenderer.BG_COLOR_BUILDER : WbRenderer.BG_GRADIENT_BUILDER,
                x, y, imageWidth, imageHeight);

        drawTabs(poseStack, x, y, mouseX, mouseY);
        drawTitle(poseStack, x, y, activeTab == 0 ? TITLE_KEY_COLOR : TITLE_KEY_GRADIENT);
        drawFilters(poseStack, x + filterX(), y + FILTER_Y, mouseX, mouseY);
        drawModifierFilters(poseStack, x, y, mouseX, mouseY);
        drawCopyArrow(poseStack, x + ARROW_X, y + ARROW_Y);

        RenderSystem.enableDepthTest();
    }

    private void drawTabs(PoseStack poseStack, int x, int y, int mouseX, int mouseY) {
        boolean colorHover = isIn(mouseX, mouseY, x + TAB_COLOR_X, y + TAB_Y, TAB_SIZE, TAB_SIZE);
        boolean gradientHover = isIn(mouseX, mouseY, x + TAB_GRADIENT_X, y + TAB_Y, TAB_SIZE, TAB_SIZE);
        WbRenderer.drawTabButton(poseStack, x + TAB_COLOR_X, y + TAB_Y, activeTab == 0, colorHover,
                WbRenderer.TAB_COLOR, WbRenderer.TAB_COLOR_HOVER, WbRenderer.TAB_COLOR_SEL);
        WbRenderer.drawTabButton(poseStack, x + TAB_GRADIENT_X, y + TAB_Y, activeTab == 1, gradientHover,
                WbRenderer.TAB_GRADIENT, WbRenderer.TAB_GRADIENT_HOVER, WbRenderer.TAB_GRADIENT_SEL);
    }

    private int filterX() {
        return activeTab == 0 ? COLOR_FILTER_X : GRADIENT_FILTER_X;
    }

    private void drawTitle(PoseStack poseStack, int x, int y, String key) {
        String title = new TranslatableComponent(key).getString();
        int textWidth = font.width(title);
        int usable = TITLE_W - TITLE_PAD * 2;

        // Shrink to the shared usable width. Titles that already fit are left alone
        // rather than stretched, so short localisations never look blown up.
        float scale = textWidth > usable ? Math.max(TITLE_MIN_SCALE, (float) usable / textWidth) : 1.0f;

        // font.width() counts the 1px spacing that follows every glyph, including the
        // last one, so the visible ink is one pixel narrower than the measured width.
        // Centring on the raw value biases the text to the left; centre on the ink.
        float inkWidth = (textWidth - TITLE_TRAILING_ADVANCE) * scale;
        float drawX = x + TITLE_X + (TITLE_W - inkWidth) / 2.0f;
        float drawY = y + TITLE_Y + (TITLE_H - TITLE_H * scale) / 2.0f;

        poseStack.pushPose();
        poseStack.translate(drawX, drawY, 0.0f);
        poseStack.scale(scale, scale, 1.0f);
        font.draw(poseStack, title, 0.0f, 0.0f, 0xFF372211);
        poseStack.popPose();
    }

    private void drawFilters(PoseStack poseStack, int x, int y, int mouseX, int mouseY) {
        for (int i = 0; i < 3; i++) {
            boolean hovered = isIn(mouseX, mouseY, x, y + i * FILTER_SPACING, 18, 18);
            WbRenderer.drawFilterButton(poseStack, x, y + i * FILTER_SPACING, i, filterMask, hovered);
        }
    }

    private void drawModifierFilters(PoseStack poseStack, int x, int y, int mouseX, int mouseY) {
        boolean singleHover = isIn(mouseX, mouseY, x + SINGLE_TEXTURE_X, y + MODIFIER_Y,
                WbRenderer.MODIFIER_SIZE, WbRenderer.MODIFIER_SIZE);
        WbRenderer.drawModifierButton(poseStack, x + SINGLE_TEXTURE_X, y + MODIFIER_Y,
                (filterMask & ColorGradientSolver.FILTER_SINGLE_TEXTURE) != 0, singleHover,
                WbRenderer.BTN_SINGLE_TEXTURE, WbRenderer.BTN_SINGLE_TEXTURE_HOVER,
                WbRenderer.BTN_SINGLE_TEXTURE_SEL);

        boolean shapeHover = isIn(mouseX, mouseY, x + MATCH_SHAPE_X, y + MODIFIER_Y,
                WbRenderer.MODIFIER_SIZE, WbRenderer.MODIFIER_SIZE);
        WbRenderer.drawModifierButton(poseStack, x + MATCH_SHAPE_X, y + MODIFIER_Y,
                (filterMask & ColorGradientSolver.FILTER_MATCH_SHAPE) != 0, shapeHover,
                WbRenderer.BTN_MATCH_SHAPE, WbRenderer.BTN_MATCH_SHAPE_HOVER,
                WbRenderer.BTN_MATCH_SHAPE_SEL);
    }

    private void drawCopyArrow(PoseStack poseStack, int x, int y) {
        WbRenderer.drawCopyArrow(poseStack, x, y, menu.getCopyProgress() / 40.0f);
    }

    /**
     * Drawn after super.render() and lifted on the Z axis: item stacks are rendered at a
     * blit offset of their own, so without this the dots would disappear under any item
     * sitting in the slot.
     */
    private void renderRerollControls(PoseStack poseStack, int mouseX, int mouseY) {
        poseStack.pushPose();
        poseStack.translate(0.0f, 0.0f, REROLL_Z);

        if (activeTab == 0) {
            for (int i = 0; i < 9; i++) drawRerollControl(poseStack, mouseX, mouseY,
                    leftPos + COLOR_RESULT_X + i % 3 * 18, topPos + COLOR_RESULT_Y + i / 3 * 18,
                    BuildersWorkbenchMenu.MENU_COLOR_RESULT_START + i);
        } else {
            for (int i = 0; i < 9; i++) {
                if (isGradientAnchor(i)) continue;
                drawRerollControl(poseStack, mouseX, mouseY,
                        leftPos + GRADIENT_OUTPUT_X + i * 18, topPos + GRADIENT_OUTPUT_Y,
                        BuildersWorkbenchMenu.MENU_GRADIENT_OUTPUT_START + i);
            }
        }

        poseStack.popPose();
    }

    private void drawRerollControl(PoseStack poseStack, int mouseX, int mouseY, int slotX, int slotY, int menuSlot) {
        if (menuSlot < 0 || menuSlot >= menu.slots.size() || menu.getSlot(menuSlot).getItem().isEmpty()) return;
        boolean hovered = isIn(mouseX, mouseY, slotX + 11, slotY + 11, 5, 5);
        fill(poseStack, slotX + 11, slotY + 11, slotX + 16, slotY + 16,
                hovered ? 0xFF00FFCC : 0xFF00AA88);
        fill(poseStack, slotX + 13, slotY + 13, slotX + 14, slotY + 14, 0xFF000000);
    }

    @Override
    protected void renderTooltip(PoseStack poseStack, int mouseX, int mouseY) {
        if (isIn(mouseX, mouseY, leftPos + TAB_COLOR_X, topPos + TAB_Y, TAB_SIZE, TAB_SIZE)) {
            renderComponentTooltip(poseStack, List.of(new TranslatableComponent(
                    "screen.buildscape.builders_workbench.color_builder")), mouseX, mouseY);
            return;
        }
        if (isIn(mouseX, mouseY, leftPos + TAB_GRADIENT_X, topPos + TAB_Y, TAB_SIZE, TAB_SIZE)) {
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
            int strictBit = (1 << filter) << ColorGradientSolver.STRICT_SHIFT;
            String hintKey = (filterMask & strictBit) != 0
                    ? "screen.buildscape.builders_workbench.filter.shift_active"
                    : "screen.buildscape.builders_workbench.filter.shift_hint";
            renderComponentTooltip(poseStack, List.of(
                    new TranslatableComponent(key), new TranslatableComponent(hintKey)), mouseX, mouseY);
            return;
        }
        int modifier = hoveredModifier(mouseX, mouseY);
        if (modifier >= 0) {
            String key = modifier == 0
                    ? "screen.buildscape.builders_workbench.filter.single_texture"
                    : "screen.buildscape.builders_workbench.filter.match_shape";
            String description = key + ".description";
            renderComponentTooltip(poseStack, List.of(
                    new TranslatableComponent(key), new TranslatableComponent(description)), mouseX, mouseY);
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
        if (isIn(mouseX, mouseY, x + TAB_COLOR_X, y + TAB_Y, TAB_SIZE, TAB_SIZE)) {
            switchTab(0);
            return true;
        }
        if (isIn(mouseX, mouseY, x + TAB_GRADIENT_X, y + TAB_Y, TAB_SIZE, TAB_SIZE)) {
            switchTab(1);
            return true;
        }

        int filter = hoveredFilter(mouseX, mouseY);
        if (filter >= 0 && (button == 0 || button == 1)) {
            int categoryBit = 1 << filter;
            int strictBit = categoryBit << ColorGradientSolver.STRICT_SHIFT;
            if (hasShiftDown()) {
                if ((filterMask & categoryBit) == 0) {
                    filterMask |= categoryBit | strictBit;
                } else if ((filterMask & strictBit) == 0) {
                    filterMask |= strictBit;
                } else {
                    filterMask &= ~(categoryBit | strictBit);
                }
            } else {
                filterMask ^= categoryBit;
                if ((filterMask & categoryBit) == 0) filterMask &= ~strictBit;
            }
            resetAllResultOffsets();
            lastInputSignature = inputSignature();
            solveNow();
            playClick();
            return true;
        }

        int modifier = hoveredModifier(mouseX, mouseY);
        if (modifier >= 0 && button == 0) {
            filterMask ^= modifier == 0
                    ? ColorGradientSolver.FILTER_SINGLE_TEXTURE
                    : ColorGradientSolver.FILTER_MATCH_SHAPE;
            resetAllResultOffsets();
            lastInputSignature = inputSignature();
            solveNow();
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
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void switchTab(int tab) {
        if (tab == activeTab) return;
        activeTab = tab;
        lastInputSignature = inputSignature();
        lastSentSignature = Integer.MIN_VALUE;
        updateDimensions();
        if (minecraft != null && minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, tab);
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
        if (minecraft != null && minecraft.player != null) {
            minecraft.player.playSound(SoundEvents.UI_BUTTON_CLICK, 0.3f, 1.1f);
        }
    }
}
