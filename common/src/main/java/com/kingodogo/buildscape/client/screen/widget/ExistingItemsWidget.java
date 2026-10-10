package com.kingodogo.buildscape.client.screen.widget;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import com.kingodogo.buildscape.util.ComponentHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ExistingItemsWidget implements ICustomWidget {
    private static final int ITEM_SIZE = 20;
    private static final int ITEM_SPACING = 2;
    private static final int SCROLLBAR_WIDTH = 10;
    private static final int HORIZONTAL_MARGIN = 10;
    private static final int MAX_ITEMS_PER_ROW = 16;
    private static final int MAX_VISIBLE_ROWS = 16;

    public int x;
    public int y;
    public int width;
    public int height;
    public boolean visible = true;
    public boolean active = true;
    private boolean focused = false;

    private final Consumer<String> onItemRemoved;
    private final Predicate<String> isItemInConfig;
    private List<String> itemIds;
    private List<DisplayEntry> displayEntries;
    private double scrollOffset = 0;
    private int maxVisibleRows;
    private int itemsPerRow;
    private long lastUpdateTime = 0;
    private static final long CYCLE_INTERVAL = 1000;
    private int headerAreaHeight = 20;
    private static final int GRID_PADDING_TOP = 5;
    private final CustomScrollbarRenderer scrollbarRenderer = new CustomScrollbarRenderer();

    public ExistingItemsWidget(int x, int y, int width, int height,
            List<String> itemIds,
            Consumer<String> onItemRemoved,
            Predicate<String> isItemInConfig) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.onItemRemoved = onItemRemoved;
        this.isItemInConfig = isItemInConfig;
        this.itemIds = new ArrayList<>(itemIds);

        calculateItemsPerRow();
        this.maxVisibleRows = Math.min(MAX_VISIBLE_ROWS, (height - headerAreaHeight - GRID_PADDING_TOP - 5) / (ITEM_SIZE + ITEM_SPACING));
        updateDisplayEntries();
    }

    public void setHeaderAreaHeight(int height) {
        this.headerAreaHeight = height;
        updateDisplayEntries();
    }

    private void calculateItemsPerRow() {
        int availableWidth = width - 21;
        itemsPerRow = Math.max(1,
                Math.min(MAX_ITEMS_PER_ROW, (availableWidth + ITEM_SPACING) / (ITEM_SIZE + ITEM_SPACING)));
    }

    public void setWidth(int width) {
        this.width = width;
        calculateItemsPerRow();
        updateDisplayEntries();
    }

    public void setHeight(int height) {
        this.height = height;
        this.maxVisibleRows = Math.min(MAX_VISIBLE_ROWS, (height - headerAreaHeight - GRID_PADDING_TOP - 5) / (ITEM_SIZE + ITEM_SPACING));
        updateDisplayEntries();
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    @Override
    public boolean isFocused() {
        return focused;
    }

    @Override
    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public void setItems(List<String> itemIds) {
        this.itemIds = new ArrayList<>(itemIds);
        updateDisplayEntries();
    }

    private void updateDisplayEntries() {
        displayEntries = new ArrayList<>();
        for (String itemId : itemIds) {
            if (itemId.startsWith("#")) {
                String tagString = itemId.substring(1);
                try {
                    CommonId tagLoc = CommonId.tryParse(tagString);
                    if (tagLoc != null) {
                        TagKey<Item> tagKey = Services.PLATFORM.createItemTagKey(tagLoc);
                        displayEntries.add(new TagDisplayEntry(itemId, tagKey));
                    }
                } catch (Exception ignored) {
                }
            } else {
                try {
                    CommonId itemLoc = CommonId.tryParse(itemId);
                    if (itemLoc != null) {
                        Item item = Services.PLATFORM.getItem(itemLoc);
                        if (item != null) {
                            displayEntries.add(new ItemDisplayEntry(itemId, item));
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }

    @Override
    public void render(Object poseStackOrGraphics, int mouseX, int mouseY, float partialTick) {
        calculateItemsPerRow();
        int availableHeight = height - headerAreaHeight - GRID_PADDING_TOP;
        maxVisibleRows = Math.min(MAX_VISIBLE_ROWS, availableHeight / (ITEM_SIZE + ITEM_SPACING));

        Services.PLATFORM.pushGuiPose(poseStackOrGraphics);

        Minecraft mc = Minecraft.getInstance();
        double guiScale = mc.getWindow().getGuiScale();
        int windowHeight = mc.getWindow().getHeight();

        int scissorX = (int) (x * guiScale);
        int bottomMargin = 10;
        int scissorY = (int) (windowHeight - (y + height) * guiScale + bottomMargin * guiScale);
        int scissorWidth = (int) ((width - 21) * guiScale);

        int scissorHeight = (int) ((height - headerAreaHeight - 1 - bottomMargin) * guiScale);

        if (scissorHeight > 0 && scissorWidth > 0) {
            Services.PLATFORM.enableScissor(poseStackOrGraphics, scissorX, scissorY, scissorWidth, scissorHeight);
        }
        try {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastUpdateTime >= CYCLE_INTERVAL) {
                lastUpdateTime = currentTime;
            }

            if (displayEntries == null || displayEntries.isEmpty()) {
                updateDisplayEntries();
            }

            int totalRows = (int) Math.ceil((double) displayEntries.size() / itemsPerRow);
            int startRow = (int) Math.floor(scrollOffset / (ITEM_SIZE + ITEM_SPACING));
            int endRow = Math.min(startRow + maxVisibleRows + 2, totalRows);

            double pixelOffsetInRow = scrollOffset % (ITEM_SIZE + ITEM_SPACING);
            int itemY = y + headerAreaHeight + GRID_PADDING_TOP - (int) pixelOffsetInRow;

            for (int row = startRow; row < endRow; row++) {
                int rowY = itemY + (row - startRow) * (ITEM_SIZE + ITEM_SPACING);

                if (rowY + ITEM_SIZE < y + headerAreaHeight || rowY > y + height - bottomMargin) {
                    continue;
                }

                for (int col = 0; col < itemsPerRow; col++) {
                    int index = row * itemsPerRow + col;
                    if (index >= displayEntries.size())
                        break;

                    DisplayEntry entry = displayEntries.get(index);
                    int totalRowWidth = itemsPerRow * (ITEM_SIZE + ITEM_SPACING) - ITEM_SPACING;
                    int availableAreaWidth = width - 21;
                    int startXOffset = Math.max(0, (availableAreaWidth - totalRowWidth) / 2);

                    int itemX = x + 5 + startXOffset + col * (ITEM_SIZE + ITEM_SPACING);

                    if (itemX + ITEM_SIZE < x || itemX > x + width) {
                        continue;
                    }

                    Item itemToDisplay = entry.getCurrentItem();

                    int removeSize = 8;
                    int removeX = itemX + ITEM_SIZE - removeSize;
                    int removeY = rowY;
                    boolean removeHovered = mouseX >= removeX && mouseX <= itemX + ITEM_SIZE &&
                            mouseY >= removeY && mouseY <= removeY + removeSize &&
                            mouseX >= x && mouseX < x + width &&
                            mouseY >= y + headerAreaHeight + 1 && mouseY < y + height - bottomMargin;

                    boolean itemHovered = mouseX >= itemX && mouseX < itemX + ITEM_SIZE &&
                            mouseY >= rowY && mouseY < rowY + ITEM_SIZE &&
                            !removeHovered &&
                            mouseX >= x && mouseX < x + width &&
                            mouseY >= y + headerAreaHeight + 1 && mouseY < y + height - bottomMargin;

                    int bgColor = itemHovered ? 0x40CCCCCC : 0x33CCCCCC;
                    Services.PLATFORM.fill(poseStackOrGraphics, itemX, rowY, itemX + ITEM_SIZE, rowY + ITEM_SIZE, bgColor);

                    if (itemToDisplay != null) {
                        ItemStack stack = new ItemStack(itemToDisplay);
                        Services.PLATFORM.renderGuiItem(poseStackOrGraphics, stack, itemX + 2, rowY + 2);
                        Services.PLATFORM.renderGuiItemDecorations(poseStackOrGraphics, mc.font, stack, itemX + 2, rowY + 2);
                    }

                    Services.PLATFORM.fill(poseStackOrGraphics, removeX, removeY, itemX + ITEM_SIZE, removeY + removeSize,
                            removeHovered ? 0x40CCCCCC : 0x33CCCCCC);
                    Services.PLATFORM.draw(poseStackOrGraphics, mc.font, "×", removeX + 2, removeY - 1, 0xFFFFFF);
                }
            }

        } finally {
            if (scissorHeight > 0 && scissorWidth > 0) {
                Services.PLATFORM.disableScissor(poseStackOrGraphics);
            }
            Services.PLATFORM.popGuiPose(poseStackOrGraphics);
        }

        int borderColor = 0xFF666666;
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + width, y + 1, borderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y + height - 1, x + width, y + height, borderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + 1, y + height, borderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, x + width - 1, y, x + width, y + height, borderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y + headerAreaHeight + 1, x + width, y + headerAreaHeight + 2, borderColor);

        double maxScrollForBar = getMaxScroll();
        if (maxScrollForBar > 0) {
            int scrollbarX = x + width - CustomScrollbarRenderer.getScrollbarWidth() - 4;
            int scrollbarY = y + headerAreaHeight + GRID_PADDING_TOP;
            int scrollbarHeight = height - headerAreaHeight - GRID_PADDING_TOP - bottomMargin;

            double visibleRatio = (maxVisibleRows * (ITEM_SIZE + ITEM_SPACING)) / (double) (Math.ceil((double) displayEntries.size() / itemsPerRow) * (ITEM_SIZE + ITEM_SPACING));
            scrollbarRenderer.renderScrollbar(poseStackOrGraphics, scrollbarX, scrollbarY, scrollbarHeight,
                    scrollOffset, maxScrollForBar, visibleRatio);
        }
    }

    public void renderTooltip(Object poseStackOrGraphics, int mouseX, int mouseY) {
        if (mouseX < x || mouseX >= x + width || mouseY < y || mouseY >= y + height) {
            return;
        }

        int startRow = (int) Math.floor(scrollOffset / (ITEM_SIZE + ITEM_SPACING));
        int endRow = Math.min(startRow + maxVisibleRows + 2, (int) Math.ceil((double) displayEntries.size() / itemsPerRow));

        double pixelOffsetInRow = scrollOffset % (ITEM_SIZE + ITEM_SPACING);
        int itemY = y + headerAreaHeight + GRID_PADDING_TOP - (int) pixelOffsetInRow;
        DisplayEntry hoveredEntry = null;
        boolean hoveringRemoveButton = false;
        int hoveredRemoveX = 0, hoveredRemoveY = 0;

        for (int row = startRow; row < endRow; row++) {
            int rowY = itemY + (row - startRow) * (ITEM_SIZE + ITEM_SPACING);

            if (rowY + ITEM_SIZE < y || rowY > y + height) {
                continue;
            }

            int totalRowWidth = itemsPerRow * (ITEM_SIZE + ITEM_SPACING) - ITEM_SPACING;
            int availableAreaWidth = width - 21;
            int startXOffset = Math.max(0, (availableAreaWidth - totalRowWidth) / 2);

            for (int col = 0; col < itemsPerRow; col++) {
                int index = row * itemsPerRow + col;
                if (index >= displayEntries.size())
                    break;

                DisplayEntry entry = displayEntries.get(index);
                int itemX = x + 5 + startXOffset + col * (ITEM_SIZE + ITEM_SPACING);

                if (itemX + ITEM_SIZE < x || itemX > x + width) {
                    continue;
                }

                int removeSize = 8;
                int removeX = itemX + ITEM_SIZE - removeSize;
                int removeY = rowY;
                boolean removeHovered = mouseX >= removeX && mouseX <= itemX + ITEM_SIZE &&
                        mouseY >= removeY && mouseY <= removeY + removeSize &&
                        mouseX >= x && mouseX < x + width &&
                        mouseY >= y && mouseY < y + height;

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
            }
        }

        Minecraft mc = Minecraft.getInstance();
        if (hoveredEntry != null && hoveredEntry.getCurrentItem() != null) {
            if (hoveredEntry.getItemId().startsWith("#")) {
                List<net.minecraft.network.chat.Component> tooltip = Collections.singletonList(
                        ComponentHelper.literal(hoveredEntry.getItemId()));
                Services.PLATFORM.renderComponentTooltip(poseStackOrGraphics, mc.font, tooltip, mouseX, mouseY);
            } else {
                ItemStack stack = new ItemStack(hoveredEntry.getCurrentItem());
                List<net.minecraft.network.chat.Component> tooltipLines = Services.PLATFORM.getTooltipFromItem(stack);
                Services.PLATFORM.renderComponentTooltip(poseStackOrGraphics, mc.font, tooltipLines, mouseX, mouseY);
            }
        }

        if (hoveringRemoveButton) {
            List<net.minecraft.network.chat.Component> tooltip = Collections.singletonList(
                    ComponentHelper.translatable("buildscape.config.remove"));
            Services.PLATFORM.renderComponentTooltip(poseStackOrGraphics, mc.font, tooltip, hoveredRemoveX, hoveredRemoveY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isMouseOver(mouseX, mouseY)) {
            return false;
        }

        double maxScroll = getMaxScroll();
        if (maxScroll > 0) {
            int scrollbarX = x + width - CustomScrollbarRenderer.getScrollbarWidth() - 4;
            int scrollbarY = y + headerAreaHeight + GRID_PADDING_TOP;
            int bottomMargin = 10;
            int scrollbarHeight = height - headerAreaHeight - GRID_PADDING_TOP - bottomMargin;

            int contentX = x + 5;
            int contentY = y + headerAreaHeight + GRID_PADDING_TOP;
            int contentWidth = width - 21;
            int contentHeight = scrollbarHeight;

            double visibleRatio = (maxVisibleRows * (ITEM_SIZE + ITEM_SPACING)) / (double) (Math.ceil((double) displayEntries.size() / itemsPerRow) * (ITEM_SIZE + ITEM_SPACING));
            double newOffset = scrollbarRenderer.handleMouseClick(mouseX, mouseY, button,
                    scrollbarX, scrollbarY, scrollbarHeight,
                    contentX, contentY, contentWidth, contentHeight,
                    scrollOffset, maxScroll, visibleRatio);

            if (newOffset >= 0) {
                scrollOffset = newOffset;
                return true;
            }
        }

        int startRow = (int) Math.floor(scrollOffset / (ITEM_SIZE + ITEM_SPACING));
        int endRow = Math.min(startRow + maxVisibleRows + 2, (int) Math.ceil((double) displayEntries.size() / itemsPerRow));
        double pixelOffsetInRow = scrollOffset % (ITEM_SIZE + ITEM_SPACING);
        int itemY_fixed = y + headerAreaHeight + GRID_PADDING_TOP - (int) pixelOffsetInRow;

        for (int row = startRow; row < endRow; row++) {
            int rowY = itemY_fixed + (row - startRow) * (ITEM_SIZE + ITEM_SPACING);

            if (rowY + ITEM_SIZE < y + headerAreaHeight || rowY > y + height - 10) {
                continue;
            }

            int totalRowWidth = itemsPerRow * (ITEM_SIZE + ITEM_SPACING) - ITEM_SPACING;
            int availableAreaWidth = width - 21;
            int startXOffset = Math.max(0, (availableAreaWidth - totalRowWidth) / 2);

            for (int col = 0; col < itemsPerRow; col++) {
                int index = row * itemsPerRow + col;
                if (index >= displayEntries.size())
                    break;

                int itemX = x + 5 + startXOffset + col * (ITEM_SIZE + ITEM_SPACING);
                int removeSize = 8;
                int removeX = itemX + ITEM_SIZE - removeSize;
                int removeY = rowY;

                if (mouseX >= removeX && mouseX <= itemX + ITEM_SIZE &&
                        mouseY >= removeY && mouseY <= removeY + removeSize &&
                        mouseY >= y + headerAreaHeight + GRID_PADDING_TOP && mouseY < y + height - 10) {
                    DisplayEntry entry = displayEntries.get(index);
                    onItemRemoved.accept(entry.getItemId());
                    return true;
                }
            }
        }

        return false;
    }

    private double getMaxScroll() {
        if (displayEntries == null || displayEntries.isEmpty()) return 0;
        int totalRows = (int) Math.ceil((double) displayEntries.size() / itemsPerRow);
        return Math.max(0, totalRows * (ITEM_SIZE + ITEM_SPACING) - maxVisibleRows * (ITEM_SIZE + ITEM_SPACING));
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
                int labelHeight = 20;
                int itemY = y + labelHeight;
                int bottomMargin = 10;
                int scrollbarHeight = height - labelHeight - bottomMargin;
                double visibleRatio = (maxVisibleRows * (ITEM_SIZE + ITEM_SPACING)) / (double) (Math.ceil((double) displayEntries.size() / itemsPerRow) * (ITEM_SIZE + ITEM_SPACING));

                double newOffset = scrollbarRenderer.handleMouseDrag(mouseY, itemY, scrollbarHeight,
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
    public boolean charTyped(char codePoint, int modifiers) {
        return false;
    }

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
            for (Item item : Services.PLATFORM.getAllItems()) {
                if (item.builtInRegistryHolder().is(tagKey)) {
                    tagItems.add(item);
                }
            }
        }

        @Override
        public Item getCurrentItem() {
            if (tagItems.isEmpty()) {
                return null;
            }

            long currentTime = System.currentTimeMillis();
            if (currentTime - lastCycleTime >= 1000) {
                lastCycleTime = currentTime;
                currentIndex = (currentIndex + 1) % tagItems.size();
            }

            return tagItems.get(currentIndex);
        }
    }
}
