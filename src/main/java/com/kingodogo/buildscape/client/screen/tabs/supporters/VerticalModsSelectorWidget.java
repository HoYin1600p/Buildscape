package com.kingodogo.buildscape.client.screen.tabs.supporters;

import com.kingodogo.buildscape.client.screen.widget.CustomScrollbarRenderer;
import com.kingodogo.buildscape.client.screen.widget.SortToggleButton;
import com.kingodogo.buildscape.variantengine.family.BlockFamily;
import com.kingodogo.buildscape.variantengine.registry.VariantRegistrar;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class VerticalModsSelectorWidget extends AbstractWidget {
    private static final int ROW_HEIGHT = 20;
    private int headerAreaHeight = 65; // Room for label, buttons, search
    
    private List<String> allMods = new ArrayList<>();
    private List<String> filteredMods = new ArrayList<>();
    private String filter = "";
    private double scrollOffset = 0;
    private final Consumer<String> onModSelected;
    private final java.util.function.Function<String, Integer> getModStatusFunc;
    private final CustomScrollbarRenderer scrollbarRenderer = new CustomScrollbarRenderer();
    private final java.util.function.Supplier<List<String>> modsSupplier;
    private SortToggleButton.SortType sortType = SortToggleButton.SortType.ALL_ITEMS;

    public VerticalModsSelectorWidget(int x, int y, int width, int height, java.util.function.Supplier<List<String>> modsSupplier, Consumer<String> onModSelected, java.util.function.Function<String, Integer> getModStatusFunc) {
        super(x, y, width, height, TextComponent.EMPTY);
        this.modsSupplier = modsSupplier;
        this.onModSelected = onModSelected;
        this.getModStatusFunc = getModStatusFunc;
        this.allMods = new ArrayList<>(modsSupplier.get());
        this.allMods.sort(String::compareTo);
        this.filteredMods = new ArrayList<>(allMods);
    }

    public void setHeaderAreaHeight(int height) {
        this.headerAreaHeight = height;
    }

    public void setSortType(SortToggleButton.SortType sortType) {
        this.sortType = sortType;
        refresh();
    }

    public SortToggleButton.SortType getSortType() {
        return sortType;
    }

    private int getModStatus(String mod) {
        if (getModStatusFunc != null) return getModStatusFunc.apply(mod);
        return 0;
    }

    public void reload() {
        this.allMods = new ArrayList<>(modsSupplier.get());
        this.allMods.sort(String::compareTo);
        refresh();
    }

    public void setFilter(String filter) {
        this.filter = filter.toLowerCase();
        refresh();
    }

    public void refresh() {
        List<String> toFilter = new ArrayList<>(allMods);

        if (sortType == SortToggleButton.SortType.INVENTORY) {
            Set<String> inventoryNamespaces = new HashSet<>();
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                net.minecraft.world.entity.player.Inventory inventory = mc.player.getInventory();
                for (int i = 0; i < inventory.getContainerSize(); i++) {
                    net.minecraft.world.item.ItemStack stack = inventory.getItem(i);
                    if (!stack.isEmpty()) {
                        inventoryNamespaces.add(stack.getItem().getRegistryName().getNamespace());
                    }
                }
            }
            toFilter = toFilter.stream().filter(inventoryNamespaces::contains).collect(java.util.stream.Collectors.toList());
        } else if (sortType == SortToggleButton.SortType.MOD_ONLY) {
            toFilter = toFilter.stream().filter(m -> m.equals("buildscape")).collect(java.util.stream.Collectors.toList());
        }

        if (filter.isEmpty()) {
            filteredMods = new ArrayList<>(toFilter);
        } else {
            filteredMods = new ArrayList<>();
            for (String mod : toFilter) {
                if (mod.toLowerCase().contains(filter)) {
                    filteredMods.add(mod);
                }
            }
        }
        scrollOffset = Math.max(0, Math.min(scrollOffset, getMaxScroll()));
    }

    private double getMaxScroll() {
        return Math.max(0, filteredMods.size() * ROW_HEIGHT - (height - headerAreaHeight - 10));
    }

    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        Minecraft mc = Minecraft.getInstance();

        double guiScale = mc.getWindow().getGuiScale();
        int windowHeight = mc.getWindow().getHeight();
        
        int scissorX = (int) (x * guiScale);
        int scissorY = (int) (windowHeight - (y + height - 1) * guiScale);
        int scissorWidth = (int) ((width - 16) * guiScale);
        int scissorHeight = (int) ((height - headerAreaHeight - 3) * guiScale);

        if (scissorHeight > 0 && scissorWidth > 0) {
            RenderSystem.enableScissor(scissorX, scissorY, scissorWidth, scissorHeight);
        }

        int renderY = y + headerAreaHeight + 2 - (int) scrollOffset;
        for (int i = 0; i < filteredMods.size(); i++) {
            int rowY = renderY + i * ROW_HEIGHT;
            if (rowY + ROW_HEIGHT < y + headerAreaHeight + 2 || rowY > y + height - 1) continue;

            String mod = filteredMods.get(i);
            int entryWidth = width - 20 - 5;
            boolean isHovered = mouseX >= x + 5 && mouseX < x + width - 20 && mouseY >= Math.max(rowY, y + headerAreaHeight + 2) && mouseY < Math.min(rowY + ROW_HEIGHT, y + height - 1);
            
            int status = getModStatus(mod); // 0=None, 1=Allowed (Green), 2=Blocked (Red)
            int bgColor = 0x22000000;
            int textColor = 0xCCCCCC;
            
            if (status == 1) {
                bgColor = 0x4000FF00;
                textColor = 0x00FF00;
                // Add green border if selected
                fill(poseStack, x + 5, rowY, x + width - 20, rowY + 1, 0xFF00FF00); // Top
                fill(poseStack, x + 5, rowY + ROW_HEIGHT - 2, x + width - 20, rowY + ROW_HEIGHT - 1, 0xFF00FF00); // Bottom
                fill(poseStack, x + 4, rowY, x + 5, rowY + ROW_HEIGHT - 1, 0xFF00FF00); // Left
                fill(poseStack, x + width - 20, rowY, x + width - 19, rowY + ROW_HEIGHT - 1, 0xFF00FF00); // Right
            } else if (status == 2) {
                bgColor = 0x40FF0000;
                textColor = 0xFF0000;
                // Add red border if blocked
                fill(poseStack, x + 5, rowY, x + width - 20, rowY + 1, 0xFFFF0000); // Top
                fill(poseStack, x + 5, rowY + ROW_HEIGHT - 2, x + width - 20, rowY + ROW_HEIGHT - 1, 0xFFFF0000); // Bottom
                fill(poseStack, x + 4, rowY, x + 5, rowY + ROW_HEIGHT - 1, 0xFFFF0000); // Left
                fill(poseStack, x + width - 20, rowY, x + width - 19, rowY + ROW_HEIGHT - 1, 0xFFFF0000); // Right
            } else if (isHovered) {
                bgColor = 0x40CCCCCC;
            }

            fill(poseStack, x + 5, rowY, x + width - 20, rowY + ROW_HEIGHT - 2, bgColor);
            
            // Text rendering with optional truncation
            int maxTextWidth = entryWidth - 10;
            String displayText = mod;
            if (mc.font.width(displayText) > maxTextWidth) {
                displayText = mc.font.plainSubstrByWidth(displayText, maxTextWidth - mc.font.width("...")) + "...";
            }
            mc.font.draw(poseStack, displayText, x + 10, rowY + 6, textColor);
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
            double visibleRatio = (double)scrollbarHeight / (filteredMods.size() * ROW_HEIGHT);
            scrollbarRenderer.renderScrollbar(poseStack, scrollbarX, scrollbarY, scrollbarHeight, scrollOffset, getMaxScroll(), visibleRatio);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseY >= y + headerAreaHeight && mouseY <= y + height && getMaxScroll() > 0) {
            int scrollbarX = x + width - 12;
            int scrollbarHeight = height - headerAreaHeight - 10;
            if (mouseX >= scrollbarX) {
                double visibleRatio = (double)scrollbarHeight / (filteredMods.size() * ROW_HEIGHT);
                double newOffset = scrollbarRenderer.handleMouseClick(mouseX, mouseY, button, scrollbarX, y + headerAreaHeight, scrollbarHeight, x, y + headerAreaHeight, width - 16, scrollbarHeight, scrollOffset, getMaxScroll(), visibleRatio);
                if (newOffset >= 0) {
                    scrollOffset = newOffset;
                    return true;
                }
            }
        }
        
        if (!isMouseOver(mouseX, mouseY) || mouseY < y + headerAreaHeight + 2 || mouseY > y + height - 1) return false;

        int index = (int) ((mouseY - (y + headerAreaHeight + 2) + scrollOffset) / ROW_HEIGHT);
        if (index >= 0 && index < filteredMods.size()) {
            onModSelected.accept(filteredMods.get(index));
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (isMouseOver(mouseX, mouseY)) {
            scrollOffset = Mth.clamp(scrollOffset - delta * ROW_HEIGHT, 0, getMaxScroll());
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (scrollbarRenderer.isDragging() && button == 0) {
            int scrollbarHeight = height - headerAreaHeight - 10;
            double contentHeight = filteredMods.size() * ROW_HEIGHT;
            double visibleRatio = contentHeight > 0 ? (double) scrollbarHeight / contentHeight : 1.0;
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
}
