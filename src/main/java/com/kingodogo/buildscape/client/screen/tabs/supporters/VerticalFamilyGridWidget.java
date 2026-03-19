package com.kingodogo.buildscape.client.screen.tabs.supporters;

import com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen;
import com.kingodogo.buildscape.client.screen.widget.CustomScrollbarRenderer;
import com.kingodogo.buildscape.client.screen.widget.SortToggleButton;
import com.kingodogo.buildscape.variantengine.family.BlockFamily;
import net.minecraft.resources.ResourceLocation;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class VerticalFamilyGridWidget extends AbstractWidget {
    private static final int ITEM_SIZE = 20;
    private final int ITEM_SPACING = 2;
    private int headerAreaHeight = 65; // Dynamic header space
    private final Component title;
    private final Supplier<List<BlockFamily>> familySupplier;
    private final Consumer<BlockFamily> onClick;
    private final FamilyStatusChecker statusChecker;
    
    private List<BlockFamily> filteredFamilies = new ArrayList<>();
    private String filter = "";
    private double scrollOffset = 0;
    private int itemsPerRow;
    private final CustomScrollbarRenderer scrollbarRenderer = new CustomScrollbarRenderer();
    private SortToggleButton.SortType sortMode = SortToggleButton.SortType.ALL_ITEMS;
    private String currentModNamespace = "buildscape";
    private final java.util.Map<Block, ItemStack> itemStackCache = new java.util.HashMap<>();

    public enum RenderMode {
        BASE_BLOCK,
        VERTICAL_VARIANT,
        CYCLE_VERT_SLAB_STAIR
    }
    private final RenderMode renderMode;

    public VerticalFamilyGridWidget(int x, int y, int width, int height, Component title, 
                                  Supplier<List<BlockFamily>> familySupplier, 
                                  Consumer<BlockFamily> onClick,
                                  FamilyStatusChecker statusChecker,
                                  RenderMode renderMode) {
        super(x, y, width, height, TextComponent.EMPTY);
        this.title = title;
        this.familySupplier = familySupplier;
        this.onClick = onClick;
        this.statusChecker = statusChecker;
        this.renderMode = renderMode;
    }

    public void setHeaderAreaHeight(int height) {
        this.headerAreaHeight = height;
        calculateLayout();
    }

    private void calculateLayout() {
        int availableWidth = width - 16;
        itemsPerRow = Math.min(16, Math.max(1, availableWidth / (ITEM_SIZE + ITEM_SPACING)));
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

    public void setFilter(String filter) {
        this.filter = filter.toLowerCase();
        refresh();
    }

    public void refresh() {
        List<BlockFamily> all = new ArrayList<>(familySupplier.get());
        all.sort((a, b) -> {
            ResourceLocation idA = a.getBaseBlock().getRegistryName();
            ResourceLocation idB = b.getBaseBlock().getRegistryName();
            if (idA == null || idB == null) return 0;
            return idA.toString().compareTo(idB.toString());
        });
        List<BlockFamily> toFilter;

        switch (sortMode) {
            case INVENTORY:
                toFilter = getInventoryFamilies(all);
                break;
            case MOD_ONLY:
                toFilter = all.stream()
                    .filter(f -> f.getBaseBlock().getRegistryName().getNamespace().equals(currentModNamespace))
                    .collect(Collectors.toList());
                break;
            case ALL_ITEMS:
            default:
                toFilter = all;
                break;
        }

        if (filter.isEmpty()) {
            filteredFamilies = new ArrayList<>(toFilter);
        } else {
            filteredFamilies = new ArrayList<>();
            for (BlockFamily family : toFilter) {
                String name = family.getBaseBlock().getName().getString().toLowerCase();
                String reg = family.getBaseBlock().getRegistryName().toString().toLowerCase();
                if (name.contains(filter) || reg.contains(filter)) {
                    filteredFamilies.add(family);
                }
            }
        }
        scrollOffset = Math.max(0, Math.min(scrollOffset, getMaxScroll()));
        calculateLayout();
    }

    private List<BlockFamily> getInventoryFamilies(List<BlockFamily> all) {
        java.util.Set<String> inventoryNamespaces = new java.util.HashSet<>();
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            net.minecraft.world.entity.player.Inventory inventory = mc.player.getInventory();
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                net.minecraft.world.item.ItemStack stack = inventory.getItem(i);
                if (!stack.isEmpty()) {
                    inventoryNamespaces.add(stack.getItem().getRegistryName().toString());
                }
            }
        }
        return all.stream()
            .filter(f -> inventoryNamespaces.contains(f.getBaseBlock().getRegistryName().toString()))
            .collect(Collectors.toList());
    }

    private double getMaxScroll() {
        int rows = (int) Math.ceil((double) filteredFamilies.size() / itemsPerRow);
        int visibleHeight = height - headerAreaHeight - 10;
        int contentHeight = rows * (ITEM_SIZE + ITEM_SPACING);
        return Math.max(0, contentHeight - visibleHeight);
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        calculateLayout();

        Minecraft mc = Minecraft.getInstance();

        double guiScale = mc.getWindow().getGuiScale();
        int windowHeight = mc.getWindow().getHeight();
        
        int scissorX = (int) (x * guiScale);
        int scissorY = (int) (windowHeight - (y + height - 1) * guiScale);
        int scissorWidth = (int) ((width - 16) * guiScale);
        int scissorHeight = (int) ((height - headerAreaHeight - 1) * guiScale);

        if (scissorHeight > 0 && scissorWidth > 0) {
            RenderSystem.enableScissor(scissorX, scissorY, scissorWidth, scissorHeight);
        }

        int startRow = (int) (scrollOffset / (ITEM_SIZE + ITEM_SPACING));
        int totalRows = (int) Math.ceil((double) filteredFamilies.size() / itemsPerRow);
        int endRow = Math.min(startRow + (height / (ITEM_SIZE + ITEM_SPACING)) + 2, totalRows);

        int renderY = y + headerAreaHeight + 2 - (int)(scrollOffset % (ITEM_SIZE + ITEM_SPACING));
        int totalRowWidth = itemsPerRow * (ITEM_SIZE + ITEM_SPACING) - ITEM_SPACING;
        int availableAreaWidth = width - 16;
        int startXOffset = Math.max(0, (availableAreaWidth - totalRowWidth) / 2);

        for (int row = startRow; row < endRow; row++) {
            int rowY = renderY + (row - startRow) * (ITEM_SIZE + ITEM_SPACING);
            for (int col = 0; col < itemsPerRow; col++) {
                int index = row * itemsPerRow + col;
                if (index >= filteredFamilies.size()) break;

                BlockFamily family = filteredFamilies.get(index);
                int itemX = x + 5 + startXOffset + col * (ITEM_SIZE + ITEM_SPACING);

                boolean isHovered = mouseX >= itemX && mouseX < itemX + ITEM_SIZE && mouseY >= rowY && mouseY < rowY + ITEM_SIZE;
                int status = statusChecker.getStatus(family);

                int color = 0xFF555555;
                int bgColor = 0x20000000;
                
                if (status == 1) { // Allowed
                    color = 0xFF00FF00;
                    bgColor = 0x4000FF00;
                } else if (status == 2) { // Blocked
                    color = 0xFFFF0000;
                    bgColor = 0x40FF0000;
                } else if (isHovered) {
                    color = 0xFFFFFFFF;
                }

                fill(poseStack, itemX, rowY, itemX + ITEM_SIZE, rowY + ITEM_SIZE, bgColor);
                
                // Outer border
                fill(poseStack, itemX, rowY, itemX + ITEM_SIZE, rowY + 1, color);
                fill(poseStack, itemX, rowY + ITEM_SIZE - 1, itemX + ITEM_SIZE, rowY + ITEM_SIZE, color);
                fill(poseStack, itemX, rowY, itemX + 1, rowY + ITEM_SIZE, color);
                fill(poseStack, itemX + ITEM_SIZE - 1, rowY, itemX + ITEM_SIZE, rowY + ITEM_SIZE, color);

                Block renderBlock = resolveRenderBlock(family);
                
                ItemStack stack = itemStackCache.computeIfAbsent(renderBlock, ItemStack::new);
                mc.getItemRenderer().renderAndDecorateItem(stack, itemX + 2, rowY + 2);
            }
        }

        RenderSystem.disableScissor();

        // Border rendering AFTER scissor to perfectly frame overlapping items
        int borderColor = 0xFF666666;
        fill(poseStack, x, y, x + width, y + 1, borderColor);
        fill(poseStack, x, y + height - 1, x + width, y + height, borderColor);
        fill(poseStack, x, y, x + 1, y + height, borderColor);
        fill(poseStack, x + width - 1, y, x + width, y + height, borderColor);

        if (getMaxScroll() > 0) {
            int scrollbarX = x + width - 12;
            int scrollbarY = y + headerAreaHeight;
            int scrollbarHeight = height - headerAreaHeight - 10;
            double visibleRatio = (double)scrollbarHeight / (totalRows * (ITEM_SIZE + ITEM_SPACING));
            scrollbarRenderer.renderScrollbar(poseStack, scrollbarX, scrollbarY, scrollbarHeight, scrollOffset, getMaxScroll(), visibleRatio);
        }
    }

    public void renderTooltip(PoseStack poseStack, int mouseX, int mouseY) {
        if (!isMouseOver(mouseX, mouseY) || mouseY < y + headerAreaHeight + 2 || mouseY > y + height - 1) return;

        int startRow = (int) (scrollOffset / (ITEM_SIZE + ITEM_SPACING));
        int renderY = y + headerAreaHeight + 2 - (int)(scrollOffset % (ITEM_SIZE + ITEM_SPACING));
        int totalRowWidth = itemsPerRow * (ITEM_SIZE + ITEM_SPACING) - ITEM_SPACING;
        int availableAreaWidth = width - 16;
        int startXOffset = Math.max(0, (availableAreaWidth - totalRowWidth) / 2);

        for (int row = startRow; row < startRow + (height / (ITEM_SIZE + ITEM_SPACING)) + 2; row++) {
            int rowY = renderY + (row - startRow) * (ITEM_SIZE + ITEM_SPACING);
            for (int col = 0; col < itemsPerRow; col++) {
                int index = row * itemsPerRow + col;
                if (index < 0 || index >= filteredFamilies.size()) continue;

                int itemX = x + 5 + startXOffset + col * (ITEM_SIZE + ITEM_SPACING);
                if (mouseX >= itemX && mouseX < itemX + ITEM_SIZE && mouseY >= rowY && mouseY < rowY + ITEM_SIZE) {
                    BlockFamily family = filteredFamilies.get(index);
                    Block renderBlock = resolveRenderBlock(family);
                    ItemStack stack = new ItemStack(renderBlock);
                    Minecraft mc = Minecraft.getInstance();
                    if (mc.screen != null) {
                        List<Component> lines = stack.getTooltipLines(null, net.minecraft.world.item.TooltipFlag.Default.NORMAL);
                        mc.screen.renderTooltip(poseStack, lines.stream().map(Component::getVisualOrderText).collect(java.util.stream.Collectors.toList()), mouseX, mouseY);
                    }
                    return;
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Intercept scrollbar interaction explicitly over entire widget right side if bounds exceed header
        if (mouseY >= y + headerAreaHeight && mouseY <= y + height && getMaxScroll() > 0) {
            int scrollbarX = x + width - 12;
            int scrollbarHeight = height - headerAreaHeight - 10;
            if (mouseX >= scrollbarX) {
                double visibleRatio = (double)scrollbarHeight / (Math.ceil((double)filteredFamilies.size() / itemsPerRow) * (ITEM_SIZE + ITEM_SPACING));
                double newOffset = scrollbarRenderer.handleMouseClick(mouseX, mouseY, button, scrollbarX, y + headerAreaHeight, scrollbarHeight, x, y + headerAreaHeight, width - 16, scrollbarHeight, scrollOffset, getMaxScroll(), visibleRatio);
                if (newOffset >= 0) {
                    scrollOffset = newOffset;
                    return true; // Only return true if dragging actually started!
                }
                // Fall through so other widgets can receive the drag latch on continuous iteration!
            }
        }
        
        if (!isMouseOver(mouseX, mouseY) || mouseY < y + headerAreaHeight + 2 || mouseY > y + height - 1) return false;

        int startRow = (int) (scrollOffset / (ITEM_SIZE + ITEM_SPACING));
        int renderY = y + headerAreaHeight + 2 - (int)(scrollOffset % (ITEM_SIZE + ITEM_SPACING));
        int totalRowWidth = itemsPerRow * (ITEM_SIZE + ITEM_SPACING) - ITEM_SPACING;
        int availableAreaWidth = width - 16;
        int startXOffset = Math.max(0, (availableAreaWidth - totalRowWidth) / 2);

        for (int row = startRow; row < startRow + (height / (ITEM_SIZE + ITEM_SPACING)) + 2; row++) {
            int rowY = renderY + (row - startRow) * (ITEM_SIZE + ITEM_SPACING);
            for (int col = 0; col < itemsPerRow; col++) {
                int index = row * itemsPerRow + col;
                if (index < 0 || index >= filteredFamilies.size()) continue;

                int itemX = x + 5 + startXOffset + col * (ITEM_SIZE + ITEM_SPACING);
                if (mouseX >= itemX && mouseX < itemX + ITEM_SIZE && mouseY >= rowY && mouseY < rowY + ITEM_SIZE) {
                    onClick.accept(filteredFamilies.get(index));
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
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (scrollbarRenderer.isDragging() && button == 0) {
            int scrollbarHeight = height - headerAreaHeight - 10;
            int totalRows = (int) Math.ceil((double) filteredFamilies.size() / itemsPerRow);
            double visibleRatio = totalRows > 0 ? (double) scrollbarHeight / (totalRows * (ITEM_SIZE + ITEM_SPACING)) : 1.0;
            double newOffset = scrollbarRenderer.handleMouseDrag(mouseY, y + headerAreaHeight, scrollbarHeight, getMaxScroll(), visibleRatio, 1.0);
            if (newOffset >= 0) {
                scrollOffset = newOffset;
                return true;
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return scrollbarRenderer.handleMouseRelease(button) || super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {}

    public interface FamilyStatusChecker {
        int getStatus(BlockFamily family);
    }

    private Block resolveRenderBlock(BlockFamily family) {
        if (renderMode == RenderMode.BASE_BLOCK) {
            return family.getBaseBlock();
        } else if (renderMode == RenderMode.CYCLE_VERT_SLAB_STAIR) {
            boolean showStair = (System.currentTimeMillis() / 1500) % 2 == 0;
            if (showStair) {
                return family.getVariant(com.kingodogo.buildscape.variantengine.builder.BlockShape.VERTICAL_STAIRS)
                        .orElseGet(() -> family.getVariant(com.kingodogo.buildscape.variantengine.builder.BlockShape.VERTICAL_SLAB)
                                .orElse(family.getBaseBlock()));
            } else {
                return family.getVariant(com.kingodogo.buildscape.variantengine.builder.BlockShape.VERTICAL_SLAB)
                        .orElseGet(() -> family.getVariant(com.kingodogo.buildscape.variantengine.builder.BlockShape.VERTICAL_STAIRS)
                                .orElse(family.getBaseBlock()));
            }
        } else {
            return family.getVariant(com.kingodogo.buildscape.variantengine.builder.BlockShape.VERTICAL_STAIRS)
                    .orElseGet(() -> family.getVariant(com.kingodogo.buildscape.variantengine.builder.BlockShape.VERTICAL_SLAB)
                            .orElse(family.getBaseBlock()));
        }
    }
}
