package com.kingodogo.buildscape.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public class CopperChestItem extends BlockItem {
    public CopperChestItem(Block block, Properties properties) {
        super(block, properties);
    }

    // TODO S7b: the built-in item renderer (CopperChestItemRenderer.INSTANCE) is exposed through
    // Forge's IItemRenderProperties in the loader glue. It cannot be referenced from this class:
    // the dedicated server verifies it and must not resolve client renderer types.
}
