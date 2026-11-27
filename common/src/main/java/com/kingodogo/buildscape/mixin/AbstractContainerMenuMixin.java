package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.mixinsupport.MixinFactory;
import com.kingodogo.buildscape.util.StonecutterMenuExtension;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.StonecutterMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin {

    @Inject(method = "clicked", at = @At("HEAD"), cancellable = true)
    private void onBeforeClicked(int slotId, int buttonId, ClickType clickType, Player player, CallbackInfo ci) {
        if ((Object) this instanceof StonecutterMenu menu
                && slotId == 1 && ((StonecutterMenuExtension) menu).buildscape$isCutAll()) {
            ci.cancel();
            if (!player.level().isClientSide()) {
                MixinFactory.handleStonecutterCutAll(menu, player);
            }
        }
    }
}
