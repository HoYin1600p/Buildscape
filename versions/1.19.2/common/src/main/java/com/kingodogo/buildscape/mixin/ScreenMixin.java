package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.mixinsupport.MixinFactory;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Dynamic;
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
    @Unique private ItemStack buildscape$currentHoverStack = ItemStack.EMPTY;

    @Dynamic
    @Inject(method = "renderTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/item/ItemStack;II)V", at = @At("HEAD"), require = 0)
    private void buildscape$captureStack(PoseStack poses, ItemStack stack, int x, int y, CallbackInfo ci) {
        buildscape$currentHoverStack = stack == null ? ItemStack.EMPTY : stack;
    }

    @Dynamic
    @Inject(method = "renderTooltipInternal", at = @At("TAIL"), require = 0)
    private void buildscape$renderPreview(PoseStack poses, List<ClientTooltipComponent> components, int x, int y, CallbackInfo ci) {
        MixinFactory.renderCustomScreenTooltip((Screen) (Object) this, poses, components, x, y,
                buildscape$currentHoverStack, font, itemRenderer, width, height);
    }

    @Dynamic
    @Inject(method = "renderTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/item/ItemStack;II)V", at = @At("RETURN"), require = 0)
    private void buildscape$clearStack(PoseStack poses, ItemStack stack, int x, int y, CallbackInfo ci) {
        buildscape$currentHoverStack = ItemStack.EMPTY;
    }
}