package com.kingodogo.buildscape.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(StonecutterMenu.class)
public interface StonecutterMenuAccessor {
    @Invoker("setupRecipeList")
    void callSetupRecipeList(Container container, ItemStack stack);
}
