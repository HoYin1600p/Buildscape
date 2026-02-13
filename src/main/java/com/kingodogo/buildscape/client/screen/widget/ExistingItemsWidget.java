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
    private static final int HORIZONTAL_MARGIN = 10;
    private static final int MAX_ITEMS_PER_ROW = 16;
    private static final int MAX_VISIBLE_ROWS = 16;

    private final Consumer<String> onItemRemoved;
    private final Predicate<String> isItemInConfig;
    private List<String> itemIds;
    private List<DisplayEntry> displayEntries;
    private double scrollOffset = 0;
    private int maxVisibleRows;
    private int itemsPerRow;
    private long lastUpdateTime = 0;
    private static final long CYCLE_INTERVAL = 1000;
    private final CustomScrollbarRenderer scrollbarRenderer = new CustomScrollbarRenderer();

    public ExistingItemsWidget(int x, int y, int width, int height,
            List<String> itemIds,
            Consumer<String> onItemRemoved,
            Predicate<String> isItemInConfig) {
        super(x, y, width, height, net.minecraft.network.chat.TextComponent.EMPTY);
        this.onItemRemoved = onItemRemoved;
        this.isItemInConfig = isItemInConfig;
        this.itemIds = new ArrayList<>(itemIds);

        calculateItemsPerRow();
        this.maxVisibleRows = Math.min(MAX_VISIBLE_ROWS, (height - 5) / (ITEM_SIZE + ITEM_SPACING));
        updateDisplayEntries();
    }

    private void calculateItemsPerRow() {
        int availableWidth = width - 21; // width - 16 - 5
        itemsPerRow = Math.max(1,
                Math.min(MAX_ITEMS_PER_ROW, (availableWidth + ITEM_SPACING) / (ITEM_SIZE + ITEM_SPACING)));
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        calculateItemsPerRow();
        updateDisplayEntries();
    }

    public void setHeight(int height) {
        try {
            java.lang.reflect.Field heightField = AbstractWidget.class.getDeclaredField("height");
            heightField.setAccessible(true);
            heightField.setInt(this, height);
            this.maxVisibleRows = Math.min(MAX_VISIBLE_ROWS, (height - 5) / (ITEM_SIZE + ITEM_SPACING));
            updateDisplayEntries();
        } catch (Exception e) {
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
                String tagString = itemId.substring(1);
                try {
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
                }
            } else {
                try {
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
                }
            }
        }
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        calculateItemsPerRow();
        int labelHeight = 20;
        int availableHeight = height - labelHeight;
        maxVisibleRows = Math.min(MAX_VISIBLE_ROWS, availableHeight / (ITEM_SIZE + ITEM_SPACING));

        poseStack.pushPose();
        Minecraft mc = Minecraft.getInstance();
        double guiScale = mc.getWindow().getGuiScale();
        int windowHeight = mc.getWindow().getHeight();

        // Draw border around panel (debug mode)
        if (com.kingodogo.buildscape.client.screen.DebugRenderConfig.RENDER_PANEL_BORDERS) {
            int borderColor = com.kingodogo.buildscape.client.screen.DebugRenderConfig.PANEL_BORDER_COLOR;
            fill(poseStack, x, y, x + width, y + 1, borderColor); // Top
            fill(poseStack, x, y + height - 1, x + width, y + height, borderColor); // Bottom
            fill(poseStack, x, y, x + 1, y + height, borderColor); // Left
            fill(poseStack, x + width - 1, y, x + width, y + height, borderColor); // Right
        }

        int scissorX = (int) (x * guiScale);
        int bottomMargin = 10;
        int scissorY = (int) (windowHeight - (y + height) * guiScale + bottomMargin * guiScale);
        int scissorWidth = (int) ((width - 21) * guiScale); // Exclude scrollbar area
        int scissorHeight = (int) ((height - labelHeight - bottomMargin) * guiScale);

        if (scissorHeight > 0 && scissorWidth > 0) {
            RenderSystem.enableScissor(scissorX, scissorY, scissorWidth, scissorHeight);
        }
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTime >= CYCLE_INTERVAL) {
            lastUpdateTime = currentTime;
        }

        if (displayEntries == null || displayEntries.isEmpty()) {
            updateDisplayEntries();
        }

        int startRow = (int) scrollOffset;
        int endRow = Math.min(startRow + maxVisibleRows,
                (int) Math.ceil((double) displayEntries.size() / itemsPerRow));

        int itemY = y + labelHeight;
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        DisplayEntry hoveredEntry = null;
        boolean hoveringRemoveButton = false;
        int hoveredRemoveX = 0, hoveredRemoveY = 0;

        for (int row = startRow; row < endRow; row++) {
            int rowY = itemY + (row - startRow) * (ITEM_SIZE + ITEM_SPACING);

            if (rowY + ITEM_SIZE < y || rowY > y + height) {
                continue;
            }

            for (int col = 0; col < itemsPerRow; col++) {
                int index = row * itemsPerRow + col;
                if (index >= displayEntries.size())
                    break;

                DisplayEntry entry = displayEntries.get(index);
                int itemX = x + 5 + col * (ITEM_SIZE + ITEM_SPACING);

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

                int bgColor = itemHovered ? 0x40CCCCCC : 0x33CCCCCC;
                fill(poseStack, itemX, rowY, itemX + ITEM_SIZE, rowY + ITEM_SIZE, bgColor);

                if (itemToDisplay != null) {
                    poseStack.pushPose();
                    poseStack.translate(0, 0, 100);
                    ItemStack stack = new ItemStack(itemToDisplay);
                    itemRenderer.renderGuiItem(stack, itemX + 2, rowY + 2);
                    itemRenderer.renderGuiItemDecorations(
                            mc.font, stack, itemX + 2, rowY + 2);
                    poseStack.popPose();
                }

                fill(poseStack, removeX, removeY, itemX + ITEM_SIZE, removeY + removeSize,
                        removeHovered ? 0x40CCCCCC : 0x33CCCCCC);
                Minecraft.getInstance().font.draw(
                        poseStack, "×", removeX + 2, removeY - 1, 0xFFFFFF);
            }
        }

        RenderSystem.disableScissor();
        poseStack.popPose();

        // Render scrollbar after disabling scissor so it is not clipped
        // Use local variable maxScroll which needs to be re-calculated or scoped
        // correctly
        // We will re-calculate maxScroll here to be safe and clear
        double maxScrollForBar = Math.max(0, Math.ceil((double) displayEntries.size() / itemsPerRow) - maxVisibleRows);
        if (maxScrollForBar > 0) {
            int scrollbarX = x + width - CustomScrollbarRenderer.getScrollbarWidth() - 4; // 4px from edge
            int scrollbarY = itemY;
            // bottomMargin is a local variable in the removed block, need to redefine or
            // use constant
            int scrollbarHeight = height - labelHeight - 10; // Replaced bottomMargin with its constant value 10

            double visibleRatio = maxVisibleRows * itemsPerRow / (double) displayEntries.size();
            scrollbarRenderer.renderScrollbar(poseStack, scrollbarX, scrollbarY, scrollbarHeight,
                    scrollOffset, maxScrollForBar, visibleRatio);
        }

    }

    public void renderTooltip(PoseStack poseStack, int mouseX, int mouseY) {
        if (mouseX < x || mouseX >= x + width || mouseY < y || mouseY >= y + height) {
            return;
        }

        int labelHeight = 20;
        int startRow = (int) scrollOffset;
        int endRow = Math.min(startRow + maxVisibleRows,
                (int) Math.ceil((double) displayEntries.size() / itemsPerRow));

        int itemY = y + labelHeight;
        DisplayEntry hoveredEntry = null;
        boolean hoveringRemoveButton = false;
        int hoveredRemoveX = 0, hoveredRemoveY = 0;

        for (int row = startRow; row < endRow; row++) {
            int rowY = itemY + (row - startRow) * (ITEM_SIZE + ITEM_SPACING);

            if (rowY + ITEM_SIZE < y || rowY > y + height) {
                continue;
            }

            for (int col = 0; col < itemsPerRow; col++) {
                int index = row * itemsPerRow + col;
                if (index >= displayEntries.size())
                    break;

                DisplayEntry entry = displayEntries.get(index);
                int itemX = x + 5 + col * (ITEM_SIZE + ITEM_SPACING);

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

        if (hoveredEntry != null && hoveredEntry.getCurrentItem() != null) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.screen != null) {
                if (hoveredEntry.getItemId().startsWith("#")) {
                    net.minecraft.network.chat.Component tooltip = new net.minecraft.network.chat.TextComponent(
                            hoveredEntry.getItemId());
                    mc.screen.renderTooltip(poseStack, tooltip, mouseX, mouseY);
                } else {
                    ItemStack stack = new ItemStack(hoveredEntry.getCurrentItem());
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
        }

        if (hoveringRemoveButton) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.screen != null) {
                net.minecraft.network.chat.Component tooltip = new net.minecraft.network.chat.TranslatableComponent(
                        "buildscape.config.remove");
                mc.screen.renderTooltip(poseStack, tooltip, hoveredRemoveX, hoveredRemoveY);
            }
        }

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isMouseOver(mouseX, mouseY)) {
            return false;
        }

        int startRow = (int) scrollOffset;
        int endRow = Math.min(startRow + maxVisibleRows,
                (int) Math.ceil((double) displayEntries.size() / itemsPerRow));

        int labelHeight = 20;
        int itemY = y + labelHeight;

        double maxScroll = Math.max(0, Math.ceil((double) displayEntries.size() / itemsPerRow) - maxVisibleRows);
        if (maxScroll > 0) {
            int scrollbarX = x + width - CustomScrollbarRenderer.getScrollbarWidth() - 4; // 4px form edge
            int scrollbarY = itemY;
            int bottomMargin = 10;
            int scrollbarHeight = height - labelHeight - bottomMargin;
            int contentX = x + 5;
            int contentY = itemY;
            int contentWidth = width - 21; // width - 16 - 5
            int contentHeight = scrollbarHeight;

            double visibleRatio = maxVisibleRows / Math.ceil((double) displayEntries.size() / itemsPerRow);
            double newOffset = scrollbarRenderer.handleMouseClick(mouseX, mouseY, button,
                    scrollbarX, scrollbarY, scrollbarHeight,
                    contentX, contentY, contentWidth, contentHeight,
                    scrollOffset, maxScroll, visibleRatio);

            if (newOffset >= 0) {
                scrollOffset = newOffset;
                return true;
            }
        }

        for (int row = startRow; row < endRow; row++) {
            int rowY = itemY + (row - startRow) * (ITEM_SIZE + ITEM_SPACING);

            for (int col = 0; col < itemsPerRow; col++) {
                int index = row * itemsPerRow + col;
                if (index >= displayEntries.size())
                    break;

                int itemX = x + 5 + col * (ITEM_SIZE + ITEM_SPACING);
                int removeSize = 8;
                int removeX = itemX + ITEM_SIZE - removeSize;
                int removeY = rowY;

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
        if (scrollbarRenderer.isDragging() && button == 0) {
            double maxScroll = Math.max(0, Math.ceil((double) displayEntries.size() / itemsPerRow) - maxVisibleRows);
            if (maxScroll > 0) {
                int labelHeight = 20;
                int itemY = y + labelHeight;
                int scrollbarY = itemY;
                int scrollbarHeight = maxVisibleRows * (ITEM_SIZE + ITEM_SPACING);
                double visibleRatio = maxVisibleRows / Math.ceil((double) displayEntries.size() / itemsPerRow);

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

            long currentTime = System.currentTimeMillis();
            if (currentTime - lastCycleTime >= 1000) {
                lastCycleTime = currentTime;
                currentIndex = (currentIndex + 1) % tagItems.size();
            }

            return tagItems.get(currentIndex);
        }
    }
}
