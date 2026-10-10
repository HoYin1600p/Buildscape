package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MuffBlockEntity extends BlockEntity {
    public MuffBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MUFF_TYPE, pos, state);
    }

    @Override
    public void setLevel(net.minecraft.world.level.Level level) {
        super.setLevel(level);
        if (level.isClientSide()) {
            com.kingodogo.buildscape.client.MuffBlockManager.register(this.worldPosition);
        }
    }

    @Override
    public void setRemoved() {
        if (this.level != null && this.level.isClientSide()) {
            com.kingodogo.buildscape.client.MuffBlockManager.unregister(this.worldPosition);
        }
        super.setRemoved();
    }
}
