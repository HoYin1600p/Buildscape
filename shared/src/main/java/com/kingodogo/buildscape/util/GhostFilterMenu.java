package com.kingodogo.buildscape.util;

import net.minecraft.world.item.Item;

public interface GhostFilterMenu {
    Item buildscape$getFilterItem(int menuSlot);

    int buildscape$getFilterSlotCount();
}
