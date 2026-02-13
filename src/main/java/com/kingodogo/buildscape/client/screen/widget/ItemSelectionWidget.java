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
    private static final int HORIZONTAL_MARGIN = 10;
    private static final int MAX_ITEMS_PER_ROW = 16;
    private static final int MAX_VISIBLE_ROWS = 16;
    // Header area: top padding (5px) + search box height (~20px) + gap (5px) = 30px
    // Items start below this area
    private static final int HEADER_AREA_HEIGHT = 26; // Reduced to start 1px below search bar (5px padding + 20px
                                                      // height + 1px)

    private final Consumer<String> onItemSelected;
    private final Predicate<String> isItemInConfig;
    private final List<Item> allItems;
    private List<Item> filteredItems;
    private String filter = "";
    private double scrollOffset = 0;
    private int maxVisibleRows;
    private int itemsPerRow;
    private SortToggleButton.SortType sortMode = SortToggleButton.SortType.ALL_ITEMS;
    private String currentModNamespace = "buildscape";
    private final CustomScrollbarRenderer scrollbarRenderer = new CustomScrollbarRenderer();

    public ItemSelectionWidget(int x, int y, int width, int height,
            Consumer<String> onItemSelected,
            Predicate<String> isItemInConfig) {
        super(x, y, width, height, net.minecraft.network.chat.TextComponent.EMPTY);
        this.onItemSelected = onItemSelected;
        this.isItemInConfig = isItemInConfig;

        allItems = new ArrayList<>(ForgeRegistries.ITEMS.getValues());
        filteredItems = new ArrayList<>(allItems);

        calculateItemsPerRow();
        calculateItemsPerRow();
        int bottomMargin = 10;
        int visibleHeight = height - HEADER_AREA_HEIGHT - bottomMargin;
        maxVisibleRows = Math.max(1, visibleHeight / (ITEM_SIZE + ITEM_SPACING));
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
        refresh();
    }

    public void setHeight(int height) {
        try {
            java.lang.reflect.Field heightField = AbstractWidget.class.getDeclaredField("height");
            heightField.setAccessible(true);
            heightField.setInt(this, height);
            heightField.setInt(this, height);
            int bottomMargin = 10;
            int visibleHeight = height - HEADER_AREA_HEIGHT - bottomMargin;
            this.maxVisibleRows = Math.max(1, visibleHeight / (ITEM_SIZE + ITEM_SPACING));
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
        calculateItemsPerRow();
        maxVisibleRows = Math.min(MAX_VISIBLE_ROWS, (height - HEADER_AREA_HEIGHT - 5) / (ITEM_SIZE + ITEM_SPACING)); // Account
                                                                                                                     // for
                                                                                                                     // header
                                                                                                                     // area

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
        // Exclude header area from scissor height
        // Scissor Y is from bottom, so it remains the same (scissoring from bottom
        // usually implies bottom origin)
        // Correct logic: scissorY is bottom of rect. scissorHeight is height.
        // We want to reduce height by HEADER_AREA_HEIGHT.
        // And we want the bottom to stay fixed.
        int bottomMargin = 10;
        int scissorY = (int) (windowHeight - (y + height) * guiScale + bottomMargin * guiScale);
        int scissorWidth = (int) ((width - 21) * guiScale); // Exclude scrollbar area
        int scissorHeight = (int) ((height - HEADER_AREA_HEIGHT - bottomMargin) * guiScale);

        if (scissorHeight > 0 && scissorWidth > 0) {
            RenderSystem.enableScissor(scissorX, scissorY, scissorWidth, scissorHeight);
        }

        int totalRows = (int) Math.ceil((double) filteredItems.size() / itemsPerRow);
        int startRow = (int) Math.floor(scrollOffset / (ITEM_SIZE + ITEM_SPACING));
        int endRow = Math.min(startRow + maxVisibleRows + 2, totalRows);

        double pixelOffsetInRow = scrollOffset % (ITEM_SIZE + ITEM_SPACING);
        int itemY = y + HEADER_AREA_HEIGHT - (int) pixelOffsetInRow; // Start below header area
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        for (int row = startRow; row < endRow; row++) {
            int rowY = itemY + (row - startRow) * (ITEM_SIZE + ITEM_SPACING);

            // Updated visibility check to respect HEADER_AREA_HEIGHT
            if (rowY + ITEM_SIZE < y + HEADER_AREA_HEIGHT || rowY > y + height) {
                continue;
            }

            for (int col = 0; col < itemsPerRow; col++) {
                int index = row * itemsPerRow + col;
                if (index >= filteredItems.size())
                    break;

                Item item = filteredItems.get(index);
                int itemX = x + 5 + col * (ITEM_SIZE + ITEM_SPACING);

                if (itemX + ITEM_SIZE < x || itemX > x + width) {
                    continue;
                }

                boolean isHovered = mouseX >= itemX && mouseX < itemX + ITEM_SIZE &&
                        mouseY >= rowY && mouseY < rowY + ITEM_SIZE &&
                        mouseX >= x && mouseX < x + width &&
                        mouseY >= y + HEADER_AREA_HEIGHT && mouseY < y + height; // Updated hover check

                String itemId = ForgeRegistries.ITEMS.getKey(item).toString();
                boolean inConfig = isItemInConfig.test(itemId);

                int bgColor;
                if (inConfig) {
                    bgColor = isHovered ? 0x6000FF00 : 0x4000FF00;
                } else {
                    bgColor = isHovered ? 0x40CCCCCC : 0x33CCCCCC;
                }
                fill(poseStack, itemX, rowY, itemX + ITEM_SIZE, rowY + ITEM_SIZE, bgColor);

                if (inConfig) {
                    int borderColor = 0xFF00FF00;
                    fill(poseStack, itemX - 1, rowY - 1, itemX + ITEM_SIZE + 1, rowY, borderColor);
                    fill(poseStack, itemX - 1, rowY + ITEM_SIZE, itemX + ITEM_SIZE + 1, rowY + ITEM_SIZE + 1,
                            borderColor);
                    fill(poseStack, itemX - 1, rowY - 1, itemX, rowY + ITEM_SIZE + 1, borderColor);
                    fill(poseStack, itemX + ITEM_SIZE, rowY - 1, itemX + ITEM_SIZE + 1, rowY + ITEM_SIZE + 1,
                            borderColor);
                }

                poseStack.pushPose();
                poseStack.translate(0, 0, 100);
                ItemStack stack = new ItemStack(item);
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

        // Render scrollbar after disabling scissor so it is not clipped
        if (getMaxScroll() > 0) {
            int scrollbarX = x + width - CustomScrollbarRenderer.getScrollbarWidth() - 4; // 4px form edge
            int scrollbarY = y + HEADER_AREA_HEIGHT;
            bottomMargin = 10;
            int scrollbarHeight = height - HEADER_AREA_HEIGHT - bottomMargin;

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
        int itemY = y + HEADER_AREA_HEIGHT - (int) pixelOffsetInRow; // Updated to HEADER_AREA_HEIGHT
        Item hoveredItem = null;

        for (int row = startRow; row < endRow; row++) {
            int rowY = itemY + (row - startRow) * (ITEM_SIZE + ITEM_SPACING);

            // Updated visibility check
            if (rowY + ITEM_SIZE < y + HEADER_AREA_HEIGHT || rowY > y + height) {
                continue;
            }

            for (int col = 0; col < itemsPerRow; col++) {
                int index = row * itemsPerRow + col;
                if (index >= filteredItems.size())
                    break;

                Item item = filteredItems.get(index);
                int itemX = x + 5 + col * (ITEM_SIZE + ITEM_SPACING);

                if (itemX + ITEM_SIZE < x || itemX > x + width) {
                    continue;
                }

                boolean isHovered = mouseX >= itemX && mouseX < itemX + ITEM_SIZE &&
                        mouseY >= rowY && mouseY < rowY + ITEM_SIZE &&
                        mouseX >= x && mouseX < x + width &&
                        mouseY >= y + HEADER_AREA_HEIGHT && mouseY < y + height; // Updated hover check
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
        // Updated check to ignore clicks in header area
        if (!isMouseOver(mouseX, mouseY) || mouseY < y + HEADER_AREA_HEIGHT) {
            return false;
        }

        double maxScroll = getMaxScroll();
        if (maxScroll > 0) {
            double pixelOffsetInRow = scrollOffset % (ITEM_SIZE + ITEM_SPACING);
            // int itemY = y + HEADER_AREA_HEIGHT - (int) pixelOffsetInRow; // Not used for
            // scrollbar click

            int scrollbarX = x + width - CustomScrollbarRenderer.getScrollbarWidth() - 4; // 4px form edge
            int scrollbarY = y + HEADER_AREA_HEIGHT;
            int bottomMargin = 10;
            int scrollbarHeight = height - HEADER_AREA_HEIGHT - bottomMargin;
            int contentX = x + 5;
            int contentY = y + HEADER_AREA_HEIGHT;
            int contentWidth = width - 21; // width - 16 - 5
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
        int itemY = y + HEADER_AREA_HEIGHT - (int) pixelOffsetInRow;

        for (int row = startRow; row < endRow; row++) {
            int rowY = itemY + (row - startRow) * (ITEM_SIZE + ITEM_SPACING);

            // Updated visibility check
            if (rowY + ITEM_SIZE < y + HEADER_AREA_HEIGHT || rowY > y + height) {
                continue;
            }

            for (int col = 0; col < itemsPerRow; col++) {
                int index = row * itemsPerRow + col;
                if (index >= filteredItems.size())
                    break;

                int itemX = x + 5 + col * (ITEM_SIZE + ITEM_SPACING);

                if (itemX + ITEM_SIZE < x || itemX > x + width) {
                    continue;
                }

                if (mouseX >= itemX && mouseX < itemX + ITEM_SIZE &&
                        mouseY >= rowY && mouseY < rowY + ITEM_SIZE &&
                        mouseX >= x && mouseX < x + width &&
                        mouseY >= y + HEADER_AREA_HEIGHT && mouseY < y + height) { // Updated click check
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
                int scrollbarY = y + HEADER_AREA_HEIGHT;
                int bottomMargin = 10;
                int scrollbarHeight = height - HEADER_AREA_HEIGHT - bottomMargin;
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
