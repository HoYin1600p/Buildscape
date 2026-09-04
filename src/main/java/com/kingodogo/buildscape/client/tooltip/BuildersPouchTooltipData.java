package com.kingodogo.buildscape.client.tooltip;

import com.kingodogo.buildscape.BuildScape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public class BuildersPouchTooltipData implements TooltipComponent, ClientTooltipComponent {
    private static final ResourceLocation TEXTURE = new ResourceLocation(BuildScape.MODID, "textures/gui/shulker_box_tooltip.png");

    public static final int COLOR_GOLD = 0xFBC02D;

    public static float[] hexToRgb(int hex) {
        float r = ((hex >> 16) & 0xFF) / 255.0f;
        float g = ((hex >> 8) & 0xFF) / 255.0f;
        float b = (hex & 0xFF) / 255.0f;
        return new float[]{r, g, b};
    }

    private final NonNullList<ItemStack> filterStacks;
    private final NonNullList<ItemStack> realStacks;

    public BuildersPouchTooltipData(NonNullList<ItemStack> filterStacks, NonNullList<ItemStack> realStacks) {
        this.filterStacks = filterStacks;
        this.realStacks = realStacks;
    }

    public NonNullList<ItemStack> getFilterStacks() {
        return filterStacks;
    }

    public NonNullList<ItemStack> getRealStacks() {
        return realStacks;
    }

    @Override
    public int getHeight() {
        return 1 * 18 + 14;
    }

    @Override
    public int getWidth(Font font) {
        return 9 * 18 + 14;
    }

    @Override
    public void renderImage(Font font, int x, int y, PoseStack poseStack, ItemRenderer itemRenderer, int blitOffset) {
        try {
            Minecraft mc = Minecraft.getInstance();

            int width = getWidth(font);
            int height = getHeight();

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            float[] tint = hexToRgb(COLOR_GOLD);
            RenderSystem.setShaderColor(tint[0], tint[1], tint[2], 1.0F);
            RenderSystem.setShaderTexture(0, TEXTURE);

            GuiComponent.blit(poseStack, x, y, 0, 0, 7, 7, 32, 32);
            GuiComponent.blit(poseStack, x + width - 7, y, 25, 0, 7, 7, 32, 32);
            GuiComponent.blit(poseStack, x, y + height - 7, 0, 25, 7, 7, 32, 32);
            GuiComponent.blit(poseStack, x + width - 7, y + height - 7, 25, 25, 7, 7, 32, 32);

            for (int col = 0; col < 9; col++) {
                GuiComponent.blit(poseStack, x + 7 + col * 18, y, 7, 0, 18, 7, 32, 32);
                GuiComponent.blit(poseStack, x + 7 + col * 18, y + height - 7, 7, 25, 18, 7, 32, 32);
            }

            GuiComponent.blit(poseStack, x, y + 7, 0, 7, 7, 18, 32, 32);
            GuiComponent.blit(poseStack, x + width - 7, y + 7, 25, 7, 7, 18, 32, 32);

            for (int col = 0; col < 9; col++) {
                GuiComponent.blit(poseStack, x + 7 + col * 18, y + 7, 7, 7, 18, 18, 32, 32);
            }

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            for (int col = 0; col < 9; col++) {
                int slotX = x + 7 + col * 18;
                int slotY = y + 7;

                ItemStack filter = col < filterStacks.size() ? filterStacks.get(col) : ItemStack.EMPTY;
                ItemStack real = col < realStacks.size() ? realStacks.get(col) : ItemStack.EMPTY;

                int itemX = slotX + 1;
                int itemY = slotY + 1;

                try {
                    if (!real.isEmpty()) {
                        float prevBlit = itemRenderer.blitOffset;
                        itemRenderer.blitOffset = blitOffset + 100.0F;
                        itemRenderer.renderAndDecorateItem(mc.player, real, itemX, itemY, col);
                        itemRenderer.renderGuiItemDecorations(font, real, itemX, itemY);
                        itemRenderer.blitOffset = prevBlit;
                    } else if (!filter.isEmpty()) {
                        float prevBlit = itemRenderer.blitOffset;
                        itemRenderer.blitOffset = blitOffset + 100.0F;
                        itemRenderer.renderAndDecorateItem(mc.player, filter, itemX, itemY, col);
                        itemRenderer.blitOffset = prevBlit;
                    }
                } catch (Throwable itemError) {
                    BuildScape.getLogger().debug("BuildersPouchTooltipData: Error rendering item slot " + col, itemError);
                }
            }
        } catch (Throwable t) {
            BuildScape.getLogger().debug("BuildersPouchTooltipData: Error in renderImage", t);
        }
    }
}
