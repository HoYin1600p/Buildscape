package com.kingodogo.buildscape.block.plant;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.List;
import java.util.function.Supplier;

public class ColoredMossLayersBlock extends MossLayersBlock {

    private final Supplier<Item> dropItem;

    public ColoredMossLayersBlock(Properties properties, Supplier<Item> dropItem, Supplier<Block> fullBlock) {
        super(properties, fullBlock);
        this.dropItem = dropItem;
    }

    @Override
    public List<ItemStack> getDrops(
            BlockState state,
            LootContext.Builder builder) {
        int layerCount = state.getValue(LAYERS);
        return List.of(new ItemStack(dropItem.get(), layerCount));
    }
}
