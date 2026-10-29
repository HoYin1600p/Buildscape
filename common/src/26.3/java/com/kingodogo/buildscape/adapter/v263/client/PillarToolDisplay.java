package com.kingodogo.buildscape.adapter.v263.client;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** The tool subclasses used by the reference renderer were removed in 26.2. */
public final class PillarToolDisplay {
    private PillarToolDisplay() {}

    public static boolean swordLike(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        String path = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
        return stack.is(Items.TRIDENT) || stack.is(ItemTags.SWORDS) || stack.is(ItemTags.SHOVELS)
                || path.endsWith("_sword") || path.endsWith("_shovel");
    }

    public static boolean axeLike(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        String path = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
        return stack.is(ItemTags.AXES) || stack.is(ItemTags.PICKAXES) || stack.is(ItemTags.HOES)
                || path.endsWith("_axe") || path.endsWith("_pickaxe") || path.endsWith("_hoe");
    }
}
