package com.kingodogo.buildscape.client.tooltip;

import com.kingodogo.buildscape.BuildScape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class ShulkerBoxTooltipData implements TooltipComponent, ClientTooltipComponent {
    private static final ResourceLocation TEXTURE = new ResourceLocation(BuildScape.MODID, "textures/gui/shulker_box_tooltip.png");

    public static final int COLOR_UNCOLORED = 0x975DA8;

    public static final Map<DyeColor, Integer> DYE_COLORS = Util.make(new EnumMap<>(DyeColor.class), map -> {
        map.put(DyeColor.WHITE,      0xD7D7D7);
        map.put(DyeColor.ORANGE,     0xD7601F);
        map.put(DyeColor.MAGENTA,    0xAB329F);
        map.put(DyeColor.LIGHT_BLUE, 0x2999C6);
        map.put(DyeColor.YELLOW,     0xE1A119);
        map.put(DyeColor.LIME,       0x54A411);
        map.put(DyeColor.PINK,       0xF1759A);
        map.put(DyeColor.GRAY,       0x34383B);
        map.put(DyeColor.LIGHT_GRAY, 0x69695F);
        map.put(DyeColor.CYAN,       0x12707E);
        map.put(DyeColor.PURPLE,     0x5E1F99);
        map.put(DyeColor.BLUE,       0x282B88);
        map.put(DyeColor.BROWN,      0x5D3A21);
        map.put(DyeColor.GREEN,      0x4C6514);
        map.put(DyeColor.RED,        0x891E1C);
        map.put(DyeColor.BLACK,      0x1E1E23);
    });

    public static int getHexColor(@Nullable DyeColor dye) {
        if (dye == null) return COLOR_UNCOLORED;
        return DYE_COLORS.getOrDefault(dye, COLOR_UNCOLORED);
    }

    public static float[] getTint(@Nullable DyeColor dye) {
        return hexToRgb(getHexColor(dye));
    }

    public static float[] hexToRgb(int hex) {
        float r = ((hex >> 16) & 0xFF) / 255.0f;
        float g = ((hex >> 8) & 0xFF) / 255.0f;
        float b = (hex & 0xFF) / 255.0f;
        return new float[]{r, g, b};
    }

    private final NonNullList<ItemStack> filterStacks;
    private final NonNullList<ItemStack> realStacks;
    @Nullable
    private final DyeColor color;

    public ShulkerBoxTooltipData(NonNullList<ItemStack> filterStacks, NonNullList<ItemStack> realStacks, @Nullable DyeColor color) {
        this.filterStacks = filterStacks;
        this.realStacks = realStacks;
        this.color = color;
    }

    public NonNullList<ItemStack> getFilterStacks() {
        return filterStacks;
    }

    public NonNullList<ItemStack> getRealStacks() {
        return realStacks;
    }

    @Nullable
    public DyeColor getColor() {
        return color;
    }

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
            float[] tint = getTint(color);
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

                    ItemStack filter = index < filterStacks.size() ? filterStacks.get(index) : ItemStack.EMPTY;
                    ItemStack real = index < realStacks.size() ? realStacks.get(index) : ItemStack.EMPTY;

                    int itemX = slotX + 1;
                    int itemY = slotY + 1;

                    try {
                        if (!real.isEmpty()) {
                            float prevBlit = itemRenderer.blitOffset;
                            itemRenderer.blitOffset = blitOffset + 100.0F;
                            itemRenderer.renderAndDecorateItem(mc.player, real, itemX, itemY, index);
                            itemRenderer.renderGuiItemDecorations(font, real, itemX, itemY);
                            itemRenderer.blitOffset = prevBlit;
                        } else if (!filter.isEmpty()) {
                            float prevBlit = itemRenderer.blitOffset;
                            itemRenderer.blitOffset = blitOffset + 100.0F;
                            itemRenderer.renderAndDecorateItem(mc.player, filter, itemX, itemY, index);
                            itemRenderer.blitOffset = prevBlit;
                        }
                    } catch (Throwable itemError) {
                        BuildScape.getLogger().debug("ShulkerBoxTooltipData: Error rendering item slot " + index, itemError);
                    }
                }
            }
        } catch (Throwable t) {
            BuildScape.getLogger().debug("ShulkerBoxTooltipData: Error in renderImage", t);
        }
    }
}
