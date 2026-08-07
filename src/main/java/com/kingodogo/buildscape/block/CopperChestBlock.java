package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Random;
import java.util.function.Supplier;

public class CopperChestBlock extends ChestBlock {
    private final boolean isWaxed;

    public CopperChestBlock(boolean isWaxed, BlockBehaviour.Properties properties, Supplier<BlockEntityType<? extends ChestBlockEntity>> blockEntityTypeSupplier) {
        super(isWaxed ? properties : properties.randomTicks(), blockEntityTypeSupplier);
        this.isWaxed = isWaxed;
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return !isWaxed;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        if (!isWaxed) {
            CopperOxidationHandler.tryOxidize(level, pos, state);
        }
    }
}
