package com.kingodogo.buildscape.event;

import net.minecraft.world.item.ItemStack;

public final class FestiveGlintAnvilHandler {
    private FestiveGlintAnvilHandler() {}

    public static FestiveGlintAnvilLogic.AnvilResult processAnvil(ItemStack left, ItemStack right, String renameText) {
        return FestiveGlintAnvilLogic.processAnvil(left, right, renameText);
    }
}
