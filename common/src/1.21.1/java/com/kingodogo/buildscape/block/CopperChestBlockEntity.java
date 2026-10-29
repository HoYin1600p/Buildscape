package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CopperChestBlockEntity extends ChestBlockEntity {
    public CopperChestBlockEntity(BlockPos pos, BlockState state) {
        super(com.kingodogo.buildscape.adapter.v121x.RestoredContentTypes.blockEntity("copper_chest"), pos, state);
    }
}
