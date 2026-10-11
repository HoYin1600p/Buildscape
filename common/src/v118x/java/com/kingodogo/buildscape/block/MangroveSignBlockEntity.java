package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MangroveSignBlockEntity extends SignBlockEntity {

    public MangroveSignBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public BlockEntityType<?> getType() {
        return com.kingodogo.buildscape.adapter.v118x.RestoredContentTypes.blockEntity("mangrove_sign_block_entity");
    }
}
