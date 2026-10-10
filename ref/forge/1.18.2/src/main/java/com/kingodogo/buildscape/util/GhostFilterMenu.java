package com.kingodogo.buildscape.util;

import net.minecraft.world.item.Item;

import javax.annotation.Nullable;

public interface GhostFilterMenu {
    @Nullable
    Item buildscape$getFilterItem(int menuSlot);

    int buildscape$getFilterSlotCount();
}
