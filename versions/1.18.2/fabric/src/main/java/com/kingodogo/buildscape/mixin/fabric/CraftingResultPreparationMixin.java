package com.kingodogo.buildscape.mixin.fabric;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingMenu.class)
public abstract class CraftingResultPreparationMixin {
    @Inject(method = "slotChangedCraftingGrid", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/world/inventory/ResultContainer;setItem(ILnet/minecraft/world/item/ItemStack;)V", shift = At.Shift.AFTER))
    private static void buildscape$prepare(AbstractContainerMenu menu, Level level, Player player,
            CraftingContainer ingredients, ResultContainer result, CallbackInfo ci) {
        com.kingodogo.buildscape.event.ModCommonEvents.prepareCraftedItem(player, result.getItem(0), ingredients);
    }
}
