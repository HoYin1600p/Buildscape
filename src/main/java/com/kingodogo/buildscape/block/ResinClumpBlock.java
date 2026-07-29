package com.kingodogo.buildscape.block;

import net.minecraft.world.level.block.GlowLichenBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ResinClumpBlock extends GlowLichenBlock {
    public ResinClumpBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public int getLightEmission(net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.BlockGetter level, net.minecraft.core.BlockPos pos) {
        return 7;
    }
}
