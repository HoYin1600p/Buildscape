package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.mixinsupport.MixinFactory;

import com.mojang.blaze3d.vertex.PoseStack;
import com.kingodogo.buildscape.util.GhostFilterMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {

    @Shadow
    protected int leftPos;

    @Shadow
    protected int topPos;

    @Dynamic
    @Inject(require = 0, method = "renderSlot(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/inventory/Slot;)V", at = @At("TAIL"))
    private void renderFilterPlaceholder(PoseStack graphics, Slot slot, CallbackInfo ci) {
        AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) (Object) this;
        MixinFactory.renderFilterPlaceholder(screen.getMenu(), slot, graphics);
    }
}
