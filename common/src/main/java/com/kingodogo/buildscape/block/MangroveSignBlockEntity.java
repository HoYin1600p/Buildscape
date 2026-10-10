package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MangroveSignBlockEntity extends SignBlockEntity {

    public MangroveSignBlockEntity(BlockPos pos, BlockState state) {
        // Must pass Buildscape's own type: the vanilla SIGN type rejects blocks that are not in its valid set.
        super(ModBlockEntities.MANGROVE_SIGN_BLOCK_ENTITY_TYPE, pos, state);
    }
}
