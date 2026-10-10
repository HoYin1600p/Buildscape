package com.kingodogo.buildscape.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
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

    @Inject(method = "init", at = @At("TAIL"))
    protected void buildscape$initStonecutterCutAll(CallbackInfo ci) {
        if ((Object) this instanceof StonecutterScreen screen) {
            MixinFactory.addStonecutterCutAllButton(screen, this.leftPos + 142, this.topPos + 10, screen.getMenu());
        }
    }

    @Dynamic
    @Inject(method = "renderSlot(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/inventory/Slot;)V", at = @At("TAIL"), require = 0)
    private void renderFilterPlaceholder(PoseStack poseStack, Slot slot, CallbackInfo ci) {
        AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) (Object) this;
        MixinFactory.renderFilterPlaceholder(screen.getMenu(), slot, poseStack);
    }
}
