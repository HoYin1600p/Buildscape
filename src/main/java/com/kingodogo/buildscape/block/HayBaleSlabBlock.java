package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class HayBaleSlabBlock extends SlabBlock {

    public HayBaleSlabBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public void fallOn(
            Level level,
            BlockState state,
            BlockPos pos,
            Entity entity,
            float fallDistance
    ) {
        entity.causeFallDamage(
                fallDistance,
                0.2F,
                net.minecraft.world.damagesource.DamageSource.FALL
        );
    }
}
