package com.kingodogo.buildscape.client.screen.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ExistingItemsWidget extends AbstractWidget {
    private static final int ITEM_SIZE = 20;
    private static final int ITEM_SPACING = 2;
    private static final int SCROLLBAR_WIDTH = 10;
    private static final int HORIZONTAL_MARGIN = 10; // Left + right margins
    private static final int MAX_ITEMS_PER_ROW = 16; // Maximum columns to prevent lag
    private static final int MAX_VISIBLE_ROWS = 16; // Maximum rows to prevent lag
    
    private final Consumer<String> onItemRemoved;
    private final Predicate<String> isItemInConfig;
    private List<String> itemIds;
    private List<DisplayEntry> displayEntries;
    private double scrollOffset = 0;
    private int maxVisibleRows;
    private int itemsPerRow; // Dynamically calculated based on width
    private long lastUpdateTime = 0;
    private static final long CYCLE_INTERVAL = 1000; // 1 second per item in tag
    private boolean isDraggingScrollbar = false;
    private double scrollbarDragStartY = 0;
    private double scrollbarDragStartOffset = 0;
    
    public ExistingItemsWidget(int x, int y, int width, int height,
                               List<String> itemIds,
                               Consumer<String> onItemRemoved,
                               Predicate<String> isItemInConfig) {
        super(x, y, width, height, net.minecraft.network.chat.TextComponent.EMPTY);
        this.onItemRemoved = onItemRemoved;
        this.isItemInConfig = isItemInConfig;
        this.itemIds = new ArrayList<>(itemIds);
        
        // Calculate items per row based on available width
        calculateItemsPerRow();
        // Limit max visible rows to prevent lag
        this.maxVisibleRows = Math.min(MAX_VISIBLE_ROWS, (height - 5) / (ITEM_SIZE + ITEM_SPACING));
        updateDisplayEntries();
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
        updateDisplayEntries(); // Refresh display when resized
    }
    
    // Allow height to be set and trigger recalculation
    public void setHeight(int height) {
        try {
            java.lang.reflect.Field heightField = AbstractWidget.class.getDeclaredField("height");
            heightField.setAccessible(true);
            heightField.setInt(this, height);
            // Recalculate max visible rows when height changes
            this.maxVisibleRows = Math.min(MAX_VISIBLE_ROWS, (height - 5) / (ITEM_SIZE + ITEM_SPACING));
            updateDisplayEntries(); // Refresh display when resized
        } catch (Exception e) {
            // Fallback - just set via reflection without recalculation
        }
    }
    
    public void setItems(List<String> itemIds) {
        this.itemIds = new ArrayList<>(itemIds);
        updateDisplayEntries();
    }
    
    private void updateDisplayEntries() {
        displayEntries = new ArrayList<>();
        for (String itemId : itemIds) {
            if (itemId.startsWith("#")) {
                // This is a tag - create a tag entry
                String tagString = itemId.substring(1);
                try {
                    // Parse namespace:path format
                    String[] parts = tagString.split(":", 2);
                    ResourceLocation tagLocation;
                    if (parts.length == 2) {
                        tagLocation = new ResourceLocation(parts[0], parts[1]);
                    } else {
                        tagLocation = new ResourceLocation("minecraft", tagString);
                    }
                    TagKey<Item> tagKey = TagKey.create(Registry.ITEM_REGISTRY, tagLocation);
                    displayEntries.add(new TagDisplayEntry(itemId, tagKey));
                } catch (Exception e) {
                    // Invalid tag, skip it
                }
            } else {
                // Regular item
                try {
                    // Parse namespace:path format
                    String[] parts = itemId.split(":", 2);
                    ResourceLocation itemLocation;
                    if (parts.length == 2) {
                        itemLocation = new ResourceLocation(parts[0], parts[1]);
                    } else {
                        itemLocation = new ResourceLocation("minecraft", itemId);
                    }
                    Item item = ForgeRegistries.ITEMS.getValue(itemLocation);
                    if (item != null) {
                        displayEntries.add(new ItemDisplayEntry(itemId, item));
                    }
                } catch (Exception e) {
                    // Invalid item ID, skip it
                }
            }
        }
    }
    
    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        // Recalculate items per row in case width changed (e.g., GUI scale)
        calculateItemsPerRow();
        // Recalculate and cap max visible rows to prevent lag
        int labelHeight = 20; // Height for label + padding
        int availableHeight = height - labelHeight;
        maxVisibleRows = Math.min(MAX_VISIBLE_ROWS, availableHeight / (ITEM_SIZE + ITEM_SPACING));
        
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
        
        // Background removed - transparent
        
        // Title removed - it's rendered by parent tab as "Pillar items"
        
        // Update tag cycling
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTime >= CYCLE_INTERVAL) {
            lastUpdateTime = currentTime;
            // Trigger re-render for tag entries
        }
        
        // Ensure display entries are up to date
        if (displayEntries == null || displayEntries.isEmpty()) {
            updateDisplayEntries();
        }
        
        // Calculate visible items
        int startRow = (int) scrollOffset;
        int endRow = Math.min(startRow + maxVisibleRows, 
                            (int) Math.ceil((double) displayEntries.size() / itemsPerRow));
        
        // Start rendering items after the "Pillar items" label (label is ~20px tall with padding)
        int itemY = y + labelHeight; // Start rendering items after label (labelHeight already declared above)
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        DisplayEntry hoveredEntry = null;
        boolean hoveringRemoveButton = false;
        int hoveredRemoveX = 0, hoveredRemoveY = 0;
        
        for (int row = startRow; row < endRow; row++) {
            int rowY = itemY + (row - startRow) * (ITEM_SIZE + ITEM_SPACING);
            
            // Skip rows that are completely outside the widget bounds
            if (rowY + ITEM_SIZE < y || rowY > y + height) {
                continue;
            }
            
            for (int col = 0; col < itemsPerRow; col++) {
                int index = row * itemsPerRow + col;
                if (index >= displayEntries.size()) break;
                
                DisplayEntry entry = displayEntries.get(index);
                int itemX = x + 5 + col * (ITEM_SIZE + ITEM_SPACING);
                
                // Skip items that are outside the widget bounds
                if (itemX + ITEM_SIZE < x || itemX > x + width) {
                    continue;
                }
                
                // Get the current item to display (for tags, this cycles)
                Item itemToDisplay = entry.getCurrentItem();
                
                // Render remove button (small X in corner)
                int removeSize = 8;
                int removeX = itemX + ITEM_SIZE - removeSize;
                int removeY = rowY;
                boolean removeHovered = mouseX >= removeX && mouseX <= itemX + ITEM_SIZE &&
                                     mouseY >= removeY && mouseY <= removeY + removeSize &&
                                     mouseX >= x && mouseX < x + width &&
                                     mouseY >= y && mouseY < y + height;
                
                // Check if hovering over item (but not remove button)
                boolean itemHovered = mouseX >= itemX && mouseX < itemX + ITEM_SIZE &&
                                    mouseY >= rowY && mouseY < rowY + ITEM_SIZE &&
                                    !removeHovered &&
                                    mouseX >= x && mouseX < x + width &&
                                    mouseY >= y && mouseY < y + height;
                
                if (itemHovered) {
                    hoveredEntry = entry;
                }
                if (removeHovered) {
                    hoveringRemoveButton = true;
                    hoveredRemoveX = removeX;
                    hoveredRemoveY = removeY;
                }
                
                // Render item slot background
                int bgColor = itemHovered ? 0x40CCCCCC : 0x33CCCCCC;
                fill(poseStack, itemX, rowY, itemX + ITEM_SIZE, rowY + ITEM_SIZE, bgColor);
                
                // Render item - scales naturally with GUI scale
                if (itemToDisplay != null) {
                    poseStack.pushPose();
                    poseStack.translate(0, 0, 100); // Lower z-level for items
                    ItemStack stack = new ItemStack(itemToDisplay);
                    itemRenderer.renderGuiItem(stack, itemX + 2, rowY + 2);
                    itemRenderer.renderGuiItemDecorations(
                        mc.font, stack, itemX + 2, rowY + 2
                    );
                    poseStack.popPose();
                }
                
                // Render remove button - gray instead of red
                fill(poseStack, removeX, removeY, itemX + ITEM_SIZE, removeY + removeSize,
                     removeHovered ? 0x40CCCCCC : 0x33CCCCCC);
                // Draw X
                Minecraft.getInstance().font.draw(
                    poseStack, "×", removeX + 2, removeY - 1, 0xFFFFFF
                );
            }
        }
        
        // Disable scissor before rendering scrollbar
        RenderSystem.disableScissor();
        poseStack.popPose();
        
        // Render scrollbar if needed - positioned to align with items
        double maxScroll = Math.max(0, Math.ceil((double) displayEntries.size() / itemsPerRow) - maxVisibleRows);
        if (maxScroll > 0) {
            int scrollbarX = x + width - 10;
            // Scrollbar should start where items start and match items height
            int scrollbarY = itemY; // Align with items
            int scrollbarHeight = maxVisibleRows * (ITEM_SIZE + ITEM_SPACING); // Match items area height
            
            double scrollRatio = maxScroll > 0 ? scrollOffset / maxScroll : 0;
            int totalRows = (int) Math.ceil((double) displayEntries.size() / itemsPerRow);
            int thumbHeight = (int) (scrollbarHeight * (maxVisibleRows / (double) totalRows));
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
        
        // Calculate visible items to check hover (must match renderButton positions exactly)
        int labelHeight = 20;
        int startRow = (int) scrollOffset;
        int endRow = Math.min(startRow + maxVisibleRows, 
                            (int) Math.ceil((double) displayEntries.size() / itemsPerRow));
        
        int itemY = y + labelHeight; // Match renderButton itemY exactly
        DisplayEntry hoveredEntry = null;
        boolean hoveringRemoveButton = false;
        int hoveredRemoveX = 0, hoveredRemoveY = 0;
        
        for (int row = startRow; row < endRow; row++) {
            int rowY = itemY + (row - startRow) * (ITEM_SIZE + ITEM_SPACING);
            
            // Skip rows that are completely outside the widget bounds
            if (rowY + ITEM_SIZE < y || rowY > y + height) {
                continue;
            }
            
            for (int col = 0; col < itemsPerRow; col++) {
                int index = row * itemsPerRow + col;
                if (index >= displayEntries.size()) break;
                
                DisplayEntry entry = displayEntries.get(index);
                int itemX = x + 5 + col * (ITEM_SIZE + ITEM_SPACING);
                
                // Skip items that are outside the widget bounds
                if (itemX + ITEM_SIZE < x || itemX > x + width) {
                    continue;
                }
                
                // Render remove button (small X in corner)
                int removeSize = 8;
                int removeX = itemX + ITEM_SIZE - removeSize;
                int removeY = rowY;
                boolean removeHovered = mouseX >= removeX && mouseX <= itemX + ITEM_SIZE &&
                                     mouseY >= removeY && mouseY <= removeY + removeSize &&
                                     mouseX >= x && mouseX < x + width &&
                                     mouseY >= y && mouseY < y + height;
                
                // Check if hovering over item (but not remove button) - must match renderButton exactly
                boolean itemHovered = mouseX >= itemX && mouseX < itemX + ITEM_SIZE &&
                                    mouseY >= rowY && mouseY < rowY + ITEM_SIZE &&
                                    !removeHovered &&
                                    mouseX >= x && mouseX < x + width &&
                                    mouseY >= y && mouseY < y + height;
                
                if (itemHovered) {
                    hoveredEntry = entry;
                    // Don't break - continue to find the last (topmost) hovered item
                }
                if (removeHovered) {
                    hoveringRemoveButton = true;
                    hoveredRemoveX = removeX;
                    hoveredRemoveY = removeY;
                }
            }
        }
        
        // Render tooltip for hovered item using Minecraft's standard tooltip rendering
        if (hoveredEntry != null && hoveredEntry.getCurrentItem() != null) {
            Minecraft mc = Minecraft.getInstance();
            
            // Use Minecraft's standard tooltip rendering which handles z-ordering automatically
            if (mc.screen != null) {
                // For tags, show tag name; for items, use standard ItemStack tooltip
                if (hoveredEntry.getItemId().startsWith("#")) {
                    // For tags, render a simple text tooltip
                    net.minecraft.network.chat.Component tooltip = new net.minecraft.network.chat.TextComponent(hoveredEntry.getItemId());
                    mc.screen.renderTooltip(poseStack, tooltip, mouseX, mouseY);
                } else {
                    // For items, get tooltip lines and convert to FormattedCharSequence
                    ItemStack stack = new ItemStack(hoveredEntry.getCurrentItem());
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
        }
        
        // Render tooltip for remove button using Minecraft's standard tooltip rendering
        if (hoveringRemoveButton) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.screen != null) {
                net.minecraft.network.chat.Component tooltip = new net.minecraft.network.chat.TranslatableComponent("buildscape.config.remove");
                // Render using Screen's renderTooltip which handles all the depth/z-ordering
                mc.screen.renderTooltip(poseStack, tooltip, hoveredRemoveX, hoveredRemoveY);
            }
        }
        
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isMouseOver(mouseX, mouseY)) {
            return false;
        }
        
        // Check if clicking on remove button
        int startRow = (int) scrollOffset;
        int endRow = Math.min(startRow + maxVisibleRows, 
                            (int) Math.ceil((double) displayEntries.size() / itemsPerRow));
        
        int labelHeight = 20;
        int itemY = y + labelHeight; // Match the render position
        
        // Check if clicking on scrollbar (like creative inventory)
        double maxScroll = Math.max(0, Math.ceil((double) displayEntries.size() / itemsPerRow) - maxVisibleRows);
        if (maxScroll > 0) {
            int scrollbarX = x + width - 10;
            int scrollbarY = itemY;
            int scrollbarHeight = maxVisibleRows * (ITEM_SIZE + ITEM_SPACING);
            
            if (mouseX >= scrollbarX && mouseX <= scrollbarX + 5 &&
                mouseY >= scrollbarY && mouseY <= scrollbarY + scrollbarHeight) {
                // Clicked on scrollbar - start dragging
                isDraggingScrollbar = true;
                scrollbarDragStartY = mouseY;
                scrollbarDragStartOffset = scrollOffset;
                
                // Calculate thumb position and check if clicking on thumb or track
                int totalRows = (int) Math.ceil((double) displayEntries.size() / itemsPerRow);
                int thumbHeight = (int) (scrollbarHeight * (maxVisibleRows / (double) totalRows));
                thumbHeight = Math.max(20, thumbHeight);
                double scrollRatio = maxScroll > 0 ? scrollOffset / maxScroll : 0;
                int thumbY = scrollbarY + (int) (scrollRatio * (scrollbarHeight - thumbHeight));
                
                if (mouseY >= thumbY && mouseY <= thumbY + thumbHeight) {
                    // Clicked on thumb - drag from current position
                    // scrollbarDragStartOffset already set to current scrollOffset
                    return true;
                } else {
                    // Clicked on track - jump to that position and allow dragging from there
                    double clickRatio = (mouseY - scrollbarY) / (double) scrollbarHeight;
                    scrollOffset = Math.max(0, Math.min(maxScroll, clickRatio * maxScroll));
                    // Update drag start to new position so dragging continues smoothly
                    scrollbarDragStartOffset = scrollOffset;
                    scrollbarDragStartY = mouseY;
                    return true;
                }
            }
        }
        
        for (int row = startRow; row < endRow; row++) {
            int rowY = itemY + (row - startRow) * (ITEM_SIZE + ITEM_SPACING);
            
            for (int col = 0; col < itemsPerRow; col++) {
                int index = row * itemsPerRow + col;
                if (index >= displayEntries.size()) break;
                
                int itemX = x + 5 + col * (ITEM_SIZE + ITEM_SPACING);
                int removeSize = 8;
                int removeX = itemX + ITEM_SIZE - removeSize;
                int removeY = rowY; // Same as render position
                
                if (mouseX >= removeX && mouseX <= itemX + ITEM_SIZE &&
                    mouseY >= removeY && mouseY <= removeY + removeSize) {
                    DisplayEntry entry = displayEntries.get(index);
                    onItemRemoved.accept(entry.getItemId());
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
        
        double maxScroll = Math.max(0, Math.ceil((double) displayEntries.size() / itemsPerRow) - maxVisibleRows);
        scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset - delta));
        return true;
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        // Handle scrollbar dragging (like creative inventory) - works even if mouse leaves widget
        if (isDraggingScrollbar && button == 0) {
            double maxScroll = Math.max(0, Math.ceil((double) displayEntries.size() / itemsPerRow) - maxVisibleRows);
            if (maxScroll > 0) {
                int labelHeight = 20;
                int itemY = y + labelHeight;
                int scrollbarY = itemY;
                int scrollbarHeight = maxVisibleRows * (ITEM_SIZE + ITEM_SPACING);
                
                // Calculate thumb height for accurate dragging
                int totalRows = (int) Math.ceil((double) displayEntries.size() / itemsPerRow);
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
    
    // Base class for display entries
    private abstract static class DisplayEntry {
        protected final String itemId;
        
        public DisplayEntry(String itemId) {
            this.itemId = itemId;
        }
        
        public String getItemId() {
            return itemId;
        }
        
        public abstract Item getCurrentItem();
    }
    
    // Regular item entry
    private static class ItemDisplayEntry extends DisplayEntry {
        private final Item item;
        
        public ItemDisplayEntry(String itemId, Item item) {
            super(itemId);
            this.item = item;
        }
        
        @Override
        public Item getCurrentItem() {
            return item;
        }
    }
    
    // Tag entry that cycles through items
    private static class TagDisplayEntry extends DisplayEntry {
        private final TagKey<Item> tagKey;
        private List<Item> tagItems;
        private int currentIndex = 0;
        private long lastCycleTime = 0;
        
        public TagDisplayEntry(String itemId, TagKey<Item> tagKey) {
            super(itemId);
            this.tagKey = tagKey;
            loadTagItems();
        }
        
        private void loadTagItems() {
            tagItems = new ArrayList<>();
            ForgeRegistries.ITEMS.getValues().forEach(item -> {
                if (item.builtInRegistryHolder().is(tagKey)) {
                    tagItems.add(item);
                }
            });
        }
        
        @Override
        public Item getCurrentItem() {
            if (tagItems.isEmpty()) {
                return null;
            }
            
            // Cycle through items every second
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastCycleTime >= 1000) {
                lastCycleTime = currentTime;
                currentIndex = (currentIndex + 1) % tagItems.size();
            }
            
            return tagItems.get(currentIndex);
        }
    }
}

