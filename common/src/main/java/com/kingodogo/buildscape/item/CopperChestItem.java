package com.kingodogo.buildscape.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public class CopperChestItem extends BlockItem {
    public CopperChestItem(Block block, Properties properties) {
        super(block, properties);
    }

    // The client-only Forge mixin supplies IItemRenderProperties through initializeClient.
    // The renderer cannot be referenced from this class:
    // the dedicated server verifies it and must not resolve client renderer types.
}
