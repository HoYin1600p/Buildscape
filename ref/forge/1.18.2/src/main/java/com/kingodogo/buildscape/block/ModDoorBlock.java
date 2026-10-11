package com.kingodogo.buildscape.block;

import java.util.Collections;
import java.util.List;

import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootContext;

public class ModDoorBlock extends DoorBlock {

    public ModDoorBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public List<net.minecraft.world.item.ItemStack> getDrops(
            BlockState state,
            LootContext.Builder builder
    ) {
        if (state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
            return super.getDrops(state, builder);
        } else {
            return Collections.emptyList();
        }
    }
}
