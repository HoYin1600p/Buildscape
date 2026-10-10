package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.mixinsupport.MixinFactory;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
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

    @Unique
    private ItemStack buildscape$currentHoverStack = ItemStack.EMPTY;

    @Dynamic
    @Inject(
            method = "renderTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/item/ItemStack;II)V",
            at = @At("HEAD"),
            require = 0
    )
    private void buildscape$onRenderTooltipHead(PoseStack poseStack, ItemStack itemStack, int x, int y, CallbackInfo ci) {
        this.buildscape$currentHoverStack = itemStack != null ? itemStack : ItemStack.EMPTY;
    }

    @Dynamic
    @Inject(
            method = "renderTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/item/ItemStack;II)V",
            at = @At("TAIL"),
            require = 0
    )
    private void buildscape$onRenderTooltipTail(PoseStack poseStack, ItemStack itemStack, int x, int y, CallbackInfo ci) {
        this.buildscape$currentHoverStack = ItemStack.EMPTY;
    }

    @Dynamic
    @Inject(method = "renderTooltipInternal", at = @At("TAIL"), require = 0)
    private void renderCustomTooltipOutside(PoseStack poseStack, List<ClientTooltipComponent> components, int mouseX, int mouseY, CallbackInfo ci) {
        Screen self = (Screen) (Object) this;
        MixinFactory.renderCustomScreenTooltip(self, poseStack, components, mouseX, mouseY, this.buildscape$currentHoverStack, this.font, null, this.width, this.height);
    }
}
