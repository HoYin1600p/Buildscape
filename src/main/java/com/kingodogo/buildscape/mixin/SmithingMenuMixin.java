package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.item.ModItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmithingMenu.class)
public abstract class SmithingMenuMixin extends ItemCombinerMenu {

    @Unique
    private ItemStack buildscape$savedTemplate = ItemStack.EMPTY;

    public SmithingMenuMixin() {
        super(null, 0, null, null);
    }

    @Inject(method = "onTake", at = @At("HEAD"))
    private void buildscape$preserveTemplateHead(Player player, ItemStack stack, CallbackInfo ci) {
        ItemStack rightSlot = this.inputSlots.getItem(1);
        if (rightSlot.is(ModItems.BIG_ORNAMENT_TEMPLATE.get())) {
            this.buildscape$savedTemplate = rightSlot.copy();
        } else {
            this.buildscape$savedTemplate = ItemStack.EMPTY;
        }
    }

    @Inject(method = "onTake", at = @At("RETURN"))
    private void buildscape$preserveTemplateReturn(Player player, ItemStack stack, CallbackInfo ci) {
        if (!this.buildscape$savedTemplate.isEmpty()) {
            this.inputSlots.setItem(1, this.buildscape$savedTemplate);
            this.broadcastChanges();
            this.buildscape$savedTemplate = ItemStack.EMPTY;
        }
    }
}
