package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.client.tooltip.BuildersPouchTooltipData;
import com.kingodogo.buildscape.client.tooltip.ShulkerBoxTooltipData;
import com.kingodogo.buildscape.event.TagTooltipHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = Screen.class, priority = 900)
public abstract class ScreenMixin {
    @Shadow public int width;
    @Shadow public int height;
    @Shadow protected Font font;
    @Shadow protected ItemRenderer itemRenderer;

    @Unique
    private ItemStack buildscape$currentHoverStack = ItemStack.EMPTY;

    @Unique
    private static boolean buildscape$isRenderingCustomTooltip = false;

    @Inject(
            method = "renderTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/item/ItemStack;II)V",
            at = @At("HEAD")
    )
    private void buildscape$onRenderTooltipHead(PoseStack poseStack, ItemStack itemStack, int x, int y, CallbackInfo ci) {
        if (!buildscape$isRenderingCustomTooltip) {
            this.buildscape$currentHoverStack = itemStack != null ? itemStack : ItemStack.EMPTY;
        }
    }

    @Inject(
            method = "renderTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/item/ItemStack;II)V",
            at = @At("TAIL")
    )
    private void buildscape$onRenderTooltipTail(PoseStack poseStack, ItemStack itemStack, int x, int y, CallbackInfo ci) {
        if (!buildscape$isRenderingCustomTooltip) {
            this.buildscape$currentHoverStack = ItemStack.EMPTY;
        }
    }

    @Inject(method = "renderTooltipInternal", at = @At("TAIL"))
    private void renderCustomTooltipOutside(PoseStack poseStack, List<ClientTooltipComponent> components, int mouseX, int mouseY, CallbackInfo ci) {
        // Recursion guard: prevent infinite loops if other mods hook into item rendering
        if (buildscape$isRenderingCustomTooltip) {
            return;
        }

        // Check if player has Shulker Preview enabled in Player Rules
        if (!TagTooltipHandler.isShulkerPreviewEnabled()) {
            return;
        }

        // Check if Shift key is pressed and a valid item is being hovered
        if (!Screen.hasShiftDown() || this.buildscape$currentHoverStack == null || this.buildscape$currentHoverStack.isEmpty()) {
            return;
        }

        try {
            buildscape$isRenderingCustomTooltip = true;

            ClientTooltipComponent customComponent = null;
            ShulkerBoxTooltipData shulkerData = TagTooltipHandler.getShulkerTooltipData(this.buildscape$currentHoverStack);
            if (shulkerData != null) {
                customComponent = shulkerData;
            } else {
                BuildersPouchTooltipData pouchData = TagTooltipHandler.getBuildersPouchTooltipData(this.buildscape$currentHoverStack);
                if (pouchData != null) {
                    customComponent = pouchData;
                }
            }

            if (customComponent == null) {
                return;
            }

            // 1. Calculate vanilla text tooltip bounding box
            int textTooltipWidth = 0;
            int textTooltipHeight = (components != null && components.size() == 1) ? -2 : 0;
            if (components != null) {
                for (ClientTooltipComponent comp : components) {
                    if (comp != null) {
                        int k = comp.getWidth(this.font);
                        if (k > textTooltipWidth) {
                            textTooltipWidth = k;
                        }
                        textTooltipHeight += comp.getHeight();
                    }
                }
            }

            int textX = mouseX + 12;
            int textY = mouseY - 12;

            if (textX + textTooltipWidth > this.width) {
                textX -= 28 + textTooltipWidth;
            }
            if (textY + textTooltipHeight + 6 > this.height) {
                textY = this.height - textTooltipHeight - 6;
            }
            if (textY < 4) {
                textY = 4;
            }

            int customWidth = customComponent.getWidth(this.font);
            int customHeight = customComponent.getHeight();

            // 2. Position custom tooltip directly below the vanilla text tooltip box
            // Vanilla tooltip box bounds: (textX - 3, textY - 3) to (textX + textTooltipWidth + 3, textY + textTooltipHeight + 3)
            int customX = textX - 3;
            int customY = textY + textTooltipHeight + 6; // 3px border bottom + 3px gap

            // 3. Screen bounds check for custom box:
            // If it doesn't fit below the vanilla text tooltip, place it right above the text tooltip!
            if (customY + customHeight > this.height - 4) {
                customY = (textY - 3) - customHeight - 3;
            }
            if (customY < 4) {
                customY = 4;
            }

            if (customX + customWidth > this.width - 4) {
                customX = this.width - customWidth - 4;
            }
            if (customX < 4) {
                customX = 4;
            }

            // 4. Render custom tooltip image (9-slice frame & items)
            poseStack.pushPose();
            customComponent.renderImage(this.font, customX, customY, poseStack, this.itemRenderer, 400);
            poseStack.popPose();
        } catch (Throwable t) {
            BuildScape.getLogger().debug("BuildScape: Suppressed error during custom tooltip rendering", t);
        } finally {
            buildscape$isRenderingCustomTooltip = false;
        }
    }
}
