package com.kingodogo.buildscape.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
public abstract class DataBlockEntity extends BlockEntity implements IDataSerializable {
    protected DataBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) { super(type, pos, state); }

    @Override public void load(CompoundTag tag) {
        super.load(tag);
        readData(new CompoundTagData(tag));
    }

    @Override protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        writeData(new CompoundTagData(tag));
    }

    @Override public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        writeData(new CompoundTagData(tag));
        return tag;
    }
}
