package com.kingodogo.buildscape.client.screen.tabs.supporters;

import com.kingodogo.buildscape.client.screen.widget.CustomScrollbarRenderer;
import com.kingodogo.buildscape.variantengine.family.BlockFamily;
import com.kingodogo.buildscape.variantengine.registry.VariantRegistrar;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class VerticalFamiliesSelectorWidget extends AbstractWidget {
    private static final int ITEM_SIZE = 20;
    private static final int ITEM_SPACING = 2;
    private static final int HEADER_HEIGHT = 20;
    
    private final List<BlockFamily> allFamilies;
    private List<BlockFamily> filteredFamilies;
    private String filter = "";
    private double scrollOffset = 0;
    private int itemsPerRow;
    private final Consumer<BlockFamily> onFamilySelected;
    private final Predicate<BlockFamily> isFamilySelected;
    private final CustomScrollbarRenderer scrollbarRenderer = new CustomScrollbarRenderer();

    public VerticalFamiliesSelectorWidget(int x, int y, int width, int height, Consumer<BlockFamily> onFamilySelected, Predicate<BlockFamily> isFamilySelected) {
        super(x, y, width, height, TextComponent.EMPTY);
        this.onFamilySelected = onFamilySelected;
        this.isFamilySelected = isFamilySelected;
        this.allFamilies = new ArrayList<>(VariantRegistrar.getDetectedFamilies());
        this.allFamilies.sort((a, b) -> a.getBaseBlock().getRegistryName().toString().compareTo(b.getBaseBlock().getRegistryName().toString()));
        this.filteredFamilies = new ArrayList<>(allFamilies);
        calculateLayout();
    }

    private void calculateLayout() {
        int availableWidth = width - 16;
        itemsPerRow = Math.max(1, (availableWidth - 10) / (ITEM_SIZE + ITEM_SPACING));
    }

    public void setFilter(String filter) {
        this.filter = filter.toLowerCase();
        refresh();
    }

    public void refresh() {
        if (filter.isEmpty()) {
            filteredFamilies = new ArrayList<>(allFamilies);
        } else {
            filteredFamilies = new ArrayList<>();
            for (BlockFamily family : allFamilies) {
                String name = family.getBaseBlock().getName().getString().toLowerCase();
                String reg = family.getBaseBlock().getRegistryName().toString().toLowerCase();
                if (name.contains(filter) || reg.contains(filter)) {
                    filteredFamilies.add(family);
                }
            }
        }
        scrollOffset = Math.max(0, Math.min(scrollOffset, getMaxScroll()));
    }

    private double getMaxScroll() {
        int rows = (int) Math.ceil((double) filteredFamilies.size() / itemsPerRow);
        int visibleHeight = height - HEADER_HEIGHT - 10;
        int contentHeight = rows * (ITEM_SIZE + ITEM_SPACING);
        return Math.max(0, contentHeight - visibleHeight);
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        calculateLayout();
        
        // Border
        int borderColor = 0xFF666666;
        fill(poseStack, x, y, x + width, y + 1, borderColor);
        fill(poseStack, x, y + height - 1, x + width, y + height, borderColor);
        fill(poseStack, x, y, x + 1, y + height, borderColor);
        fill(poseStack, x + width - 1, y, x + width, y + height, borderColor);

        Minecraft mc = Minecraft.getInstance();
        mc.font.draw(poseStack, new TranslatableComponent("buildscape.config.vertical.families"), x + 5, y + 5, 0xFFFFFF);

        double guiScale = mc.getWindow().getGuiScale();
        int windowHeight = mc.getWindow().getHeight();
        
        int scissorX = (int) (x * guiScale);
        int scissorY = (int) (windowHeight - (y + height - 5) * guiScale);
        int scissorWidth = (int) ((width - 16) * guiScale);
        int scissorHeight = (int) ((height - HEADER_HEIGHT - 10) * guiScale);

        if (scissorHeight > 0 && scissorWidth > 0) {
            RenderSystem.enableScissor(scissorX, scissorY, scissorWidth, scissorHeight);
        }

        int startRow = (int) (scrollOffset / (ITEM_SIZE + ITEM_SPACING));
        int totalRows = (int) Math.ceil((double) filteredFamilies.size() / itemsPerRow);
        int endRow = Math.min(startRow + (height / (ITEM_SIZE + ITEM_SPACING)) + 2, totalRows);

        int renderY = y + HEADER_HEIGHT - (int)(scrollOffset % (ITEM_SIZE + ITEM_SPACING));

        for (int row = startRow; row < endRow; row++) {
            int rowY = renderY + (row - startRow) * (ITEM_SIZE + ITEM_SPACING);
            for (int col = 0; col < itemsPerRow; col++) {
                int index = row * itemsPerRow + col;
                if (index >= filteredFamilies.size()) break;

                BlockFamily family = filteredFamilies.get(index);
                int itemX = x + 5 + col * (ITEM_SIZE + ITEM_SPACING);

                boolean isHovered = mouseX >= itemX && mouseX < itemX + ITEM_SIZE && mouseY >= rowY && mouseY < rowY + ITEM_SIZE;
                boolean selected = isFamilySelected.test(family);

                int color = selected ? 0xFF00FF00 : (isHovered ? 0xFFFFFFFF : 0xFF555555);
                fill(poseStack, itemX, rowY, itemX + ITEM_SIZE, rowY + ITEM_SIZE, selected ? 0x4000FF00 : 0x20000000);
                
                // Outer border
                fill(poseStack, itemX, rowY, itemX + ITEM_SIZE, rowY + 1, color);
                fill(poseStack, itemX, rowY + ITEM_SIZE - 1, itemX + ITEM_SIZE, rowY + ITEM_SIZE, color);
                fill(poseStack, itemX, rowY, itemX + 1, rowY + ITEM_SIZE, color);
                fill(poseStack, itemX + ITEM_SIZE - 1, rowY, itemX + ITEM_SIZE, rowY + ITEM_SIZE, color);

                ItemStack stack = new ItemStack(family.getBaseBlock());
                mc.getItemRenderer().renderAndDecorateItem(stack, itemX + 2, rowY + 2);
            }
        }

        RenderSystem.disableScissor();

        if (getMaxScroll() > 0) {
            int scrollbarX = x + width - 12;
            int scrollbarY = y + HEADER_HEIGHT;
            int scrollbarHeight = height - HEADER_HEIGHT - 10;
            double visibleRatio = (double)scrollbarHeight / (totalRows * (ITEM_SIZE + ITEM_SPACING));
            scrollbarRenderer.renderScrollbar(poseStack, scrollbarX, scrollbarY, scrollbarHeight, scrollOffset, getMaxScroll(), visibleRatio);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isMouseOver(mouseX, mouseY) || mouseY < y + HEADER_HEIGHT) return false;

        double maxScroll = getMaxScroll();
        if (maxScroll > 0) {
            int scrollbarX = x + width - 12;
            int scrollbarHeight = height - HEADER_HEIGHT - 10;
            double visibleRatio = (double)scrollbarHeight / (Math.ceil((double)filteredFamilies.size() / itemsPerRow) * (ITEM_SIZE + ITEM_SPACING));
            double newOffset = scrollbarRenderer.handleMouseClick(mouseX, mouseY, button, scrollbarX, y + HEADER_HEIGHT, scrollbarHeight, x, y + HEADER_HEIGHT, width - 16, scrollbarHeight, scrollOffset, maxScroll, visibleRatio);
            if (newOffset >= 0) {
                scrollOffset = newOffset;
                return true;
            }
        }

        int startRow = (int) (scrollOffset / (ITEM_SIZE + ITEM_SPACING));
        int renderY = y + HEADER_HEIGHT - (int)(scrollOffset % (ITEM_SIZE + ITEM_SPACING));

        for (int row = startRow; row < startRow + (height / (ITEM_SIZE + ITEM_SPACING)) + 2; row++) {
            int rowY = renderY + (row - startRow) * (ITEM_SIZE + ITEM_SPACING);
            for (int col = 0; col < itemsPerRow; col++) {
                int index = row * itemsPerRow + col;
                if (index < 0 || index >= filteredFamilies.size()) continue;

                int itemX = x + 5 + col * (ITEM_SIZE + ITEM_SPACING);
                if (mouseX >= itemX && mouseX < itemX + ITEM_SIZE && mouseY >= rowY && mouseY < rowY + ITEM_SIZE) {
                    onFamilySelected.accept(filteredFamilies.get(index));
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (isMouseOver(mouseX, mouseY)) {
            scrollOffset = Mth.clamp(scrollOffset - delta * (ITEM_SIZE + ITEM_SPACING), 0, getMaxScroll());
            return true;
        }
        return false;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {}
}
