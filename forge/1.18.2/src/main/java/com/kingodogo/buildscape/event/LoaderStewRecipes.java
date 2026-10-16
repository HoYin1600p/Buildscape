package com.kingodogo.buildscape.event;

import net.minecraftforge.event.entity.player.PlayerEvent;

/** Forge exposes the reference crafting inventory through ItemCraftedEvent. */
public final class LoaderStewRecipes {
    private LoaderStewRecipes() {}
    public static void crafted(PlayerEvent.ItemCraftedEvent event) {
        // Shared preparation preserves FrostRoseStew NBT and also records crafting milestones.
        ModCommonEvents.onItemCrafted(event.getPlayer(), event.getCrafting(), event.getInventory());
    }
}
