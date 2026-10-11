package com.kingodogo.buildscape.adapter.v118x.client;

import com.kingodogo.buildscape.client.tooltip.ShulkerBoxTooltipData;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;

public final class ShulkerTooltipComponent implements ClientTooltipComponent {
    private final ShulkerBoxTooltipData data;
    public ShulkerTooltipComponent(ShulkerBoxTooltipData data) { this.data = data; }
    @Override
    public int getHeight() {
        return 3 * 18 + 14;
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
            float[] tint = ShulkerBoxTooltipData.getTint(data.getColor());
            RenderSystem.setShaderColor(tint[0], tint[1], tint[2], 1.0F);
            RenderSystem.setShaderTexture(0, new net.minecraft.resources.ResourceLocation(ShulkerBoxTooltipData.TEXTURE.toString()));

            GuiComponent.blit(poseStack, x, y, 0, 0, 7, 7, 32, 32);
            GuiComponent.blit(poseStack, x + width - 7, y, 25, 0, 7, 7, 32, 32);
            GuiComponent.blit(poseStack, x, y + height - 7, 0, 25, 7, 7, 32, 32);
            GuiComponent.blit(poseStack, x + width - 7, y + height - 7, 25, 25, 7, 7, 32, 32);

            for (int col = 0; col < 9; col++) {
                GuiComponent.blit(poseStack, x + 7 + col * 18, y, 7, 0, 18, 7, 32, 32);
                GuiComponent.blit(poseStack, x + 7 + col * 18, y + height - 7, 7, 25, 18, 7, 32, 32);
            }

            for (int row = 0; row < 3; row++) {
                GuiComponent.blit(poseStack, x, y + 7 + row * 18, 0, 7, 7, 18, 32, 32);
                GuiComponent.blit(poseStack, x + width - 7, y + 7 + row * 18, 25, 7, 7, 18, 32, 32);
            }

            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 9; col++) {
                    GuiComponent.blit(poseStack, x + 7 + col * 18, y + 7 + row * 18, 7, 7, 18, 18, 32, 32);
                }
            }

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 9; col++) {
                    int index = row * 9 + col;
                    int slotX = x + 7 + col * 18;
                    int slotY = y + 7 + row * 18;

                    ItemStack filter = index < data.getFilterStacks().size() ? data.getFilterStacks().get(index) : ItemStack.EMPTY;
                    ItemStack real = index < data.getRealStacks().size() ? data.getRealStacks().get(index) : ItemStack.EMPTY;

                    int itemX = slotX + 1;
                    int itemY = slotY + 1;

                    float prevBlit = itemRenderer.blitOffset;
                    try {
                        if (!real.isEmpty()) {
                            itemRenderer.blitOffset = blitOffset + 100.0F;
                            itemRenderer.renderAndDecorateItem(mc.player, real, itemX, itemY, index);
                            itemRenderer.renderGuiItemDecorations(font, real, itemX, itemY);
                        } else if (!filter.isEmpty()) {
                            itemRenderer.blitOffset = blitOffset + 100.0F;
                            itemRenderer.renderAndDecorateItem(mc.player, filter, itemX, itemY, index);
                        }
                    } catch (Throwable itemError) {
                        com.kingodogo.buildscape.BuildscapeCommon.LOGGER.debug("ShulkerBoxTooltipData: Error rendering item slot " + index, itemError);
                    } finally {
                        itemRenderer.blitOffset = prevBlit;
                    }
                }
            }
        } catch (Throwable t) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.debug("ShulkerBoxTooltipData: Error in renderImage", t);
        } finally {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
