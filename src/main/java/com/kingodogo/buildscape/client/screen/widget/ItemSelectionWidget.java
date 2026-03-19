package com.kingodogo.buildscape.client.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

public class ItemSelectionWidget extends AbstractWidget {
    private static final int ITEM_SIZE = 20;
    private static final int ITEM_SPACING = 2;
    private static final int SCROLLBAR_WIDTH = 10;
    private static final int HORIZONTAL_MARGIN = 10;
    private static final int MAX_ITEMS_PER_ROW = 16;
    private static final int MAX_VISIBLE_ROWS = 16;
    // Header area: top padding (5px) + search box height (~20px) + gap (5px)
    private int headerAreaHeight = 26; // Default to 1px below 20px search bar
    private static final int GRID_PADDING_TOP = 5; // Fixed headspace between separator and items

    private final Consumer<String> onItemSelected;
    private final ToIntFunction<String> getItemState;
    private final List<Item> allItems;
    private List<Item> filteredItems;
    private String filter = "";
    private double scrollOffset = 0;
    private int maxVisibleRows;
    private int itemsPerRow;
    private SortToggleButton.SortType sortMode = SortToggleButton.SortType.ALL_ITEMS;
    private String currentModNamespace = "buildscape";
    private final CustomScrollbarRenderer scrollbarRenderer = new CustomScrollbarRenderer();
    private final java.util.Map<Item, ItemStack> itemStackCache = new java.util.HashMap<>();

    public ItemSelectionWidget(int x, int y, int width, int height,
            Consumer<String> onItemSelected,
            ToIntFunction<String> getItemState) {
        super(x, y, width, height, net.minecraft.network.chat.TextComponent.EMPTY);
        this.onItemSelected = onItemSelected;
        this.getItemState = getItemState;

        allItems = new ArrayList<>(ForgeRegistries.ITEMS.getValues());
        filteredItems = new ArrayList<>(allItems);

        calculateLayout();
    }

    public void setHeaderAreaHeight(int height) {
        this.headerAreaHeight = height;
        calculateLayout();
    }

    private void calculateLayout() {
        if (width <= 0)
            return;

        int contentWidth = width - SCROLLBAR_WIDTH - 10;
        itemsPerRow = Math.min(16, Math.max(1, contentWidth / (ITEM_SIZE + ITEM_SPACING)));
        maxVisibleRows = (height - headerAreaHeight - GRID_PADDING_TOP - 10) / (ITEM_SIZE + ITEM_SPACING);

        // Ensure scroll offset is valid after layout changes
        if (filteredItems != null) {
            scrollOffset = Math.max(0, Math.min(scrollOffset, getMaxScroll()));
        }
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        calculateLayout();
        refresh();
    }

    public void setHeight(int height) {
        try {
            java.lang.reflect.Field heightField = AbstractWidget.class.getDeclaredField("height");
            heightField.setAccessible(true);
            heightField.setInt(this, height);
            calculateLayout();
            refresh();
        } catch (Exception e) {
        }
    }

    public void setFilter(String filter) {
        this.filter = filter.toLowerCase();
        refresh();
    }

    public void setSortMode(SortToggleButton.SortType sortMode) {
        this.sortMode = sortMode;
        refresh();
    }

    public void setModNamespace(String namespace) {
        this.currentModNamespace = namespace;
        if (sortMode == SortToggleButton.SortType.MOD_ONLY) {
            refresh();
        }
    }

    public String getCurrentModNamespace() {
        return currentModNamespace;
    }

    public SortToggleButton.SortType getSortMode() {
        return sortMode;
    }

    public void refresh() {
        List<Item> itemsToFilter = allItems;

        switch (sortMode) {
            case INVENTORY:
                itemsToFilter = getInventoryItems();
                break;
            case MOD_ONLY:
                itemsToFilter = allItems.stream()
                        .filter(item -> {
                            ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(item);
                            return itemId != null && itemId.getNamespace().equals(currentModNamespace);
                        })
                        .collect(Collectors.toList());
                break;
            case ALL_ITEMS:
            default:
                itemsToFilter = allItems;
                break;
        }

        if (filter.isEmpty()) {
            filteredItems = new ArrayList<>(itemsToFilter);
        } else {
            filteredItems = itemsToFilter.stream()
                    .filter(item -> {
                        String itemId = ForgeRegistries.ITEMS.getKey(item).toString().toLowerCase();
                        String itemName = item.getDescription().getString().toLowerCase();
                        return itemId.contains(filter) || itemName.contains(filter);
                    })
                    .collect(Collectors.toList());
        }
        calculateLayout();
    }

    private List<Item> getInventoryItems() {
        Set<Item> inventoryItems = new HashSet<>();
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            Inventory inventory = mc.player.getInventory();
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack stack = inventory.getItem(i);
                if (!stack.isEmpty()) {
                    inventoryItems.add(stack.getItem());
                }
            }
        }
        return new ArrayList<>(inventoryItems);
    }

    private double getMaxScroll() {
        if (filteredItems == null || filteredItems.isEmpty() || itemsPerRow <= 0)
            return 0;

        int totalRows = (int) Math.ceil((double) filteredItems.size() / itemsPerRow);
        int visibleHeight = height - headerAreaHeight - GRID_PADDING_TOP - 10; // Extra padding
        int contentHeight = totalRows * (ITEM_SIZE + ITEM_SPACING);
        return Math.max(0, contentHeight - visibleHeight);
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        calculateLayout();

        poseStack.pushPose();
        Minecraft mc = Minecraft.getInstance();
        double guiScale = mc.getWindow().getGuiScale();
        int windowHeight = mc.getWindow().getHeight();

        int scissorX = (int) (x * guiScale);
        // Exclude header area from scissor height
        // Scissor Y is from bottom, so it remains the same (scissoring from bottom
        // usually implies bottom origin)
        // Correct logic: scissorY is bottom of rect. scissorHeight is height.
        // We want to reduce height by headerAreaHeight.
        // And we want the bottom to stay fixed.
        int bottomMargin = 10;
        int scissorY = (int) (windowHeight - (y + height) * guiScale + bottomMargin * guiScale);
        int scissorWidth = (int) ((width - 21) * guiScale); // Exclude scrollbar area
        
        // Fix: Scissor starts exactly below the header separator. This allows padding and selection borders to be visible.
        int scissorHeight = (int) ((height - headerAreaHeight - 1 - bottomMargin) * guiScale);

        if (scissorHeight > 0 && scissorWidth > 0) {
            RenderSystem.enableScissor(scissorX, scissorY, scissorWidth, scissorHeight);
        }

        int totalRows = (int) Math.ceil((double) filteredItems.size() / itemsPerRow);
        int startRow = (int) Math.floor(scrollOffset / (ITEM_SIZE + ITEM_SPACING));
        int endRow = Math.min(startRow + maxVisibleRows + 2, totalRows);

        double pixelOffsetInRow = scrollOffset % (ITEM_SIZE + ITEM_SPACING);
        int itemY = y + headerAreaHeight + GRID_PADDING_TOP - (int) pixelOffsetInRow; // Start below header area with padding
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        for (int row = startRow; row < endRow; row++) {
            int rowY = itemY + (row - startRow) * (ITEM_SIZE + ITEM_SPACING);

            // Updated visibility check - allow items slightly above the start point for smooth scrolling
            if (rowY + ITEM_SIZE < y + headerAreaHeight || rowY > y + height - bottomMargin) {
                continue;
            }

            // Calculate centering offset
            int totalRowWidth = itemsPerRow * (ITEM_SIZE + ITEM_SPACING) - ITEM_SPACING;
            int availableAreaWidth = width - 21; // Same as in calculateItemsPerRow
            int startXOffset = Math.max(0, (availableAreaWidth - totalRowWidth) / 2);

            for (int col = 0; col < itemsPerRow; col++) {
                int index = row * itemsPerRow + col;
                if (index >= filteredItems.size())
                    break;

                Item item = filteredItems.get(index);
                int itemX = x + 5 + startXOffset + col * (ITEM_SIZE + ITEM_SPACING);

                if (itemX + ITEM_SIZE < x || itemX > x + width) {
                    continue;
                }

                boolean isHovered = mouseX >= itemX && mouseX < itemX + ITEM_SIZE &&
                        mouseY >= rowY && mouseY < rowY + ITEM_SIZE &&
                        mouseX >= x && mouseX < x + width &&
                        mouseY >= y + headerAreaHeight + 1 && mouseY < y + height - bottomMargin; // Updated hover check

                String itemId = ForgeRegistries.ITEMS.getKey(item).toString();
                int state = getItemState.applyAsInt(itemId); // 0 = none, 1 = allowed, 2 = blocklisted

                int bgColor;
                if (state == 1) {
                    bgColor = isHovered ? 0x6000FF00 : 0x4000FF00;
                } else if (state == 2) {
                    bgColor = isHovered ? 0x60FF0000 : 0x40FF0000;
                } else {
                    bgColor = isHovered ? 0x40CCCCCC : 0x33CCCCCC;
                }
                fill(poseStack, itemX, rowY, itemX + ITEM_SIZE, rowY + ITEM_SIZE, bgColor);

                if (state != 0) {
                    int panelBorderColor = (state == 1) ? 0xFF00FF00 : 0xFFFF0000;
                    fill(poseStack, itemX - 1, rowY - 1, itemX + ITEM_SIZE + 1, rowY, panelBorderColor);
                    fill(poseStack, itemX - 1, rowY + ITEM_SIZE, itemX + ITEM_SIZE + 1, rowY + ITEM_SIZE + 1,
                            panelBorderColor);
                    fill(poseStack, itemX - 1, rowY - 1, itemX, rowY + ITEM_SIZE + 1, panelBorderColor);
                    fill(poseStack, itemX + ITEM_SIZE, rowY - 1, itemX + ITEM_SIZE + 1, rowY + ITEM_SIZE + 1,
                            panelBorderColor);
                }

                poseStack.pushPose();
                poseStack.translate(0, 0, 100);
                ItemStack stack = itemStackCache.computeIfAbsent(item, ItemStack::new);
                itemRenderer.renderGuiItem(stack, itemX + 2, rowY + 2);
                itemRenderer.renderGuiItemDecorations(
                        mc.font, stack, itemX + 2, rowY + 2);
                poseStack.popPose();
            }
        }

        if (scissorHeight > 0 && scissorWidth > 0) {
            RenderSystem.disableScissor();
        }
        poseStack.popPose();

        // Draw panel borders and separator
        int borderColor = 0xFF666666;
        fill(poseStack, x, y, x + width, y + 1, borderColor); // Top
        fill(poseStack, x, y + height - 1, x + width, y + height, borderColor); // Bottom
        fill(poseStack, x, y, x + 1, y + height, borderColor); // Left
        fill(poseStack, x + width - 1, y, x + width, y + height, borderColor); // Right
        fill(poseStack, x, y + headerAreaHeight + 1, x + width, y + headerAreaHeight + 2, borderColor); // Separator (moved 1px down)
        
        // Render scrollbar after disabling scissor so it is not clipped
        if (getMaxScroll() > 0) {
            int scrollbarX = x + width - CustomScrollbarRenderer.getScrollbarWidth() - 4; // 4px form edge
            int scrollbarY = y + headerAreaHeight + GRID_PADDING_TOP;
            bottomMargin = 10;
            int scrollbarHeight = height - headerAreaHeight - GRID_PADDING_TOP - bottomMargin;

            double visibleRatio = maxVisibleRows * itemsPerRow / (double) filteredItems.size();
            scrollbarRenderer.renderScrollbar(poseStack, scrollbarX, scrollbarY, scrollbarHeight,
                    scrollOffset, getMaxScroll(), visibleRatio);
        }

    }

    public void renderTooltip(PoseStack poseStack, int mouseX, int mouseY) {
        if (mouseX < x || mouseX >= x + width || mouseY < y || mouseY >= y + height) {
            return;
        }

        int totalRows = (int) Math.ceil((double) filteredItems.size() / itemsPerRow);
        int startRow = (int) Math.floor(scrollOffset / (ITEM_SIZE + ITEM_SPACING));
        int endRow = Math.min(startRow + maxVisibleRows + 2, totalRows);

        double pixelOffsetInRow = scrollOffset % (ITEM_SIZE + ITEM_SPACING);
        int itemY = y + headerAreaHeight + GRID_PADDING_TOP - (int) pixelOffsetInRow; // Updated to headerAreaHeight + padding
        Item hoveredItem = null;

        for (int row = startRow; row < endRow; row++) {
            int rowY = itemY + (row - startRow) * (ITEM_SIZE + ITEM_SPACING);

            // Updated visibility check
            if (rowY + ITEM_SIZE < y + headerAreaHeight + GRID_PADDING_TOP || rowY > y + height) {
                continue;
            }

            // Calculate centering offset
            int totalRowWidth = itemsPerRow * (ITEM_SIZE + ITEM_SPACING) - ITEM_SPACING;
            int availableAreaWidth = width - 21; // Same as in calculateItemsPerRow
            int startXOffset = Math.max(0, (availableAreaWidth - totalRowWidth) / 2);

            for (int col = 0; col < itemsPerRow; col++) {
                int index = row * itemsPerRow + col;
                if (index >= filteredItems.size())
                    break;

                Item item = filteredItems.get(index);
                int itemX = x + 5 + startXOffset + col * (ITEM_SIZE + ITEM_SPACING);

                if (itemX + ITEM_SIZE < x || itemX > x + width) {
                    continue;
                }

                boolean isHovered = mouseX >= itemX && mouseX < itemX + ITEM_SIZE &&
                        mouseY >= rowY && mouseY < rowY + ITEM_SIZE &&
                        mouseX >= x && mouseX < x + width &&
                        mouseY >= y + headerAreaHeight && mouseY < y + height; // Updated hover check
                if (isHovered) {
                    hoveredItem = item;
                }
            }
        }

        if (hoveredItem != null) {
            ItemStack stack = new ItemStack(hoveredItem);
            Minecraft mc = Minecraft.getInstance();

            if (mc.screen != null) {
                java.util.List<net.minecraft.network.chat.Component> tooltipLines = stack.getTooltipLines(null,
                        net.minecraft.world.item.TooltipFlag.Default.NORMAL);

                java.util.List<net.minecraft.util.FormattedCharSequence> formattedLines = new java.util.ArrayList<>();
                int maxWidth = Math.max(200, mc.screen.width / 2);
                for (net.minecraft.network.chat.Component line : tooltipLines) {
                    formattedLines.addAll(mc.font.split(line, maxWidth));
                }

                mc.screen.renderTooltip(poseStack, formattedLines, mouseX, mouseY);
            }
        }

        // Scrollbar is now rendered in renderButton method using
        // CustomScrollbarRenderer
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Updated check to ignore clicks in header area and padding gap
        if (!isMouseOver(mouseX, mouseY) || mouseY < y + headerAreaHeight + GRID_PADDING_TOP) {
            return false;
        }

        double maxScroll = getMaxScroll();
        if (maxScroll > 0) {
            int scrollbarX = x + width - CustomScrollbarRenderer.getScrollbarWidth() - 4; // 4px form edge
            int scrollbarY = y + headerAreaHeight + GRID_PADDING_TOP;
            int bottomMargin = 10;
            int scrollbarHeight = height - headerAreaHeight - GRID_PADDING_TOP - bottomMargin;
            
            // Content area for dragging
            int contentX = x + 5;
            int contentY = y + headerAreaHeight + GRID_PADDING_TOP;
            int contentWidth = width - 21; 
            int contentHeight = scrollbarHeight;

            double visibleRatio = maxVisibleRows * itemsPerRow / (double) filteredItems.size();
            double newOffset = scrollbarRenderer.handleMouseClick(mouseX, mouseY, button,
                    scrollbarX, scrollbarY, scrollbarHeight,
                    contentX, contentY, contentWidth, contentHeight,
                    scrollOffset, maxScroll, visibleRatio);

            if (newOffset >= 0) {
                scrollOffset = newOffset;
                return true;
            }
        }

        int totalRows = (int) Math.ceil((double) filteredItems.size() / itemsPerRow);
        int startRow = (int) Math.floor(scrollOffset / (ITEM_SIZE + ITEM_SPACING));
        int endRow = Math.min(startRow + maxVisibleRows + 2, totalRows);

        double pixelOffsetInRow = scrollOffset % (ITEM_SIZE + ITEM_SPACING);
        int itemY = y + headerAreaHeight + GRID_PADDING_TOP - (int) pixelOffsetInRow;

        for (int row = startRow; row < endRow; row++) {
            int rowY = itemY + (row - startRow) * (ITEM_SIZE + ITEM_SPACING);

            // Updated visibility check
            if (rowY + ITEM_SIZE < y + headerAreaHeight + GRID_PADDING_TOP || rowY > y + height) {
                continue;
            }

            // Calculate centering offset
            int totalRowWidth = itemsPerRow * (ITEM_SIZE + ITEM_SPACING) - ITEM_SPACING;
            int availableAreaWidth = width - 21; // Same as in calculateItemsPerRow
            int startXOffset = Math.max(0, (availableAreaWidth - totalRowWidth) / 2);

            for (int col = 0; col < itemsPerRow; col++) {
                int index = row * itemsPerRow + col;
                if (index >= filteredItems.size())
                    break;

                int itemX = x + 5 + startXOffset + col * (ITEM_SIZE + ITEM_SPACING);

                if (itemX + ITEM_SIZE < x || itemX > x + width) {
                    continue;
                }

                if (mouseX >= itemX && mouseX < itemX + ITEM_SIZE &&
                        mouseY >= rowY && mouseY < rowY + ITEM_SIZE &&
                        mouseX >= x && mouseX < x + width &&
                        mouseY >= y + headerAreaHeight + GRID_PADDING_TOP && mouseY < y + height) { // Updated click check
                    Item item = filteredItems.get(index);
                    String itemId = ForgeRegistries.ITEMS.getKey(item).toString();
                    onItemSelected.accept(itemId);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (!isMouseOver(mouseX, mouseY)) {
            return false;
        }

        double step = (ITEM_SIZE + ITEM_SPACING);
        scrollOffset = Math.max(0, Math.min(getMaxScroll(), scrollOffset - delta * step));
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (scrollbarRenderer.isDragging() && button == 0) {
            double maxScroll = getMaxScroll();
            if (maxScroll > 0) {
                int scrollbarY = y + headerAreaHeight + GRID_PADDING_TOP;
                int bottomMargin = 10;
                int scrollbarHeight = height - headerAreaHeight - GRID_PADDING_TOP - bottomMargin;
                double visibleRatio = maxVisibleRows * itemsPerRow / (double) filteredItems.size();

                double newOffset = scrollbarRenderer.handleMouseDrag(mouseY, scrollbarY, scrollbarHeight,
                        maxScroll, visibleRatio, 1.0);

                if (newOffset >= 0) {
                    scrollOffset = newOffset;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return scrollbarRenderer.handleMouseRelease(button);
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
    }
}
