package com.kingodogo.buildscape.client.screen.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagManager;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TagsSelectorWidget extends AbstractWidget {
    private static final int TAG_BUTTON_HEIGHT = 20;
    private static final int TAG_BUTTON_SPACING = 2;
    private static final int TAGS_PER_ROW = 1;
    private int headerAreaHeight = 26;
    private static final int GRID_PADDING_TOP = 5;

    public void setHeaderAreaHeight(int height) {
        this.headerAreaHeight = height;
        refresh();
    }

    private List<TagKey<Item>> allTags;
    private List<TagKey<Item>> filteredTags;
    private String filter = "";
    private double scrollOffset = 0;
    private int maxVisibleRows;
    private final Consumer<String> onTagSelected;
    private Set<String> selectedTags;
    private SortType sortType = SortType.ALL_ITEMS;
    private final CustomScrollbarRenderer scrollbarRenderer = new CustomScrollbarRenderer();

    private String currentHoveredTagId = null;
    private long hoverStartTime = 0;

    public enum SortType {
        INVENTORY,
        ALL_ITEMS,
        MOD_ONLY
    }

    public TagsSelectorWidget(int x, int y, int width, int height, Consumer<String> onTagSelected) {
        super(x, y, width, height, net.minecraft.network.chat.TextComponent.EMPTY);
        this.onTagSelected = onTagSelected;
        this.selectedTags = new HashSet<>();
        loadAllTags();
        filteredTags = new ArrayList<>(allTags);
        maxVisibleRows = (height - headerAreaHeight) / (TAG_BUTTON_HEIGHT + TAG_BUTTON_SPACING);
    }

    private void loadAllTags() {
        allTags = new ArrayList<>();
        Set<String> seenTags = new HashSet<>();

        ForgeRegistries.ITEMS.getValues().forEach(item -> {
            item.builtInRegistryHolder().tags().forEach(tagKey -> {
                String tagId = tagKey.location().toString();
                if (!seenTags.contains(tagId)) {
                    seenTags.add(tagId);
                    allTags.add(tagKey);
                }
            });
        });

        allTags.sort((a, b) -> a.location().toString().compareToIgnoreCase(b.location().toString()));
    }

    public void setFilter(String filter) {
        this.filter = filter.toLowerCase();
        refresh();
    }

    public void setSortType(SortType sortType) {
        this.sortType = sortType;
        refresh();
    }

    public SortType getSortType() {
        return sortType;
    }

    public void refresh() {
        List<TagKey<Item>> baseList = allTags;

        if (!filter.isEmpty()) {
            baseList = allTags.stream()
                    .filter(tag -> {
                        String tagId = tag.location().toString().toLowerCase();
                        return tagId.contains(filter);
                    })
                    .collect(Collectors.toList());
        }

        switch (sortType) {
            case INVENTORY:
                filteredTags = baseList.stream()
                        .filter(tag -> {
                            return ForgeRegistries.ITEMS.getValues().stream()
                                    .anyMatch(item -> {
                                        if (!item.builtInRegistryHolder().tags().anyMatch(t -> t.equals(tag))) {
                                            return false;
                                        }
                                        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft
                                                .getInstance();
                                        if (mc.player != null) {
                                            return mc.player.getInventory().items.stream()
                                                    .anyMatch(stack -> stack.getItem() == item);
                                        }
                                        return false;
                                    });
                        })
                        .collect(Collectors.toList());
                break;
            case MOD_ONLY:
                filteredTags = baseList.stream()
                        .filter(tag -> tag.location().getNamespace().equals("buildscape"))
                        .collect(Collectors.toList());
                break;
            case ALL_ITEMS:
            default:
                filteredTags = new ArrayList<>(baseList);
                break;
        }

        scrollOffset = Math.max(0, Math.min(scrollOffset, getMaxScroll()));
    }

    public void setSelectedTags(Set<String> tags) {
        this.selectedTags = new HashSet<>(tags);
    }

    private double getMaxScroll() {
        int totalRows = filteredTags.size();
        return Math.max(0, totalRows - maxVisibleRows);
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        int topPadding = headerAreaHeight;
        int bottomMargin = 10;
        int availableHeight = height - topPadding - bottomMargin;
        maxVisibleRows = availableHeight / (TAG_BUTTON_HEIGHT + TAG_BUTTON_SPACING);
        maxVisibleRows = Math.max(1, maxVisibleRows);

        poseStack.pushPose();
        Minecraft mc = Minecraft.getInstance();
        double guiScale = mc.getWindow().getGuiScale();
        int windowHeight = mc.getWindow().getHeight();
        int scissorX = (int) (x * guiScale);
        int scissorY = (int) (windowHeight - (y + height) * guiScale + bottomMargin * guiScale);

        int scissorWidth = (int) ((width - 16) * guiScale);
        int scissorHeight = (int) ((height - headerAreaHeight - 1 - bottomMargin) * guiScale);

        if (scissorHeight > 0 && scissorWidth > 0) {
            RenderSystem.enableScissor(scissorX, scissorY, scissorWidth, scissorHeight);
        }

        int startRow = (int) Math.floor(scrollOffset);
        int endRow = Math.min(startRow + maxVisibleRows + 2, filteredTags.size());

        double pixelOffsetInRow = (scrollOffset % 1.0) * (TAG_BUTTON_HEIGHT + TAG_BUTTON_SPACING);
        int tagY = y + headerAreaHeight + GRID_PADDING_TOP - (int) pixelOffsetInRow;
        int tagWidth = width - CustomScrollbarRenderer.getScrollbarWidth() - 20;

        for (int row = startRow; row < endRow; row++) {
            if (row < 0 || row >= filteredTags.size())
                break;

            TagKey<Item> tag = filteredTags.get(row);
            String tagId = "#" + tag.location();
            int rowY = tagY + (row - startRow) * (TAG_BUTTON_HEIGHT + TAG_BUTTON_SPACING);

            if (rowY + TAG_BUTTON_HEIGHT < y + headerAreaHeight || rowY > y + height - bottomMargin) {
                continue;
            }

            int tagX = x + 5;

            boolean isSelected = selectedTags.contains(tagId);
            boolean isHovered = mouseX >= tagX && mouseX < tagX + tagWidth &&
                    mouseY >= rowY && mouseY < rowY + TAG_BUTTON_HEIGHT &&
                    mouseX >= x && mouseX < x + width &&
                    mouseY >= y + headerAreaHeight + 1 && mouseY < y + height - bottomMargin;

            int bgColor;
            if (isSelected) {
                bgColor = isHovered ? 0x6000FF00 : 0x4000FF00;
            } else {
                bgColor = isHovered ? 0x40CCCCCC : 0x33CCCCCC;
            }
            fill(poseStack, tagX, rowY, tagX + tagWidth, rowY + TAG_BUTTON_HEIGHT, bgColor);

            if (isSelected) {
                int selectionBorderColor = 0xFF00FF00;
                fill(poseStack, tagX - 1, rowY - 1, tagX + tagWidth + 1, rowY, selectionBorderColor);
                fill(poseStack, tagX - 1, rowY + TAG_BUTTON_HEIGHT, tagX + tagWidth + 1, rowY + TAG_BUTTON_HEIGHT + 1,
                        selectionBorderColor);
                fill(poseStack, tagX - 1, rowY - 1, tagX, rowY + TAG_BUTTON_HEIGHT + 1, selectionBorderColor);
                fill(poseStack, tagX + tagWidth, rowY - 1, tagX + tagWidth + 1, rowY + TAG_BUTTON_HEIGHT + 1,
                        selectionBorderColor);
            }

            String displayName = tag.location().toString();
            int availableWidth = tagWidth - 10;
            int textWidth = Minecraft.getInstance().font.width(displayName);

            if (isHovered && textWidth > availableWidth) {
                if (!tagId.equals(currentHoveredTagId)) {
                    currentHoveredTagId = tagId;
                    hoverStartTime = System.currentTimeMillis();
                }

                long elapsed = System.currentTimeMillis() - hoverStartTime;
                double speed = 0.001;
                int maxScroll = textWidth - availableWidth;

                double scrollProgress = (1.0 - Math.cos(elapsed * speed)) / 2.0;
                int textOffset = (int) (maxScroll * scrollProgress);

                int buttonScissorX = (int) ((tagX + 5) * guiScale);
                int buttonScissorY = (int) (windowHeight - (rowY + TAG_BUTTON_HEIGHT - 6) * guiScale);
                int buttonScissorWidth = (int) ((availableWidth) * guiScale);
                int buttonScissorHeight = (int) (TAG_BUTTON_HEIGHT * guiScale);


                int textScissorY = (int) (windowHeight - (rowY + TAG_BUTTON_HEIGHT) * guiScale);
                int textScissorH = (int) (TAG_BUTTON_HEIGHT * guiScale);

                RenderSystem.enableScissor(buttonScissorX, textScissorY, buttonScissorWidth, textScissorH);

                Minecraft.getInstance().font.draw(
                        poseStack,
                        displayName,
                        tagX + 5 - textOffset, rowY + 6,
                        0xFFFFFF);

                if (scissorHeight > 0 && scissorWidth > 0) {
                    RenderSystem.enableScissor(scissorX, scissorY, scissorWidth, scissorHeight);
                } else {
                    RenderSystem.disableScissor();
                }
            } else {
                if (textWidth > availableWidth) {
                    String truncated = Minecraft.getInstance().font.plainSubstrByWidth(displayName,
                            availableWidth - Minecraft.getInstance().font.width("..."));
                    displayName = truncated + "...";
                }
                Minecraft.getInstance().font.draw(
                        poseStack,
                        displayName,
                        tagX + 5, rowY + 6,
                        0xFFFFFF);
            }
        }

        if (scissorHeight > 0 && scissorWidth > 0) {
            RenderSystem.disableScissor();
        }
        poseStack.popPose();

        int borderCol = 0xFF666666;
        fill(poseStack, x, y, x + width, y + 1, borderCol);
        fill(poseStack, x, y + height - 1, x + width, y + height, borderCol);
        fill(poseStack, x, y, x + 1, y + height, borderCol);
        fill(poseStack, x + width - 1, y, x + width, y + height, borderCol);
        fill(poseStack, x, y + headerAreaHeight + 1, x + width, y + headerAreaHeight + 2, borderCol);

        if (getMaxScroll() > 0) {
            int scrollbarX = x + width - CustomScrollbarRenderer.getScrollbarWidth() - 4;
            bottomMargin = 10;
            int scrollbarHeight = height - headerAreaHeight - GRID_PADDING_TOP - bottomMargin - 1;
            int scrollbarY = y + headerAreaHeight + GRID_PADDING_TOP + 1;

            double visibleRatio = maxVisibleRows / (double) filteredTags.size();
            scrollbarRenderer.renderScrollbar(poseStack, scrollbarX, scrollbarY, scrollbarHeight,
                    scrollOffset, getMaxScroll(), visibleRatio);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isMouseOver(mouseX, mouseY) || mouseY < y + headerAreaHeight + GRID_PADDING_TOP) {
            return false;
        }

        double maxScroll = getMaxScroll();
        if (maxScroll > 0) {
            int scrollbarX = x + width - CustomScrollbarRenderer.getScrollbarWidth() - 4;
            int scrollbarY = y + headerAreaHeight + GRID_PADDING_TOP + 1;
            int bottomMargin = 10;
            int scrollbarHeight = height - headerAreaHeight - GRID_PADDING_TOP - bottomMargin - 1;
            int contentX = x + 5;
            int contentY = y + headerAreaHeight + GRID_PADDING_TOP + 1;
            int contentWidth = width - 21;
            int contentHeight = scrollbarHeight;

            double visibleRatio = maxVisibleRows / (double) filteredTags.size();
            double newOffset = scrollbarRenderer.handleMouseClick(mouseX, mouseY, button,
                    scrollbarX, scrollbarY, scrollbarHeight,
                    contentX, contentY, contentWidth, contentHeight,
                    scrollOffset, maxScroll, visibleRatio);

            if (newOffset >= 0) {
                scrollOffset = newOffset;
                return true;
            }
        }

        int topPadding = headerAreaHeight;
        int bottomMargin = 10;
        int availableHeight = height - topPadding - bottomMargin;
        maxVisibleRows = availableHeight / (TAG_BUTTON_HEIGHT + TAG_BUTTON_SPACING);
        maxVisibleRows = Math.max(1, maxVisibleRows);

        int startRow = (int) scrollOffset;
        int endRow = Math.min(startRow + maxVisibleRows + 1, filteredTags.size());

        int tagY = y + topPadding;
        int tagWidth = width - 21;

        for (int row = startRow; row < endRow; row++) {
            if (row >= filteredTags.size())
                break;

            TagKey<Item> tag = filteredTags.get(row);
            String tagId = "#" + tag.location();
            int rowY = tagY + (row - startRow) * (TAG_BUTTON_HEIGHT + TAG_BUTTON_SPACING);

            if (rowY + TAG_BUTTON_HEIGHT < y + headerAreaHeight + GRID_PADDING_TOP || rowY > y + height) {
                continue;
            }

            int tagX = x + 5;

            if (mouseX >= tagX && mouseX < tagX + tagWidth &&
                    mouseY >= rowY && mouseY < rowY + TAG_BUTTON_HEIGHT &&
                    mouseX >= x && mouseX < x + width &&
                    mouseY >= y + headerAreaHeight + GRID_PADDING_TOP && mouseY < y + height) {
                if (selectedTags.contains(tagId)) {
                    selectedTags.remove(tagId);
                } else {
                    selectedTags.add(tagId);
                }
                if (onTagSelected != null) {
                    onTagSelected.accept(tagId);
                }
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (!isMouseOver(mouseX, mouseY)) {
            return false;
        }

        scrollOffset = Math.max(0, Math.min(getMaxScroll(), scrollOffset - delta));
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (scrollbarRenderer.isDragging() && button == 0) {
            double maxScroll = getMaxScroll();
            if (maxScroll > 0) {
                int scrollbarY = y + headerAreaHeight + GRID_PADDING_TOP + 1;
                int bottomMargin = 10;
                int scrollbarHeight = height - headerAreaHeight - GRID_PADDING_TOP - bottomMargin - 1;
                double visibleRatio = maxVisibleRows / (double) filteredTags.size();

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
