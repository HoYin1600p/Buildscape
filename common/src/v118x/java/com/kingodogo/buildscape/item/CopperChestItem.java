package com.kingodogo.buildscape.item;

import com.kingodogo.buildscape.client.renderer.CopperChestItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public class CopperChestItem extends BlockItem {
    public CopperChestItem(Block block, Properties properties) {
        super(block, properties);
    }

    // TODO S7b: expose this renderer through Forge's client item extension in loader glue.
    public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
        return CopperChestItemRenderer.INSTANCE;
    }
}
