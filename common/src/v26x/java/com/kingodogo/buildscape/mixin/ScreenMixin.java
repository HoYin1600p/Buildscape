package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.mixinsupport.MixinFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;

/** Item tooltips are scheduled by the graphics extractor in 26.2. */
@Mixin(value = GuiGraphicsExtractor.class, priority = 900)
public abstract class ScreenMixin {
    @Unique private ItemStack buildscape$currentHoverStack = ItemStack.EMPTY;

    @Dynamic
    @Inject(method = "setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V", at = @At("HEAD"), require = 0)
    private void buildscape$captureStack(Font font, ItemStack stack, int x, int y, CallbackInfo ci) {
        buildscape$currentHoverStack = stack;
    }

    @Dynamic
    @Redirect(method = "setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;getTooltipFromItem(Lnet/minecraft/client/Minecraft;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;"), require = 0)
    private List<Component> buildscape$tooltipText(Minecraft minecraft, ItemStack stack) {
        return MixinFactory.get().prepareCustomTooltipText(stack, Screen.getTooltipFromItem(minecraft, stack));
    }

    @Dynamic
    @Inject(method = "setTooltipForNextFrameInternal(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;Lnet/minecraft/resources/Identifier;Z)V",
            at = @At("HEAD"), require = 0)
    private void buildscape$attachPreview(Font font, List<ClientTooltipComponent> components, int x, int y,
            ClientTooltipPositioner positioner, Identifier style, boolean replace, CallbackInfo ci) {
        MixinFactory.get().renderCustomScreenTooltip((Object) this, components, x, y, buildscape$currentHoverStack, font);
    }

    @Dynamic
    @Inject(method = "setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V", at = @At("RETURN"), require = 0)
    private void buildscape$clearStack(Font font, ItemStack stack, int x, int y, CallbackInfo ci) {
        buildscape$currentHoverStack = ItemStack.EMPTY;
    }
}
