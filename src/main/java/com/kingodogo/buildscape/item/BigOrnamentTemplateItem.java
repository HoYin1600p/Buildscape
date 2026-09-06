package com.kingodogo.buildscape.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BigOrnamentTemplateItem extends Item {
    public BigOrnamentTemplateItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        return itemStack.copy();
    }
}
