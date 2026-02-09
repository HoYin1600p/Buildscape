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
    private static final int TAGS_PER_ROW = 1; // One tag per line
    
    private List<TagKey<Item>> allTags;
    private List<TagKey<Item>> filteredTags;
    private String filter = "";
    private double scrollOffset = 0;
    private int maxVisibleRows;
    private final Consumer<String> onTagSelected;
    private Set<String> selectedTags;
    private SortType sortType = SortType.ALL_ITEMS;
    private boolean isDraggingScrollbar = false;
    private double scrollbarDragStartY = 0;
    private double scrollbarDragStartOffset = 0;
    
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
        maxVisibleRows = (height - 20) / (TAG_BUTTON_HEIGHT + TAG_BUTTON_SPACING);
    }
    
    private void loadAllTags() {
        allTags = new ArrayList<>();
        Set<String> seenTags = new HashSet<>();
        
        // Get all tags by checking all items and their tags
        ForgeRegistries.ITEMS.getValues().forEach(item -> {
            item.builtInRegistryHolder().tags().forEach(tagKey -> {
                String tagId = tagKey.location().toString();
                if (!seenTags.contains(tagId)) {
                    seenTags.add(tagId);
                    allTags.add(tagKey);
                }
            });
        });
        
        // Sort by tag ID
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
        
        // Apply filter
        if (!filter.isEmpty()) {
            baseList = allTags.stream()
                .filter(tag -> {
                    String tagId = tag.location().toString().toLowerCase();
                    return tagId.contains(filter);
                })
                .collect(Collectors.toList());
        }
        
        // Apply sorting
        switch (sortType) {
            case INVENTORY:
                // Filter to only tags that have items in player's inventory
                filteredTags = baseList.stream()
                    .filter(tag -> {
                        // Check if any item with this tag is in inventory
                        return ForgeRegistries.ITEMS.getValues().stream()
                            .anyMatch(item -> {
                                if (!item.builtInRegistryHolder().tags().anyMatch(t -> t.equals(tag))) {
                                    return false;
                                }
                                // Check if item is in player's inventory
                                net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
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
                // Filter to only BuildScape mod tags
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
        int totalRows = filteredTags.size(); // One tag per row
        return Math.max(0, totalRows - maxVisibleRows);
    }
    
    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        // Recalculate maxVisibleRows dynamically based on current height
        // This ensures proper display at all GUI scales
        int topPadding = 5;
        int availableHeight = height - topPadding;
        maxVisibleRows = availableHeight / (TAG_BUTTON_HEIGHT + TAG_BUTTON_SPACING);
        maxVisibleRows = Math.max(1, maxVisibleRows); // At least 1 row visible

        // Enable scissor to clip tags to widget bounds
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

        // Title is now rendered in PillarItemsConfigTab for proper alignment
        // Removed title rendering here

        // Calculate visible tags (one per line)
        int startRow = (int) scrollOffset;
        int endRow = Math.min(startRow + maxVisibleRows, filteredTags.size());

        int tagY = y + topPadding; // Start tags with top padding
        int tagWidth = width - 15; // Full width minus scrollbar space
        
        for (int row = startRow; row < endRow; row++) {
            if (row >= filteredTags.size()) break;
            
            TagKey<Item> tag = filteredTags.get(row);
            String tagId = "#" + tag.location();
            int rowY = tagY + (row - startRow) * (TAG_BUTTON_HEIGHT + TAG_BUTTON_SPACING);
            
            // Skip rows that are completely outside the widget bounds
            if (rowY + TAG_BUTTON_HEIGHT < y || rowY > y + height) {
                continue;
            }
            
            int tagX = x + 5;
            
            boolean isSelected = selectedTags.contains(tagId);
            boolean isHovered = mouseX >= tagX && mouseX < tagX + tagWidth &&
                              mouseY >= rowY && mouseY < rowY + TAG_BUTTON_HEIGHT &&
                              mouseX >= x && mouseX < x + width &&
                              mouseY >= y && mouseY < y + height;
            
            // Render tag button background - highlight selected tags
            int bgColor;
            if (isSelected) {
                bgColor = isHovered ? 0x6000FF00 : 0x4000FF00; // Green tint for selected
            } else {
                bgColor = isHovered ? 0x40CCCCCC : 0x33CCCCCC;
            }
            fill(poseStack, tagX, rowY, tagX + tagWidth, rowY + TAG_BUTTON_HEIGHT, bgColor);
            
            // Render border for selected tags
            if (isSelected) {
                int borderColor = 0xFF00FF00; // Green border
                fill(poseStack, tagX - 1, rowY - 1, tagX + tagWidth + 1, rowY, borderColor);
                fill(poseStack, tagX - 1, rowY + TAG_BUTTON_HEIGHT, tagX + tagWidth + 1, rowY + TAG_BUTTON_HEIGHT + 1, borderColor);
                fill(poseStack, tagX - 1, rowY - 1, tagX, rowY + TAG_BUTTON_HEIGHT + 1, borderColor);
                fill(poseStack, tagX + tagWidth, rowY - 1, tagX + tagWidth + 1, rowY + TAG_BUTTON_HEIGHT + 1, borderColor);
            }
            
            // Render tag name (truncate if too long based on available width)
            String displayName = tag.location().toString();
            int availableWidth = tagWidth - 10; // Margin for text
            int textWidth = Minecraft.getInstance().font.width(displayName);
            if (textWidth > availableWidth) {
                // Truncate text to fit available width
                String truncated = Minecraft.getInstance().font.plainSubstrByWidth(displayName, availableWidth - Minecraft.getInstance().font.width("..."));
                displayName = truncated + "...";
            }
            Minecraft.getInstance().font.draw(
                poseStack,
                displayName,
                tagX + 5, rowY + 6,
                0xFFFFFF
            );
        }
        
        // Disable scissor before rendering scrollbar
        RenderSystem.disableScissor();
        poseStack.popPose();
        
        // Render scrollbar if needed
        if (getMaxScroll() > 0) {
            int scrollbarX = x + width - 10;
            int scrollbarHeight = height - 20;
            int scrollbarY = y + 20;
            
            double scrollRatio = scrollOffset / getMaxScroll();
            int thumbHeight = (int) (scrollbarHeight * (maxVisibleRows / (double) filteredTags.size()));
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
            int scrollbarY = y + 20;
            int scrollbarHeight = height - 20;
            
            if (mouseX >= scrollbarX && mouseX <= scrollbarX + 5 &&
                mouseY >= scrollbarY && mouseY <= scrollbarY + scrollbarHeight) {
                // Clicked on scrollbar - start dragging
                isDraggingScrollbar = true;
                scrollbarDragStartY = mouseY;
                scrollbarDragStartOffset = scrollOffset;
                
                // Calculate thumb position and check if clicking on thumb or track
                int thumbHeight = (int) (scrollbarHeight * (maxVisibleRows / (double) filteredTags.size()));
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
        
        // Check if clicking on a tag (one per line) - must match renderButton positions exactly
        // Recalculate maxVisibleRows to match renderButton
        int topPadding = 5;
        int availableHeight = height - topPadding;
        maxVisibleRows = availableHeight / (TAG_BUTTON_HEIGHT + TAG_BUTTON_SPACING);
        maxVisibleRows = Math.max(1, maxVisibleRows);

        int startRow = (int) scrollOffset;
        int endRow = Math.min(startRow + maxVisibleRows, filteredTags.size());

        int tagY = y + topPadding; // Match renderButton tagY exactly
        int tagWidth = width - 15;
        
        for (int row = startRow; row < endRow; row++) {
            if (row >= filteredTags.size()) break;
            
            TagKey<Item> tag = filteredTags.get(row);
            String tagId = "#" + tag.location();
            int rowY = tagY + (row - startRow) * (TAG_BUTTON_HEIGHT + TAG_BUTTON_SPACING);
            
            // Skip rows that are completely outside the widget bounds
            if (rowY + TAG_BUTTON_HEIGHT < y || rowY > y + height) {
                continue;
            }
            
            int tagX = x + 5;
            
            // Check if clicking on the entire tag button area (not just the border)
            if (mouseX >= tagX && mouseX < tagX + tagWidth &&
                mouseY >= rowY && mouseY < rowY + TAG_BUTTON_HEIGHT &&
                mouseX >= x && mouseX < x + width &&
                mouseY >= y && mouseY < y + height) {
                // Toggle tag selection
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
        // Handle scrollbar dragging (like creative inventory) - works even if mouse leaves widget
        if (isDraggingScrollbar && button == 0) {
            double maxScroll = getMaxScroll();
            if (maxScroll > 0) {
                int scrollbarY = y + 20;
                int scrollbarHeight = height - 20;
                
                // Calculate thumb height for accurate dragging
                int thumbHeight = (int) (scrollbarHeight * (maxVisibleRows / (double) filteredTags.size()));
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
        // Not needed
    }
}

