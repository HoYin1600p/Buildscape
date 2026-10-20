package com.kingodogo.buildscape.item;

import net.minecraft.world.item.Item;

import javax.annotation.Nullable;

public interface GhostFilterMenu {
    @Nullable
    Item buildscape$getFilterItem(int menuSlot);

    int buildscape$getFilterSlotCount();
}
