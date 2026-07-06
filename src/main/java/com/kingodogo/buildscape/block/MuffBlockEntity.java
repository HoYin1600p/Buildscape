package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.client.MuffBlockManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MuffBlockEntity extends BlockEntity {

    public MuffBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MUFF_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (this.level != null && this.level.isClientSide) {
            MuffBlockManager.register(this.worldPosition);
        }
    }

    @Override
    public void setRemoved() {
        if (this.level != null && this.level.isClientSide) {
            MuffBlockManager.unregister(this.worldPosition);
        }
        super.setRemoved();
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        if (this.level != null && this.level.isClientSide) {
            MuffBlockManager.unregister(this.worldPosition);
        }
    }
}
