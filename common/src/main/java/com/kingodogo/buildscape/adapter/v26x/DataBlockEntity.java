package com.kingodogo.buildscape.block.entity;

import com.kingodogo.buildscape.adapter.v26x.ValueInputData;
import com.kingodogo.buildscape.adapter.v26x.ValueOutputData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
public abstract class DataBlockEntity extends BlockEntity implements IDataSerializable {
    protected DataBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) { super(type, pos, state); }

    @Override protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        readData(new ValueInputData(input));
    }

    @Override protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        writeData(new ValueOutputData(output));
    }
}
