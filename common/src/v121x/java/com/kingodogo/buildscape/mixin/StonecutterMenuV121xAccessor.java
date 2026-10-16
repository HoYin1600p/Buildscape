package com.kingodogo.buildscape.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** 1.21.1 still supplies the input container when rebuilding stonecutting recipes. */
@Mixin(StonecutterMenu.class)
public interface StonecutterMenuV121xAccessor {
    @Invoker("setupRecipeList")
    void buildscape$setupRecipeList(Container container, ItemStack stack);
}
