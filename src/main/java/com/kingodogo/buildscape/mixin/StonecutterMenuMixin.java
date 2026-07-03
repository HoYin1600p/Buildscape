package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.util.StonecutterMenuExtension;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.StonecutterMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StonecutterMenu.class)
public abstract class StonecutterMenuMixin extends AbstractContainerMenu implements StonecutterMenuExtension {
    private boolean buildscape$cutAll = false;

    protected StonecutterMenuMixin(MenuType<?> p_38852_, int p_38853_) {
        super(p_38852_, p_38853_);
    }

    @Override
    public boolean buildscape$isCutAll() {
        return this.buildscape$cutAll;
    }

    @Override
    public void buildscape$setCutAll(boolean cutAll) {
        this.buildscape$cutAll = cutAll;
    }

    @Inject(method = "clickMenuButton", at = @At("HEAD"), cancellable = true)
    private void onBeforeClickMenuButton(Player player, int id, CallbackInfoReturnable<Boolean> cir) {
        if (id == 31337) {
            this.buildscape$cutAll = !this.buildscape$cutAll;
            cir.setReturnValue(true);
        }
    }
}
