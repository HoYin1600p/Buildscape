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
    // Header area: top padding (5px) + search box height (~20px) + gap (5px) = 30px
    // Tags start below this area
    private static final int HEADER_AREA_HEIGHT = 26; // Reduced to start 1px below search bar (5px padding + 20px
                                                      // height + 1px)

    private List<TagKey<Item>> allTags;
    private List<TagKey<Item>> filteredTags;
    private String filter = "";
    private double scrollOffset = 0;
    private int maxVisibleRows;
    private final Consumer<String> onTagSelected;
    private Set<String> selectedTags;
    private SortType sortType = SortType.ALL_ITEMS;
    private final CustomScrollbarRenderer scrollbarRenderer = new CustomScrollbarRenderer();

    // Scrolling text state
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
        maxVisibleRows = (height - HEADER_AREA_HEIGHT) / (TAG_BUTTON_HEIGHT + TAG_BUTTON_SPACING);
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
        // Tags should start below the header area (label, search box, buttons)
        // Tags should start below the header area (label, search box, buttons)
        int topPadding = HEADER_AREA_HEIGHT;
        int bottomMargin = 10;
        int availableHeight = height - topPadding - bottomMargin; // Account for bottom margin
        maxVisibleRows = availableHeight / (TAG_BUTTON_HEIGHT + TAG_BUTTON_SPACING);
        maxVisibleRows = Math.max(1, maxVisibleRows);

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
        // Exclude header area from scissor height and add bottom margin
        // bottomMargin is already defined above
        // Raise bottom edge by bottomMargin
        int scissorY = (int) (windowHeight - (y + height) * guiScale + bottomMargin * guiScale);
        // Clip content to exclude scrollbar area:
        // 4px right gap + 8px scrollbar + 4px left gap = 16px reserved
        int scissorWidth = (int) ((width - 16) * guiScale);
        int scissorHeight = (int) ((height - HEADER_AREA_HEIGHT - bottomMargin) * guiScale); // Reduce height

        if (scissorHeight > 0 && scissorWidth > 0) {
            RenderSystem.enableScissor(scissorX, scissorY, scissorWidth, scissorHeight);
        }

        int startRow = (int) scrollOffset;
        int endRow = Math.min(startRow + maxVisibleRows + 1, filteredTags.size()); // +1 to ensure smooth scrolling

        int tagY = y + topPadding;
        // Total width - 5px left padding - 16px right reserved space = width - 21
        int tagWidth = width - 21;

        for (int row = startRow; row < endRow; row++) {
            if (row >= filteredTags.size())
                break;

            TagKey<Item> tag = filteredTags.get(row);
            String tagId = "#" + tag.location();
            int rowY = tagY + (row - startRow) * (TAG_BUTTON_HEIGHT + TAG_BUTTON_SPACING);

            // Updated visibility check to respect HEADER_AREA_HEIGHT
            if (rowY + TAG_BUTTON_HEIGHT < y + HEADER_AREA_HEIGHT || rowY > y + height) {
                continue;
            }

            int tagX = x + 5;

            boolean isSelected = selectedTags.contains(tagId);
            boolean isHovered = mouseX >= tagX && mouseX < tagX + tagWidth &&
                    mouseY >= rowY && mouseY < rowY + TAG_BUTTON_HEIGHT &&
                    mouseX >= x && mouseX < x + width &&
                    mouseY >= y + HEADER_AREA_HEIGHT && mouseY < y + height; // Updated hover check

            int bgColor;
            if (isSelected) {
                bgColor = isHovered ? 0x6000FF00 : 0x4000FF00;
            } else {
                bgColor = isHovered ? 0x40CCCCCC : 0x33CCCCCC;
            }
            fill(poseStack, tagX, rowY, tagX + tagWidth, rowY + TAG_BUTTON_HEIGHT, bgColor);

            if (isSelected) {
                int borderColor = 0xFF00FF00;
                fill(poseStack, tagX - 1, rowY - 1, tagX + tagWidth + 1, rowY, borderColor);
                fill(poseStack, tagX - 1, rowY + TAG_BUTTON_HEIGHT, tagX + tagWidth + 1, rowY + TAG_BUTTON_HEIGHT + 1,
                        borderColor);
                fill(poseStack, tagX - 1, rowY - 1, tagX, rowY + TAG_BUTTON_HEIGHT + 1, borderColor);
                fill(poseStack, tagX + tagWidth, rowY - 1, tagX + tagWidth + 1, rowY + TAG_BUTTON_HEIGHT + 1,
                        borderColor);
            }

            String displayName = tag.location().toString();
            int availableWidth = tagWidth - 10;
            int textWidth = Minecraft.getInstance().font.width(displayName);

            if (isHovered && textWidth > availableWidth) {
                // Scrolling text logic
                if (!tagId.equals(currentHoveredTagId)) {
                    currentHoveredTagId = tagId;
                    hoverStartTime = System.currentTimeMillis();
                }

                long elapsed = System.currentTimeMillis() - hoverStartTime;
                double speed = 0.001; // Speed factor
                int maxScroll = textWidth - availableWidth;

                // Sine wave scrolling: 0 -> max -> 0
                // (1 - cos(t)) / 2 ranges from 0 to 1 to 0
                double scrollProgress = (1.0 - Math.cos(elapsed * speed)) / 2.0;
                int textOffset = (int) (maxScroll * scrollProgress);

                // Clip text to button bounds to prevent leaking
                int buttonScissorX = (int) ((tagX + 5) * guiScale);
                int buttonScissorY = (int) (windowHeight - (rowY + TAG_BUTTON_HEIGHT - 6) * guiScale); // Approx bounds
                int buttonScissorWidth = (int) ((availableWidth) * guiScale);
                int buttonScissorHeight = (int) (TAG_BUTTON_HEIGHT * guiScale);

                // Use intersection with existing scissor (list area) to remain safe
                // But simplified: just enabling scissor for the text line is usually enough if
                // strictly contained
                // Better: Use viewport intersection logic or just a tighter scissor

                // Strict clipping for the text
                int textScissorY = (int) (windowHeight - (rowY + TAG_BUTTON_HEIGHT) * guiScale);
                int textScissorH = (int) (TAG_BUTTON_HEIGHT * guiScale);

                // Apply scissor for text
                RenderSystem.enableScissor(buttonScissorX, textScissorY, buttonScissorWidth, textScissorH);

                Minecraft.getInstance().font.draw(
                        poseStack,
                        displayName,
                        tagX + 5 - textOffset, rowY + 6,
                        0xFFFFFF);

                // Restore list scissor
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

        if (getMaxScroll() > 0) {
            // Scrollbar X = width - 4px gap - 8px width = width - 12
            int scrollbarX = x + width - CustomScrollbarRenderer.getScrollbarWidth() - 4;
            bottomMargin = 10; // Increased margin at bottom
            int scrollbarHeight = height - HEADER_AREA_HEIGHT - bottomMargin;
            int scrollbarY = y + HEADER_AREA_HEIGHT;

            double visibleRatio = maxVisibleRows / (double) filteredTags.size();
            scrollbarRenderer.renderScrollbar(poseStack, scrollbarX, scrollbarY, scrollbarHeight,
                    scrollOffset, getMaxScroll(), visibleRatio);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Updated check to ignore clicks in header area
        if (!isMouseOver(mouseX, mouseY) || mouseY < y + HEADER_AREA_HEIGHT) {
            return false;
        }

        double maxScroll = getMaxScroll();
        if (maxScroll > 0) {
            int scrollbarX = x + width - CustomScrollbarRenderer.getScrollbarWidth() - 4; // 4px form edge
            int scrollbarY = y + HEADER_AREA_HEIGHT;
            int bottomMargin = 10;
            int scrollbarHeight = height - HEADER_AREA_HEIGHT - bottomMargin;
            int contentX = x + 5;
            int contentY = y + HEADER_AREA_HEIGHT;
            // Content width = width - 16px reserved - 5px left padding = width - 21
            int contentWidth = width - 21;
            int contentHeight = height - HEADER_AREA_HEIGHT;

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

        int topPadding = HEADER_AREA_HEIGHT; // Use constant
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

            // Updated visibility check
            if (rowY + TAG_BUTTON_HEIGHT < y + HEADER_AREA_HEIGHT || rowY > y + height) {
                continue;
            }

            int tagX = x + 5;

            if (mouseX >= tagX && mouseX < tagX + tagWidth &&
                    mouseY >= rowY && mouseY < rowY + TAG_BUTTON_HEIGHT &&
                    mouseX >= x && mouseX < x + width &&
                    mouseY >= y + HEADER_AREA_HEIGHT && mouseY < y + height) { // Updated click check
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
                int scrollbarY = y + HEADER_AREA_HEIGHT;
                int bottomMargin = 10;
                int scrollbarHeight = height - HEADER_AREA_HEIGHT - bottomMargin;
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
