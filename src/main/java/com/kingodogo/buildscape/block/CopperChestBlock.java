package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
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
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CopperChestBlockEntity(pos, state);
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

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (newState.getBlock() instanceof CopperChestBlock) {
            level.updateNeighbourForOutputSignal(pos, this);
            return;
        }

        super.onRemove(state, level, pos, newState, isMoving);
    }
}
