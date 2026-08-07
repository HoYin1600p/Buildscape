package com.kingodogo.buildscape.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.kingodogo.buildscape.util.GhostFilterMenu;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {
    private static final float BUILDSCAPE_GHOST_OPACITY = 0.6F;

    @Shadow
    @Final
    protected AbstractContainerMenu menu;

    @Inject(method = "renderSlot", at = @At("TAIL"))
    private void renderFilterPlaceholder(PoseStack poseStack, Slot slot, CallbackInfo ci) {
        if (slot.hasItem() || !slot.isActive() || !(menu instanceof GhostFilterMenu filters)) return;
        Item filter = filters.buildscape$getFilterItem(slot.index);
        if (filter == null) return;

        ItemStack placeholder = new ItemStack(filter);
        placeholder.getOrCreateTag().putBoolean("ghost", true);
        Minecraft minecraft = Minecraft.getInstance();
        ItemRenderer renderer = minecraft.getItemRenderer();
        float previousBlitOffset = renderer.blitOffset;
        float[] previousShaderColor = RenderSystem.getShaderColor();
        float previousRed = previousShaderColor[0];
        float previousGreen = previousShaderColor[1];
        float previousBlue = previousShaderColor[2];
        float previousAlpha = previousShaderColor[3];

        try {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, BUILDSCAPE_GHOST_OPACITY);
            renderer.blitOffset = 100.0F;
            renderer.renderAndDecorateItem(minecraft.player, placeholder, slot.x, slot.y,
                    slot.x + slot.y * 176);
        } finally {
            renderer.blitOffset = previousBlitOffset;
            RenderSystem.setShaderColor(previousRed, previousGreen, previousBlue, previousAlpha);
        }
    }
}
