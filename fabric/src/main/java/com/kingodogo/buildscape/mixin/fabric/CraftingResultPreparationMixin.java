package com.kingodogo.buildscape.mixin.fabric;

import com.kingodogo.buildscape.event.ModCommonEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingMenu.class)
public abstract class CraftingResultPreparationMixin {
    // Both inventory and workbench grids use this method, before the result is transferred.
    @Inject(method = "slotChangedCraftingGrid", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/world/inventory/ResultContainer;setItem(ILnet/minecraft/world/item/ItemStack;)V",
            shift = At.Shift.AFTER))
    private static void buildscape$prepare(AbstractContainerMenu menu, ServerLevel level, Player player,
            CraftingContainer ingredients, ResultContainer result, RecipeHolder<CraftingRecipe> recipe,
            CallbackInfo ci) {
        ModCommonEvents.prepareCraftedItem(player, result.getItem(0), ingredients);
    }
}
