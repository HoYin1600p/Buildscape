package com.kingodogo.buildscape.client.screen.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ItemSelectionWidget extends AbstractWidget {
    private static final int ITEM_SIZE = 20;
    private static final int ITEM_SPACING = 2;
    private static final int SCROLLBAR_WIDTH = 10;
    private static final int HORIZONTAL_MARGIN = 10; // Left + right margins
    private static final int MAX_ITEMS_PER_ROW = 16; // Maximum columns to prevent lag
    private static final int MAX_VISIBLE_ROWS = 16; // Maximum rows to prevent lag
    
    private final Consumer<String> onItemSelected;
    private final Predicate<String> isItemInConfig;
    private final List<Item> allItems;
    private List<Item> filteredItems;
    private String filter = "";
    private double scrollOffset = 0;
    private int maxVisibleRows;
    private int itemsPerRow; // Dynamically calculated based on width
    private SortToggleButton.SortType sortMode = SortToggleButton.SortType.ALL_ITEMS;
    private String currentModNamespace = "buildscape"; // For mod cycling
    private boolean isDraggingScrollbar = false;
    private double scrollbarDragStartY = 0;
    private double scrollbarDragStartOffset = 0;
    
    public ItemSelectionWidget(int x, int y, int width, int height, 
                              Consumer<String> onItemSelected,
                              Predicate<String> isItemInConfig) {
        super(x, y, width, height, net.minecraft.network.chat.TextComponent.EMPTY);
        this.onItemSelected = onItemSelected;
        this.isItemInConfig = isItemInConfig;
        
        // Load all items
        allItems = new ArrayList<>(ForgeRegistries.ITEMS.getValues());
        filteredItems = new ArrayList<>(allItems);
        
        // Calculate items per row based on available width
        calculateItemsPerRow();
        // Limit max visible rows to prevent lag
        maxVisibleRows = Math.min(MAX_VISIBLE_ROWS, (height - 5) / (ITEM_SIZE + ITEM_SPACING));
    }
    
    private void calculateItemsPerRow() {
        // Available width = total width - scrollbar - margins
        int availableWidth = width - SCROLLBAR_WIDTH - HORIZONTAL_MARGIN;
        // Calculate how many items fit: (availableWidth + spacing) / (itemSize + spacing)
        // Cap at MAX_ITEMS_PER_ROW to prevent lag on very low GUI scales
        itemsPerRow = Math.max(1, Math.min(MAX_ITEMS_PER_ROW, (availableWidth + ITEM_SPACING) / (ITEM_SIZE + ITEM_SPACING)));
    }
    
    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        calculateItemsPerRow(); // Recalculate when width changes
        refresh(); // Refresh display when resized
    }
    
    // Allow height to be set and trigger recalculation
    public void setHeight(int height) {
        try {
            java.lang.reflect.Field heightField = AbstractWidget.class.getDeclaredField("height");
            heightField.setAccessible(true);
            heightField.setInt(this, height);
            // Recalculate max visible rows when height changes
            this.maxVisibleRows = Math.min(MAX_VISIBLE_ROWS, (height - 5) / (ITEM_SIZE + ITEM_SPACING));
            refresh(); // Refresh display when resized
        } catch (Exception e) {
            // Fallback - just set via reflection without recalculation
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
        
        // Apply sort mode filter
        switch (sortMode) {
            case INVENTORY:
                // Show only items in player's inventory
                itemsToFilter = getInventoryItems();
                break;
            case MOD_ONLY:
                // Show only items from current mod namespace
                itemsToFilter = allItems.stream()
                    .filter(item -> {
                        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(item);
                        return itemId != null && itemId.getNamespace().equals(currentModNamespace);
                    })
                    .collect(Collectors.toList());
                break;
            case ALL_ITEMS:
            default:
                // Show all items
                itemsToFilter = allItems;
                break;
        }
        
        // Apply text filter
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
        scrollOffset = Math.max(0, Math.min(scrollOffset, getMaxScroll()));
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
        int totalRows = (int) Math.ceil((double) filteredItems.size() / itemsPerRow);
        int visibleHeight = maxVisibleRows * (ITEM_SIZE + ITEM_SPACING);
        int contentHeight = totalRows * (ITEM_SIZE + ITEM_SPACING);
        return Math.max(0, contentHeight - visibleHeight);
    }
    
    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        // Recalculate items per row in case width changed (e.g., GUI scale)
        calculateItemsPerRow();
        // Recalculate and cap max visible rows to prevent lag
        maxVisibleRows = Math.min(MAX_VISIBLE_ROWS, (height - 5) / (ITEM_SIZE + ITEM_SPACING));
        
        // Enable scissor to clip items to widget bounds
        poseStack.pushPose();
        Minecraft mc = Minecraft.getInstance();
        double guiScale = mc.getWindow().getGuiScale();
        int windowHeight = mc.getWindow().getHeight();
        int scissorX = (int)(x * guiScale);
        int scissorY = (int)(windowHeight - (y + height) * guiScale);
        int scissorWidth = (int)(width * guiScale);
        int scissorHeight = (int)(height * guiScale);
        RenderSystem.enableScissor(scissorX, scissorY, scissorWidth, scissorHeight);
        
        // Background removed - too large
        
        // Title is now rendered by the parent tab, not here
        
        // Calculate visible items
        int totalRows = (int) Math.ceil((double) filteredItems.size() / itemsPerRow);
        // Calculate start row based on scroll offset (pixel-based scrolling)
        int startRow = (int) Math.floor(scrollOffset / (ITEM_SIZE + ITEM_SPACING));
        // Add extra row for smooth scrolling (partial row visibility)
        int endRow = Math.min(startRow + maxVisibleRows + 2, totalRows);
        
        // Calculate pixel offset within the first visible row for smooth scrolling
        double pixelOffsetInRow = scrollOffset % (ITEM_SIZE + ITEM_SPACING);
        int itemY = y + 5 - (int) pixelOffsetInRow; // Offset by pixel scroll so rows slide smoothly
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        Item hoveredItem = null;
        
        for (int row = startRow; row < endRow; row++) {
            int rowY = itemY + (row - startRow) * (ITEM_SIZE + ITEM_SPACING);
            
            // Skip rows that are completely outside the widget bounds
            if (rowY + ITEM_SIZE < y || rowY > y + height) {
                continue;
            }
            
            for (int col = 0; col < itemsPerRow; col++) {
                int index = row * itemsPerRow + col;
                if (index >= filteredItems.size()) break;
                
                Item item = filteredItems.get(index);
                int itemX = x + 5 + col * (ITEM_SIZE + ITEM_SPACING);
                
                // Skip items that are outside the widget bounds
                if (itemX + ITEM_SIZE < x || itemX > x + width) {
                    continue;
                }
                
                // Check if hovering over this item
                boolean isHovered = mouseX >= itemX && mouseX < itemX + ITEM_SIZE &&
                                 mouseY >= rowY && mouseY < rowY + ITEM_SIZE &&
                                 mouseX >= x && mouseX < x + width &&
                                 mouseY >= y && mouseY < y + height;
                if (isHovered) {
                    hoveredItem = item;
                }
                
                // Check if item is in config
                String itemId = ForgeRegistries.ITEMS.getKey(item).toString();
                boolean inConfig = isItemInConfig.test(itemId);
                
                // Render item slot background - highlight selected items
                int bgColor;
                if (inConfig) {
                    // Highlight selected items with a green tint
                    bgColor = isHovered ? 0x6000FF00 : 0x4000FF00;
                } else {
                    bgColor = isHovered ? 0x40CCCCCC : 0x33CCCCCC;
                }
                fill(poseStack, itemX, rowY, itemX + ITEM_SIZE, rowY + ITEM_SIZE, bgColor);
                
                // Render border for selected items
                if (inConfig) {
                    int borderColor = 0xFF00FF00; // Green border
                    fill(poseStack, itemX - 1, rowY - 1, itemX + ITEM_SIZE + 1, rowY, borderColor);
                    fill(poseStack, itemX - 1, rowY + ITEM_SIZE, itemX + ITEM_SIZE + 1, rowY + ITEM_SIZE + 1, borderColor);
                    fill(poseStack, itemX - 1, rowY - 1, itemX, rowY + ITEM_SIZE + 1, borderColor);
                    fill(poseStack, itemX + ITEM_SIZE, rowY - 1, itemX + ITEM_SIZE + 1, rowY + ITEM_SIZE + 1, borderColor);
                }
                
                // Render item - scales naturally with GUI scale
                poseStack.pushPose();
                poseStack.translate(0, 0, 100); // Lower z-level for items
                ItemStack stack = new ItemStack(item);
                itemRenderer.renderGuiItem(stack, itemX + 2, rowY + 2);
                itemRenderer.renderGuiItemDecorations(
                    mc.font, stack, itemX + 2, rowY + 2
                );
                poseStack.popPose();
            }
        }
        
        // Disable scissor before rendering scrollbar
        RenderSystem.disableScissor();
        poseStack.popPose();
        
        // Render scrollbar if needed
        if (getMaxScroll() > 0) {
            int scrollbarX = x + width - 10;
            int scrollbarHeight = height - 5;
            int scrollbarY = y + 5;
            
            double scrollRatio = scrollOffset / getMaxScroll();
            int thumbHeight = (int) (scrollbarHeight * (maxVisibleRows / Math.ceil((double) filteredItems.size() / itemsPerRow)));
            thumbHeight = Math.max(20, thumbHeight);
            int thumbY = scrollbarY + (int) (scrollRatio * (scrollbarHeight - thumbHeight));
            
            fill(poseStack, scrollbarX, scrollbarY, scrollbarX + 5, scrollbarY + scrollbarHeight, 0x33CCCCCC);
            fill(poseStack, scrollbarX, thumbY, scrollbarX + 5, thumbY + thumbHeight, 0x40CCCCCC);
        }
    }
    
    // Render tooltip separately after all items to ensure it's on top
    public void renderTooltip(PoseStack poseStack, int mouseX, int mouseY) {
        // First check if mouse is within widget bounds
        if (mouseX < x || mouseX >= x + width || mouseY < y || mouseY >= y + height) {
            return;
        }
        
        // Calculate visible items to check hover - must match renderButton exactly
        int totalRows = (int) Math.ceil((double) filteredItems.size() / itemsPerRow);
        // Calculate start row based on scroll offset (pixel-based scrolling) - must match renderButton
        int startRow = (int) Math.floor(scrollOffset / (ITEM_SIZE + ITEM_SPACING));
        // Add extra row for smooth scrolling (partial row visibility) - must match renderButton
        int endRow = Math.min(startRow + maxVisibleRows + 2, totalRows);
        
        // Calculate pixel offset within the first visible row for smooth scrolling - must match renderButton
        double pixelOffsetInRow = scrollOffset % (ITEM_SIZE + ITEM_SPACING);
        int itemY = y + 5 - (int) pixelOffsetInRow; // Match rendering position with pixel scroll
        Item hoveredItem = null;
        
        // Check items in reverse order (top to bottom, left to right) to get the topmost item
        for (int row = startRow; row < endRow; row++) {
            int rowY = itemY + (row - startRow) * (ITEM_SIZE + ITEM_SPACING);
            
            // Skip rows that are completely outside the widget bounds (with margin for partial visibility)
            if (rowY + ITEM_SIZE < y - 5 || rowY > y + height + 5) {
                continue;
            }
            
            for (int col = 0; col < itemsPerRow; col++) {
                int index = row * itemsPerRow + col;
                if (index >= filteredItems.size()) break;
                
                Item item = filteredItems.get(index);
                int itemX = x + 5 + col * (ITEM_SIZE + ITEM_SPACING);
                
                // Skip items that are outside the widget bounds
                if (itemX + ITEM_SIZE < x || itemX > x + width) {
                    continue;
                }
                
                // Check if hovering over this item - must match renderButton bounds check exactly
                boolean isHovered = mouseX >= itemX && mouseX < itemX + ITEM_SIZE &&
                                 mouseY >= rowY && mouseY < rowY + ITEM_SIZE &&
                                 mouseX >= x && mouseX < x + width &&
                                 mouseY >= y && mouseY < y + height;
                if (isHovered) {
                    hoveredItem = item;
                    // Don't break - continue to find the last (topmost) hovered item
                }
            }
        }
        
        // Render tooltip for hovered item using Minecraft's standard tooltip rendering
        if (hoveredItem != null) {
            ItemStack stack = new ItemStack(hoveredItem);
            Minecraft mc = Minecraft.getInstance();
            
            // Use Minecraft's standard tooltip rendering which handles z-ordering automatically
            // This ensures tooltips render correctly above items like in vanilla inventory
            if (mc.screen != null) {
                // Get tooltip lines and convert to FormattedCharSequence for public renderTooltip method
                java.util.List<net.minecraft.network.chat.Component> tooltipLines = 
                    stack.getTooltipLines(null, net.minecraft.world.item.TooltipFlag.Default.NORMAL);
                
                // Convert Components to FormattedCharSequence
                java.util.List<net.minecraft.util.FormattedCharSequence> formattedLines = new java.util.ArrayList<>();
                int maxWidth = Math.max(200, mc.screen.width / 2);
                for (net.minecraft.network.chat.Component line : tooltipLines) {
                    formattedLines.addAll(mc.font.split(line, maxWidth));
                }
                
                // Render using Screen's public renderTooltip method
                mc.screen.renderTooltip(poseStack, formattedLines, mouseX, mouseY);
            }
        }
        
        // Render scrollbar if needed
        if (getMaxScroll() > 0) {
            int scrollbarX = x + width - 10;
            int scrollbarHeight = height - 5;
            int scrollbarY = y + 5;
            
            double scrollRatio = scrollOffset / getMaxScroll();
            int thumbHeight = (int) (scrollbarHeight * (maxVisibleRows / Math.ceil((double) filteredItems.size() / itemsPerRow)));
            thumbHeight = Math.max(20, thumbHeight);
            int thumbY = scrollbarY + (int) (scrollRatio * (scrollbarHeight - thumbHeight));
            
            fill(poseStack, scrollbarX, scrollbarY, scrollbarX + 5, scrollbarY + scrollbarHeight, 0x33CCCCCC);
            fill(poseStack, scrollbarX, thumbY, scrollbarX + 5, thumbY + thumbHeight, 0x40CCCCCC);
        }
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isMouseOver(mouseX, mouseY)) {
            return false;
        }
        
        // Check if clicking on scrollbar (like creative inventory)
        double maxScroll = getMaxScroll();
        if (maxScroll > 0) {
            int scrollbarX = x + width - 10;
            int scrollbarY = y + 5;
            int scrollbarHeight = height - 5;
            
            if (mouseX >= scrollbarX && mouseX <= scrollbarX + 5 &&
                mouseY >= scrollbarY && mouseY <= scrollbarY + scrollbarHeight) {
                // Clicked on scrollbar - start dragging
                isDraggingScrollbar = true;
                scrollbarDragStartY = mouseY;
                scrollbarDragStartOffset = scrollOffset;
                
                // Calculate thumb position and check if clicking on thumb or track
                int totalRows = (int) Math.ceil((double) filteredItems.size() / itemsPerRow);
                int thumbHeight = (int) (scrollbarHeight * (maxVisibleRows / (double) totalRows));
                thumbHeight = Math.max(20, thumbHeight);
                double scrollRatio = maxScroll > 0 ? scrollOffset / maxScroll : 0;
                int thumbY = scrollbarY + (int) (scrollRatio * (scrollbarHeight - thumbHeight));
                
                if (mouseY >= thumbY && mouseY <= thumbY + thumbHeight) {
                    // Clicked on thumb - drag from current position
                    return true;
                } else {
                    // Clicked on track - jump to that position and allow dragging from there
                    double clickRatio = (mouseY - scrollbarY) / (double) scrollbarHeight;
                    scrollOffset = Math.max(0, Math.min(maxScroll, clickRatio * maxScroll));
                    scrollbarDragStartOffset = scrollOffset;
                    scrollbarDragStartY = mouseY;
                    return true;
                }
            }
        }
        
        // Check if clicking on an item - must match renderButton calculation exactly
        int totalRows = (int) Math.ceil((double) filteredItems.size() / itemsPerRow);
        // Calculate start row based on scroll offset (pixel-based scrolling) - must match renderButton
        int startRow = (int) Math.floor(scrollOffset / (ITEM_SIZE + ITEM_SPACING));
        // Add extra row for smooth scrolling (partial row visibility) - must match renderButton
        int endRow = Math.min(startRow + maxVisibleRows + 2, totalRows);
        
        // Calculate pixel offset within the first visible row for smooth scrolling - must match renderButton
        double pixelOffsetInRow = scrollOffset % (ITEM_SIZE + ITEM_SPACING);
        int itemY = y + 5 - (int) pixelOffsetInRow; // Match the rendering position
        
        for (int row = startRow; row < endRow; row++) {
            int rowY = itemY + (row - startRow) * (ITEM_SIZE + ITEM_SPACING);
            
            // Skip rows that are completely outside the widget bounds
            if (rowY + ITEM_SIZE < y - 5 || rowY > y + height + 5) {
                continue;
            }
            
            for (int col = 0; col < itemsPerRow; col++) {
                int index = row * itemsPerRow + col;
                if (index >= filteredItems.size()) break;
                
                int itemX = x + 5 + col * (ITEM_SIZE + ITEM_SPACING);
                
                // Skip items that are outside the widget bounds
                if (itemX + ITEM_SIZE < x || itemX > x + width) {
                    continue;
                }
                
                if (mouseX >= itemX && mouseX < itemX + ITEM_SIZE &&
                    mouseY >= rowY && mouseY < rowY + ITEM_SIZE &&
                    mouseX >= x && mouseX < x + width &&
                    mouseY >= y && mouseY < y + height) {
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
        
        // Scroll by one row worth of pixels per wheel step
        double step = (ITEM_SIZE + ITEM_SPACING);
        scrollOffset = Math.max(0, Math.min(getMaxScroll(), scrollOffset - delta * step));
        return true;
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        // Handle scrollbar dragging (like creative inventory) - works even if mouse leaves widget
        if (isDraggingScrollbar && button == 0) {
            double maxScroll = getMaxScroll();
            if (maxScroll > 0) {
                int scrollbarY = y + 5;
                int scrollbarHeight = height - 5;
                
                // Calculate thumb height for accurate dragging
                int totalRows = (int) Math.ceil((double) filteredItems.size() / itemsPerRow);
                int thumbHeight = (int) (scrollbarHeight * (maxVisibleRows / (double) totalRows));
                thumbHeight = Math.max(20, thumbHeight);
                
                // Calculate the usable scrollbar track height (total height minus thumb height)
                int usableTrackHeight = scrollbarHeight - thumbHeight;
                
                // Map mouse Y position to scroll position
                // Clamp mouse Y to scrollbar bounds for smooth dragging
                double clampedMouseY = Math.max(scrollbarY, Math.min(scrollbarY + scrollbarHeight, mouseY));
                double mouseYRelative = clampedMouseY - scrollbarY;
                
                // Calculate scroll ratio (0.0 to 1.0) based on where the thumb center should be
                double thumbCenterRatio = usableTrackHeight > 0 ? 
                    Math.max(0, Math.min(1, (mouseYRelative - thumbHeight / 2.0) / usableTrackHeight)) : 0;
                
                // Convert ratio to scroll offset
                scrollOffset = thumbCenterRatio * maxScroll;
                
                // Clamp to valid range
                scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset));
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (isDraggingScrollbar && button == 0) {
            isDraggingScrollbar = false;
            return true;
        }
        return false;
    }
    
    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
        // Not needed for this widget
    }
}

