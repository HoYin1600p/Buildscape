package com.kingodogo.buildscape.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** 1.18.2 shape of StonecutterMenu.setupRecipeList, which takes the container as well. */
@Mixin(StonecutterMenu.class)
public interface StonecutterMenuLegacyAccessor {
    @Invoker("setupRecipeList")
    void buildscape$setupRecipeList(Container container, ItemStack stack);
}
