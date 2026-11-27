package net.minecraft.core.cauldron;

import net.minecraft.world.item.Item;

/** Package access bridge for Minecraft 26.2's native dispatcher registration API. */
public final class BuildscapeCauldronRegistration {
    private BuildscapeCauldronRegistration() {}
    public static void register(Item item, CauldronInteraction interaction) {
        CauldronInteractions.EMPTY.put(item, interaction);
    }
}
