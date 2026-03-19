package com.kingodogo.buildscape.client.screen.tabs.supporters;

import com.kingodogo.buildscape.client.screen.widget.CustomScrollbarRenderer;
import com.kingodogo.buildscape.config.VerticalConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class VerticalActiveConfigWidget extends AbstractWidget {
    private static final int ITEM_SIZE = 20;
    private static final int ROW_HEIGHT = 20;
    private static final int HEADER_HEIGHT = 20;
    
    private List<String> families = new ArrayList<>();
    private List<String> mods = new ArrayList<>();
    private double scrollOffset = 0;
    private final Consumer<String> onFamilyRemoved;
    private final Consumer<String> onModRemoved;
    private final CustomScrollbarRenderer scrollbarRenderer = new CustomScrollbarRenderer();

    public VerticalActiveConfigWidget(int x, int y, int width, int height, Consumer<String> onFamilyRemoved, Consumer<String> onModRemoved) {
        super(x, y, width, height, TextComponent.EMPTY);
        this.onFamilyRemoved = onFamilyRemoved;
        this.onModRemoved = onModRemoved;
        refreshData();
    }

    public void refreshData() {
        VerticalConfig config = VerticalConfig.get();
        families = new ArrayList<>(config.getAllowedFamilies());
        mods = new ArrayList<>(config.getAllowedMods());
        scrollOffset = Math.max(0, Math.min(scrollOffset, getMaxScroll()));
    }

    private double getMaxScroll() {
        int contentHeight = (families.size() + mods.size() + 2) * ROW_HEIGHT;
        return Math.max(0, contentHeight - (height - HEADER_HEIGHT - 10));
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        int borderColor = 0xFF666666;
        fill(poseStack, x, y, x + width, y + 1, borderColor);
        fill(poseStack, x, y + height - 1, x + width, y + height, borderColor);
        fill(poseStack, x, y, x + 1, y + height, borderColor);
        fill(poseStack, x + width - 1, y, x + width, y + height, borderColor);

        Minecraft mc = Minecraft.getInstance();
        mc.font.draw(poseStack, new net.minecraft.network.chat.TranslatableComponent("buildscape.config.vertical.active"), x + 5, y + 5, 0xFFFFFF);

        double guiScale = mc.getWindow().getGuiScale();
        int windowHeight = mc.getWindow().getHeight();
        RenderSystem.enableScissor((int)(x * guiScale), (int)(windowHeight - (y + height - 5) * guiScale), (int)((width - 16) * guiScale), (int)((height - HEADER_HEIGHT - 10) * guiScale));

        int renderY = y + HEADER_HEIGHT - (int) scrollOffset;
        
        // Families Section
        mc.font.draw(poseStack, "Allowed Families:", x + 5, renderY + 5, 0xAAAAAA);
        renderY += ROW_HEIGHT;
        for (String family : families) {
            renderEntry(poseStack, family, x + 10, renderY, width - 25, mouseX, mouseY, true);
            renderY += ROW_HEIGHT;
        }

        renderY += 5;
        mc.font.draw(poseStack, "Allowed Mods:", x + 5, renderY + 5, 0xAAAAAA);
        renderY += ROW_HEIGHT;
        for (String mod : mods) {
            renderEntry(poseStack, mod, x + 10, renderY, width - 25, mouseX, mouseY, false);
            renderY += ROW_HEIGHT;
        }

        RenderSystem.disableScissor();

        if (getMaxScroll() > 0) {
            int scrollbarX = x + width - 12;
            int scrollbarHeight = height - HEADER_HEIGHT - 10;
            double visibleRatio = (double)scrollbarHeight / ((families.size() + mods.size() + 2) * ROW_HEIGHT);
            scrollbarRenderer.renderScrollbar(poseStack, scrollbarX, y + HEADER_HEIGHT, scrollbarHeight, scrollOffset, getMaxScroll(), visibleRatio);
        }
    }

    private void renderEntry(PoseStack poseStack, String name, int ex, int ey, int ew, int mouseX, int mouseY, boolean isFamily) {
        if (ey + ROW_HEIGHT < y + HEADER_HEIGHT || ey > y + height) return;
        
        Minecraft mc = Minecraft.getInstance();
        boolean hovered = mouseX >= ex && mouseX < ex + ew && mouseY >= ey && mouseY < ey + ROW_HEIGHT;
        fill(poseStack, ex, ey, ex + ew, ey + ROW_HEIGHT - 2, hovered ? 0x44FFFFFF : 0x22FFFFFF);
        
        int textX = ex + 5;
        if (isFamily) {
            ItemStack stack = new ItemStack(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(name)));
            if (!stack.isEmpty()) {
                mc.getItemRenderer().renderGuiItem(stack, ex + 2, ey + 2);
                textX += 20;
            }
        }
        
        String display = name.contains(":") ? name.split(":")[1] : name;
        mc.font.draw(poseStack, display, textX, ey + 6, 0xCCCCCC);
        
        // Remove button
        int rx = ex + ew - 15;
        boolean rh = mouseX >= rx && mouseX < rx + 12 && mouseY >= ey && mouseY < ey + ROW_HEIGHT;
        mc.font.draw(poseStack, "X", rx, ey + 6, rh ? 0xFF0000 : 0x880000);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isMouseOver(mouseX, mouseY) || mouseY < y + HEADER_HEIGHT) return false;

        double maxScroll = getMaxScroll();
        if (maxScroll > 0) {
           // Handle scrollbar...
        }

        int renderY = y + HEADER_HEIGHT - (int) scrollOffset;
        renderY += ROW_HEIGHT; // Skip "Families" title
        for (String family : families) {
            if (checkRemove(mouseX, mouseY, x + 10, renderY, width - 25)) {
                onFamilyRemoved.accept(family);
                return true;
            }
            renderY += ROW_HEIGHT;
        }
        renderY += ROW_HEIGHT + 5; // Skip "Mods" title
        for (String mod : mods) {
            if (checkRemove(mouseX, mouseY, x + 10, renderY, width - 25)) {
                onModRemoved.accept(mod);
                return true;
            }
            renderY += ROW_HEIGHT;
        }
        return false;
    }

    private boolean checkRemove(double mx, double my, int ex, int ey, int ew) {
        int rx = ex + ew - 15;
        return mx >= rx && mx < rx + 12 && my >= ey && my < ey + ROW_HEIGHT;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {}
}
